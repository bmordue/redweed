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
    *   `201 Created`: vCard data imported successfully.
        ```json
        {
          "status": "success",
          "person-uri": "...",
          "message": "vCard data imported successfully"
        }
        ```
    *   `400 Bad Request`: Invalid vCard data.
        ```json
        {
          "error": "Invalid vCard format"
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
          "message": "Photo uploaded successfully",
          "file-uri": "..."
        }
        ```
    *   `500 Internal Server Error`: Error processing the photo.
        ```json
        {
          "error": "Error processing photo upload"
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

## iCal Import

### POST /api/ical/import

*   **Description:** Imports iCalendar data (events, tasks, journal entries) into the RDF store.
*   **Consumes:** `text/calendar`, `application/json`
*   **Parameters:**
    *   `body`: The iCalendar data as a string (RFC 5545 format).
*   **Responses:**
    *   `201 Created`: iCalendar data imported successfully.
        ```json
        {
          "status": "success",
          "imported": {
            "events": 5,
            "tasks": 3,
            "journals": 2
          },
          "calendar-uri": "redweed:calendar/abc123",
          "message": "iCalendar data imported successfully"
        }
        ```
    *   `400 Bad Request`: Invalid iCalendar data.
        ```json
        {
          "error": "Invalid iCalendar format",
          "details": "Missing required PRODID property"
        }
        ```
    *   `422 Unprocessable Entity`: Some components could not be processed.
        ```json
        {
          "status": "partial_success",
          "imported": {
            "events": 3,
            "tasks": 1,
            "journals": 0
          },
          "warnings": [
            "Invalid timezone in event 'Meeting with John'",
            "Missing summary in task component"
          ],
          "calendar-uri": "redweed:calendar/abc123"
        }
        ```

### POST /api/ical/import/file

*   **Description:** Uploads and imports an iCalendar file.
*   **Consumes:** `multipart/form-data`
*   **Parameters:**
    *   `file`: The .ics file to upload and import.
    *   `calendar-name` (optional): Name for the imported calendar.
*   **Responses:**
    *   `201 Created`: iCalendar file imported successfully.
        ```json
        {
          "status": "success",
          "filename": "mycalendar.ics",
          "imported": {
            "events": 12,
            "tasks": 5,
            "journals": 1
          },
          "calendar-uri": "redweed:calendar/mycalendar"
        }
        ```
    *   `415 Unsupported Media Type`: Invalid file format.
        ```json
        {
          "error": "Unsupported file type. Only .ics files are supported."
        }
        ```

### GET /api/ical/export

*   **Description:** Exports calendar data in iCalendar format.
*   **Parameters:**
    *   `start_date` (query, optional): Start date for export range (YYYY-MM-DD).
    *   `end_date` (query, optional): End date for export range (YYYY-MM-DD).
    *   `calendar` (query, optional): Specific calendar URI to export.
*   **Produces:** `text/calendar`
*   **Responses:**
    *   `200 OK`: iCalendar data exported successfully.
        ```
        BEGIN:VCALENDAR
        VERSION:2.0
        PRODID:-//Redweed//Redweed Calendar Export//EN
        CALSCALE:GREGORIAN
        BEGIN:VEVENT
        UID:event123@redweed.local
        DTSTART:20240315T100000Z
        DTEND:20240315T110000Z
        SUMMARY:Project Meeting
        DESCRIPTION:Weekly project status meeting
        LOCATION:Conference Room A
        END:VEVENT
        END:VCALENDAR
        ```
