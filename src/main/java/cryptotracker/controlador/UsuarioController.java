package cryptotracker.controlador;

import cryptotracker.modelo.entity.Usuario;
import cryptotracker.modelo.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "*")
public class UsuarioController {

    @Autowired
    private UsuarioRepository usuarioRepo;

    // DTO para crear usuario
    public static class UsuarioDTO {
        public String nombre;
        public String contraseña;
    }

    // Endpoint para registrar usuario nuevo
    @PostMapping("/registrar")
    public ResponseEntity<String> registrarUsuario(@RequestBody UsuarioDTO datos) {
        Optional<Usuario> existente = usuarioRepo.findByNombre(datos.nombre);

        if (existente.isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Usuario ya existe");
        }

        Usuario nuevo = new Usuario();
        nuevo.setNombre(datos.nombre);
        nuevo.setContraseña(datos.contraseña);
        usuarioRepo.save(nuevo);

        return ResponseEntity.status(HttpStatus.CREATED).body("Usuario registrado");
    }
}
