package uniandes.edu.co.epsandes.modelo;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "AGENDARCITA")
public class AgendarCita {
    
    @Id
    @Column(name = "IDCita")
    private Long idCita;
    
    @Column(name = "Fecha_hora", nullable = false)
    private LocalDateTime fechaHora;
    
    @ManyToOne
    @JoinColumn(name = "Afiliado_NumeroDocumento", nullable = false)
    private Afiliado afiliado;
    
    @ManyToOne
    @JoinColumn(name = "Medico_NumeroDocumento", nullable = false)
    private Medico medico;
    
    @ManyToOne
    @JoinColumn(name = "ID_OrdenDeServicio")
    private OrdenDeServicio ordenDeServicio;
    
    @ManyToOne
    @JoinColumn(name = "ServicioDeSalud_ID", nullable = false)
    private ServicioDeSalud servicioDeSalud;
    
    @OneToOne(mappedBy = "agendarCita")
    private PrestacionServicio prestacionServicio;

    // Constructor vacío
    public AgendarCita() {}

    // Constructor con parámetros
    public AgendarCita(Long idCita, LocalDateTime fechaHora, Afiliado afiliado, 
                      Medico medico, OrdenDeServicio ordenDeServicio, 
                      ServicioDeSalud servicioDeSalud) {
        this.idCita = idCita;
        this.fechaHora = fechaHora;
        this.afiliado = afiliado;
        this.medico = medico;
        this.ordenDeServicio = ordenDeServicio;
        this.servicioDeSalud = servicioDeSalud;
    }

    // Getters y Setters
    public Long getIdCita() {
        return idCita;
    }

    public void setIdCita(Long idCita) {
        this.idCita = idCita;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }

    public Afiliado getAfiliado() {
        return afiliado;
    }

    public void setAfiliado(Afiliado afiliado) {
        this.afiliado = afiliado;
    }

    public Medico getMedico() {
        return medico;
    }

    public void setMedico(Medico medico) {
        this.medico = medico;
    }

    public OrdenDeServicio getOrdenDeServicio() {
        return ordenDeServicio;
    }

    public void setOrdenDeServicio(OrdenDeServicio ordenDeServicio) {
        this.ordenDeServicio = ordenDeServicio;
    }

    public ServicioDeSalud getServicioDeSalud() {
        return servicioDeSalud;
    }

    public void setServicioDeSalud(ServicioDeSalud servicioDeSalud) {
        this.servicioDeSalud = servicioDeSalud;
    }

    public PrestacionServicio getPrestacionServicio() {
        return prestacionServicio;
    }

    public void setPrestacionServicio(PrestacionServicio prestacionServicio) {
        this.prestacionServicio = prestacionServicio;
    }
}