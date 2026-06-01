# Assembly — Framework de aplicaciones Java 8 (Legacy)

> **Assembly** es un *framework* propio, escrito a medida sobre Java 1.8, que reúne en un mismo
> artefacto una capa de UI de escritorio (Swing), un servidor web ligero, una capa de acceso a
> datos multi-motor, un sistema de daemons (scheduler + workers sobre Gearman) y un servicio de
> notificaciones *push* (APNs / FCM). No depende de ningún contenedor de aplicaciones ni de Spring:
> todo el ciclo de vida se arranca desde un `main` propio.

| | |
|---|---|
| **GroupId / ArtifactId** | `com.assembly` / `Assembly` |
| **Versión** | `0.15` (build `773`, marca `20211026-120611`) |
| **Java** | 1.8 (`source`/`target` = 1.8) |
| **Build** | Maven (`Assembly/pom.xml`), empaquetado `jar-with-dependencies` |
| **Tamaño** | ~115 clases Java (~13.800 LOC) |
| **Empaquetado** | JAR ejecutable autocontenido (uber-jar) |

---

## Tabla de contenidos

1. [Visión general](#visión-general)
2. [Arquitectura](#arquitectura)
3. [Estructura del proyecto](#estructura-del-proyecto)
4. [Puntos de entrada (`main`)](#puntos-de-entrada-main)
5. [El núcleo (`com.assembly.core`)](#el-núcleo-comassemblycore)
6. [Capa de servicio (`com.assembly.service`)](#capa-de-servicio-comassemblyservice)
7. [Capa de UI / escritorio (`com.assembly.ui`)](#capa-de-ui--escritorio-comassemblyui)
8. [Cliente Gearman embebido](#cliente-gearman-embebido)
9. [Dependencias](#dependencias)
10. [Compilación y ejecución](#compilación-y-ejecución)
11. [Configuración](#configuración)
12. [Modelo de datos del sistema](#modelo-de-datos-del-sistema)
13. [Recursos (`src/res`)](#recursos-srcres)
14. [Notas, limitaciones y deuda técnica](#notas-limitaciones-y-deuda-técnica)

---

## Visión general

Assembly está pensado como una **base reutilizable** sobre la que construir tanto aplicaciones de
escritorio como servicios de *backend*. El proyecto define tres tipos de aplicación distintos, cada
uno con su propio `ApplicationDelegate` (`main`):

- **UI** (`com.assembly.ui`) — aplicación de escritorio Swing con su propio sistema de navegación
  (estilo iOS: *push/pop* de controladores), controles personalizados, *theming*, tablas, gráficas
  (JFreeChart) e informes PDF.
- **Service** (`com.assembly.service`) — servicio *headless* que arranca el planificador de tareas
  (cron), los *workers* de Gearman y un servidor HTTP en el puerto 8080. Es el origen del subsistema
  de **notificaciones push**.
- **Provider** (`com.assembly.provider`) — esqueleto reservado para integraciones de proveedor (hoy
  vacío).

El denominador común de los tres es el paquete `com.assembly.core`, que aporta toda la
infraestructura transversal: configuración, acceso a datos, caché, servidor web, observadores,
operaciones asíncronas, monitorización por cron, cifrado, correo, informes, versión, i18n y trazas.

---

## Arquitectura

```
┌──────────────────────────────────────────────────────────────────────────┐
│                          APLICACIONES (main)                               │
│   ui.application.*        service.ui.application.*     provider.ui.*        │
│   (escritorio Swing)      (servicio headless)          (reservado)          │
└───────────────┬───────────────────────┬───────────────────────────────────┘
                │                        │
   ┌────────────▼───────────┐  ┌─────────▼──────────────────────────────────┐
   │  com.assembly.ui        │  │  com.assembly.service                       │
   │  navigation / controls  │  │  business · dao · domain · web              │
   │  components · themes     │  │  push (APNs/FCM) · notifications (cola)      │
   └────────────┬───────────┘  └─────────┬──────────────────────────────────┘
                │                         │
   ┌────────────▼─────────────────────────▼──────────────────────────────────┐
   │                        com.assembly.core (NÚCLEO)                         │
   │                                                                           │
   │  web ─ servidor HTTP propio (com.sun.net.httpserver)                      │
   │  config ─ ConfigManager (.env + .properties), pools, sesiones de BD       │
   │  source/db ─ conectores SQLite / MySQL / SQL Server + DBCP2               │
   │  source/cache ─ caché SQLite por reflexión (@Cache)                       │
   │  source/api · source/service · source/cube                                │
   │  daemons ─ Scheduler (cron) · Worker (Gearman)                            │
   │  monitor · observer · operations ─ concurrencia / eventos / cron          │
   │  secure · mailer · reports · preferences · version · i18n · trace         │
   └───────────────────────────────────────────────────────────────────────┘
                                    │
                         net.johnewart.gearman.client
                       (cliente/worker Gearman embebido)
```

### Patrones de diseño recurrentes

- **Singleton**: `WebServer`, `ConfigManager`, `APIConnector`, `ObserverCenter`, `NavigationController`,
  `ThemeFactory`, `EncryptionManager`, `VersionBundle`, `RelativeResource`.
- **Factory**: `DBConnector`, `CacheManager`, `TableFactory`, `RequestExchange.parseRequest()`.
- **Strategy**: `ConnectorInterface` (un conector por motor de BD), `PushInterface` (un servicio por
  plataforma), `Encryption` (algoritmo de hash).
- **Template Method**: `WebController`, `Scheduler`, `Worker`, `MonitorProtocol`, `OperationProtocol`.
- **Observer / Pub-Sub**: `ObserverCenter` + `ObserverInterface`, callbacks de ciclo de vida en `ControllerInterface`.
- **Annotation-driven + reflexión**: `@Cache` / `@CacheMemory` para la caché; `Preferences` mapea
  campos a ficheros `.ini` por reflexión.

---

## Estructura del proyecto

```
AssemblyFactoryLegacyJava8/
├── README.md
├── .gitignore
└── Assembly/
    ├── pom.xml
    └── src/
        ├── com/assembly/
        │   ├── core/                 ← infraestructura transversal
        │   │   ├── commons/          FileHelper, TextFormatter
        │   │   ├── config/           ConfigManager, ConfigProperties, ConfigReference,
        │   │   │                     DatabaseSession, PoolAdapter, exceptions/
        │   │   ├── daemons/          scheduler/Scheduler, worker/Worker
        │   │   ├── i18n/             Localizable
        │   │   ├── mailer/           MailerTemplate
        │   │   ├── monitor/          MonitorProtocol, MonitorInterface, MonitorMode
        │   │   ├── observer/         ObserverCenter, ObserverInterface, ObserverQuery
        │   │   ├── operations/       OperationProtocol, OperationInterface, OperationQuery
        │   │   ├── preferences/      Preferences, PreferencesProtocol
        │   │   ├── reports/          PDFReport
        │   │   ├── resources/        RelativeResource
        │   │   ├── secure/           Encryption, EncryptionManager
        │   │   ├── source/
        │   │   │   ├── db/            DBConnector, ConnectorInterface, NamedParameterStatement,
        │   │   │   │                  MysqlConnector, SQLiteConnector, SQLServerConnector,
        │   │   │   │                  DriverAdapter, Transaction, DatabaseSource, exception/
        │   │   │   ├── cache/         CacheManager, Cache, CacheMemory, CacheInterface
        │   │   │   ├── cube/          (reservado / vacío)
        │   │   │   ├── api/           APIConnector
        │   │   │   └── service/       ProviderService
        │   │   ├── trace/            Trace (Log4j)
        │   │   ├── version/          VersionBundle, VersionResource
        │   │   └── web/              WebServer, WebProvider, WebController, WebResource,
        │   │                         WebAuthenticator, RequestExchange, ResponseExchange
        │   ├── service/              ← servicio headless + push
        │   │   ├── business/         SchedulerManager, WorkerManager
        │   │   ├── dao/              SchedulerDAO, WorkerDAO
        │   │   ├── domain/           SchedulerTask, WorkerTask
        │   │   ├── web/              ServiceController, TesterController
        │   │   ├── ui/application/   ApplicationDelegate  ← main funcional
        │   │   ├── push/             business/ daemons/ domain/ service/ exceptions/
        │   │   └── notifications/    business/ domain/ service/ exceptions/
        │   ├── ui/                   ← escritorio Swing
        │   │   ├── application/      ApplicationDelegate  ← main declarado en el manifest
        │   │   ├── navigation/       NavigationController, Frame/Dialog/WindowController…
        │   │   ├── controls/         CheckBox, RadioButton, LabelField, ImageField,
        │   │   │                     ScrollBarUI, TabPanel, WrapLayout, table/
        │   │   ├── components/       plot/ (JFreeChart), search/, viewer/
        │   │   └── themes/           ThemeFactory, ThemeStyle, *Style
        │   └── provider/             ← reservado
        ├── net/johnewart/gearman/    ← cliente Gearman embebido
        └── res/                      ← recursos (web, fuentes, i18n, assets, temas, versión)
```

---

## Puntos de entrada (`main`)

El proyecto contiene **tres** clases `ApplicationDelegate`, una por tipo de aplicación:

| Clase | Estado | Función |
|---|---|---|
| `com.assembly.ui.application.ApplicationDelegate` | **Vacío** | Es la `mainClass` declarada en el manifest del JAR (`pom.xml`). |
| `com.assembly.service.ui.application.ApplicationDelegate` | **Funcional** | Arranca scheduler + workers + servidor web. Es el punto de entrada operativo. |
| `com.assembly.provider.ui.application.ApplicationDelegate` | **Vacío** | Reservado para integraciones de proveedor. |

> ⚠️ **Importante:** el `mainClass` del manifest apunta al delegado de UI, que actualmente está vacío.
> Para arrancar el servicio hay que invocar explícitamente la clase de servicio (ver
> [Compilación y ejecución](#compilación-y-ejecución)).

El `main` funcional del servicio hace, en orden:

```java
SchedulerManager schedulerManager = new SchedulerManager();
schedulerManager.initialize();                 // carga y planifica tareas cron desde BD

WorkerManager workerManager = new WorkerManager();
workerManager.initialize();                     // arranca workers Gearman desde BD

WebServer.instance().resource("/service", ServiceController.class, ContentType.WEB,  Security.PUBLIC);
WebServer.instance().resource("/tester",  TesterController.class,  ContentType.JSON, Security.PUBLIC);
WebServer.instance().start(8080);               // servidor HTTP en el puerto 8080
```

---

## El núcleo (`com.assembly.core`)

### `web` — Servidor HTTP propio
Servidor ligero construido sobre `com.sun.net.httpserver.HttpServer` (sin Servlet container).

- **`WebServer`** (singleton): registra rutas con `resource(uri, ControllerClass, ContentType, Security)`,
  gestiona credenciales para rutas `PRIVATE` (HTTP Basic Auth) y arranca el servidor con un
  `CachedThreadPool`.
- **`WebProvider`** (`HttpHandler`): enruta peticiones, gestiona cabeceras CORS/cache, sirve recursos
  estáticos desde `/res/web/` y renderiza la respuesta según `ContentType`: **WEB** (plantillas
  Mustache vía jmustache) o **JSON** (serialización con GSON).
- **`WebController`** (abstracta): contrato `Object execute(RequestExchange)` que implementan los
  controladores concretos.
- **`RequestExchange` / `ResponseExchange`**: envoltorios de petición/respuesta. `RequestExchange`
  parsea *query string*, cuerpos `key=value` y *multipart* (Apache Commons FileUpload, incluyendo
  ficheros subidos). `ResponseExchange` normaliza estado (`OK` / `ERROR` / `EXCEPTION`), mensaje y
  resultados.
- **`WebAuthenticator`** (extiende `BasicAuthenticator`): autenticación por cabecera Basic o por
  parámetros `username` / `password`.

### `config` — Configuración
- **`ConfigManager`** (singleton): localiza el fichero `.env` más reciente del directorio de trabajo y
  carga el `.properties` correspondiente desde `/res/config/`. Parsea hasta 1.000 definiciones de base
  de datos (`resources.db{0..999}.*`) y la configuración de *pool* por driver (`resources.pool.{driver}.*`).
  Lanza `EnvironmentRequiredException` si no encuentra `.env`.
- **`ConfigProperties`**: lector de `.properties` con soporte de valores entrecomillados y sintaxis de
  array (`clave[]`).
- **`ConfigReference`** (enum): claves de configuración tipadas (Gearman, correo SMTP, certificados
  APNs, clave FCM, lista de servicios push…).
- **`DatabaseSession`** / **`PoolAdapter`**: DTOs de conexión a BD y de configuración de pool (máximo,
  idle, mínimo…).

### `source/db` — Acceso a datos multi-motor
- **`DBConnector`** (factory): `instance(alias)` devuelve el `ConnectorInterface` adecuado según el
  `DriverAdapter` del alias. Resuelve esquemas (`schema(source[, table])`).
- **`ConnectorInterface`**: API homogénea — `prepare()`, `bind()`, `read()` → `ResultSet`,
  `cache()` → `CachedRowSet`, `write()` (con soporte de *identity*/clave generada), `transaction()`
  (`BEGIN`/`COMMIT`/`ROLLBACK`) y `dispose()`.
- **Conectores**:
  - **`SQLiteConnector`** — fichero local (`jdbc:sqlite:{alias}.db`), sin pool.
  - **`MysqlConnector`** — `com.mysql.cj.jdbc.Driver`, *pool* opcional con Apache DBCP2.
  - **`SQLServerConnector`** — driver MS SQL, *pool* opcional con DBCP2; *identity* vía `SCOPE_IDENTITY()`.
- **`NamedParameterStatement`**: envoltorio de `PreparedStatement` con parámetros con nombre
  (`:nombre`), respetando comillas y permitiendo reutilizar un mismo parámetro.

### `source/cache` — Caché por reflexión
Caché local sobre SQLite (en disco `{Clase}.cache` o en memoria con `@CacheMemory`). Los campos
anotados con **`@Cache(column, type)`** se mapean a columnas; `CacheManager.cache(Clase.class)` genera
dinámicamente la tabla y permite `write()` (reemplaza), `queue()` (añade), `read()`, `fetch()`,
`dequeue()` y `empty()`. Acceso protegido con `ReentrantLock`.

### `source/api` y `source/service`
- **`APIConnector`** (singleton): cliente HTTP `POST` con parámetros `x-www-form-urlencoded` y parseo
  de respuesta JSON con GSON.
- **`ProviderService`**: sustitución de plantillas `{source.table}` / `{source, table}` en SQL por el
  esquema cualificado real (vía `DBConnector.schema`).
- **`source/cube`**: paquete reservado (clases vacías) para analítica tipo OLAP.

### `daemons` — Planificador y workers
- **`Scheduler`** (abstracta): al instanciarse ejecuta el método `cron()` y registra el resultado
  (estado, mensaje, duración, contador) en la tabla `system_cron`, identificando la ejecución por el
  MD5 del nombre de la clase.
- **`Worker`** (abstracta): crea un `NetworkGearmanWorkerPool` con `poolsize` hilos, registra como
  función el nombre canónico de la clase y delega cada trabajo en el método `work(WorkEvent)`.

### Concurrencia y eventos
- **`monitor`** (`MonitorProtocol`): ejecución de tareas según expresión cron (cron-utils, zona
  `Europe/Madrid`), modo `IMMEDIATE` / `DELAYED`, con notificación a observadores en el hilo de Swing.
- **`observer`** (`ObserverCenter`): publicación/suscripción asíncrona por tipo de `ObserverQuery`,
  procesada en hilo dedicado con `PriorityQueue`.
- **`operations`** (`OperationProtocol`): ejecución asíncrona de operaciones con *pool* de hilos y
  callbacks `operationSuccess` / `operationError`.

### Servicios transversales
- **`secure`** — `EncryptionManager` (singleton): cifrado simétrico **AES/ECB/PKCS5Padding** (clave de
  128 bits) con codificación Base64 + URL, y *hashing* unidireccional **MD5 / SHA-256** (enum `Encryption`).
- **`mailer`** — `MailerTemplate`: composición de correo HTML+texto con plantillas Mustache (`.html` /
  `.txt`) y envío SMTP (JavaMail, STARTTLS/SMTPS) usando configuración de `ConfigManager`.
- **`reports`** — `PDFReport`: generación de PDF a partir de plantillas ODT (XDocReport + Velocity) y
  visualización con `com.sun.pdfview`.
- **`preferences`** — `Preferences`: persistencia de configuración clave-valor en ficheros `.ini`
  mapeados por reflexión a los campos de clases `PreferencesProtocol`.
- **`version`** — `VersionBundle`: lee `/res/version/version.properties` (identificador, nombre,
  versión, build, marca temporal).
- **`i18n`** — `Localizable`: cadenas localizadas desde `/res/i18n/{idioma}.xml` (formato XML de
  `java.util.Properties`).
- **`trace`** — `Trace`: fachada de logging sobre **Log4j 1.x**. Solo activa el logging si existe el
  directorio `logs/` en el directorio de trabajo; en ese caso escribe en consola y en
  `logs/assembly.log` (rotación a 32 MB, 8 *backups*).
- **`resources`** — `RelativeResource`: carga de recursos con *fallback* sistema de ficheros →
  *classpath* y caché de la ubicación detectada.
- **`commons`** — `FileHelper` (normalización de rutas), `TextFormatter` (recorte con máscara).

---

## Capa de servicio (`com.assembly.service`)

Orquesta tareas programadas y procesamiento asíncrono basado en Gearman, con el subsistema de
notificaciones *push* como caso de uso principal.

### Gestión de tareas
- **`SchedulerManager`**: al `initialize()` carga los planificadores habilitados (`system_scheduler`)
  vía `SchedulerDAO`, y envuelve cada uno en un `MonitorProtocol` que lo instancia por reflexión según
  su expresión cron.
- **`WorkerManager`**: al `initialize()` carga los workers habilitados (`system_worker`) vía
  `WorkerDAO` y lanza un hilo por worker, instanciándolo por reflexión con su `poolsize`.
- **DAOs y dominio**: `SchedulerDAO`/`WorkerDAO` persisten estado y contadores; `SchedulerTask` y
  `WorkerTask` son los POJOs correspondientes.
- **Controladores web**: `ServiceController` (`/service`, devuelve la versión de la aplicación) y
  `TesterController` (`/tester`, eco de parámetros en JSON).

### Notificaciones *push* (`service.push`)
- **`PushScheduler`** (extiende `Scheduler`): consulta notificaciones programadas, ejecuta sus
  consultas SQL, renderiza plantillas Mustache, encola los mensajes en `system_notifications_queue` y
  los envía a Gearman.
- **`PushWorker`** (extiende `Worker`): consume trabajos de Gearman, reconstruye el `PushPacket` y lo
  entrega a través de `PushManager`.
- **`PushManager`**: encadena los servicios de entrega (`ServiceApns`, `ServiceFcm` y los extra
  configurados en `ConfigReference.PushServices`); cada uno implementa **`PushInterface`**
  (`tokens(username)` + `push(packet)`).
  - **`ServiceApns`** — APNs vía librería **JavaPNS** (certificado + passphrase, producción/sandbox por
    configuración). Tokens válidos de los últimos 32 días.
  - **`ServiceFcm`** — FCM (Android) vía HTTP `POST` a `https://fcm.googleapis.com/fcm/send`. Tokens
    válidos de los últimos 28 días.
- **`PushPacket`** / **`PushTokenException`**: DTO de la notificación y excepción si ningún servicio
  logra entregar.

### Cola de notificaciones (`service.notifications`)
- **`QueriesService`**: evalúa las consultas programadas (`service_notifications`) contra su cron
  (cron-utils, `Europe/Madrid`), ejecuta el SQL, renderiza `summary`/`layout` con Mustache y genera
  `Queue` en modo `DIRECTED` (un usuario) o `BROADCAST` (todos los usuarios activos).
- **`QueueService`**: ciclo de vida de la cola con estados `PENDING → QUEUED → PROGRESS →
  COMPLETED / EXCEPTION`, reintentos (hasta 32 intentos) y purga de registros con más de 16 días.
- **`QueueManager`**: serializa cada `Queue` a JSON (Jackson) y lo envía a Gearman con prioridad
  `LOW`/`NORMAL`/`HIGH`, usando como callback `Worker.callback(PushWorker.class)`.

**Flujo extremo a extremo:**
`PushScheduler.cron()` → `QueriesService` (SQL + plantillas) → `QueueService.pushQueue()` →
`QueueService.fetchQueue()` → `QueueManager.process()` → **Gearman** → `PushWorker.work()` →
`PushManager.pushService()` → `ServiceApns` + `ServiceFcm` → actualización de estado en BD.

---

## Capa de UI / escritorio (`com.assembly.ui`)

Aplicación de **Java Swing** con un *framework* propio de presentación.

- **`navigation`** — `NavigationController` (singleton, `CardLayout`) implementa navegación tipo iOS
  (`rootController`, `pushController`, `dismissController`) y apertura de ventanas flotantes
  (`FrameController` no modal / `DialogController` modal) configurables con el enum `Windowed`
  (`Undecorated`, `AlwaysOnTop`, `Modal`…). `ControllerInterface` aporta callbacks de ciclo de vida
  (`viewWillAppear`, `viewDidAppear`…).
- **`controls`** — controles personalizados: `CheckBox`/`RadioButton` con iconos PNG propios,
  `LabelField` (con soporte HTML y alineación), `ImageField` (escalado e imágenes `@2x` para HiDPI),
  `ScrollBarUI` (barra de scroll a medida), `TabPanel` (pestañas dinámicas) y `WrapLayout` (un
  `FlowLayout` que envuelve en varias líneas).
- **`controls/table`** — `TableFactory` construye `JTable` de solo lectura con estilo por columna,
  ancho fijo/porcentual/relleno, filas alternas y *renderers* propios (`HeaderCellFactory`,
  `DefaultCellFactory` con ajuste de texto y altura dinámica). `TableAdapter` expone eventos de
  clic simple / doble / secundario. `syncronize()` enlaza listas de objetos por reflexión.
- **`components/plot`** — gráficas sobre **JFreeChart**: `BarPlot` y `LinePlot` (series temporales y
  XY) implementados; `ColumnPlot` y `PiePlot` como esqueletos. Contrato común `PlotInterface`
  (`add`, `stylize`, `render`).
- **`components`** — `SearchModule` y `PDFViewer` son *stubs* (la visualización real de PDF está en
  `core.reports.PDFReport`).
- **`themes`** — sistema de *theming* programático: `ThemeStyle` (clase central, ~1.200 líneas:
  tipografía, colores, alineación, bordes por posición, *padding*/*margin*, anchos, modo solo
  lectura, estilo de cabecera) con métodos `stylize(...)` para cada tipo de componente Swing.
  `ThemeFactory` es el registro de estilos por clase de componente. Enums de apoyo: `AlignmentStyle`,
  `BorderStyle`, `DimensionStyle`, `FontStyle`, `PositionStyle`. Carga fuentes TTF desde `/res/fonts/`.

---

## Cliente Gearman embebido

El paquete `net.johnewart.gearman.client` incluye una implementación de cliente/worker de **Gearman**
(complementa la dependencia `gearman-common`):

- **`NetworkGearmanClient`** — envío de trabajos (`submitJobInBackground`, `submitJob` síncrono,
  `getStatus`) con *pool* de conexiones y *failover*.
- **`NetworkGearmanWorker`** — hilo worker que registra funciones (`registerCallback`) y procesa
  trabajos en bucle (`doWork`).
- **`NetworkGearmanWorkerPool`** — *pool* de workers con API *builder* (`threads()`, `withConnection()`,
  `build()`).

---

## Dependencias

Declaradas en `Assembly/pom.xml`:

| Dependencia | Versión | Uso |
|---|---|---|
| `log4j:log4j` | 1.2.17 | Logging (`core.trace.Trace`) |
| `org.apache.commons:commons-dbcp2` | 2.2.0 | Pool de conexiones JDBC |
| `com.google.code.gson:gson` | 2.8.2 | JSON (web, API) |
| `com.samskivert:jmustache` | 1.14 | Plantillas Mustache (web, correo, notificaciones) |
| `fr.opensagres.xdocreport:xdocreport` | 2.0.1 | Generación de informes (ODT → PDF, Velocity) |
| `com.kenai.nbpwr:com-sun-pdfview` | 1.0.5 | Visor de PDF |
| `org.jfree:jfreechart` | 1.0.19 | Gráficas |
| `org.xerial:sqlite-jdbc` | 3.21.0.1 | Driver SQLite (también caché) |
| `mysql:mysql-connector-java` | 6.0.6 | Driver MySQL |
| `com.microsoft.sqlserver:mssql-jdbc` | 6.2.2.jre8 | Driver SQL Server |
| `com.cronutils:cron-utils` | 8.1.1 | Expresiones cron (monitor / scheduler) |
| `org.ocpsoft.prettytime:prettytime` | 4.0.1 | Formato de tiempos relativos |
| `commons-fileupload:commons-fileupload` | 1.3.3 | *Multipart* en el servidor web |
| `javax.servlet:servlet-api` | 2.5 | API de servlet (compilación) |
| `com.fasterxml.jackson.core:jackson-core` | 2.9.5 | JSON (cola de notificaciones) |
| `com.github.fernandospr:javapns-jdk16` | 2.4.0 | Push APNs (iOS) |
| `javax.mail:mail` | 1.4.7 | Envío SMTP |
| `net.johnewart.gearman:gearman-common` | 0.8.11-SNAPSHOT | Protocolo Gearman |

> El `gearman-common` es un *SNAPSHOT* y se resuelve desde el repositorio
> `https://oss.sonatype.org/content/repositories/snapshots` (declarado en el `pom.xml`).

---

## Compilación y ejecución

### Requisitos
- **JDK 1.8** (el proyecto usa APIs internas `com.sun.net.httpserver` y `com.sun.pdfview`).
- **Apache Maven 3.x**.

### Compilar

```bash
cd Assembly
mvn clean package
```

El `maven-assembly-plugin` genera un *uber-jar* con todas las dependencias:
`Assembly/target/Assembly-0.15-jar-with-dependencies.jar`.

> Durante la fase `initialize`, el `maven-antrun-plugin` regenera
> `src/res/version/version.properties` e incrementa automáticamente `bundle.build`.

### Ejecutar

El manifest apunta a `com.assembly.ui.application.ApplicationDelegate` (vacío). Para arrancar el
**servicio** (scheduler + workers + servidor web en el puerto 8080), invoca directamente su `main`:

```bash
java -cp target/Assembly-0.15-jar-with-dependencies.jar \
     com.assembly.service.ui.application.ApplicationDelegate
```

Una vez arrancado, los *endpoints* de prueba quedan disponibles:

```bash
curl http://localhost:8080/service     # versión de la aplicación (HTML)
curl http://localhost:8080/tester       # eco de parámetros (JSON)
```

> Para arrancar la aplicación de escritorio, implementa el `main` de
> `com.assembly.ui.application.ApplicationDelegate` (hoy vacío) usando el `NavigationController`.

---

## Configuración

### Ficheros de entorno
`ConfigManager` busca el fichero **`.env`** más reciente del directorio de trabajo y carga el
`.properties` asociado desde `/res/config/`. Si no encuentra ningún `.env`, lanza
`EnvironmentRequiredException`.

Formato esperado de las propiedades:

```properties
# Definiciones de base de datos (índices 0..999)
resources.db0.alias    = main
resources.db0.adapter  = MYSQL          # NONE | SQLITE | MYSQL | MSSQL
resources.db0.server   = 127.0.0.1
resources.db0.port     = 3306
resources.db0.dbname   = assembly
resources.db0.username = user
resources.db0.password = secret
resources.db0.schema   = dbo

# Pool por driver (DBCP2)
resources.pool.MYSQL.enable  = 1
resources.pool.MYSQL.maximum = 8
resources.pool.MYSQL.idle    = 8
resources.pool.MYSQL.minimum = 0
```

### Claves de `ConfigReference`
Configuración tipada disponible en `core.config.ConfigReference`:

| Ámbito | Claves |
|---|---|
| **Gearman** | `GearmanServer`, `GearmanPort` |
| **Correo SMTP** | `MailTransportHost`, `MailTransportPort`, `MailTransportUsername`, `MailTransportPassword`, `MailDefaultFromEmail`, `MailDefaultFromName`, `MailDefaultReplyToEmail`, `MailDefaultReplyToName` |
| **Push APNs** | `PushAPNSCertificate`, `PushAPNSPassphrase`, `PushAPNSSandbox` |
| **Push FCM** | `PushFCMKey` |
| **Push (extra)** | `PushServices` (lista de clases `PushInterface` adicionales) |

### Logging
El logging (Log4j) **solo se activa si existe el directorio `logs/`** en el directorio de trabajo.
Si existe, se escribe en consola y en `logs/assembly.log` (rotación a 32 MB, hasta 8 ficheros).

---

## Modelo de datos del sistema

La capa de servicio espera estas tablas (motor configurado vía `ConfigManager`):

| Tabla | Uso |
|---|---|
| `system_cron` | Registro de ejecuciones de `Scheduler` (estado, mensaje, duración, contador). |
| `system_scheduler` | Planificadores registrados (clase, expresión cron, estado, contador…). |
| `system_worker` | Workers registrados (clase, `poolsize`, estado, contador…). |
| `service_notifications` | Consultas de notificación programadas (SQL, plantillas, cron, modo). |
| `system_notifications_queue` | Cola de notificaciones por entregar (estado, intentos, prioridad). |
| `system_session` / `system_session_token` | Sesiones y tokens de dispositivo (plataforma `APPLE` / `ANDROID`) para push. |

---

## Recursos (`src/res`)

```
res/
├── web/
│   ├── css/styles.css                  fondo gris claro para la UI web
│   ├── js/jquery-1.12.4.min.js         jQuery
│   ├── ServiceController.html          formulario de prueba del endpoint /tester
│   └── ServiceController.js            envío AJAX + manejo de errores
├── i18n/
│   ├── en.xml                          cadenas localizadas (inglés)
│   └── es.xml                          cadenas localizadas (español)
├── fonts/
│   ├── Roboto-Regular.ttf
│   ├── OpenSans-Regular.ttf
│   └── HelveticaNeue-Regular.ttf
├── assets/
│   ├── checkon.png / checkoff.png      estados de CheckBox
│   └── radioon.png / radiooff.png      estados de RadioButton
├── themes/theme.css                    estilos del theming Swing (no parseado aún)
└── version/version.properties          metadatos de versión (auto-generado en build)
```

---

## Notas, limitaciones y deuda técnica

- **Proyecto *legacy* sobre Java 8.** Usa APIs internas del JDK (`com.sun.net.httpserver`,
  `com.sun.pdfview`), por lo que está acoplado a JDK 8 y no compila tal cual en JDK modernos sin ajustes.
- **`mainClass` del manifest vacío.** El JAR ejecutable apunta a la UI vacía; el servicio debe
  lanzarse por su clase concreta.
- **Componentes sin terminar / *stubs*:** `source/cube/*`, `components/search/SearchModule`,
  `components/viewer/PDFViewer`, y los gráficos `ColumnPlot` / `PiePlot`. `ThemeFactory` aún no parsea
  `theme.css` (el *theming* se aplica de forma programática con `ThemeStyle`).
- **Seguridad:** `EncryptionManager` usa una **clave AES embebida en el código** y modo **ECB**, y el
  `MailerTemplate` confía en todos los certificados TLS. No apto para producción sin endurecimiento
  (gestión de claves, modo GCM/CBC con IV, validación de certificados).
- **Dependencias antiguas** (Log4j 1.x EOL, drivers JDBC antiguos, `gearman-common` SNAPSHOT). Conviene
  evaluar actualizaciones/migraciones antes de un uso productivo.
- **Zona horaria fija** `Europe/Madrid` codificada en monitor, scheduler y servicio de consultas.

---

<sub>Assembly v0.15 · `com.assembly` · Java 8 · build 773 (2021-10-26)</sub>
