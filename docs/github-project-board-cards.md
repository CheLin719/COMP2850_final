# GitHub Project Board Cards

Project board columns: Backlog / In Progress / Review / Done

Assignee rule used for this board:
- `ac`: work described in `Backend_API_Handoff 1.docx`, mainly backend API implementation and handoff.
- `chelin`: all remaining website, frontend, documentation, UX, and integration work.

## Done

| Card | Type | Label | Assignee | Summary |
|---|---|---|---|---|
| User registration, login, JWT session flow | User story | feature | ac | Implement `/api/auth/register`, `/api/auth/login`, and `/api/auth/me`, including Bearer-token protected access and role-aware user data. |
| Backend startup, H2 database, seed demo accounts | Task | enhancement | ac | Provide Kotlin + Spring Boot startup flow, H2 file database, seeded professional and subscriber accounts, and demo-ready data. |
| Subscriber food diary API | User story | feature | ac | Implement diary retrieval, creation, and deletion through `/api/diary`, with date filtering and meal fields for frontend integration. |
| Exercise logging API | User story | feature | ac | Implement `/api/exercise` GET, POST, and DELETE so subscribers can record activity alongside meal tracking. |
| Nutrition summary, feedback, trends, and status backend | User story | feature | ac | Support nutrition-related pages with summary, feedback, trend, and status data derived from diary records. |
| Recipe list, search, and recipe creation API | User story | feature | ac | Implement `/api/recipes`, `/api/recipes/search?q=...`, and recipe creation with ingredients, steps, calories, cost, and preparation time. |
| Recipe favourites API | User story | feature | ac | Implement favourite add, list, and remove behavior through `/api/favourites`. |
| Recipe ratings API | User story | feature | ac | Implement `/api/ratings` so users can submit or update recipe scores. |
| Recipe comments API | User story | feature | ac | Implement `/api/comments` with validation to reject blank comments and HTML tags. |
| Professional client list API | User story | feature | ac | Implement `/api/clients` so professionals can view assigned subscribers with diary count, last diary date, and status. |
| Client diary and exercise detail API | User story | feature | ac | Implement `/api/clients/{id}/diary` with meals, exercise, and summary data for assigned professional access only. |
| Professional-client messaging API | User story | feature | ac | Implement conversation retrieval and message sending through `/api/messages/{clientId}` and `/api/messages`. |
| Appointment API | User story | feature | ac | Implement `/api/appointments` list/create/update flow with appointment statuses: pending, confirmed, cancelled, completed. |
| Backend API handoff document | Documentation | enhancement | ac | Produce a bilingual handoff document explaining endpoints, request/response shapes, demo accounts, known limitations, and troubleshooting. |
| Landing page and authentication UI | User story | feature | chelin | Build NourishWell landing page plus login/register screens with role selection and validation-focused UI. |
| Subscriber dashboard and food diary UI | User story | feature | chelin | Build subscriber dashboard with record-today flow, daily meal slots, diary history, and nutrition dashboard presentation. |
| Recipe browsing, search, detail, rating, comments, and favourites UI | User story | feature | chelin | Build recipe cards, ingredient search, filters, detail modal/page, saved recipes, star ratings, and community comments interactions. |
| Recipe comparison tool | User story | enhancement | chelin | Build side-by-side recipe comparison for saved recipes so users can compare nutrition values. |
| Meal planner UI | User story | enhancement | chelin | Build calorie-budget meal planning interface with recipe suggestions and locked slots. |
| Subscriber onboarding and profile setup | User story | enhancement | chelin | Build first-login welcome modal, BMI/BMR/TDEE calculation, macro targets, exercise preferences, and dashboard sync. |
| Guided tour and help pages | User story | enhancement | chelin | Build subscriber/pro guided tour and Q&A/help pages for onboarding and support. |
| Community hub frontend | User story | enhancement | chelin | Build subscriber and professional community areas with posts, replies, client-related questions, and bind request UI. |
| Professional dashboard frontend | User story | feature | chelin | Build professional overview, client sidebar, client profile, analytics, messages, meal/training plan, appointment, and notes UI. |
| Client search UI | User story | enhancement | chelin | Add real-time client search in the professional sidebar with clear no-match behavior. |
| Client notes UI | User story | enhancement | chelin | Add editable private notes area inside the professional client profile. |
| Client history timeline UI | User story | enhancement | chelin | Add chronological event/history display for client progress and session records. |
| Accessibility and responsive frontend pass | Task | enhancement | chelin | Improve ARIA labels, keyboard navigation, responsive behavior, dark mode support, focus states, and readable layouts. |
| Project documentation: personas, user stories, meeting notes, retrospectives | Documentation | enhancement | chelin | Maintain project evidence including personas, user story versions, development requirements, meeting minutes, and sprint retrospectives. |

## Review

| Card | Type | Label | Assignee | Summary |
|---|---|---|---|---|
| Replace remaining localStorage flows with backend fetch calls | Task | enhancement | chelin | Use the backend handoff to connect diary, exercise, recipes, favourites, ratings, comments, clients, messages, and appointments to `/api/...` endpoints. |
| Final demo flow verification | Task | enhancement | chelin | Verify the demo path: subscriber login, diary/nutrition/exercise, recipe interactions, professional login, client dashboard, messaging, and appointment creation. |
| Frontend/backend authorization behavior review | Task | bug | ac | Check protected APIs return correct 401/403 behavior and that only assigned professionals can access client data. |

## In Progress

| Card | Type | Label | Assignee | Summary |
|---|---|---|---|---|
| GitHub Projects setup for user stories and bug tracking | Task | enhancement | chelin | Create project board with Backlog, In Progress, Review, and Done columns, then add cards with labels and assignees based on this file. |

## Backlog

| Card | Type | Label | Assignee | Summary |
|---|---|---|---|---|
| Fix placeholder carbs, fat, and time values in diary response | Bug | bug | ac | `Backend_API_Handoff 1.docx` notes that diary responses still expose placeholder values for carbs, fat, and time. |
| Add delete endpoint for comments | User story | enhancement | ac | Add an API endpoint for deleting recipe comments and connect it to frontend moderation/removal behavior. |
| Add delete endpoint for ratings | User story | enhancement | ac | Add an API endpoint for deleting/resetting recipe ratings if users need to undo a submitted rating. |
| Add automated tests and CI | Task | enhancement | ac | Add backend/API test coverage and CI workflow for auth, diary, recipes, professional access, messages, and appointments. |
| Improve empty-state validation for diary and nutrition pages | Bug | bug | chelin | Ensure empty diary, insufficient trend data, and no logged meals show useful states instead of misleading or blank UI. |
| Validate appointment conflict handling | Bug | bug | ac | Confirm overlapping professional appointments are rejected instead of creating duplicates. |
| Validate maximum recipe comparison limit | Bug | bug | chelin | Confirm users cannot compare more than four recipes and receive clear feedback when they try. |
| Validate allergen and tag filter empty results | Bug | bug | chelin | Ensure recipe allergen/tag filters show a clear no-results message when no recipes match. |

## Copy-Paste Issue Template

Use this structure when creating each GitHub issue/card:

```md
## User Story / Task
As a [user/persona], I want [capability], so that [benefit].

## Acceptance Criteria
- [ ] Given ..., when ..., then ...
- [ ] Given ..., when ..., then ...

## Labels
feature / bug / enhancement

## Assignee
ac / chelin

## Project Status
Backlog / In Progress / Review / Done
```
