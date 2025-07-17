package banco.modelo.empleado.beans;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import banco.utils.Fechas;

public class DAOPrestamoImpl implements DAOPrestamo {

	private static Logger logger = LoggerFactory.getLogger(DAOPrestamoImpl.class);
	
	private Connection conexion;
	
	public DAOPrestamoImpl(Connection c) {
		this.conexion = c;
	}
	
	
	@Override
	public void crearPrestamo(PrestamoBean prestamo) throws Exception {

	    logger.info("Creación o actualización del préstamo.");
	    logger.debug("meses : {}", prestamo.getCantidadMeses());
	    logger.debug("monto : {}", prestamo.getMonto());
	    logger.debug("tasa : {}", prestamo.getTasaInteres());
	    logger.debug("interes : {}", prestamo.getInteres());
	    logger.debug("cuota : {}", prestamo.getValorCuota());
	    logger.debug("legajo : {}", prestamo.getLegajo());
	    logger.debug("cliente : {}", prestamo.getNroCliente());

	    PreparedStatement stmt = null;
	    ResultSet generatedKeys = null;
	    String insertSql = "INSERT INTO prestamo (fecha, cant_meses, monto, tasa_interes, interes, valor_cuota, legajo, nro_cliente) "
	                     + "VALUES (CURRENT_DATE, ?, ?, ?, ?, ?, ?, ?)";

	    try {
	        // Preparamos el statement para insertar un nuevo préstamo
	        stmt = conexion.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS);

	        // Asignamos los valores al statement
	        stmt.setInt(1, prestamo.getCantidadMeses() != 0 ? prestamo.getCantidadMeses() : 0); // cantidad_meses
	        stmt.setDouble(2, prestamo.getMonto() != 0 ? prestamo.getMonto() : 0.0); // monto
	        stmt.setDouble(3, prestamo.getTasaInteres() != 0 ? prestamo.getTasaInteres() : 0.0); // tasa_interes
	        stmt.setDouble(4, prestamo.getInteres() != 0 ? prestamo.getInteres() : 0.0); // interes
	        stmt.setDouble(5, prestamo.getValorCuota() != 0 ? prestamo.getValorCuota() : 0.0); // valor_cuota
	        stmt.setInt(6, prestamo.getLegajo()); // legajo
	        stmt.setInt(7, prestamo.getNroCliente()); // nro_cliente

	        // Ejecutamos la inserción
	        int rowsAffected = stmt.executeUpdate();
	        logger.info("Préstamo creado con éxito. Filas afectadas: {}", rowsAffected);

	        // Recuperamos el número de préstamo generado
	        generatedKeys = stmt.getGeneratedKeys();
	        if (generatedKeys.next()) {
	            int generatedNroPrestamo = generatedKeys.getInt(1);
	            prestamo.setNroPrestamo(generatedNroPrestamo); // Actualizamos el bean con el valor generado
	            logger.info("Préstamo generado con nro_prestamo: {}", generatedNroPrestamo);
	        }

	    } catch (SQLException ex) {
	        // Logueamos los errores y propagamos la excepción
	        logger.error("SQLException: " + ex.getMessage());
	        logger.error("SQLState: " + ex.getSQLState());
	        logger.error("VendorError: " + ex.getErrorCode());
	        throw new Exception("Error al crear el préstamo", ex);

	    } finally {
	        // Cerramos el PreparedStatement y el ResultSet
	        if (generatedKeys != null) {
	                generatedKeys.close();
	        }
	        if (stmt != null) {
	                stmt.close();
	        }
	    }
	}

	@Override
	public PrestamoBean recuperarPrestamo(int nroPrestamo) throws Exception {
	    logger.info("Recupera el prestamo nro {}.", nroPrestamo);

	    // Declaración del objeto PrestamoBean
	    PrestamoBean prestamo = null;
	    PreparedStatement stmt = null;
	    ResultSet rs = null;

	    try {
	        // Preparar la consulta SQL
	        String sql = "SELECT nro_prestamo, fecha, cant_meses, monto, tasa_interes, interes, valor_cuota, legajo, nro_cliente " +
	                     "FROM prestamo WHERE nro_prestamo = "+nroPrestamo+"";
	        stmt = conexion.prepareStatement(sql);

	        // Ejecutar la consulta
	        rs = stmt.executeQuery();

	        // Si se encuentra el préstamo con el ID proporcionado
	        if (rs.next()) {
	            prestamo = new PrestamoBeanImpl();
	            prestamo.setNroPrestamo(rs.getInt("nro_prestamo"));
	            prestamo.setFecha(rs.getDate("fecha"));
	            prestamo.setCantidadMeses(rs.getInt("cant_meses"));
	            prestamo.setMonto(rs.getDouble("monto"));
	            prestamo.setTasaInteres(rs.getDouble("tasa_interes"));
	            prestamo.setInteres(rs.getDouble("interes"));
	            prestamo.setValorCuota(rs.getDouble("valor_cuota"));
	            prestamo.setLegajo(rs.getInt("legajo"));
	            prestamo.setNroCliente(rs.getInt("nro_cliente"));
	        }

	    } catch (SQLException e) {
	        logger.error("Error al recuperar el prestamo nro {}: {}", nroPrestamo, e.getMessage());
	        throw new Exception("Error al recuperar el prestamo", e);
	    } finally {
	        // Cerrar ResultSet y PreparedStatement
	        if (rs != null) rs.close(); 
	        if (stmt != null) stmt.close(); 
	    }

	    // Retornar el préstamo, si no se encontró se retornará null
	    return prestamo;
	}

}
