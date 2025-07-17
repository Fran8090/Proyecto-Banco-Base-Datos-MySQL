CREATE DATABASE banco;
USE banco;

-- Tabla: ciudad
CREATE TABLE IF NOT EXISTS ciudad (
    cod_postal INT UNSIGNED PRIMARY KEY ,
    nombre VARCHAR(50) NOT NULL
);

-- Tabla: sucursal
CREATE TABLE IF NOT EXISTS sucursal (
    nro_suc INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL,
    direccion VARCHAR(50) NOT NULL,
    telefono VARCHAR(50) NOT NULL,
    horario VARCHAR(50) NOT NULL,
    cod_postal INT UNSIGNED NOT NULL,
    FOREIGN KEY (cod_postal) REFERENCES ciudad(cod_postal)
);

-- Tabla: empleado
CREATE TABLE IF NOT EXISTS empleado (
    legajo INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    apellido VARCHAR(50) NOT NULL,
    nombre VARCHAR(50) NOT NULL,
    tipo_doc VARCHAR(20) NOT NULL,
    nro_doc INT UNSIGNED NOT NULL,
    direccion VARCHAR(50) NOT NULL,
    telefono VARCHAR(50) NOT NULL,
    cargo VARCHAR(50) NOT NULL,
    nro_suc INT UNSIGNED NOT NULL,
    FOREIGN KEY (nro_suc) REFERENCES sucursal(nro_suc),
    password CHAR(32) NOT NULL
);

-- Tabla: cliente
CREATE TABLE IF NOT EXISTS cliente (
    nro_cliente INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    apellido VARCHAR(50) NOT NULL,
    nombre VARCHAR(50) NOT NULL,
    tipo_doc VARCHAR(20) NOT NULL,
    nro_doc INT UNSIGNED NOT NULL,
    direccion VARCHAR(50) NOT NULL,
    telefono VARCHAR(50) NOT NULL,
    fecha_nac DATE NOT NULL
);

-- Tabla: plazo_fijo
CREATE TABLE IF NOT EXISTS plazo_fijo (
    nro_plazo INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    capital DECIMAL(16,2) UNSIGNED NOT NULL,
    fecha_inicio DATE NOT NULL,
    fecha_fin DATE NOT NULL,
    tasa_interes DECIMAL(4,2) UNSIGNED NOT NULL,
    interes DECIMAL(16,2) UNSIGNED NOT NULL,
    nro_suc INT UNSIGNED NOT NULL,
    FOREIGN KEY (nro_suc) REFERENCES sucursal(nro_suc)
);

-- Tabla: tasa_plazo_fijo
CREATE TABLE tasa_plazo_fijo (
    periodo INT UNSIGNED,
    monto_inf DECIMAL(16,2) UNSIGNED,
    monto_sup DECIMAL(16,2) UNSIGNED,
    primary key (periodo,monto_inf,monto_sup),
    tasa DECIMAL(4,2) UNSIGNED NOT NULL
);

-- Tabla: plazo_cliente
CREATE TABLE IF NOT EXISTS plazo_cliente (
    nro_plazo INT UNSIGNED,
    nro_cliente INT UNSIGNED,
    PRIMARY KEY (nro_plazo, nro_cliente),
    FOREIGN KEY (nro_plazo) REFERENCES plazo_fijo(nro_plazo),
    FOREIGN KEY (nro_cliente) REFERENCES cliente(nro_cliente)
);

-- Tabla: prestamo
CREATE TABLE IF NOT EXISTS prestamo (
    nro_prestamo INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    fecha DATE NOT NULL,
    cant_meses INT UNSIGNED NOT NULL,
    monto DECIMAL(10,2) UNSIGNED NOT NULL,
    tasa_interes DECIMAL(4,2) UNSIGNED NOT NULL,
    interes DECIMAL(9,2) UNSIGNED NOT NULL,
    valor_cuota DECIMAL(9,2) UNSIGNED NOT NULL,
    legajo INT UNSIGNED NOT NULL,
    FOREIGN KEY (legajo) REFERENCES empleado(legajo),
    nro_cliente INT UNSIGNED NOT NULL,
    FOREIGN KEY (nro_cliente) REFERENCES cliente(nro_cliente)
);

-- Tabla: pago
CREATE TABLE IF NOT EXISTS pago (
    nro_pago INT UNSIGNED AUTO_INCREMENT,
    nro_prestamo INT UNSIGNED NOT NULL,
    fecha_venc DATE NOT NULL,
    fecha_pago DATE NULL,
    PRIMARY key (nro_pago,nro_prestamo),
    FOREIGN KEY (nro_prestamo) REFERENCES prestamo(nro_prestamo)
);

