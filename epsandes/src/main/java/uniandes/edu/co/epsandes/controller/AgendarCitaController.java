package uniandes.edu.co.epsandes.controller;
import uniandes.edu.co.epsandes.modelo.AgendarCita;
import uniandes.edu.co.epsandes.modelo.PrestacionServicio;
import uniandes.edu.co.epsandes.servicio.AgendarCitaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/citas")
public class AgendarCitaController {

    private static final Logger logger = LoggerFactory.getLogger(AgendarCitaController.class);

    @Autowired
    private AgendarCitaService citaService;

    // RF7.1 - Consultar la agenda de disponibilidad de un servicio
    @GetMapping("/disponibilidad/{servicioId}")
    public ResponseEntity<List<Object[]>> consultarDisponibilidadServicio(@PathVariable Long servicioId) {
        List<Object[]> disponibilidad = citaService.consultarDisponibilidadServicio(servicioId);
        return new ResponseEntity<>(disponibilidad, HttpStatus.OK);
    }

    // RF7.2 - Agendar un servicio de salud
    @PostMapping
    public ResponseEntity<AgendarCita> agendarServicio(@RequestBody AgendarCita cita) {
        logger.debug("POST /api/citas - Body: {}", cita);
        try {
            AgendarCita nuevaCita = citaService.agendarServicio(cita);
            logger.info("Cita agendada: {}", nuevaCita);
            return new ResponseEntity<>(nuevaCita, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            logger.error("Error al agendar cita: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        }
    }

    // RF8 - Registrar la prestación de un servicio de salud
    @PostMapping("/{citaId}/prestacion")
    public ResponseEntity<PrestacionServicio> registrarPrestacionServicio(
            @PathVariable Long citaId,
            @RequestParam Long ipsNit) {
        try {
            if (!citaService.obtenerCitaPorId(citaId).isPresent()) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            PrestacionServicio prestacion = citaService.registrarPrestacionServicio(citaId, ipsNit);
            return new ResponseEntity<>(prestacion, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Obtener todas las citas
    @GetMapping
    public ResponseEntity<List<AgendarCita>> obtenerTodasCitas() {
        List<AgendarCita> citas = citaService.obtenerTodasCitas();
        return new ResponseEntity<>(citas, HttpStatus.OK);
    }

    // Obtener cita por ID
    @GetMapping("/{id}")
    public ResponseEntity<AgendarCita> obtenerCitaPorId(@PathVariable Long id) {
        try {
            return citaService.obtenerCitaPorId(id)
                    .map(cita -> new ResponseEntity<>(cita, HttpStatus.OK))
                    .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Obtener citas por afiliado
    @GetMapping("/afiliado/{numeroDocumento}")
    public ResponseEntity<List<AgendarCita>> obtenerCitasPorAfiliado(@PathVariable Long numeroDocumento) {
        List<AgendarCita> citas = citaService.obtenerCitasPorAfiliado(numeroDocumento);
        return new ResponseEntity<>(citas, HttpStatus.OK);
    }

    // Obtener citas por médico
    @GetMapping("/medico/{numeroDocumento}")
    public ResponseEntity<List<AgendarCita>> obtenerCitasPorMedico(@PathVariable Long numeroDocumento) {
        List<AgendarCita> citas = citaService.obtenerCitasPorMedico(numeroDocumento);
        return new ResponseEntity<>(citas, HttpStatus.OK);
    }

    // Eliminar cita
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarCita(@PathVariable Long id) {
        try {
            citaService.eliminarCita(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}