package uniandes.edu.co.epsandes.servicio;
import uniandes.edu.co.epsandes.modelo.IPS;
import uniandes.edu.co.epsandes.modelo.ServicioDeSalud;
import uniandes.edu.co.epsandes.repositorio.IPSRepository;
import uniandes.edu.co.epsandes.modelo.Medico;
import uniandes.edu.co.epsandes.repositorio.MedicoRepository;
import uniandes.edu.co.epsandes.repositorio.ServicioDeSaludRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

@Service
public class MedicoService {

    private static final Logger logger = LoggerFactory.getLogger(MedicoService.class);

    @Autowired
    private MedicoRepository medicoRepository;

    @Autowired
    private IPSRepository ipsRepository;

    @Autowired
    private ServicioDeSaludRepository servicioRepository;

    // RF4 - Registrar médico
    @Transactional
    public Medico registrarMedico(Medico medico) {
        logger.debug("Intentando registrar Medico: {}", medico);
        // Verificar si ya existe un médico con el mismo número de documento
        if (medicoRepository.existsById(medico.getNumeroDocumento())) {
            logger.warn("Ya existe un médico con el número de documento: {}", medico.getNumeroDocumento());
            throw new RuntimeException("Ya existe un médico con el número de documento: " + medico.getNumeroDocumento());
        }

        // Verificar si ya existe un médico con el mismo número de registro médico
        Optional<Medico> medicoExistente = medicoRepository.findByNumeroRegistroMedico(medico.getNumeroRegistroMedico());
        if (medicoExistente.isPresent()) {
            logger.warn("Ya existe un médico con el número de registro médico: {}", medico.getNumeroRegistroMedico());
            throw new RuntimeException("Ya existe un médico con el número de registro médico: " + medico.getNumeroRegistroMedico());
        }

        // Validar que la IPS no sea null y tenga NIT
        logger.debug("Contenido de medico.getIps(): {}", medico.getIps());
        if (medico.getIps() != null) {
            logger.debug("Contenido de medico.getIps().getNit(): {}", medico.getIps().getNit());
        }
        if (medico.getIps() == null || medico.getIps().getNit() == null) {
            logger.warn("El campo 'ips' y su 'nit' son obligatorios para registrar un médico");
            throw new RuntimeException("El campo 'ips' y su 'nit' son obligatorios para registrar un médico");
        }

        // Verificar que la IPS existe
        IPS ips = ipsRepository.findById(medico.getIps().getNit())
                .orElseThrow(() -> new RuntimeException("IPS no encontrada con NIT: " + medico.getIps().getNit()));

        medico.setIps(ips);
        Medico saved = medicoRepository.save(medico);
        logger.info("Médico registrado exitosamente: {}", saved);
        return saved;
    }

    // Asignar servicio a médico
    @Transactional
    public void asignarServicioAMedico(Long medicoDocumento, Long servicioId) {
        logger.debug("Asignando servicio {} a médico {}", servicioId, medicoDocumento);
        Medico medico = medicoRepository.findById(medicoDocumento)
                .orElseThrow(() -> new RuntimeException("Médico no encontrado con documento: " + medicoDocumento));

        ServicioDeSalud servicio = servicioRepository.findById(servicioId)
                .orElseThrow(() -> new RuntimeException("Servicio no encontrado con ID: " + servicioId));

        medico.getServicios().add(servicio);
        servicio.getMedicos().add(medico);

        medicoRepository.save(medico);
        servicioRepository.save(servicio);
        logger.info("Servicio {} asignado a médico {}", servicioId, medicoDocumento);
    }

    // Obtener todos los médicos
    public List<Medico> obtenerTodosMedicos() {
        return medicoRepository.findAll();
    }

    // Obtener médico por número de documento
    public Optional<Medico> obtenerMedicoPorDocumento(Long numeroDocumento) {
        return medicoRepository.findById(numeroDocumento);
    }

    // Obtener médico por número de registro médico
    public Optional<Medico> obtenerMedicoPorRegistroMedico(Long numeroRegistroMedico) {
        return medicoRepository.findByNumeroRegistroMedico(numeroRegistroMedico);
    }

    // Obtener médicos por especialidad
    public List<Medico> obtenerMedicosPorEspecialidad(String especialidad) {
        return medicoRepository.findByEspecialidad(especialidad);
    }

    // Obtener médicos por IPS
    public List<Medico> obtenerMedicosPorIPS(Long nit) {
        return medicoRepository.findByIpsNit(nit);
    }

    // Obtener médicos que prestan un servicio específico
    public List<Medico> obtenerMedicosPorServicio(Long servicioId) {
        return medicoRepository.findByServicioId(servicioId);
    }

    // Actualizar médico
    @Transactional
    public Medico actualizarMedico(Long numeroDocumento, Medico medicoActualizado) {
        Medico medico = medicoRepository.findById(numeroDocumento)
                .orElseThrow(() -> new RuntimeException("Médico no encontrado con documento: " + numeroDocumento));

        medico.setNombre(medicoActualizado.getNombre());
        medico.setTipoDocumento(medicoActualizado.getTipoDocumento());
        medico.setEspecialidad(medicoActualizado.getEspecialidad());

        if (!medico.getIps().getNit().equals(medicoActualizado.getIps().getNit())) {
            IPS nuevaIps = ipsRepository.findById(medicoActualizado.getIps().getNit())
                    .orElseThrow(() -> new RuntimeException("IPS no encontrada con NIT: " + medicoActualizado.getIps().getNit()));
            medico.setIps(nuevaIps);
        }

        return medicoRepository.save(medico);
    }

    // Eliminar médico
    @Transactional
    public void eliminarMedico(Long numeroDocumento) {
        if (!medicoRepository.existsById(numeroDocumento)) {
            throw new RuntimeException("Médico no encontrado con documento: " + numeroDocumento);
        }
        medicoRepository.deleteById(numeroDocumento);
    }
}