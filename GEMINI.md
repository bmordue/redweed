# Gemini Project Overview: redweed

This document provides a quick overview of the `redweed` project, intended to help developers get started quickly.

## Project Summary

`redweed` is a Clojure application that interacts with a Redweed database. It is an RDF-based system, using Apache Jena for RDF handling and providing a web API via Ring and Compojure. The project is set up with a Nix environment for reproducible builds and to easily setup development and runtime environments.
Issues are tracked at https://github.com/bmordue/redweed/issues .

## Key Files

*   `rwclj/deps.edn`: Defines the project's dependencies and aliases for common tasks.
*   `rwclj/project.clj`: Legacy project definition, might contain some configuration not yet migrated to `deps.edn`.
*   `shell.nix`: Defines the development environment, including dependencies like Java, Clojure, and Apache Jena.
*   `rwclj/src/rwclj/core.clj`: The main application entry point.
*   `rwclj/src/rwclj/db.clj`:  Likely contains database interaction logic.
*   `rwclj/src/redweed/server`:  The web server implementation.
*   `rwclj/src/rwclj/seed.clj`: Contains logic for seeding the database with initial data.
*   `README.md`: The project's README file, containing key information and aliases.

## Key Dependencies

*   **Clojure**: The primary programming language.
*   **Apache Jena**: A Java framework for building Semantic Web and Linked Data applications.
*   **Ring/Compojure**: A web server and routing library for Clojure.
*   **Jsonista**: A fast JSON library.
*   **Aero**: A configuration library.
*   **Nix**: For creating a reproducible development environment.

## Common Tasks

The project uses aliases defined in `rwclj/deps.edn` for common tasks.

*   **Seed the database**: `clj -M:seed`
*   **Start the API server**: `clj -M:server`
*   **Run tests**: `clj -M:test`
*   **Start a development REPL**: `clj -M:repl`
*   **Build an uberjar**: `clj -M:uberjar`

To enter the development environment, run `nix-shell` from the project root. This will make all the necessary tools and dependencies available.
