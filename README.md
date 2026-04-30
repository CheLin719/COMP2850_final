# NourishWell

NourishWell is a healthy eating and nutrition tracking web application for subscribers and health professionals.

## Features

- User registration and login with role-based access
- Subscriber food diary and daily meal tracking
- Nutrition summaries, feedback, trends, and status indicators
- Exercise logging and daily insight cards
- Recipe browsing, search, filtering, favourites, ratings, and comments
- Recipe comparison and meal planning tools
- Professional dashboard for client monitoring
- Client analytics, notes, meal plans, messages, and appointments
- Community hub and professional-client connection workflow
- Guided tours, help pages, responsive design, and accessibility improvements

## Tech Stack

- Backend: Kotlin + Spring Boot
- Frontend: HTML/CSS/JavaScript
- Database: H2 (development)
- Charts: Chart.js
- Authentication: JWT + BCrypt

## Getting Started

### Prerequisites

- JDK 17+
- Gradle

### Running the application

1. Clone the repository
2. `./gradlew bootRun`
3. Open `http://localhost:4040`

### Running tests

```bash
./gradlew test
```

## Project Structure

- `backend/goodfood/` - Kotlin Spring Boot application, API controllers, services, repositories, static frontend pages, and Gradle files
- `backend/goodfood/src/main/kotlin/` - backend source code organized by feature modules
- `backend/goodfood/src/main/resources/static/` - HTML, CSS, and JavaScript frontend assets
- `backend/goodfood/src/main/resources/` - application configuration
- `backend/goodfood/src/test/` - backend test code
- `data/` - development H2 database files
- `docs/` - personas, user stories, meeting notes, retrospectives, and project planning documents

## Team

- Che Lin
- Tengchuan Jiang
- Chin Pang Chan
- Baiyi He
