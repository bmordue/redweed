# Project Roadmap

This document outlines features for the `redweed` project, tracking implementation status and future development priorities.

**Legend:**
- ✅ **Completed** - Feature is implemented and functional
- 🔄 **In Progress** - Feature is partially implemented 
- ❌ **Not Started** - Feature not yet implemented
- 📝 **Documentation** - Documentation task
- 🆕 **New Suggestion** - A new feature idea that has not been prioritized

## Implemented Features

### ✅ **VCard Import/Export** 
**Status:** Completed  
**Implementation:** Full vCard import functionality with `/api/vcard/import` endpoint, `VCardToRdfConverter` service, and proper response handling via `IngestVCardResponseDto`. Supports text/vcard and application/json content types.
**Location:** `jweed/src/main/java/me/bmordue/redweed/service/VCardToRdfConverter.java`

### ✅ **Integration with a Frontend Application** 
**Status:** Completed  
**Implementation:** React-based frontend application with full API integration. Includes file upload forms for books/media/music and data submission forms for events/persons/places/reviews/TTL. Uses PicoCSS and Tailwind for styling.
**Location:** `frontend/` directory with React app consuming all backend APIs
**Endpoints:** Integrated with /books, /media, /music, /events, /persons, /places, /reviews, /ttl

### ✅ **Support for Additional Media Types** 
**Status:** Completed  
**Implementation:** Full support for multiple media types with metadata extraction:
- **MP3/Audio:** `MusicService`, `MusicController`, `Mp3Parser` with ID3v2 tag extraction
- **MP4/Video:** `MediaService`, `MediaController` with video metadata processing  
- **EPUB/Books:** `BookService`, `BookController` with EPUB metadata extraction
**Vocabularies:** Custom RDF vocabularies defined for music (MusicVocabulary) and other media types

## Partially Implemented Features

### 🔄 **Calendar/Event Management** 
**Status:** In Progress  
**Implementation:** Core event management with `EventService` and `EventController` for creating/managing calendar events. Support for linking events to people, places, and media through RDF relationships.
**Remaining Work:** Enhanced querying by date range, improved frontend integration, iCal import/export functionality
**Location:** `jweed/src/main/java/me/bmordue/redweed/service/EventService.java`

### 🔄 **Geolocation and Mapping** 
**Status:** In Progress  
**Implementation:** `GoogleMapsController` exists for mapping functionality. GPS metadata extraction likely available through media metadata processing.
**Remaining Work:** Complete GPS data extraction from photos, map display integration in frontend, enhanced geolocation APIs
**Location:** `jweed/src/main/java/me/bmordue/redweed/controller/GoogleMapsController.java`

### 🔄 **Advanced Photo Search**
**Status:** In Progress
**Implementation:** Core SPARQL querying capability exists via `ExplorerController` with pagination support. While SPARQL provides a foundation for querying RDF data, it does not yet fully enable photo-specific search functionality. Basic metadata extraction from media files is implemented, but additional work is required to leverage SPARQL for advanced photo search features.
**Remaining Work:** Development of specialized photo search endpoints to handle queries specific to photos, such as filtering by date range, location-based search, and querying EXIF metadata. These features will build on the existing SPARQL querying capabilities.
**Location:** `jweed/src/main/java/me/bmordue/redweed/controller/ExplorerController.java`

## Future Development Priorities

### ❌ **User Authentication** 
**Priority:** High  
**Description:** Implement user accounts and authentication to restrict access to API endpoints. Essential for multi-user deployments and data privacy.
**Implementation Approach:** Add user model to RDF store, implement JWT or session-based authentication, integrate with existing controllers
**Dependencies:** User management UI in frontend, security configuration

### ❌ **Tagging/Categorization of Photos** 
**Priority:** High  
**Description:** Allow users to add tags/categories to photos for improved organization and searchability.
**Implementation Approach:** 
- Add tagging vocabulary to RDF model (e.g., SKOS concepts)
- Create tag management API endpoints (POST/DELETE /photos/{id}/tags)
- Extend photo search to filter by tags
- Add tagging UI to frontend
**Dependencies:** Enhanced photo search functionality

### ❌ **Album Management** 
**Priority:** Medium  
**Description:** Group photos into collections/albums for better organization. Distinct from music albums already supported.
**Implementation Approach:**
- Define album vocabulary using FOAF Collections or similar
- Create album CRUD API endpoints
- Implement album-photo relationship management
- Add album management UI to frontend
**Dependencies:** Photo tagging system for enhanced album features

