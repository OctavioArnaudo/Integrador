# API RESTful

Esta es una API RESTful diseñada para gestionar contraseñas de forma segura y eficiente. Esta API proporciona endpoints para crear, leer, actualizar y eliminar contraseñas, así como para autenticar usuarios.

## Tecnologías Utilizadas

- **Flask**: Framework web de Python para desarrollar la API.
- **Flask-JWT-Extended**: Extensión de Flask para la gestión de tokens de autenticación JWT.
- **Flask-CORS**: Extensión de Flask para habilitar el intercambio de recursos entre diferentes dominios (CORS).
- **psycopg2**: Biblioteca de Python para interactuar con bases de datos SQL de forma eficiente.
- **PostgreSQL**: Motor de bases de datos relacional GNU utilizado para almacenar datos de forma segura.

## Requisitos de Ejecución

Antes de ejecutar la API, asegúrate de tener instalado Python en tu sistema. Puedes descargar Python desde [python.org](https://www.python.org/).

### Pasos para Ejecutar la API Localmente

1. **Posicionate en el directorio**:

```sh
cd backend
```

2. **Configura las Variables de Entorno**:


Crea un archivo .env en el directorio del proyecto y establece las variables de entorno necesarias, como la clave secreta del JWT y las credenciales de la base de datos.

```sh
SECRET_KEY='tu_clave_secreta'
DB_HOST='host de concecion a la base de datos'
DB_PORT=5432
DB_USER='usuario_de_la_base_de_datos'
DB_PASSWORD='password_de_la_base_de_datos'
DB_NAME='nombre_de_la_base_de_datos'
ENCRYPTION_KEY= 'clave_de_encriptacion'
JWT_KEY= 'clave_para_los_token'
PORT=5000
```

3. **Crea un entorno virtual, y luego activalo***:

```sh
 python -m venv venv 
 .\venv\Scripts\activate
```

4. **Instala las librerias requeridas**

```sh
pip install --no-cache-dir --upgrade -r requirements.txt
```

5. **Inicia la aplicacion**

```sh
python .\src\app.py
```

La API estará disponible en http://localhost:5000.
