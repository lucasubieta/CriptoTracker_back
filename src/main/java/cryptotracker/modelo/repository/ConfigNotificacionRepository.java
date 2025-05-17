package cryptotracker.modelo.repository;

import cryptotracker.modelo.entity.ConfigNotificacion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConfigNotificacionRepository extends JpaRepository<ConfigNotificacion, Long> {
    ConfigNotificacion findByUsuarioId(Long usuarioId);
    ConfigNotificacion findByUsuarioNombre(String nombreUsuario);

}