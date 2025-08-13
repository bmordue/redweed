# Redweed RDF Personal Information Management System

Redweed is a personal information management system using an RDF database. The system consists of a Java Micronaut backend with Jena TDB2 RDF storage and a React frontend. It supports importing and querying descriptions of people, places, events, media, and other data types.

Always reference these instructions first and fallback to search or bash commands only when you encounter unexpected information that does not match the info here.

## Working Effectively

### System Requirements
- Java 17+ (system has Java 17)
- Node.js 18+ (system has Node.js 20)
- Gradle (available at /usr/bin/gradle)
- npm 6+ (system has npm 10.8.2)

### Build and Test Process
- **CRITICAL: NEVER CANCEL long-running builds or tests. Set timeouts to 60+ minutes for builds and 30+ minutes for tests.**

1. **Backend (jweed) - First Time Setup:**
   ```bash
   cd jweed
   ./gradlew clean build  # NEVER CANCEL: Takes 10-11 minutes on first run. Set timeout to 60+ minutes.
   ```

2. **Backend - Subsequent Builds:**
   ```bash
   cd jweed
   ./gradlew build  # Takes ~2-3 minutes on subsequent runs
   ```

3. **Backend Tests:**
   ```bash
   cd jweed
   ./gradlew test  # Takes ~8 seconds on subsequent runs after initial build
   ```

4. **Frontend Setup:**
   ```bash
   cd frontend
   npm ci  # NEVER CANCEL: Takes ~3 minutes. Set timeout to 15+ minutes.
   ```

5. **Frontend Build:**
   ```bash
   cd frontend
   npm run build  # Takes ~7 seconds
   ```

6. **Frontend Tests:**
   ```bash
   cd frontend
   npm test -- --coverage --watchAll=false  # Takes ~4 seconds
   ```

7. **Frontend Linting:**
   ```bash
   cd frontend
   NODE_ENV=development npm run lint  # Requires NODE_ENV environment variable
   ```

### Running the Application
- **ALWAYS run the bootstrapping build steps first before starting servers.**

1. **Start Backend Server:**
   ```bash
   cd jweed
   ./gradlew run  # Starts on http://localhost:8888
   ```
   - Startup takes ~2-3 seconds
   - OpenAPI documentation available at http://localhost:8888/swagger-ui/
   - Uses Jena TDB2 database (auto-created in ./data/tdb)

2. **Start Frontend Development Server:**
   ```bash
   cd frontend
   npm start  # Starts on http://localhost:3000
   ```
   - Development server with hot reload
   - Takes ~5-10 seconds to start

### Validation and Testing
- **MANUAL VALIDATION REQUIREMENT:** Always test actual functionality by running through complete user scenarios.
- Always run both backend and frontend servers to test the full application stack.
- Test key API endpoints:
  - GET http://localhost:8888/swagger-ui/ (OpenAPI documentation)
  - POST http://localhost:8888/api/vcard/import (vCard import)
  - POST http://localhost:8888/api/photo/upload (photo upload)

### Pre-commit Validation
- **Always run these commands before committing changes:**
  ```bash
  cd jweed && ./gradlew test                               # Backend tests
  cd frontend && NODE_ENV=development npm run lint        # Frontend linting
  cd frontend && npm test -- --coverage --watchAll=false  # Frontend tests
  ```

## Architecture Overview

### Backend (jweed/)
- **Technology:** Java 17, Micronaut 4.9.1, Jetty server
- **Database:** Apache Jena TDB2 RDF store
- **Port:** 8888
- **Build System:** Gradle 8.14.2
- **Key Features:**
  - RDF-based personal information management
  - vCard import/export (FOAF vocabulary)
  - Photo processing with EXIF metadata
  - Media file processing (MP3, MP4)
  - SPARQL query endpoint
  - OpenAPI/Swagger documentation

### Frontend (frontend/)
- **Technology:** React 19, Create React App
- **Port:** 3000 (development)
- **Build System:** npm/webpack
- **Key Features:**
  - File upload interface
  - Data submission forms
  - Media type selection (books, media, music)
  - Data type selection (events, persons, places, reviews, TTL)

### Data Storage
- **RDF Database:** ./data/tdb (auto-created)
- **Media Files:** ./media/ (photos/, videos/, music/)
- **Configuration:** Uses application.yml in jweed/src/main/resources/

## API Endpoints

### Core Endpoints
- **Health Check:** GET /health
- **vCard Import:** POST /api/vcard/import (Content-Type: text/vcard)
- **Photo Upload:** POST /api/photo/upload (Content-Type: multipart/form-data)
- **OpenAPI Docs:** GET /swagger-ui/

### Controller Structure
- PersonController (/persons) - Person data management
- MediaController - Media file handling
- EventController - Event data
- PlaceController - Location data
- VCardImportController (/api/vcard/import) - vCard processing
- BookController, MusicController, ReviewController - Specialized data types

## Development Environment Setup

### Optional: Nix Development Environment
- Uses flake.nix for reproducible development environment
- Includes JDK 21, Node.js 24, Gradle, and Jena tools
- Sets up isolated project directories and TDB configuration
- Run: `nix develop` (if Nix is available)

