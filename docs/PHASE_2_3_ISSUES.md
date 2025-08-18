# Phase 2 & 3 Implementation Issues

This document contains GitHub issue templates for implementing the remaining phases of the iCal to RDF conversion feature.

## Phase 2 Issues

### Issue 1: Add VTODO (Tasks) Component Support to iCal to RDF Converter

**Description:**
Extend the existing iCal to RDF converter to support VTODO components (tasks) in addition to the currently supported VEVENT components.

**Context:**
Phase 1 of the iCal implementation (#111) successfully implemented VEVENT component conversion. Phase 2 extends this to support task management through VTODO components.

**Acceptance Criteria:**
- [ ] Extend `ICalToRdfConverter.java` to handle VTODO components
- [ ] Map VTODO properties to appropriate RDF vocabularies:
  - `SUMMARY` → `rdfs:label`
  - `DESCRIPTION` → `dc:description`
  - `DUE` → custom property for due dates
  - `COMPLETED` → completion date property
  - `PERCENT-COMPLETE` → percentage completion property
  - `STATUS` → task status (NEEDS-ACTION, IN-PROCESS, COMPLETED, CANCELLED)
  - `PRIORITY` → priority level (1-9)
- [ ] Generate stable URIs for tasks based on UID with fallback to UUID
- [ ] Add comprehensive unit tests for VTODO conversion
- [ ] Update API documentation to reflect VTODO support
- [ ] Create example VTODO test files in `docs/examples/`

**Implementation Notes:**
- Follow the existing pattern in `ICalToRdfConverter.convert()` method
- Add a new private method `convertVTodo(VToDo todo, Model model)` 
- Use similar vocabulary mapping approach as VEVENT conversion
- Ensure backward compatibility with existing VEVENT functionality

**Files to Modify:**
- `jweed/src/main/java/me/bmordue/redweed/service/ICalToRdfConverter.java`
- `jweed/src/test/java/me/bmordue/redweed/service/ICalToRdfConverterTest.java`
- `docs/examples/` (add task examples)
- `docs/API.md` (update documentation)

**Dependencies:**
- Requires completion of Phase 1 (#111)
- Uses existing ical4j library already included in dependencies

---

### Issue 2: Add VJOURNAL (Journal Entries) Component Support to iCal to RDF Converter

**Description:**
Extend the iCal to RDF converter to support VJOURNAL components (journal entries/notes) for comprehensive calendar data import.

**Context:**
Building on Phase 1 VEVENT support and Phase 2 VTODO support, this adds the final major iCal component type for complete calendar data management.

**Acceptance Criteria:**
- [ ] Extend `ICalToRdfConverter.java` to handle VJOURNAL components
- [ ] Map VJOURNAL properties to RDF vocabularies:
  - `SUMMARY` → `rdfs:label`
  - `DESCRIPTION` → `dc:description`
  - `DTSTART` → creation/entry date
  - `STATUS` → journal entry status
  - `CLASS` → privacy classification (PUBLIC, PRIVATE, CONFIDENTIAL)
  - `CATEGORIES` → topic categorization
- [ ] Generate stable URIs for journal entries based on UID
- [ ] Add comprehensive unit tests for VJOURNAL conversion
- [ ] Create example VJOURNAL test files
- [ ] Update documentation

**Implementation Notes:**
- Add `convertVJournal(VJournal journal, Model model)` method
- Consider using Dublin Core terms for journal/note metadata
- Ensure proper handling of privacy classifications
- Maintain consistency with VEVENT and VTODO URI patterns

**Files to Modify:**
- `jweed/src/main/java/me/bmordue/redweed/service/ICalToRdfConverter.java`
- `jweed/src/test/java/me/bmordue/redweed/service/ICalToRdfConverterTest.java`
- `docs/examples/` (add journal examples)
- `docs/API.md`

**Dependencies:**
- Requires completion of VTODO support issue

---

### Issue 3: Implement Enhanced Category and Priority Mapping for iCal Components

**Description:**
Enhance the category and priority handling across all iCal component types (VEVENT, VTODO, VJOURNAL) with proper RDF vocabulary mapping and standardized categorization.

**Context:**
This completes Phase 2 by ensuring consistent category and priority handling across all supported iCal component types.

**Acceptance Criteria:**
- [ ] Implement standardized category mapping using SKOS vocabulary
- [ ] Add priority level mapping (1-9 scale) with semantic meaning
- [ ] Support multiple categories per component
- [ ] Create category hierarchy support for organizing related categories
- [ ] Add unit tests for category and priority edge cases
- [ ] Document category mapping strategy

**Implementation Notes:**
- Use SKOS (Simple Knowledge Organization System) for categories
- Map iCal priority 1-9 to semantic priority levels (high/medium/low)
- Handle comma-separated category lists
- Consider creating predefined category schemes for common use cases

**Files to Modify:**
- `jweed/src/main/java/me/bmordue/redweed/service/ICalToRdfConverter.java`
- Add tests for all component types with categories/priorities
- `docs/data-model.md` (document category strategy)

---

## Phase 3 Issues

### Issue 4: Implement Recurrence Rule Processing for iCal Events

**Description:**
Add support for processing RRULE (recurrence rules) in VEVENT components to handle repeating events with proper RDF representation.

**Context:**
Phase 3 advanced features begin with recurrence rule support, enabling import of repeating events from calendar applications.

**Acceptance Criteria:**
- [ ] Parse RRULE properties from VEVENT components
- [ ] Support common recurrence patterns:
  - Daily (FREQ=DAILY)
  - Weekly (FREQ=WEEKLY)
  - Monthly (FREQ=MONTHLY)
  - Yearly (FREQ=YEARLY)
- [ ] Handle recurrence modifiers:
  - INTERVAL (every N periods)
  - COUNT (number of occurrences)
  - UNTIL (end date)
  - BYDAY, BYMONTH, BYMONTHDAY
- [ ] Map to W3C Time Ontology recurrence vocabulary
- [ ] Generate individual event instances or link to recurrence pattern
- [ ] Add comprehensive tests for various recurrence patterns
- [ ] Handle EXDATE (exception dates) for recurrence rules

**Implementation Notes:**
- Use ical4j RecurrenceRule classes for parsing
- Consider whether to generate individual event instances or store recurrence metadata
- Map to W3C Time Ontology for temporal relationships
- Handle timezone implications in recurrence calculations

**Files to Modify:**
- `jweed/src/main/java/me/bmordue/redweed/service/ICalToRdfConverter.java`
- Add comprehensive recurrence rule tests
- `docs/examples/` (add recurring event examples)

**Dependencies:**
- Requires Phase 2 completion
- May benefit from timezone handling implementation

---

### Issue 5: Implement Timezone Handling with W3C Time Ontology

**Description:**
Add comprehensive timezone support for iCal datetime properties using W3C Time Ontology for proper temporal data representation.

**Context:**
Timezone handling is critical for accurate event scheduling and ensures proper datetime interpretation across different geographical locations.

**Acceptance Criteria:**
- [ ] Parse VTIMEZONE components from iCal data
- [ ] Handle TZID references in datetime properties
- [ ] Map to W3C Time Ontology timezone representations
- [ ] Support common timezone formats:
  - UTC timestamps
  - Local time with timezone reference
  - Floating time (no timezone)
- [ ] Handle daylight saving time transitions
- [ ] Preserve original timezone information in RDF
- [ ] Add timezone conversion utilities for queries
- [ ] Comprehensive timezone testing with various formats

**Implementation Notes:**
- Use ical4j TimeZone handling capabilities
- Map to W3C Time Ontology temporal entities
- Consider storing both original and normalized timezone data
- Handle edge cases like timezone definition changes

**Files to Modify:**
- `jweed/src/main/java/me/bmordue/redweed/service/ICalToRdfConverter.java`
- Add timezone-specific test cases
- `docs/examples/` (add timezone examples)

**Dependencies:**
- Benefits from recurrence rule implementation for complex scheduling

---

### Issue 6: Implement Attendee and Organizer Relationship Mapping

**Description:**
Add support for ATTENDEE and ORGANIZER properties in VEVENT components, creating proper RDF relationships for meeting participants.

**Context:**
Completes Phase 3 by adding social/collaborative aspects to event data, enabling relationship tracking between people and events.

**Acceptance Criteria:**
- [ ] Parse ORGANIZER property and create appropriate RDF relationships
- [ ] Parse ATTENDEE properties with all parameters:
  - CN (common name)
  - ROLE (participant role)
  - PARTSTAT (participation status)
  - RSVP (response required)
  - CUTYPE (calendar user type)
- [ ] Map to FOAF vocabulary for person relationships
- [ ] Link to existing Person entities in the knowledge base
- [ ] Create new Person entities when needed
- [ ] Handle email-based person identification
- [ ] Support attendee status tracking (ACCEPTED, DECLINED, TENTATIVE)
- [ ] Add tests for various attendee scenarios

**Implementation Notes:**
- Integrate with existing FOAF vocabulary usage
- Consider creating Person entities for unknown attendees
- Use email addresses as primary identifiers for linking
- Map participation roles to semantic relationships

**Files to Modify:**
- `jweed/src/main/java/me/bmordue/redweed/service/ICalToRdfConverter.java`
- Integrate with existing Person/FOAF handling
- Add attendee/organizer test scenarios
- `docs/examples/` (add meeting examples)

**Dependencies:**
- Requires understanding of existing FOAF Person handling
- Benefits from completion of other Phase 3 features

---

## Implementation Order

**Recommended implementation order:**
1. **Phase 2:** VTODO support → VJOURNAL support → Enhanced categories/priorities
2. **Phase 3:** Timezone handling → Recurrence rules → Attendee/organizer relationships

This order ensures foundational component support before adding advanced temporal and social features.

## Testing Strategy

Each issue should include:
- Unit tests for the specific functionality
- Integration tests with existing systems
- Example iCal files demonstrating the feature
- Error handling for malformed data
- Performance testing for complex scenarios

## Documentation Updates

Each implementation should update:
- API documentation with new capabilities
- Data model documentation with vocabulary mappings
- Tutorial examples incorporating new features
- How-to guides for common use cases