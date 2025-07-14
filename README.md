[![codecov](https://codecov.io/gh/bmordue/redweed/branch/main/graph/badge.svg)](https://codecov.io/gh/bmordue/redweed)

**About**

redweed is an RDF database for personal information management. It c supports importing and querying descriptions of people, places, events, media and other arbitrary types of data.

**Key Dependencies:**
- **Jena stack** - Core RDF functionality, TDB2 storage, SPARQL
- **Ring/Compojure** - Web server for your API
- **Jsonista** - Fast JSON handling for JSON-LD
- **Aero** - Configuration management
- **Spec** - Data validation

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

The configuration supports both development (with extra tooling) and production (minimal dependencies in uberjar).