### Manual Setup
1. Ensure Java 17+, Node.js 18+, and Gradle are installed
2. Create data directories: `mkdir -p data/tdb media/photos media/videos media/music`
3. Follow build and test process above

## Common Tasks and File Locations

### Key Configuration Files
- **Backend Config:** jweed/src/main/resources/application.yml
- **Frontend Config:** frontend/package.json
- **Build Config:** jweed/build.gradle
- **CI/CD:** .github/workflows/gradle.yml, .github/workflows/frontend-ci.yml

### Important Directories
- **Source Code:** jweed/src/main/java/me/bmordue/redweed/
- **Tests:** jweed/src/test/java/, frontend/src/App.test.js
- **Frontend Source:** frontend/src/
- **Documentation:** docs/ (architecture.md, API.md, data-model.md)
- **Media Storage:** media/photos/, media/videos/, media/music/
- **Data Storage:** data/tdb/ (auto-created by backend)

### Vocabulary and Data Model
- **FOAF:** People and relationships (http://xmlns.com/foaf/0.1)
- **vCard:** Contact information and photo metadata
- **Dublin Core:** Photo metadata (dc:date)
- **GEO:** Places (http://www.w3.org/2003/01/geo/wgs84_pos#)
- **Music Ontology:** http://purl.org/ontology/mo/
- **Media Annotation:** http://www.w3.org/ns/ma-ont/

## Timing Expectations and Warnings

### CRITICAL BUILD TIMING WARNINGS
- **NEVER CANCEL:** Initial backend build takes 10-11 minutes. Always set timeout to 60+ minutes.
- **NEVER CANCEL:** Frontend npm ci takes ~3 minutes. Set timeout to 15+ minutes.
- **NEVER CANCEL:** All builds may take longer on slower systems or with poor network connectivity.

### Quick Operations (< 10 seconds)
- Frontend build: ~7 seconds
- Frontend tests: ~4 seconds  
- Frontend linting: ~2 seconds (with NODE_ENV set)
- Backend tests: ~8 seconds (after initial build)
- Server startup: ~2-3 seconds

### Medium Operations (1-5 minutes)  
- Backend subsequent builds: ~2-3 minutes
- Frontend dependency install: ~3 minutes

### Long Operations (10+ minutes)
- Backend first-time build: 10-11 minutes (NEVER CANCEL)

## Troubleshooting

### Common Issues
- **Frontend linting fails:** Set NODE_ENV=development before running npm run lint
- **Backend fails to start:** Check Java version (requires Java 17+)
- **Database issues:** Delete ./data/tdb directory to reset RDF database
- **Port conflicts:** Backend uses 8888, frontend uses 3000
- **Build failures:** Run ./gradlew clean build for fresh backend build

### Validation Scenarios
- Upload a photo file through the frontend interface
- Import a vCard file through the API
- Access OpenAPI documentation at http://localhost:8888/swagger-ui/
- Verify both frontend and backend serve content correctly
- Test file upload and data submission forms in the React interface

## Development Workflow
1. Always build backend first (long operation)
2. Set up frontend dependencies  
3. Start both servers for full-stack testing
4. Make incremental changes
5. Run validation commands before committing
6. Test actual user scenarios, not just build success

## Document-Driven Development

### Planning and Decision Making
Following Amazon's document-driven culture, use structured documents for planning and implementation:

#### Strategic Planning
- **Major Features/Changes:** Start with [6-pager template](../docs/templates/6-pager.md) for comprehensive analysis
- **Small Improvements:** Use [1-pager template](../docs/templates/1-pager.md) for quick decisions
- **Customer-Focused Features:** Begin with [PR/FAQ template](../docs/templates/pr-faq.md) to work backwards from customer value
- **User Experience Design:** Use [Working Backwards Document](../docs/templates/working-backwards.md) for customer journey mapping

#### Technical Implementation  
- **Architecture Decisions:** Document significant technical choices using [ADR template](../docs/templates/adr.md)
- **Complex Features:** Create [Design Documents](../docs/templates/design-document.md) for detailed technical specifications
- **System Operations:** Maintain [Operational Runbooks](../docs/templates/operational-runbook.md) for production systems

#### Continuous Improvement
- **Incident Response:** Use [Post-Incident Report template](../docs/templates/post-incident-report.md) after any operational issues
- **Learning Culture:** Focus on improvement rather than blame, document lessons learned

### Document Usage in Development Process
1. **Before Coding:** Write appropriate planning documents (PR/FAQ, 6-pager, or 1-pager)
2. **During Design:** Create technical specifications using Design Documents and ADRs
3. **For Production Systems:** Maintain Operational Runbooks
4. **After Incidents:** Write Post-Incident Reports for learning and improvement
5. **Decision Reviews:** Use silent reading at the beginning of meetings to review documents

### Template Selection Guidelines
- **Scope:** Larger initiatives require more comprehensive documents (6-pager vs 1-pager)
- **Audience:** Consider stakeholders who need to review and approve
- **Customer Impact:** Customer-facing changes should include PR/FAQ or Working Backwards documents
- **Technical Complexity:** Complex implementations need Design Documents and ADRs
- **Operational Impact:** Production systems require Operational Runbooks

All document templates are available in [docs/templates/](../docs/templates/) with detailed usage guidelines.