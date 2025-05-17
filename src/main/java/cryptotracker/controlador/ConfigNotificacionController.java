package cryptotracker.controlador;

import cryptotracker.modelo.entity.ConfigNotificacion;
import cryptotracker.modelo.entity.Usuario;
import cryptotracker.modelo.repository.ConfigNotificacionRepository;
import cryptotracker.modelo.repository.UsuarioRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/notificaciones")
@CrossOrigin(origins = "*")
public class ConfigNotificacionController {

    @Autowired
    private ConfigNotificacionRepository configNotificacionRepo;

    @Autowired
    private UsuarioRepository usuarioRepo;

    @Autowired
    private RestTemplate restTemplate;

    private final String POWER_AUTOMATE_BASE_URL = "https://prod-187.westeurope.logic.azure.com:443/"
        + "workflows/7b287e41a1b84c23a1c40f1826771007/triggers/manual/paths/invoke";
    private final String POWER_AUTOMATE_QUERY_PARAMS = "?api-version=2016-06-01"
        + "&sp=%2Ftriggers%2Fmanual%2Frun&sv=1.0"
        + "&sig=DannVac9o1aHKDsqCfPqrY7f8xdJ4c9WTf37G-cGvX0";

    // DTO para recibir datos del frontend
    public static class ConfigNotificacionDTO {
        public String nombreUsuario;
        public String tipo;
        public String email;
        public double umbral;
        public String mensajePersonalizado;
        public List<CriptomonedaDTO> billetera;
    }

    // DTO para criptomonedas
    public static class CriptomonedaDTO {
        public String nombre;
        public double cantidad;
    }

    // DTO para enviar respuesta al frontend
    public static class ConfigNotificacionResponseDTO {
        public Long id;
        public String tipo;
        public String email;
        public double umbral;
        public String mensajePersonalizado;
        public String nombreUsuario;
    }

    @GetMapping("/{nombreUsuario}")
    public ResponseEntity<?> getConfiguracion(@PathVariable String nombreUsuario) {
        ConfigNotificacion config = configNotificacionRepo.findByUsuarioNombre(nombreUsuario);
        if (config == null) {
            return ResponseEntity.notFound().build();
        }
        ConfigNotificacionResponseDTO dto = new ConfigNotificacionResponseDTO();
        dto.id = config.getId();
        dto.tipo = config.getTipo();
        dto.email = config.getEmail();
        dto.umbral = config.getUmbral().doubleValue();
        dto.mensajePersonalizado = config.getMensajePersonalizado();
        dto.nombreUsuario = config.getUsuario().getNombre();
        return ResponseEntity.ok(dto);
    }

    @PostMapping
    public ResponseEntity<?> crearConfiguracion(@RequestBody ConfigNotificacionDTO configDTO) {
        try {
            Usuario usuario = usuarioRepo.findByNombre(configDTO.nombreUsuario)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

            // Guardar o actualizar configuración
            ConfigNotificacion config = configNotificacionRepo
                .findByUsuarioNombre(usuario.getNombre());
            if (config == null) {
                config = new ConfigNotificacion();
                config.setUsuario(usuario);
            }
            config.setTipo(configDTO.tipo);
            config.setEmail(configDTO.email);
            config.setUmbral(new BigDecimal(configDTO.umbral));
            config.setMensajePersonalizado(configDTO.mensajePersonalizado);
            config.setActivo(true);
            config = configNotificacionRepo.save(config);

            // Enviar a Power Automate si hay billetera
            if (configDTO.billetera != null && !configDTO.billetera.isEmpty()) {
                Map<String,Object> resp = enviarAPowerAutomate(config, configDTO.billetera);
                System.out.println("Respuesta de Power Automate: " + resp);
            }

            // Devolver DTO
            ConfigNotificacionResponseDTO responseDTO = new ConfigNotificacionResponseDTO();
            responseDTO.id = config.getId();
            responseDTO.tipo = config.getTipo();
            responseDTO.email = config.getEmail();
            responseDTO.umbral = config.getUmbral().doubleValue();
            responseDTO.mensajePersonalizado = config.getMensajePersonalizado();
            responseDTO.nombreUsuario = config.getUsuario().getNombre();
            return ResponseEntity.ok(responseDTO);

        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body("Error al procesar la solicitud: " + e.getMessage());
        }
    }

    private Map<String, Object> enviarAPowerAutomate(
        ConfigNotificacion config,
        List<CriptomonedaDTO> billetera
    ) {
        try {
            // 1. Construir URI sin recodificar nada
            String fullUrl = POWER_AUTOMATE_BASE_URL + POWER_AUTOMATE_QUERY_PARAMS;
            System.out.println("Power Automate URL -> " + fullUrl);
            URI uri = URI.create(fullUrl);

            // 2. Construir el payload con los mismos nombres del trigger
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("nombreUsuario",        config.getUsuario().getNombre());
            requestBody.put("tipo",                 config.getTipo());
            requestBody.put("email",                config.getEmail());
            requestBody.put("umbral",               config.getUmbral().doubleValue());
            requestBody.put("mensajePersonalizado", config.getMensajePersonalizado());
            requestBody.put("billetera",            billetera);

            // 2.1 Serializar y loguear el JSON exacto
            ObjectMapper mapper = new ObjectMapper();
            try {
                String jsonPayload = mapper.writeValueAsString(requestBody);
                System.out.println("Payload JSON para Power Automate:\n" + jsonPayload);
            } catch (JsonProcessingException e) {
                System.err.println("Error al serializar payload: " + e.getMessage());
            }

            // 3. Cabecera: sólo content-type
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            // 4. Envío
            ResponseEntity<String> response = restTemplate.exchange(
                uri, HttpMethod.POST, entity, String.class
            );

            // 5. Devolver un pequeño mapa con status y body
            Map<String, Object> responseMap = new HashMap<>();
            responseMap.put("status", response.getStatusCodeValue());
            responseMap.put("body",   response.getBody());
            return responseMap;

        } catch (Exception e) {
            throw new RuntimeException("Error al comunicarse con Power Automate", e);
        }
    }

    @PostMapping("/enviar-prueba/{nombreUsuario}")
    public ResponseEntity<?> enviarNotificacionPrueba(@PathVariable String nombreUsuario) {
        try {
            ConfigNotificacion config = configNotificacionRepo.findByUsuarioNombre(nombreUsuario);
            if (config == null) {
                return ResponseEntity.badRequest().body("Configuración no encontrada");
            }

            // payload de prueba
            Map<String,Object> request = new HashMap<>();
            request.put("nombreUsuario",        config.getUsuario().getNombre());
            request.put("tipo",                 config.getTipo());
            request.put("email",                config.getEmail());
            request.put("umbral",               config.getUmbral().doubleValue());
            request.put("mensajePersonalizado", config.getMensajePersonalizado());
            request.put("billetera",            List.of());
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String,Object>> entity = new HttpEntity<>(request, headers);

            restTemplate.exchange(
                URI.create(POWER_AUTOMATE_BASE_URL + POWER_AUTOMATE_QUERY_PARAMS),
                HttpMethod.POST,
                entity,
                String.class
            );

            return ResponseEntity.ok("Notificación de prueba enviada a Power Automate");
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body("Error al enviar notificación: " + e.getMessage());
        }
    }
}
