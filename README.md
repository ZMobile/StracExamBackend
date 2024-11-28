# StracExamBackend

## Overview

StracExamBackend is a Spring Boot Maven application designed to integrate with Google Drive, providing the following core functionalities:

1. **User Authentication**: Authenticate users via OAuth 2.0 to access their Google Drive.
2. **List Files**: Retrieve and display the list of files from the user's Google Drive, including file names, types, and last modified dates.
3. **Upload Files**: Enable users to upload files from their local system to their Google Drive.
4. **Download Files**: Allow users to download files from their Google Drive to their local system.
5. **Delete Files**: Provide functionality for users to delete files from their Google Drive.

This project demonstrates clean code practices, adherence to SOLID principles, and proper unit and integration testing.

---

## Features

- **OAuth 2.0 Authentication**: Secure authentication using Google APIs.
- **Google Drive Integration**:
    - List files with metadata (name, type, last modified date).
    - Upload files from the local machine.
    - Download files to the local machine.
    - Delete files from Google Drive.
- **Spring Security Integration**:
    - Role-based access control.
    - CSRF protection.
    - Session management.
- **Test Coverage**:
    - Unit tests for core components.
    - Integration tests for end-to-end workflows.
- **Clean Architecture**:
    - Modular and maintainable design.
    - Clear separation of concerns and use of design patterns.

---

## Prerequisites

To set up and run the project, ensure you have the following:

- **Java**: Version 21 or higher
- **Maven**: Version 3.9.9 or higher
- **Google Cloud Platform**: A project with Google Drive API enabled
- **Google OAuth 2.0 Credentials**:
    - Download the `credentials.json` file for the project.

---

## Setup Instructions

### 1. Clone the Repository
```bash
git clone https://github.com/your-username/StracExamBackend.git
cd StracExamBackend

### 2. Compile the Application
To compile and run tests:
```bash
mvn clean install
```

### 3. Configure Google OAuth 2.0 Credentials
1. Create a project on the Google Cloud Platform.
2. Enable the Google Drive API for the project.
3. Enable the following scopes for the project:
    - `https://www.googleapis.com/auth/drive`
3. Go to APIs & Services > Credentials.
4. Click on "Create Credentials" and select "OAuth client ID".
5. Choose "Web application" as the application type.
6. Add the following URI to the authorized redirect URIs:
    - `http://localhost:8081/oauth2/callback
    - or whatever you redirect url you have configured in:
      - your **front-end** application at org.strac.service.GoogleDriveAuthenticatorServiceImpl REDIRECT_ENDPOINT and REDIRECT_PORT variables
      - as well as your **backend** application org.strac.dao.config.StracExamDaoConfig REDIRECT_URL variables
7. Download the `credentials.json` file and save it in a path consistent with the configured org.strac.dao.config.StracExamDaoConfig GOOGLE_CREDENTIALS_PATH variable (default: USER_HOME/documents/.dev/.strac/keys/google/credentials.json)

### 4. Run the Application
To run the application:
```bash
mvn spring-boot:run
```

Or run the Main class directly:
```bash
org.strac.Main.java
```