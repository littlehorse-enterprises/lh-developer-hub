# WfSpec App

This example registers a simple WfSpec to the LittleHorse server along with the greet task.
The worker-start snippet in this app is:

```java
Runtime.getRuntime().addShutdownHook(new Thread(greetWorker::close));
greetWorker.start();

```

This starts the worker and adds a shutdown hook so it closes cleanly when you exit the program.

## Prerequisites
- Java 21+

## Run
From the repository root:

```sh
./gradlew :examples:lh-server:java:wfspec-app:run
```

From this directory:

```sh
../../../../gradlew :examples:lh-server:java:wfspec-app:run
```
