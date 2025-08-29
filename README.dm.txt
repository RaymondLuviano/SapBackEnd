# SAP_EXAMEN_PRACTICO
Examen practico para demostrar conocimientos técnicos


Este proyecto implementa una **Página principal (Dashboard)** con tres tarjetas clave (En la raíz del backend se encuentran las colecciones postman y el script de bd)

1. **Historial de transacciones**: gráfico circular con la **suma de transferencias por cuenta destino** (se asume misma moneda entre cuentas destino).
2. **Gastos principales**: **Top 3** por concepto de gasto (se requiere registrar conceptos).
3. **Saldo actual**: **tabla** con el resumen de cuentas del usuario.

Incluye **login con JWT**, backend en Spring Boot 3 y base de datos **PostgreSQL** (esquema `sap`.

---

##  Enfoque de diseño

* **Front (Angular standalone)**
  Se toma la decisión de desarrollar este proyecto con Angular ya que es un reto el hecho de retomar esta tecnología ya que no había trabajado con ella desde hace varios años
  * Angular sin NgModules (standalone components), **router** y **HttpClient**.
  * Autenticación con **servicio Auth**, **interceptor** para adjuntar `Authorization: Bearer <token>`, **guard** para proteger rutas, y **guest guard** opcional para evitar que usuarios logueados vean el login.
  * Dashboard con **`ng2-charts` + `chart.js`**; se registra `ChartJS.register(...registerables)` para evitar errores de controladores no registrados.
  * **Proxy de dev** (`proxy.conf.json`) para redirigir `/api` a `http://localhost:8080` y evitar CORS.

* **Back (Spring Boot 3 + JPA)**
En este backend cabe destagar que se cuenta con más experiencia y conocimiento de la creación de Servicios REST y consumo es por eso que se mantiene el uso del framework con SPRING
  * Capas: **Controller → Service → Repository**.
  * Endpoints REST: `/api/auth/login` (JWT), `/api/dashboard/**` para agregaciones, `/api/concepts` para registrar/listar conceptos.
  * **DTOs** para respuesta del dashboard: `PieSlice`, `TopExpense`, `AccountRow`, `DashboardSummary`.
  * Seguridad: filtro JWT (o `permitAll()` temporal en desarrollo). UserId fijo a `1L` en modo demo; en producción se resuelve desde el **JWT** (`auth.getName()` → `users.id`).

* **DB (PostgreSQL)** 
  Se decide uilizar Postgres ya que es una de las basese de datos menos complicadas de configurar, además de que su implementación con JPA se conoe bastante. (Se adjunta script para la    creaión del esquema SAP)
  * Esquema `sap` con **llaves primarias simples** (BIGSERIAL) y **FK simples**.
  * Tablas: `users`, `accounts`, `expense_concepts`, `transactions`.
  * `transactions.type` con `CHECK` (`DEPOSIT|WITHDRAWAL|TRANSFER|EXPENSE`), `occurred_at` como `TIMESTAMPTZ`.
  * **Seed** de ejemplo para probar dashboard.
* **EnPoints**
*Se adjunta colección para validar los endpoints desde postman

##  Prerrequisitos

* **Java 17+**, **Maven 3.9+**
* **PostgreSQL 14+** (usuario con permisos para crear esquema y tablas)
* **Node.js 18/20 LTS**, **Angular CLI**

```bash
node -v
npm -v
ng version
java -version
psql --version
```

---

##  Base de datos (PostgreSQL)

### Crear esquema y tablas

Ejecutar Script en el manejador de base de datos de su preferencia



##  Backend (Spring Boot)

### `application.properties`

Debemos asegurarnos que este correctamente configurado nuestro archivo de propiedades en local.

```properties
server.port=8080

spring.datasource.url=jdbc:postgresql://localhost:5432/bankdb
spring.datasource.username=TU_USUARIO
spring.datasource.password=TU_PASSWORD

spring.jpa.hibernate.ddl-auto=none
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.properties.hibernate.default_schema=sap
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

# logs extra
# logging.level.org.springframework.security=DEBUG
```

### Construir y ejecutar


cd backend
mvn clean package
mvn spring-boot:run


En la consola deberías ver: `Started ... on port(s): 8080`.

### Endpoints relevantes

* `POST /api/auth/login` → `{ username, password }` → `{ accessToken }`
* `GET  /api/dashboard/summary`
* `GET  /api/dashboard/transfers-pie`
* `GET  /api/dashboard/top-expenses`
* `GET  /api/dashboard/account-balances`
* `POST /api/concepts` `{ name }` (crea si no existe para el usuario)
* `GET  /api/concepts`

> En modo demo, `userId = 1L`. En prod, resolver desde JWT.

### cURL rápido

```bash
# login
TOKEN=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H 'Content-Type: application/json' \
  -d '{"username":"admin@example.com","password":"123456"}' | jq -r .accessToken)

# summary
curl -i http://localhost:8080/api/dashboard/summary -H "Authorization: Bearer $TOKEN"
```

---

## Frontend (Angular)

### Instalación

```bash
cd web
npm install
npm i chart.js ng2-charts
```

### Proxy para backend

`web/proxy.conf.json`:

```json
{
  "/api": {
    "target": "http://localhost:8080",
    "secure": false,
    "changeOrigin": true,
    "logLevel": "debug"
  }
}
```

`package.json`:

```json
"start": "ng serve --proxy-config proxy.conf.json"
```

### Chart.js (registro de controladores)

En `dashboard.page.ts` (o globalmente):

```ts
import { Chart as ChartJS, registerables } from 'chart.js';
ChartJS.register(...registerables);
```

### Rutas (ir primero a login y proteger dashboard)

```ts
export const routes: Routes = [
  { path: 'login', loadComponent: () => import('./features/login/login').then(m => m.Login) },
  { path: 'dashboard', loadComponent: () => import('./features/dashboard/dashboard.page').then(m => m.DashboardPage), canActivate: [authGuard] },
  { path: '', redirectTo: 'login', pathMatch: 'full' },
  { path: '**', redirectTo: 'login' }
];
```

### Ejecutar

```bash
npm start
# abre http://localhost:4200
```

### Login y flujo

* Inicia sesión con `admin@example.com / 123456`.
* Tras login, el front navega a `/dashboard`.

---

## Pruebas rápidas

* **Backend directo** (sin front): usa los cURL anteriores.
* **Front**: abre DevTools → Network → confirma que `POST /api/auth/login` devuelve 200 y que `GET /api/dashboard/summary` trae datos.

---

