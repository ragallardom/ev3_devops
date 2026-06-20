-- V2__insert_sample_tasks.sql
INSERT INTO tasks (title, description, status, priority, due_date, created_at, updated_at) VALUES
('Diseñar Arquitectura del Sistema', 'Definir tecnologías, base de datos y esquema inicial del backend con Spring Boot.', 'DONE', 'LOW', CURRENT_TIMESTAMP - INTERVAL '2 days', CURRENT_TIMESTAMP - INTERVAL '3 days', CURRENT_TIMESTAMP - INTERVAL '2 days'),
('Conectar Base de Datos con Flyway', 'Asegurar que las migraciones de base de datos se ejecuten automáticamente al iniciar el backend.', 'IN_PROGRESS', 'HIGH', CURRENT_TIMESTAMP + INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP),
('Implementar Frontend en Vue.js', 'Diseñar una UI moderna, limpia y responsive para visualizar las tareas del tablero.', 'IN_PROGRESS', 'MEDIUM', CURRENT_TIMESTAMP + INTERVAL '3 days', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP),
('Configurar clúster ECS en AWS', 'Configurar la VPC, subnets, ECS Cluster y las tareas correspondientes para desplegar la app en ECR.', 'TODO', 'HIGH', CURRENT_TIMESTAMP + INTERVAL '5 days', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Configurar Pipeline de CI/CD con GitHub Actions', 'Agregar builds automáticos y push de imágenes a AWS ECR al hacer push a main.', 'TODO', 'MEDIUM', CURRENT_TIMESTAMP + INTERVAL '7 days', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
