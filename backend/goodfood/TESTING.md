# Backend Testing Summary

This backend uses automated tests to verify authentication, security, validation, and core API behaviour.

## Test command

```bash
cd backend/goodfood
./gradlew clean test jacocoTestReport
JaCoCo was added to provide quantitative coverage evidence.

Final backend coverage:

Line coverage: 64.82%
Method coverage: 59.13%
Class coverage: 74.40%

The nutrition package improved to 53.86% line coverage after adding controller tests.

Tested areas

The test suite covers:

Authentication and login/register behaviour
JWT token generation and validation
Password validation
Security and unauthenticated access rejection
Request validation
Client binding
Diary entries
Exercise entries
Recipes and recipe search
Favourites
Comments
Ratings
Plans
Appointments
Messages
Notifications
Nutrition endpoints
CI/CD

Tests are automatically executed using GitHub Actions. The latest workflow runs passed successfully on the main branch.
