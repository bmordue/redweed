# Planned architecture

This document proposes an architecture of the Redweed application. The goal is to ensure the maintainability, readability, and extensibility of the application.

## Proposals

*   **Separate SPARQL queries from the server logic.** 
*   **Use a modular approach to building the API.**
*   **Introduce a data access layer.**  A data access layer abstracts the database from the rest of the application.

## Improved Architecture

The following diagram shows the improved architecture:

```mermaid
graph TD
    subgraph "Clients"
        A[Web Browser]
        B[curl]
    end

    subgraph "Web Server (Jetty)"
        C[API Routes]
    end

    subgraph "API Endpoints"
        D[Contacts Endpoint]
        E[Events Endpoint]
        F[Places Endpoint]
        G[vCard Endpoint]
        H[Photo Endpoint]
    end

    subgraph "Application Logic"
        I[Import Parser A]
        J[Import Parser B...]
    end

    subgraph "Data Access Layer"
        K[SPARQL Queries]
        L[Database Connection]
    end

    subgraph "Database (Jena TDB2)"
        M[RDF Store]
    end

    A --> C
    B --> C
    C --> D
    C --> E
    C --> F
    C --> G
    C --> H
    D --> K
    E --> K
    F --> K
    G --> I
    H --> J
    G --> L
    H --> L
    K --> L
    L --> M
```
