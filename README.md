# NourishWell - Healthy Eating Web Application

**COMP2850 Software Engineering Group Project**  
**University of Leeds, School of Computing**  
**Submission Date:** 8 May 2026

---

## Team Members

- **Che Lin** - Frontend development, documentation, design, and project coordination，Wiki uploads
- **Tengchuan Jiang** - Frontend testing, Wiki uploads
- **Chin Pang Chan** - Backend development，Backend Testing
- **Baiyi He** - Backend development

---

## Project Overview

NourishWell is a web-based healthy eating application designed to help users:
- Record daily meals and track nutrition intake
- Search for healthy recipes and save favourites
- Plan weekly meals and exercise routines
- Connect with professional nutritionists for personalized advice
- Participate in a community forum for peer support

The system supports two user types:
1. **Subscribers** - Track meals, discover recipes, and manage health goals
2. **Professionals** - Monitor clients, provide advice, and manage appointments

---

## Technologies

**Frontend:**
- HTML, CSS, JavaScript
- Chart.js for data visualization

**Backend:**
- Kotlin with Spring Boot
- H2 Database (development)
- JWT authentication with BCrypt password hashing

**Development Tools:**
- GitHub for version control
- GitHub Codespaces for development environment
- GitHub Actions for CI/CD

---

## Running Instructions

### Prerequisites

- **Java 17 or higher** (for backend)
- **Python 3** (for frontend local server, optional)
- **Git** (to clone the repository)

### Option 1: Running on GitHub Codespaces (Recommended)

1. Open the repository in GitHub Codespaces
2. The environment will automatically configure

**Start the backend:**
```bash
cd backend/goodfood
./gradlew bootRun
```

The backend API will be available at: `http://localhost:8080`

**Start the frontend:**
```bash
cd frontend
python3 -m http.server 3000
```

The frontend will be available at: `http://localhost:3000`

---

### Option 2: Running Locally

**Clone the repository:**
```bash
git clone https://github.com/yoen-dev/COMP2850_final.git
cd COMP2850_final
```

**Start the backend:**
```bash
cd backend/goodfood
./gradlew bootRun
```

**Start the frontend:**

Open `frontend/index.html` in a web browser, or run a local server:

```bash
cd frontend
python3 -m http.server 3000
```

Then open: `http://localhost:3000`

---

## Demo Accounts

**Subscriber Account:**
- Email: `alice@example.com`
- Password: `Abcdef1!`

**Professional Account:**
- Email: `dr.rivera@example.com`
- Password: `Abcdef1!`

---

## Main Features

### Core Features
- Landing page with system introduction
- User registration and login (Subscriber/Professional roles)
- Food diary and meal logging
- Calorie and nutrition tracking
- Recipe browsing, search, and favourites
- Exercise tracking
- Weekly meal and exercise plan management

### Professional Features
- Professional dashboard for client monitoring
- Client health analytics with charts
- Messaging system between professionals and clients
- Appointment management
- Fitness plan creation for clients

### Extended Features
- Community Hub with feed, Q&A, and professional directory
- Subscriber-professional binding system
- CSV data export for diary tracking
- Developer changelog (Dev Log)
- Dark mode support
- WCAG 2.1 AA accessibility compliance

---

## Project Structure

```
COMP2850_final/
├── frontend/
│   ├── index.html              # Landing page and authentication
│   ├── dashboard.html          # Subscriber dashboard
│   ├── pro_dashboard.html      # Professional dashboard
│   ├── styles/
│   │   └── main.css
│   └── scripts/
│       └── main.js
├── backend/
│   └── goodfood/
│       ├── src/
│       │   ├── main/
│       │   │   ├── kotlin/
│       │   │   │   └── com/goodfood/
│       │   │   │       ├── controller/
│       │   │   │       ├── service/
│       │   │   │       ├── repository/
│       │   │   │       └── model/
│       │   │   └── resources/
│       │   │       └── application.properties
│       │   └── test/
│       ├── build.gradle.kts
│       └── gradlew
└── README.md
```

---

## API Documentation

Full API documentation is available in the GitHub Wiki:  
[Backend API Documentation](https://github.com/yoen-dev/COMP2850_final/wiki/Backend-API-Documentation)

**Key Endpoints:**
- `POST /api/auth/register` - User registration
- `POST /api/auth/login` - User authentication
- `GET /api/auth/me` - Get current user info
- `GET /api/diary` - Get food diary entries
- `GET /api/recipes` - Search recipes
- `GET /api/clients` - Get professional's client list

---

## Documentation

Complete project documentation is available in the GitHub Wiki:

- [Personas v1/v2/v3/v4](https://github.com/yoen-dev/COMP2850_final/wiki/Personas-v4)
- [User Stories v1/v2/v3/v4](https://github.com/yoen-dev/COMP2850_final/wiki/User-Stories-v4)
- [Design Specification](https://github.com/yoen-dev/COMP2850_final/wiki/Design-Specification)
- [Wireframes and UI Changes](https://github.com/yoen-dev/COMP2850_final/wiki/Wireframes-and-UI-Changes)
- [Testing Strategy](https://github.com/yoen-dev/COMP2850_final/wiki/Testing-Strategy)
- [Meeting Notes](https://github.com/yoen-dev/COMP2850_final/wiki/Meeting-Notes)
- [Retrospectives](https://github.com/yoen-dev/COMP2850_final/wiki/Retrospectives)

---

## Testing

**Backend Testing:**
```bash
cd backend/goodfood
./gradlew test
```

**Frontend Testing:**
- Accessibility testing with WAVE and Lighthouse
- End-to-end testing with Playwright
- Manual UX testing (4 test sessions recorded in Wiki)

Test documentation: [Testing Strategy](https://github.com/yoen-dev/COMP2850_final/wiki/Testing-Strategy)


---

## Deployment

**Live Demo:**
- Frontend: [https://yoen-dev.github.io/COMP2850_final/](https://yoen-dev.github.io/COMP2850_final/)
- Backend: Deployed on GitHub Codespaces (port 8080)

---

## License

This project is developed for academic purposes as part of COMP2850 Software Engineering module at the University of Leeds.

---

## Contact

For questions or issues, please contact the team through the GitHub repository or University of Leeds email.

**Repository:** https://github.com/yoen-dev/COMP2850_final  
**Wiki:** https://github.com/yoen-dev/COMP2850_final/wiki