# Project Roadmap

This document outlines potential new features for the `redweed` project. These features are not yet prioritized and are intended for future refinement and planning.

## Proposed Features

1.  **Tagging/Categorization of Photos:** Allow users to add tags or categories to photos. This would involve a new API endpoint to add/remove tags from a photo, and the ability to search for photos by tag.

2.  **User Authentication:** Implement user accounts and authentication to restrict access to certain API endpoints. This would likely involve adding a user model to the database and using a library like Buddy for handling authentication tokens.

3.  **Advanced Photo Search:** Extend the photo search capabilities to allow searching by date range, location (if GPS data is available in EXIF), and other metadata fields.

4.  **Album Management:** Introduce the concept of photo albums, allowing users to group photos together. This would require new API endpoints for creating, deleting, and managing albums.

5.  **Integration with a Frontend Application:** Develop a simple frontend application (e.g., using React or Vue.js) that consumes the `redweed` API to provide a user-friendly interface for managing photos.

6.  **VCard Import/Export:** Full support for importing and exporting contacts in vCard format. This would involve enhancing the existing vCard functionality to handle a wider range of vCard properties and to support batch import/export operations.

7.  **Calendar/Event Management:** Add support for managing calendar events, which can be linked to people, places, and media. This would include API endpoints for creating, updating, and deleting events, as well as for querying events by date range or other criteria.

8.  **Geolocation and Mapping:** Extract GPS data from photos and display them on a map. This would involve adding a new API endpoint to retrieve photos with GPS data, enabling a client application to display them on a map (e.g., using Leaflet or OpenLayers).

9.  **Support for Additional Media Types:** Extend the application to handle videos and audio files, with metadata extraction and search capabilities. This would require adding support for different file types and extending the metadata extraction process to handle video and audio-specific metadata.

10. **Linked Data Integration:** Connect to external Linked Data sources like DBpedia or Wikidata to enrich the existing data with additional information. This could involve adding a mechanism to link local resources to external URIs and to fetch and cache data from external sources.

## Documentation

*   **Deployment Guide:** A guide that explains how to deploy the application to a production environment.
*   **Configuration Guide:** A guide that explains all of the available configuration options.
*   **Troubleshooting Guide:** A guide that provides solutions to common problems.
*   **Developer Guide:** A guide for developers who want to contribute to the project. This would include information about the development workflow, coding standards, and how to run the tests.
