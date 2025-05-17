package cryptotracker.modelo.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
public class ConfigNotificacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Boolean activo = true;
    private String tipo; // DIARIO, SEMANAL, MENSUAL
    private String email;
    private BigDecimal umbral;
    private String mensajePersonalizado;

    @OneToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    // Getters y setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public BigDecimal getUmbral() { return umbral; }
    public void setUmbral(BigDecimal umbral) { this.umbral = umbral; }

    public String getMensajePersonalizado() { return mensajePersonalizado; }
    public void setMensajePersonalizado(String mensajePersonalizado) { this.mensajePersonalizado = mensajePersonalizado; }
}