# Conditionals

Routes a message through a comlink or hologram task using `doIfElse()`.

## Prerequisites

A compatible LittleHorse Server must be running and reachable through `LHC_*` environment variables or SDK defaults. See the shared [server setup](../../README.md#littlehorse-server-version).

From the repository root:

```bash
./gradlew :examples:lh-server:java:04-conditionals:run
lhctl run send-message user-id anakin message "Hello there"
```