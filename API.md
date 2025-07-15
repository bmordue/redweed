# Redweed API Documentation

This document provides a detailed description of the Redweed API endpoints.

## Health Check

### GET /health

*   **Description:** Checks the health of the server.
*   **Parameters:** None
*   **Responses:**
    *   `200 OK`: Server is healthy.
        ```json
        {
          "status": "ok",
          "service": "Redweed Server"
        }
        ```

## vCard

### POST /api/vcard/import

*   **Description:** Imports vCard data into the RDF store.
*   **Consumes:** `text/vcard`, `application/json`
*   **Parameters:**
    *   `body`: The vCard data as a string.
*   **Responses:**
    *   `200 OK`: vCard data imported successfully.
        ```json
        {
          "message": "vCard data imported successfully"
        }
        ```
    *   `400 Bad Request`: Invalid vCard data.
        ```json
        {
          "error": "Invalid vCard data"
        }
        ```

## Photo

### POST /api/photo/upload

*   **Description:** Uploads a photo.
*   **Consumes:** `multipart/form-data`
*   **Parameters:**
    *   `file`: The photo file to upload.
*   **Responses:**
    *   `200 OK`: Photo uploaded successfully.
        ```json
        {
          "message": "File processed successfully",
          "file-uri": "..."
        }
        ```
    *   `500 Internal Server Error`: Error processing the photo.
        ```json
        {
          "error": "Error processing photo"
        }
        ```

## Contacts

### GET /contacts

*   **Description:** Lists all contacts.
*   **Parameters:** None
*   **Responses:**
    *   `200 OK`: A list of contacts.
        ```json
        {
          "contacts": [
            {
              "person": "...",
              "name": "...",
              "givenName": "...",
              "familyName": "..."
            }
          ]
        }
        ```

### GET /contacts/:name

*   **Description:** Gets a contact by name.
*   **Parameters:**
    *   `name` (path): The full name of the contact.
*   **Responses:**
    *   `200 OK`: The contact information.
        ```json
        {
          "contact": {
            "person": "...",
            "name": "...",
            "givenName": "...",
            "familyName": "..."
          },
          "events": [
            {
              "event": "...",
              "eventLabel": "...",
              "eventTime": "..."
            }
          ]
        }
        ```
    *   `404 Not Found`: Contact not found.
        ```json
        {
          "error": "Contact not found"
        }
        ```

## Events

### GET /events

*   **Description:** Lists events in a date range.
*   **Parameters:**
    *   `start_date` (query): The start date of the range (YYYY-MM-DD).
    *   `end_date` (query): The end date of the range (YYYY-MM-DD).
*   **Responses:**
    *   `200 OK`: A list of events.
        ```json
        {
          "events": [
            {
              "event": "...",
              "label": "...",
              "time": "...",
              "startDate": "...",
              "agent": "...",
              "agentName": "...",
              "place": "...",
              "placeLabel": "..."
            }
          ],
          "date-range": {
            "start": "...",
            "end": "..."
          }
        }
        ```

## Places

### GET /places

*   **Description:** Lists all places.
*   **Parameters:** None
*   **Responses:**
    *   `200 OK`: A list of places.
        ```json
        {
          "places": [
            {
              "place": "...",
              "label": "...",
              "lat": "...",
              "long": "...",
              "type": "..."
            }
          ]
        }
        ```
