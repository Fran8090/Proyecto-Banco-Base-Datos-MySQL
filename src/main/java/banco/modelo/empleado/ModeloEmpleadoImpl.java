package banco.modelo.empleado;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import banco.modelo.ModeloImpl;
import banco.modelo.empleado.beans.ClienteBean;
import banco.modelo.empleado.beans.ClienteMorosoBean;
import banco.modelo.empleado.beans.DAOCliente;
import banco.modelo.empleado.beans.DAOClienteImpl;
import banco.modelo.empleado.beans.DAOClienteMoroso;
import banco.modelo.empleado.beans.DAOClienteMorosoImpl;
import banco.modelo.empleado.beans.DAOEmpleado;
import banco.modelo.empleado.beans.DAOEmpleadoImpl;
import banco.modelo.empleado.beans.DAOPago;
import banco.modelo.empleado.beans.DAOPagoImpl;
import banco.modelo.empleado.beans.DAOPrestamo;
import banco.modelo.empleado.beans.DAOPrestamoImpl;
import banco.modelo.empleado.beans.EmpleadoBean;
import banco.modelo.empleado.beans.PagoBean;
import banco.modelo.empleado.beans.PrestamoBean;

public class ModeloEmpleadoImpl extends ModeloImpl implements ModeloEmpleado {

	private static Logger logger = LoggerFactory.getLogger(ModeloEmpleadoImpl.class);	

	// Indica el usuario actualmente logueado. Null corresponde que todavia no se ha autenticado
	private Integer legajo = null;
	
	public ModeloEmpleadoImpl() {
		logger.debug("Se crea el modelo Empleado.");
	}
	

	@Override
	public boolean autenticarUsuarioAplicacion(String legajo, String password) throws Exception {
	    logger.info("Se intenta autenticar el legajo {} con password {}", legajo, password);

	    Integer legajoInt = null;
	    try {
	        legajoInt = Integer.valueOf(legajo.trim());
	    } catch (Exception ex) {
	        throw new Exception("Se esperaba que el legajo sea un valor entero.");
	    }

	    // Construimos la consulta SQL con los valores concatenados
	    String sql = "SELECT legajo FROM empleado WHERE legajo = " + legajoInt + " AND password = MD5('" + password + "')";

	    // Ejecutamos la consulta y validamos
	    try {
	        ResultSet rs = consulta(sql); // Usamos la consulta ya concatenada

	        if (rs.next()) {
	            int resultadoLegajo = rs.getInt("legajo");

	            // Verificamos si el legajo coincide con el que se proporcionó
	            if (resultadoLegajo == legajoInt) {
	                this.legajo = resultadoLegajo; // Autenticación exitosa
	                return true;
	            } else {
	                return false; // El legajo no coincide
	            }
	        } else {
	            return false; // No se encontró ningún registro
	        }
	    } catch (SQLException ex) {
	        logger.error("Error SQL: {}", ex.getMessage());
	        throw new Exception("Error al autenticar el usuario en la base de datos.");
	    }
	}
	
	@Override
	public EmpleadoBean obtenerEmpleadoLogueado() throws Exception {
		logger.info("Solicita al DAO un empleado con legajo {}", this.legajo);
		if (this.legajo == null) {
			logger.info("No hay un empleado logueado.");
			throw new Exception("No hay un empleado logueado. La sesión terminó.");
		}
		
		DAOEmpleado dao = new DAOEmpleadoImpl(this.conexion);
		return dao.recuperarEmpleado(this.legajo);
	}	
	
	@Override
	public ArrayList<String> obtenerTiposDocumento() throws Exception {
	    logger.info("Recupera los tipos de documentos de los clientes.");

	    ArrayList<String> tipos = new ArrayList<>();

	    // Consulta SQL para obtener los tipos de documentos únicos de los clientes
	    String sql = "SELECT DISTINCT tipo_doc FROM cliente"; 

	    try {
	        ResultSet rs = consulta(sql); // Ejecuta la consulta

	        // Itera sobre los resultados y agrega los tipos a la lista
	        while (rs.next()) {
	            String tipoDocumento = rs.getString("tipo_doc");
	            tipos.add(tipoDocumento);
	        }
	    } catch (SQLException ex) {
	        logger.error("Error al obtener los tipos de documentos: {}", ex.getMessage());
	        throw new Exception("Error al recuperar los tipos de documentos.");
	    }

	    // Retorna la lista de tipos de documentos
	    return tipos;
	}

	@Override
	public double obtenerTasa(double monto, int cantidadMeses) throws Exception {
	    logger.info("Busca la tasa correspondiente al monto {} con una cantidad de meses {}", monto, cantidadMeses);

	    // La consulta SQL busca la tasa según el rango de monto y cantidad de meses
	    String sql = "SELECT tasa FROM tasa_prestamo WHERE "+monto+" BETWEEN monto_inf AND monto_sup AND periodo = " + cantidadMeses;

	    try {

	        ResultSet rs = consulta(sql);

	        if (rs.next()) {
	            double tasa = rs.getDouble("tasa");
	            return tasa; // Retorna la tasa obtenida
	        } else {
	            throw new Exception("No se encontró una tasa para el monto y la cantidad de meses especificados.");
	        }
	    } catch (SQLException ex) {
	        logger.error("Error al obtener la tasa: {}", ex.getMessage());
	        throw new Exception("Error al buscar la tasa en la base de datos.");
	    }
	}