-- Tabla: tasa_prestamo
CREATE TABLE IF NOT EXISTS tasa_prestamo (
    periodo INT UNSIGNED,
    monto_inf DECIMAL(10,2) UNSIGNED,
    monto_sup DECIMAL(10,2) UNSIGNED,
    PRIMARY key (periodo,monto_inf,monto_sup),
    tasa DECIMAL(4,2) UNSIGNED NOT NULL
);

-- Tabla: caja_ahorro
CREATE TABLE IF NOT EXISTS caja_ahorro (
    nro_ca INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    CBU BIGINT UNSIGNED NOT NULL,
    saldo DECIMAL(16,2) UNSIGNED NOT NULL
);

-- Tabla: cliente_ca
CREATE TABLE IF NOT EXISTS cliente_ca (
    nro_cliente INT UNSIGNED,
    nro_ca INT UNSIGNED,
    PRIMARY KEY(nro_cliente, nro_ca),
    FOREIGN KEY (nro_cliente) REFERENCES cliente(nro_cliente),
    FOREIGN KEY (nro_ca) REFERENCES caja_ahorro(nro_ca)
);

-- Tabla: tarjeta
CREATE TABLE IF NOT EXISTS tarjeta (
    nro_tarjeta BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    PIN CHAR(32) NOT NULL,
    CVT CHAR(32) NOT NULL,
    fecha_venc DATE NOT NULL,
    nro_cliente INT UNSIGNED NOT NULL,
    nro_ca INT UNSIGNED NOT NULL,
    FOREIGN KEY (nro_cliente, nro_ca) REFERENCES cliente_ca(nro_cliente, nro_ca)
);

-- Tabla: caja
CREATE TABLE IF NOT EXISTS caja (
    cod_caja INT UNSIGNED AUTO_INCREMENT PRIMARY KEY
);

-- Tabla: ventanilla
CREATE TABLE IF NOT EXISTS ventanilla (
    cod_caja INT UNSIGNED,
    nro_suc INT UNSIGNED NOT NULL,
    PRIMARY KEY (cod_caja),
    FOREIGN KEY (cod_caja) REFERENCES caja(cod_caja),
    FOREIGN KEY (nro_suc) REFERENCES sucursal(nro_suc)
);

-- Tabla: atm
CREATE TABLE IF NOT EXISTS atm (
    cod_caja INT UNSIGNED PRIMARY KEY,
    cod_postal INT UNSIGNED NOT NULL,
    direccion VARCHAR(100) NOT NULL,
    FOREIGN KEY (cod_postal) REFERENCES ciudad(cod_postal),
    FOREIGN KEY (cod_caja) REFERENCES caja(cod_caja)
);

-- Tabla: transaccion
CREATE TABLE IF NOT EXISTS transaccion (
    nro_trans BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    fecha DATE NOT NULL,
    hora TIME NOT NULL,
    monto DECIMAL(16,2) UNSIGNED NOT NULL
);

-- Tabla: transaccion_por_caja
CREATE TABLE IF NOT EXISTS transaccion_por_caja (
    nro_trans BIGINT UNSIGNED,
    cod_caja INT UNSIGNED NOT NULL,
    PRIMARY KEY (nro_trans),
    FOREIGN KEY (nro_trans) REFERENCES transaccion(nro_trans),
    FOREIGN KEY (cod_caja) REFERENCES caja(cod_caja)
);

-- Tabla: debito
CREATE TABLE IF NOT EXISTS debito (
    nro_trans BIGINT UNSIGNED,
    descripcion TEXT,
    nro_cliente INT UNSIGNED NOT NULL,
    nro_ca INT UNSIGNED NOT NULL,
    PRIMARY KEY (nro_trans),
    FOREIGN KEY (nro_trans) REFERENCES transaccion(nro_trans),
    FOREIGN KEY (nro_cliente, nro_ca) REFERENCES cliente_ca(nro_cliente, nro_ca)
);

-- Tabla: deposito
CREATE TABLE IF NOT EXISTS deposito (
    nro_trans BIGINT UNSIGNED,
    nro_ca INT UNSIGNED NOT NULL,
    PRIMARY KEY (nro_trans),
    FOREIGN KEY (nro_trans) REFERENCES transaccion_por_caja(nro_trans),
    FOREIGN KEY (nro_ca) REFERENCES caja_ahorro(nro_ca)
);

