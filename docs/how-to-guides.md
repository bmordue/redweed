# How-to Guides

This section provides solutions to common problems.

## Adding a new media type

To add a new media type, you will need to do the following:

1.  **Define the vocabulary.** Choose an existing RDF vocabulary or create a new one to represent the new media type. Add the vocabulary to the `docs/data-model.md` file.
2.  **Create an import parser.** Create a new class that implements the `ImportParser` interface. This class will be responsible for parsing the new media type and converting it to RDF.
3.  **Update the `MediaTypeController`** Add a new endpoint to the `MediaTypeController` to handle the new media type. This endpoint should use the new import parser to parse the media type and add it to the RDF store.
4.  **Add a new API endpoint.** Add a new endpoint to the `ApiController` to expose the new media type.
5.  **Update the documentation.** Add a new section to the `docs/API.md` file to document the new endpoint.

## Implementing iCal Import

To implement the iCal import functionality, follow these steps:

### 1. Parser Implementation

**Create the main converter class:**
```java
@Singleton
public class ICalToRdfConverter {
    public Model convert(String icsContent) {
        // Use ical4j to parse the content
        // Map components to RDF using defined vocabularies
        // Return Jena Model with converted data
    }
}
```

**Component conversion methods:**
- `convertVEvent()` - Events using Event Ontology
- `convertVTodo()` - Tasks using custom task vocabulary  
- `convertVJournal()` - Journal entries using SIOC
- `convertVTimezone()` - Timezone handling with W3C Time

### 2. Service Layer

**Create ICalImportService:**
```java
@Singleton
public class ICalImportService {
    private final ICalToRdfConverter converter;
    private final RdfRepository repository;
    
    public ICalImportResponse importICalendar(String icsData) {
        // Validate input
        // Convert to RDF model
        // Store in repository
        // Return import statistics
    }
}
```

### 3. Controller Implementation

**Create ICalImportController:**
```java
@Controller("/api/ical")
public class ICalImportController {
    @Post("/import")
    public ICalImportResponse importCalendar(@Body String icsData) {
        // Delegate to service layer
    }
    
    @Post("/import/file") 
    public ICalImportResponse importFile(@Part CompletedFileUpload file) {
        // Handle file upload
    }
    
    @Get("/export")
    @Produces("text/calendar")
    public String exportCalendar(@QueryValue Optional<String> startDate) {
        // Export RDF back to iCal format
    }
}
```

### 4. Error Handling Strategy

**Validation levels:**
- **Critical errors**: Invalid iCal format, missing required properties
- **Warnings**: Timezone issues, unknown properties, deprecated features
- **Information**: Successful imports, skipped components

**Response patterns:**
- Return partial success with warnings for recoverable errors
- Provide detailed error messages for debugging
- Log all import attempts for auditing

### 5. Testing Approach

**Test file creation:**
Create sample .ics files for testing:
- `basic-event.ics` - Single event with common properties
- `recurring-meeting.ics` - Event with recurrence rules
- `task-list.ics` - Multiple tasks with dependencies
- `complex-calendar.ics` - Mixed components with timezones
- `invalid-format.ics` - Malformed data for error testing

**Unit test coverage:**
- Component conversion accuracy
- Error handling scenarios  
- Timezone conversion logic
- Recurrence rule processing
- URI generation consistency

### 6. Performance Considerations

**Large calendar handling:**
- Stream processing for files > 1MB
- Batch insert for multiple components
- Progress callbacks for long operations
- Memory management for recurring events

**Caching strategy:**
- Cache parsed timezone definitions
- Reuse person/place URI mappings
- Store conversion statistics for optimization
