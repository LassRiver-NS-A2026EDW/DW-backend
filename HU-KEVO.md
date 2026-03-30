
### Historias de Usuario Funcionales

| Historia de Usuario |  |  |  |
| ----- | :---- | ----- | ----- |
| **Código:** | HU-F01-01 |  |  |
| **Nombre:** | Registro de usuario |  |  |
| **Actor:** | Visitante |  |  |
| **Descripción:** | **Como** visitante,<br>**Quiero** crear una cuenta con nombre, correo y contraseña,<br>**Para** acceder a funcionalidades personalizadas. |  |  |
| **Criterios de aceptación:** | 1. Si el correo ya existe, el sistema muestra error claro.<br>2. La contraseña cumple reglas mínimas de seguridad.<br>3. Tras registro exitoso, el usuario puede iniciar sesión. |  |  |
| **Prioridad:** | Alta |  |  |
| **Dependencias:** | EP-T01, EP-T02 |  |  |

<br>

| Historia de Usuario |  |  |  |
| ----- | :---- | ----- | ----- |
| **Código:** | HU-F01-02 |  |  |
| **Nombre:** | Inicio de sesión |  |  |
| **Actor:** | Usuario registrado |  |  |
| **Descripción:** | **Como** usuario registrado,<br>**Quiero** autenticarme con correo y contraseña,<br>**Para** entrar a mi cuenta y mis datos. |  |  |
| **Criterios de aceptación:** | 1. Credenciales válidas generan token/sesión.<br>2. Credenciales inválidas retornan error controlado.<br>3. El frontend recibe perfil mínimo (id, nombre, rol). |  |  |
| **Prioridad:** | Alta |  |  |
| **Dependencias:** | HU-F01-01 |  |  |

<br>

| Historia de Usuario |  |  |  |
| ----- | :---- | ----- | ----- |
| **Código:** | HU-F01-03 |  |  |
| **Nombre:** | Cierre de sesión |  |  |
| **Actor:** | Usuario autenticado |  |  |
| **Descripción:** | **Como** usuario autenticado,<br>**Quiero** cerrar sesión,<br>**Para** proteger mi cuenta en dispositivos compartidos. |  |  |
| **Criterios de aceptación:** | 1. El token/sesión deja de ser usable.<br>2. Las rutas privadas quedan protegidas nuevamente. |  |  |
| **Prioridad:** | Alta |  |  |
| **Dependencias:** | HU-F01-02 |  |  |

<br>

| Historia de Usuario |  |  |  |
| ----- | :---- | ----- | ----- |
| **Código:** | HU-F01-04 |  |  |
| **Nombre:** | Editar perfil |  |  |
| **Actor:** | Usuario autenticado |  |  |
| **Descripción:** | **Como** usuario autenticado,<br>**Quiero** actualizar mis datos básicos (nombre, idioma, preferencias),<br>**Para** personalizar mi experiencia. |  |  |
| **Criterios de aceptación:** | 1. Solo puedo editar mi perfil.<br>2. El sistema valida formato de campos.<br>3. Los cambios persisten correctamente. |  |  |
| **Prioridad:** | Media |  |  |
| **Dependencias:** | HU-F01-02 |  |  |

<br>

| Historia de Usuario |  |  |  |
| ----- | :---- | ----- | ----- |
| **Código:** | HU-F02-01 |  |  |
| **Nombre:** | Listar catálogo |  |  |
| **Actor:** | Visitante o usuario |  |  |
| **Descripción:** | **Como** visitante o usuario,<br>**Quiero** ver libros disponibles,<br>**Para** explorar contenido. |  |  |
| **Criterios de aceptación:** | 1. Se muestran título, autor, categoría, portada.<br>2. La respuesta soporta paginación.<br>3. Si no hay resultados, se muestra estado vacío. |  |  |
| **Prioridad:** | Alta |  |  |
| **Dependencias:** | EP-T02 |  |  |

<br>

| Historia de Usuario |  |  |  |
| ----- | :---- | ----- | ----- |
| **Código:** | HU-F02-02 |  |  |
| **Nombre:** | Buscar libros |  |  |
| **Actor:** | Visitante o usuario |  |  |
| **Descripción:** | **Como** visitante o usuario,<br>**Quiero** buscar por título o autor,<br>**Para** encontrar libros rápidamente. |  |  |
| **Criterios de aceptación:** | 1. Búsqueda por coincidencia parcial.<br>2. Permite limpiar búsqueda.<br>3. Mantiene tiempos de respuesta adecuados. |  |  |
| **Prioridad:** | Alta |  |  |
| **Dependencias:** | HU-F02-01 |  |  |

<br>

| Historia de Usuario |  |  |  |
| ----- | :---- | ----- | ----- |
| **Código:** | HU-F02-03 |  |  |
| **Nombre:** | Filtrar catálogo |  |  |
| **Actor:** | Visitante o usuario |  |  |
| **Descripción:** | **Como** visitante o usuario,<br>**Quiero** filtrar por categoría e idioma,<br>**Para** ver resultados relevantes. |  |  |
| **Criterios de aceptación:** | 1. Filtros combinables.<br>2. El estado de filtros se conserva al paginar. |  |  |
| **Prioridad:** | Media |  |  |
| **Dependencias:** | HU-F02-01 |  |  |

<br>

| Historia de Usuario |  |  |  |
| ----- | :---- | ----- | ----- |
| **Código:** | HU-F02-04 |  |  |
| **Nombre:** | Ver detalle de libro |  |  |
| **Actor:** | Visitante o usuario |  |  |
| **Descripción:** | **Como** visitante o usuario,<br>**Quiero** abrir la ficha de un libro,<br>**Para** decidir si lo reservo, valoro o guardo. |  |  |
| **Criterios de aceptación:** | 1. Muestra metadatos completos.<br>2. Muestra rating promedio y reseñas.<br>3. Presenta acciones según permisos. |  |  |
| **Prioridad:** | Alta |  |  |
| **Dependencias:** | HU-F02-01, HU-F04-03 |  |  |

<br>

| Historia de Usuario |  |  |  |
| ----- | :---- | ----- | ----- |
| **Código:** | HU-F04-01 |  |  |
| **Nombre:** | Gestionar favoritos |  |  |
| **Actor:** | Usuario autenticado |  |  |
| **Descripción:** | **Como** usuario autenticado,<br>**Quiero** agregar/quitar libros favoritos,<br>**Para** guardar libros de interés. |  |  |
| **Criterios de aceptación:** | 1. No se duplican favoritos.<br>2. El cambio se refleja inmediatamente en UI. |  |  |
| **Prioridad:** | Alta |  |  |
| **Dependencias:** | HU-F01-02, HU-F02-01 |  |  |

<br>

| Historia de Usuario |  |  |  |
| ----- | :---- | ----- | ----- |
| **Código:** | HU-F04-02 |  |  |
| **Nombre:** | Listar favoritos |  |  |
| **Actor:** | Usuario autenticado |  |  |
| **Descripción:** | **Como** usuario autenticado,<br>**Quiero** ver mis favoritos,<br>**Para** retomar lecturas fácilmente. |  |  |
| **Criterios de aceptación:** | 1. Lista paginada ordenada por fecha de agregado.<br>2. Permite navegar al detalle de libro. |  |  |
| **Prioridad:** | Media |  |  |
| **Dependencias:** | HU-F04-01 |  |  |

<br>

| Historia de Usuario |  |  |  |
| ----- | :---- | ----- | ----- |
| **Código:** | HU-F04-03 |  |  |
| **Nombre:** | Crear reseña/calificación |  |  |
| **Actor:** | Usuario autenticado |  |  |
| **Descripción:** | **Como** usuario autenticado,<br>**Quiero** calificar y reseñar un libro,<br>**Para** compartir mi opinión. |  |  |
| **Criterios de aceptación:** | 1. Rating entre 1 y 5.<br>2. Una reseña por usuario/libro (editable).<br>3. Se recalcula promedio del libro. |  |  |
| **Prioridad:** | Alta |  |  |
| **Dependencias:** | HU-F01-02, HU-F02-04 |  |  |

<br>

| Historia de Usuario |  |  |  |
| ----- | :---- | ----- | ----- |
| **Código:** | HU-F04-04 |  |  |
| **Nombre:** | Editar/eliminar reseña propia |  |  |
| **Actor:** | Usuario autenticado |  |  |
| **Descripción:** | **Como** usuario autenticado,<br>**Quiero** modificar o borrar mi reseña,<br>**Para** mantener mis opiniones actualizadas. |  |  |
| **Criterios de aceptación:** | 1. Solo autor puede modificar/eliminar.<br>2. El rating promedio se actualiza tras cambios. |  |  |
| **Prioridad:** | Media |  |  |
| **Dependencias:** | HU-F04-03 |  |  |

<br>

| Historia de Usuario |  |  |  |
| ----- | :---- | ----- | ----- |
| **Código:** | HU-F05-01 |  |  |
| **Nombre:** | Crear libros |  |  |
| **Actor:** | Administrador |  |  |
| **Descripción:** | **Como** administrador,<br>**Quiero** registrar libros en catálogo,<br>**Para** mantener la biblioteca actualizada. |  |  |
| **Criterios de aceptación:** | 1. Valida campos obligatorios.<br>2. ISBN único cuando aplique. |  |  |
| **Prioridad:** | Alta |  |  |
| **Dependencias:** | EP-T01 |  |  |

<br>

| Historia de Usuario |  |  |  |
| ----- | :---- | ----- | ----- |
| **Código:** | HU-F05-02 |  |  |
| **Nombre:** | Editar/eliminar libros |  |  |
| **Actor:** | Administrador |  |  |
| **Descripción:** | **Como** administrador,<br>**Quiero** modificar o desactivar libros,<br>**Para** asegurar calidad del catálogo. |  |  |
| **Criterios de aceptación:** | 1. Cambios quedan registrados con timestamp.<br>2. No se elimina físicamente si hay préstamo activo. |  |  |
| **Prioridad:** | Alta |  |  |
| **Dependencias:** | HU-F05-01, HU-F03-01 |  |  |

<br>

| Historia de Usuario |  |  |  |
| ----- | :---- | ----- | ----- |
| **Código:** | HU-F05-03 |  |  |
| **Nombre:** | Gestionar préstamos en panel |  |  |
| **Actor:** | Administrador/bibliotecario |  |  |
| **Descripción:** | **Como** administrador/bibliotecario,<br>**Quiero** supervisar estados y operaciones,<br>**Para** controlar el flujo de préstamos. |  |  |
| **Criterios de aceptación:** | 1. Vista por estados (activo, vencido, devuelto).<br>2. Acciones administrativas auditables. |  |  |
| **Prioridad:** | Media |  |  |
| **Dependencias:** | HU-F03-01 |  |  |

<br>

| Historia de Usuario |  |  |  |
| ----- | :---- | ----- | ----- |
| **Código:** | HU-F05-04 |  |  |
| **Nombre:** | Moderar reseñas |  |  |
| **Actor:** | Administrador |  |  |
| **Descripción:** | **Como** administrador,<br>**Quiero** ocultar reseñas inapropiadas,<br>**Para** mantener calidad de la comunidad. |  |  |
| **Criterios de aceptación:** | 1. Reseña puede marcarse como oculta.<br>2. Se registra quién moderó y cuándo. |  |  |
| **Prioridad:** | Baja |  |  |
| **Dependencias:** | HU-F04-03 |  |  |

-----

### Historias de Usuario Técnicas

| Historia de Usuario |  |  |  |
| ----- | :---- | ----- | ----- |
| **Código:** | HU-T01-01 |  |  |
| **Nombre:** | Seguridad JWT |  |  |
| **Actor:** | Equipo técnico |  |  |
| **Descripción:** | **Como** equipo técnico,<br>**Quiero** proteger endpoints con JWT,<br>**Para** manejar autenticación stateless. |  |  |
| **Criterios de aceptación:** | 1. Endpoints públicos y privados definidos.<br>2. Token inválido/expirado responde 401. |  |  |
| **Prioridad:** | Alta |  |  |
| **Dependencias:** | Ninguna |  |  |

<br>

| Historia de Usuario |  |  |  |
| ----- | :---- | ----- | ----- |
| **Código:** | HU-T01-02 |  |  |
| **Nombre:** | Control por roles |  |  |
| **Actor:** | Equipo técnico |  |  |
| **Descripción:** | **Como** equipo técnico,<br>**Quiero** restringir endpoints administrativos,<br>**Para** evitar accesos no autorizados. |  |  |
| **Criterios de aceptación:** | 1. Usuario común no accede a rutas admin.<br>2. Rol admin sí accede a gestión completa. |  |  |
| **Prioridad:** | Alta |  |  |
| **Dependencias:** | HU-T01-01 |  |  |

<br>

| Historia de Usuario |  |  |  |
| ----- | :---- | ----- | ----- |
| **Código:** | HU-T01-03 |  |  |
| **Nombre:** | Contraseñas seguras |  |  |
| **Actor:** | Equipo técnico |  |  |
| **Descripción:** | **Como** equipo técnico,<br>**Quiero** almacenar contraseñas con hash robusto,<br>**Para** proteger credenciales de usuarios. |  |  |
| **Criterios de aceptación:** | 1. Nunca se guarda contraseña en texto plano.<br>2. Se aplica política mínima de contraseña. |  |  |
| **Prioridad:** | Alta |  |  |
| **Dependencias:** | HU-T01-01 |  |  |

<br>

| Historia de Usuario |  |  |  |
| ----- | :---- | ----- | ----- |
| **Código:** | HU-T02-01 |  |  |
| **Nombre:** | Arquitectura en capas + DTOs |  |  |
| **Actor:** | Equipo técnico |  |  |
| **Descripción:** | **Como** equipo técnico,<br>**Quiero** separar controller/service/repository y usar DTOs,<br>**Para** facilitar mantenimiento e integración con frontend. |  |  |
| **Criterios de aceptación:** | 1. Entidades no se exponen directamente.<br>2. Validación de request centralizada. |  |  |
| **Prioridad:** | Alta |  |  |
| **Dependencias:** | Ninguna |  |  |

<br>

| Historia de Usuario |  |  |  |
| ----- | :---- | ----- | ----- |
| **Código:** | HU-T02-02 |  |  |
| **Nombre:** | Manejo global de errores |  |  |
| **Actor:** | Equipo técnico |  |  |
| **Descripción:** | **Como** equipo técnico,<br>**Quiero** un formato estándar de errores,<br>**Para** simplificar manejo en frontend. |  |  |
| **Criterios de aceptación:** | 1. Validaciones retornan 400 con detalle por campo.<br>2. Errores no controlados retornan 500 estandarizado. |  |  |
| **Prioridad:** | Alta |  |  |
| **Dependencias:** | HU-T02-01 |  |  |

<br>

| Historia de Usuario |  |  |  |
| ----- | :---- | ----- | ----- |
| **Código:** | HU-T02-03 |  |  |
| **Nombre:** | Paginación/filtros estándar |  |  |
| **Actor:** | Equipo técnico |  |  |
| **Descripción:** | **Como** equipo técnico,<br>**Quiero** estandarizar page, size, sort,<br>**Para** consistencia de APIs de listado. |  |  |
| **Criterios de aceptación:** | 1. Endpoints de lista soportan paginación.<br>2. Respuesta incluye metadatos de paginación. |  |  |
| **Prioridad:** | Media |  |  |
| **Dependencias:** | HU-T02-01 |  |  |

<br>

| Historia de Usuario |  |  |  |
| ----- | :---- | ----- | ----- |
| **Código:** | HU-T02-04 |  |  |
| **Nombre:** | Documentación OpenAPI |  |  |
| **Actor:** | Equipo técnico y frontend |  |  |
| **Descripción:** | **Como** equipo técnico y frontend,<br>**Quiero** documentación viva de endpoints,<br>**Para** acelerar integración y pruebas. |  |  |
| **Criterios de aceptación:** | 1. Swagger UI disponible en desarrollo.<br>2. Endpoints MVP documentados. |  |  |
| **Prioridad:** | Media |  |  |
| **Dependencias:** | HU-T02-01 |  |  |

<br>

| Historia de Usuario |  |  |  |
| ----- | :---- | ----- | ----- |
| **Código:** | HU-T03-01 |  |  |
| **Nombre:** | Migraciones de base de datos |  |  |
| **Actor:** | Equipo técnico |  |  |
| **Descripción:** | **Como** equipo técnico,<br>**Quiero** versionar esquema con migraciones,<br>**Para** garantizar reproducibilidad del entorno. |  |  |
| **Criterios de aceptación:** | 1. Esquema se crea automáticamente.<br>2. Migraciones quedan en repositorio. |  |  |
| **Prioridad:** | Alta |  |  |
| **Dependencias:** | HU-T02-01 |  |  |

<br>

| Historia de Usuario |  |  |  |
| ----- | :---- | ----- | ----- |
| **Código:** | HU-T03-02 |  |  |
| **Nombre:** | Configuración por ambientes |  |  |
| **Actor:** | Equipo técnico |  |  |
| **Descripción:** | **Como** equipo técnico,<br>**Quiero** separar perfiles dev/prod,<br>**Para** desplegar sin riesgos de configuración. |  |  |
| **Criterios de aceptación:** | 1. Variables sensibles por entorno.<br>2. Sin secretos hardcodeados. |  |  |
| **Prioridad:** | Alta |  |  |
| **Dependencias:** | HU-T03-01 |  |  |

<br>

| Historia de Usuario |  |  |  |
| ----- | :---- | ----- | ----- |
| **Código:** | HU-T03-03 |  |  |
| **Nombre:** | Despliegue en nube |  |  |
| **Actor:** | Equipo técnico |  |  |
| **Descripción:** | **Como** equipo técnico,<br>**Quiero** desplegar backend + BD cloud,<br>**Para** contar con demo accesible públicamente. |  |  |
| **Criterios de aceptación:** | 1. API pública disponible.<br>2. Endpoint de health operativo. |  |  |
| **Prioridad:** | Media |  |  |
| **Dependencias:** | HU-T03-02 |  |  |

<br>

| Historia de Usuario |  |  |  |
| ----- | :---- | ----- | ----- |
| **Código:** | HU-T03-04 |  |  |
| **Nombre:** | Logging y monitoreo básico |  |  |
| **Actor:** | Equipo técnico |  |  |
| **Descripción:** | **Como** equipo técnico,<br>**Quiero** trazabilidad mínima de fallos,<br>**Para** depurar incidencias rápidamente. |  |  |
| **Criterios de aceptación:** | 1. Logs de errores estructurados.<br>2. Healthcheck y métricas básicas habilitadas. |  |  |
| **Prioridad:** | Baja |  |  |
| **Dependencias:** | HU-T03-03 |  |  |

## Requisitos del Sistema

### Historia 1

> Registro de usuario
Como visitante,
Quiero crear una cuenta con nombre, correo y contraseña,
Para acceder a funcionalidades personalizadas.


#### Casos de Uso - Vista Resumen

| ID | Nombre | Actor Principal | Precondiciones | Postcondiciones | Prioridad | Complejidad | Flujos Alt |
|----|--------|-----------------|----------------|-----------------|-----------|-------------|------------|
| UC-1-1 | Crear Cuenta de Usuario | Visitante | 1 | 2 | Alta | Media | 3 |

#### Detalle de Casos de Uso

##### UC-1-1: Crear Cuenta de Usuario

**Descripción:** Permite a un visitante crear una nueva cuenta de usuario.

**Actor Principal:** Visitante  
**Actores Secundarios:** Sistema

**Precondiciones:**
- El visitante está en la página de registro.

**Postcondiciones:**
- La cuenta de usuario ha sido creada en la base de datos.
- El sistema envía un correo de bienvenida al usuario.

**Flujo Básico (resumen):**
1. El visitante completa el formulario de registro con nombre, correo electrónico y contraseña.
2. El sistema valida los datos ingresados (formato de correo, longitud de contraseña).
3. Si la validación es exitosa, el sistema hashea la contraseña y crea la cuenta de usuario en la base de datos.
4. El sistema envía un correo electrónico de bienvenida al usuario con instrucciones de acceso.

**Flujos Alternativos:**
- AF-1-1: Validación Fallida (Correo Inválido)
- AF-1-2: Validación Fallida (Contraseña Corta)
- AF-1-3: Correo Electrónico Duplicado

**Requisitos cubiertos:** FR_001_1, FR_002_1, FR_003_1

#### Criterios de Aceptación

| ID | Descripción | Escenario de Prueba | Condiciones | Tipo | Prioridad |
|----|-------------|---------------------|-------------|------|-----------|
| AC-1-1 | El usuario puede crear una cuenta de usuario exitosamente con un nombre, correo electrónico y contraseña válidos en menos de 30 segundos. | Registro de nuevo usuario - Happy Path | <br>• El nombre del usuario tiene entre 3 y 50 caracteres.<br>• La dirección de correo electrónico tiene un formato válido (ej., example@domain.com).<br>• La contraseña tiene una longitud de al menos 8 caracteres.<br>• No hay errores de validación mostrados al usuario. | Funcional | Media |
| AC-1-2 | El sistema rechaza la creación de una cuenta si el nombre del usuario está vacío. | Registro de nuevo usuario - Validación de campos | <br>• El campo 'Nombre' está vacío.<br>• El resto de los campos están completos y válidos. | Funcional | Media |
| AC-1-3 | El sistema rechaza la creación de una cuenta si la dirección de correo electrónico está vacía. | Registro de nuevo usuario - Validación de campos | <br>• El campo 'Correo' está vacío.<br>• El resto de los campos están completos y válidos. | Funcional | Media |
| AC-1-4 | El sistema rechaza la creación de una cuenta si la contraseña está vacía. | Registro de nuevo usuario - Validación de campos | <br>• El campo 'Contraseña' está vacío.<br>• El resto de los campos están completos y válidos. | Funcional | Media |
| AC-1-5 | El sistema rechaza la creación de una cuenta si la dirección de correo electrónico ya está registrada. | Registro de nuevo usuario - Duplicados | <br>• La dirección de correo electrónico ya existe en la base de datos. | Funcional | Media |
| AC-1-6 | Después de un registro exitoso, se envía un correo electrónico de bienvenida a la dirección de correo electrónico proporcionada por el usuario. | Registro de nuevo usuario - Confirmación | <br>• La cuenta del usuario se crea correctamente en la base de datos.<br>• El correo electrónico de bienvenida se envía sin errores. | Funcional | Media |
| AC-1-7 | La contraseña del usuario se hashea correctamente antes de ser almacenada en la base de datos. | Registro de nuevo usuario - Seguridad | <br>• La contraseña ingresada por el usuario se ha hasheado utilizando un algoritmo de hashing seguro (ej., bcrypt).<br>• La contraseña hasheada se almacena en la base de datos. | Funcional | Media |

**Total de criterios:** 7

#### Requisitos Funcionales
| ID | Nombre | Descripción | Prioridad | Criterios | Rationale |
|----|--------|-------------|-----------|-----------|-----------|
| FR_001_1 | Creación de Cuenta de Usuario | El sistema debe permitir a un visitante crear una nueva cuenta de usuario proporcionando un nombre válido, una dirección de correo electrónico y una contraseña. | Alta | El sistema debe mostrar un formulario de registro con campos para nombre, correo electrónico y contraseña.<br>El sistema debe validar el formato de la dirección de correo electrónico.<br>El sistema debe aplicar una longitud mínima de contraseña de 8 caracteres.<br>El sistema debe hashear la contraseña antes de almacenarla en la base de datos. | Esta es la funcionalidad principal del proceso de registro de usuario. |
| FR_002_1 | Validación de Datos | El sistema debe validar todos los datos de entrada del usuario para garantizar la integridad y seguridad de los datos. | Alta | El sistema debe rechazar los intentos de registro con formatos de correo electrónico inválidos.<br>El sistema debe rechazar los intentos de registro con contraseñas que no cumplen con el requisito de longitud mínima.<br>El sistema debe rechazar los intentos de registro con direcciones de correo electrónico duplicadas. | Prevenir que datos inválidos entren en el sistema es crucial para la integridad de los datos. |
| FR_003_1 | Confirmación de Creación de Cuenta | Tras un registro exitoso, el sistema debe crear una nueva cuenta de usuario en la base de datos y enviar un correo electrónico de bienvenida al usuario. | Alta | El sistema debe crear un nuevo registro de usuario en la base de datos con la información proporcionada.<br>El sistema debe enviar un correo electrónico a la dirección de correo electrónico registrada del usuario que contenga un mensaje de bienvenida e instrucciones para acceder al sistema. | Confirmación de la creación de la cuenta y la incorporación inicial del usuario. |


#### Requisitos No Funcionales
| ID | Nombre | Descripción | Prioridad | Criterios | Rationale |
|----|--------|-------------|-----------|-----------|-----------|
| NFR_001_1 | Seguridad | El sistema debe proteger los datos del usuario contra el acceso y la modificación no autorizados. | Alta | Las contraseñas deben almacenarse de forma segura utilizando el hashing y el salado.<br>El sistema debe implementar medidas para prevenir vulnerabilidades comunes de aplicaciones web, como la inyección SQL y el scripting entre sitios (XSS).<br>El sistema debe hacer cumplir políticas de contraseñas sólidas. | Asegurar la seguridad de los datos del usuario es primordial. |
| NFR_002_1 | Rendimiento | El proceso de registro de usuario debe ser receptivo y eficiente. | Media | El formulario de registro debe cargarse en 2 segundos.<br>El sistema debe crear una nueva cuenta de usuario en la base de datos en 5 segundos. | Un proceso de registro rápido mejora la experiencia del usuario. |


#### Requisitos de Información
| ID | Nombre | Descripción | Prioridad | Criterios | Rationale |
|----|--------|-------------|-----------|-----------|-----------|
| IR_001_1 | Plantilla de Correo Electrónico | El sistema debe utilizar una plantilla de correo electrónico predefinida para los correos electrónicos de bienvenida. | Media | La plantilla de correo electrónico debe incluir un saludo personalizado.<br>La plantilla de correo electrónico debe proporcionar instrucciones claras para acceder al sistema. | Asegurar correos electrónicos de bienvenida consistentes e informativos. |


### Historia 2

> Inicio de sesión
Como usuario registrado,
Quiero autenticarme con correo y contraseña,
Para entrar a mi cuenta y mis datos.


#### Casos de Uso - Vista Resumen

| ID | Nombre | Actor Principal | Precondiciones | Postcondiciones | Prioridad | Complejidad | Flujos Alt |
|----|--------|-----------------|----------------|-----------------|-----------|-------------|------------|
| UC-AUTH-001 | Iniciar Sesión con Credenciales Válidas | Usuario Registrado | 2 | 2 | Alta | Media | 1 |

#### Detalle de Casos de Uso

##### UC-AUTH-001: Iniciar Sesión con Credenciales Válidas

**Descripción:** Permite a un usuario registrado iniciar sesión en el sistema utilizando su correo electrónico y contraseña.

