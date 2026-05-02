# Backend Testing Summary

This backend uses automated tests to verify authentication, security, validation, and core API behaviour.

## Test Strategy

The backend test suite combines controller-level integration tests and smaller unit-style tests.

Controller integration tests use Spring Boot MockMvc to simulate real HTTP requests against API endpoints. These tests cover successful requests, authentication failures, invalid input rejection, role-based access control, and common error responses.

Unit-style tests isolate smaller pieces of logic such as JWT handling, password validation, and request validation. This keeps low-level validation logic testable without relying only on full controller tests.

This structure was used to check both external API behaviour and internal validation/security logic.

## Test command

```bash
cd backend/goodfood
./gradlew clean test jacocoTestReport
```

## CI/CD Evidence

GitHub Actions is configured to run the backend test suite automatically on the main branch. The latest workflow runs passed successfully, showing that the committed code builds and tests correctly in CI.

Evidence to include in the portfolio:

- Screenshot of the green GitHub Actions workflow run.
- Link or screenshot showing the latest successful commit workflow.
- Uploaded test and coverage reports from GitHub Actions artifacts, where available.

## Coverage Evidence

JaCoCo was added to provide quantitative coverage evidence.

Final backend coverage:

- Line coverage: 74.88%
- Branch coverage: 39.10%
- Method coverage: 70.37%
- Class coverage: 79.20%

The nutrition package improved to 53.86% line coverage after adding controller tests.

Evidence to include in the portfolio:

- Screenshot of `build/reports/jacoco/test/html/index.html`.
- Screenshot or text output from `jacocoTestReport.xml` summary.

## Security Testing

Security testing is covered separately because the marking criteria explicitly asks for evidence of security testing.

The security-related tests check that:

- Protected endpoints reject unauthenticated requests.
- Invalid or malformed JWT tokens are rejected.
- Subscribers cannot access professional-only endpoints.
- Users cannot access or modify data that belongs to another user.
- HTML/script input is rejected in comment and message-related flows.
- Role boundaries are enforced for plans, appointments, messages, and client-related endpoints.

## Tested Areas

The test suite covers:

- Authentication and login/register behaviour
- JWT token generation and validation
- Password validation
- Security and unauthenticated access rejection
- Request validation
- Client binding
- Diary entries
- Exercise entries
- Recipes and recipe search
- Favourites
- Comments
- Ratings
- Plans
- Appointments
- Messages
- Notifications
- Nutrition endpoints

## E2E Testing

Frontend E2E tests are also present in the repository under the Playwright test files. These tests support the backend work by checking user-facing flows through the interface, such as authentication and main page behaviour.

Relevant files include:

- `tests/e2e/api.spec.js`
- `tests/e2e/index.spec.js`

## Known Limitations

The test suite prioritises API behaviour, authentication, authorisation, validation, and key user flows. Branch coverage is lower than line coverage because not every conditional path or rare error path is exhaustively tested.

The current branch coverage is 29.49%. Further improvement would require adding more tests for uncommon error paths, such as invalid appointment statuses, missing resources, forbidden access to other users' data, and more service-level branch cases.

The project should not claim near-perfect coverage. Instead, the evidence should describe the suite as broad, automated, measurable, and supported by CI/CD and security testing.
