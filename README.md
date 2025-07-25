[![codecov](https://codecov.io/gh/bmordue/redweed/branch/main/graph/badge.svg)](https://codecov.io/gh/bmordue/redweed)

**About**

redweed is an RDF database for personal information management. It c supports importing and querying descriptions of people, places, events, media and other arbitrary types of data.

**Configuration**

The application can be configured using environment variables.

*   `JENA_DB_PATH`: The path to the Jena TDB2 database. Defaults to `data/tdb2`.

**Key Dependencies:**
- **Jena stack** - Core RDF functionality, TDB2 storage, SPARQL

**Data Model**

The application uses the following RDF vocabularies to represent data:

*   **FOAF (Friend of a Friend):** Used to describe people and their relationships.
    *   `foaf:Person`: Represents a person.
    *   `foaf:name`: The full name of the person.
    *   `foaf:givenName`: The given name of the person.
    *   `foaf:familyName`: The family name of the person.
    *   `foaf:mbox`: The email address of the person.
    *   `foaf:phone`: The phone number of the person.
*   **vCard:** Used to represent contact information and for photo metadata.
    *   `vcard:Individual`: Represents an individual.
    *   `vcard:hasAddress`: The address of the person.
    *   `vcard:organization-name`: The organization name of the person.
    *   `vcard:PHOTO`: Represents a photo.
*   **Dublin Core:** Used for photo metadata.
    *   `dc:date`: The date the photo was taken.

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

*   **List all contacts:**

    ```bash
    curl http://localhost:8080/contacts
    ```

For more information, see the full [documentation](./docs/README.md).

The configuration supports both development (with extra tooling) and production (minimal dependencies in uberjar).
