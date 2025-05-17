package cryptotracker.controlador;

import cryptotracker.modelo.entity.Billetera;
import cryptotracker.modelo.entity.Criptomoneda;
import cryptotracker.modelo.entity.Usuario;
import cryptotracker.modelo.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;

@RestController
@RequestMapping("/api/billetera")
@CrossOrigin(origins = "*")
public class BilleteraController {

    @Autowired
    private UsuarioRepository usuarioRepo;

    // DTO para agregar criptomoneda
    public static class CriptoDTO {
        public String nombre;
        public double cantidad;
    }

    // GET /api/billetera/{usuario}
    // Devuelve las criptos de la billetera
    @GetMapping("/{nombreUsuario}")
    public ResponseEntity<?> obtenerBilletera(@PathVariable String nombreUsuario) {
        Optional<Usuario> usuarioOpt = usuarioRepo.findByNombre(nombreUsuario);
        if (usuarioOpt.isEmpty()) return ResponseEntity.notFound().build();

        Usuario usuario = usuarioOpt.get();
        Billetera billetera = usuario.getBilletera();
        if (billetera == null) return ResponseEntity.ok(Collections.emptyList());

        return ResponseEntity.ok(billetera.getCriptomonedas());
    }

    // POST /api/billetera/{usuario}/agregar
    // Agrega o actualiza una cripto en la billetera
    
    @PostMapping("/{nombreUsuario}/agregar")
    @Transactional
    public ResponseEntity<String> agregarCriptomoneda(
            @PathVariable String nombreUsuario,
            @RequestBody CriptoDTO criptoData
    ) {
        Optional<Usuario> usuarioOpt = usuarioRepo.findByNombre(nombreUsuario);
        if (usuarioOpt.isEmpty()) return ResponseEntity.notFound().build();

        Usuario usuario = usuarioOpt.get();
        Billetera billetera = usuario.getBilletera();

        // Crear billetera si no existe aún
        if (billetera == null) {
            billetera = new Billetera();
            billetera.setUsuario(usuario);
            billetera.setCriptomonedas(new ArrayList<>());
            usuario.setBilletera(billetera);
        }

        // Ver si ya existe esa cripto en la billetera
        Criptomoneda existente = billetera.getCriptomonedas()
                .stream()
                .filter(c -> c.getNombre().equalsIgnoreCase(criptoData.nombre))
                .findFirst()
                .orElse(null);

        if (existente != null) {
            // Si ya existe, actualizar cantidad
            existente.setCantidad(existente.getCantidad() + criptoData.cantidad);
        } else {
            // Si no existe, crear nueva cripto
            Criptomoneda nueva = new Criptomoneda();
            nueva.setNombre(criptoData.nombre);
            nueva.setCantidad(criptoData.cantidad);
            nueva.setBilletera(billetera);
            billetera.getCriptomonedas().add(nueva);
        }

        usuarioRepo.save(usuario); // se guarda todo gracias al cascade

        return ResponseEntity.ok("Criptomoneda agregada");
    }
    
    
    // PUT /api/billetera/{usuario}/modificar
    @PutMapping("/{nombreUsuario}/modificar")
    @Transactional
    public ResponseEntity<String> modificarCriptomoneda(
            @PathVariable String nombreUsuario,
            @RequestBody CriptoDTO criptoData
    ) {
        Optional<Usuario> usuarioOpt = usuarioRepo.findByNombre(nombreUsuario);
        if (usuarioOpt.isEmpty()) return ResponseEntity.notFound().build();

        Usuario usuario = usuarioOpt.get();
        Billetera billetera = usuario.getBilletera();
        if (billetera == null) return ResponseEntity.badRequest().body("El usuario no tiene billetera");

        Optional<Criptomoneda> criptoOpt = billetera.getCriptomonedas()
                .stream()
                .filter(c -> c.getNombre().equalsIgnoreCase(criptoData.nombre))
                .findFirst();

        if (criptoOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Criptomoneda no encontrada");
        }

        Criptomoneda cripto = criptoOpt.get();
        cripto.setCantidad(criptoData.cantidad);

        usuarioRepo.save(usuario);

        return ResponseEntity.ok("Criptomoneda modificada");
    }
          
    
    // DELETE /api/billetera/{usuario}/eliminar/{nombreCripto}
    @DeleteMapping("/{nombreUsuario}/eliminar/{nombreCripto}")
    @Transactional
    public ResponseEntity<String> eliminarCriptomoneda(
            @PathVariable String nombreUsuario,
            @PathVariable String nombreCripto
    ) {
        Optional<Usuario> usuarioOpt = usuarioRepo.findByNombre(nombreUsuario);
        if (usuarioOpt.isEmpty()) return ResponseEntity.notFound().build();

        Usuario usuario = usuarioOpt.get();
        Billetera billetera = usuario.getBilletera();
        if (billetera == null) return ResponseEntity.badRequest().body("El usuario no tiene billetera");

        boolean eliminada = billetera.getCriptomonedas()
                .removeIf(c -> c.getNombre().equalsIgnoreCase(nombreCripto));

        if (!eliminada) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Criptomoneda no encontrada");
        }

        usuarioRepo.save(usuario);
        return ResponseEntity.ok("Criptomoneda eliminada");
    }

    
   

}
