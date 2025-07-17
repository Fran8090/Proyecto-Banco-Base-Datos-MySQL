package banco.modelo.empleado.beans;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import banco.utils.Fechas;

public class DAOClienteImpl implements DAOCliente {

	private static Logger logger = LoggerFactory.getLogger(DAOClienteImpl.class);
	
	private Connection conexion;
	
	public DAOClienteImpl(Connection c) {
		this.conexion = c;
	}
	
	@Override
	public ClienteBean recuperarCliente(String tipoDoc, int nroDoc) throws Exception {

		logger.info("recupera el cliente con documento de tipo {} y nro {}.", tipoDoc, nroDoc);
		
		String sql = "SELECT nro_cliente,apellido,nombre,direccion,telefono,fecha_nac FROM Cliente WHERE tipo_doc = '"+tipoDoc+"' AND nro_doc = "+nroDoc+";";
		
		PreparedStatement stmt = null;
		ResultSet rs = null;
		ClienteBean cliente = new ClienteBeanImpl();
		try {
			 stmt = conexion.prepareStatement(sql);
			 rs = stmt.executeQuery();
			
			if(rs.next()) {
				cliente.setNroCliente(rs.getInt("nro_cliente"));
				cliente.setApellido(rs.getString("apellido"));
				cliente.setNombre(rs.getString("nombre"));
				cliente.setTipoDocumento(tipoDoc);
				cliente.setNroDocumento(nroDoc);
				cliente.setDireccion(rs.getString("direccion"));
				cliente.setTelefono(rs.getString("telefono"));
				cliente.setFechaNacimiento(rs.getDate("fecha_nac"));
				logger.debug("Cliente recuperado: {}",cliente);
				
			}
		}catch (SQLException e) {
			logger.error("Error al recuperar el cliente con nroDoc{}",nroDoc,e);
			throw new Exception ("Error al recuperar el cliente con nroDoc "+nroDoc,e);
		} finally {
			if (rs != null) rs.close();
			if (stmt != null) stmt.close();
		}
		return cliente;
	}
		

	@Override
	public ClienteBean recuperarCliente(Integer nroCliente) throws Exception {
		logger.info("recupera el cliente por nro de cliente.");
		
String sql = "SELECT nro_doc,tipo_doc,apellido,nombre,direccion,telefono,fecha_nac FROM Cliente WHERE nro_cliente = "+nroCliente+";";
		
		PreparedStatement stmt = null;
		ResultSet rs = null;
		ClienteBean cliente = new ClienteBeanImpl();
		try {
			 stmt = conexion.prepareStatement(sql);
			 rs = stmt.executeQuery();
			
			if(rs.next()) {
				cliente.setNroCliente(nroCliente);
				cliente.setApellido(rs.getString("apellido"));
				cliente.setNombre(rs.getString("nombre"));
				cliente.setTipoDocumento(rs.getString("tipo_doc"));
				cliente.setNroDocumento(rs.getInt("nro_doc"));
				cliente.setDireccion(rs.getString("direccion"));
				cliente.setTelefono(rs.getString("telefono"));
				cliente.setFechaNacimiento(rs.getDate("fecha_nac"));
				logger.debug("Cliente recuperado: {}",cliente);
				
			}
		}catch (SQLException e) {
			logger.error("Error al recuperar el cliente con nroCliente{}",nroCliente,e);
			throw new Exception ("Error al recuperar el cliente con nroCliente "+nroCliente,e);
		} finally {
			if (rs != null) rs.close();
			if (stmt != null) stmt.close();
		}
		return cliente;
	}

}