-- Tabla: extraccion
CREATE TABLE IF NOT EXISTS extraccion (
    nro_trans BIGINT UNSIGNED,
    nro_cliente INT UNSIGNED NOT NULL,
    nro_ca INT UNSIGNED NOT NULL,
    PRIMARY KEY (nro_trans),
    FOREIGN KEY (nro_trans) REFERENCES transaccion_por_caja(nro_trans),
    FOREIGN KEY (nro_cliente, nro_ca) REFERENCES cliente_ca(nro_cliente, nro_ca)
);

-- Tabla: transferencia
CREATE TABLE IF NOT EXISTS transferencia (
    nro_trans BIGINT UNSIGNED,
    nro_cliente INT UNSIGNED NOT NULL,
    origen INT UNSIGNED NOT NULL,
    destino INT UNSIGNED NOT NULL,
    PRIMARY KEY (nro_trans),
    FOREIGN KEY (nro_trans) REFERENCES transaccion_por_caja(nro_trans),
    FOREIGN KEY (nro_cliente, origen) REFERENCES cliente_ca(nro_cliente, nro_ca),
    FOREIGN KEY (destino) REFERENCES caja_ahorro(nro_ca)
);

CREATE OR REPLACE VIEW trans_cajas_ahorro AS
(
    -- Consulta para débitos
    SELECT 
        c.nro_ca AS nro_ca,
        c.saldo AS saldo,
        t.nro_trans AS nro_trans,
        t.fecha AS fecha,
        t.hora AS hora,
        'debito' AS tipo,
        t.monto AS monto,
        NULL AS cod_caja,
        d.nro_cliente AS nro_cliente,
        cl.tipo_doc AS tipo_doc,
        cl.nro_doc AS nro_doc,
        cl.nombre AS nombre,
        cl.apellido AS apellido,
        NULL AS destino
    FROM
        Transaccion t
        JOIN Debito d ON t.nro_trans = d.nro_trans
        JOIN Caja_Ahorro c ON d.nro_ca = c.nro_ca
        JOIN Cliente cl ON d.nro_cliente = cl.nro_cliente
    
    UNION ALL
    
    -- Consulta para depósitos
    SELECT 
        c.nro_ca AS nro_ca,
        c.saldo AS saldo,
        t.nro_trans AS nro_trans,
        t.fecha AS fecha,
        t.hora AS hora,
        'deposito' AS tipo,
        t.monto AS monto,
        tc.cod_caja AS cod_caja,
        NULL AS nro_cliente,
        NULL AS tipo_doc,
        NULL AS nro_doc,
        NULL AS nombre,
        NULL AS apellido,
        NULL AS destino
    FROM
        Transaccion t
        JOIN Deposito dp ON t.nro_trans = dp.nro_trans
        JOIN Caja_Ahorro c ON dp.nro_ca = c.nro_ca
        JOIN transaccion_por_caja tc ON tc.nro_trans = t.nro_trans
    
    UNION ALL
    
    -- Consulta para extracciones
    SELECT 
        c.nro_ca AS nro_ca,
        c.saldo AS saldo,
        t.nro_trans AS nro_trans,
        t.fecha AS fecha,
        t.hora AS hora,
        'extraccion' AS tipo,
        t.monto AS monto,
        tc.cod_caja AS cod_caja,
        e.nro_cliente AS nro_cliente,
        cl.tipo_doc AS tipo_doc,
        cl.nro_doc AS nro_doc,
        cl.nombre AS nombre,
        cl.apellido AS apellido,
        NULL AS destino
    FROM
        Transaccion t
        JOIN Extraccion e ON t.nro_trans = e.nro_trans
        JOIN Caja_Ahorro c ON e.nro_ca = c.nro_ca
        JOIN Cliente cl ON e.nro_cliente = cl.nro_cliente
        JOIN transaccion_por_caja tc ON t.nro_trans = tc.nro_trans
    
    UNION ALL
    
    -- Consulta para transferencias
    SELECT 
        c1.nro_ca AS nro_ca,
        c1.saldo AS saldo,
        t.nro_trans AS nro_trans,
        t.fecha AS fecha,
        t.hora AS hora,
        'transferencia' AS tipo,
        t.monto AS monto,
        tc.cod_caja AS cod_caja,
        tf.nro_cliente AS nro_cliente,
        cl.tipo_doc AS tipo_doc,
        cl.nro_doc AS nro_doc,
        cl.nombre AS nombre,
        cl.apellido AS apellido,
        tf.destino AS destino
    FROM
        Transaccion t
        JOIN Transferencia tf ON t.nro_trans = tf.nro_trans
        JOIN Caja_Ahorro c1 ON tf.origen = c1.nro_ca
        JOIN Caja_Ahorro c2 ON tf.destino = c2.nro_ca
        JOIN Cliente cl ON tf.nro_cliente = cl.nro_cliente
        JOIN transaccion_por_caja tc ON tc.nro_trans = t.nro_trans
);

