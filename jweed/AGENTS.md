# Agent Instructions for jweed

## About This Project

This is a Micronaut 4 application written in Java 21. It uses Jetty as the server and Jackson for serialization. The project is set up with GitHub Actions for CI, Testcontainers for integration testing, and Mockito for mocking. It also includes OpenAPI support for API documentation.

The project's main dependencies include `jena`, `ical4j`, `mp3agic`, and `javacv`

It is designed to handle various data formats, including RDF (with TDB2 persistence), vCards, MP3s, and video files. It will function as a data processing and integration hub.

## Tech Stack

*   **Language:** Java 21
*   **Framework:** Micronaut 4.9.1
*   **Server:** Jetty
*   **Build Tool:** Gradle 8
*   **Testing:** JUnit 5, Mockito, Testcontainers
*   **CI/CD:** GitHub Actions
*   **Code Quality:** Jacoco for code coverage, SonarQube for static analysis.

## Key Commands

This project uses the Gradle wrapper (`./gradlew`) for all build-related tasks.

*   **Run the application:**
    ```bash
    ./gradlew run
    ```

*   **Run tests:**
    ```bash
    ./gradlew test
    ```

*   **Build the project:**
    ```bash
    ./gradlew build
    ```

*   **Clean the build directory:**
    ```bash
    ./gradlew clean
    ```

*   **Check for dependency updates:**
    ```bash
    ./gradlew dependencyUpdates
    ```

*   **Run SonarQube analysis (requires a configured token):**
    ```bash
    ./gradlew sonar
    ```

## Development Guidelines

*   **Code Style:** Adhere to standard Java conventions and match the style of existing code.
*   **Testing:** All new features and bug fixes must be accompanied by unit or integration tests. A minimum of 70% test coverage is enforced via the Jacoco plugin.
*   **Commit Messages:** Follow the conventional commit format (e.g., `feat:`, `fix:`, `docs:`, `test:`). Refer to the existing `git log` for examples.
*   **Dependencies:** Use the `./gradlew allDependencies` task to review the dependency tree when adding or updating libraries.