**Actor Principal:** Usuario Registrado  
**Actores Secundarios:** Sistema

**Precondiciones:**
- El usuario ha creado una cuenta y está registrado en el sistema.
- El usuario tiene acceso a su dirección de correo electrónico y contraseña.

**Postcondiciones:**
- El usuario está autenticado y redirigido a su panel de control de cuenta.
- Se establece una sesión de usuario activa.

**Flujo Básico (resumen):**
1. El usuario ingresa su dirección de correo electrónico en el campo correspondiente.
2. El usuario ingresa su contraseña en el campo correspondiente.
3. El usuario hace clic en el botón 'Iniciar Sesión'.
4. El sistema verifica la dirección de correo electrónico y la contraseña contra los datos almacenados.
5. Si la verificación es exitosa, el sistema redirige al usuario a su panel de control de cuenta.
... y 1 pasos más

**Flujos Alternativos:**
- AF-AUTH-001-1: Credenciales Inválidas

**Requisitos cubiertos:** FR-AUTH-001-1, FR-AUTH-002-2

#### Criterios de Aceptación

| ID | Descripción | Escenario de Prueba | Condiciones | Tipo | Prioridad |
|----|-------------|---------------------|-------------|------|-----------|
| AC-1 | El usuario puede iniciar sesión con credenciales válidas y ser redirigido al panel de control de su cuenta. | Inicio de sesión exitoso | <br>• El usuario ingresa una dirección de correo electrónico válida registrada en el sistema.<br>• El usuario ingresa la contraseña correcta asociada a esa dirección de correo electrónico.<br>• La contraseña ingresada coincide con el hash de contraseña almacenado en el sistema.<br>• El sistema redirige al usuario al panel de control de su cuenta después de una autenticación exitosa. | Funcional | Media |
| AC-2 | El sistema muestra un mensaje de error claro y específico cuando el usuario ingresa credenciales inválidas. | Inicio de sesión fallido - Credenciales inválidas | <br>• El usuario ingresa una dirección de correo electrónico que no está registrada en el sistema.<br>• El usuario ingresa una contraseña incorrecta asociada a esa dirección de correo electrónico.<br>• El sistema muestra un mensaje de error que indica que las credenciales son inválidas (por ejemplo, 'Correo electrónico o contraseña incorrectos'). | Caso de error | Media |
| AC-3 | El sistema rechaza la entrada de caracteres especiales en los campos de correo electrónico y contraseña. | Validación de entrada - Caracteres especiales | <br>• El usuario intenta ingresar caracteres especiales (por ejemplo, @#$%^&*) en el campo de correo electrónico.<br>• El sistema muestra un mensaje de error que indica que la entrada no es válida para el campo de correo electrónico.<br>• El usuario intenta ingresar caracteres especiales en el campo de contraseña.<br>• El sistema muestra un mensaje de error que indica que la entrada no es válida para el campo de contraseña. | Funcional | Media |
| AC-4 | El sistema maneja correctamente los campos vacíos en los campos de correo electrónico y contraseña. | Validación de entrada - Campos vacíos | <br>• El usuario deja el campo de correo electrónico vacío.<br>• El sistema muestra un mensaje de error que indica que el campo de correo electrónico es obligatorio.<br>• El usuario deja el campo de contraseña vacío.<br>• El sistema muestra un mensaje de error que indica que el campo de contraseña es obligatorio. | Funcional | Media |
| AC-5 | El sistema limita el número de intentos de inicio de sesión fallidos para evitar ataques de fuerza bruta. | Protección contra ataques de fuerza bruta | <br>• El usuario intenta iniciar sesión con credenciales incorrectas cinco veces consecutivas.<br>• Después del quinto intento fallido, el sistema bloquea temporalmente la cuenta del usuario (por ejemplo, bloquea el acceso durante 15 minutos). | Funcional | Media |
| AC-6 | El sistema registra los intentos de inicio de sesión, tanto exitosos como fallidos, para fines de auditoría y seguridad. | Registro de eventos de inicio de sesión | <br>• El sistema registra cada intento de inicio de sesión, incluyendo la dirección de correo electrónico, la hora y el resultado (éxito o fallo). | Seguridad | Media |

**Total de criterios:** 6

#### Requisitos Funcionales
| ID | Nombre | Descripción | Prioridad | Criterios | Rationale |
|----|--------|-------------|-----------|-----------|-----------|
| FR-AUTH-001-1 | User Authentication | The system shall allow registered users to authenticate themselves using their registered email address and password. | Alta | The system accepts a valid email address and password.<br>Upon successful authentication, the system redirects the user to their account dashboard.<br>Upon incorrect authentication, the system displays an error message indicating invalid credentials. | This is the core functionality of the login process. |
| FR-AUTH-002-2 | Password Verification | The system shall verify the provided password against the stored password hash. | Alta | The system uses a secure hashing algorithm to store passwords.<br>The system compares the entered password with the stored hash.<br>The system returns a success or failure status based on the comparison. | Ensures password security and correct authentication. |


#### Requisitos No Funcionales
| ID | Nombre | Descripción | Prioridad | Criterios | Rationale |
|----|--------|-------------|-----------|-----------|-----------|
| NFR-AUTH-001-3 | Security | The authentication process shall be secure and protect user credentials from unauthorized access. | Alta | The system shall use HTTPS for all communication.<br>The system shall implement measures to prevent brute-force attacks.<br>The system shall store passwords using a strong hashing algorithm (e.g., bcrypt). | Protecting user data is paramount. |


#### Requisitos de Información
| ID | Nombre | Descripción | Prioridad | Criterios | Rationale |
|----|--------|-------------|-----------|-----------|-----------|
| IR-AUTH-001-4 | User Credentials | The system requires user email addresses and passwords to authenticate users. | Alta | The system shall store user email addresses and passwords securely.<br>The system shall validate the format of email addresses.<br>The system shall enforce password complexity requirements (e.g., minimum length, character types). | Necessary data for user identification and access. |


### Historia 3

> Cierre de sesión
Como usuario autenticado,
Quiero cerrar sesión,
Para proteger mi cuenta en dispositivos compartidos.


#### Casos de Uso - Vista Resumen

| ID | Nombre | Actor Principal | Precondiciones | Postcondiciones | Prioridad | Complejidad | Flujos Alt |
|----|--------|-----------------|----------------|-----------------|-----------|-------------|------------|
| UC-3-1 | Iniciar Sesión y Cerrar Sesión | Usuario Autenticado | 2 | 2 | Alta | Baja | 1 |

#### Detalle de Casos de Uso

##### UC-3-1: Iniciar Sesión y Cerrar Sesión

**Descripción:** Permite a un usuario autenticado iniciar sesión y luego cerrar sesión para proteger su cuenta.

**Actor Principal:** Usuario Autenticado  
**Actores Secundarios:** Sistema

**Precondiciones:**
- El usuario ha iniciado sesión en el sistema.
- El usuario está en una página donde se ofrece la opción de cerrar sesión.

**Postcondiciones:**
- La sesión del usuario ha sido terminada.
- El usuario es redirigido a la página de inicio de sesión.

**Flujo Básico (resumen):**
1. El usuario hace clic en el botón o enlace 'Cerrar Sesión'.
2. El sistema envía una solicitud de cierre de sesión al servidor.
3. El servidor verifica la identidad del usuario y elimina su sesión.
4. El servidor redirige al usuario a la página de inicio de sesión.

**Flujos Alternativos:**
- AF-1: Error al cerrar sesión

**Requisitos cubiertos:** FR-3-1

#### Criterios de Aceptación

| ID | Descripción | Escenario de Prueba | Condiciones | Tipo | Prioridad |
|----|-------------|---------------------|-------------|------|-----------|
| AC-3-1 | El usuario puede cerrar sesión a través del botón 'Cerrar sesión' ubicado en la esquina superior derecha de la página. | Cierre de sesión desde la interfaz de usuario | <br>• El botón 'Cerrar sesión' debe estar visible y accesible.<br>• Al hacer clic en el botón, se debe mostrar un mensaje de confirmación breve (ej. 'Cerrando sesión...'). | Funcional | Media |
| AC-3-2 | Después de un cierre de sesión exitoso, el usuario es redirigido a la página de inicio de sesión. | Redirección después del cierre de sesión | <br>• La URL de la página de destino después del cierre de sesión debe ser la página de inicio de sesión.<br>• El usuario no debe tener acceso a ninguna funcionalidad protegida después del cierre de sesión. | Funcional | Media |
| AC-3-3 | La sesión del usuario es eliminada del servidor después de un cierre de sesión exitoso. | Eliminación de datos de sesión | <br>• Después del cierre de sesión, no se debe poder acceder a la cuenta del usuario a través de ninguna API o endpoint.<br>• La información de sesión del usuario debe ser eliminada de la base de datos. | Funcional | Media |
| AC-3-4 | Si el usuario intenta cerrar sesión sin estar autenticado, se redirige a la página de inicio de sesión. | Cierre de sesión sin autenticación | <br>• Si el usuario intenta cerrar sesión sin estar autenticado, se debe redirigir a la página de inicio de sesión.<br>• La página de inicio de sesión debe estar visible y accesible. | Funcional | Media |
| AC-3-5 | El sistema debe manejar correctamente los intentos de cierre de sesión múltiples en un corto período de tiempo. | Manejo de múltiples intentos de cierre de sesión | <br>• El sistema debe evitar ataques de fuerza bruta al cerrar la sesión del usuario después de un número determinado de intentos fallidos.<br>• El sistema debe registrar los intentos de cierre de sesión fallidos para fines de auditoría. | Rendimiento | Media |
| AC-3-6 | El tiempo de respuesta para el cierre de sesión no debe exceder los 1 segundo. | Rendimiento del cierre de sesión | <br>• El tiempo de respuesta para el cierre de sesión debe ser medible y estar dentro del rango especificado.<br>• El sistema debe ser capaz de manejar múltiples solicitudes de cierre de sesión simultáneamente sin afectar el rendimiento. | Rendimiento | Media |

**Total de criterios:** 6

#### Requisitos Funcionales
| ID | Nombre | Descripción | Prioridad | Criterios | Rationale |
|----|--------|-------------|-----------|-----------|-----------|
| FR-3-1 | Logout Functionality | The system shall provide a mechanism for an authenticated user to log out of their session. | Alta | A user can initiate a logout request through a clearly labeled button or link.<br>Upon successful logout, the user is redirected to the login page.<br>The session data associated with the user is cleared from the server. | This directly addresses the user's need to protect their account on shared devices. |


#### Requisitos No Funcionales
| ID | Nombre | Descripción | Prioridad | Criterios | Rationale |
|----|--------|-------------|-----------|-----------|-----------|
| NFR-3-1 | Logout Security | The logout functionality shall be secure and prevent unauthorized access. | Alta | Logout should require valid authentication credentials (e.g., session token).<br>Logout should not be susceptible to brute-force attacks. | Critical for protecting user accounts. |


#### Requisitos de Información
| ID | Nombre | Descripción | Prioridad | Criterios | Rationale |
|----|--------|-------------|-----------|-----------|-----------|
| IR-3-1 | User Session Data Tracking | The system shall track user session data to enable logout functionality. | Alta | Session data must include a unique session identifier.<br>Session data must be stored securely. | Necessary for identifying and terminating the user's session. |


### Historia 4

> Editar perfil
Como usuario autenticado,
Quiero actualizar mis datos básicos (nombre, idioma, preferencias),
Para personalizar mi experiencia.


#### Casos de Uso - Vista Resumen

| ID | Nombre | Actor Principal | Precondiciones | Postcondiciones | Prioridad | Complejidad | Flujos Alt |
|----|--------|-----------------|----------------|-----------------|-----------|-------------|------------|
| UC-4-001 | Actualizar Información de Perfil | Usuario Autenticado | 2 | 2 | Alta | Media | 2 |

#### Detalle de Casos de Uso

##### UC-4-001: Actualizar Información de Perfil

**Descripción:** Permite a un usuario autenticado modificar sus datos básicos de perfil.

**Actor Principal:** Usuario Autenticado  
**Actores Secundarios:** Sistema

**Precondiciones:**
- El usuario ha iniciado sesión en el sistema.
- El usuario ha accedido a la sección de edición de perfil.

**Postcondiciones:**
- Los datos de perfil del usuario han sido actualizados en la base de datos.
- El usuario visualiza los datos actualizados en la sección de perfil.

**Flujo Básico (resumen):**
1. El usuario accede a la página de edición de perfil.
2. El sistema muestra los campos para actualizar: Nombre, Idioma, Preferencias.
3. El usuario modifica los campos según sus preferencias.
4. El usuario confirma los cambios.
5. El sistema valida los datos ingresados.
... y 2 pasos más

**Flujos Alternativos:**
- AF-4-001-1: Campo de nombre inválido
- AF-4-001-2: Campo de idioma inválido

**Requisitos cubiertos:** FR-USR-001-001, FR-USR-002-001

#### Criterios de Aceptación

| ID | Descripción | Escenario de Prueba | Condiciones | Tipo | Prioridad |
|----|-------------|---------------------|-------------|------|-----------|
| AC-4-001 | El usuario puede modificar con éxito su nombre, y el cambio se refleja en la interfaz del perfil. | Actualización del nombre del usuario | <br>• El usuario está autenticado.<br>• El usuario ingresa un nombre válido (mínimo 3 caracteres, máximo 50).<br>• El sistema guarda el nuevo nombre sin errores. | Funcional | Media |
| AC-4-002 | El usuario puede seleccionar un idioma diferente de la opción predeterminada, y la interfaz se muestra en ese idioma. | Cambio de idioma del usuario | <br>• El usuario está autenticado.<br>• El usuario selecciona un idioma válido de la lista proporcionada.<br>• La interfaz de usuario cambia al idioma seleccionado. | Funcional | Media |
| AC-4-003 | El sistema valida que el campo de nombre no esté vacío y que el formato del nombre sea válido. | Validación del campo de nombre | <br>• El usuario intenta guardar el perfil con un campo de nombre vacío.<br>• El sistema muestra un mensaje de error indicando que el campo de nombre es obligatorio. | Validación | Media |
| AC-4-004 | El sistema valida que el campo de idioma seleccionado sea un idioma válido. | Validación del campo de idioma | <br>• El usuario intenta guardar el perfil con un idioma no válido.<br>• El sistema muestra un mensaje de error indicando que el idioma seleccionado no es válido. | Validación | Media |
| AC-4-005 | Después de guardar los cambios en el perfil, el usuario puede acceder a su perfil actualizado correctamente. | Acceso al perfil actualizado | <br>• El usuario está autenticado.<br>• El usuario accede a su perfil.<br>• La información mostrada en el perfil coincide con los datos actualizados. | Funcional | Media |
| AC-4-006 | El sistema almacena de forma segura los datos del perfil del usuario, garantizando la privacidad. | Almacenamiento seguro de datos | <br>• El usuario actualiza su perfil.<br>• Los datos del perfil se almacenan en la base de datos de forma segura.<br>• Solo el usuario autenticado puede acceder a los datos del perfil. | Funcional | Media |

**Total de criterios:** 6

#### Requisitos Funcionales
| ID | Nombre | Descripción | Prioridad | Criterios | Rationale |
|----|--------|-------------|-----------|-----------|-----------|
| FR-USR-001-001 | Actualizar Datos de Perfil del Usuario | El sistema debe permitir a un usuario autenticado actualizar su información básica de perfil, incluyendo nombre, preferencia de idioma y otras preferencias especificadas. | Alta | El usuario puede modificar con éxito el campo de nombre.<br>El usuario puede seleccionar una preferencia de idioma diferente de las opciones disponibles.<br>El usuario puede ajustar las preferencias especificadas (p. ej., configuraciones de notificación). | Esta es la funcionalidad principal de la historia. |
| FR-USR-002-001 | Validación de Datos | El sistema debe validar todos los datos ingresados por el usuario antes de guardar los cambios en el perfil. | Alta | El sistema evita guardar datos inválidos (p. ej., campos vacíos, formatos de datos incorrectos).<br>El sistema muestra mensajes de error apropiados al usuario para datos inválidos. | Asegura la integridad de los datos y evita que se almacene información incorrecta. |
| FR-USR-003-001 | Almacenamiento de Datos de Perfil | El sistema debe almacenar de forma segura los datos actualizados del perfil del usuario. | Media | Los datos se almacenan en una base de datos segura.<br>Los datos solo son accesibles para el usuario autenticado. | Asegura la privacidad y seguridad de los datos. |


#### Requisitos No Funcionales
| ID | Nombre | Descripción | Prioridad | Criterios | Rationale |
|----|--------|-------------|-----------|-----------|-----------|
| NFR-PER-001-001 | Rendimiento - Actualización de Perfil | El sistema debe actualizar los datos del perfil del usuario en 3 segundos. | Alta | El tiempo de respuesta promedio para la actualización del perfil es inferior a 3 segundos bajo carga normal. | Proporciona una experiencia de usuario receptiva. |
| NFR-SEC-001-001 | Seguridad - Autenticación | El sistema debe requerir la autenticación del usuario antes de permitir la actualización del perfil. | Alta | Solo los usuarios autenticados pueden acceder y modificar sus datos de perfil.<br>El mecanismo de autenticación es seguro y robusto. | Protege los datos del usuario y previene el acceso no autorizado. |


#### Requisitos de Información
| ID | Nombre | Descripción | Prioridad | Criterios | Rationale |
|----|--------|-------------|-----------|-----------|-----------|
| IR-LANG-001-001 | Idiomas Admitidos | El sistema debe admitir una lista predefinida de idiomas para la selección de preferencias del usuario. | Media | Se documenta y mantiene una lista de idiomas admitidos.<br>El usuario puede seleccionar entre los idiomas admitidos. | Define el alcance del soporte de idiomas. |
| IR-DATA-001-001 | Campos de Datos de Perfil | El sistema debe almacenar los siguientes campos de datos de perfil: nombre, preferencia de idioma y otras preferencias especificadas. | Alta | El sistema almacena y recupera con precisión los campos de datos de perfil definidos. | Define la estructura de los datos de perfil. |


### Historia 5

> Listar catálogo
Como visitante o usuario,
Quiero ver libros disponibles,
Para explorar contenido.


#### Casos de Uso - Vista Resumen

| ID | Nombre | Actor Principal | Precondiciones | Postcondiciones | Prioridad | Complejidad | Flujos Alt |
|----|--------|-----------------|----------------|-----------------|-----------|-------------|------------|
| UC-5-1 | Explorar Catálogo de Libros | Visitante/Usuario | 2 | 2 | Alta | Media | 2 |

#### Detalle de Casos de Uso

##### UC-5-1: Explorar Catálogo de Libros

**Descripción:** Permite a los usuarios navegar y visualizar los libros disponibles en el catálogo.

**Actor Principal:** Visitante/Usuario

**Precondiciones:**
- El usuario ha accedido a la página del catálogo.
- El sistema ha cargado la lista de libros disponibles.

**Postcondiciones:**
- El usuario visualiza la lista de libros en el catálogo.
- La información del libro (título, autor, imagen) se muestra correctamente.

**Flujo Básico (resumen):**
1. El usuario accede a la página del catálogo.
2. El sistema muestra una lista de libros, incluyendo título, autor e imagen de portada.
3. El usuario puede hacer clic en un libro para ver más detalles.
4. El sistema muestra la página de detalles del libro con información completa.

**Flujos Alternativos:**
- AF-1-1: Filtro por Género
- AF-1-2: Búsqueda por Término

**Requisitos cubiertos:** FR_001-5-1, FR_002-5-1

#### Criterios de Aceptación

| ID | Descripción | Escenario de Prueba | Condiciones | Tipo | Prioridad |
|----|-------------|---------------------|-------------|------|-----------|
| AC-5-1 | El catálogo se carga completamente en menos de 3 segundos. | Carga inicial del catálogo | <br>• El servidor tiene suficiente capacidad para procesar la solicitud.<br>• La base de datos de libros está accesible y responde rápidamente. | Funcional | Media |
| AC-5-2 | El catálogo muestra al menos 50 libros diferentes. | Verificación del número de libros | <br>• La base de datos contiene al menos 50 libros.<br>• El sistema consulta y muestra correctamente todos los libros de la base de datos. | Funcional | Media |
| AC-5-3 | Cada libro en el catálogo muestra el título, el autor y la imagen de portada. | Visualización de detalles del libro | <br>• El título del libro se muestra correctamente.<br>• El nombre del autor se muestra correctamente.<br>• La imagen de portada del libro se muestra correctamente (si está disponible). | Funcional | Media |
| AC-5-4 | La búsqueda de libros por título devuelve resultados relevantes. | Búsqueda de libros por título | <br>• La búsqueda devuelve libros cuyo título coincida con el término de búsqueda.<br>• La búsqueda es insensible a mayúsculas y minúsculas. | Funcional | Media |
| AC-5-5 | Si no hay resultados de búsqueda, se muestra un mensaje indicando que no se encontraron libros. | Búsqueda de libros inexistentes | <br>• La búsqueda devuelve un mensaje claro indicando que no se encontraron libros.<br>• El mensaje es informativo para el usuario. | Funcional | Media |
| AC-5-6 | El sistema maneja correctamente errores de conexión a la base de datos. | Error de conexión a la base de datos | <br>• Si la conexión a la base de datos falla, se muestra un mensaje de error amigable para el usuario.<br>• El sistema no se bloquea y permite al usuario continuar navegando por el catálogo. | Caso de error | Media |

**Total de criterios:** 6

#### Requisitos Funcionales
| ID | Nombre | Descripción | Prioridad | Criterios | Rationale |
|----|--------|-------------|-----------|-----------|-----------|
| FR_001-5-1 | Navegar catálogo de libros | El sistema debe permitir a los usuarios navegar por el catálogo de libros disponibles. | Alta | Los usuarios pueden navegar a través de una lista de libros.<br>El catálogo muestra títulos de libros, autores e imágenes de portada.<br>Los usuarios pueden filtrar el catálogo por género o término de búsqueda. | Esta es la funcionalidad principal de la función del catálogo. |
| FR_002-5-1 | Recuperar datos del libro | El sistema debe recuperar datos del libro de la base de datos. | Alta | El sistema consulta correctamente la base de datos para obtener información del libro.<br>El sistema devuelve detalles del libro precisos (título, autor, ISBN, etc.) | Es necesario mostrar la información del libro al usuario. |


#### Requisitos No Funcionales
| ID | Nombre | Descripción | Prioridad | Criterios | Rationale |
|----|--------|-------------|-----------|-----------|-----------|
| NFR_001-5-1 | Rendimiento del catálogo | La experiencia de navegación del catálogo debe ser receptiva. | Alta | El catálogo debe cargarse en 3 segundos o menos.<br>El sistema debe manejar un gran número de libros (p. ej., 10.000) sin una degradación significativa del rendimiento. | Un catálogo lento afectará negativamente la experiencia del usuario. |
| NFR_002-5-1 | Accesibilidad | La interfaz del catálogo debe ser accesible para usuarios con discapacidades. | Media | El catálogo debe cumplir con las pautas de accesibilidad WCAG 2.1 Nivel AA.<br>Los usuarios con lectores de pantalla pueden navegar por el catálogo. | Garantizar la inclusión y la usabilidad para todos los usuarios. |
| NFR_003-5-1 | Manejo de errores | El sistema debe manejar los errores con gracia. | Media | El sistema muestra mensajes de error informativos al usuario cuando falla la recuperación de datos.<br>El sistema registra errores para la depuración. | Robustez y mantenibilidad. |


#### Requisitos de Información
| ID | Nombre | Descripción | Prioridad | Criterios | Rationale |
|----|--------|-------------|-----------|-----------|-----------|
| IR_001-5-1 | Metadatos del libro | El sistema requiere metadatos para cada libro. | Alta | El sistema debe almacenar título, autor, ISBN, fecha de publicación, género e URL de la imagen de portada para cada libro. | Datos esenciales para mostrar y administrar el catálogo. |
| IR_002-5-1 | Autenticación de usuario | El sistema requiere autenticación de usuario para rastrear el historial de navegación (opcional). | Baja | El sistema permite a los usuarios iniciar sesión con un nombre de usuario y contraseña válidos.<br>El sistema almacena el historial de navegación del usuario (con el consentimiento del usuario). | Mejoras futuras para recomendaciones personalizadas. |


### Historia 6

> Buscar libros
Como visitante o usuario,
Quiero buscar por título o autor,
Para encontrar libros rápidamente.


#### Casos de Uso - Vista Resumen

| ID | Nombre | Actor Principal | Precondiciones | Postcondiciones | Prioridad | Complejidad | Flujos Alt |
|----|--------|-----------------|----------------|-----------------|-----------|-------------|------------|
| UC-6-001 | Buscar Libro por Título | Usuario | 2 | 2 | Alta | Baja | 1 |
| UC-6-002 | Buscar Libro por Autor | Usuario | 2 | 2 | Alta | Baja | 1 |
| UC-6-003 | Mostrar Resultados de Búsqueda | Sistema | 2 | 2 | Alta | Baja | 0 |

#### Detalle de Casos de Uso

##### UC-6-001: Buscar Libro por Título

**Descripción:** Permite al usuario buscar libros ingresando el título del libro.

**Actor Principal:** Usuario  
**Actores Secundarios:** Sistema

**Precondiciones:**
- El usuario ha iniciado sesión.
- El usuario se encuentra en la página de búsqueda.

**Postcondiciones:**
- Se muestran los resultados de la búsqueda de libros que coinciden con el título ingresado.
- Si no se encuentran resultados, se muestra un mensaje indicando que no se encontraron libros.

**Flujo Básico (resumen):**
1. El usuario ingresa el título del libro en el campo de búsqueda 'Título'.
2. El usuario presiona el botón de 'Buscar'.
3. El sistema consulta la base de datos de libros utilizando el título ingresado.
4. El sistema muestra los resultados de la búsqueda en una lista, incluyendo el título y autor de cada libro que coincida.
5. Si no se encuentran resultados, el sistema muestra un mensaje como 'No se encontraron libros con ese título'.

**Flujos Alternativos:**
- AF-6-001-1: Título no válido (vacío)

**Requisitos cubiertos:** FR-006-001

---

##### UC-6-002: Buscar Libro por Autor

**Descripción:** Permite al usuario buscar libros ingresando el nombre del autor.

**Actor Principal:** Usuario  
**Actores Secundarios:** Sistema

**Precondiciones:**
- El usuario ha iniciado sesión.
- El usuario se encuentra en la página de búsqueda.

**Postcondiciones:**
- Se muestran los resultados de la búsqueda de libros que coinciden con el autor ingresado.
- Si no se encuentran resultados, se muestra un mensaje indicando que no se encontraron libros.

**Flujo Básico (resumen):**
1. El usuario ingresa el nombre del autor en el campo de búsqueda 'Autor'.
2. El usuario presiona el botón de 'Buscar'.
3. El sistema consulta la base de datos de libros utilizando el nombre del autor ingresado.
4. El sistema muestra los resultados de la búsqueda en una lista, incluyendo el título y autor de cada libro que coincida.
5. Si no se encuentran resultados, el sistema muestra un mensaje como 'No se encontraron libros con ese autor'.

**Flujos Alternativos:**
- AF-6-002-1: Autor no válido (vacío)

**Requisitos cubiertos:** FR-006-002

---

##### UC-6-003: Mostrar Resultados de Búsqueda

**Descripción:** Muestra los resultados de la búsqueda al usuario.

**Actor Principal:** Sistema  
**Actores Secundarios:** Usuario

**Precondiciones:**
- Se han realizado búsquedas por título o autor.
- El usuario ha iniciado sesión.

**Postcondiciones:**
- Los resultados de la búsqueda se presentan de forma clara y organizada al usuario.
- Cada resultado incluye el título y autor del libro.

**Flujo Básico (resumen):**
1. El sistema recibe los resultados de la búsqueda de la base de datos.
2. El sistema formatea los resultados en una lista o tabla.
3. El sistema muestra la lista/tabla de resultados en la página de búsqueda.
4. Cada elemento de la lista/tabla incluye el título y autor del libro.

**Requisitos cubiertos:** FR-006-003

#### Criterios de Aceptación

| ID | Descripción | Escenario de Prueba | Condiciones | Tipo | Prioridad |
|----|-------------|---------------------|-------------|------|-----------|
| AC-6-001 | El usuario puede ingresar un título de libro válido en el campo de búsqueda y ver resultados de búsqueda relevantes en menos de 3 segundos. | Búsqueda por título exitosa | <br>• El campo de búsqueda tiene un valor válido (ej. | Funcional | Media |

**Total de criterios:** 1

#### Requisitos Funcionales
| ID | Nombre | Descripción | Prioridad | Criterios | Rationale |
|----|--------|-------------|-----------|-----------|-----------|
| FR-006-001 | Buscar por Título | El sistema debe permitir al usuario buscar libros ingresando el título del libro. | Alta | El sistema muestra un campo de búsqueda etiquetado 'Título'. | Esta es una función principal para el descubrimiento de libros. |
| FR-006-002 | Buscar por Autor | El sistema debe permitir al usuario buscar libros ingresando el nombre del autor. | Alta | El sistema muestra un campo de búsqueda etiquetado 'Autor'. | Permite a los usuarios encontrar libros por el autor. |
| FR-006-003 | Mostrar Resultados de Búsqueda | El sistema debe mostrar los resultados de la búsqueda al usuario. | Alta | Los resultados de la búsqueda se muestran de manera clara y organizada.<br>Cada resultado de búsqueda incluye el título del libro y el autor. | Esencial para presentar el resultado de la búsqueda. |


#### Requisitos No Funcionales
| ID | Nombre | Descripción | Prioridad | Criterios | Rationale |
|----|--------|-------------|-----------|-----------|-----------|
| NFR-006-001 | Tiempo de Respuesta de Búsqueda | El sistema debe devolver los resultados de la búsqueda en 3 segundos. | Alta | Medido utilizando pruebas automatizadas bajo condiciones de carga típicas. | Una respuesta rápida de búsqueda mejora la experiencia del usuario. |


#### Requisitos de Información
| ID | Nombre | Descripción | Prioridad | Criterios | Rationale |
|----|--------|-------------|-----------|-----------|-----------|
| IR-006-001 | Datos del Título del Libro | El sistema debe almacenar el título de cada libro en la base de datos. | Alta | El campo de título del libro en la base de datos es de tipo VARCHAR(255). | Requerido para las búsquedas basadas en el título. |
| IR-006-002 | Datos del Autor | El sistema debe almacenar el nombre de cada autor en la base de datos. | Alta | El campo de nombre del autor en la base de datos es de tipo VARCHAR(255). | Requerido para las búsquedas basadas en el autor. |


### Historia 7

> Filtrar catálogo
Como visitante o usuario,
Quiero filtrar por categoría e idioma,
Para ver resultados relevantes.


#### Casos de Uso - Vista Resumen

| ID | Nombre | Actor Principal | Precondiciones | Postcondiciones | Prioridad | Complejidad | Flujos Alt |
|----|--------|-----------------|----------------|-----------------|-----------|-------------|------------|
| UC-7-001 | Filtrar Productos por Categoría e Idioma | Usuario | 2 | 2 | Alta | Media | 2 |

#### Detalle de Casos de Uso

##### UC-7-001: Filtrar Productos por Categoría e Idioma

**Descripción:** Permite al usuario filtrar el catálogo de productos por una o más categorías y un idioma específico, mostrando los resultados relevantes.

**Actor Principal:** Usuario  
**Actores Secundarios:** Sistema

**Precondiciones:**
- El usuario ha iniciado sesión en el sistema.
- El catálogo de productos está cargado.

**Postcondiciones:**
- La vista del catálogo se actualiza para mostrar solo los productos que coinciden con los criterios de filtro seleccionados.
- El idioma de visualización de los productos se establece según la selección del usuario.

**Flujo Básico (resumen):**
1. El usuario navega a la página del catálogo.
2. El usuario selecciona una o más categorías de la lista de categorías disponibles.
3. El usuario selecciona un idioma de la lista de idiomas disponibles.
4. El sistema aplica los filtros seleccionados al catálogo de productos.
5. El sistema actualiza la vista del catálogo para mostrar los productos filtrados.
... y 1 pasos más

**Flujos Alternativos:**
- AF-7-001: Selección de Categorías Inválidas
- AF-7-002: Selección de Idioma Inválido

**Requisitos cubiertos:** FR-CAT-001-001, FR-LANG-001-002

#### Criterios de Aceptación

| ID | Descripción | Escenario de Prueba | Condiciones | Tipo | Prioridad |
|----|-------------|---------------------|-------------|------|-----------|
| AC-7-001 | El usuario puede aplicar filtros de categoría y idioma simultáneamente. | Filtrado combinado | <br>• El usuario selecciona una categoría y un idioma.<br>• El catálogo se actualiza para mostrar solo productos que coinciden con ambas selecciones. | Funcional | Media |
| AC-7-002 | El sistema muestra un mensaje de error si el usuario selecciona una categoría o idioma inválido. | Selección inválida | <br>• El usuario selecciona una categoría o idioma que no existe en la lista predefinida.<br>• El sistema muestra un mensaje de error claro y conciso indicando que la selección es inválida. | Caso de error | Media |
| AC-7-003 | El sistema actualiza correctamente la vista del catálogo después de aplicar filtros. | Actualización de la vista | <br>• El usuario aplica filtros de categoría y/o idioma.<br>• La interfaz de usuario muestra los productos filtrados de forma clara y organizada. | Funcional | Media |
| AC-7-004 | El sistema mantiene los filtros seleccionados al recargar la página. | Persistencia de filtros | <br>• El usuario aplica filtros de categoría y/o idioma.<br>• Al recargar la página, los filtros seleccionados permanecen activos y el catálogo se actualiza en consecuencia. | Funcional | Media |
| AC-7-005 | El sistema maneja correctamente el caso en que no hay productos que coincidan con los filtros aplicados. | Sin resultados | <br>• El usuario aplica filtros que no tienen productos correspondientes.<br>• El sistema muestra un mensaje indicando que no se encontraron productos que coincidan con los criterios de búsqueda. | Funcional | Media |

**Total de criterios:** 5

#### Requisitos Funcionales
| ID | Nombre | Descripción | Prioridad | Criterios | Rationale |
|----|--------|-------------|-----------|-----------|-----------|
| FR-CAT-001-001 | Filtrar por Categoría | El sistema debe permitir al usuario seleccionar una o más categorías de una lista predefinida para filtrar el catálogo de productos. | Alta | El usuario puede seleccionar múltiples categorías.<br>Las categorías seleccionadas se reflejan en la lista de productos filtrados.<br>El sistema maneja las selecciones de categorías inválidas con gracia (por ejemplo, mostrando un mensaje de error). | Funcionalidad principal para reducir la selección de productos en función de la preferencia del usuario. |
| FR-LANG-001-002 | Filtrar por Idioma | El sistema debe permitir al usuario seleccionar un idioma de una lista predefinida para filtrar el catálogo de productos. | Alta | El usuario puede seleccionar un idioma de una lista predefinida.<br>La lista de productos filtrados muestra productos en el idioma seleccionado.<br>El sistema maneja las selecciones de idiomas inválidas con gracia (por ejemplo, mostrando un mensaje de error). | Funcionalidad principal para reducir la selección de productos en función de la preferencia del usuario. |


#### Requisitos No Funcionales

_No se generaron requisitos._


#### Requisitos de Información

_No se generaron requisitos._


### Historia 8

> Ver detalle de libro
Como visitante o usuario,
Quiero abrir la ficha de un libro,
Para decidir si lo reservo, valoro o guardo.


#### Casos de Uso - Vista Resumen

| ID | Nombre | Actor Principal | Precondiciones | Postcondiciones | Prioridad | Complejidad | Flujos Alt |
|----|--------|-----------------|----------------|-----------------|-----------|-------------|------------|
| UC-8-1 | Visualizar Detalles del Libro Selecciona... | Visitante/Usuario | 1 | 8 | Alta | Baja | 1 |
| UC-8-2 | Mostrar Ficha Detallada del Libro | Usuario | 1 | 1 | Alta | Baja | 0 |

#### Detalle de Casos de Uso

##### UC-8-1: Visualizar Detalles del Libro Seleccionado

**Descripción:** Permite al usuario ver todos los detalles de un libro específico después de seleccionarlo.

**Actor Principal:** Visitante/Usuario

**Precondiciones:**
- El usuario ha navegado a la página de detalles de un libro específico.

**Postcondiciones:**
- La ficha de detalles del libro se muestra al usuario
- incluyendo título
- autor
- ISBN
- fecha de publicación
- editorial
- número de páginas
- imagen de portada y resumen.

**Flujo Básico (resumen):**
1. El usuario selecciona un libro de la lista de libros.
2. El sistema recupera los detalles del libro seleccionado de la base de datos.
3. El sistema formatea y presenta los detalles del libro en una interfaz de usuario.
4. El sistema muestra la imagen de alta resolución de la portada del libro.
5. El sistema muestra el resumen o sinopsis del libro.

**Flujos Alternativos:**
- AF-8-1: Libro no encontrado

**Requisitos cubiertos:** FR-LIB-001

---

##### UC-8-2: Mostrar Ficha Detallada del Libro

**Descripción:** Muestra la ficha completa del libro con todos los campos disponibles.

**Actor Principal:** Usuario

**Precondiciones:**
- El usuario ha seleccionado un libro específico.

**Postcondiciones:**
- La ficha detallada del libro se muestra completamente al usuario.

**Flujo Básico (resumen):**
1. El usuario hace clic en el botón 'Ver Detalle'.
2. El sistema recupera la información del libro de la base de datos.
3. El sistema renderiza la información en una página HTML con todos los campos.
4. El sistema muestra la imagen de portada en alta resolución.
5. El sistema muestra el resumen del libro.

**Requisitos cubiertos:** FR-LIB-001

#### Criterios de Aceptación

| ID | Descripción | Escenario de Prueba | Condiciones | Tipo | Prioridad |
|----|-------------|---------------------|-------------|------|-----------|
| AC-8-1 | El usuario puede abrir la ficha de un libro haciendo clic en el título del libro en la lista de resultados de búsqueda o en la lista de libros. | Abrir ficha desde la lista de resultados | <br>• El usuario está en la lista de resultados de búsqueda o la lista de libros.<br>• El usuario hace clic en el título del libro. | Funcional | Media |
| AC-8-2 | La ficha del libro muestra el título, autor, ISBN, fecha de publicación, editorial y número de páginas del libro. | Visualización de detalles básicos del libro | <br>• El libro seleccionado está presente en la base de datos.<br>• Los campos de título, autor, ISBN, fecha de publicación, editorial y número de páginas se muestran correctamente en la ficha. | Funcional | Media |
| AC-8-3 | La ficha del libro muestra una imagen de alta resolución de la portada del libro. | Visualización de la portada del libro | <br>• La imagen de la portada del libro está presente en la base de datos.<br>• La imagen se muestra en alta resolución y sin distorsión. | Funcional | Media |
| AC-8-4 | La ficha del libro muestra un resumen o sinopsis del libro. | Visualización del resumen del libro | <br>• El resumen del libro está presente en la base de datos.<br>• El resumen se muestra correctamente en la ficha. | Funcional | Media |
| AC-8-5 | Si el libro no existe en la base de datos, se muestra un mensaje de error indicando que el libro no se encontró. | Manejo de libro inexistente | <br>• El usuario intenta abrir la ficha de un libro con un ISBN que no existe en la base de datos. | Caso de error | Media |
| AC-8-6 | La ficha del libro se carga completamente en menos de 3 segundos. | Rendimiento de la carga de la ficha | <br>• La conexión a la red es estable. | Funcional | Media |

**Total de criterios:** 6

#### Requisitos Funcionales
| ID | Nombre | Descripción | Prioridad | Criterios | Rationale |
|----|--------|-------------|-----------|-----------|-----------|
| FR-LIB-001 | Mostrar Detalles del Libro | El sistema debe mostrar todos los detalles disponibles de un libro seleccionado al usuario. | Alta | El sistema muestra el título, autor, ISBN, fecha de publicación, editorial y número de páginas del libro.<br>El sistema muestra una imagen de alta resolución de la portada del libro.<br>El sistema muestra el resumen o sinopsis del libro. | Esta es la funcionalidad principal del requisito, permitiendo al usuario tomar una decisión informada sobre el libro. |


#### Requisitos No Funcionales
| ID | Nombre | Descripción | Prioridad | Criterios | Rationale |
|----|--------|-------------|-----------|-----------|-----------|
| NFR-LIB-001 | Tiempo de Respuesta | El sistema debe mostrar los detalles del libro dentro de 2 segundos tras la solicitud del usuario. | Alta | Medido mediante pruebas de rendimiento automatizadas.<br>Tiempo de respuesta objetivo: 2 segundos bajo carga normal. | Los tiempos de respuesta rápidos son cruciales para una experiencia de usuario positiva. |


#### Requisitos de Información
| ID | Nombre | Descripción | Prioridad | Criterios | Rationale |
|----|--------|-------------|-----------|-----------|-----------|
| IR-LIB-001 | Fuente de Datos del Libro | El sistema debe obtener datos del libro de una fuente fiable y actualizada. | Alta | La fuente de datos del libro se actualiza regularmente.<br>La fuente de datos del libro se valida para garantizar su exactitud. | Los datos del libro precisos son esenciales para que la funcionalidad sea útil. |


### Historia 9

> Gestionar favoritos
Como usuario autenticado,
Quiero agregar/quitar libros favoritos,
Para guardar libros de interés.


#### Casos de Uso - Vista Resumen

| ID | Nombre | Actor Principal | Precondiciones | Postcondiciones | Prioridad | Complejidad | Flujos Alt |
|----|--------|-----------------|----------------|-----------------|-----------|-------------|------------|
| UC-9-1 | Agregar Libro Favorito | Usuario | 2 | 2 | Baja | Baja | 1 |
| UC-9-2 | Quitar Libro Favorito | Usuario | 2 | 2 | Baja | Baja | 1 |

#### Detalle de Casos de Uso

##### UC-9-1: Agregar Libro Favorito

**Descripción:** Permite al usuario agregar un libro a su lista de favoritos.

**Actor Principal:** Usuario  
**Actores Secundarios:** Sistema

**Precondiciones:**
- El usuario ha iniciado sesión.
- El usuario está en la página de detalles del libro.

**Postcondiciones:**
- El libro se agrega a la lista de favoritos del usuario.
- El estado del libro en la base de datos se actualiza para reflejar el estado de favorito.

**Flujo Básico (resumen):**
1. El usuario hace clic en el botón 'Agregar a Favoritos'.
2. El sistema verifica si el libro ya está en la lista de favoritos del usuario.
3. Si el libro no está en la lista, el sistema agrega el libro a la lista de favoritos del usuario.
4. El sistema muestra un mensaje de confirmación al usuario.

**Flujos Alternativos:**
- AF-1-1: El libro ya está en la lista de favoritos.

**Requisitos cubiertos:** FR-1-1

---

##### UC-9-2: Quitar Libro Favorito

**Descripción:** Permite al usuario eliminar un libro de su lista de favoritos.

**Actor Principal:** Usuario  
**Actores Secundarios:** Sistema

**Precondiciones:**
- El usuario ha iniciado sesión.
- El usuario está en la página de detalles del libro y el libro está en su lista de favoritos.

**Postcondiciones:**
- El libro se elimina de la lista de favoritos del usuario.
- El estado del libro en la base de datos se actualiza para reflejar el estado de no favorito.

**Flujo Básico (resumen):**
1. El usuario hace clic en el botón 'Quitar de Favoritos'.
2. El sistema verifica si el libro está en la lista de favoritos del usuario.
3. Si el libro está en la lista, el sistema elimina el libro de la lista de favoritos del usuario.
4. El sistema muestra un mensaje de confirmación al usuario.

**Flujos Alternativos:**
- AF-2-1: El libro no está en la lista de favoritos.

**Requisitos cubiertos:** FR-1-2

#### Criterios de Aceptación

| ID | Descripción | Escenario de Prueba | Condiciones | Tipo | Prioridad |
|----|-------------|---------------------|-------------|------|-----------|
| AC-9-1 | El usuario puede agregar un libro a su lista de favoritos. | Agregar libro al favorito | <br>• El usuario debe estar autenticado.<br>• El libro debe existir en la base de datos.<br>• El usuario debe poder seleccionar el libro para agregarlo a favoritos.<br>• El libro debe aparecer en la lista de favoritos del usuario. | Funcional | Media |
| AC-9-2 | El usuario puede quitar un libro de su lista de favoritos. | Quitar libro del favorito | <br>• El usuario debe estar autenticado.<br>• El libro debe estar presente en la lista de favoritos del usuario.<br>• El usuario debe poder seleccionar el libro para eliminarlo de favoritos.<br>• El libro debe ser removido de la lista de favoritos del usuario. | Funcional | Media |
| AC-9-3 | El sistema muestra un mensaje de confirmación al agregar un libro a favoritos. | Agregar confirmación | <br>• El libro debe ser agregado correctamente a los favoritos del usuario.<br>• Debe aparecer un mensaje visual que indique que el libro ha sido agregado a favoritos. | Funcional | Media |
| AC-9-4 | El sistema muestra un mensaje de confirmación al quitar un libro de favoritos. | Quitar confirmación | <br>• El libro debe ser removido correctamente de los favoritos del usuario.<br>• Debe aparecer un mensaje visual que indique que el libro ha sido removido de favoritos. | Funcional | Media |
| AC-9-5 | El sistema valida que el usuario esté autenticado antes de permitir agregar o quitar libros de favoritos. | Validación de autenticación | <br>• El usuario debe estar autenticado.<br>• Si el usuario no está autenticado, el sistema debe mostrar un mensaje de error indicando que el usuario debe iniciar sesión. | Validación | Media |
| AC-9-6 | La lista de favoritos del usuario se guarda correctamente en la base de datos. | Persistencia de datos | <br>• El usuario debe agregar o quitar libros de favoritos.<br>• La base de datos debe reflejar los cambios en la lista de favoritos del usuario. | Funcional | Media |

**Total de criterios:** 6

#### Requisitos Funcionales
| ID | Nombre | Descripción | Prioridad | Criterios | Rationale |
|----|--------|-------------|-----------|-----------|-----------|
| FR-001-9-1 | Agregar Libro Favorito | El sistema debe permitir a un usuario autenticado agregar un libro a su lista de favoritos. | Alta | El usuario debe poder seleccionar un libro de la lista de libros disponibles.<br>El sistema debe actualizar la lista de favoritos del usuario.<br>Una confirmación visual debe indicar que el libro ha sido agregado. | Permite al usuario guardar libros de interés. |
| FR-002-9-2 | Quitar Libro Favorito | El sistema debe permitir a un usuario autenticado quitar un libro de su lista de favoritos. | Alta | El usuario debe poder seleccionar un libro de su lista de favoritos.<br>El sistema debe eliminar el libro de la lista de favoritos del usuario.<br>Una confirmación visual debe indicar que el libro ha sido eliminado. | Permite al usuario eliminar libros de su lista de favoritos. |
| FR-003-9-3 | Ver Lista de Favoritos | El sistema debe permitir a un usuario autenticado visualizar la lista de libros que ha marcado como favoritos. | Media | El usuario debe poder ver una lista de todos los libros que ha marcado como favoritos.<br>La lista debe mostrar el nombre del libro.<br>La lista debe estar ordenada (por defecto, alfabéticamente). | Permite al usuario revisar sus libros favoritos. |


#### Requisitos No Funcionales
| ID | Nombre | Descripción | Prioridad | Criterios | Rationale |
|----|--------|-------------|-----------|-----------|-----------|
| NFR-001-9-1 | Seguridad | El sistema debe asegurar que solo los usuarios autenticados puedan agregar o quitar libros de sus favoritos. | Alta | El sistema debe requerir autenticación antes de permitir la modificación de la lista de favoritos.<br>El acceso no autenticado a la lista de favoritos debe ser denegado. | Protege la integridad de la lista de favoritos de usuarios no autorizados. |
| NFR-002-9-2 | Rendimiento | La operación de agregar y quitar libros de favoritos debe ser rápida y eficiente. | Media | El tiempo de respuesta para agregar o quitar un libro no debe exceder 2 segundos.<br>El sistema debe ser capaz de manejar un número razonable de usuarios agregando y quitando favoritos simultáneamente. | Garantiza una buena experiencia de usuario. |
| NFR-003-9-3 | Usabilidad | La interfaz de usuario para agregar y quitar favoritos debe ser intuitiva y fácil de usar. | Media | La interfaz debe ser clara y fácil de entender.<br>El proceso de agregar y quitar favoritos debe ser simple y directo.<br>El sistema debe proporcionar retroalimentación visual clara al usuario. | Facilita la interacción del usuario con la funcionalidad. |


#### Requisitos de Información
| ID | Nombre | Descripción | Prioridad | Criterios | Rationale |
|----|--------|-------------|-----------|-----------|-----------|
| IR-001-9-1 | Definición de Libro | Se debe definir la estructura de datos para un libro, incluyendo al menos nombre, autor y ISBN. | Alta | La definición debe incluir campos para almacenar información relevante sobre el libro.<br>La definición debe ser compatible con la base de datos del sistema. | Proporciona la información necesaria para representar los libros en el sistema. |
| IR-002-9-2 | Definición de Usuario | Se debe definir la estructura de datos para un usuario, incluyendo al menos nombre de usuario, contraseña y correo electrónico. | Alta | La definición debe incluir campos para almacenar información de identificación del usuario.<br>La definición debe ser compatible con el sistema de autenticación. | Proporciona la información necesaria para identificar y autenticar a los usuarios. |
| IR-003-9-3 | Estructura de la Lista de Favoritos | Se debe definir cómo se almacenan los favoritos de un usuario (por ejemplo, una tabla en la base de datos). | Media | La estructura debe permitir el almacenamiento eficiente de la lista de favoritos de un usuario.<br>La estructura debe permitir la búsqueda rápida de libros en la lista de favoritos. | Permite el almacenamiento y la gestión de la lista de favoritos del usuario. |


### Historia 10

> Listar favoritos
Como usuario autenticado,
Quiero ver mis favoritos,
Para retomar lecturas fácilmente.


#### Casos de Uso - Vista Resumen

| ID | Nombre | Actor Principal | Precondiciones | Postcondiciones | Prioridad | Complejidad | Flujos Alt |
|----|--------|-----------------|----------------|-----------------|-----------|-------------|------------|
| UC-10-01 | Ver Lista de Favoritos | Usuario Autenticado | 2 | 1 | Alta | Baja | 1 |
| UC-10-02 | Acceder a la Sección de Favoritos | Usuario Autenticado | 1 | 1 | Alta | Baja | 0 |

#### Detalle de Casos de Uso

##### UC-10-01: Ver Lista de Favoritos

**Descripción:** El usuario autenticado visualiza su lista de libros marcados como favoritos.

**Actor Principal:** Usuario Autenticado

**Precondiciones:**
- El usuario ha iniciado sesión en el sistema.
- El usuario tiene al menos un libro marcado como favorito.

**Postcondiciones:**
- La lista de favoritos del usuario se muestra en la interfaz de usuario.

**Flujo Básico (resumen):**
1. El usuario navega a la sección de 'Favoritos'.
2. El sistema recupera la lista de favoritos del usuario autenticado.
3. El sistema formatea y presenta la lista de favoritos en la interfaz de usuario, mostrando el título y, opcionalmente, la portada de cada libro.

**Flujos Alternativos:**
- AF-10-01-1: No hay favoritos

**Requisitos cubiertos:** FR-10-01-01, FR-10-02-01

---

##### UC-10-02: Acceder a la Sección de Favoritos

**Descripción:** El usuario accede a la sección de su lista de favoritos.

**Actor Principal:** Usuario Autenticado

**Precondiciones:**
- El usuario ha iniciado sesión en el sistema.

**Postcondiciones:**
- El usuario se encuentra en la sección de su lista de favoritos.

**Flujo Básico (resumen):**
1. El usuario navega a la sección de 'Favoritos' (por ejemplo, a través de un enlace en la navegación principal).

**Requisitos cubiertos:** FR-10-02-01

#### Criterios de Aceptación

| ID | Descripción | Escenario de Prueba | Condiciones | Tipo | Prioridad |
|----|-------------|---------------------|-------------|------|-----------|
| AC-10-01 | El usuario autenticado ve una lista de sus favoritos en la página principal. | Visualización de Favoritos en Página Principal | <br>• El usuario debe estar autenticado.<br>• La lista de favoritos debe estar visible en la página principal. | Funcional | Media |
| AC-10-02 | Cada elemento de la lista de favoritos muestra el título del libro. | Visualización de Títulos de Libros | <br>• La lista de favoritos debe mostrar el título de cada libro.<br>• El título debe ser legible y claro. | Funcional | Media |
| AC-10-03 | Si la portada del libro está disponible, se muestra junto al título. | Visualización de Portadas de Libros | <br>• La portada debe estar en un formato de imagen válido (ej., JPG, PNG).<br>• La portada debe estar visualmente atractiva y de tamaño adecuado. | Funcional | Media |
| AC-10-04 | La lista de favoritos se actualiza en tiempo real después de agregar un nuevo favorito. | Agregar Favorito y Ver Actualización | <br>• El usuario debe agregar un nuevo libro a sus favoritos.<br>• La lista de favoritos debe mostrar el nuevo libro inmediatamente. | Rendimiento | Media |
| AC-10-05 | La lista de favoritos no se muestra si el usuario no está autenticado. | Acceso No Autorizado | <br>• El usuario debe intentar acceder a la lista de favoritos sin estar autenticado.<br>• El usuario debe ser redirigido a la página de inicio de sesión o ver un mensaje indicando que debe estar autenticado. | Funcional | Media |
| AC-10-06 | La lista de favoritos muestra un mensaje 'No hay favoritos' si el usuario no tiene favoritos agregados. | Sin Favoritos | <br>• El usuario no debe tener ningún libro agregado a sus favoritos.<br>• La lista de favoritos debe mostrar el mensaje 'No hay favoritos'. | Funcional | Media |

**Total de criterios:** 6

#### Requisitos Funcionales
| ID | Nombre | Descripción | Prioridad | Criterios | Rationale |
|----|--------|-------------|-----------|-----------|-----------|
| FR-10-01-01 | Listar Favoritos | El sistema debe permitir al usuario autenticado visualizar una lista de los libros que ha marcado como favoritos. | Alta | La lista de favoritos se muestra en la interfaz de usuario.<br>La lista de favoritos incluye el título y, opcionalmente, la portada de cada libro.<br>La lista de favoritos se actualiza en tiempo real cuando el usuario agrega o elimina favoritos. | Permite al usuario acceder rápidamente a sus lecturas preferidas. |
| FR-10-02-01 | Acceder a la Lista de Favoritos | El sistema debe proporcionar un mecanismo para que el usuario autenticado acceda a su lista de favoritos. | Alta | El acceso a la lista de favoritos requiere que el usuario esté autenticado.<br>El acceso a la lista de favoritos se realiza a través de un enlace o botón claramente visible en la interfaz de usuario. | Garantiza que solo los usuarios autenticados puedan ver sus favoritos. |


#### Requisitos No Funcionales
| ID | Nombre | Descripción | Prioridad | Criterios | Rationale |
|----|--------|-------------|-----------|-----------|-----------|
| NFR-10-01-01 | Rendimiento | La lista de favoritos debe cargarse y mostrarse en un tiempo razonable. | Media | El tiempo de carga de la lista de favoritos no debe exceder los 3 segundos.<br>La lista de favoritos debe mostrarse de forma fluida, sin retrasos ni errores. | Una carga rápida mejora la experiencia del usuario. |


#### Requisitos de Información
| ID | Nombre | Descripción | Prioridad | Criterios | Rationale |
|----|--------|-------------|-----------|-----------|-----------|
| IR-10-01-01 | Datos de Favoritos | El sistema necesita información sobre los libros que un usuario ha marcado como favoritos. | Alta | El sistema debe almacenar la lista de favoritos para cada usuario autenticado.<br>La información de los favoritos debe incluir el título, autor, portada y, opcionalmente, la fecha de adición al favorito. | Esencial para la funcionalidad de listar favoritos. |


### Historia 11

> Crear reseña/calificación
Como usuario autenticado,
Quiero calificar y reseñar un libro,
Para compartir mi opinión.


#### Casos de Uso - Vista Resumen

| ID | Nombre | Actor Principal | Precondiciones | Postcondiciones | Prioridad | Complejidad | Flujos Alt |
|----|--------|-----------------|----------------|-----------------|-----------|-------------|------------|
| UC-11-01 | Calificar y Reseñar Libro | Usuario Autenticado | 2 | 2 | Alta | Media | 2 |

#### Detalle de Casos de Uso

##### UC-11-01: Calificar y Reseñar Libro

**Descripción:** Permite a un usuario autenticado calificar y escribir una reseña para un libro.

**Actor Principal:** Usuario Autenticado  
**Actores Secundarios:** Sistema

**Precondiciones:**
- El usuario ha iniciado sesión en el sistema.
- El usuario ha seleccionado un libro para calificar y reseñar.

**Postcondiciones:**
- La calificación y la reseña del usuario se guardan en la base de datos.
- La calificación y la reseña se muestran en la página del libro.

**Flujo Básico (resumen):**
1. El usuario navega a la página de detalles del libro.
2. El usuario selecciona una calificación de 1 a 5 estrellas.
3. El usuario escribe una reseña en el campo de texto proporcionado.
4. El usuario hace clic en el botón 'Guardar Reseña'.

**Flujos Alternativos:**
- AF-11-01-1: Calificación inválida (fuera del rango 1-5)
- AF-11-01-2: Reseña vacía

**Requisitos cubiertos:** FR-11-01, FR-11-02, FR-11-03, FR-11-04

#### Criterios de Aceptación

| ID | Descripción | Escenario de Prueba | Condiciones | Tipo | Prioridad |
|----|-------------|---------------------|-------------|------|-----------|
| AC-11-01 | El usuario puede asignar una calificación de 1 a 5 estrellas al libro. | Calificación de libro | <br>• El usuario puede seleccionar una estrella de 1 a 5.<br>• La calificación seleccionada se muestra visualmente en la interfaz.<br>• La calificación numérica (1-5) se guarda correctamente en la base de datos para el libro. | Funcional | Media |
| AC-11-02 | El usuario puede ingresar una reseña de texto de hasta 500 caracteres. | Reseña de libro | <br>• El campo de texto de la reseña permite ingresar texto.<br>• El sistema valida que la reseña no exceda los 500 caracteres.<br>• La reseña ingresada se guarda correctamente en la base de datos para el libro. | Funcional | Media |
| AC-11-03 | La calificación y la reseña se guardan correctamente después de que el usuario hace clic en el botón 'Enviar'. | Guardado de reseña | <br>• Después de hacer clic en 'Enviar', la calificación y la reseña se muestran en la página del libro.<br>• La calificación y la reseña se almacenan en la base de datos asociada al libro.<br>• No se producen errores durante el proceso de guardado. | Funcional | Media |
| AC-11-04 | El sistema muestra un mensaje de error si el usuario intenta calificar o reseñar un libro sin estar autenticado. | Acceso no autenticado | <br>• El usuario no ha iniciado sesión.<br>• El sistema muestra un mensaje de error indicando que el usuario debe iniciar sesión.<br>• El sistema redirige al usuario a la página de inicio de sesión. | Caso de error | Media |
| AC-11-05 | La calificación y la reseña se muestran correctamente en la página de detalles del libro. | Visualización de reseña | <br>• La calificación y la reseña se muestran en la página de detalles del libro.<br>• La calificación y la reseña se muestran en el formato correcto.<br>• La calificación y la reseña se muestran junto con la información del libro. | Funcional | Media |
| AC-11-06 | El sistema valida que la calificación ingresada sea un número entero entre 1 y 5. | Validación de la calificación | <br>• El usuario ingresa una calificación que no es un número entero.<br>• El sistema muestra un mensaje de error indicando que la calificación debe ser un número entero.<br>• El usuario ingresa una calificación menor que 1 o mayor que 5.<br>• El sistema muestra un mensaje de error indicando que la calificación debe estar entre 1 y 5. | Validación | Media |

**Total de criterios:** 6

#### Requisitos Funcionales
| ID | Nombre | Descripción | Prioridad | Criterios | Rationale |
|----|--------|-------------|-----------|-----------|-----------|
| FR-11-01 | Calificar Libro | El sistema debe permitir al usuario autenticado asignar una calificación numérica (por ejemplo, de 1 a 5 estrellas) a un libro. | Alta | El usuario puede seleccionar una calificación de 1 a 5.<br>La calificación se muestra correctamente al usuario.<br>La calificación se guarda en la base de datos asociada al libro. | Permite al usuario expresar su opinión sobre la calidad del libro. |
| FR-11-02 | Reseñar Libro | El sistema debe permitir al usuario autenticado escribir una reseña textual sobre un libro. | Alta | El usuario puede ingresar texto en un campo de texto.<br>El texto de la reseña se muestra correctamente al usuario.<br>La reseña se guarda en la base de datos asociada al libro. | Permite al usuario proporcionar una descripción más detallada de su opinión sobre el libro. |
| FR-11-03 | Validar Autenticación Usuario | El sistema debe verificar la autenticidad del usuario antes de permitirle calificar y reseñar un libro. | Alta | El sistema requiere un nombre de usuario y contraseña válidos.<br>El sistema rechaza el acceso si las credenciales son incorrectas.<br>El sistema registra los intentos de inicio de sesión (éxitos y fallidos). | Garantiza que solo los usuarios autenticados puedan calificar y reseñar libros, protegiendo la integridad de los datos. |
| FR-11-04 | Guardar Reseña/Calificación | El sistema debe guardar la calificación y la reseña del usuario en la base de datos, asociándolas al libro específico. | Alta | La calificación y la reseña se guardan en la base de datos.<br>La información se guarda correctamente para cada libro.<br>La información se guarda de forma segura. | Permite el almacenamiento persistente de las opiniones del usuario sobre los libros. |


#### Requisitos No Funcionales

_No se generaron requisitos._


#### Requisitos de Información

_No se generaron requisitos._


### Historia 12

> Editar/eliminar reseña propia
Como usuario autenticado,
Quiero modificar o borrar mi reseña,
Para mantener mis opiniones actualizadas.


#### Casos de Uso - Vista Resumen

| ID | Nombre | Actor Principal | Precondiciones | Postcondiciones | Prioridad | Complejidad | Flujos Alt |
|----|--------|-----------------|----------------|-----------------|-----------|-------------|------------|
| UC-12-001 | Editar Reseña Propia | Usuario Autenticado | 2 | 2 | Alta | Media | 1 |
| UC-12-002 | Eliminar Reseña Propia | Usuario Autenticado | 2 | 2 | Alta | Media | 1 |

#### Detalle de Casos de Uso

##### UC-12-001: Editar Reseña Propia

**Descripción:** Permite al usuario autenticado modificar la reseña que ha creado previamente.

**Actor Principal:** Usuario Autenticado  
**Actores Secundarios:** Sistema

**Precondiciones:**
- El usuario ha iniciado sesión en el sistema.
- El usuario tiene una reseña previamente creada asociada a su cuenta.

**Postcondiciones:**
- La reseña del usuario ha sido actualizada en la base de datos.
- La reseña modificada se muestra al usuario.

**Flujo Básico (resumen):**
1. El usuario navega a la página de detalles de su reseña.
2. El sistema muestra la reseña existente para su modificación.
3. El usuario edita el texto de la reseña.
4. El usuario confirma la modificación.
5. El sistema guarda la reseña modificada con el ID de usuario correcto.
... y 1 pasos más

**Flujos Alternativos:**
- AF-12-001-1: El usuario intenta editar una reseña que no es suya.

**Requisitos cubiertos:** FR-20231027-001-1, FR-20231027-002-1

---

##### UC-12-002: Eliminar Reseña Propia

**Descripción:** Permite al usuario autenticado eliminar la reseña que ha creado previamente.

**Actor Principal:** Usuario Autenticado  
**Actores Secundarios:** Sistema

**Precondiciones:**
- El usuario ha iniciado sesión en el sistema.
- El usuario tiene una reseña previamente creada asociada a su cuenta.

**Postcondiciones:**
- La reseña del usuario ha sido eliminada de la base de datos.
- El usuario recibe una confirmación de la eliminación.

**Flujo Básico (resumen):**
1. El usuario navega a la página de detalles de su reseña.
2. El usuario selecciona la opción de eliminar la reseña.
3. El sistema muestra un mensaje de confirmación de la eliminación (por ejemplo, '¿Está seguro de que desea eliminar esta reseña?').
4. El usuario confirma la eliminación.
5. El sistema elimina la reseña de la base de datos.
... y 1 pasos más

**Flujos Alternativos:**
- AF-12-002-1: El usuario intenta eliminar una reseña que no es suya.

**Requisitos cubiertos:** FR-20231027-001-1, FR-20231027-002-1

#### Criterios de Aceptación

| ID | Descripción | Escenario de Prueba | Condiciones | Tipo | Prioridad |
|----|-------------|---------------------|-------------|------|-----------|
| AC-12-1 | El usuario puede modificar la reseña existente, cambiando el texto de la misma. | Editar Reseña - Cambio de Texto | <br>• El usuario debe estar autenticado.<br>• La reseña que el usuario intenta modificar debe existir y estar asociada a su cuenta.<br>• El usuario debe poder editar el campo de texto de la reseña.<br>• El sistema debe guardar la reseña modificada con el ID de usuario correcto y la fecha/hora de la modificación. | Funcional | Media |
| AC-12-2 | El usuario no puede modificar la reseña si no está autenticado. | Editar Reseña - Sin Autenticación | <br>• El usuario no está autenticado.<br>• El sistema debe impedir que el usuario acceda a la funcionalidad de edición de reseñas. | Funcional | Media |
| AC-12-3 | El usuario no puede modificar la reseña de otro usuario. | Editar Reseña - Reseña de Otro Usuario | <br>• El usuario intenta modificar una reseña que no es suya.<br>• El sistema debe impedir la modificación de la reseña.<br>• El sistema debe mostrar un mensaje de error indicando que el usuario no tiene permisos para modificar esa reseña. | Funcional | Media |
| AC-12-4 | El usuario puede eliminar la reseña asociada a su cuenta. | Eliminar Reseña | <br>• El usuario debe estar autenticado.<br>• La reseña que el usuario intenta eliminar debe existir y estar asociada a su cuenta.<br>• El sistema debe mostrar un mensaje de confirmación antes de eliminar la reseña. | Funcional | Media |
| AC-12-5 | El sistema impide la eliminación de reseñas de otros usuarios. | Eliminar Reseña - Reseña de Otro Usuario | <br>• El usuario intenta eliminar una reseña que no es suya.<br>• El sistema debe impedir la eliminación de la reseña.<br>• El sistema debe mostrar un mensaje de error indicando que el usuario no tiene permisos para eliminar esa reseña. | Funcional | Media |
| AC-12-6 | El sistema valida que el texto de la reseña no exceda el límite de caracteres permitido (ej. 500 caracteres). | Editar Reseña - Texto Excesivo | <br>• El usuario intenta editar la reseña introduciendo un texto que excede el límite de caracteres.<br>• El sistema debe mostrar un mensaje de error indicando que el texto es demasiado largo. | Validación | Media |
| AC-12-7 | Después de la modificación o eliminación, la reseña correspondiente ya no se muestra en la lista de reseñas. | Estado del Sistema - Reseña Eliminada/Modificada | <br>• El usuario modifica o elimina una reseña.<br>• La reseña modificada o eliminada ya no debe ser visible en la lista de reseñas. | Funcional | Media |

**Total de criterios:** 7

#### Requisitos Funcionales
| ID | Nombre | Descripción | Prioridad | Criterios | Rationale |
|----|--------|-------------|-----------|-----------|-----------|
| FR-20231027-001-1 | Editar Reseña | El sistema debe permitir al usuario autenticado modificar la reseña que ha creado previamente. | Alta | El usuario debe poder editar el texto de la reseña.<br>El sistema debe mostrar la reseña existente para su modificación.<br>El sistema debe guardar la reseña modificada con el ID de usuario correcto. | Permite al usuario mantener la precisión de sus opiniones. |
| FR-20231027-002-1 | Eliminar Reseña | El sistema debe permitir al usuario autenticado eliminar la reseña que ha creado previamente. | Alta | El usuario debe poder eliminar la reseña asociada a su cuenta.<br>El sistema debe confirmar la eliminación de la reseña.<br>El sistema debe impedir la eliminación de reseñas de otros usuarios. | Permite al usuario eliminar reseñas obsoletas o incorrectas. |
| FR-20231027-003-1 | Verificar Autenticación | El sistema debe verificar la autenticación del usuario antes de permitir la edición o eliminación de reseñas. | Alta | El sistema debe requerir un nombre de usuario y contraseña válidos.<br>El sistema debe verificar la credencial del usuario contra la base de datos.<br>El sistema debe redirigir al usuario a la página de inicio si la autenticación falla. | Garantiza que solo los usuarios autorizados puedan modificar o eliminar reseñas. |


#### Requisitos No Funcionales

_No se generaron requisitos._


#### Requisitos de Información

_No se generaron requisitos._


### Historia 13

> Crear libros
Como administrador,
Quiero registrar libros en catálogo,
Para mantener la biblioteca actualizada.


#### Casos de Uso - Vista Resumen

| ID | Nombre | Actor Principal | Precondiciones | Postcondiciones | Prioridad | Complejidad | Flujos Alt |
|----|--------|-----------------|----------------|-----------------|-----------|-------------|------------|
| UC-13-01 | Registrar Libro - Flujo Principal | Administrador | 2 | 2 | Alta | Baja | 0 |

#### Detalle de Casos de Uso

##### UC-13-01: Registrar Libro - Flujo Principal

**Descripción:** El administrador registra un nuevo libro en el catálogo.

**Actor Principal:** Administrador

**Precondiciones:**
- El administrador ha iniciado sesión en el sistema.
- El administrador ha navegado a la sección de gestión de libros.

**Postcondiciones:**
- El libro ha sido creado en el catálogo con los datos ingresados.
- El sistema muestra un mensaje de confirmación al administrador.

**Flujo Básico (resumen):**
1. El administrador accede a la página de creación de libros.
2. El sistema muestra el formulario de registro de libros con los campos obligatorios (título, autor, ISBN, editorial, fecha de publicación, etc.).
3. El administrador completa los campos del formulario con los datos del libro.
4. El administrador verifica la información ingresada.

**Requisitos cubiertos:** FR_LIB_001, FR_LIB_002

#### Criterios de Aceptación

| ID | Descripción | Escenario de Prueba | Condiciones | Tipo | Prioridad |
|----|-------------|---------------------|-------------|------|-----------|
| AC-13-01 | El administrador puede crear un nuevo registro de libro desde el formulario. | Creación de libro desde formulario | <br>• El formulario de creación de libro se muestra correctamente.<br>• El administrador puede ingresar datos válidos en todos los campos obligatorios (título, autor, ISBN, editorial).<br>• El sistema crea un nuevo registro de libro en el catálogo. | Funcional | Media |
| AC-13-02 | El sistema valida que el título del libro no esté vacío. | Validación del campo título | <br>• Si el campo título está vacío, el sistema muestra un mensaje de error indicando que es obligatorio.<br>• Si el campo título contiene caracteres válidos, el sistema crea el registro de libro. | Validación | Media |
| AC-13-03 | El sistema valida que el ISBN tenga un formato correcto (ejemplo: 978-032176587-x). | Validación del campo ISBN | <br>• Si el campo ISBN contiene un formato incorrecto, el sistema muestra un mensaje de error indicando el formato requerido.<br>• Si el campo ISBN contiene un formato correcto, el sistema crea el registro de libro. | Validación | Media |
| AC-13-04 | El sistema crea un registro de libro en la base de datos con todos los datos ingresados. | Creación de registro en base de datos | <br>• La base de datos contiene un nuevo registro de libro con los datos ingresados por el administrador.<br>• El registro incluye todos los campos obligatorios definidos en la configuración del sistema. | Funcional | Media |
| AC-13-05 | El sistema muestra un mensaje de confirmación de que el libro ha sido creado correctamente. | Mensaje de confirmación | <br>• Después de crear el libro, el sistema muestra un mensaje de confirmación indicando que el libro ha sido creado con éxito.<br>• El mensaje de confirmación es visible durante al menos 5 segundos. | Funcional | Media |
| AC-13-06 | El sistema maneja correctamente la creación de un libro con datos inválidos (ej. ISBN incorrecto). | Manejo de datos inválidos | <br>• Si el administrador intenta crear un libro con datos inválidos, el sistema muestra mensajes de error claros y específicos para cada campo inválido.<br>• El registro de libro no se crea en la base de datos. | Funcional | Media |

**Total de criterios:** 6

#### Requisitos Funcionales
| ID | Nombre | Descripción | Prioridad | Criterios | Rationale |
|----|--------|-------------|-----------|-----------|-----------|
| FR_LIB_001 | Registrar Libro | El sistema debe permitir al administrador registrar nuevos libros en el catálogo. | Alta | El sistema debe mostrar un formulario para ingresar los detalles del libro (título, autor, ISBN, editorial, etc.).<br>El sistema debe validar los datos ingresados para asegurar la integridad de la información.<br>El sistema debe crear un nuevo registro de libro en el catálogo con los datos ingresados. | Esta es la funcionalidad principal de la historia. |
| FR_LIB_002 | Crear Registro de Libro | El sistema debe crear un nuevo registro de libro en el catálogo. | Alta | El sistema debe crear un nuevo registro en la base de datos para el libro.<br>El registro debe incluir todos los campos obligatorios definidos en la configuración del sistema. | Es el resultado de la acción de registrar un libro. |


#### Requisitos No Funcionales
| ID | Nombre | Descripción | Prioridad | Criterios | Rationale |
|----|--------|-------------|-----------|-----------|-----------|
| NFR_LIB_PERF_001 | Rendimiento | El sistema debe registrar un libro en el catálogo en menos de 5 segundos. | Alta | El tiempo de registro de un libro no debe exceder los 5 segundos en condiciones normales de carga.<br>El tiempo de registro debe ser medible a través de pruebas de rendimiento. | Un registro rápido mejora la experiencia del usuario. |


#### Requisitos de Información
| ID | Nombre | Descripción | Prioridad | Criterios | Rationale |
|----|--------|-------------|-----------|-----------|-----------|
| IR_LIB_DATA_001 | Datos del Libro | El sistema necesita información sobre los libros para registrarse en el catálogo. | Alta | El sistema debe requerir al menos el título, autor, ISBN y editorial del libro.<br>El sistema debe permitir la entrada de campos adicionales como género, número de páginas, etc. | La información es necesaria para crear el registro del libro. |


### Historia 14

> Editar/eliminar libros
Como administrador,
Quiero modificar o desactivar libros,
Para asegurar calidad del catálogo.


#### Casos de Uso - Vista Resumen

| ID | Nombre | Actor Principal | Precondiciones | Postcondiciones | Prioridad | Complejidad | Flujos Alt |
|----|--------|-----------------|----------------|-----------------|-----------|-------------|------------|
| UC-14-001 | Editar Detalles del Libro | Administrador | 2 | 2 | Alta | Media | 2 |
| UC-14-002 | Desactivar Libro | Administrador | 2 | 2 | Alta | Baja | 1 |

#### Detalle de Casos de Uso

##### UC-14-001: Editar Detalles del Libro

**Descripción:** Permite al administrador modificar la información de un libro existente en el catálogo.

**Actor Principal:** Administrador

**Precondiciones:**
- El administrador ha iniciado sesión en el sistema.
- El administrador ha localizado el libro que desea editar en el catálogo.

**Postcondiciones:**
- La información del libro en la base de datos ha sido actualizada.
- El libro se muestra con la información actualizada en el catálogo.

**Flujo Básico (resumen):**
1. El administrador selecciona el libro a editar desde el catálogo.
2. El sistema muestra una página de edición con los campos del libro (título, autor, ISBN, descripción, portada).
3. El administrador modifica los campos deseados.
4. El administrador confirma los cambios.
5. El sistema valida los datos modificados.
... y 2 pasos más

**Flujos Alternativos:**
- AF-14-001-01: Campo obligatorio no completado
- AF-14-001-02: Formato de datos inválido

**Requisitos cubiertos:** FR_14_001_001

---

##### UC-14-002: Desactivar Libro

**Descripción:** Permite al administrador deshabilitar un libro, haciéndolo inactivo para la venta y el préstamo.

**Actor Principal:** Administrador

**Precondiciones:**
- El administrador ha iniciado sesión en el sistema.
- El administrador ha localizado el libro que desea desactivar en el catálogo.

**Postcondiciones:**
- El libro se marca como inactivo en la base de datos.
- El libro no se muestra en la búsqueda o en la lista de libros disponibles.

**Flujo Básico (resumen):**
1. El administrador selecciona el libro a desactivar desde el catálogo.
2. El sistema presenta una opción para desactivar el libro (ej., un botón 'Desactivar').
3. El administrador confirma la desactivación.
4. El sistema actualiza el estado del libro en la base de datos a 'inactivo'.
5. El sistema actualiza la visualización del libro en el catálogo para ocultarlo.

**Flujos Alternativos:**
- AF-14-002-01: No se permite desactivar si el libro está en stock

**Requisitos cubiertos:** FR_14_002_001

#### Criterios de Aceptación

| ID | Descripción | Escenario de Prueba | Condiciones | Tipo | Prioridad |
|----|-------------|---------------------|-------------|------|-----------|
| AC-14-001 | El administrador puede modificar con éxito el título, autor, ISBN, descripción y portada de un libro existente. | Modificación de detalles del libro | <br>• El administrador inicia sesión correctamente.<br>• El administrador selecciona un libro existente para editar.<br>• El administrador modifica cada uno de los campos (título, autor, ISBN, descripción, portada).<br>• El sistema guarda correctamente los cambios en la base de datos.<br>• Después de la modificación, los detalles del libro se muestran correctamente con los nuevos valores. | Funcional | Media |
| AC-14-002 | El administrador no puede modificar los campos obligatorios de un libro sin proporcionar un valor válido. | Validación de entrada de campos obligatorios | <br>• El administrador inicia sesión correctamente.<br>• El administrador selecciona un libro existente para editar.<br>• El administrador intenta modificar un campo obligatorio (por ejemplo, título) dejando el campo vacío.<br>• El sistema muestra un mensaje de error claro indicando que el campo es obligatorio. | Funcional | Media |
| AC-14-003 | El administrador puede desactivar un libro, lo que impide que se muestre en la búsqueda y no se pueda comprar o prestar. | Desactivación de un libro | <br>• El administrador inicia sesión correctamente.<br>• El administrador selecciona un libro existente para desactivar.<br>• El sistema actualiza el estado del libro a 'inactivo' en la base de datos.<br>• El libro ya no aparece en los resultados de búsqueda.<br>• El libro ya no está disponible para compra o préstamo. | Funcional | Media |
| AC-14-004 | El administrador puede volver a activar un libro desactivado, restaurando su visibilidad y disponibilidad. | Reactivación de un libro | <br>• El administrador inicia sesión correctamente.<br>• El administrador selecciona un libro previamente desactivado.<br>• El sistema actualiza el estado del libro a 'activo' en la base de datos.<br>• El libro vuelve a aparecer en los resultados de búsqueda.<br>• El libro vuelve a estar disponible para compra o préstamo. | Funcional | Media |
| AC-14-005 | El sistema muestra un mensaje de confirmación después de que el administrador modifica o desactiva un libro. | Confirmación de la acción | <br>• El administrador realiza una acción de modificación o desactivación de un libro.<br>• El sistema muestra un mensaje de confirmación indicando que la acción se ha realizado correctamente. | Funcional | Media |
| AC-14-006 | El sistema maneja correctamente los errores al intentar modificar un libro que no existe. | Manejo de errores - Libro inexistente | <br>• El administrador intenta modificar un libro que no existe en la base de datos.<br>• El sistema muestra un mensaje de error claro indicando que el libro no se encontró. | Caso de error | Media |

**Total de criterios:** 6

#### Requisitos Funcionales
| ID | Nombre | Descripción | Prioridad | Criterios | Rationale |
|----|--------|-------------|-----------|-----------|-----------|
| FR_14_001_001 | Editar Libro | El sistema debe permitir al administrador modificar los detalles de un libro existente, incluyendo título, autor, ISBN, descripción, y portada. | Alta | El administrador puede modificar el título del libro.<br>El administrador puede modificar el autor del libro.<br>El administrador puede modificar el ISBN del libro.<br>El administrador puede modificar la descripción del libro.<br>El administrador puede modificar la portada del libro. | Permite mantener la información del catálogo actualizada y precisa. |
| FR_14_002_001 | Desactivar Libro | El sistema debe permitir al administrador desactivar un libro, haciéndolo visible para los usuarios pero no disponible para la compra o préstamo. | Alta | El administrador puede desactivar un libro.<br>La desactivación de un libro impide que se muestre en la búsqueda.<br>La desactivación de un libro impide que se pueda comprar o prestar. | Permite ocultar libros que ya no están disponibles o que requieren mantenimiento. |


#### Requisitos No Funcionales
| ID | Nombre | Descripción | Prioridad | Criterios | Rationale |
|----|--------|-------------|-----------|-----------|-----------|
| NFR_14_001_001 | Rendimiento - Edición | La operación de edición de un libro no debe exceder los 3 segundos en condiciones normales de carga. | Alta | El tiempo de respuesta para la edición de un libro no supera los 3 segundos en un entorno con 100 usuarios concurrentes.<br>El tiempo de respuesta para la edición de un libro no supera los 3 segundos en un entorno de prueba con 10 usuarios concurrentes. | Garantiza una experiencia de usuario fluida y eficiente. |
| NFR_14_002_001 | Seguridad - Acceso | El acceso a la funcionalidad de edición y desactivación de libros debe estar restringido únicamente a usuarios con el rol de 'Administrador'. | Alta | Solo usuarios con el rol de 'Administrador' pueden acceder a la funcionalidad de edición y desactivación de libros.<br>El sistema registra todos los intentos de acceso no autorizados. | Protege la integridad del catálogo y evita modificaciones no autorizadas. |


#### Requisitos de Información
| ID | Nombre | Descripción | Prioridad | Criterios | Rationale |
|----|--------|-------------|-----------|-----------|-----------|
| IR_14_001_001 | Detalles del Libro | El sistema debe almacenar información detallada sobre cada libro, incluyendo título, autor, ISBN, descripción, portada, estado (activo/inactivo), y categoría. | Alta | El sistema almacena el título del libro.<br>El sistema almacena el nombre del autor del libro.<br>El sistema almacena el ISBN del libro.<br>El sistema almacena la descripción del libro.<br>El sistema almacena la URL de la portada del libro.<br>El sistema almacena el estado del libro (activo/inactivo).<br>El sistema almacena la categoría del libro. | Proporciona la información necesaria para gestionar el catálogo de libros. |


### Historia 15

> Gestionar préstamos en panel
Como administrador/bibliotecario,
Quiero supervisar estados y operaciones,
Para controlar el flujo de préstamos.


#### Casos de Uso - Vista Resumen

| ID | Nombre | Actor Principal | Precondiciones | Postcondiciones | Prioridad | Complejidad | Flujos Alt |
|----|--------|-----------------|----------------|-----------------|-----------|-------------|------------|
| UC-15-01 | Supervisar Estados de Préstamo | Administrador/Biblio... | 2 | 2 | Alta | Baja | 1 |
| UC-15-02 | Supervisar Operaciones de Préstamo | Administrador/Biblio... | 2 | 2 | Media | Baja | 1 |

#### Detalle de Casos de Uso

##### UC-15-01: Supervisar Estados de Préstamo

**Descripción:** Permite al administrador/bibliotecario visualizar el estado actual de cada préstamo registrado en el sistema.

**Actor Principal:** Administrador/Bibliotecario

**Precondiciones:**
- El administrador/bibliotecario ha iniciado sesión en el panel.
- El sistema ha cargado los datos de préstamos.

**Postcondiciones:**
- La pantalla muestra una lista de todos los préstamos con su estado actual.
- El estado de cada préstamo está claramente indicado.

**Flujo Básico (resumen):**
1. El administrador/bibliotecario navega a la sección de 'Gestión de Préstamos'.
2. El sistema muestra una tabla con los detalles de cada préstamo (ID, Título, Usuario, Fecha de Emisión, Estado).
3. El administrador/bibliotecario puede filtrar la tabla por diferentes criterios (e.g., fecha de emisión, título).
4. El sistema actualiza la tabla en tiempo real para reflejar cualquier cambio en el estado de los préstamos.

**Flujos Alternativos:**
- AF-15-01-1: No hay préstamos activos.

**Requisitos cubiertos:** FR_001-15-01

---

##### UC-15-02: Supervisar Operaciones de Préstamo

**Descripción:** Permite al administrador/bibliotecario visualizar el historial de operaciones realizadas en cada préstamo.

**Actor Principal:** Administrador/Bibliotecario

**Precondiciones:**
- El administrador/bibliotecario ha iniciado sesión en el panel.
- El sistema ha cargado los datos de préstamos.

**Postcondiciones:**
- La pantalla muestra una lista de todas las operaciones realizadas en cada préstamo, ordenadas cronológicamente.
- Cada operación incluye la fecha, hora, tipo de operación y usuario que la realizó.

**Flujo Básico (resumen):**
1. El administrador/bibliotecario navega a la sección de 'Gestión de Préstamos'.
2. El administrador/bibliotecario selecciona un préstamo específico de la lista.
3. El sistema muestra una tabla con el historial de operaciones del préstamo seleccionado (ID, Tipo de Operación, Fecha, Hora, Usuario).
4. El administrador/bibliotecario puede filtrar la tabla por fecha y tipo de operación.

**Flujos Alternativos:**
- AF-15-02-1: No hay operaciones registradas para un préstamo.

**Requisitos cubiertos:** FR_002-15-02

#### Criterios de Aceptación

| ID | Descripción | Escenario de Prueba | Condiciones | Tipo | Prioridad |
|----|-------------|---------------------|-------------|------|-----------|
| AC-15-01 | El sistema muestra el estado actual de cada préstamo de forma precisa, reflejando el estado almacenado en la base de datos. | Visualización del estado del préstamo | <br>• El estado del préstamo en el panel es 'Disponible' para un préstamo sin ser devuelto.<br>• El estado del préstamo en el panel es 'Devuelto' para un préstamo con estado 'Devuelto' en la base de datos.<br>• El estado del préstamo en el panel es 'Vencido' para un préstamo con estado 'Vencido' en la base de datos. | Funcional | Media |
| AC-15-02 | El sistema muestra un registro de todas las operaciones realizadas en un préstamo, incluyendo la fecha y hora de cada operación. | Registro de operaciones del préstamo | <br>• Se puede ver un registro de 'Préstamo Emitido' para un préstamo recién emitido.<br>• Se puede ver un registro de 'Devolución' para un préstamo devuelto.<br>• El registro de operaciones incluye la fecha y hora exactas de cada operación. | Funcional | Media |
| AC-15-03 | El sistema permite filtrar el registro de operaciones por ID de préstamo y rango de fechas. | Filtrado de operaciones del préstamo | <br>• Se puede filtrar el registro de operaciones para mostrar solo las operaciones realizadas en un préstamo específico.<br>• Se puede filtrar el registro de operaciones para mostrar las operaciones realizadas dentro de un rango de fechas específico. | Funcional | Media |
| AC-15-04 | El sistema muestra un mensaje de error si se intenta modificar un préstamo que ya ha sido devuelto. | Intento de modificación de préstamo devuelto | <br>• Al intentar modificar un préstamo con estado 'Devuelto', el sistema muestra un mensaje de error indicando que no se puede modificar el préstamo. | Caso de error | Media |
| AC-15-05 | El sistema muestra un mensaje de error si se intenta realizar una operación no permitida para un préstamo en un estado no compatible. | Intento de operación no permitida | <br>• Al intentar emitir un préstamo que ya está prestado, el sistema muestra un mensaje de error indicando que el préstamo ya está en uso. | Caso de error | Media |
| AC-15-06 | El sistema carga la lista de estados de préstamo en menos de 3 segundos. | Carga de estados de préstamo | <br>• El tiempo de carga de la lista de estados de préstamo no excede los 3 segundos. | Funcional | Media |
| AC-15-07 | El sistema muestra un mensaje de error si el ID de préstamo ingresado no existe. | ID de préstamo inválido | <br>• Al ingresar un ID de préstamo que no existe en la base de datos, el sistema muestra un mensaje de error indicando que el ID no es válido. | Caso de error | Media |

**Total de criterios:** 7

#### Requisitos Funcionales
| ID | Nombre | Descripción | Prioridad | Criterios | Rationale |
|----|--------|-------------|-----------|-----------|-----------|
| FR_001-15-01 | Display Loan Status | The system shall display the current status of each loan (e.g., 'Checked Out', 'Returned', 'Overdue'). | Alta | The system accurately reflects the loan status as recorded in the database.<br>The status is displayed in a clear and understandable format. | Essential for monitoring loan activity. |
| FR_002-15-02 | Display Loan Operations | The system shall display a log of all operations performed on a loan (e.g., 'Loan Issued', 'Loan Returned', 'Fine Charged'). | Media | The system records all loan operations with timestamps.<br>The system allows filtering of operations by loan ID and date range. | Provides an audit trail for loan transactions. |


#### Requisitos No Funcionales
| ID | Nombre | Descripción | Prioridad | Criterios | Rationale |
|----|--------|-------------|-----------|-----------|-----------|
| NFR_001-15-03 | System Response Time | The system shall respond to requests for loan status and operation data within 2 seconds. | Alta | Measured response time for retrieving loan data under normal load.<br>Response time remains within the specified limit during peak usage. | Ensures a responsive user experience. |
| NFR_002-15-04 | Data Security | The system shall protect loan data from unauthorized access and modification. | Alta | Access to loan data is restricted to authorized administrators and librarians.<br>Data is encrypted both in transit and at rest. | Protects sensitive borrower information. |


#### Requisitos de Información
| ID | Nombre | Descripción | Prioridad | Criterios | Rationale |
|----|--------|-------------|-----------|-----------|-----------|
| IR_001-15-05 | Loan Status Codes | The system shall utilize a defined set of loan status codes. | Media | A documented list of loan status codes and their corresponding meanings is maintained.<br>The system consistently uses the defined codes. | Ensures consistency and clarity in loan status reporting. |
| IR_002-15-06 | Operation Log Fields | The system shall log the following fields for each loan operation: Loan ID, User ID, Timestamp, Operation Type, Description. | Alta | All specified fields are recorded for each operation.<br>Data types for each field are clearly defined. | Provides comprehensive information for auditing and analysis. |


### Historia 16

> Moderar reseñas
Como administrador,
Quiero ocultar reseñas inapropiadas,
Para mantener calidad de la comunidad.


#### Casos de Uso - Vista Resumen

| ID | Nombre | Actor Principal | Precondiciones | Postcondiciones | Prioridad | Complejidad | Flujos Alt |
|----|--------|-----------------|----------------|-----------------|-----------|-------------|------------|
| UC-16-01 | Ocultar Reseña Inapropiada por el Admini... | Administrador | 3 | 2 | Alta | Baja | 1 |
| UC-16-02 | Revisión de Reseñas Marcadas como Inapro... | Administrador | 2 | 2 | Alta | Baja | 1 |

#### Detalle de Casos de Uso

##### UC-16-01: Ocultar Reseña Inapropiada por el Administrador

**Descripción:** Permite a un administrador ocultar una reseña marcada como inapropiada, eliminándola de la vista pública.

**Actor Principal:** Administrador  
**Actores Secundarios:** Sistema

**Precondiciones:**
- El administrador ha iniciado sesión en el sistema.
- El sistema ha identificado una reseña como inapropiada (basado en FR_16_02).
- El administrador está visualizando la lista de reseñas marcadas como inapropiadas.

**Postcondiciones:**
- La reseña marcada como inapropiada ya no es visible para los usuarios.
- El estado de la reseña en la base de datos se actualiza para indicar que está oculta.

**Flujo Básico (resumen):**
1. El administrador selecciona la reseña marcada como inapropiada de la lista.
2. El sistema presenta una confirmación para ocultar la reseña.
3. El administrador confirma la acción de ocultar.
4. El sistema oculta la reseña, actualizando su estado y eliminándola de la vista pública.

**Flujos Alternativos:**
- AF-16-01-1: Administrador cancela la ocultación

**Requisitos cubiertos:** FR_16_01, FR_16_02

---

##### UC-16-02: Revisión de Reseñas Marcadas como Inapropiadas

**Descripción:** Permite al administrador revisar las reseñas que el sistema ha identificado como inapropiadas para confirmar si la clasificación es correcta.

**Actor Principal:** Administrador  
**Actores Secundarios:** Sistema

**Precondiciones:**
- El administrador ha iniciado sesión en el sistema.
- El sistema ha marcado una o más reseñas como inapropiadas.

**Postcondiciones:**
- El administrador tiene una visión clara de las reseñas marcadas como inapropiadas.
- El administrador puede decidir si la reseña debe permanecer oculta o no.

**Flujo Básico (resumen):**
1. El administrador accede a la vista de reseñas marcadas como inapropiadas.
2. El sistema presenta una lista de las reseñas marcadas, mostrando el contenido de la reseña y la razón por la que fue marcada como inapropiada.
3. El administrador examina la reseña.
4. El administrador decide si la reseña es realmente inapropiada o no.

**Flujos Alternativos:**
- AF-16-02-1: No hay reseñas marcadas

**Requisitos cubiertos:** FR_16_02

#### Criterios de Aceptación

| ID | Descripción | Escenario de Prueba | Condiciones | Tipo | Prioridad |
|----|-------------|---------------------|-------------|------|-----------|
| AC-16-01 | Un administrador puede ocultar una reseña marcada como inapropiada desde la lista de reseñas. | Ocultar Reseña Inapropiada - Flujo Principal | <br>• El administrador está autenticado y tiene permisos de moderación.<br>• La lista de reseñas muestra reseñas marcadas como 'inapropiada' (basado en el sistema de identificación).<br>• El administrador selecciona una reseña marcada como 'inapropiada' de la lista. | Funcional | Media |
| AC-16-02 | Después de ocultar una reseña, la reseña no es visible para los usuarios regulares. | Ocultar Reseña Inapropiada - Verificación de Visibilidad | <br>• El administrador ha ocultado una reseña.<br>• Un usuario regular accede a la página de reseñas.<br>• La reseña previamente visible ya no está presente en la lista de reseñas. | Funcional | Media |
| AC-16-03 | El sistema informa al administrador que la reseña ha sido ocultada. | Ocultar Reseña Inapropiada - Confirmación del Sistema | <br>• El administrador ha ocultado una reseña.<br>• El sistema muestra un mensaje de confirmación indicando que la reseña ha sido ocultada (ej. 'Reseña X ocultada'). | Funcional | Media |
| AC-16-04 | El sistema no permite ocultar reseñas que no han sido marcadas como inapropiadas. | Ocultar Reseña Inapropiada - Caso Límite - Reseña No Marcada | <br>• El administrador está autenticado y tiene permisos de moderación.<br>• La lista de reseñas muestra reseñas que no han sido marcadas como 'inapropiada'. | Funcional | Media |
| AC-16-05 | El sistema valida que el administrador ingresa un ID de reseña válido al intentar ocultar una reseña. | Ocultar Reseña Inapropiada - Validación de Entrada | <br>• El administrador intenta ocultar una reseña ingresando un ID de reseña que no existe.<br>• El sistema muestra un mensaje de error indicando que el ID de reseña es inválido. | Validación | Media |
| AC-16-06 | El sistema registra la acción de ocultar una reseña (ej. en un log de auditoría). | Ocultar Reseña Inapropiada - Estado del Sistema | <br>• El administrador ha ocultado una reseña.<br>• Se crea un registro en el log de auditoría que documenta la acción de ocultar la reseña, incluyendo el ID de la reseña, el nombre del administrador y la hora de la acción. | Funcional | Media |

**Total de criterios:** 6

#### Requisitos Funcionales
| ID | Nombre | Descripción | Prioridad | Criterios | Rationale |
|----|--------|-------------|-----------|-----------|-----------|
| FR_16_01 | Ocultar Reseña Inapropiada | El sistema debe permitir a un administrador ocultar una reseña identificada como inapropiada. | Alta | Un administrador puede seleccionar una reseña de una lista de reseñas.<br>Al seleccionar una reseña, el sistema presenta una opción para ocultarla.<br>Al confirmar la acción de ocultar, la reseña ya no es visible para otros usuarios. | Aborda directamente el objetivo de la historia de usuario de eliminar contenido inapropiado. |
| FR_16_02 | Identificar Reseña Inapropiada | El sistema debe identificar reseñas que violen las directrices de la comunidad. | Alta | El sistema debe utilizar un conjunto predefinido de reglas o algoritmos para evaluar el contenido de la reseña.<br>El sistema debe marcar reseñas que excedan un umbral definido de violaciones (por ejemplo, lenguaje ofensivo, discurso de odio, spam).<br>El sistema debe proporcionar un mecanismo para que los administradores revisen las reseñas marcadas. | Es un requisito previo necesario para que el administrador oculte la reseña. |


#### Requisitos No Funcionales
| ID | Nombre | Descripción | Prioridad | Criterios | Rationale |
|----|--------|-------------|-----------|-----------|-----------|
| NFR_16_01 | Tiempo de Respuesta | El sistema debe ocultar una reseña inapropiada en 5 segundos después de la acción del administrador. | Alta | Medir el tiempo transcurrido desde que el administrador inicia la acción de ocultar hasta que la reseña se elimina de la pantalla.<br>Objetivo: 5 segundos (percentil 95). | Un tiempo de respuesta rápido garantiza una moderación eficiente. |
| NFR_16_02 | Registro de Auditoría | El sistema debe registrar todas las acciones relacionadas con la moderación de reseñas, incluida la ocultación y la identificación. | Media | Todas las acciones realizadas por los administradores en las reseñas deben registrarse en un registro de auditoría.<br>El registro debe incluir el ID del administrador, el ID de la reseña, la marca de tiempo y la acción realizada (ocultar/marcar). | Proporciona responsabilidad y facilita la investigación. |


#### Requisitos de Información
| ID | Nombre | Descripción | Prioridad | Criterios | Rationale |
|----|--------|-------------|-----------|-----------|-----------|
| IR_16_01 | Directrices de la Comunidad | El sistema debe utilizar un conjunto documentado de directrices de la comunidad para identificar reseñas inapropiadas. | Alta | Las directrices deben ser fácilmente accesibles para los administradores.<br>Las directrices deben definir claramente qué constituye una reseña inapropiada (por ejemplo, palabras clave específicas, tipos de contenido).<br>Las directrices deben revisarse y actualizarse regularmente. | Proporciona la base para las reglas de identificación del sistema. |
| IR_16_02 | Contenido de la Reseña | El sistema debe almacenar el contenido de la reseña en un formato adecuado para el análisis y la identificación. | Media | El contenido de la reseña debe almacenarse en un formato buscable.<br>El formato debe permitir búsquedas y coincidencias de patrones eficientes de palabras clave. | Esencial para el proceso de identificación. |


### Historia 17

> Seguridad JWT
Como equipo técnico,
Quiero proteger endpoints con JWT,
Para manejar autenticación stateless.


#### Casos de Uso - Vista Resumen

| ID | Nombre | Actor Principal | Precondiciones | Postcondiciones | Prioridad | Complejidad | Flujos Alt |
|----|--------|-----------------|----------------|-----------------|-----------|-------------|------------|
| UC-17-001 | Acceder a un Endpoint Protegido con JWT | Usuario Autenticado | 2 | 2 | Alta | Media | 2 |
| UC-17-002 | Manejo de Error JWT Inválido | Sistema | 1 | 2 | Alta | Baja | 0 |

#### Detalle de Casos de Uso

##### UC-17-001: Acceder a un Endpoint Protegido con JWT

**Descripción:** Permite a un usuario autenticado acceder a un endpoint protegido mediante la presentación de un JWT válido.

**Actor Principal:** Usuario Autenticado  
**Actores Secundarios:** Sistema, Servicio de Autenticación

**Precondiciones:**
- El usuario ha iniciado sesión y ha recibido un JWT.
- El JWT es válido y no ha expirado.

**Postcondiciones:**
- El sistema autentica al usuario.
- El sistema otorga acceso al endpoint protegido.

**Flujo Básico (resumen):**
1. El usuario envía una solicitud HTTP al endpoint protegido.
2. La solicitud incluye el encabezado `Authorization` con el JWT.
3. El sistema recibe la solicitud.
4. El sistema valida el JWT (firma, expiración, afirmaciones).
5. Si el JWT es válido, el sistema autentica al usuario utilizando la información del JWT.
... y 1 pasos más

**Flujos Alternativos:**
- AF-17-001-1: JWT Inválido
- AF-17-001-2: Encabezado Authorization Faltante

**Requisitos cubiertos:** FR-JWT-001, FR-JWT-002, FR-JWT-003

---

##### UC-17-002: Manejo de Error JWT Inválido

**Descripción:** El sistema maneja la situación cuando un JWT no es válido.

**Actor Principal:** Sistema

**Precondiciones:**
- El sistema ha recibido una solicitud HTTP con un JWT.

**Postcondiciones:**
- El sistema ha rechazado la solicitud.
- El sistema ha registrado el evento de error.

**Flujo Básico (resumen):**
1. El sistema recibe una solicitud HTTP con un JWT.
2. El sistema valida el JWT.
3. Si el JWT es inválido, el sistema devuelve una respuesta HTTP con un código de estado 401 Unauthorized.
4. El sistema registra el evento de error (JWT inválido).

**Requisitos cubiertos:** FR-JWT-003

#### Criterios de Aceptación

| ID | Descripción | Escenario de Prueba | Condiciones | Tipo | Prioridad |
|----|-------------|---------------------|-------------|------|-----------|
| AC-17-001 | Un usuario autenticado con un JWT válido puede acceder al endpoint protegido '/users' y recibir una lista de usuarios. | Acceso a endpoint protegido con JWT válido | <br>• El JWT proporcionado en el encabezado 'Authorization' es válido y firmado.<br>• El usuario asociado al JWT tiene permisos para acceder al endpoint '/users'. | Funcional | Media |
| AC-17-002 | El sistema devuelve un código de estado HTTP 401 Unauthorized cuando se presenta un JWT inválido (firma incorrecta o expirado) en el encabezado 'Authorization'. | Validación de JWT inválido | <br>• El JWT proporcionado es incorrecto (firma inválida o expirado). | Funcional | Media |
| AC-17-003 | El sistema devuelve un código de estado HTTP 401 Unauthorized cuando la solicitud a un endpoint protegido no incluye el encabezado 'Authorization'. | Solicitud sin JWT | <br>• La solicitud a un endpoint protegido no incluye el encabezado 'Authorization'. | Funcional | Media |
| AC-17-004 | Tras una validación exitosa del JWT, el sistema recupera la información del usuario de la base de datos utilizando el ID de usuario presente en el JWT. | Recuperación de información del usuario | <br>• El JWT es válido y contiene un ID de usuario.<br>• El ID de usuario existe en la base de datos. | Validación | Media |
| AC-17-005 | El sistema registra cada intento de acceso a un endpoint protegido con un JWT inválido en el registro de eventos, incluyendo la dirección IP, el JWT y la hora del intento. | Registro de intentos de JWT inválidos | <br>• El JWT proporcionado es inválido. | Seguridad | Media |
| AC-17-006 | El sistema establece una sesión para el usuario autenticado después de una validación exitosa del JWT. | Creación de sesión | <br>• El JWT es válido. | Validación | Media |
| AC-17-007 | El sistema valida que el JWT contiene las afirmaciones esperadas (por ejemplo, ID de usuario, roles) y que estas afirmaciones coinciden con los datos almacenados en la base de datos. | Validación de afirmaciones JWT | <br>• El JWT contiene las afirmaciones esperadas.<br>• Las afirmaciones del JWT coinciden con los datos del usuario en la base de datos. | Validación | Media |

**Total de criterios:** 7

#### Requisitos Funcionales
| ID | Nombre | Descripción | Prioridad | Criterios | Rationale |
|----|--------|-------------|-----------|-----------|-----------|
| FR-JWT-001 | Validación de JWT | El sistema debe validar los JWTs presentados en las solicitudes a los endpoints protegidos. | Alta | El JWT debe estar presente en el encabezado de la solicitud.<br>El JWT debe estar firmado y ser válido.<br>Las afirmaciones del JWT (p. ej., ID de usuario, roles) deben estar presentes. | Asegura que solo los usuarios autorizados puedan acceder a los recursos protegidos. |
| FR-JWT-002 | Autenticación de Usuario | El sistema debe autenticar a los usuarios en función de la información contenida en un JWT válido. | Alta | Tras una validación exitosa del JWT, el sistema debe recuperar la información del usuario de la base de datos.<br>El sistema debe establecer una sesión para el usuario autenticado. | Proporciona autenticación sin estado basada en el JWT. |
| FR-JWT-003 | Manejo de JWTs Inválidos | El sistema debe rechazar las solicitudes con JWTs inválidos o faltantes. | Alta | El sistema debe devolver un error 401 Unauthorized para JWTs inválidos.<br>El sistema debe registrar los intentos de JWT inválidos. | Previene el acceso no autorizado y proporciona registro para la supervisión de seguridad. |


#### Requisitos No Funcionales

_No se generaron requisitos._


#### Requisitos de Información

_No se generaron requisitos._


### Historia 18

> Control por roles
Como equipo técnico,
Quiero restringir endpoints administrativos,
Para evitar accesos no autorizados.


#### Casos de Uso - Vista Resumen

| ID | Nombre | Actor Principal | Precondiciones | Postcondiciones | Prioridad | Complejidad | Flujos Alt |
|----|--------|-----------------|----------------|-----------------|-----------|-------------|------------|
| UC-18-01 | Restricción de Acceso Administrativo Bas... | Usuario (con rol no ... | 3 | 2 | Alta | Baja | 1 |
| UC-18-02 | Validación de Roles para Endpoints Admin... | Sistema | 3 | 2 | Alta | Media | 1 |
| UC-18-03 | Definición y Mantenimiento de Endpoints ... | Administrador del Si... | 2 | 2 | Media | Baja | 1 |

#### Detalle de Casos de Uso

##### UC-18-01: Restricción de Acceso Administrativo Basada en Roles

**Descripción:** Verifica que un usuario con un rol no autorizado no pueda acceder a un endpoint administrativo.

**Actor Principal:** Usuario (con rol no autorizado)  
**Actores Secundarios:** Sistema, Administrador del Sistema

**Precondiciones:**
- El usuario está autenticado en el sistema.
- El usuario tiene asignado un rol que no incluye permisos para el endpoint administrativo específico.
- El endpoint administrativo está definido como un endpoint administrativo.

**Postcondiciones:**
- El usuario es redirigido a una página de error o está bloqueado del acceso al endpoint.
- El evento de intento de acceso no autorizado se registra en el sistema de registro.

**Flujo Básico (resumen):**
1. El usuario intenta acceder al endpoint administrativo.
2. El sistema verifica el rol del usuario.
3. El sistema valida si el rol del usuario tiene permisos para acceder al endpoint.
4. Si el rol no tiene permisos, el sistema deniega el acceso y registra el evento.

**Flujos Alternativos:**
- AF-18-01-1: Permisos explícitos para el rol

**Requisitos cubiertos:** FR-18-01, FR-18-02

---

##### UC-18-02: Validación de Roles para Endpoints Administrativos

**Descripción:** Verifica que el sistema valide correctamente los roles del usuario contra los permisos definidos para cada endpoint administrativo.

**Actor Principal:** Sistema  
**Actores Secundarios:** Usuario (con rol asignado), Administrador del Sistema

**Precondiciones:**
- El usuario está autenticado en el sistema.
- El usuario tiene asignado un rol específico.
- El endpoint administrativo está definido y asociado a un rol específico.

**Postcondiciones:**
- El sistema permite el acceso al endpoint si el rol del usuario tiene los permisos necesarios.
- El sistema deniega el acceso si el rol no tiene los permisos necesarios.

**Flujo Básico (resumen):**
1. El usuario intenta acceder al endpoint administrativo.
2. El sistema determina el rol del usuario.
3. El sistema consulta la definición del endpoint para determinar los permisos requeridos.
4. El sistema compara el rol del usuario con los permisos requeridos.
5. Si hay coincidencia, el sistema permite el acceso.
... y 1 pasos más

**Flujos Alternativos:**
- AF-18-02-1: Rol desconocido

**Requisitos cubiertos:** FR-18-02

---

##### UC-18-03: Definición y Mantenimiento de Endpoints Administrativos

**Descripción:** Verifica que la lista de endpoints administrativos se define y mantiene correctamente por el administrador del sistema.

**Actor Principal:** Administrador del Sistema  
**Actores Secundarios:** Sistema

**Precondiciones:**
- El administrador del sistema está autenticado en el sistema.
- La lista de endpoints administrativos está inicialmente vacía o contiene algunos endpoints predefinidos.

**Postcondiciones:**
- La lista de endpoints administrativos se actualiza en el sistema.
- El sistema reconoce los nuevos endpoints administrativos como endpoints administrativos.

**Flujo Básico (resumen):**
1. El administrador del sistema accede a la interfaz de administración.
2. El administrador del sistema añade un nuevo endpoint a la lista de endpoints administrativos.
3. El administrador del sistema guarda los cambios.
4. El sistema actualiza la lista de endpoints administrativos.

**Flujos Alternativos:**
- AF-18-03-1: Eliminación de un endpoint

**Requisitos cubiertos:** FR-18-03

#### Criterios de Aceptación

| ID | Descripción | Escenario de Prueba | Condiciones | Tipo | Prioridad |
|----|-------------|---------------------|-------------|------|-----------|
| AC-18-01 | Un usuario sin el rol de administrador no puede acceder al endpoint '/admin/users' y recibe un mensaje de error indicando falta de permisos. | Acceso no autorizado a endpoint administrativo | <br>• El usuario no tiene asignado el rol 'admin'.<br>• El usuario intenta acceder a la URL '/admin/users'. | Caso de error | Media |
| AC-18-02 | El sistema registra en el log de auditoría todos los intentos de acceso a endpoints administrativos, incluyendo el usuario que intentó acceder y el endpoint accedido. | Registro de acceso a endpoint administrativo | <br>• El usuario intenta acceder a un endpoint administrativo (ej., '/admin/users').<br>• El log de auditoría contiene la información del usuario y el endpoint accedido. | Seguridad | Media |
| AC-18-03 | El sistema valida correctamente el rol 'admin' al acceder al endpoint '/admin/users'. | Validación de rol 'admin' en endpoint | <br>• El usuario tiene asignado el rol 'admin'.<br>• El usuario intenta acceder a la URL '/admin/users'. | Validación | Media |
| AC-18-04 | Si un usuario intenta acceder a un endpoint administrativo sin permisos, la respuesta HTTP debe ser 403 (Forbidden). | Respuesta HTTP para acceso denegado | <br>• El usuario no tiene los permisos necesarios para acceder al endpoint.<br>• La respuesta HTTP del servidor es 403. | Funcional | Media |
| AC-18-05 | La lista de endpoints administrativos está definida y mantenida por el administrador del sistema. | Gestión de la lista de endpoints administrativos | <br>• El administrador del sistema puede agregar, modificar y eliminar endpoints de la lista.<br>• La lista de endpoints administrativos está documentada y accesible para el administrador. | Funcional | Media |
| AC-18-06 | El sistema maneja correctamente el caso donde el rol del usuario está vacío o no es válido, negando el acceso al endpoint administrativo. | Manejo de roles inválidos | <br>• El usuario intenta acceder a un endpoint administrativo con un rol vacío o no reconocido.<br>• El sistema devuelve un mensaje de error indicando que el rol no es válido y niega el acceso. | Seguridad | Media |

**Total de criterios:** 6

#### Requisitos Funcionales
| ID | Nombre | Descripción | Prioridad | Criterios | Rationale |
|----|--------|-------------|-----------|-----------|-----------|
| FR-18-01 | Endpoint Administrative Access Control | The system shall restrict access to administrative endpoints based on user role. | Alta | A user attempting to access an administrative endpoint without the appropriate role shall be denied access.<br>The system logs all attempts to access administrative endpoints, including the user attempting access and the endpoint accessed. | This directly addresses the stated goal of preventing unauthorized access. |
| FR-18-02 | Role-Based Permission Validation | The system shall validate user roles against the permissions defined for each administrative endpoint. | Alta | The system must accurately determine the user's role.<br>The system must correctly map roles to the permissions associated with each endpoint. | This is the core mechanism for restricting access based on roles. |
| FR-18-03 | Endpoint Administrative Endpoint Definition | The system shall define a list of endpoints classified as administrative. | Media | The list of administrative endpoints shall be maintained and updated by the system administrator.<br>Each endpoint shall be clearly identified as an administrative endpoint. | Necessary for the system to know which endpoints require access control. |


#### Requisitos No Funcionales
| ID | Nombre | Descripción | Prioridad | Criterios | Rationale |
|----|--------|-------------|-----------|-----------|-----------|
| NFR-18-01 | Security | The system shall maintain a high level of security to prevent unauthorized access to administrative endpoints. | Alta | The system shall be protected against common web vulnerabilities (e.g., SQL injection, XSS).<br>Access control mechanisms shall be regularly audited. | Fundamental requirement for any system handling sensitive data and operations. |


#### Requisitos de Información
| ID | Nombre | Descripción | Prioridad | Criterios | Rationale |
|----|--------|-------------|-----------|-----------|-----------|
| IR-18-01 | User Role Definitions | The system shall maintain a definition of all user roles and their associated permissions. | Alta | Each role definition shall include a unique identifier.<br>Each role definition shall specify the permissions granted to that role. | Essential for mapping roles to endpoint access control. |


### Historia 19

> Contraseñas seguras
Como equipo técnico,
Quiero almacenar contraseñas con hash robusto,
Para proteger credenciales de usuarios.


#### Casos de Uso - Vista Resumen

| ID | Nombre | Actor Principal | Precondiciones | Postcondiciones | Prioridad | Complejidad | Flujos Alt |
|----|--------|-----------------|----------------|-----------------|-----------|-------------|------------|
| UC-19-1 | Registrar Usuario con Contraseña | Usuario | 2 | 2 | Alta | Baja | 1 |
| UC-19-2 | Actualizar Contraseña de Usuario | Usuario | 2 | 2 | Alta | Media | 1 |

#### Detalle de Casos de Uso

##### UC-19-1: Registrar Usuario con Contraseña

**Descripción:** El sistema registra un nuevo usuario, hasheando su contraseña antes de almacenarla.

**Actor Principal:** Usuario  
**Actores Secundarios:** Sistema, Equipo Técnico

**Precondiciones:**
- El usuario está en la página de registro.
- El usuario ha ingresado una contraseña.

**Postcondiciones:**
- La contraseña del usuario ha sido hasheada y almacenada en la base de datos.
- El usuario está registrado en el sistema.

**Flujo Básico (resumen):**
1. El usuario ingresa su información personal (nombre, email, etc.).
2. El usuario ingresa y confirma su contraseña.
3. El sistema llama a la función de hashing de contraseñas con la contraseña proporcionada.
4. El sistema almacena la contraseña hasheada en la base de datos.
5. El sistema confirma la creación de la cuenta al usuario.

**Flujos Alternativos:**
- AF-19-1-1: Contraseña no cumple con los criterios de seguridad.

**Requisitos cubiertos:** FR-001-1-1, FR-002-1-1

---

##### UC-19-2: Actualizar Contraseña de Usuario

**Descripción:** El usuario actualiza su contraseña, y el sistema ha utiliza el algoritmo de hashing para almacenar la nueva contraseña.

**Actor Principal:** Usuario  
**Actores Secundarios:** Sistema

**Precondiciones:**
- El usuario ha iniciado sesión en el sistema.
- El usuario ha accedido al formulario de cambio de contraseña.

**Postcondiciones:**
- La contraseña anterior del usuario ha sido eliminada de la base de datos.
- La nueva contraseña del usuario ha sido hasheada y almacenada en la base de datos.

**Flujo Básico (resumen):**
1. El usuario ingresa su contraseña actual.
2. El sistema verifica la contraseña actual del usuario.
3. El usuario ingresa y confirma su nueva contraseña.
4. El sistema ha el algoritmo de hashing de contraseñas con la nueva contraseña proporcionada.
5. El sistema almacena la nueva contraseña hasheada en la base de datos.
... y 1 pasos más

**Flujos Alternativos:**
- AF-19-2-1: Contraseña actual incorrecta.

**Requisitos cubiertos:** FR-001-1-1, FR-002-1-1

#### Criterios de Aceptación

| ID | Descripción | Escenario de Prueba | Condiciones | Tipo | Prioridad |
|----|-------------|---------------------|-------------|------|-----------|
| AC-19-1 | El sistema debe hashear todas las contraseñas proporcionadas por el usuario utilizando Argon2id con parámetros de memoria de 32MB, iteraciones de 32000 y tiempo de cálculo de 0.5 segundos. | Registro de nuevo usuario | <br>• El usuario ingresa una contraseña que cumple con la política de complejidad (longitud mínima, caracteres requeridos).<br>• El sistema ha ejecuta el algoritmo Argon2id con los parámetros especificados. | Rendimiento | Media |
| AC-19-2 | Al intentar revertir la contraseña hasheada, el sistema debe generar un error y no revelar la contraseña original. | Intento de recuperación de contraseña | <br>• El usuario proporciona la contraseña hasheada.<br>• El sistema intenta revertir la contraseña hasheada. | Caso de error | Media |
| AC-19-3 | El sistema debe almacenar las contraseñas hasheadas en la base de datos utilizando un esquema que evita el acceso directo a los valores de la contraseña en texto plano. | Creación de nuevo usuario | <br>• El usuario crea una nueva cuenta.<br>• La contraseña hasheada se almacena en la base de datos sin ser almacenada en texto plano. | Seguridad | Media |
| AC-19-4 | El sistema debe registrar el algoritmo de hashing y sus parámetros de configuración en un archivo de configuración seguro y accesible solo para el personal autorizado. | Administración del sistema | <br>• Un administrador de sistema accede al archivo de configuración. | Funcional | Media |
| AC-19-5 | El sistema debe validar que la contraseña ingresada por el usuario cumple con los criterios de complejidad definidos (longitud mínima, caracteres requeridos). | Registro de nuevo usuario | <br>• El usuario ingresa una contraseña que no cumple con los criterios de complejidad.<br>• El sistema muestra un mensaje de error al usuario indicando la razón de la invalidación. | Validación | Media |
| AC-19-6 | Después de un intento fallido de inicio de sesión con una contraseña incorrecta, el sistema debe bloquear la cuenta del usuario durante 5 minutos. | Inicio de sesión fallido | <br>• El usuario intenta iniciar sesión con una contraseña incorrecta.<br>• El sistema bloquea la cuenta del usuario durante 5 minutos. | Funcional | Media |

**Total de criterios:** 6

#### Requisitos Funcionales
| ID | Nombre | Descripción | Prioridad | Criterios | Rationale |
|----|--------|-------------|-----------|-----------|-----------|
| FR-001-1-1 | Hashing de Contraseñas | El sistema debe hashear de forma segura todas las contraseñas proporcionadas por el usuario utilizando un algoritmo de hashing robusto (por ejemplo, Argon2, bcrypt) antes de almacenarlas en la base de datos. | Alta | La contraseña hasheada almacenada en la base de datos no puede revertirse para revelar la contraseña original.<br>El algoritmo de hashing utilizado debe ser revisado y actualizado periódicamente para abordar las vulnerabilidades emergentes.<br>El sistema debe admitir parámetros de hashing configurables (por ejemplo, uso de memoria, iteraciones) para equilibrar la seguridad y el rendimiento. | Para proteger las credenciales de los usuarios del acceso no autorizado en caso de una brecha de la base de datos. |
| FR-002-1-1 | Almacenamiento de Contraseñas | El sistema debe almacenar las contraseñas hasheadas de forma segura, evitando el acceso directo a los valores de la contraseña en texto plano. | Alta | El esquema de la base de datos debe estar diseñado para evitar el acceso directo a los valores de la contraseña hasheada.<br>El acceso a la base de datos debe estar restringido a personal autorizado únicamente. | Para evitar la exposición directa de contraseñas en texto plano. |


#### Requisitos No Funcionales
| ID | Nombre | Descripción | Prioridad | Criterios | Rationale |
|----|--------|-------------|-----------|-----------|-----------|
| NFR-001-1-1 | Seguridad | El sistema debe cumplir con las prácticas de seguridad estándar de la industria para el almacenamiento y el manejo de contraseñas. | Alta | El sistema debe ser conforme con las regulaciones de seguridad pertinentes (por ejemplo, GDPR, CCPA).<br>Se deben realizar auditorías de seguridad periódicas para identificar y abordar las vulnerabilidades. | Para garantizar la confidencialidad y la integridad de los datos de los usuarios. |


#### Requisitos de Información
| ID | Nombre | Descripción | Prioridad | Criterios | Rationale |
|----|--------|-------------|-----------|-----------|-----------|
| IR-001-1-1 | Especificación del Algoritmo de Hashing | El sistema debe utilizar un algoritmo de hashing específico y documentado con parámetros definidos. | Media | El algoritmo de hashing elegido debe estar disponible públicamente y ser bien establecido.<br>Las propiedades de seguridad y las limitaciones del algoritmo deben estar claramente documentadas. | Para garantizar prácticas de hashing de contraseñas consistentes y seguras. |


### Historia 20

> Arquitectura en capas + DTOs


#### Casos de Uso - Vista Resumen

| ID | Nombre | Actor Principal | Precondiciones | Postcondiciones | Prioridad | Complejidad | Flujos Alt |
|----|--------|-----------------|----------------|-----------------|-----------|-------------|------------|
| UC-20-1 | Crear Usuario a través del Controlador | Usuario (implícito) | 2 | 2 | Alta | Media | 1 |
| UC-20-2 | Obtener Datos de Usuario desde el Reposi... | Service Usuario | 1 | 1 | Alta | Baja | 1 |

#### Detalle de Casos de Uso

##### UC-20-1: Crear Usuario a través del Controlador

**Descripción:** El controlador recibe la solicitud para crear un nuevo usuario, validando los datos y delegando la creación al servicio.

**Actor Principal:** Usuario (implícito)  
**Actores Secundarios:** Controlador, Service Usuario, Repository Usuario

**Precondiciones:**
- El usuario ha completado el formulario de registro en el frontend.
- El frontend ha serializado los datos del formulario en un DTO de Usuario.

**Postcondiciones:**
- El DTO de Usuario ha sido validado y convertido a un objeto de negocio.
- El servicio Usuario ha creado un nuevo usuario en el repositorio.

**Flujo Básico (resumen):**
1. El usuario envía el formulario de registro.
2. El controlador recibe la solicitud con el DTO de Usuario.
3. El controlador valida el DTO de Usuario (e.g., formato de email, longitud de contraseña).
4. El controlador crea una instancia del DTO de Usuario con los datos validados.
5. El controlador llama al método 'crearUsuario' del servicio Usuario, pasando el DTO de Usuario.
... y 1 pasos más

**Flujos Alternativos:**
- AF-1: Validación fallida

**Requisitos cubiertos:** FR-001-20-1, FR-002-20-1

---

##### UC-20-2: Obtener Datos de Usuario desde el Repositorio

**Descripción:** El servicio recupera los datos de un usuario específico del repositorio, utilizando un DTO para la transferencia de datos.

**Actor Principal:** Service Usuario  
**Actores Secundarios:** Service Usuario, Repository Usuario, DTO Usuario

**Precondiciones:**
- El servicio Usuario necesita los identificadores del usuario (e.g., ID, email).

**Postcondiciones:**
- El DTO de Usuario contiene los datos del usuario recuperado del repositorio.

**Flujo Básico (resumen):**
1. El servicio Usuario recibe la solicitud para obtener los datos de un usuario.
2. El servicio Usuario crea una instancia del DTO de Usuario.
3. El servicio Usuario llama al método 'obtenerUsuario' del repositorio Usuario, pasando el ID del usuario.
4. El repositorio Usuario consulta la base de datos para obtener los datos del usuario.
5. El repositorio Usuario devuelve los datos del usuario en un DTO de Usuario.

**Flujos Alternativos:**
- AF-2: Usuario no encontrado

**Requisitos cubiertos:** FR-001-20-1, FR-002-20-1

#### Criterios de Aceptación

| ID | Descripción | Escenario de Prueba | Condiciones | Tipo | Prioridad |
|----|-------------|---------------------|-------------|------|-----------|
| AC-20-1 | El controlador solo puede acceder a los servicios a través de interfaces bien definidas. | Prueba de Integración del Controlador | <br>• El controlador llama a métodos del servicio utilizando las interfaces especificadas.<br>• No hay llamadas directas al repositorio desde el controlador. | Funcional | Media |
| AC-20-2 | Los servicios solo pueden acceder al repositorio a través de interfaces bien definidas. | Prueba de Integración del Servicio | <br>• El servicio llama a métodos del repositorio utilizando las interfaces especificadas.<br>• No hay llamadas directas a la base de datos desde el servicio. | Funcional | Media |
| AC-20-3 | Cada DTO representa un objeto de negocio específico y es inmutable. | Validación de DTOs | <br>• Los DTOs tienen campos que corresponden a atributos de negocio.<br>• Intentar modificar un DTO después de su creación resulta en un error o excepción.<br>• Se verifica que los DTOs no puedan ser modificados directamente. | Funcional | Media |
| AC-20-4 | Los DTOs son serializables y deserializables utilizando un formato estándar (JSON). | Comunicación con Frontend | <br>• El DTO puede ser convertido a JSON y viceversa sin pérdida de datos.<br>• La serialización y deserialización se realizan correctamente en diferentes entornos (ej: servidor, frontend). | Validación | Media |
| AC-20-5 | El repositorio expone métodos para acceder y manipular datos, pero no implementa la lógica de negocio. | Prueba de Acceso a Datos | <br>• El repositorio solo realiza operaciones de lectura y escritura en la base de datos.<br>• La lógica de negocio se encuentra en el servicio. | Funcional | Media |
| AC-20-6 | La comunicación entre capas se realiza a través de interfaces bien definidas, evitando dependencias directas. | Prueba de Dependencias | <br>• El controlador interactúa con el servicio a través de una interfaz definida.<br>• El servicio interactúa con el repositorio a través de una interfaz definida. | Funcional | Media |

**Total de criterios:** 6

#### Requisitos Funcionales
| ID | Nombre | Descripción | Prioridad | Criterios | Rationale |
|----|--------|-------------|-----------|-----------|-----------|
| FR-001-20-1 | Separar Capas de Aplicación | El sistema debe implementar una arquitectura en capas bien definida, separando las responsabilidades entre el controlador, el servicio y el repositorio. | Alta | El controlador debe interactuar únicamente con el servicio.<br>El servicio debe interactuar únicamente con el repositorio.<br>La comunicación entre capas debe ser a través de interfaces bien definidas. | Facilita el mantenimiento y la modificación de componentes individuales sin afectar al resto del sistema. |
| FR-002-20-1 | Implementar DTOs | El sistema debe utilizar DTOs (Data Transfer Objects) para la transferencia de datos entre capas. | Alta | Cada DTO debe representar un objeto de negocio específico.<br>Los DTOs deben ser inmutables para garantizar la integridad de los datos.<br>Los DTOs deben ser serializables y deserializables para facilitar la comunicación con el frontend. | Mejora la legibilidad del código, reduce la complejidad y facilita la integración con el frontend. |


#### Requisitos No Funcionales
| ID | Nombre | Descripción | Prioridad | Criterios | Rationale |
|----|--------|-------------|-----------|-----------|-----------|
| NFR-001-20-1 | Mantenibilidad | El sistema debe ser fácil de mantener y modificar. | Alta | El código debe estar bien documentado.<br>El código debe ser modular y reutilizable.<br>Los cambios en una capa no deben requerir cambios en otras capas. | Reduce el tiempo y el costo de mantenimiento del sistema. |
| NFR-002-20-1 | Integración Frontend | El sistema debe ser fácilmente integrable con el frontend. | Media | Los DTOs deben ser compatibles con el formato de datos esperado por el frontend.<br>La API debe ser bien documentada y fácil de usar.<br>El sistema debe proporcionar mecanismos para la gestión de errores y la validación de datos. | Acelera el desarrollo e implementación de la interfaz de usuario. |


#### Requisitos de Información
| ID | Nombre | Descripción | Prioridad | Criterios | Rationale |
|----|--------|-------------|-----------|-----------|-----------|
| IR-001-20-1 | Definición de DTOs | Se debe definir la estructura y los campos de cada DTO. | Alta | Cada DTO debe representar un objeto de negocio específico.<br>Los tipos de datos de cada campo deben ser claramente definidos.<br>Se debe documentar el propósito de cada DTO. | Asegura la consistencia y la precisión de los datos. |
| IR-002-20-1 | Especificación de la API | Se debe especificar la interfaz de la API para la comunicación entre el frontend y el backend. | Media | Se deben definir los endpoints de la API.<br>Se deben definir los métodos HTTP para cada endpoint.<br>Se deben definir los formatos de datos de entrada y salida. | Facilita la integración del frontend con el backend. |


### Historia 21

> Manejo global de errores
Como equipo técnico,
Quiero un formato estándar de errores,
Para simplificar manejo en frontend.


#### Casos de Uso - Vista Resumen

| ID | Nombre | Actor Principal | Precondiciones | Postcondiciones | Prioridad | Complejidad | Flujos Alt |
|----|--------|-----------------|----------------|-----------------|-----------|-------------|------------|
| UC-21-1 | Generar Error Estándar | Sistema | 1 | 1 | Alta | Baja | 1 |
| UC-21-2 | Utilizar Error Estándar en Frontend | Frontend | 2 | 1 | Alta | Baja | 1 |

#### Detalle de Casos de Uso

##### UC-21-1: Generar Error Estándar

**Descripción:** El sistema genera un objeto de error con un formato predefinido para ser utilizado en el manejo de errores.

**Actor Principal:** Sistema  
**Actores Secundarios:** Módulo de Manejo de Errores

**Precondiciones:**
- Se ha detectado una excepción en el sistema.

**Postcondiciones:**
- Se crea un objeto de error con código, mensaje y nivel de severidad.

**Flujo Básico (resumen):**
1. El módulo de manejo de errores captura la excepción.
2. El módulo de manejo de errores crea un objeto de error siguiendo el formato estándar (código, mensaje, nivel de severidad).
3. El objeto de error se devuelve al módulo de manejo de errores.

**Flujos Alternativos:**
- AF-1: Nivel de severidad no especificado

**Requisitos cubiertos:** FR-001-21-1

---

##### UC-21-2: Utilizar Error Estándar en Frontend

**Descripción:** El frontend recibe el objeto de error generado por el sistema y lo muestra al usuario.

**Actor Principal:** Frontend  
**Actores Secundarios:** Sistema, Módulo de Manejo de Errores

**Precondiciones:**
- El frontend ha recibido un objeto de error del sistema.
- El frontend ha configurado un componente para mostrar errores.

**Postcondiciones:**
- El usuario ve un mensaje de error en la interfaz de usuario.

**Flujo Básico (resumen):**
1. El frontend recibe el objeto de error.
2. El frontend utiliza el código de error para identificar el tipo de error.
3. El frontend utiliza el mensaje de error para mostrar un mensaje comprensible al usuario.
4. El frontend utiliza el nivel de severidad para determinar la importancia del error (e.g., mostrar un banner de alerta).

**Flujos Alternativos:**
- AF-2: Componente de error no disponible

**Requisitos cubiertos:** FR-001-21-1

#### Criterios de Aceptación

| ID | Descripción | Escenario de Prueba | Condiciones | Tipo | Prioridad |
|----|-------------|---------------------|-------------|------|-----------|
| AC-21-1 | El sistema genera un objeto de error estándar con campos obligatorios: 'code', 'message' y 'severity' (opcional). | Simulación de errores en diferentes módulos | <br>• Se lanza un error en un módulo específico.<br>• El objeto de error generado contiene un código de error único.<br>• El objeto de error contiene un mensaje de error descriptivo del error.<br>• Si se define la severidad, debe reflejar el impacto del error (e.g., 'info', 'warning', 'error'). | Caso de error | Media |
| AC-21-2 | El formato de error se aplica consistentemente a todos los mecanismos de manejo de errores en el sistema. | Prueba de regresión después de cambios en el manejo de errores | <br>• Se introducen cambios en el manejo de errores en diferentes módulos.<br>• Se verifica que todos los mecanismos de manejo de errores generan objetos de error en el formato estándar. | Validación | Media |
| AC-21-3 | El código de error es único y está mapeado a una condición de error específica en la documentación. | Validación del mapeo de códigos de error | <br>• Se genera un error específico.<br>• Se verifica que el código de error generado corresponde al código de error definido para esa condición de error.<br>• Se consulta la documentación para verificar que el mapeo entre código de error y condición de error es correcto. | Caso de error | Media |
| AC-21-4 | El mensaje de error es claro y comprensible para el usuario final o el desarrollador. | Visualización de mensajes de error en la interfaz de usuario | <br>• Se genera un error que afecta a la interfaz de usuario.<br>• Se verifica que el mensaje de error mostrado al usuario es claro, conciso y fácil de entender.<br>• Se verifica que el mensaje de error es útil para el desarrollador (e.g., proporciona información sobre la causa del error). | Caso de error | Media |
| AC-21-5 | El nivel de severidad del error (si está presente) refleja con precisión el impacto del error. | Simulación de errores con diferentes severidades | <br>• Se generan errores con diferentes niveles de severidad (e.g., 'info', 'warning', 'error').<br>• Se verifica que el nivel de severidad asignado al error corresponde al impacto real del error. | Caso de error | Media |
| AC-21-6 | El sistema maneja errores sin causar bloqueos o fallos inesperados. | Prueba de estrés en el manejo de errores | <br>• Se simulan múltiples errores simultáneamente.<br>• Se verifica que el sistema continúa funcionando sin errores ni bloqueos. | Caso de error | Media |

**Total de criterios:** 6

#### Requisitos Funcionales
| ID | Nombre | Descripción | Prioridad | Criterios | Rationale |
|----|--------|-------------|-----------|-----------|-----------|
| FR-001-21-1 | Implementación de Formato Estándar de Errores | El sistema deberá implementar un formato estándar de errores, consistiendo en al menos un código de error, un mensaje de error descriptivo y un nivel de severidad de error opcional. | Alta | El formato de error se aplica de forma consistente en todos los mecanismos de manejo de errores.<br>El código de error es único y corresponde a una condición de error específica.<br>El mensaje de error es claro y comprensible para el usuario final o el desarrollador.<br>El nivel de severidad del error (si está presente) refleja con precisión el impacto del error. | Garantiza un manejo de errores consistente y simplifica la integración del frontend. |


#### Requisitos No Funcionales
| ID | Nombre | Descripción | Prioridad | Criterios | Rationale |
|----|--------|-------------|-----------|-----------|-----------|
| NFR-001-21-2 | Rendimiento del Manejo de Errores | El sistema deberá manejar los errores sin afectar significativamente el rendimiento general de la aplicación. | Alta | La latencia del manejo de errores no deberá exceder los 200 ms en condiciones normales de funcionamiento.<br>El manejo de errores no deberá consumir más del 10% de los recursos de CPU de la aplicación. | Previene que los errores causen degradación del rendimiento. |


#### Requisitos de Información
| ID | Nombre | Descripción | Prioridad | Criterios | Rationale |
|----|--------|-------------|-----------|-----------|-----------|
| IR-001-21-3 | Definiciones de Códigos de Error | Se deberá mantener una lista documentada de todos los códigos de error y sus significados correspondientes. | Media | Las definiciones de códigos de error son fácilmente accesibles para los desarrolladores.<br>Las definiciones se actualizan regularmente para reflejar los cambios en el sistema. | Proporciona claridad y consistencia en el manejo de errores. |


### Historia 22

> Paginación/filtros estándar
Como equipo técnico,
Quiero estandarizar page, size, sort,
Para consistencia de APIs de listado.


#### Casos de Uso - Vista Resumen

| ID | Nombre | Actor Principal | Precondiciones | Postcondiciones | Prioridad | Complejidad | Flujos Alt |
|----|--------|-----------------|----------------|-----------------|-----------|-------------|------------|
| UC-22-01 | Obtener Lista de Elementos Paginaada y F... | Usuario | 2 | 2 | Alta | Media | 2 |
| UC-22-02 | Solicitar Lista de Elementos con Paginac... | Usuario | 2 | 2 | Alta | Media | 2 |

#### Detalle de Casos de Uso

##### UC-22-01: Obtener Lista de Elementos Paginaada y Filtrada

**Descripción:** Permite al usuario obtener una lista de elementos paginada y filtrada según los parámetros especificados.

**Actor Principal:** Usuario  
**Actores Secundarios:** Sistema

**Precondiciones:**
- El usuario ha iniciado sesión.
- La lista de elementos está disponible para ser mostrada.

**Postcondiciones:**
- La lista de elementos se muestra al usuario, paginada y filtrada.
- La URL de paginación se proporciona al usuario.

**Flujo Básico (resumen):**
1. El usuario selecciona los parámetros de paginación (page, size) y los filtros (sort).
2. El usuario solicita la lista de elementos.
3. El sistema valida los parámetros de entrada.
4. El sistema aplica la paginación y los filtros a los datos.
5. El sistema devuelve la lista de elementos paginada y filtrada al usuario.

**Flujos Alternativos:**
- AF-22-01-1: Parámetros de paginación inválidos
- AF-22-01-2: Parámetro de ordenación inválido

**Requisitos cubiertos:** FR-22-01, FR-22-02, FR-22-03

---

##### UC-22-02: Solicitar Lista de Elementos con Paginación

**Descripción:** El usuario solicita una lista de elementos paginada.

**Actor Principal:** Usuario  
**Actores Secundarios:** Sistema

**Precondiciones:**
- El usuario ha iniciado sesión.
- El usuario ha seleccionado un valor para 'page' y 'size'.

**Postcondiciones:**
- La lista de elementos se muestra al usuario, paginada.
- La URL de paginación para la siguiente página está disponible.

**Flujo Básico (resumen):**
1. El usuario solicita la lista de elementos con los parámetros 'page' y 'size' especificados.
2. El sistema valida los parámetros 'page' y 'size'.
3. El sistema aplica la paginación a los datos.
4. El sistema devuelve la lista de elementos paginada al usuario.

**Flujos Alternativos:**
- AF-22-02-1: Parámetro 'page' inválido
- AF-22-02-2: Parámetro 'size' inválido

**Requisitos cubiertos:** FR-22-01

#### Criterios de Aceptación

| ID | Descripción | Escenario de Prueba | Condiciones | Tipo | Prioridad |
|----|-------------|---------------------|-------------|------|-----------|
| AC-22-01 | La API devuelve un conjunto de resultados limitado a 'size' elementos cuando se especifica un 'size' válido. | Paginación - Tamaño de página válido | <br>• Se envía una solicitud a la API con un 'size' válido (e.g., 10, 20, 100).<br>• La respuesta de la API contiene un conjunto de resultados con un número máximo de elementos igual al valor de 'size'. | Funcional | Media |
| AC-22-02 | La API devuelve URLs de paginación correctas para la siguiente y la página anterior cuando se utilizan los parámetros 'page' y 'size'. | Paginación - URLs de paginación | <br>• Se envía una solicitud a la API con un 'page' y 'size' válidos.<br>• La respuesta de la API incluye URLs de paginación para la siguiente y la página anterior, calculadas correctamente. | Funcional | Media |
| AC-22-03 | La API acepta un parámetro 'sort' que especifica el campo por el cual ordenar. | Ordenamiento - Parámetro 'sort' | <br>• Se envía una solicitud a la API con un parámetro 'sort' válido (e.g., 'name', 'date').<br>• La respuesta de la API devuelve los resultados ordenados según el campo especificado en 'sort'. | Funcional | Media |
| AC-22-04 | La API acepta un parámetro 'sort' que especifica la dirección del orden (ascendente o descendente). | Ordenamiento - Dirección del orden | <br>• Se envía una solicitud a la API con un parámetro 'sort' válido y un parámetro 'direction' (e.g., 'asc', 'desc').<br>• La respuesta de la API devuelve los resultados ordenados según el campo especificado en 'sort' y la dirección especificada en 'direction'. | Funcional | Media |
| AC-22-05 | La API valida que el parámetro 'page' sea un entero positivo. | Validación - Parámetro 'page' | <br>• Se envía una solicitud a la API con un parámetro 'page' que es un entero negativo o cero.<br>• La API devuelve un error de validación indicando que 'page' debe ser un entero positivo. | Validación | Media |
| AC-22-06 | La API valida que el parámetro 'size' sea un entero positivo. | Validación - Parámetro 'size' | <br>• Se envía una solicitud a la API con un parámetro 'size' que es un entero negativo o cero.<br>• La API devuelve un error de validación indicando que 'size' debe ser un entero positivo. | Validación | Media |
| AC-22-07 | La API valida que el parámetro 'sort' sea una cadena de texto válida. | Validación - Parámetro 'sort' | <br>• Se envía una solicitud a la API con un parámetro 'sort' que no es una cadena de texto.<br>• La API devuelve un error de validación indicando que 'sort' debe ser una cadena de texto válida. | Validación | Media |

**Total de criterios:** 7

#### Requisitos Funcionales
| ID | Nombre | Descripción | Prioridad | Criterios | Rationale |
|----|--------|-------------|-----------|-----------|-----------|
| FR-22-01 | Implementar Paginación | El sistema debe permitir la paginación de los resultados de la lista utilizando los parámetros 'page' y 'size'. | Alta | La API debe devolver un conjunto de resultados limitado a 'size' elementos.<br>La API debe devolver una URL de paginación para la siguiente página.<br>La API debe devolver una URL de paginación para la página anterior. | Permite mostrar grandes conjuntos de datos de forma eficiente. |
| FR-22-02 | Implementar Filtros | El sistema debe permitir la aplicación de filtros a los resultados de la lista utilizando el parámetro 'sort'. | Alta | La API debe aceptar un parámetro 'sort' que especifique el campo por el cual ordenar.<br>La API debe aceptar un parámetro 'sort' que especifique la dirección del orden (ascendente o descendente).<br>La API debe devolver los resultados ordenados según los criterios especificados. | Permite filtrar los resultados de la lista según los criterios especificados. |
| FR-22-03 | Validar Parámetros de Paginación y Filtro | El sistema debe validar los parámetros 'page', 'size' y 'sort' antes de aplicar la paginación y los filtros. | Alta | El parámetro 'page' debe ser un entero positivo.<br>El parámetro 'size' debe ser un entero positivo.<br>El parámetro 'sort' debe ser una cadena de texto válida. | Evita errores y garantiza la integridad de los datos. |


#### Requisitos No Funcionales
| ID | Nombre | Descripción | Prioridad | Criterios | Rationale |
|----|--------|-------------|-----------|-----------|-----------|
| NFR-22-01 | Rendimiento | La paginación y los filtros deben ser implementados de manera que no afecten significativamente el rendimiento de la API. | Alta | El tiempo de respuesta de la API con paginación y filtros debe ser inferior a 2 segundos en condiciones normales.<br>La API debe ser capaz de manejar un número máximo de solicitudes concurrentes sin degradar el rendimiento. | Garantiza una experiencia de usuario fluida. |
| NFR-22-02 | Escalabilidad | La implementación debe ser escalable para manejar un número creciente de usuarios y datos. | Media | La arquitectura debe permitir la fácil adición de recursos para manejar un aumento en la carga.<br>La base de datos debe ser capaz de manejar un número creciente de consultas. | Permite que el sistema se adapte al crecimiento futuro. |


#### Requisitos de Información
| ID | Nombre | Descripción | Prioridad | Criterios | Rationale |
|----|--------|-------------|-----------|-----------|-----------|
| IR-22-01 | Definición de 'Page' y 'Size' | Se debe definir formalmente el significado de 'page' y 'size' en el contexto de la API. | Alta | Se debe especificar el formato de los valores de 'page' y 'size'.<br>Se debe definir el valor predeterminado para 'page' y 'size'. | Asegura una comprensión común de los parámetros. |
| IR-22-02 | Tipos de Datos para 'Sort' | Se debe definir los tipos de datos que pueden ser utilizados como criterios de ordenamiento. | Media | Se debe especificar los campos de datos que pueden ser utilizados como criterios de ordenamiento.<br>Se debe definir el formato de los valores de 'sort'. | Define los criterios de ordenamiento válidos. |


### Historia 23

> Documentación OpenAPI
Como equipo técnico y frontend,
Quiero documentación viva de endpoints,
Para acelerar integración y pruebas.


#### Casos de Uso - Vista Resumen

| ID | Nombre | Actor Principal | Precondiciones | Postcondiciones | Prioridad | Complejidad | Flujos Alt |
|----|--------|-----------------|----------------|-----------------|-----------|-------------|------------|
| UC-23-001 | Generar Documentación OpenAPI Inicial | Equipo técnico | 2 | 2 | Alta | Baja | 1 |
| UC-23-002 | Actualizar Documentación OpenAPI en Tiem... | Sistema | 2 | 2 | Alta | Baja | 1 |
| UC-23-003 | Solicitar Documentación OpenAPI | Frontend | 2 | 2 | Media | Baja | 1 |

#### Detalle de Casos de Uso

##### UC-23-001: Generar Documentación OpenAPI Inicial

**Descripción:** El equipo técnico solicita la generación de la documentación OpenAPI inicial para la API.

**Actor Principal:** Equipo técnico  
**Actores Secundarios:** Frontend

**Precondiciones:**
- La definición de los endpoints de la API está disponible (por ejemplo, en un archivo Swagger/OpenAPI).
- El sistema está en funcionamiento.

**Postcondiciones:**
- La documentación OpenAPI se genera y se almacena.
- La documentación está accesible a través de la interfaz de usuario.

**Flujo Básico (resumen):**
1. El equipo técnico inicia el proceso de generación de documentación desde la interfaz de usuario.
2. El sistema lee la definición de los endpoints de la API.
3. El sistema genera la documentación OpenAPI en formato JSON.
4. El sistema guarda la documentación OpenAPI en su almacenamiento persistente.
5. El sistema notifica al frontend que la documentación está disponible.

**Flujos Alternativos:**
- AF-1: Error al leer la definición de los endpoints.

**Requisitos cubiertos:** FR_23_001

---

##### UC-23-002: Actualizar Documentación OpenAPI en Tiempo Real

**Descripción:** El sistema actualiza automáticamente la documentación OpenAPI cuando se modifican los endpoints de la API.

**Actor Principal:** Sistema  
**Actores Secundarios:** Equipo técnico, Frontend

**Precondiciones:**
- Los endpoints de la API han sido modificados.
- El sistema está en funcionamiento.

**Postcondiciones:**
- La documentación OpenAPI se actualiza en tiempo real.
- Los cambios se reflejan en la interfaz de usuario.

**Flujo Básico (resumen):**
1. El sistema detecta un cambio en la definición de los endpoints de la API (por ejemplo, a través de un webhook o una suscripción).
2. El sistema genera la documentación OpenAPI actualizada en formato JSON.
3. El sistema actualiza la documentación OpenAPI en su almacenamiento persistente.
4. El sistema notifica al frontend que la documentación ha sido actualizada.

**Flujos Alternativos:**
- AF-2: Error al generar la documentación actualizada.

**Requisitos cubiertos:** FR_23_002

---

##### UC-23-003: Solicitar Documentación OpenAPI

**Descripción:** El frontend solicita la documentación OpenAPI actualizada.

**Actor Principal:** Frontend  
**Actores Secundarios:** Equipo técnico, Sistema

**Precondiciones:**
- La documentación OpenAPI está disponible en el sistema.
- El frontend está en funcionamiento.

**Postcondiciones:**
- El frontend recibe la documentación OpenAPI actualizada.
- El frontend muestra la documentación a los usuarios.

**Flujo Básico (resumen):**
1. El usuario (a través del frontend) solicita la documentación OpenAPI.
2. El frontend envía una solicitud al sistema para obtener la documentación.
3. El sistema busca la documentación OpenAPI actualizada en su almacenamiento persistente.
4. El sistema devuelve la documentación OpenAPI en formato JSON al frontend.
5. El frontend recibe la documentación y la muestra al usuario.

**Flujos Alternativos:**
- AF-3: Documentación no disponible.

**Requisitos cubiertos:** FR_23_003

#### Criterios de Aceptación

| ID | Descripción | Escenario de Prueba | Condiciones | Tipo | Prioridad |
|----|-------------|---------------------|-------------|------|-----------|
| AC-23-001 | La documentación OpenAPI generada incluye todos los endpoints definidos en la API. | Verificación de la cobertura de endpoints | <br>• La documentación contiene una sección para cada endpoint definido en la API.<br>• Cada sección de endpoint incluye detalles como el método HTTP, la ruta, y la descripción. | Funcional | Media |
| AC-23-002 | La documentación incluye detalles como métodos HTTP, parámetros de solicitud, ejemplos de solicitud y respuestas. | Validación de detalles de endpoint | <br>• Para cada endpoint, se especifica el método HTTP (GET, POST, PUT, DELETE, etc.).<br>• Se definen los parámetros de solicitud (query, path, header) con sus tipos de datos y descripciones.<br>• Se proporcionan ejemplos de solicitud (request) y respuestas (response) para cada método HTTP. | Funcional | Media |
| AC-23-003 | La documentación se genera en formato JSON. | Verificación del formato de salida | <br>• La documentación generada es un archivo JSON válido.<br>• El archivo JSON contiene la estructura de datos definida en la especificación OpenAPI. | Validación | Media |
| AC-23-004 | El tiempo de generación de la documentación no excede los 30 segundos para un endpoint. | Medición del tiempo de generación | <br>• Se mide el tiempo que tarda el sistema en generar la documentación OpenAPI para un endpoint.<br>• El tiempo medido debe ser inferior a 30 segundos. | Rendimiento | Media |
| AC-23-005 | Los cambios en la definición de los endpoints de la API se reflejan automáticamente en la documentación OpenAPI en menos de 10 segundos. | Prueba de actualización en tiempo real | <br>• Se modifica la definición de un endpoint (por ejemplo, se agrega un parámetro).<br>• Se mide el tiempo que tarda la documentación en actualizarse automáticamente. | Funcional | Media |
| AC-23-006 | La actualización de la documentación debe ser transparente para el usuario, sin interrupciones ni errores. | Observación de la actualización | <br>• El usuario observa la actualización de la documentación sin interrupciones.<br>• La documentación actualizada es visualmente consistente con la documentación anterior. | Caso de error | Media |
| AC-23-007 | La interfaz de solicitud de documentación es accesible desde la interfaz de usuario. | Acceso a la interfaz de solicitud | <br>• La interfaz de solicitud de documentación se encuentra en una ubicación predecible dentro de la interfaz de usuario.<br>• La interfaz de solicitud de documentación es fácilmente identificable. | Funcional | Media |

**Total de criterios:** 7

#### Requisitos Funcionales
| ID | Nombre | Descripción | Prioridad | Criterios | Rationale |
|----|--------|-------------|-----------|-----------|-----------|
| FR_23_001 | Generar Documentación OpenAPI | El sistema debe generar automáticamente documentación OpenAPI a partir de la definición de los endpoints de la API. | Alta | La documentación OpenAPI generada incluye todos los endpoints definidos.<br>La documentación incluye detalles como métodos HTTP, parámetros de solicitud, ejemplos de solicitud y respuestas.<br>La documentación se genera en formato JSON.<br>El tiempo de generación de la documentación no excede los 30 segundos. | Permite la creación de la documentación inicial y su mantenimiento continuo. |
| FR_23_002 | Actualizar Documentación en Vivo | El sistema debe actualizar la documentación OpenAPI en tiempo real a medida que los endpoints de la API cambian. | Alta | Los cambios en la definición de los endpoints de la API se reflejan automáticamente en la documentación OpenAPI.<br>La actualización de la documentación debe ser transparente para el usuario.<br>El tiempo de actualización de la documentación no excede los 10 segundos. | Garantiza que la documentación siempre esté sincronizada con la API. |
| FR_23_003 | Solicitar Documentación | El sistema debe proporcionar una interfaz para que los usuarios soliciten la documentación OpenAPI. | Media | La interfaz de solicitud de documentación es accesible desde la interfaz de usuario.<br>La solicitud de documentación devuelve la documentación OpenAPI actualizada.<br>La interfaz de solicitud de documentación es intuitiva y fácil de usar. | Permite a los usuarios acceder a la documentación de la API. |


#### Requisitos No Funcionales
| ID | Nombre | Descripción | Prioridad | Criterios | Rationale |
|----|--------|-------------|-----------|-----------|-----------|
| NFR_23_001 | Rendimiento | La generación y actualización de la documentación OpenAPI debe ser rápida y eficiente. | Alta | El tiempo de generación de la documentación no excede los 30 segundos.<br>El tiempo de actualización de la documentación no excede los 10 segundos.<br>El sistema debe ser capaz de manejar múltiples solicitudes de documentación simultáneamente. | Asegura que la documentación esté disponible rápidamente y no afecte el rendimiento de la API. |
| NFR_23_002 | Disponibilidad | La documentación OpenAPI debe estar siempre disponible. | Media | El sistema debe estar disponible el 99.9% del tiempo.<br>El sistema debe tener un mecanismo de recuperación ante fallos.<br>La documentación debe ser accesible desde diferentes navegadores y dispositivos. | Garantiza que los usuarios puedan acceder a la documentación cuando la necesiten. |


#### Requisitos de Información
| ID | Nombre | Descripción | Prioridad | Criterios | Rationale |
|----|--------|-------------|-----------|-----------|-----------|
| IR_23_001 | Definición de Endpoints | Se requiere una definición precisa de todos los endpoints de la API, incluyendo sus métodos HTTP, parámetros de solicitud, ejemplos de solicitud y respuestas. | Alta | La definición de los endpoints debe estar en formato OpenAPI (YAML o JSON).<br>La definición de los endpoints debe ser completa y precisa.<br>La definición de los endpoints debe ser mantenida actualizada con los cambios en la API. | Es la base para la generación de la documentación OpenAPI. |
| IR_23_002 | Usuarios de la Documentación | Se debe identificar y comprender las necesidades de los usuarios que utilizarán la documentación. | Media | Se debe realizar un análisis de las necesidades de los usuarios.<br>Se debe considerar el nivel de experiencia de los usuarios.<br>Se debe adaptar la documentación a las necesidades de los usuarios. | Permite crear una documentación que sea útil y fácil de usar para los usuarios. |


### Historia 24

> Migraciones de base de datos
Como equipo técnico,
Quiero versionar esquema con migraciones,
Para garantizar reproducibilidad del entorno.


#### Casos de Uso - Vista Resumen

| ID | Nombre | Actor Principal | Precondiciones | Postcondiciones | Prioridad | Complejidad | Flujos Alt |
|----|--------|-----------------|----------------|-----------------|-----------|-------------|------------|
| UC-24-001 | Crear y Aplicar Migración de Base de Dat... | Equipo Técnico | 3 | 2 | Alta | Baja | 1 |
| UC-24-002 | Gestionar Versiones de Migraciones | Equipo Técnico | 1 | 2 | Media | Baja | 1 |

#### Detalle de Casos de Uso

##### UC-24-001: Crear y Aplicar Migración de Base de Datos

**Descripción:** Permite al equipo técnico crear un nuevo script de migración y aplicarlo a la base de datos, actualizando el esquema según lo especificado en el script.

**Actor Principal:** Equipo Técnico  
**Actores Secundarios:** Sistema de Migraciones

**Precondiciones:**
- El equipo técnico ha identificado la necesidad de un cambio en el esquema de la base de datos.
- El equipo técnico ha creado un script de migración que define los cambios necesarios.
- La instancia de base de datos está accesible y en el estado deseado.

**Postcondiciones:**
- El esquema de la base de datos ha sido actualizado según el script de migración aplicado.
- Se ha creado un nuevo registro de migración con el número de versión especificado.

**Flujo Básico (resumen):**
1. El equipo técnico crea un nuevo script de migración de base de datos con un número de versión único.
2. El equipo técnico selecciona la instancia de base de datos a la que se aplicará la migración.
3. El equipo técnico ejecuta el script de migración a través del sistema de migraciones.
4. El sistema aplica el script de migración a la base de datos.
5. El sistema verifica la correcta aplicación del script y registra el resultado.

**Flujos Alternativos:**
- AF-1: Error en el Script de Migración

**Requisitos cubiertos:** FR_DB_001, FR_DB_002

---

##### UC-24-002: Gestionar Versiones de Migraciones

**Descripción:** Permite al equipo técnico visualizar, recuperar y eliminar versiones de scripts de migración de base de datos.

**Actor Principal:** Equipo Técnico  
**Actores Secundarios:** Sistema de Migraciones

**Precondiciones:**
- Existen múltiples versiones de scripts de migración almacenadas en el sistema.

**Postcondiciones:**
- El sistema mantiene un registro actualizado de todas las versiones de los scripts de migración.
- El equipo técnico puede recuperar un script de migración específico por su versión.

**Flujo Básico (resumen):**
1. El equipo técnico accede a la interfaz de gestión de versiones de migraciones.
2. El equipo técnico visualiza la lista de versiones disponibles.
3. El equipo técnico selecciona una versión específica de un script de migración.
4. El sistema permite al equipo técnico recuperar el script de migración.
5. El equipo técnico puede eliminar una versión de migración si ya no es necesaria.

**Flujos Alternativos:**
- AF-2: Versión no encontrada

**Requisitos cubiertos:** FR_DB_003

#### Criterios de Aceptación

| ID | Descripción | Escenario de Prueba | Condiciones | Tipo | Prioridad |
|----|-------------|---------------------|-------------|------|-----------|
| AC-24-01 | Se puede crear un nuevo script de migración con un número de versión único. | Creación de Script de Migración | <br>• El script de migración contiene una descripción clara.<br>• El número de versión asignado al script es un entero positivo.<br>• La creación del script no genera errores. | Funcional | Media |
| AC-24-02 | Aplicar un script de migración con un número de versión válido actualiza correctamente el esquema de la base de datos. | Aplicación de Script de Migración | <br>• El script de migración está diseñado para actualizar el esquema de la base de datos.<br>• La base de datos está en un estado consistente antes de la aplicación del script.<br>• Después de la aplicación, el esquema de la base de datos refleja los cambios definidos en el script. | Funcional | Media |
| AC-24-03 | El sistema mantiene un registro de todos los scripts de migración y sus versiones. | Gestión de Versiones de Migraciones | <br>• El registro de migraciones incluye el nombre del script, la versión y la fecha de creación.<br>• La información del registro es accesible y legible. | Funcional | Media |
| AC-24-04 | Se puede recuperar un script de migración por su número de versión. | Recuperación de Script de Migración | <br>• El número de versión del script es válido y existe en el registro.<br>• El script recuperado es idéntico al script original con el número de versión especificado. | Funcional | Media |
| AC-24-05 | Intentar aplicar un script de migración con un número de versión inexistente genera un error informativo. | Aplicación de Script de Migración - Versión Inexistente | <br>• El número de versión especificado no existe en el registro de migraciones.<br>• El sistema devuelve un mensaje de error claro indicando que el número de versión no es válido. | Validación | Media |
| AC-24-06 | El sistema permite la creación de migraciones con números de versión secuenciales (e.g., 1, 2, 3...). | Creación de Migraciones - Secuencia | <br>• La creación de la migración 2 depende de la creación exitosa de la migración 1.<br>• La creación de la migración 3 depende de la creación exitosa de la migración 2. | Funcional | Media |

**Total de criterios:** 6

#### Requisitos Funcionales
| ID | Nombre | Descripción | Prioridad | Criterios | Rationale |
|----|--------|-------------|-----------|-----------|-----------|
| FR_DB_001 | Crear Script de Migración de Base de Datos | El sistema debe permitir la creación de scripts de migración de base de datos con un número de versión definido. | Alta | Los scripts de migración se crean con un número de versión.<br>Los números de versión son únicos. | Asegura la trazabilidad y permite deshacer los cambios a estados anteriores. |
| FR_DB_002 | Aplicar Script de Migración de Base de Datos | El sistema debe permitir la aplicación de scripts de migración de base de datos a una instancia de base de datos. | Alta | El sistema aplica correctamente el script de migración especificado a la base de datos.<br>El esquema de la base de datos se actualiza de acuerdo con el script de migración. | Permite el despliegue de cambios en la base de datos. |
| FR_DB_003 | Gestionar Versiones de Migraciones | El sistema debe proporcionar un mecanismo para rastrear y gestionar las versiones de los scripts de migración de base de datos. | Media | El sistema mantiene un registro de todos los scripts de migración y sus versiones.<br>El sistema permite la fácil recuperación de scripts de migración por versión. | Facilita el deshacer y la comprensión de la evolución del esquema de la base de datos. |


#### Requisitos No Funcionales

_No se generaron requisitos._


#### Requisitos de Información

_No se generaron requisitos._


### Historia 25

> Configuración por ambientes
Como equipo técnico,
Quiero separar perfiles dev/prod,
Para desplegar sin riesgos de configuración.


#### Casos de Uso - Vista Resumen

| ID | Nombre | Actor Principal | Precondiciones | Postcondiciones | Prioridad | Complejidad | Flujos Alt |
|----|--------|-----------------|----------------|-----------------|-----------|-------------|------------|
| UC-25-001 | Crear y Configurar Perfiles de Entorno (... | Equipo técnico | 2 | 2 | Alta | Media | 1 |

#### Detalle de Casos de Uso

##### UC-25-001: Crear y Configurar Perfiles de Entorno (Dev/Prod)

**Descripción:** Permite al equipo técnico crear y configurar los perfiles de entorno de desarrollo (Dev) y producción (Prod) con sus respectivas configuraciones predeterminadas y ajustes específicos.

**Actor Principal:** Equipo técnico  
**Actores Secundarios:** Sistema

**Precondiciones:**
- El equipo técnico tiene los permisos necesarios para administrar los perfiles de entorno.
- El sistema está en funcionamiento.

**Postcondiciones:**
- Se han creado los perfiles Dev y Prod en el sistema.
- Los perfiles Dev y Prod contienen configuraciones específicas para su entorno.

**Flujo Básico (resumen):**
1. El equipo técnico inicia el proceso de creación de perfiles.
2. El sistema presenta la opción de crear un nuevo perfil (Dev o Prod).
3. El equipo técnico selecciona 'Dev' o 'Prod'.
4. El sistema muestra la configuración predeterminada para el perfil seleccionado.
5. El equipo técnico modifica las configuraciones predeterminadas según sea necesario.
... y 3 pasos más

**Flujos Alternativos:**
- AF-1: Error de validación de configuración

**Requisitos cubiertos:** FR_ENV_001, FR_ENV_002, FR_ENV_003, FR_ENV_004

#### Criterios de Aceptación

| ID | Descripción | Escenario de Prueba | Condiciones | Tipo | Prioridad |
|----|-------------|---------------------|-------------|------|-----------|
| AC-25-01 | El sistema debe permitir la creación de un perfil 'Dev' con configuraciones predeterminadas, incluyendo una URL base, un puerto y un nombre de base de datos. | Creación de Perfil Dev | <br>• El usuario tiene los permisos necesarios para crear perfiles.<br>• Los valores predeterminados para 'Dev' son los especificados en la documentación. | Funcional | Media |
| AC-25-02 | El sistema debe permitir la creación de un perfil 'Prod' con configuraciones predeterminadas, incluyendo una URL base, un puerto y un nombre de base de datos. | Creación de Perfil Prod | <br>• El usuario tiene los permisos necesarios para crear perfiles.<br>• Los valores predeterminados para 'Prod' son los especificados en la documentación. | Funcional | Media |
| AC-25-03 | El usuario debe poder modificar los parámetros de configuración del perfil 'Dev' (URL base, puerto, nombre de base de datos) sin errores. | Modificación de Configuración Dev | <br>• El usuario tiene los permisos necesarios para modificar perfiles.<br>• Los cambios se guardan correctamente en el perfil 'Dev'. | Caso de error | Media |
| AC-25-04 | El usuario debe poder modificar los parámetros de configuración del perfil 'Prod' (URL base, puerto, nombre de base de datos) sin errores. | Modificación de Configuración Prod | <br>• El usuario tiene los permisos necesarios para modificar perfiles.<br>• Los cambios se guardan correctamente en el perfil 'Prod'. | Caso de error | Media |
| AC-25-05 | Al desplegar la aplicación utilizando el perfil 'Dev', la aplicación se ejecuta con las configuraciones definidas para 'Dev'. | Despliegue con Perfil Dev | <br>• El despliegue se completa sin errores.<br>• La aplicación se inicia y funciona correctamente utilizando las configuraciones de 'Dev'. | Funcional | Media |
| AC-25-06 | Al desplegar la aplicación utilizando el perfil 'Prod', la aplicación se ejecuta con las configuraciones definidas para 'Prod'. | Despliegue con Perfil Prod | <br>• El despliegue se completa sin errores.<br>• La aplicación se inicia y funciona correctamente utilizando las configuraciones de 'Prod'. | Funcional | Media |
| AC-25-07 | Si se intenta desplegar la aplicación utilizando un perfil no definido, el sistema debe mostrar un mensaje de error indicando que el perfil es desconocido. | Despliegue con Perfil Inválido | <br>• Se intenta desplegar utilizando un perfil inexistente (e.g., 'Test'). | Caso de error | Media |

**Total de criterios:** 7

#### Requisitos Funcionales
| ID | Nombre | Descripción | Prioridad | Criterios | Rationale |
|----|--------|-------------|-----------|-----------|-----------|
| FR_ENV_001 | Crear Perfil de Entorno de Desarrollo | El sistema debe permitir la creación de un perfil dedicado para el entorno de desarrollo (Dev). | Alta | El sistema permite la creación de un perfil denominado 'Dev'.<br>El perfil contiene configuraciones de configuración predeterminadas para el entorno Dev. | Esencial para el desarrollo y las pruebas iniciales. |
| FR_ENV_002 | Crear Perfil de Entorno de Producción | El sistema debe permitir la creación de un perfil dedicado para el entorno de producción (Prod). | Alta | El sistema permite la creación de un perfil denominado 'Prod'.<br>El perfil contiene configuraciones de configuración predeterminadas para el entorno Prod. | Esencial para implementar la aplicación en el entorno de producción. |
| FR_ENV_003 | Configurar Perfil de Entorno de Desarrollo | El sistema debe permitir la configuración del perfil del entorno de desarrollo con ajustes específicos. | Alta | El sistema permite la modificación de parámetros de configuración dentro del perfil Dev.<br>Los cambios en el perfil Dev se guardan. | Permite la personalización del entorno Dev para las necesidades de desarrollo específicas. |
| FR_ENV_004 | Configurar Perfil de Entorno de Producción | El sistema debe permitir la configuración del perfil del entorno de producción con ajustes específicos. | Alta | El sistema permite la modificación de parámetros de configuración dentro del perfil Prod.<br>Los cambios en el perfil Prod se guardan. | Permite la personalización del entorno Prod para las necesidades de producción específicas. |


#### Requisitos No Funcionales
| ID | Nombre | Descripción | Prioridad | Criterios | Rationale |
|----|--------|-------------|-----------|-----------|-----------|
| NFR_ENV_001 | Aislamiento del Entorno | El sistema debe garantizar el aislamiento completo entre los entornos Dev y Prod. | Alta | Ninguna modificación de configuración en el entorno Dev afectará al entorno Prod.<br>Ninguna modificación de configuración en el entorno Prod afectará al entorno Dev. | Crítico para prevenir consecuencias no deseadas durante las implementaciones. |
| NFR_ENV_002 | Mitigación del Riesgo de Implementación | El sistema debe minimizar el riesgo de errores de configuración durante la implementación. | Alta | El sistema proporciona un mecanismo para validar la configuración de los parámetros antes de la implementación.<br>El sistema permite la reversión a una configuración anterior en caso de problemas. | Aborda el requisito principal de la historia. |


#### Requisitos de Información
| ID | Nombre | Descripción | Prioridad | Criterios | Rationale |
|----|--------|-------------|-----------|-----------|-----------|
| IR_ENV_001 | Detalles de Configuración del Entorno | El sistema debe almacenar y administrar todos los detalles de configuración para los entornos Dev y Prod. | Alta | Los detalles de configuración incluyen cadenas de conexión de la base de datos, claves de API y otras configuraciones específicas del entorno. | Necesario para administrar y mantener los entornos. |


### Historia 26

> Despliegue en nube
Como equipo técnico,
Quiero desplegar backend + BD cloud,
Para contar con demo accesible públicamente.


#### Casos de Uso - Vista Resumen

| ID | Nombre | Actor Principal | Precondiciones | Postcondiciones | Prioridad | Complejidad | Flujos Alt |
|----|--------|-----------------|----------------|-----------------|-----------|-------------|------------|
| UC-26-1 | Desplegar Backend y BD en Nube | Equipo técnico | 3 | 3 | Alta | Media | 2 |
| UC-26-2 | Verificar Accesibilidad Pública | Equipo técnico | 1 | 3 | Alta | Baja | 1 |

#### Detalle de Casos de Uso

##### UC-26-1: Desplegar Backend y BD en Nube

**Descripción:** El equipo técnico despliega el backend y la base de datos en un entorno de nube, configurando los servicios necesarios para la demo pública.

**Actor Principal:** Equipo técnico  
**Actores Secundarios:** Proveedor de Nube, Sistema de Automatización de Despliegue

**Precondiciones:**
- El equipo técnico tiene acceso a la cuenta del proveedor de nube.
- Se han definido los parámetros de configuración del backend y la base de datos (e.g., versiones de software, credenciales).
- El sistema de automatización de despliegue está configurado y funcional.

**Postcondiciones:**
- El backend y la base de datos están desplegados en el entorno de nube.
- Se han configurado las conexiones entre el backend y la base de datos.
- Se ha verificado la accesibilidad básica del backend.

**Flujo Básico (resumen):**
1. El equipo técnico inicia el proceso de despliegue a través del sistema de automatización.
2. El sistema de automatización selecciona el entorno de nube y los servicios necesarios.
3. El sistema de automatización despliega el backend y la base de datos, utilizando la configuración predefinida.
4. El sistema de automatización verifica la correcta configuración de la conexión entre el backend y la base de datos.
5. El equipo técnico realiza una verificación inicial de la accesibilidad del backend a través de la IP pública asignada.

**Flujos Alternativos:**
- AF-1: Error de despliegue en el backend
- AF-2: Error de despliegue en la BD

**Requisitos cubiertos:** FR-001-26-1, FR-002-26-2

---

##### UC-26-2: Verificar Accesibilidad Pública

**Descripción:** El equipo técnico verifica que el backend y la base de datos son accesibles públicamente a través de la red.

**Actor Principal:** Equipo técnico  
**Actores Secundarios:** Navegador Web

**Precondiciones:**
- El backend y la base de datos están desplegados y accesibles (ver UC-26-1).

**Postcondiciones:**
- Se confirma que el backend es accesible desde cualquier dirección IP.
- Se confirma que la base de datos es accesible desde el backend.
- Se confirma que la demo es accesible a través de un navegador web.

**Flujo Básico (resumen):**
1. El equipo técnico utiliza una herramienta de prueba de acceso remoto (e.g., telnet, nc) para verificar la accesibilidad del backend a través de la IP pública.
2. El equipo técnico utiliza el backend para intentar conectarse a la base de datos.
3. El equipo técnico accede a la demo a través de un navegador web, verificando que la aplicación se carga correctamente.

**Flujos Alternativos:**
- AF-1: Firewall bloquea el acceso

**Requisitos cubiertos:** FR-003-26-3

#### Criterios de Aceptación

| ID | Descripción | Escenario de Prueba | Condiciones | Tipo | Prioridad |
|----|-------------|---------------------|-------------|------|-----------|
| AC-26-1 | El backend desplegado en la nube responde a las peticiones HTTP/HTTPS con un código de estado 200 OK. | Petición HTTP al backend | <br>• La petición HTTP se realiza desde un navegador web.<br>• La petición HTTP utiliza el puerto correcto (ej. 8080). | Funcional | Media |
| AC-26-2 | La base de datos en la nube es accesible desde el backend utilizando el puerto configurado. | Conexión desde el backend a la base de datos | <br>• La base de datos está en funcionamiento.<br>• Las credenciales de acceso a la base de datos son correctas. | Funcional | Media |
| AC-26-3 | El backend es accesible públicamente a través de la IP pública asignada, verificable desde cualquier dirección IP. | Acceso público al backend | <br>• La IP pública asignada al backend es correcta.<br>• Se puede acceder al backend utilizando la IP pública desde cualquier navegador web. | Funcional | Media |
| AC-26-4 | La base de datos está protegida con las credenciales adecuadas y solo accesible desde el backend. | Acceso a la base de datos desde el backend | <br>• Las credenciales de acceso a la base de datos son correctas.<br>• Se verifica que la base de datos no es accesible directamente desde el navegador web. | Funcional | Media |
| AC-26-5 | La demo es accesible a través de un navegador web utilizando la URL proporcionada. | Acceso a la demo a través de navegador | <br>• La URL de la demo es correcta.<br>• La demo se carga correctamente en el navegador web. | Funcional | Media |
| AC-26-6 | El backend registra correctamente las peticiones HTTP/HTTPS recibidas. | Monitorización de peticiones al backend | <br>• Se realiza una serie de peticiones HTTP/HTTPS al backend.<br>• Se verifica que el backend registra cada petición en sus logs. | Funcional | Media |
| AC-26-7 | La base de datos contiene al menos un registro de prueba para verificar la funcionalidad de acceso a datos. | Verificación de datos en la base de datos | <br>• Se realiza una consulta a la base de datos.<br>• Se verifica que la consulta devuelve un resultado válido. | Seguridad | Media |

**Total de criterios:** 7

#### Requisitos Funcionales
| ID | Nombre | Descripción | Prioridad | Criterios | Rationale |
|----|--------|-------------|-----------|-----------|-----------|
| FR-001-26-1 | Desplegar Backend en Nube | El sistema debe permitir el despliegue del backend en un entorno de nube seleccionado. | Alta | El backend debe estar accesible a través de la IP pública asignada.<br>El backend debe responder a las peticiones HTTP/HTTPS.<br>El backend debe estar configurado con la versión correcta del software. | Permite la funcionalidad principal de la demo pública. |
| FR-002-26-2 | Desplegar Base de Datos en Nube | El sistema debe permitir el despliegue de la base de datos en un entorno de nube seleccionado. | Alta | La base de datos debe estar accesible desde el backend.<br>La base de datos debe estar configurada con la versión correcta del software.<br>La base de datos debe estar protegida con las credenciales adecuadas. | Permite el almacenamiento y acceso de datos para la demo. |
| FR-003-26-3 | Verificar Acceso Público | El sistema debe verificar que el backend y la base de datos son accesibles públicamente a través de la red. | Alta | Se debe poder acceder al backend desde cualquier dirección IP.<br>Se debe poder acceder a la base de datos desde el backend.<br>Se debe poder acceder a la demo a través de un navegador web. | Garantiza que la demo está realmente accesible al público. |


#### Requisitos No Funcionales

_No se generaron requisitos._


#### Requisitos de Información

_No se generaron requisitos._


### Historia 27

> Logging y monitoreo básico
Como equipo técnico,
Quiero trazabilidad mínima de fallos,
Para depurar incidencias rápidamente.


#### Casos de Uso - Vista Resumen

| ID | Nombre | Actor Principal | Precondiciones | Postcondiciones | Prioridad | Complejidad | Flujos Alt |
|----|--------|-----------------|----------------|-----------------|-----------|-------------|------------|
| UC-27-001 | Registrar Evento de Error | Sistema | 2 | 2 | Alta | Baja | 1 |
| UC-27-002 | Monitorear Métricas del Sistema | Equipo Técnico | 2 | 2 | Media | Baja | 1 |
| UC-27-003 | Enviar Informe de Error a Plataforma de ... | Sistema | 2 | 2 | Alta | Baja | 1 |

#### Detalle de Casos de Uso

##### UC-27-001: Registrar Evento de Error

**Descripción:** Permite al sistema registrar eventos de error con detalles relevantes para la depuración.

**Actor Principal:** Sistema  
**Actores Secundarios:** Equipo Técnico

**Precondiciones:**
- El sistema ha detectado un error.
- El nivel de registro es superior a 'Advertencia'.

**Postcondiciones:**
- Se crea una entrada de registro con detalles del error.
- La entrada de registro se almacena para su posterior análisis.

**Flujo Básico (resumen):**
1. El sistema detecta un error.
2. El sistema recopila información relevante: marca de tiempo, tipo de error, traza de pila, contexto.
3. El sistema genera una entrada de registro con la información recopilada.
4. El sistema guarda la entrada de registro en el sistema de almacenamiento de registros.

**Flujos Alternativos:**
- AF-1: Nivel de registro configurado a 'Información' o 'Error'

**Requisitos cubiertos:** FR_001, FR_003

---

##### UC-27-002: Monitorear Métricas del Sistema

**Descripción:** Permite al equipo técnico observar las métricas del sistema en tiempo real para identificar posibles problemas de rendimiento.

**Actor Principal:** Equipo Técnico  
**Actores Secundarios:** Sistema

**Precondiciones:**
- El sistema está en funcionamiento.
- El monitoreo está habilitado.

**Postcondiciones:**
- El equipo técnico puede ver las métricas del sistema en tiempo real.
- Se generan alertas si las métricas exceden los umbrales predefinidos.

**Flujo Básico (resumen):**
1. El sistema recopila métricas clave (CPU, memoria, red).
2. El sistema muestra las métricas en un panel de control.
3. El sistema compara las métricas con los umbrales predefinidos.
4. Si una métrica excede el umbral, el sistema genera una alerta.

**Flujos Alternativos:**
- AF-2: Monitoreo deshabilitado

**Requisitos cubiertos:** FR_002

---

##### UC-27-003: Enviar Informe de Error a Plataforma de Monitoreo

**Descripción:** Permite al sistema enviar automáticamente informes de errores críticos a una plataforma de monitoreo centralizada.

**Actor Principal:** Sistema  
**Actores Secundarios:** Plataforma de Monitoreo

**Precondiciones:**
- El sistema ha detectado un error crítico.
- La plataforma de monitoreo está configurada.

**Postcondiciones:**
- El informe de error se envía a la plataforma de monitoreo.
- La plataforma de monitoreo genera una alerta.

**Flujo Básico (resumen):**
1. El sistema detecta un error crítico.
2. El sistema recopila información relevante: traza de pila, código de error, contexto.
3. El sistema genera un informe de error con la información recopilada.
4. El sistema envía el informe de error a la plataforma de monitoreo.

**Flujos Alternativos:**
- AF-3: Plataforma de monitoreo no disponible

**Requisitos cubiertos:** FR_003

#### Criterios de Aceptación

| ID | Descripción | Escenario de Prueba | Condiciones | Tipo | Prioridad |
|----|-------------|---------------------|-------------|------|-----------|
| AC-27-01 | Cada evento registrado incluye un identificador único, marca de tiempo precisa y contexto relevante (nombre de la función, clase, línea de código). | Registro de errores de la aplicación | <br>• Se lanza un error simulado en la aplicación.<br>• El registro contiene el ID de transacción, la hora exacta del error, el nombre de la función que causó el error y la línea de código donde ocurrió. | Rendimiento | Media |
| AC-27-02 | Los registros de eventos se almacenan durante un mínimo de 7 días. | Registro continuo de eventos durante una semana. | <br>• El sistema registra eventos de forma continua durante 7 días.<br>• Después de 7 días, los registros de eventos siguen estando disponibles. | Funcional | Media |
| AC-27-03 | Los registros de eventos son buscables por marca de tiempo, tipo de evento y gravedad. | Búsqueda de registros por tipo de evento. | <br>• Se registran eventos de diferentes tipos (Error, Warning, Info).<br>• Se puede filtrar la búsqueda por tipo de evento (Error, Warning, Info). | Rendimiento | Media |
| AC-27-04 | El sistema muestra el uso de CPU, memoria y red en tiempo real. | Monitoreo del uso de recursos del sistema. | <br>• El sistema muestra los valores de CPU, memoria y red en tiempo real.<br>• Los valores se actualizan con una frecuencia de al menos 1 segundo. | Rendimiento | Media |
| AC-27-05 | El sistema genera alertas cuando las métricas exceden los umbrales predefinidos (CPU > 90%, Memoria > 80%, Tráfico de red > 10MB/s). | Prueba de alertas por sobrecarga de recursos. | <br>• Se simula una carga alta en el sistema para aumentar el uso de CPU, memoria y red.<br>• El sistema genera alertas cuando las métricas exceden los umbrales configurados. | Funcional | Media |
| AC-27-06 | Los informes de errores incluyen trazas de pila, códigos de error y contexto relevante, y se envían a la plataforma de monitoreo centralizada. | Generación y envío de informe de error. | <br>• Se simula un error crítico en la aplicación.<br>• El informe de error incluye la traza de pila, el código de error, la hora del error y el contexto relevante.<br>• El informe de error se envía a la plataforma de monitoreo centralizada. | Caso de error | Media |
| AC-27-07 | Los usuarios pueden seleccionar diferentes niveles de registro (Depuración, Información, Advertencia, Error) a través de la interfaz de configuración. | Cambio de nivel de registro. | <br>• La interfaz de configuración permite seleccionar diferentes niveles de registro.<br>• El nivel de registro seleccionado se aplica a todas las operaciones de registro. | Caso de error | Media |

**Total de criterios:** 7

#### Requisitos Funcionales
| ID | Nombre | Descripción | Prioridad | Criterios | Rationale |
|----|--------|-------------|-----------|-----------|-----------|
| FR_001 | Registro de eventos | El sistema debe registrar todos los eventos críticos, incluyendo errores, advertencias y mensajes informativos, con una marca de tiempo y contexto relevante. | Alta | Cada entrada de registro de eventos incluye un identificador único.<br>Los registros de eventos se almacenan durante un mínimo de 7 días.<br>Los registros de eventos son buscables por marca de tiempo, tipo de evento y gravedad. | Esencial para la depuración y el análisis de causa raíz. |
| FR_002 | Monitoreo del sistema | El sistema debe proporcionar un monitoreo básico de las métricas del sistema clave, como el uso de la CPU, el uso de la memoria y el tráfico de red. | Medio | El sistema muestra el uso de CPU, memoria y red en tiempo real.<br>El sistema genera alertas cuando las métricas exceden los umbrales predefinidos.<br>Los datos de monitoreo son accesibles a través de un panel. | Permite la identificación proactiva de problemas de rendimiento. |
| FR_003 | Informes de errores | El sistema debe informar automáticamente los errores críticos a un servicio de monitoreo designado. | Alta | Los informes de errores incluyen trazas de pila, códigos de error y contexto relevante.<br>Los informes de errores se envían a una plataforma de monitoreo centralizada.<br>La plataforma de monitoreo proporciona alertas para errores críticos. | Asegura la notificación inmediata de problemas críticos. |
| FR_004 | Configuración de niveles de registro | El sistema debe permitir la configuración de niveles de registro (por ejemplo, Depuración, Información, Advertencia, Error) para controlar la verbosidad del registro. | Medio | Los usuarios pueden seleccionar diferentes niveles de registro a través de una interfaz de configuración.<br>El nivel de registro seleccionado se aplica a todas las operaciones de registro.<br>La configuración del nivel de registro se guarda en los reinicios del sistema. | Proporciona flexibilidad en la gestión de la salida de registro en función de las necesidades operativas. |


#### Requisitos No Funcionales
| ID | Nombre | Descripción | Prioridad | Criterios | Rationale |
|----|--------|-------------|-----------|-----------|-----------|
| NFR_001 | Rendimiento | El sistema de registro y monitoreo no debe introducir una sobrecarga de rendimiento significativa en la aplicación principal. | Alta | La sobrecarga del sistema no debe exceder el 1% del uso de la CPU de la aplicación.<br>Los tiempos de respuesta para las consultas de monitoreo deben ser inferiores a 1 segundo. | Asegura que el sistema de monitoreo no afecte negativamente el rendimiento de la aplicación. |
| NFR_002 | Escalabilidad | El sistema de registro y monitoreo debe ser escalable para manejar volúmenes crecientes de datos de registro y solicitudes de monitoreo. | Medio | El sistema debe ser capaz de manejar 10 veces el volumen de registro actual sin degradación del rendimiento.<br>El sistema debe admitir solicitudes de monitoreo concurrentes de múltiples usuarios. | Soporta el crecimiento futuro y la complejidad del sistema aumentada. |
| NFR_003 | Seguridad | El sistema de registro y monitoreo debe estar seguro para evitar el acceso no autorizado a los datos de registro y la información de monitoreo. | Alta | El acceso al sistema de registro y monitoreo está restringido al personal autorizado.<br>Los datos de registro están encriptados en reposo e en tránsito.<br>El sistema cumple con los estándares de seguridad pertinentes. | Protege los datos confidenciales y previene actividades maliciosas. |


#### Requisitos de Información
| ID | Nombre | Descripción | Prioridad | Criterios | Rationale |
|----|--------|-------------|-----------|-----------|-----------|
| IR_001 | Panel de control de monitoreo | Una representación visual de las métricas del sistema clave y las alertas. | Alta | El panel muestra datos en tiempo real para CPU, memoria, red y otras métricas relevantes.<br>El panel incluye alertas para eventos críticos y umbrales excedidos.<br>El panel es personalizable para mostrar métricas y alertas específicas. | Proporciona una vista centralizada de la salud del sistema. |
| IR_002 | Esquema de datos de registro | Un esquema definido para los datos de registro para garantizar la consistencia y facilitar el análisis. | Medio | El esquema incluye campos para marca de tiempo, tipo de evento, gravedad, origen y contexto.<br>El esquema está documentado y mantenido.<br>El esquema es compatible con las herramientas de monitoreo existentes. | Permite un análisis y un informe de registro eficientes. |
| IR_003 | Configuración de alertas | Un mecanismo para definir y administrar alertas basadas en las métricas del sistema. | Alta | Los usuarios pueden definir alertas basadas en métricas y umbrales específicos.<br>Los usuarios pueden configurar la gravedad de las alertas y los canales de notificación.<br>Las alertas se activan automáticamente cuando se cumplen las condiciones. | Facilita la identificación y la respuesta proactiva a los problemas del sistema. |
