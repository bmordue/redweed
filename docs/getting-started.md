# Getting Started

This guide will help you get the redweed application up and running on your local machine.

## Prerequisites

*   [Java 11 or higher](https://openjdk.java.net/install/)
*   [Node.js 14 or higher](https://nodejs.org/en/download/)
*   [npm 6 or higher](https://www.npmjs.com/get-npm)

## Installation

1.  **Clone the repository:**

    ```bash
    git clone https://github.com/bmordue/redweed.git
    cd redweed
    ```

2.  **Install frontend dependencies:**

    ```bash
    cd frontend
    npm install
    cd ..
    ```

3.  **Install backend dependencies:**

    The backend dependencies are managed by Gradle and will be downloaded automatically when you run the application.

## Running the application

You will need to run the backend and frontend applications in separate terminals.

### Backend

To run the backend application, run the following command from the root of the project:

```bash
./jweed/gradlew run
```

The backend server will start on port 8080.

### Frontend

To run the frontend application, run the following command from the `frontend` directory:

```bash
npm start
```

The frontend development server will start on port 3000. You can view the application by opening [http://localhost:3000](http://localhost:3000) in your browser.
