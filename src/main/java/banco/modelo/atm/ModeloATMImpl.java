package banco.modelo.atm;

import java.io.FileInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import banco.utils.Fechas;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import banco.modelo.ModeloImpl;
import banco.utils.Fechas;


public class ModeloATMImpl extends ModeloImpl implements ModeloATM {
	
	private static Logger logger = LoggerFactory.getLogger(ModeloATMImpl.class);	

	private String tarjeta = null;   // mantiene la tarjeta del cliente actual
	private Integer codigoATM = null;
	
	/*
	 * La información del cajero ATM se recupera del archivo que se encuentra definido en ModeloATM.CONFIG
	 */
	public ModeloATMImpl() {
		logger.debug("Se crea el modelo ATM.");

		logger.debug("Recuperación de la información sobre el cajero");
		
		Properties prop = new Properties();
		try (FileInputStream file = new FileInputStream(ModeloATM.CONFIG))
		{
			logger.debug("Se intenta leer el archivo de propiedades {}",ModeloATM.CONFIG);
			prop.load(file);

			codigoATM = Integer.valueOf(prop.getProperty("atm.codigo.cajero"));

			logger.debug("Código cajero ATM: {}", codigoATM);
		}
		catch(Exception ex)
		{
        	logger.error("Se produjo un error al recuperar el archivo de propiedades {}.",ModeloATM.CONFIG); 
		}
		return;
	}
	
