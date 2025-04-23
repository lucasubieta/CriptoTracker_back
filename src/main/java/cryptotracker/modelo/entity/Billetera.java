package cryptotracker.modelo.entity;

import jakarta.persistence.*;
import java.util.List;

@Entity
public class Billetera {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relación uno a uno con usuario
    @OneToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    // Una billetera tiene muchas criptomonedas
    @OneToMany(mappedBy = "billetera", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Criptomoneda> criptomonedas;


    // Getters y setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    public List<Criptomoneda> getCriptomonedas() { return criptomonedas; }
    public void setCriptomonedas(List<Criptomoneda> criptomonedas) { this.criptomonedas = criptomonedas; }
}
