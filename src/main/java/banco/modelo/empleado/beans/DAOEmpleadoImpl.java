package banco.modelo.empleado.beans;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DAOEmpleadoImpl implements DAOEmpleado {

	private static Logger logger = LoggerFactory.getLogger(DAOEmpleadoImpl.class);
	
	private Connection conexion;
	
	public DAOEmpleadoImpl(Connection c) {
		this.conexion = c;
	}


	@Override
	public EmpleadoBean recuperarEmpleado(int legajo) throws Exception {
	    logger.info("Recupera el empleado que corresponde al legajo {}.", legajo);

	    
	    PreparedStatement statement = null;
	    ResultSet resultSet = null;
	    EmpleadoBean empleado = null;

	    try {

	        // Prepara la consulta SQL
	        String sql = "SELECT legajo, apellido, nombre, tipo_doc, nro_doc, direccion, telefono, cargo, nro_suc, password FROM empleado WHERE legajo = "+legajo+"";
	        statement = conexion.prepareStatement(sql);

	        // Ejecuta la consulta
	        resultSet = statement.executeQuery();

	        // Si encuentra un empleado, llena los datos
	        if (resultSet.next()) {
	            empleado = new EmpleadoBeanImpl();
	            empleado.setLegajo(resultSet.getInt(legajo));
	            empleado.setApellido(resultSet.getString("apellido"));
	            empleado.setNombre(resultSet.getString("nombre"));
	            empleado.setTipoDocumento(resultSet.getString("tipo_doc"));
	            empleado.setNroDocumento(resultSet.getInt("nro_doc"));
	            empleado.setDireccion(resultSet.getString("direccion"));
	            empleado.setTelefono(resultSet.getString("telefono"));
	            empleado.setCargo(resultSet.getString("cargo"));
	            empleado.setPassword(resultSet.getString("password"));
	            empleado.setNroSucursal(resultSet.getInt("nro_suc"));
	        }

	    } catch (SQLException e) {
	        logger.error("Error al recuperar el empleado con legajo {}: {}", legajo, e.getMessage());
	        throw new Exception("Error al recuperar el empleado", e);
	    } finally {
	        // Cierra los recursos
	        if (resultSet != null) {
	                resultSet.close();
	        }
	        if (statement != null) {
	                statement.close();
	        }
	    }

	    return empleado; // Devuelve el empleado o null si no se encontró
	}


}
