# Notification Module

The notification module tells users when something happens to their to-do lists and tasks. Each notification is **stored in the database** (so the client can show a notification list and track read / unread state) and **pushed to the user's devices** through Firebase Cloud Messaging (FCM).

This module covers in-app notification messages only. Emails (password reset OTP, the daily reminder email) are sent through `EmailChannel` and are **not** stored as notifications.

## Features

- Store a notification for every create / update of a to-do list or task, and for every to-do list that starts today.
- Push each notification to all devices registered by the user (FCM).
- Get the 5 most recent notifications of the current user.
- Get the number of unread notifications.
- Mark one notification as read or unread, or mark all as read.
- Register a device's FCM token for push notifications.

## Package Structure

```text
common/
├── notification/
│   ├── channel/NotificationChannel.java          # Interface shared by delivery channels (push, email)
│   ├── controller/NotificationController.java    # REST API: /api/notification/**
│   ├── domain/
│   │   ├── MessageType.java                      # Event types that produce a notification
│   │   ├── MessageContent.java                   # (title, body) pair
│   │   ├── dto/NotificationResponseDto.java      # API response shape
│   │   └── entity/Notification.java              # JPA entity, table "notification"
│   ├── internal/repository/NotificationRepository.java
│   └── service/
│       ├── NotficationMessageFactory.java        # Builds title + body for each MessageType
│       └── NotificationService.java              # Entry point used by other modules
└── fcm/
    ├── config/FirebaseConfig.java                # FirebaseApp + FirebaseMessaging beans
    ├── controller/PushNotificationController.java# POST /push-token
    ├── domain/entity/DeviceToken.java            # One row per (device token, user)
    ├── internal/repository/DeviceTokenRepository.java
    └── service/PushNotificationChannel.java      # Sends messages through FCM
```

## Data Model

### `notification` table (`Notification` entity)

| Field | Type | Description |
|---|---|---|
| `id` | Integer | Primary key (from `BaseEntity`) |
| `userId` | Integer, not null | Owner of the notification |
| `type` | `MessageType` (stored as string), not null | Event that produced it |
| `title` | String, not null | Short title, e.g. `Task Created` |
| `body` | String (max 1000), not null | Full message text |
| `isRead` | boolean, not null, default `false` | Read state |
| `readAt` | LocalDateTime, nullable | When it was marked read; `null` while unread |
| `createdAt` | LocalDateTime | Creation time (from `BaseEntity`) |
| `lastModifiedAt` | LocalDateTime | Last change (from `BaseEntity`) |

Index: `idx_notification_user_created` on `(userId, createdAt)`, used by the "recent notifications" query.

### `device_token` table (`DeviceToken` entity)

| Field | Type | Description |
|---|---|---|
| `id` | Integer | Primary key |
| `userId` | Integer, not null | User currently logged in on the device |
| `token` | String, not null | FCM registration token of the device |

A user can have several devices (several rows).

## Message Types

Messages are built by `NotficationMessageFactory.create(type, resourceName)`, where `resourceName` is the to-do list topic or the task name.

| `MessageType` | Title | Body |
|---|---|---|
| `TODO_LIST_CREATED` | ToDo List Created | Your ToDo List '`<topic>`' has been created successfully. |
| `TODO_LIST_UPDATED` | ToDo List Updated | Your ToDo List '`<topic>`' has been updated successfully. |
| `TASK_CREATED` | Task Created | Your task '`<name>`' has been created successfully. |
| `TASK_UPDATED` | Task Updated | Your task '`<name>`' has been updated successfully. |
| `TODO_LIST_DUE` | To Do List Starts Today | Your To Do List '`<topic>`' is scheduled to start today. |

To add a new kind of notification, add a value to `MessageType` and a matching case in `NotficationMessageFactory`. The `switch` has no default branch, so the compiler reports any type that has no message.

## Where Notifications Are Sent

| Trigger | Code | Type |
|---|---|---|
| Create a to-do list | `ToDoService.createToDo` | `TODO_LIST_CREATED` |
| Update a to-do list | `ToDoService.updateToDoById` | `TODO_LIST_UPDATED` |
| Create a task | `TaskService.create` | `TASK_CREATED` |
| Update a task | `TaskService.updateById` | `TASK_UPDATED` |
| Daily reminder, 00:00 Asia/Bangkok | `ScheduleService.sendDailyReminder` | `TODO_LIST_DUE`, one per to-do list starting today |

The daily reminder still sends its email as before. The in-app notifications are sent in addition to the email, and the schedule's `SENT` / `FAILED` status depends only on the email result.

## How Sending Works

Other modules call one method:

