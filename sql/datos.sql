-- Insertar datos en la tabla ciudad
INSERT INTO ciudad (cod_postal, nombre) VALUES
(1000, 'Ciudad A'),
(2000, 'Ciudad B');

-- Insertar datos en la tabla sucursal
INSERT INTO sucursal (nombre, direccion, telefono, horario, cod_postal) VALUES
('Sucursal 1', 'Calle 1', '123456789', '9:00-18:00', 1000),
('Sucursal 2', 'Calle 2', '987654321', '9:00-18:00', 2000);

INSERT INTO empleado (apellido, nombre, tipo_doc, nro_doc, direccion, telefono, cargo, nro_suc, password) VALUES
('Perez', 'Juan', 'DNI', 12345678, 'Calle 10', '123456789', 'Gerente', 1, MD5('pw1')),
('Gomez', 'Ana', 'DNI', 87654321, 'Calle 20', '987654321', 'Cajero', 2, MD5('pw2'));

-- Insertar datos en la tabla cliente
INSERT INTO cliente (apellido, nombre, tipo_doc, nro_doc, direccion, telefono, fecha_nac) VALUES
('Lopez', 'Carlos', 'DNI', 34567890, 'Calle 30', '123123123', '1980-05-15'),
('Martinez', 'Lucia', 'DNI', 65432109, 'Calle 40', '321321321', '1990-10-10');

-- Insertar datos en la tabla plazo_fijo
INSERT INTO plazo_fijo (capital, fecha_inicio, fecha_fin, tasa_interes, interes, nro_suc) VALUES
(10000, '2024-01-01', '2024-12-31', 2.5, 250, 1),
(5000, '2024-06-01', '2024-11-30', 3.0, 150, 2);

-- Insertar datos en la tabla tasa_plazo_fijo
INSERT INTO tasa_plazo_fijo (periodo, monto_inf, monto_sup, tasa) VALUES
(30, 0, 10000, 1.5),
(60, 10000, 50000, 2.5);

-- Insertar datos en la tabla plazo_cliente
INSERT INTO plazo_cliente (nro_plazo, nro_cliente) VALUES
(1, 1),
(2, 2);

-- Insertar datos en la tabla prestamo
INSERT INTO prestamo (fecha, cant_meses, monto, tasa_interes, interes, valor_cuota, legajo, nro_cliente) VALUES
('2024-02-01', 12, 20000, 3.0, 600, 1720, 1, 1),
('2024-03-01', 24, 30000, 2.5, 750, 1354, 2, 2);

-- Insertar datos en la tabla pago
INSERT INTO pago (nro_prestamo, fecha_venc, fecha_pago) VALUES
(1, '2024-03-01', '2024-03-01'),
(2, '2024-04-01', '2024-04-05');

-- Insertar datos en la tabla tasa_prestamo
INSERT INTO tasa_prestamo (periodo, monto_inf, monto_sup, tasa) VALUES
(12, 0, 20000, 1.5),
(24, 20000, 50000, 2.0);

-- Insertar datos en la tabla caja_ahorro
INSERT INTO caja_ahorro (CBU, saldo) VALUES
(1234567890123456, 5000),
(9876543210987654, 3000);

-- Insertar datos en la tabla cliente_ca
INSERT INTO cliente_ca (nro_cliente, nro_ca) VALUES
(1, 1),
(2, 2);

-- Insertar datos en la tabla tarjeta
INSERT INTO tarjeta (PIN, CVT, fecha_venc, nro_cliente, nro_ca) VALUES
(MD5('1234'), MD5('123'), '2025-12-31', 1, 1),
(MD5('4321'), MD5('321'), '2025-06-30', 2, 2);

-- Insertar datos en la tabla caja
INSERT INTO caja (cod_caja) VALUES (1), (2),(100);

-- Insertar datos en la tabla ventanilla
INSERT INTO ventanilla (cod_caja, nro_suc) VALUES
(1, 1),
(2, 2);