-- Parte de los usuarios:

-- Crear el usuario 'admin' con la contraseña 'admin'
CREATE USER 'admin'@'localhost' IDENTIFIED BY 'admin';

-- Otorgar todos los privilegios sobre la base de datos 'banco'
GRANT ALL PRIVILEGES ON banco.* TO 'admin'@'localhost';

-- Permitir al usuario 'admin' crear otros usuarios y otorgar privilegios
GRANT CREATE USER, GRANT OPTION ON *.* TO 'admin'@'localhost';

-- Aplicar los cambios
FLUSH PRIVILEGES;

-- Crear el usuario 'empleado' con la contraseña 'empleado'
CREATE USER 'empleado'@'%' IDENTIFIED BY 'empleado';

-- Otorgar permisos de solo lectura sobre las tablas especificadas
GRANT SELECT ON banco.Empleado TO 'empleado'@'%';
GRANT SELECT ON banco.Sucursal TO 'empleado'@'%';
GRANT SELECT ON banco.Tasa_Plazo_Fijo TO 'empleado'@'%';
GRANT SELECT ON banco.Tasa_Prestamo TO 'empleado'@'%';

-- Otorgar permisos de consulta e ingreso de datos
GRANT SELECT, INSERT ON banco.Prestamo TO 'empleado'@'%';
GRANT SELECT, INSERT ON banco.Plazo_Fijo TO 'empleado'@'%';
GRANT SELECT, INSERT ON banco.Plazo_Cliente TO 'empleado'@'%';
GRANT SELECT, INSERT ON banco.Caja_Ahorro TO 'empleado'@'%';
GRANT SELECT, INSERT ON banco.Tarjeta TO 'empleado'@'%';

-- Otorgar permisos de consulta, inserción y modificación de datos
GRANT SELECT, INSERT, UPDATE ON banco.Cliente_CA TO 'empleado'@'%';
GRANT SELECT, INSERT, UPDATE ON banco.Cliente TO 'empleado'@'%';
GRANT SELECT, INSERT, UPDATE ON banco.Pago TO 'empleado'@'%';

-- Aplicar los cambios
FLUSH PRIVILEGES;

-- Crear el usuario 'atm' con la contraseña 'atm'
CREATE USER 'atm'@'%' IDENTIFIED BY 'atm';

-- Otorgar permisos de lectura sobre la vista 'trans_cajas_ahorro'
GRANT SELECT ON banco.trans_cajas_ahorro TO 'atm'@'%';

-- Otorgar permisos de lectura y actualización sobre la tabla 'tarjeta'
GRANT SELECT, UPDATE ON banco.Tarjeta TO 'atm'@'%';

-- Aplicar los cambios
FLUSH PRIVILEGES;

DROP USER ''@'localhost';

delimiter !
CREATE PROCEDURE ProcesoExtraer(IN tarjeta BIGINT(16), IN monto DECIMAL(16, 2),IN num_atm INT(8), OUT resultado VARCHAR(40))
BEGIN
		DECLARE saldo_inicio DECIMAL(16, 2);
		DECLARE cod_atm INT(5);
        DECLARE caja INT(8);
        DECLARE cliente INT(5);
        DECLARE transaccion INT(10);
        DECLARE saldo_final DECIMAL(16, 2);
		
		DECLARE EXIT HANDLER FOR SQLEXCEPTION 
		BEGIN
				ROLLBACK;
				SET resultado = "Error en la operacion";
		END;
		
		START TRANSACTION;
            SELECT saldo, nro_cliente, nro_ca INTO saldo_inicio, cliente, caja FROM Tarjeta NATURAL JOIN Caja_Ahorro WHERE nro_tarjeta=tarjeta FOR UPDATE;
			IF caja IS NULL THEN 
				SET resultado= "Error en numero de tarjeta";
			ELSE
				BEGIN
					SELECT cod_caja INTO cod_atm FROM ATM WHERE cod_caja=num_atm;
					
					IF cod_atm IS NULL THEN
                        SET resultado = "Error en el codigo ATM";
					ELSE
						BEGIN
							IF saldo_inicio<monto THEN
								SET resultado = "Saldo Insuficiente";
							ELSE
								BEGIN
									SET saldo_final = saldo_inicio - monto;
									UPDATE Caja_Ahorro SET saldo=saldo_final WHERE nro_ca=caja;
									INSERT INTO Transaccion(fecha, hora, monto) VALUES (curdate(), curtime(), monto);
									SET transaccion= last_insert_id();
									INSERT INTO Transaccion_por_caja(nro_trans, cod_caja) VALUES (transaccion,cod_atm);
									INSERT INTO Extraccion(nro_trans, nro_cliente, nro_ca) VALUES (transaccion, cliente, caja);
									SET resultado = "La extraccion se realizo con exito";
								END;
							END IF;
						END;
					END IF;
				END;
			END IF;	
		COMMIT;
