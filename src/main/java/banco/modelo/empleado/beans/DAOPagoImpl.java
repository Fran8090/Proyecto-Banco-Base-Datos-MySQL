package banco.modelo.empleado.beans;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import banco.utils.Fechas;


public class DAOPagoImpl implements DAOPago {

	private static Logger logger = LoggerFactory.getLogger(DAOPagoImpl.class);
	
	private Connection conexion;
	
	public DAOPagoImpl(Connection c) {
		this.conexion = c;
	}

	@Override
	public ArrayList<PagoBean> recuperarPagos(int nroPrestamo) throws Exception {
	    logger.info("Inicia la recuperación de los pagos del préstamo {}", nroPrestamo);

	    // Declaración de la lista que contendrá los pagos
	    ArrayList<PagoBean> lista = new ArrayList<>();
	    PreparedStatement stmt = null;
	    ResultSet rs = null;

	    try {
	        // Consulta SQL para obtener todos los pagos del préstamo
	        String sql = "SELECT nro_prestamo, nro_pago, fecha_venc, fecha_pago " +
	                     "FROM pago WHERE nro_prestamo = ?";
	        stmt = conexion.prepareStatement(sql);
	        stmt.setInt(1, nroPrestamo);

	        // Ejecutar la consulta
	        rs = stmt.executeQuery();

	        // Procesar el resultado
	        while (rs.next()) {
	            PagoBean fila = new PagoBeanImpl();
	            fila.setNroPrestamo(rs.getInt("nro_prestamo"));
	            fila.setNroPago(rs.getInt("nro_pago"));
	            fila.setFechaVencimiento(rs.getDate("fecha_venc"));
	            fila.setFechaPago(rs.getDate("fecha_pago"));  // Puede ser null si está impago

	            // Añadir el pago a la lista
	            lista.add(fila);
	        }

	    } catch (SQLException e) {
	        logger.error("Error al recuperar los pagos del préstamo {}: {}", nroPrestamo, e.getMessage());
	        throw new Exception("Error al recuperar los pagos del préstamo", e);
	    } finally {
	        // Cerrar ResultSet y PreparedStatement
	        if (rs != null) try { rs.close(); } catch (SQLException e) { logger.error("Error al cerrar ResultSet", e); }
	        if (stmt != null) try { stmt.close(); } catch (SQLException e) { logger.error("Error al cerrar PreparedStatement", e); }
	    }

	    // Retornar la lista de pagos
	    return lista;
	}

	@Override
	public void registrarPagos(int nroPrestamo, List<Integer> cuotasAPagar) throws Exception {
	    logger.info("Inicia el pago de las {} cuotas del préstamo {}", cuotasAPagar.size(), nroPrestamo);

	    PreparedStatement stmtSelect = null;
	    PreparedStatement stmtUpdate = null;
	    ResultSet rs = null;

	    try {
	        // Primero, verificamos qué cuotas están pendientes de pago (fecha_pago = NULL)
	        String selectSql = "SELECT nro_pago FROM pago WHERE nro_prestamo = ? AND fecha_pago IS NULL AND nro_pago = ?";
	        String updateSql = "UPDATE pago SET fecha_pago = CURRENT_DATE() WHERE nro_prestamo = ? AND nro_pago = ?";

	        // Preparamos la consulta para verificar si la cuota está pendiente de pago
	        stmtSelect = conexion.prepareStatement(selectSql);

	        // Preparamos la actualización para registrar el pago
	        stmtUpdate = conexion.prepareStatement(updateSql);

	        // Recorremos cada cuota a pagar
	        for (Integer nroCuota : cuotasAPagar) {
	            // Verificamos si la cuota está impaga
	            stmtSelect.setInt(1, nroPrestamo);
	            stmtSelect.setInt(2, nroCuota);
	            rs = stmtSelect.executeQuery();

	            if (rs.next()) {
	                // Si está impaga, registramos el pago actualizando la fecha de pago con CURRENT_DATE()
	                stmtUpdate.setInt(1, nroPrestamo);
	                stmtUpdate.setInt(2, nroCuota);

	                // Ejecutamos la actualización
	                stmtUpdate.executeUpdate();
	                logger.info("Registrado el pago de la cuota nro {} del préstamo {}", nroCuota, nroPrestamo);
	            } else {
	                // La cuota ya estaba pagada o no existe
	                logger.warn("La cuota nro {} del préstamo {} ya estaba pagada o no existe", nroCuota, nroPrestamo);
	            }
	        }

	    } catch (SQLException e) {
	        logger.error("Error al registrar los pagos del préstamo {}: {}", nroPrestamo, e.getMessage());
	        throw new Exception("Error al registrar los pagos del préstamo", e);
	    } finally {
	        // Cerrar ResultSet, PreparedStatements
	        if (rs != null) try { rs.close(); } catch (SQLException e) { logger.error("Error al cerrar ResultSet", e); }
	        if (stmtSelect != null) try { stmtSelect.close(); } catch (SQLException e) { logger.error("Error al cerrar PreparedStatement de consulta", e); }
	        if (stmtUpdate != null) try { stmtUpdate.close(); } catch (SQLException e) { logger.error("Error al cerrar PreparedStatement de actualización", e); }
	    }
	}

}
