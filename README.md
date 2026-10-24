# ToDo Management Application

A RESTful backend application for managing personal to-do lists, tasks, notes, and projects. It provides user registration and login, JWT-based authentication, task progress tracking, and paginated/filterable queries.

## Features

- **Authentication**
  - Register a user and log in with email and password.
  - Receive a JWT access token after successful login.
  - Retrieve the currently authenticated user's profile.
- **To-do list management**
  - Create, retrieve, update, and delete to-do lists.
  - Retrieve the current user's lists or query lists by user ID.
  - Filter and paginate to-do lists.
- **Task management**
  - Create, retrieve, update, and delete tasks.
  - List tasks belonging to a to-do list with filtering and pagination.
  - Mark a task as completed.
  - Set expected start and end times.
- **Note management**
  - Create a note for a task.
  - Retrieve, update, or delete a task's note.
- **Project management**
  - Create, retrieve, update, and delete projects.
  - Retrieve the current user's projects with filtering and pagination.
- **Consistent API responses**
  - Responses use a common `success`, `data`, and `message` structure.
  - Collection endpoints use a paginated response with content and metadata.

## Tech Stack

The API document identifies this as an HTTP REST API and documents JWT bearer authentication. The implementation's exact framework, database, build tool, and versions are not specified in the API document.

## Getting Started

### Prerequisites