-- Insertar datos en la tabla atm
INSERT INTO atm (cod_caja, cod_postal, direccion) VALUES
(1, 1000, 'Calle ATM 1'),
(100, 1000, 'Calle ATM 100'),
(2, 2000, 'Calle ATM 2');

-- Insertar datos en la tabla transaccion
INSERT INTO transaccion (fecha, hora, monto) VALUES
('2024-09-01', '12:00:00', 1000),
('2024-09-02', '15:30:00', 500);

-- Insertar datos en la tabla transaccion_por_caja
INSERT INTO transaccion_por_caja (nro_trans, cod_caja) VALUES
(1, 1),
(2, 2);

-- Insertar datos en la tabla debito
INSERT INTO debito (nro_trans, descripcion, nro_cliente, nro_ca) VALUES
(1, 'Pago de servicio', 1, 1);

-- Insertar datos en la tabla deposito
INSERT INTO deposito (nro_trans, nro_ca) VALUES
(2, 2);

-- Insertar datos en la tabla extraccion
INSERT INTO extraccion (nro_trans, nro_cliente, nro_ca) VALUES
(1, 1, 1);

-- Insertar datos en la tabla transferencia
INSERT INTO transferencia (nro_trans, nro_cliente, origen, destino) VALUES
(2, 2, 2, 1);

-- Insertar datos adicionales en la tabla ciudad
INSERT INTO ciudad (cod_postal, nombre) VALUES
(3000, 'Ciudad C'),
(4000, 'Ciudad D');

-- Insertar datos adicionales en la tabla sucursal
INSERT INTO sucursal (nombre, direccion, telefono, horario, cod_postal) VALUES
('Sucursal 3', 'Calle 3', '111222333', '9:00-18:00', 3000),
('Sucursal 4', 'Calle 4', '444555666', '9:00-18:00', 4000);

-- Insertar datos adicionales en la tabla empleado
INSERT INTO empleado (apellido, nombre, tipo_doc, nro_doc, direccion, telefono, cargo, nro_suc, password) VALUES
('Fernandez', 'Mario', 'DNI', 23456789, 'Calle 50', '456456456', 'Supervisor', 3, MD5('pw3')),
('Sanchez', 'Laura', 'DNI', 76543210, 'Calle 60', '654654654', 'Cajero', 4, MD5('pw4'));

-- Insertar datos adicionales en la tabla cliente
INSERT INTO cliente (apellido, nombre, tipo_doc, nro_doc, direccion, telefono, fecha_nac) VALUES
('Diaz', 'Miguel', 'DNI', 56789012, 'Calle 70', '789789789', '1985-12-22'),
('Ramirez', 'Julia', 'DNI', 89012345, 'Calle 80', '987987987', '1995-03-30');

-- Insertar datos adicionales en la tabla plazo_fijo
INSERT INTO plazo_fijo (capital, fecha_inicio, fecha_fin, tasa_interes, interes, nro_suc) VALUES
(15000, '2024-07-01', '2025-06-30', 3.5, 525, 3),
(8000, '2024-04-01', '2024-12-31', 2.0, 160, 4);

-- Insertar datos adicionales en la tabla tasa_plazo_fijo
INSERT INTO tasa_plazo_fijo (periodo, monto_inf, monto_sup, tasa) VALUES
(90, 5000, 20000, 2.0),
(180, 20000, 100000, 3.0);

-- Insertar datos adicionales en la tabla plazo_cliente
INSERT INTO plazo_cliente (nro_plazo, nro_cliente) VALUES
(3, 3),
(4, 4);

-- Insertar datos adicionales en la tabla prestamo
INSERT INTO prestamo (fecha, cant_meses, monto, tasa_interes, interes, valor_cuota, legajo, nro_cliente) VALUES
('2024-05-01', 36, 40000, 3.0, 1200, 1150, 3, 3),
('2024-06-01', 18, 25000, 2.0, 500, 1470, 4, 4);

