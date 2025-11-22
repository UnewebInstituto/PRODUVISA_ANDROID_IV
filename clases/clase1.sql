USE emergencias;
GO

-- 1. Carga de Trabajadores (3 registros)
INSERT INTO Trabajadores (cedula, nombres, apellidos, correo_electronico, telefono) VALUES
('V12345678', 'Ana', 'Gómez Pérez', 'ana.gomez@empresa.com', '555-1234'), -- Trabajador 1 (ID 1)
('V09876543', 'Juan', 'Rodríguez López', 'juan.rodriguez@empresa.com', '555-5678'), -- Trabajador 2 (ID 2)
('V11223344', 'María', 'Fernández Soto', 'maria.fdez@empresa.com', '555-9012'); -- Trabajador 3 (ID 3)
GO

-- 2. Carga de Familiares (Asociados a los Trabajadores)

-- Familiares del Trabajador 1 (ID 1: Ana Gómez Pérez) - 1 Familiar
INSERT INTO Familiares (trabajador_id, cedula, nombres, apellidos) VALUES
((SELECT id FROM Trabajadores WHERE cedula = 'V12345678'), 'V20000001', 'Pedro', 'Gómez Pérez'); -- Familiar 1 (ID 1)

-- Familiares del Trabajador 2 (ID 2: Juan Rodríguez López) - 2 Familiares
INSERT INTO Familiares (trabajador_id, cedula, nombres, apellidos) VALUES
((SELECT id FROM Trabajadores WHERE cedula = 'V09876543'), 'V20000002', 'Luisa', 'Rodríguez López'), -- Familiar 2 (ID 2)
((SELECT id FROM Trabajadores WHERE cedula = 'V09876543'), 'V20000003', 'Carlos', 'Rodríguez López'); -- Familiar 3 (ID 3)

-- Familiares del Trabajador 3 (ID 3: María Fernández Soto) - 4 Familiares
INSERT INTO Familiares (trabajador_id, cedula, nombres, apellidos) VALUES
((SELECT id FROM Trabajadores WHERE cedula = 'V11223344'), 'V20000004', 'Elena', 'Fernández Soto'), -- Familiar 4 (ID 4)
((SELECT id FROM Trabajadores WHERE cedula = 'V11223344'), 'V00000005', 'Miguel', 'Fernández Soto'), -- Familiar 5 (ID 5)
((SELECT id FROM Trabajadores WHERE cedula = 'V11223344'), 'V00000006', 'Sofía', 'Fernández Soto'), -- Familiar 6 (ID 6)
((SELECT id FROM Trabajadores WHERE cedula = 'V11223344'), 'V00000007', 'Gabriel', 'Fernández Soto'); -- Familiar 7 (ID 7)
GO