- A running instance of the application.
- An API client such as [Postman](https://www.postman.com/) or [curl](https://curl.se/).

The documented local server address is:

```text
http://localhost:8080
```

Start the application using the run instructions and configuration in the source repository. Database setup and environment-variable requirements are not specified in the API document.

## API Documentation

The API is described using OpenAPI 3.1.0. If Swagger UI is enabled in the application, use the Swagger UI URL configured by the project to explore and execute requests. The exact Swagger UI URL is not specified in the supplied API document.

Base URL:

```text
http://localhost:8080
```

## Authentication

Most endpoints are documented with JWT bearer authentication. Register and log in first, then send the access token in the `Authorization` header.

```http
Authorization: Bearer <access_token>
```

### Register

`POST /api/auth/register`

```json
{
  "username": "demo_user",
  "email": "demo@example.com",
  "password": "ChangeMe123"
}
```

The documented validation constraints are: username at least 4 characters, password 6–72 characters, and a valid email address.

### Login

`POST /api/auth/login`

```json
{
  "email": "demo@example.com",
  "password": "ChangeMe123"
}
```

The response data contains an `accessToken` and `tokenType`.

### Get current user

`GET /api/auth/me`

Returns the authenticated user's profile.

## API Endpoints

All paths below are relative to `http://localhost:8080`. Request and response bodies are JSON unless otherwise noted. Endpoints are grouped according to the supplied OpenAPI document.

### Authentication

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/api/auth/register` | Register a user |
| `POST` | `/api/auth/login` | Log in and obtain an access token |
| `GET` | `/api/auth/me` | Get the current user's profile |

### To-do lists

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/api/todo/` | Create a to-do list |
| `GET` | `/api/todo/{id}` | Get a to-do list by ID |
| `PATCH` | `/api/todo/{id}` | Update a to-do list |
| `DELETE` | `/api/todo/{id}` | Delete a to-do list |
| `GET` | `/api/todo/user/{id}` | Get lists for a user ID, with filtering and pagination |
| `GET` | `/api/todo/me` | Get the current user's lists, with filtering and pagination |

Example create request:

```json
{
  "topic": "Prepare for backend interview",
  "startTime": "2026-11-01T09:00:00"
}
```

The topic is documented with a minimum length of 10 characters. `startTime` uses the `date-time` format.

### Tasks

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/api/task` | Create a task |
| `GET` | `/api/task/{id}` | Get a task by ID |
| `PATCH` | `/api/task/{id}` | Update a task |
| `DELETE` | `/api/task/{id}` | Delete a task |
| `PATCH` | `/api/task/done/{id}` | Mark a task as completed |
| `GET` | `/api/task/todo/{id}` | Get tasks for a to-do list, with filtering and pagination |

Example create request:

```json
{
  "name": "Review Spring Security",
  "description": "Review JWT authentication and authorization",
  "toDoListId": 1,
  "expectStart": "2026-11-01T09:00:00",
  "expectEnd": "2026-11-01T10:30:00"
}
```

The documented constraints require `description` and `toDoListId`; `name` has a minimum length of 5 characters. The expected start and end fields use the `date-time` format.

### Notes

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/api/note/` | Create a note for a task |
| `GET` | `/api/note/{taskId}` | Get a note by task ID |
| `PUT` | `/api/note/{taskId}` | Update a note by task ID |
| `DELETE` | `/api/note/{taskId}` | Delete a note by task ID |

Example create request:

```json
{
  "description": "Remember to check the acceptance criteria before submitting.",
  "taskId": 1
}
```

The documented schema requires `taskId` and specifies a minimum description length of 10 characters.

### Projects

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/api/project/` | Create a project |
| `GET` | `/api/project/{id}` | Get a project by ID |
| `PATCH` | `/api/project/{id}` | Update a project |
| `DELETE` | `/api/project/{id}` | Delete a project |
| `GET` | `/api/project/me` | Get the current user's projects, with filtering and pagination |

Example create request:

```json
{
  "topic": "Backend Learning Plan",
  "description": "Organize backend development learning goals and milestones.",
  "startTime": "2026-11-01T09:00:00",
  "expectEnd": "2026-12-01T18:00:00"
}
```

The documented constraints specify a topic of at least 5 characters and a description of at least 20 characters.

## Filtering and Pagination

The list endpoints below accept query parameters for filters and pagination:

- `GET /api/todo/user/{id}`
- `GET /api/todo/me`
- `GET /api/task/todo/{id}`
- `GET /api/project/me`

The OpenAPI schema describes `page`, `size`, and `sort` as pageable fields. It also describes `filters` or `filter` as an optional array of strings, depending on the endpoint.

Example pagination query:

```http
GET /api/todo/me?page=0&size=10&sort=startTime,desc
```

The exact filter-string syntax is not defined in the supplied API document. Check the implementation or Swagger UI for the supported field names, operators, and value formats.

Paginated responses contain:

- `content`: the returned items.
- `meta.page`: current page number.
- `meta.size`: page size.
- `meta.totalElements`: total number of matching items.
- `meta.totalPages`: total number of pages.

## Response Format

Successful API responses use a wrapper with the following fields:

```json
{
  "success": true,
  "data": {
    "id": 1
  },
  "message": "Operation completed successfully"
}
```

The example above is illustrative; actual `data` fields and message text depend on the endpoint. The OpenAPI document does not specify a common error-response schema or the full set of HTTP error status codes.

A paginated response follows this general structure:

```json
{
  "success": true,
  "data": {
    "content": [],
    "meta": {
      "page": 0,
      "size": 10,
      "totalElements": 0,
      "totalPages": 0
    }
  },
  "message": "Operation completed successfully"
}
```

## Typical Workflow

1. Register using `POST /api/auth/register`.
2. Log in using `POST /api/auth/login`.
3. Copy the returned access token and use it as a bearer token for protected endpoints.
4. Create a to-do list using `POST /api/todo/`.
5. Add tasks to the list using `POST /api/task`.
6. Optionally attach a note to a task using `POST /api/note/`.
7. Mark a task as complete using `PATCH /api/task/done/{id}`.
8. Retrieve and filter lists, tasks, or projects as needed.

## Notes and Limitations

- The supplied API document lists project endpoints, but it does not define a project-to-task creation endpoint or explicitly document how tasks are associated with projects.
- The API document describes bearer authentication globally; the exact authorization requirements for each endpoint should be confirmed against the running application.
- Database configuration, deployment instructions, test commands, and environment variables are not included in the API document.
- Some schema fields are optional in the OpenAPI document even where the application may apply additional business validation. Runtime validation is determined by the implementation.

## License

No license information was included in the supplied API document. Add a `LICENSE` file and update this section if you intend to publish the project.
