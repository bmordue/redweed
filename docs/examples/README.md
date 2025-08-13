# iCal Example Files

This directory contains sample iCalendar (.ics) files for testing and demonstrating the iCal import functionality in Redweed.

## Files

### basic-event.ics
A simple calendar event with common properties:
- Single event with organizer and attendees
- Location, description, and categories
- Demonstrates standard VEVENT component mapping

### task-list.ics
Task management examples using VTODO components:
- Multiple tasks with different priorities and statuses
- Progress tracking with PERCENT-COMPLETE
- Due dates and task relationships

### journal-entries.ics
Personal journal entries using VJOURNAL components:
- Daily reflection and planning entries
- Links to related events and contacts
- Demonstrates personal information management

### recurring-events.ics
Complex recurring events with timezone support:
- Weekly team standup with recurrence rules
- Monthly review meetings
- Timezone definitions for accurate scheduling

### invalid-format.ics
Test file with intentional errors for validation testing:
- Invalid date formats
- Missing required properties
- Unknown status values
- Demonstrates error handling capabilities

## Usage

These files can be used to:

1. **Test the import functionality**:
   ```bash
   curl -X POST http://localhost:8888/api/ical/import \
     -H "Content-Type: text/calendar" \
     --data-binary @basic-event.ics
   ```

2. **Validate error handling**:
   ```bash
   curl -X POST http://localhost:8888/api/ical/import \
     -H "Content-Type: text/calendar" \
     --data-binary @invalid-format.ics
   ```

3. **Demonstrate different component types**:
   - Import `basic-event.ics` to see event processing
   - Import `task-list.ics` to see task management
   - Import `journal-entries.ics` to see journal functionality

4. **Test file upload functionality**:
   ```bash
   curl -X POST http://localhost:8888/api/ical/import/file \
     -F "file=@recurring-events.ics"
   ```

## Expected RDF Output

Each imported component will generate RDF triples using the vocabularies defined in `docs/data-model.md`:

- **Events**: Event Ontology (http://purl.org/NET/c4dm/event.owl#)
- **Tasks**: Custom task vocabulary with vCard extensions
- **Journals**: SIOC vocabulary (http://rdfs.org/sioc/ns#)
- **Time**: W3C Time Ontology (http://www.w3.org/2006/time#)

## Integration with Tutorials

These examples correspond to the calendar management tutorial in `docs/tutorials.md`, demonstrating Emma's freelance design workflow with multiple calendar integrations.