	@Override
	public double obtenerInteres(double monto, double tasa, int cantidadMeses) {
		return (monto * tasa * cantidadMeses) / 1200;
	}


	@Override
	public double obtenerValorCuota(double monto, double interes, int cantidadMeses) {
		return (monto + interes) / cantidadMeses;
	}
		

	@Override
	public ClienteBean recuperarCliente(String tipoDoc, int nroDoc) throws Exception {
		DAOCliente dao = new DAOClienteImpl(this.conexion);
		return dao.recuperarCliente(tipoDoc, nroDoc);
	}


	@Override
	public ArrayList<Integer> obtenerCantidadMeses(double monto) throws Exception {
	    logger.info("Recupera los períodos (cantidad de meses) según el monto {} para el prestamo.", monto);

	    ArrayList<Integer> cantMeses = new ArrayList<>();
	    
	    // Consulta SQL para obtener los períodos disponibles según el monto
	    String sql = "SELECT DISTINCT periodo FROM tasa_prestamo " +
	                 "WHERE "+monto+" BETWEEN monto_inf AND monto_sup"; // Ajusta el nombre de las columnas si es necesario

	    try {

	        ResultSet rs = consulta(sql);

	        // Itera sobre los resultados y agrega la cantidad de meses a la lista
	        while (rs.next()) {
	            int meses = rs.getInt("periodo");
	            cantMeses.add(meses);
	        }

	        if (cantMeses.isEmpty()) {
	            throw new Exception("No se encontraron períodos para el monto especificado.");
	        }

	    } catch (SQLException ex) {
	        logger.error("Error al obtener los períodos de meses: {}", ex.getMessage());
	        throw new Exception("Error al recuperar los períodos de meses.");
	    }

	    return cantMeses;
	}

		
	@Override
	public Integer prestamoVigente(int nroCliente) throws Exception {
	    logger.info("Verifica si el cliente {} tiene algún préstamo con cuotas por pagar.", nroCliente);

	    Integer nroPrestamo = null;

	    // Consulta SQL para verificar si el cliente tiene cuotas sin pagar
	    String sql = "SELECT p.nro_prestamo " +
	                 "FROM prestamo p " +
	                 "JOIN pago c ON p.nro_prestamo = c.nro_prestamo " +
	                 "WHERE p.nro_cliente = "+nroCliente+" AND c.fecha_pago IS NULL " +
	                 "LIMIT 1"; // Solo necesitamos saber si hay un préstamo con cuotas sin pagar

	    try {

	        ResultSet rs = consulta(sql);

	        // Si encuentra un préstamo con cuotas sin pagar, asignamos el nro_prestamo
	        if (rs.next()) {
	            nroPrestamo = rs.getInt("nro_prestamo");
	        }

	    } catch (SQLException ex) {
	        logger.error("Error al verificar los préstamos vigentes del cliente: {}", ex.getMessage());
	        throw new Exception("Error al verificar los préstamos vigentes del cliente.");
	    }

	    return nroPrestamo; // Si no encuentra ningún préstamo vigente, devolverá null
	}


	@Override
	public void crearPrestamo(PrestamoBean prestamo) throws Exception {
		logger.info("Crea un nuevo prestamo.");
		
		if (this.legajo == null) {
			throw new Exception("No hay un empleado registrado en el sistema que se haga responsable por este prestamo.");
		}
		else 
		{
			logger.info("Actualiza el prestamo con el legajo {}",this.legajo);
			prestamo.setLegajo(this.legajo);
			
			DAOPrestamo dao = new DAOPrestamoImpl(this.conexion);		
			dao.crearPrestamo(prestamo);
		}
	}
	
	@Override
	public PrestamoBean recuperarPrestamo(int nroPrestamo) throws Exception {
		logger.info("Busca el prestamo número {}", nroPrestamo);
		
		DAOPrestamo dao = new DAOPrestamoImpl(this.conexion);		
		return dao.recuperarPrestamo(nroPrestamo);
	}
	
	@Override
	public ArrayList<PagoBean> recuperarPagos(Integer prestamo) throws Exception {
		logger.info("Solicita la busqueda de pagos al modelo sobre el prestamo {}.", prestamo);
		
		DAOPago dao = new DAOPagoImpl(this.conexion);		
		return dao.recuperarPagos(prestamo);
	}
	

	@Override
	public void pagarCuotas(String p_tipo, int p_dni, int nroPrestamo, List<Integer> cuotasAPagar) throws Exception {
		
		// Valida que sea un cliente que exista sino genera una excepción
		ClienteBean c = this.recuperarCliente(p_tipo.trim(), p_dni);

		// Valida el prestamo
		if (nroPrestamo != this.prestamoVigente(c.getNroCliente())) {
			throw new Exception ("El nro del prestamo no coincide con un prestamo vigente del cliente");
		}

		if (cuotasAPagar.size() == 0) {
			throw new Exception ("Debe seleccionar al menos una cuota a pagar.");
		}
		
		DAOPago dao = new DAOPagoImpl(this.conexion);
		dao.registrarPagos(nroPrestamo, cuotasAPagar);		
	}


	@Override
	public ArrayList<ClienteMorosoBean> recuperarClientesMorosos() throws Exception {
		logger.info("Modelo solicita al DAO que busque los clientes morosos");
		DAOClienteMoroso dao = new DAOClienteMorosoImpl(this.conexion);
		return dao.recuperarClientesMorosos();	
	}
	

	
}
