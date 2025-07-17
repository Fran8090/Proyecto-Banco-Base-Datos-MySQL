package banco.modelo.empleado.beans;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DAOClienteMorosoImpl implements DAOClienteMoroso {

	private static Logger logger = LoggerFactory.getLogger(DAOClienteMorosoImpl.class);
	
	private Connection conexion;
	
	public DAOClienteMorosoImpl(Connection c) {
		this.conexion = c;
	}
	
	@Override
	public ArrayList<ClienteMorosoBean> recuperarClientesMorosos() throws Exception { 
	    logger.info("Busca los clientes morosos.");

	    ArrayList<ClienteMorosoBean> morosos = new ArrayList<>();

	    // Consulta SQL para obtener los clientes con más de 2 cuotas impagas
	    String sql = "SELECT p.nro_prestamo, p.nro_cliente, COUNT(c.nro_pago) AS cuotas_impagas " +
	                 "FROM prestamo p " +
	                 "JOIN pago c ON p.nro_prestamo = c.nro_prestamo " +
	                 "WHERE c.fecha_pago IS NULL and c.fecha_venc < CURDATE()" +  // Las cuotas sin fecha de pago se consideran impagas
	                 "GROUP BY p.nro_prestamo, p.nro_cliente " +
	                 "HAVING COUNT(c.nro_pago) >= 2";  // Clientes morosos: más de 2 cuotas impagas y DEBEN SER ATRASADAS

	    try {
	        PreparedStatement stmt = conexion.prepareStatement(sql);
	        ResultSet rs = stmt.executeQuery();

	        // Iterar sobre los resultados de la consulta
	        while (rs.next()) {
	            int nroPrestamo = rs.getInt("nro_prestamo");
	            int nroCliente = rs.getInt("nro_cliente");
	            int cuotasImpagas = rs.getInt("cuotas_impagas");

	            // Recuperar la información del préstamo y el cliente usando los DAOs
	            PrestamoBean prestamo = new DAOPrestamoImpl(this.conexion).recuperarPrestamo(nroPrestamo);
	            ClienteBean cliente = new DAOClienteImpl(this.conexion).recuperarCliente(nroCliente);

	            // Crear un bean ClienteMorosoBeanImpl y asignar los datos
	            ClienteMorosoBean moroso = new ClienteMorosoBeanImpl();
	            moroso.setCliente(cliente);
	            moroso.setPrestamo(prestamo);
	            moroso.setCantidadCuotasAtrasadas(cuotasImpagas);

	            // Agregar el cliente moroso a la lista
	            morosos.add(moroso);
	        }

	    } catch (SQLException ex) {
	        logger.error("Error al recuperar los clientes morosos: {}", ex.getMessage());
	        throw new Exception("Error al recuperar los clientes morosos.");
	    }

	    return morosos;
	}

}