### ❌ **Linked Data Integration** 
**Priority:** Medium  
**Description:** Connect to external Linked Data sources (DBpedia, Wikidata) to enrich local data.
**Implementation Approach:**
- Create external URI resolution service
- Add caching mechanism for external data
- Implement data enrichment workflows
- Add configuration for external data sources
**Dependencies:** Robust caching strategy, network resilience

## Recently Added Features (Not in Original Roadmap)

### ✅ **Review/Rating System**
**Implementation:** Complete review management with `ReviewService` and `ReviewController` for creating and managing reviews/ratings of places, media, food, etc.
**Location:** `jweed/src/main/java/me/bmordue/redweed/service/ReviewService.java`

### ✅ **Place Management** 
**Implementation:** Comprehensive place/location management with `PlaceService` and `PlaceController` for geographic entities and business locations.
**Location:** `jweed/src/main/java/me/bmordue/redweed/service/PlaceService.java`

### ✅ **Direct TTL/RDF Import**
**Implementation:** Direct RDF Turtle format import via `TtlService` and `TtlController` for advanced users and bulk data import.
**Location:** `jweed/src/main/java/me/bmordue/redweed/service/TtlService.java`

### ✅ **Data Explorer** 
**Implementation:** SPARQL query interface with pagination via `ExplorerController` for advanced data exploration and debugging.
**Location:** `jweed/src/main/java/me/bmordue/redweed/controller/ExplorerController.java`

## New Feature Suggestions

### 🆕 **Batch Import/Export Operations**
**Priority:** Medium  
**Description:** Support bulk operations for importing multiple files or exporting data collections
**Implementation Approach:** Create batch processing endpoints, progress tracking, queue management

### 🆕 **Advanced Metadata Search**
**Priority:** Medium  
**Description:** Enhanced search across all metadata fields with faceted search capabilities  
**Implementation Approach:** Extend ExplorerController with specialized search endpoints, facet aggregation

### 🆕 **Data Validation and Quality Assurance**
**Priority:** Medium  
**Description:** Validate imported data quality, detect duplicates, ensure RDF consistency
**Implementation Approach:** Create validation service, duplicate detection algorithms, data quality metrics

### 🆕 **Mobile API Endpoints** 
**Priority:** Low  
**Description:** Optimize API responses for mobile applications with reduced payloads and offline support
**Implementation Approach:** Create mobile-specific endpoints, implement caching strategies, add compression

## Documentation Tasks

### ✅ **Developer Guide** 
**Status:** Completed  
**Implementation:** Comprehensive tutorials document with real-world examples and entity modeling
**Location:** `docs/tutorials.md` (193 lines) - covers development concepts and data modeling

### ✅ **API Documentation** 
**Status:** Completed  
**Implementation:** Detailed API endpoint documentation with examples and response formats
**Location:** `docs/API.md` (170 lines) - covers all major endpoints

### ✅ **Getting Started Guide** 
**Status:** Completed  
**Implementation:** Complete setup and installation guide for both backend and frontend
**Location:** `docs/getting-started.md` - covers prerequisites, installation, and running the application

### ✅ **Architecture Guide**
**Status:** Completed  
**Implementation:** System architecture documentation
**Location:** `docs/architecture.md` (120 lines)

### 📝 **Configuration Guide** 
**Status:** Partial  
**Current:** Basic configuration mentioned in how-to guides
**Needed:** Comprehensive guide explaining all configuration options, environment variables, and deployment settings
**Priority:** Medium

### 📝 **Deployment Guide** 
**Status:** Missing  
**Needed:** Production deployment guide covering:
- Container deployment (Docker/Kubernetes)
- Database setup and migration
- Security considerations
- Performance tuning
- Backup and recovery procedures
**Priority:** High

### 📝 **Troubleshooting Guide** 
**Status:** Missing  
**Needed:** Common problems and solutions covering:
- Build and compilation issues
- Runtime errors and debugging
- Performance issues
- Data import/export problems
- Frontend-backend connectivity issues
**Priority:** Medium

### 📝 **Data Model Documentation Enhancement**
**Status:** Needs Update  
**Current:** Basic data model exists (`docs/data-model.md`)
**Needed:** Enhanced documentation covering:
- Recently added vocabularies (Music, Media, Review)
- Relationship modeling examples
- Best practices for data organization
- RDF vocabulary extension guidelines
**Priority:** Medium
