package uniandes.edu.co.epsandes.modelo;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "ORDENDESERVICIO")
public class OrdenDeServicio {
    
    @Id
    @Column(name = "ID_Orden")
    private Long idOrden;
    
    @Column(name = "Fecha_hora", nullable = false)
    private LocalDateTime fechaHora;
    
    @Column(name = "EstadoOrden", length = 20, nullable = false)
    private String estadoOrden;
    
    @ManyToOne
    @JoinColumn(name = "Medico_NumeroDocumento", nullable = false)
    private Medico medico;
    
    @ManyToOne
    @JoinColumn(name = "Afiliado_NumeroDocumento", nullable = false)
    private Afiliado afiliado;
    
    @ManyToMany
    @JoinTable(
        name = "ServicioOrden",
        joinColumns = @JoinColumn(name = "OrdenDeServicio_ID_Orden"),
        inverseJoinColumns = @JoinColumn(name = "ServicioDeSalud_ID_Servicio")
    )
    private Set<ServicioDeSalud> servicios = new HashSet<>();
    
    @OneToMany(mappedBy = "ordenDeServicio")
    private Set<AgendarCita> citas = new HashSet<>();

    // Constructor vacío
    public OrdenDeServicio() {}

    // Constructor con parámetros
    public OrdenDeServicio(Long idOrden, LocalDateTime fechaHora, String estadoOrden, 
                          Medico medico, Afiliado afiliado) {
        this.idOrden = idOrden;
        this.fechaHora = fechaHora;
        this.estadoOrden = estadoOrden;
        this.medico = medico;
        this.afiliado = afiliado;
    }

    // Getters y Setters
    public Long getIdOrden() {
        return idOrden;
    }

    public void setIdOrden(Long idOrden) {
        this.idOrden = idOrden;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }

    public String getEstadoOrden() {
        return estadoOrden;
    }

    public void setEstadoOrden(String estadoOrden) {
        this.estadoOrden = estadoOrden;
    }

    public Medico getMedico() {
        return medico;
    }

    public void setMedico(Medico medico) {
        this.medico = medico;
    }

    public Afiliado getAfiliado() {
        return afiliado;
    }

    public void setAfiliado(Afiliado afiliado) {
        this.afiliado = afiliado;
    }

    public Set<ServicioDeSalud> getServicios() {
        return servicios;
    }

    public void setServicios(Set<ServicioDeSalud> servicios) {
        this.servicios = servicios;
    }

    public Set<AgendarCita> getCitas() {
        return citas;
    }

    public void setCitas(Set<AgendarCita> citas) {
        this.citas = citas;
    }
}