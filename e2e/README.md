# E2E harness — IRN route authorization (Studio API)

The whole E2E stack (this app + the Management API + mocks) lives in ONE compose file in the
management repo:

`../../igrp-process-management-backend-api/e2e/docker-compose.e2e.yml` (project `irn-e2e`)

See `../../igrp-process-management-backend-api/e2e/README.md` for usage, stubbed sessions, and
token minting. This app is published on http://localhost:18082.

Build this repo's jar on the host before `up`:

```sh
mvn -o -DskipTests package
```