```java
notificationService.notify(userId, MessageType.TASK_CREATED, task.getName());
```

`notify` does the following:

1. Builds the title and body with `NotficationMessageFactory`.
2. Saves a `Notification` row (unread).
3. Calls `PushNotificationChannel.sendToUser`, which sends one FCM message to every device token of the user.
4. Returns the saved notification as a `NotificationResponseDto`.

**Failure handling**

- If the push fails, the error is logged as a warning and `notify` still returns normally. The business operation (creating a task, for example) is not rolled back, and the stored notification is still visible in the app.
- If FCM answers `UNREGISTERED` or `INVALID_ARGUMENT` for a token, that token is deleted, because it will never work again (app uninstalled, token expired, or not a valid token).
- Other FCM errors are logged and the token is kept.
- A user with no registered device still gets the stored notification; only the push is skipped.

## REST API

All endpoints require a valid JWT access token (`Authorization: Bearer <token>`). The user is always taken from the token, so a user can only see and change their own notifications.

Every response uses the standard envelope:

```json
{ "success": true, "data": ..., "message": "..." }
```

### Notification object

```json
{
  "id": 12,
  "type": "TASK_CREATED",
  "title": "Task Created",
  "body": "Your task 'Buy milk' has been created successfully.",
  "isRead": false,
  "readAt": null,
  "createdAt": "2026-11-26T09:15:30"
}
```

### Endpoints

| Method | Path | Description | `data` in response |
|---|---|---|---|
| GET | `/api/notification/recent` | The 5 most recent notifications, newest first | Array of notification objects (0–5 items) |
| GET | `/api/notification/unread-count` | Number of unread notifications | Number |
| PATCH | `/api/notification/{id}/read` | Mark one notification as read | Updated notification object |
| PATCH | `/api/notification/{id}/unread` | Mark one notification as not read | Updated notification object |
| PATCH | `/api/notification/read-all` | Mark all unread notifications as read | Number of notifications changed |
| POST | `/push-token` | Register this device for push notifications | `null` |

Notes:

- Marking a notification that is already read as read (or an unread one as unread) changes nothing and returns it as it is.
- `{id}` that does not exist **or belongs to another user** returns `404` with message `Notification not found`.

### Register a device token

The client app gets its FCM registration token from the Firebase SDK and sends it after login:

```http
POST /push-token
Authorization: Bearer <access token>
Content-Type: application/json

{ "token": "<FCM registration token>" }
```

- A new token is stored for the current user.
- A token that is already stored for the same user is ignored.
- A token stored for **another** user is moved to the current user. This handles a second person logging in on the same device, so the first person's notifications stop arriving there.

### Example: client polling flow

1. On login, call `POST /push-token`.
2. To show the badge, call `GET /api/notification/unread-count`.
3. When the user opens the notification panel, call `GET /api/notification/recent`.
4. When the user taps a notification, call `PATCH /api/notification/{id}/read`.

## Configuration

`application.yml`:

```yaml
firebase:
  config:
    path: firebase/service-account.json   # classpath location of the service account key
```

- Put the Firebase service-account JSON at `src/main/resources/firebase/service-account.json`. You download it from Firebase Console → Project settings → Service accounts.
- The app **does not start** if this file is missing or invalid, because `FirebaseConfig` creates the `FirebaseApp` bean at startup.
- `FirebaseConfig` reuses an existing `FirebaseApp` if one is already initialised, so DevTools restarts and tests that start the context again do not fail.
- The `notification` and `device_token` tables are created by Hibernate (`ddl-auto: update`).

> **Security:** the service-account file is a private key for your Firebase project. Do not commit it; add `src/main/resources/firebase/` to `.gitignore` and provide the file per environment.

## Known Limitations

- **Pushes are sent synchronously.** The FCM call runs inside the request, so a slow Firebase response makes create and update requests slower. Sending in the background (`@Async` or an event listener) would remove that delay.
- **Pushes are sent before the transaction commits.** `TaskService.create` is transactional. If it were rolled back after `notify`, the user would already have received the push. Sending after commit (`@TransactionalEventListener(phase = AFTER_COMMIT)`) would avoid that.
- **`device_token.token` is not unique.** Two registrations of the same token at the same moment can create a duplicate row. A unique constraint fixes this, but existing duplicate rows must be removed first or the schema update fails at startup.
- **Only the 5 most recent notifications can be listed.** There is no paginated history endpoint yet.
- **Notifications are never deleted.** The table grows over time; a cleanup job for old, read notifications may be needed later.
- **Time zones are mixed.** `createdAt` uses the server's default time zone, while the reminder schedule uses Asia/Bangkok.
