# 🚀 CI/CD Setup — Bookworm Backend

![GitHub Actions](https://img.shields.io/badge/CI%2FCD-GitHub_Actions-2088FF.svg?logo=githubactions&logoColor=white)
![SonarCloud](https://img.shields.io/badge/Code_Quality-SonarCloud-F3702A.svg?logo=sonarcloud&logoColor=white)
![Docker](https://img.shields.io/badge/Container-Docker-2496ED.svg?logo=docker&logoColor=white)
![OWASP](https://img.shields.io/badge/Security-OWASP-000000.svg?logo=owasp&logoColor=white)
![CodeQL](https://img.shields.io/badge/SAST-CodeQL-5B5EA6.svg?logo=github&logoColor=white)

---

## 📋 Tabla de Contenidos

1. [Diagrama General del Pipeline](#-diagrama-general-del-pipeline)
2. [Descripción de Cada Etapa](#-descripción-de-cada-etapa)
3. [Flujo de Ramas](#-flujo-de-ramas)
4. [Configuración de GitHub Secrets y Variables](#-configuración-de-github-secrets-y-variables)
5. [Configuración de SonarCloud](#-configuración-de-sonarcloud)
6. [Configuración de Branch Protection Rules](#-configuración-de-branch-protection-rules)
7. [Estructura de Archivos CI/CD](#-estructura-de-archivos-cicd)
8. [Ejecución Local de Tests](#-ejecución-local-de-tests)
9. [Troubleshooting](#-troubleshooting)

---

## 🔄 Diagrama General del Pipeline

```
                    ┌─────────────────────────────────┐
                    │    PUSH / PR hacia test o main   │
                    └──────────────┬──────────────────┘
                                   │
                    ┌──────────────▼──────────────────┐
                    │       JOB 1: COMPILACIÓN         │
                    │  ☕ JDK 25 + Maven + Cache        │
                    │  📦 Compila proyecto + genera JAR │
                    └──────────────┬──────────────────┘
                                   │
                    ┌──────────────┴──────────────────┐
                    │                                  │
        ┌───────────▼──────────┐        ┌─────────────▼────────────┐
        │  JOB 2: TESTS UNIT.  │        │ JOB 3: TESTS INTEGRACIÓN │
        │ 🧪 JUnit + Mockito   │        │ 🔗 Testcontainers + PG   │
        │ 📊 JaCoCo Coverage   │        │ 🐘 PostgreSQL real       │
        └───────────┬──────────┘        └─────────────┬────────────┘
                    │                                  │
                    └──────────────┬──────────────────┘
                                   │
            ┌──────────────────────┼──────────────────────┐
            │                      │                      │
┌───────────▼──────────┐ ┌────────▼────────┐ ┌───────────▼──────────┐
│ JOB 4: SONARCLOUD    │ │ JOB 5: OWASP    │ │ JOB 6: DOCKER BUILD  │
│ 📊 Quality Gate      │ │ 🛡️ Dep-Check    │ │ 🐳 Multi-stage       │
│ 🐛 Bugs + Smells     │ │ 🔍 CVE Scan     │ │ 📤 Push a ghcr.io    │
└───────────┬──────────┘ └────────┬────────┘ └───────────┬──────────┘
            │                      │                      │
            └──────────────────────┼──────────────────────┘
                                   │
                    ┌──────────────▼──────────────────┐
                    │   JOB 7: DESPLIEGUE STAGING      │
                    │  🚀 Solo rama test               │
                    │  ✅ Smoke tests                   │
                    └──────────────────────────────────┘
```

### Pipeline Secundario: CodeQL (SAST)

```
    Push/PR/Cron → CodeQL Init → Auto-build → Análisis → Resultados en Security Tab
```

---

## 📝 Descripción de Cada Etapa

### JOB 1: Compilación (Build)

| Aspecto | Detalle |
|---------|---------|
| **Propósito** | Verificar que el código compila sin errores |
| **Herramientas** | Maven, JDK 25 (Temurin) |
| **Artefactos** | JAR del proyecto |
| **Duración estimada** | 2-4 minutos |

**¿Qué hace?**
1. Clona el repositorio con historial completo (`fetch-depth: 0`)
2. Configura JDK 25 usando la distribución Temurin
3. Restaura el cache de dependencias Maven (`~/.m2/repository`)
4. Ejecuta `mvn clean compile` para verificar compilación
5. Ejecuta `mvn package -DskipTests` para generar el JAR
6. Sube el JAR como artefacto para jobs posteriores

---

### JOB 2: Tests Unitarios

| Aspecto | Detalle |
|---------|---------|
| **Propósito** | Validar la lógica de negocio de forma aislada |
| **Herramientas** | JUnit 5, Mockito, JaCoCo |
| **Perfil Spring** | `test` (H2 en memoria) |
| **Convención** | Excluye archivos `*IT.java` y `*IntegrationTest.java` |
| **Plugin Maven** | `maven-surefire-plugin` |
| **Duración estimada** | 1-3 minutos |

**¿Qué hace?**
1. Ejecuta `mvn test` con el perfil `test` activo
2. Maven Surefire ejecuta todos los tests EXCEPTO los de integración
3. JaCoCo instrumenta el código para medir cobertura
4. Genera reportes XML/HTML en `target/site/jacoco/`
5. Los reportes se suben como artefactos para SonarCloud

---

### JOB 3: Tests de Integración

| Aspecto | Detalle |
|---------|---------|
| **Propósito** | Validar el flujo completo con base de datos real |
| **Herramientas** | Testcontainers, PostgreSQL 16, Flyway |
| **Perfil Spring** | `integration` |
| **Convención** | SOLO ejecuta archivos `*IT.java` e `*IntegrationTest.java` |
| **Plugin Maven** | `maven-failsafe-plugin` |
| **Duración estimada** | 3-8 minutos |

**¿Qué hace?**
1. Ejecuta `mvn verify` con Failsafe (saltando Surefire)
2. Testcontainers levanta un contenedor PostgreSQL 16 automáticamente
3. Flyway ejecuta las migraciones reales del proyecto (`V1__`, `V2__`, etc.)
4. Los tests validan register, login, y validaciones end-to-end
5. Al finalizar, el contenedor se destruye automáticamente

---

### JOB 4: Calidad de Código (SonarCloud)

| Aspecto | Detalle |
|---------|---------|
| **Propósito** | Análisis estático de calidad y seguridad |
| **Herramienta** | SonarCloud (SaaS gratuito para Open Source) |
| **Quality Gate** | El pipeline FALLA si no se cumple |
| **Métricas** | Bugs, Code Smells, Vulnerabilidades, Cobertura, Duplicación |
| **Duración estimada** | 2-5 minutos |

**¿Qué hace?**
1. Descarga los reportes de cobertura JaCoCo del Job 2
2. Ejecuta `mvn sonar:sonar` con el token de SonarCloud
3. SonarCloud analiza el código fuente Java completo
4. Espera el resultado del Quality Gate (`sonar.qualitygate.wait=true`)
5. Si el Quality Gate no pasa → **el pipeline FALLA**

---

### JOB 5: Escaneo de Seguridad (OWASP)

| Aspecto | Detalle |
|---------|---------|
| **Propósito** | Detectar dependencias con vulnerabilidades conocidas |
| **Herramienta** | OWASP Dependency-Check (SCA) |
| **Base de datos** | NVD (National Vulnerability Database) del NIST |
| **Umbral CVSS** | Falla si CVSS ≥ 7 (vulnerabilidad Alta o Crítica) |
| **Duración estimada** | 5-15 minutos |

**¿Qué hace?**
1. Ejecuta `mvn verify -Powasp` (activa el perfil OWASP)
2. Descarga la base de datos de vulnerabilidades NVD
3. Analiza TODAS las dependencias Maven (directas y transitivas)
4. Genera reportes HTML y JSON en `target/owasp-reports/`
5. Si encuentra CVE con CVSS ≥ 7 → **el pipeline FALLA**

---

### JOB 6: Docker Build & Push

| Aspecto | Detalle |
|---------|---------|
| **Propósito** | Construir imagen Docker optimizada y publicarla |
| **Registry** | GitHub Container Registry (`ghcr.io`) |
| **Dockerfile** | Multi-stage (build + runtime) |
| **Etiquetas** | `branch-name`, `sha-abc1234`, `latest` (solo main) |
| **Duración estimada** | 3-8 minutos |

**¿Qué hace?**
1. Configura Docker Buildx para builds optimizados
2. Se autentica con GitHub Container Registry
3. Construye la imagen usando Dockerfile multi-stage
4. Publica la imagen con etiquetas automáticas
5. Usa cache de GitHub Actions para builds incrementales

---

### JOB 7: Despliegue a Staging

| Aspecto | Detalle |
|---------|---------|
| **Propósito** | Despliegue simulado al entorno de pruebas |
| **Condición** | Solo ejecuta en push a la rama `test` |
| **Requisitos** | TODOS los jobs anteriores deben ser exitosos |
| **Duración estimada** | < 1 minuto |

**¿Qué hace?**
1. Verifica que todos los jobs previos pasaron
2. Simula un despliegue al entorno de staging
3. Ejecuta smoke tests simulados
4. Genera un resumen en el GitHub Step Summary

> **Nota:** En producción, reemplazar el script simulado con comandos reales (`docker-compose`, `kubectl`, `aws ecs`, etc.)

---

### Pipeline Secundario: CodeQL (SAST)

| Aspecto | Detalle |
|---------|---------|
| **Propósito** | Análisis estático de seguridad del código fuente |
| **Herramienta** | GitHub CodeQL (nativo, 100% gratuito) |
| **Ejecución** | Push, PR, y semanalmente (cron: Lunes 6 AM UTC) |
| **Resultado** | Alertas en la pestaña "Security" del repositorio |

**Vulnerabilidades que detecta:**
- Inyección SQL
- Inyección de comandos OS
- Cross-Site Scripting (XSS)
- Deserialización insegura
- Path traversal
- Exposición de datos sensibles

---

## 🌿 Flujo de Ramas

```
   dev ──(push/PR)──► test ──(push/PR)──► main
    │                   │                   │
    │              CI Pipeline          CI Pipeline
    │              ejecuta aquí         ejecuta aquí
    │                   │                   │
    │              Quality Gate         Quality Gate
    │              OBLIGATORIO          OBLIGATORIO
    │                   │                   │
    │              Deploy Staging       Deploy Prod
    │              (automático)         (manual/futuro)
    │                   │                   │
    ▼                   ▼                   ▼
  Desarrollo         Pruebas            Producción
```

### Reglas del Flujo

1. **`dev` → `test`**: Requiere Pull Request. El pipeline de CI se ejecuta automáticamente.
2. **`test` → `main`**: Requiere Pull Request + Aprobación. El pipeline completo DEBE pasar.
3. **`main`**: Rama protegida. NADIE puede hacer push directo.

---

## 🔑 Configuración de GitHub Secrets y Variables

### Secrets (Valores sensibles y encriptados)

Navega a: **Settings → Secrets and variables → Actions → Secrets → New repository secret**

| Nombre del Secret | Descripción | Cómo obtenerlo |
|---|---|---|
| `SONAR_TOKEN` | Token de autenticación de SonarCloud | Ver [Configuración de SonarCloud](#-configuración-de-sonarcloud) |
| `NVD_API_KEY` | API Key del NIST NVD (opcional, acelera OWASP) | Registrarse en [nvd.nist.gov](https://nvd.nist.gov/developers/request-an-api-key) |

> **Nota:** `GITHUB_TOKEN` se genera automáticamente por GitHub Actions, no necesitas configurarlo.

### Variables (Valores no sensibles)

Navega a: **Settings → Secrets and variables → Actions → Variables → New repository variable**

| Nombre de la Variable | Valor de Ejemplo | Descripción |
|---|---|---|
| `SONAR_ORGANIZATION` | `lassriver-ns` | Tu organización en SonarCloud |
| `SONAR_PROJECT_KEY` | `LassRiver-NS_bookworm-backend` | Clave del proyecto en SonarCloud |

---

## 📊 Configuración de SonarCloud

### Paso 1: Crear cuenta en SonarCloud

1. Ir a [sonarcloud.io](https://sonarcloud.io)
2. Click en **"Log in"** → **"Log in with GitHub"**
3. Autorizar la aplicación de SonarCloud

### Paso 2: Importar el repositorio

1. Click en **"+"** → **"Analyze new project"**
2. Seleccionar la organización de GitHub
3. Seleccionar el repositorio `bookworm-backend`
4. Click en **"Set Up"**

### Paso 3: Configurar el análisis con GitHub Actions

1. En SonarCloud, ir a **"Administration"** → **"Analysis Method"**
2. Desactivar **"Automatic Analysis"** (usaremos GitHub Actions)
3. Seleccionar **"With GitHub Actions"**
4. SonarCloud mostrará el `SONAR_TOKEN` → **Copiarlo**

### Paso 4: Agregar el Secret en GitHub

1. Ir a tu repositorio en GitHub
2. **Settings** → **Secrets and variables** → **Actions**
3. **New repository secret**:
   - Name: `SONAR_TOKEN`
   - Value: (pegar el token copiado)

### Paso 5: Agregar las Variables en GitHub

1. En la misma sección, ir a la pestaña **Variables**
2. **New repository variable**:
   - Name: `SONAR_ORGANIZATION` | Value: `tu-organizacion`
   - Name: `SONAR_PROJECT_KEY` | Value: `tu-org_bookworm-backend`

> **Tip:** El `SONAR_PROJECT_KEY` generalmente tiene el formato: `Organizacion_NombreRepo`

---

## 🔒 Configuración de Branch Protection Rules

### Para la rama `main` (Producción)

1. Ir a **Settings** → **Branches** → **Add branch protection rule**
2. Configurar:

| Configuración | Valor |
|---|---|
| **Branch name pattern** | `main` |
| ✅ **Require a pull request before merging** | Activado |
| ✅ **Required approving reviews** | `2` |
| ✅ **Dismiss stale pull request approvals** | Activado |
| ✅ **Require review from Code Owners** | Activado (si tienes CODEOWNERS) |
| ✅ **Require status checks to pass before merging** | Activado |
| ✅ **Require branches to be up to date** | Activado |
| **Status checks requeridos** (buscar y agregar): | |
| | `🔨 Compilación` |
| | `🧪 Tests Unitarios` |
| | `🔗 Tests de Integración` |
| | `📊 Calidad de Código (SonarCloud)` |
| | `🔍 Análisis CodeQL (Java)` |
| ✅ **Require signed commits** | Recomendado |
| ✅ **Do not allow bypassing the above settings** | **ACTIVADO** |
| ✅ **Restrict who can push** | Solo merge vía PR |
| ❌ **Allow force pushes** | **DESACTIVADO** |
| ❌ **Allow deletions** | **DESACTIVADO** |

### Para la rama `test` (Staging)

1. Ir a **Settings** → **Branches** → **Add branch protection rule**
2. Configurar:

| Configuración | Valor |
|---|---|
| **Branch name pattern** | `test` |
| ✅ **Require a pull request before merging** | Activado |
| ✅ **Required approving reviews** | `1` |
| ✅ **Dismiss stale pull request approvals** | Activado |
| ✅ **Require status checks to pass before merging** | Activado |
| ✅ **Require branches to be up to date** | Activado |
| **Status checks requeridos** (buscar y agregar): | |
| | `🔨 Compilación` |
| | `🧪 Tests Unitarios` |
| | `🔗 Tests de Integración` |
| ✅ **Do not allow bypassing the above settings** | **ACTIVADO** |
| ❌ **Allow force pushes** | **DESACTIVADO** |
| ❌ **Allow deletions** | **DESACTIVADO** |

> ⚠️ **IMPORTANTE:** Los status checks solo aparecerán en el dropdown DESPUÉS de que el pipeline se haya ejecutado al menos una vez. Haz un push inicial a `test` primero.

---

## 📁 Estructura de Archivos CI/CD

```
bookworm-backend/
├── .github/
│   └── workflows/
│       ├── ci-pipeline.yml          # Pipeline principal (7 jobs)
│       └── codeql-analysis.yml      # Análisis SAST con CodeQL
├── src/
│   ├── main/
│   │   ├── java/...                 # Código fuente
│   │   └── resources/
│   │       └── application.yml      # Config principal
│   └── test/
│       ├── java/com/lassriver/bookworm/
│       │   ├── AbstractIntegrationTest.java  # Base para tests de integración
│       │   ├── BookwormApplicationTests.java  # Test de contexto
│       │   ├── controllers/
│       │   │   └── AuthControllerIT.java     # Test integración Auth
│       │   └── services/impl/
│       │       └── UserServiceImplTest.java  # Test unitario UserService
│       └── resources/
│           ├── application-test.yml          # Config H2 (tests unitarios)
│           └── application-integration.yml   # Config Testcontainers
├── Dockerfile                       # Imagen Docker multi-stage
├── .dockerignore                    # Exclusiones para Docker
├── pom.xml                          # Maven (Surefire, Failsafe, JaCoCo, OWASP)
└── CI-CD-SETUP.md                   # Esta documentación
```

---

## 🖥️ Ejecución Local de Tests

### Tests Unitarios (sin Docker)

```bash
# Ejecutar solo tests unitarios (usa H2 en memoria)
./mvnw test -Dspring.profiles.active=test
```

### Tests de Integración (requiere Docker)

```bash
# Ejecutar tests de integración (Testcontainers levanta PostgreSQL)
./mvnw verify -Dsurefire.skip=true -Dspring.profiles.active=integration
```

### Todos los Tests

```bash
# Ejecutar ambos tipos de tests
./mvnw verify
```

### Cobertura de Código (JaCoCo)

```bash
# Generar reporte de cobertura
./mvnw verify

# Ver el reporte HTML
open target/site/jacoco/index.html
```

### Escaneo de Seguridad OWASP (local)

```bash
# Ejecutar OWASP Dependency-Check
./mvnw verify -Powasp -DskipTests

# Ver el reporte HTML
open target/owasp-reports/dependency-check-report.html
```

---

## 🔧 Troubleshooting

### ❌ Error: "JDK 25 not found"

**Problema:** La distribución Temurin aún no tiene JDK 25 disponible.

**Solución:** En `.github/workflows/ci-pipeline.yml`, cambiar la distribución:
```yaml
env:
  JAVA_VERSION: "25"
  JAVA_DISTRIBUTION: "oracle"  # o "corretto"
```

---

### ❌ Error: "Quality Gate failed" en SonarCloud

**Problema:** El código no cumple con los estándares de calidad definidos.

**Solución:**
1. Ir a [sonarcloud.io](https://sonarcloud.io) y revisar los issues detectados
2. Corregir los bugs, code smells o vulnerabilidades reportadas
3. Verificar que la cobertura de tests sea ≥ 80%

---

### ❌ Error: "OWASP Dependency-Check timeout"

**Problema:** La descarga de la base de datos NVD es lenta.

**Solución:**
1. Obtener una API Key gratuita del NVD: [nvd.nist.gov](https://nvd.nist.gov/developers/request-an-api-key)
2. Agregar el secret `NVD_API_KEY` en GitHub

---

### ❌ Error: "Testcontainers - Docker not available"

**Problema:** Docker no está disponible en el entorno de CI/CD.

**Solución:** GitHub Actions runners de Ubuntu ya incluyen Docker. Si usas runners self-hosted, asegúrate de instalar Docker.

---

### ❌ Error: "Status checks not found" en Branch Protection

**Problema:** Los status checks no aparecen en el dropdown.

**Solución:** El pipeline debe ejecutarse al menos una vez para que GitHub registre los nombres de los checks. Haz un push o crea un PR hacia la rama protegida.

---

### ❌ Error: "Permission denied" en Docker Push

**Problema:** No se puede publicar la imagen en GHCR.

**Solución:** Verificar que el workflow tiene el permiso `packages: write`:
```yaml
permissions:
  packages: write
```

---

## 📚 Referencias

- [GitHub Actions Documentation](https://docs.github.com/en/actions)
- [SonarCloud Documentation](https://docs.sonarcloud.io/)
- [OWASP Dependency-Check](https://owasp.org/www-project-dependency-check/)
- [Testcontainers for Java](https://www.testcontainers.org/)
- [JaCoCo Maven Plugin](https://www.eclemma.org/jacoco/trunk/doc/maven.html)
- [GitHub CodeQL](https://codeql.github.com/)
- [Maven Surefire Plugin](https://maven.apache.org/surefire/maven-surefire-plugin/)
- [Maven Failsafe Plugin](https://maven.apache.org/surefire/maven-failsafe-plugin/)
