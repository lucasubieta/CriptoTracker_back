package cryptotracker.controlador;

import cryptotracker.modelo.entity.Usuario;
import cryptotracker.modelo.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/login")
@CrossOrigin(origins = "*") // permitir llamadas desde Angular localmente
public class LoginController {

    @Autowired
    private UsuarioRepository usuarioRepo;

    // DTO para recibir credenciales
    public static class LoginDTO {
        public String nombre;
        public String contraseña;
    }

    // Endpoint de login
    @PostMapping
    public ResponseEntity<String> login(@RequestBody LoginDTO datos) {
        Optional<Usuario> usuarioOpt = usuarioRepo.findByNombre(datos.nombre);

        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            if (usuario.getContraseña().equals(datos.contraseña)) {
                return ResponseEntity.ok("ok"); // Si el usuario y contraseña coinciden
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("erroRRRRr");
    }
}
