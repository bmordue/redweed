[![codecov](https://codecov.io/gh/bmordue/redweed/branch/main/graph/badge.svg)](https://codecov.io/gh/bmordue/redweed)

**About**

redweed is an RDF database for personal information management. It c supports importing and querying descriptions of people, places, events, media and other arbitrary types of data.

**Configuration**

The application can be configured using environment variables.

*   `JENA_DB_PATH`: The path to the Jena TDB2 database. Defaults to `data/tdb2`.

**Key Dependencies:**
- **Jena stack** - Core RDF functionality, TDB2 storage, SPARQL
- **Ring/Compojure** - Web server for your API
- **Jsonista** - Fast JSON handling for JSON-LD
- **Aero** - Configuration management
- **Spec** - Data validation

**Data Model**

The application uses the following RDF vocabularies to represent data:

*   **FOAF (Friend of a Friend):** Used to describe people and their relationships.
    *   `foaf:Person`: Represents a person.
    *   `foaf:name`: The full name of the person.
    *   `foaf:givenName`: The given name of the person.
    *   `foaf:familyName`: The family name of the person.
    *   `foaf:mbox`: The email address of the person.
    *   `foaf:phone`: The phone number of the person.
*   **vCard:** Used to represent contact information.
    *   `vcard:Individual`: Represents an individual.
    *   `vcard:hasEmail`: The email address of the person.
    *   `vcard:hasTelephone`: The telephone number of the person.
    *   `vcard:hasAddress`: The address of the person.
    *   `vcard:organization-name`: The organization name of the person.
*   **Dublin Core:** Used for photo metadata.
    *   `dc:date`: The date the photo was taken.
*   **VCARD:** Used for photo metadata.
    *   `VCARD:PHOTO`: Represents a photo.

**Useful Aliases:**
```bash
# Seed the database
clj -M:seed

# Start the API server
clj -M:server

# Development REPL with CIDER
clj -M:repl

# Run tests
clj -M:test

# Build uberjar for deployment
clj -M:uberjar
```

**Usage**

Here are some examples of how to use the API with `curl`.

*   **Import a vCard:**

    ```bash
    curl -X POST -H "Content-Type: text/vcard" --data-binary @path/to/your.vcf http://localhost:8080/api/vcard/import
    ```

*   **Upload a photo:**

    ```bash
    curl -X POST -F "file=@/path/to/your/photo.jpg" http://localhost:8080/api/photo/upload
    ```

The configuration supports both development (with extra tooling) and production (minimal dependencies in uberjar).
