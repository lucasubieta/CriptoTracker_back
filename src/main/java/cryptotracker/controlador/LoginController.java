package cryptotracker.controlador;

import cryptotracker.modelo.entity.Usuario;
import cryptotracker.modelo.repository.UsuarioRepository;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/login")
@CrossOrigin(origins = "*")
public class LoginController {

    @Autowired
    private UsuarioRepository usuarioRepo;

    public static class LoginDTO {
        public String identificador;  // Correo o usuario
        public String contraseña;
    }


    // Creo este usuarioDTO para devolverlo ya que para las notificaciones necesito el correo y no lo estaba enviando.
    public static class UsuarioDTO {
        public String nombre;
        public String correo;

        public UsuarioDTO(Usuario usuario) {
            this.nombre = usuario.getNombre();
            this.correo = usuario.getCorreo();
        }
    }
    
    
    @PostMapping
    public ResponseEntity<?> login(@RequestBody LoginDTO datos) {
        Optional<Usuario> usuarioOpt = usuarioRepo.findByNombre(datos.identificador);

        if (usuarioOpt.isEmpty()) {
            usuarioOpt = usuarioRepo.findByCorreo(datos.identificador);
        }

        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciales incorrectas");
        }

        Usuario usuario = usuarioOpt.get();

        if (usuario.getContraseña().equals(datos.contraseña)) {
            return ResponseEntity.ok(new UsuarioDTO(usuario));
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciales incorrectas");
    }

}