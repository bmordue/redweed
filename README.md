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
