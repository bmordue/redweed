# Summary

These are the media types that will be imported into the redweed RDF store, along with details of the data formats that can be imported, and the appropriate RDF ontologies that will be used.

## People

### Vocabulary

- FOAF: http://xmlns.com/foaf/0.1

### Imports

- vCard

## Places

### Vocabulary

- geo: http://www.w3.org/2003/01/geo/wgs84_pos#

### Imports

- KML

## Music

### Vocabulary

- mo: http://purl.org/ontology/mo/

### Imports

- .mp3
- Original file imported to media directory.
- Metadata imported as RDF

## Video

### Vocabulary

- ma: http://www.w3.org/ns/ma-ont/

### Imports

- .mp4
- Original file imported to media directory.
- Metadata imported as RDF

## Books

### Vocabulary

- bibo: http://purl.org/ontology/bibo/

### Imports

- epub
- Original file imported to media directory.
- Metadata imported as RDF

## Recipes

### Vocabulary

TODO: complete this section

### Imports

- hRecipe

## Reviews and ratings

### Vocabulary

- rev: http://purl.org/stuff/rev#

### Imports

- hReview

## Accounting

### Vocabulary

- gr: http://purl.org/goodrelations/v1# maybe ??

### Imports 

- hledger

## Events

### Vocabulary

- event: http://purl.org/NET/c4dm/event.owl#
- ical: http://www.w3.org/2002/12/cal/ical#
- time: http://www.w3.org/2006/time#
- dc: http://purl.org/dc/elements/1.1/

### Imports

- vCalendar ICS (RFC 5545)

#### iCal Component Mappings

**VEVENT (Calendar Events):**
- `ical:summary` → `rdfs:label`
- `ical:description` → `dc:description` 
- `ical:dtstart` → `event:time` + `time:hasBeginning`
- `ical:dtend` → `time:hasEnd`
- `ical:location` → `event:place`
- `ical:uid` → Used for URI generation
- `ical:organizer` → `event:agent`
- `ical:attendee` → Custom property for participants
- `ical:categories` → `skos:Concept` tags
- `ical:priority` → Custom priority property
- `ical:status` → Event status (TENTATIVE, CONFIRMED, CANCELLED)

**Recurrence Rules:**
- `ical:rrule` → Time interval patterns using W3C Time Ontology
- Recurring events create multiple event instances linked to master event

## Collections

### Vocabulary

- skos: http://www.w3.org/2004/02/skos/core#

### Imports

- Manually created

## Tasks

### Vocabulary

- vcard: http://www.w3.org/2006/vcard/ns#
- ical: http://www.w3.org/2002/12/cal/ical#
- time: http://www.w3.org/2006/time#
- dc: http://purl.org/dc/elements/1.1/

### Imports

- vCalendar ICS (RFC 5545)

#### iCal Component Mappings

**VTODO (Tasks):**
- `ical:summary` → `rdfs:label`
- `ical:description` → `dc:description`
- `ical:due` → Custom due date property
- `ical:completed` → Custom completion timestamp
- `ical:percent-complete` → Custom progress percentage
- `ical:priority` → Task priority (1-9 scale)
- `ical:status` → Task status (NEEDS-ACTION, COMPLETED, IN-PROCESS, CANCELLED)
- `ical:categories` → `skos:Concept` tags
- `ical:uid` → Used for URI generation

**Task Relationships:**
- Parent-child task hierarchies using `ical:related-to`
- Dependencies and subtasks linked via RDF properties

## Journal entries

### Vocabulary

- sioc: http://rdfs.org/sioc/ns#
- ical: http://www.w3.org/2002/12/cal/ical#
- dc: http://purl.org/dc/elements/1.1/
- time: http://www.w3.org/2006/time#

### Imports

- vCalendar ICS (RFC 5545)

#### iCal Component Mappings

**VJOURNAL (Journal Entries):**
- `ical:summary` → `rdfs:label`
- `ical:description` → `sioc:content`
- `ical:dtstart` → `dc:date` (journal entry date)
- `ical:categories` → `skos:Concept` tags
- `ical:uid` → Used for URI generation
- `ical:contact` → Links to related persons
- `ical:related-to` → Links to related events or tasks

**Journal Entry Classification:**
- Use SIOC vocabulary for blog-like journal entries
- Support for mood, weather, or other contextual metadata
- Category-based organization and filtering

## iCal Calendar Import

### Overview

The iCal import feature supports comprehensive calendar data import from RFC 5545 compliant iCalendar files. This includes events, tasks, and journal entries with full support for recurrence rules, time zones, and metadata.

### Vocabulary

- ical: http://www.w3.org/2002/12/cal/ical#
- time: http://www.w3.org/2006/time#
- event: http://purl.org/NET/c4dm/event.owl#
- sioc: http://rdfs.org/sioc/ns#
- geo: http://www.w3.org/2003/01/geo/wgs84_pos#

### Supported Components

**VCALENDAR:** Calendar container
- `ical:prodid` → Software identification
- `ical:version` → iCal version (2.0)
- `ical:calscale` → Calendar scale (GREGORIAN)
- `ical:method` → Calendar method (PUBLISH, REQUEST, etc.)

**VTIMEZONE:** Timezone definitions
- Converted to W3C Time Ontology timezone references
- Support for STANDARD and DAYLIGHT time components
- Timezone offset calculations preserved

### Import Process

1. **Parsing**: Use iCal4j library to parse .ics files
2. **Validation**: Verify RFC 5545 compliance
3. **Entity Creation**: Generate RDF URIs for each component
4. **Relationship Mapping**: Link related components (events, attendees, locations)
5. **Recurrence Expansion**: Generate individual instances for recurring events
6. **Storage**: Insert into Jena TDB2 RDF store

### URI Generation Strategy

- **Events**: `redweed:event/{ical-uid}`
- **Tasks**: `redweed:task/{ical-uid}`  
- **Journal Entries**: `redweed:journal/{ical-uid}`
- **Calendar**: `redweed:calendar/{calendar-name-or-hash}`

### Error Handling

- Invalid date formats → Skip component with warning
- Missing required properties → Use default values where possible
- Timezone issues → Convert to UTC with offset notation
- Duplicate UIDs → Update existing entries or create versioned URIs