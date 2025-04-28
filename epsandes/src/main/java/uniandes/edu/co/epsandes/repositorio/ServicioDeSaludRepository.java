package uniandes.edu.co.epsandes.repositorio;
import uniandes.edu.co.epsandes.modelo.ServicioDeSalud;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServicioDeSaludRepository extends JpaRepository<ServicioDeSalud, Long> {
    
    // Buscar servicios por tipo
    List<ServicioDeSalud> findByTipo(String tipo);
    
    // Buscar servicios por nombre
    List<ServicioDeSalud> findByNombreContaining(String nombre);
    
    // Obtener los 20 servicios más solicitados
    @Query(value = "SELECT s.*, COUNT(ac.IDCita) as total_solicitudes " +
           "FROM ServicioDeSalud s " +
           "LEFT JOIN AgendarCita ac ON s.ID_Servicio = ac.ServicioDeSalud_ID " +
           "WHERE ac.Fecha_hora BETWEEN :fechaInicio AND :fechaFin " +
           "GROUP BY s.ID_Servicio, s.Nombre, s.Descripcion, s.Tipo " +
           "ORDER BY total_solicitudes DESC " +
           "FETCH FIRST 20 ROWS ONLY", nativeQuery = true)
    List<ServicioDeSalud> findTop20ServiciosMasSolicitados(@Param("fechaInicio") String fechaInicio, 
                                                          @Param("fechaFin") String fechaFin);
    
    // Buscar servicios disponibles en una IPS específica
    @Query("SELECT s FROM ServicioDeSalud s JOIN s.ipsSet i WHERE i.nit = :nit")
    List<ServicioDeSalud> findByIpsNit(@Param("nit") Long nit);
}