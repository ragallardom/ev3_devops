# Taskflow - Gestor de Tareas Fullstack (Dockerizado)

Taskflow es una aplicación web full-stack premium para la gestión de tareas estilo Kanban. Cuenta con un diseño estético moderno y responsivo con soporte para métricas y transiciones fluidas.

## Arquitectura del Proyecto

El proyecto está compuesto por tres componentes principales coordinados mediante contenedores:
1. **Base de Datos**: PostgreSQL 16 (Alpine).
2. **Backend**: API REST en Spring Boot 3.3 (Java 21) que gestiona la lógica de persistencia. La inicialización y carga de datos se maneja automáticamente con **Flyway migrations**.
3. **Frontend**: Aplicación SPA construida con Vue 3 (Vite, Vanilla CSS y Lucide Icons) servida a través de **Nginx** actuando como servidor web y proxy reverso para redirigir las peticiones de API sin problemas de CORS.

---

## Requisitos Previos

- [Docker](https://docs.docker.com/get-docker/) instalado.
- [Docker Compose](https://docs.docker.com/compose/install/) instalado.

---

## Ejecución Local

Para levantar todo el entorno localizado ejecuta el siguiente comando en la raíz del proyecto:

```bash
docker compose up --build
```

Este comando realizará las siguientes acciones:
1. Levantará el contenedor de base de datos PostgreSQL.
2. Compilará el backend mediante una imagen constructora de Maven y arrancará Spring Boot.
3. Flyway ejecutará las migraciones (`V1__create_tasks_table.sql` y `V2__insert_sample_tasks.sql`) de forma automática, estructurando la base de datos y cargando tareas de demostración.
4. Compilará la aplicación de Vue.js y la desplegará en un servidor Nginx.

Una vez que termine de compilar e iniciar, podrás acceder a las siguientes URLs:
- **Frontend (Aplicación)**: [http://localhost](http://localhost)
- **Backend (API REST)**: [http://localhost:8080/api/tasks](http://localhost:8080/api/tasks)
- **Base de Datos**: Expuesta localmente en el puerto `5432` (Credenciales: `taskflow_user` / `taskflow_password` / DB: `taskflow_db`).

Para detener los contenedores:
```bash
docker compose down
```

---

## Estructura de Endpoints de la API

| Método | Endpoint | Descripción | Body (JSON) / Params |
|--------|----------|-------------|----------------------|
| `GET` | `/api/tasks` | Obtener todas las tareas (ordenadas por fecha de creación descendente) | Ninguno |
| `GET` | `/api/tasks/{id}` | Obtener una tarea por ID | Ninguno |
| `POST` | `/api/tasks` | Crear una nueva tarea | `{ "title": "Nombre", "description": "...", "priority": "HIGH", "dueDate": "2026-06-25T12:00:00" }` |
| `PUT` | `/api/tasks/{id}` | Actualizar por completo una tarea | Mismo body que POST |
| `PATCH` | `/api/tasks/{id}/status` | Cambiar de forma rápida el estado de una tarea | `{ "status": "IN_PROGRESS" }` |
| `DELETE` | `/api/tasks/{id}` | Eliminar una tarea | Ninguno |

---

## Despliegue en AWS ECR (Elastic Container Registry)

Dado que quieres alojar estas imágenes en AWS ECR para agregarlas a un clúster de AWS (como ECS o EKS), aquí tienes los pasos necesarios para realizar el build y el push:

### 1. Iniciar sesión en AWS ECR
Primero, debes configurar tus credenciales de AWS CLI y autenticarte en tu registro de ECR:

```bash
# Reemplaza <AWS_REGION> y <AWS_ACCOUNT_ID> con tus datos correspondientes
aws ecr get-login-password --region <AWS_REGION> | docker login --username AWS --password-stdin <AWS_ACCOUNT_ID>.dkr.ecr.<AWS_REGION>.amazonaws.com
```

### 2. Crear los Repositorios en ECR (Si aún no existen)
Crea repositorios independientes en ECR para el frontend y el backend:

```bash
# Repositorio para Backend
aws ecr create-repository --repository-name taskflow-backend --region <AWS_REGION>

# Repositorio para Frontend
aws ecr create-repository --repository-name taskflow-frontend --region <AWS_REGION>
```

### 3. Compilar y Etiquetar las Imágenes

#### Compilar e Etiquetar el Backend:
```bash
# 1. Compilar imagen
docker build -t taskflow-backend ./backend

# 2. Agregar etiqueta de ECR
docker tag taskflow-backend:latest <AWS_ACCOUNT_ID>.dkr.ecr.<AWS_REGION>.amazonaws.com/taskflow-backend:latest
```

#### Compilar e Etiquetar el Frontend:
```bash
# 1. Compilar imagen
docker build -t taskflow-frontend ./frontend

# 2. Agregar etiqueta de ECR
docker tag taskflow-frontend:latest <AWS_ACCOUNT_ID>.dkr.ecr.<AWS_REGION>.amazonaws.com/taskflow-frontend:latest
```

### 4. Subir (Push) las Imágenes a ECR

```bash
# Subir Backend
docker push <AWS_ACCOUNT_ID>.dkr.ecr.<AWS_REGION>.amazonaws.com/taskflow-backend:latest

# Subir Frontend
docker push <AWS_ACCOUNT_ID>.dkr.ecr.<AWS_REGION>.amazonaws.com/taskflow-frontend:latest
```

---

## Configuración en AWS (ECS/EKS)

- **Backend**: Configura las variables de entorno en tu definición de tareas (Task Definition en ECS) o Deployment YAML (en EKS) para apuntar a tu base de datos de producción (por ejemplo, AWS RDS PostgreSQL):
  - `DB_HOST`: Endpoint de la base de datos RDS.
  - `DB_PORT`: Puerto (normalmente `5432`).
  - `DB_NAME`: Nombre de la base de datos.
  - `DB_USER`: Usuario administrador de base de datos.
  - `DB_PASSWORD`: Contraseña (preferiblemente recuperada de AWS Secrets Manager o AWS Systems Manager Parameter Store).
- **Frontend**: Servirá los archivos compilados estáticamente. Dado que el Nginx del frontend realiza un proxy reverso de `/api/` a `http://backend:8080/api/`, asegúrate de que el backend sea accesible en esa dirección dentro de tu Service Mesh / Cloud Map o configuración de red privada en la VPC de AWS.
