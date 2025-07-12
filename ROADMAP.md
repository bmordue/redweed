# Project Roadmap

This document outlines potential new features for the `redweed` project. These features are not yet prioritized and are intended for future refinement and planning.

## Proposed Features

1.  **Tagging/Categorization of Photos:** Allow users to add tags or categories to photos. This would involve a new API endpoint to add/remove tags from a photo, and the ability to search for photos by tag.

2.  **User Authentication:** Implement user accounts and authentication to restrict access to certain API endpoints. This would likely involve adding a user model to the database and using a library like Buddy for handling authentication tokens.

3.  **Advanced Photo Search:** Extend the photo search capabilities to allow searching by date range, location (if GPS data is available in EXIF), and other metadata fields.

4.  **Album Management:** Introduce the concept of photo albums, allowing users to group photos together. This would require new API endpoints for creating, deleting, and managing albums.

5.  **Integration with a Frontend Application:** Develop a simple frontend application (e.g., using React or Vue.js) that consumes the `redweed` API to provide a user-friendly interface for managing photos.
