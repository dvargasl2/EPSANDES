package uniandes.edu.co.epsandes.servicio;

import uniandes.edu.co.epsandes.modelo.Afiliado;
import uniandes.edu.co.epsandes.repositorio.AfiliadoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

@Service
public class AfiliadoService {

    private static final Logger logger = LoggerFactory.getLogger(AfiliadoService.class);

    @Autowired
    private AfiliadoRepository afiliadoRepository;

    // RF5 - Registrar afiliado
    @Transactional
    public Afiliado registrarAfiliado(Afiliado afiliado) {
        logger.debug("Intentando registrar Afiliado: {}", afiliado);
        if (afiliadoRepository.existsById(afiliado.getNumeroDocumento())) {
            logger.warn("Ya existe un afiliado con el número de documento: {}", afiliado.getNumeroDocumento());
            throw new RuntimeException("Ya existe un afiliado con el número de documento: " + afiliado.getNumeroDocumento());
        }
        if ("BENEFICIARIO".equals(afiliado.getTipoAfiliado())) {
            if (afiliado.getNumeroDocumentoContribuyente() == null) {
                logger.warn("Un beneficiario debe tener un contribuyente asociado");
                throw new RuntimeException("Un beneficiario debe tener un contribuyente asociado");
            }
            Afiliado contribuyente = afiliadoRepository.findById(afiliado.getNumeroDocumentoContribuyente())
                    .orElseThrow(() -> new RuntimeException("Contribuyente no encontrado con documento: " + 
                            afiliado.getNumeroDocumentoContribuyente()));
            if (!"CONTRIBUYENTE".equals(contribuyente.getTipoAfiliado())) {
                logger.warn("El afiliado referenciado no es un contribuyente");
                throw new RuntimeException("El afiliado referenciado no es un contribuyente");
            }
            afiliado.setContribuyente(contribuyente);
        }
        Afiliado saved = afiliadoRepository.save(afiliado);
        logger.info("Afiliado registrado exitosamente: {}", saved);
        return saved;
    }

    // Obtener todos los afiliados
    public List<Afiliado> obtenerTodosAfiliados() {
        return afiliadoRepository.findAll();
    }

    // Obtener afiliado por número de documento
    public Optional<Afiliado> obtenerAfiliadoPorDocumento(Long numeroDocumento) {
        return afiliadoRepository.findById(numeroDocumento);
    }

    // Obtener afiliados por tipo
    public List<Afiliado> obtenerAfiliadosPorTipo(String tipoAfiliado) {
        return afiliadoRepository.findByTipoAfiliado(tipoAfiliado);
    }

    // Obtener beneficiarios de un contribuyente
    public List<Afiliado> obtenerBeneficiarios(Long numeroDocumentoContribuyente) {
        return afiliadoRepository.findByNumeroDocumentoContribuyente(numeroDocumentoContribuyente);
    }

    // Actualizar afiliado
    @Transactional
    public Afiliado actualizarAfiliado(Long numeroDocumento, Afiliado afiliadoActualizado) {
        Afiliado afiliado = afiliadoRepository.findById(numeroDocumento)
                .orElseThrow(() -> new RuntimeException("Afiliado no encontrado con documento: " + numeroDocumento));

        afiliado.setNombre(afiliadoActualizado.getNombre());
        afiliado.setDireccion(afiliadoActualizado.getDireccion());
        afiliado.setTelefono(afiliadoActualizado.getTelefono());
        afiliado.setFechaNacimiento(afiliadoActualizado.getFechaNacimiento());

        // No se permite cambiar el tipo de afiliado ni el contribuyente asociado

        return afiliadoRepository.save(afiliado);
    }

    // Eliminar afiliado
    @Transactional
    public void eliminarAfiliado(Long numeroDocumento) {
        Afiliado afiliado = afiliadoRepository.findById(numeroDocumento)
                .orElseThrow(() -> new RuntimeException("Afiliado no encontrado con documento: " + numeroDocumento));

        // Si es contribuyente, verificar que no tenga beneficiarios
        if ("CONTRIBUYENTE".equals(afiliado.getTipoAfiliado()) && afiliadoRepository.tieneBeneficiarios(numeroDocumento)) {
            throw new RuntimeException("No se puede eliminar un contribuyente que tiene beneficiarios asociados");
        }

        afiliadoRepository.deleteById(numeroDocumento);
    }
}