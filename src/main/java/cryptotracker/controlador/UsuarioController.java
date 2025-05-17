package cryptotracker.controlador;

import cryptotracker.modelo.entity.Usuario;
import cryptotracker.modelo.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "*")
public class UsuarioController {

    @Autowired
    private UsuarioRepository usuarioRepo;

    public static class UsuarioDTO {
        public String nombre;
        public String contraseña;
        public String correo;
    }

    @PostMapping("/registrar")
    public ResponseEntity<String> registrarUsuario(@RequestBody UsuarioDTO datos) {
        // Verificar si nombre o correo ya existen
        if (usuarioRepo.findByNombre(datos.nombre).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("El nombre de usuario ya existe");
        }
        
        if (usuarioRepo.findByCorreo(datos.correo).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("El correo electrónico ya está registrado");
        }

        // Crear nuevo usuario
        Usuario nuevo = new Usuario();
        nuevo.setNombre(datos.nombre);
        nuevo.setContraseña(datos.contraseña); 
        nuevo.setCorreo(datos.correo);
        
        usuarioRepo.save(nuevo);

        return ResponseEntity.status(HttpStatus.CREATED).body("Usuario registrado exitosamente");
    }
}