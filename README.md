# NourishWell Wiki

## Overview

NourishWell is a web-based healthy eating application developed for the COMP2850 Software Engineering group project. The system is designed to help users record meals, understand calorie intake, search for healthy recipes, and receive useful food or meal suggestions.

The project is not only a simple calorie calculator. It is designed as a more complete healthy eating tool. A normal subscriber can use the system to track daily meals, view nutrition information, search for recipes, and manage personal health-related goals. A professional user can support clients through plans, messages, appointments, progress monitoring, and other professional tools.

This Wiki records the main documentation for the project, including planning work, user research, design decisions, meeting notes, testing strategy, diagrams, and development requirements.


## Team Members

Our team has four members:

- Che Lin
- Tengchuan Jiang
- Chin Pang Chan
- Baiyi He

In practice, the work was divided into front-end and back-end areas. Two members mainly focused on the user-facing pages, layout, interface design, and front-end behaviour, while the other two mainly worked on the back-end logic, database, and API development.

Although the responsibilities were divided, the two sides still needed to stay connected. The front-end pages depended on the data and API routes provided by the back-end, while the back-end functions needed to support the user journeys shown in the interface.

## Main Features

The main features of NourishWell include:

- Landing page introducing the system
- User login and registration
- Subscriber and professional role selection
- Food diary and meal logging
- Calorie and nutrition tracking
- Recipe browsing and searching
- Recipe favourites, ratings and comments
- Exercise tracking
- Meal and exercise plan management
- Professional dashboard for client monitoring
- Messages and notifications
- Appointment-related features
- Dark mode and accessibility-related improvements

These features were chosen because they support the main user journeys identified during the early planning stage.

## Repository and Technologies

The project uses HTML, CSS and JavaScript for the front-end interface. Kotlin and Spring Boot are used for the back-end server and API routes. H2 is used as the development database. Chart.js is used for chart-based visual summaries, and authentication is supported using JWT and BCrypt.

GitHub was used for version control, code review and project documentation. The Wiki stores planning and design documentation, while the GitHub Project Board was used to track user stories, tasks, issues and bugs during development.

The application can be run locally with:

```bash
cd backend/goodfood
./gradlew bootRun