END; !
CREATE PROCEDURE ProcesoTransferir(IN destino INT(8),IN monto DECIMAL(16, 2),IN tarjeta BIGINT(16),IN numero_atm INT(8),OUT resultado VARCHAR(40))
BEGIN
        DECLARE cod_atm INT(5);
		DECLARE cliente INT(5);
		DECLARE caja_origen INT(8);
		DECLARE caja_destino INT(8);
		DECLARE hora TIME;
		DECLARE fecha DATE;
		DECLARE transferencia INT(10);
		DECLARE deposito INT(10);
        DECLARE saldo_destino DECIMAL(16,2);
        DECLARE saldo_origen DECIMAL(16,2);
		DECLARE saldo_destino_final DECIMAL(16,2);
        DECLARE saldo_origen_final DECIMAL(16,2);
		
		DECLARE EXIT HANDLER FOR SQLEXCEPTION 
		BEGIN
				ROLLBACK;
				SET resultado = "Error en la operacion";
		END;
		
		START TRANSACTION;
		
			SELECT cod_caja INTO cod_atm FROM ATM WHERE cod_caja=numero_atm;
			
			IF cod_atm IS NULL THEN
				SET resultado = "Error en codigo atm";
			ELSE
				BEGIN
					SELECT saldo, nro_cliente, nro_ca INTO saldo_origen, cliente, caja_origen FROM Tarjeta NATURAL JOIN Caja_Ahorro WHERE nro_tarjeta=tarjeta FOR UPDATE;
				
					IF caja_origen IS NULL THEN
						SET resultado = "Error en codigo tarjeta";
					ELSE
						BEGIN
							IF saldo_origen<monto THEN
								SET resultado = "Saldo Insuficiente";
							ELSE
								BEGIN
									SELECT nro_ca, saldo INTO caja_destino, saldo_destino FROM Caja_Ahorro WHERE nro_ca=destino;
									
									IF caja_destino IS NULL THEN
										SET resultado = "Error en caja destino";
									ELSE
										BEGIN
											SET saldo_origen_final = saldo_origen - monto;
											SET saldo_destino_final = saldo_destino + monto;
											UPDATE Caja_Ahorro SET saldo=saldo_origen_final WHERE nro_ca=caja_origen;
											UPDATE Caja_Ahorro SET saldo=saldo_destino_final WHERE nro_ca=caja_destino;
											
                                            
                                            SET hora = curtime();
											SET fecha = curdate();
											INSERT INTO Transaccion(fecha, hora, monto) VALUES (fecha, hora, monto);
											SET transferencia= last_insert_id();
											INSERT INTO Transaccion_por_caja(nro_trans, cod_caja) VALUES (transferencia,cod_atm);
											INSERT INTO Transferencia(nro_trans, nro_cliente, origen, destino) VALUES (transferencia, cliente, caja_origen, caja_destino);
											INSERT INTO Transaccion(fecha, hora, monto) VALUES (fecha, hora, monto);
											SET deposito= last_insert_id();
											INSERT INTO Transaccion_por_caja(nro_trans, cod_caja) VALUES (deposito,cod_atm);
											INSERT INTO Deposito(nro_trans, nro_ca) VALUES (deposito,caja_destino);
											SET resultado = "La transferencia se realizo con exito";
										END;
									END IF;
								END;
							END IF;
						END;
					END IF;
				END;
			END IF;
			
		COMMIT;
END; !
#delimiter ;

GRANT EXECUTE ON PROCEDURE ProcesoExtraer TO atm@'%';
GRANT EXECUTE ON PROCEDURE ProcesoTransferir TO atm@'%';

#delimiter !
CREATE TRIGGER TriggerPagoEnPrestamo AFTER INSERT ON Prestamo FOR EACH ROW
BEGIN
		DECLARE j int UNSIGNED DEFAULT 1;

		WHILE j <= NEW.cant_meses DO

		INSERT INTO Pago(nro_prestamo,nro_pago,fecha_venc,fecha_pago) 
        VALUES (NEW.nro_prestamo,j,DATE_ADD(NEW.fecha, INTERVAL j MONTH),NULL);
		SET j=j+1;

		END WHILE;
END; !
delimiter ;