	@Override
	public boolean autenticarUsuarioAplicacion(String Tarjeta, String pin) {
	    PreparedStatement consulta = null;
	    ResultSet resultado = null;
	    
	    try {
	        // Consulta SQL para verificar el número de tarjeta y el PIN
	        String sql = "SELECT nro_tarjeta FROM tarjeta WHERE nro_tarjeta = "+Tarjeta+" AND PIN =MD5("+pin+")";
	        // Ejecutar la consulta
	        resultado = consulta(sql);
	        // Si hay un resultado, la autenticación fue exitosa
	        if (resultado.next()) {
	        	tarjeta = Tarjeta;
	            return true;
	        } else {
	            // Si no hay coincidencia, la autenticación falla
	            return false;
	        }
	        
	    } catch (SQLException e) {
	        e.printStackTrace();
	        return false;
	    } finally {
	        // Cerrar los recursos
	        try {
	            if (resultado != null) resultado.close();
	            if (consulta != null) consulta.close();
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	    }
	}

	
	
	@Override
	public Double obtenerSaldo() throws Exception
	{
	    logger.info("Se intenta obtener el saldo de cliente {}", 3);


	    PreparedStatement consulta = null;
	    ResultSet resultado = null;
	    Double saldo = null;  // Variable para almacenar el saldo
	    
	    try {
	        
	        // Consulta SQL para obtener el saldo
	        String sql = "SELECT tr.saldo from trans_cajas_ahorro as tr join tarjeta as t on t.nro_ca = tr.nro_ca where t.nro_tarjeta = "+this.tarjeta+" "; 
	        
	        
	        
	        // Ejecutar la consulta
	        resultado = consulta(sql);
	        // Si hay un resultado, obtener el saldo
	        if (resultado.next()) {
	            saldo = resultado.getDouble("saldo");  // Extraer el saldo
	        } else {
	            throw new Exception("No se encontró el saldo para el cliente.");
	        }
	        
	    } catch (SQLException e) {
	        e.printStackTrace();
	        throw new Exception("Error al obtener el saldo.");
	        
	    } finally {
	        // Cerrar los recursos
	        try {
	            if (resultado != null) resultado.close();
	            if (consulta != null) consulta.close();
	            
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	    }
	    
	    return saldo;  // Retornar el saldo
	}

	@Override
	public ArrayList<TransaccionCajaAhorroBean> cargarUltimosMovimientos() throws Exception {
		return this.cargarUltimosMovimientos(ModeloATM.ULTIMOS_MOVIMIENTOS_CANTIDAD);
	}	
	
	@Override
	public ArrayList<TransaccionCajaAhorroBean> cargarUltimosMovimientos(int cantidad) throws Exception {
	    logger.info("Busca las ultimas {} transacciones en la BD de la tarjeta", cantidad);

	    ArrayList<TransaccionCajaAhorroBean> lista = new ArrayList<>();
	    

	    PreparedStatement consulta = null;
	    ResultSet resultado = null;
	    try {
	        // Consulta SQL para obtener los últimos movimientos
	        String sql = "SELECT tr.fecha, tr.hora, tr.tipo, tr.monto, tr.cod_caja, tr.destino FROM tarjeta as t left join trans_cajas_ahorro as tr on t.nro_ca = tr.nro_ca where t.nro_tarjeta = "+tarjeta+" " +
	                     "ORDER BY tr.fecha DESC, tr.hora DESC " +
	                     "LIMIT "+cantidad+" ";
	        
	        // Ejecutar la consulta
	        resultado = consulta(sql);
	        
	        // Procesar el resultado
	        String tipo;
	        while (resultado.next()) {
	            TransaccionCajaAhorroBean transaccion = new TransaccionCajaAhorroBeanImpl();
	            transaccion.setTransaccionFechaHora(Fechas.convertirStringADate(resultado.getString("fecha"),resultado.getString("hora")));
	            transaccion.setTransaccionTipo(resultado.getString("tipo"));
	             tipo =resultado.getString("tipo");
	            // Verificar si la transacción disminuye el saldo para mostrar el monto negativo
	            double monto = resultado.getDouble("monto");
	            if (tipo.equals("extraccion") || tipo.equals("transferencia") || tipo.equals("debito")) {
	                monto = -monto;
	            }
	            transaccion.setTransaccionMonto(monto);

	            transaccion.setTransaccionCodigoCaja(resultado.getInt("cod_caja"));
	            transaccion.setCajaAhorroDestinoNumero(resultado.getInt("destino"));

	            lista.add(transaccion);
	        }

	    } catch (SQLException e) {
	        throw new Exception("Error al cargar los últimos movimientos de la cuenta", e);
	        
	    } finally {
	        // Cerrar los recursos
	        try {
	            if (resultado != null) resultado.close();
	            if (consulta != null) consulta.close();
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	    }

	    return lista;  // Retornar la lista de transacciones
	}

	
	@Override
	public ArrayList<TransaccionCajaAhorroBean> cargarMovimientosPorPeriodo(Date desde, Date hasta)
			throws Exception {

		if (desde == null) {
			throw new Exception("El inicio del período no puede estar vacío");
		}
		if (hasta == null) {
			throw new Exception("El fin del período no puede estar vacío");
		}
		if (desde.after(hasta)) {
			throw new Exception("El inicio del período no puede ser posterior al fin del período");
		}	
		
		Date fechaActual = new Date();
		if (desde.after(fechaActual)) {
			throw new Exception("El inicio del período no puede ser posterior a la fecha actual");
		}	
		if (hasta.after(fechaActual)) {
			throw new Exception("El fin del período no puede ser posterior a la fecha actual");
		}				

	    PreparedStatement consulta = null;
	    ResultSet resultado = null;
	    ArrayList<TransaccionCajaAhorroBean> lista = new ArrayList<>();
	    java.sql.Date hastasql = banco.utils.Fechas.convertirDateADateSQL(hasta);
	    java.sql.Date desdesql = banco.utils.Fechas.convertirDateADateSQL(desde);
	    try {
	        // Consulta SQL para obtener los últimos movimientos
	        String sql = "SELECT tr.fecha, tr.hora, tr.tipo, tr.monto, tr.cod_caja, tr.destino FROM tarjeta as t left join trans_cajas_ahorro as tr on t.nro_ca = tr.nro_ca where t.nro_tarjeta = "+tarjeta+" and tr.fecha >= '"+desdesql+"' and tr.fecha <= '"+hastasql+"' " +
	                     "ORDER BY tr.fecha DESC, tr.hora DESC ";
	        
	        // Ejecutar la consulta
	        resultado = consulta(sql);
	        
	        // Procesar el resultado
	        String tipo;
	        while (resultado.next()) {
	            TransaccionCajaAhorroBean transaccion = new TransaccionCajaAhorroBeanImpl();
	            transaccion.setTransaccionFechaHora(Fechas.convertirStringADate(resultado.getString("fecha"),resultado.getString("hora")));
	            transaccion.setTransaccionTipo(resultado.getString("tipo"));
	             tipo =resultado.getString("tipo");
	            // Verificar si la transacción disminuye el saldo para mostrar el monto negativo
	            double monto = resultado.getDouble("monto");
	            if (tipo.equals("extraccion") || tipo.equals("transferencia") || tipo.equals("debito")) {
	                monto = -monto;
	            }
	            transaccion.setTransaccionMonto(monto);

	            transaccion.setTransaccionCodigoCaja(resultado.getInt("cod_caja"));
	            transaccion.setCajaAhorroDestinoNumero(resultado.getInt("destino"));

	            lista.add(transaccion);
	        }

	    } catch (SQLException e) {
	        throw new Exception("Error al cargar los movimiento por periodo de la cuenta", e);
	        
	    } finally {
	        // Cerrar los recursos
	        try {
	            if (resultado != null) resultado.close();
	            if (consulta != null) consulta.close();
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	    }

	    return lista;  // Retornar la lista de transacciones
	}
	
	@Override
	public Double extraer(Double monto) throws Exception {
	    logger.info("Realiza la extracción de ${} sobre la cuenta", monto);

	    if (this.codigoATM == null) {
	        throw new Exception("Hubo un error al recuperar la información sobre el ATM.");
	    }
	    if (this.tarjeta == null) {
	        throw new Exception("Hubo un error al recuperar la información sobre la tarjeta del cliente.");
	    }

	    CallableStatement procedimiento = null;
	    try {
	        // Llamar al procedimiento almacenado `ProcesoExtraer` con un parámetro de salida
	        String sql = "{CALL ProcesoExtraer(?, ?, ?, ?)}"; // Agregar el parámetro de salida
	        procedimiento = conexion.prepareCall(sql);

	        // Establecer los parámetros de entrada
	        procedimiento.setString(1, this.tarjeta); // Número de tarjeta
	        procedimiento.setDouble(2, monto);        // Monto a extraer
	        procedimiento.setInt(3, this.codigoATM); // Código del ATM

	        // Registrar el parámetro de salida
	        procedimiento.registerOutParameter(4, java.sql.Types.VARCHAR); // Saldo actualizado

	        // Ejecutar el procedimiento
	        procedimiento.execute();
	        
	        // Obtener el saldo actualizado del parámetro de salida
	        String respuesta = procedimiento.getString(4);
	        if (respuesta.equals("La extraccion se realizo con exito")) {
	        	return this.obtenerSaldo();
	        }else {
	        	 throw new Exception(respuesta);
	        }
	        

	    } catch (SQLException e) {
	        // Capturar SQLException y lanzar una Exception amigable
	        throw new Exception("Error al realizar la extracción en la base de datos.", e);
	    	//e.printStackTrace();
	    } finally {
	        // Cerrar los recursos
	        try {
	            if (procedimiento != null) procedimiento.close();
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	    }
	}

	
	@Override
	public int parseCuenta(String p_cuenta) throws Exception {

	    logger.info("Intenta realizar el parsing de un código de cuenta {}", p_cuenta);

	    // Verificación si el código de cuenta es nulo
	    if (p_cuenta == null) {
	        throw new Exception("El código de la cuenta no puede ser vacío");
	    }

	    try {
	        // Convertir el código de cuenta a entero
	        int codigoCuenta = Integer.parseInt(p_cuenta);
	        // Conexión a la base de datos
	        PreparedStatement stmt = null;
	        ResultSet rs = null;

	        try {
	            // Establecer la conexión (ajusta la URL, usuario y contraseña)
	            
	        	
	            // Consulta SQL para verificar si la cuenta existe
	            String sql = "SELECT COUNT(*) FROM trans_cajas_ahorro WHERE nro_ca = ?";
	            stmt = conexion.prepareStatement(sql);
	            stmt.setInt(1, codigoCuenta);

	            // Ejecutar la consulta
	            rs = stmt.executeQuery();

	            // Verificar si la cuenta existe (COUNT debe ser mayor que 0)
	            if (rs.next()) {
	                int count = rs.getInt(1);
	                if (count == 0) {
	                    throw new Exception("La cuenta no existe en la base de datos: " + codigoCuenta);
	                }
	            }

	        } finally {
	            // Cerrar los recursos (result set, statement, conexión)
	            if (rs != null) rs.close();
	            if (stmt != null) stmt.close();
	        }

	        // Si todo está bien, retornar el código de cuenta
	        logger.info("Encontró la cuenta en la BD.");
	        return codigoCuenta;

	    } catch (NumberFormatException e) {
	        // Capturar la excepción si el código no se puede convertir a entero
	        throw new Exception("El código de la cuenta no tiene un formato válido: " + p_cuenta);
	    } catch (SQLException e) {
	        // Capturar la excepción SQLException y propagar una excepción más amigable
	        throw new Exception("Error al consultar la base de datos para el código: " + p_cuenta, e);
	    }
	}
	
	
	@Override
	public Double transferir(Double monto, int cajaDestino) throws Exception {
	    logger.info("Intentando realizar una transferencia de ${} a la cuenta {}", monto, cajaDestino);

	    if (this.codigoATM == null) {
	        throw new Exception("Hubo un error al recuperar la información sobre el ATM.");
	    }
	    if (this.tarjeta == null) {
	        throw new Exception("Hubo un error al recuperar la información sobre la tarjeta del cliente.");
	    }

	    CallableStatement procedimiento = null;
	    try {
	        // Llamar al procedimiento almacenado `ProcesoTransferencia` con parámetros de entrada y salida
	        String sql = "{CALL ProcesoTransferir(?, ?, ?, ?, ?)}"; // Cambia según los parámetros necesarios
	        procedimiento = conexion.prepareCall(sql);

	        // Establecer los parámetros de entrada
	        
	        procedimiento.setInt(1, cajaDestino); // Número de cuenta de destino
	        procedimiento.setDouble(2, monto);         // Monto de la transferencia
	        procedimiento.setString(3, this.tarjeta);  // Número de tarjeta de origen
	        procedimiento.setInt(4, this.codigoATM);   // Código del ATM
	        
	        // Registrar el parámetro de salida (por ejemplo, un mensaje o saldo actualizado)
	        procedimiento.registerOutParameter(5, java.sql.Types.VARCHAR); 

	        // Ejecutar el procedimiento
	        procedimiento.execute();

	        // Obtener el mensaje del parámetro de salida
	        String respuesta = procedimiento.getString(5);
	        if (respuesta.equals("La transferencia se realizo con exito")) {
	            logger.info("Transferencia realizada con éxito");
	            return this.obtenerSaldo();
	        } else {
	            throw new Exception(respuesta); // Lanza una excepción si hay un error
	        }

	    } catch (SQLException e) {
	        throw new Exception("Error al realizar la transferencia en la base de datos.", e);
	    } finally {
	        // Cerrar el recurso CallableStatement
	        try {
	            if (procedimiento != null) procedimiento.close();
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	    }
	}


	@Override
	public Double parseMonto(String p_monto) throws Exception {
		
		logger.info("Intenta realizar el parsing del monto {}", p_monto);
		
		if (p_monto == null) {
			throw new Exception("El monto no puede estar vacío");
		}

		try 
		{
			double monto = Double.parseDouble(p_monto);
			DecimalFormat df = new DecimalFormat("#.00");

			monto = Double.parseDouble(corregirComa(df.format(monto)));
			
			if(monto < 0)
			{
				throw new Exception("El monto no debe ser negativo.");
			}
			
			return monto;
		}		
		catch (NumberFormatException e)
		{
			throw new Exception("El monto no tiene un formato válido.");
		}	
	}

	private String corregirComa(String n)
	{
		String toReturn = "";
		
		for(int i = 0;i<n.length();i++)
		{
			if(n.charAt(i)==',')
			{
				toReturn = toReturn + ".";
			}
			else
			{
				toReturn = toReturn+n.charAt(i);
			}
		}
		
		return toReturn;
	}	
}