-- Insertar datos adicionales en la tabla pago
INSERT INTO pago (nro_prestamo, fecha_venc, fecha_pago) VALUES
(3, '2024-07-01', '2024-07-01'),
(4, '2024-08-01', '2024-08-05');

-- Insertar datos adicionales en la tabla tasa_prestamo
INSERT INTO tasa_prestamo (periodo, monto_inf, monto_sup, tasa) VALUES
(36, 20000, 50000, 2.5),
(48, 50000, 100000, 3.0);

-- Insertar datos adicionales en la tabla caja_ahorro
INSERT INTO caja_ahorro (CBU, saldo) VALUES
(1111222233334444, 8000),
(4444333322221111, 1500);

-- Insertar datos adicionales en la tabla cliente_ca
INSERT INTO cliente_ca (nro_cliente, nro_ca) VALUES
(3, 3),
(4, 4);

-- Insertar datos adicionales en la tabla tarjeta
INSERT INTO tarjeta (PIN, CVT, fecha_venc, nro_cliente, nro_ca) VALUES
(MD5('5678'), MD5('567'), '2026-03-31', 3, 3),
(MD5('8765'), MD5('765'), '2026-07-31', 4, 4);

-- Insertar datos adicionales en la tabla caja
INSERT INTO caja (cod_caja) VALUES (3), (4);

-- Insertar datos adicionales en la tabla ventanilla
INSERT INTO ventanilla (cod_caja, nro_suc) VALUES
(3, 3),
(4, 4);

-- Insertar datos adicionales en la tabla atm
INSERT INTO atm (cod_caja, cod_postal, direccion) VALUES
(3, 3000, 'Calle ATM 3'),
(4, 4000, 'Calle ATM 4');

-- Insertar datos adicionales en la tabla transaccion
INSERT INTO transaccion (fecha, hora, monto) VALUES
('2024-09-03', '10:00:00', 2000),
('2024-09-04', '14:15:00', 1500);

-- Insertar datos adicionales en la tabla transaccion_por_caja
INSERT INTO transaccion_por_caja (nro_trans, cod_caja) VALUES
(3, 3),
(4, 4);

-- Insertar datos adicionales en la tabla debito
INSERT INTO debito (nro_trans, descripcion, nro_cliente, nro_ca) VALUES
(3, 'Pago de alquiler', 3, 3);

-- Insertar datos adicionales en la tabla deposito
INSERT INTO deposito (nro_trans, nro_ca) VALUES
(4, 4);

-- Insertar datos adicionales en la tabla extraccion
INSERT INTO extraccion (nro_trans, nro_cliente, nro_ca) VALUES
(3, 3, 3);

-- Insertar datos adicionales en la tabla transferencia
INSERT INTO transferencia (nro_trans, nro_cliente, origen, destino) VALUES
(4, 4, 4, 3);

-- Insertar datos en la tabla prestamo con pagos pendientes
INSERT INTO prestamo (fecha, cant_meses, monto, tasa_interes, interes, valor_cuota, legajo, nro_cliente) VALUES
('2024-01-01', 24, 30000, 3.0, 900, 1300, 1, 1),
('2024-01-15', 36, 50000, 2.5, 1250, 1500, 1, 2),
('2024-02-15', 12, 15000, 2.0, 300, 1300, 1, 3);

-- Insertar pagos para estos préstamos, asegurando que tengan fecha_pago NULL
INSERT INTO pago (nro_prestamo, fecha_venc, fecha_pago) VALUES
(3, '2024-07-01', NULL), 
(4, '2024-08-01', NULL), 
(5, '2024-09-01', NULL); 

INSERT INTO pago (nro_prestamo, fecha_venc, fecha_pago) VALUES
(3, '2024-07-01', NULL), 
(4, '2024-08-01', NULL), 
(5, '2024-09-01', NULL); 

