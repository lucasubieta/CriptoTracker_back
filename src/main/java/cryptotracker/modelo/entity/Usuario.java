package cryptotracker.modelo.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;

@Entity
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String nombre;
    
    @Column(nullable = false)
    private String contraseña;
    
    @Column(unique = true, nullable = false)
    private String correo;
    
    // Relación uno a uno con billetera
    @OneToOne(mappedBy = "usuario", cascade = CascadeType.ALL)
    @JsonIgnore
    private Billetera billetera;

    // Relación uno a uno con config de notificaciones
    @OneToOne(mappedBy = "usuario", cascade = CascadeType.ALL)
    private ConfigNotificacion config;

    // Getters y setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getContraseña() { return contraseña; }
    public void setContraseña(String contraseña) { this.contraseña = contraseña; }

    public Billetera getBilletera() { return billetera; }
    public void setBilletera(Billetera billetera) { this.billetera = billetera; }

    public ConfigNotificacion getConfig() { return config; }
    
    public void setConfig(ConfigNotificacion config) { this.config = config; }
    
	public String getCorreo() {
		return correo;
	}
	public void setCorreo(String correo) {
		this.correo = correo;
	}
}
