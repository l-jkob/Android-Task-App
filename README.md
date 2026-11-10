# Task App: Android to-do app with offline sync

A native Android app written in Java for managing tasks, built as a group
semester project for the *Mobile Applications and Systems* course at TH Brandenburg.
It works offline with a local database and synchronizes with a REST backend
(Java EE on WildFly) whenever the server is reachable.

## Features

- **Login** with email and a 6-digit PIN, validated against the backend
- **Tasks** with name, description, due date/time, *done* and *favorite* flags
- **Sorting** by due date or by importance (open before done, favorites first)
- **Offline-first persistence** with SQLite via Room
- **Synchronization** with a RESTful backend on startup; falls back to offline mode
  when the server can't be reached
- **Contacts integration**: link people from the device address book
  (Contacts Provider) to tasks and view all tasks per contact

## Architecture

```
task-app/
├── app/        Android client (Java, minSdk 31, targetSdk 36)
│   ├── ui/          Activities and views (MVC)
│   ├── controller/  Presentation logic per screen
│   ├── db/          Room DAO, REST accessor (RESTEasy), SyncEngine, ContactLoader
│   └── model/       Todo, Users, Contact
└── backend/    JAX-RS REST service, packaged as a WAR (Java EE 7)
```

The client talks to the backend through a common `TodoCRUDAccessor` interface,
implemented once for the local Room database and once for the REST API.
`SyncEngine` uses both: if local data exists it is pushed to the server,
otherwise the server state is pulled into the local database.

## Running it

**Backend**

1. Build the WAR: `cd task-app && ./gradlew :backend:war`
2. Download [WildFly 16.0.0.Final](https://www.wildfly.org/downloads/) and copy
   `task-app/backend/build/libs/backend-1.0-SNAPSHOT.war` into
   `wildfly-16.0.0.Final/standalone/deployments/`
3. Start the server: `wildfly-16.0.0.Final/bin/standalone.sh`

**App**

Open `task-app/` in Android Studio and run the `app` configuration on an emulator.
The app reaches the backend at `http://10.0.2.2:8080/backend-1.0-SNAPSHOT/rest`
(the emulator's alias for the host machine).

## Tech stack

Java · Android SDK · Room / SQLite · RESTEasy · Jackson · JAX-RS · WildFly · Gradle
