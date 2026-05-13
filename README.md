# Student Dormitory Management System

A web application for managing student dormitory registration and check-in processes.

## Project Structure

```
StuDormitoryManagementSystem/
├── backend/
│   └── dormitory_backend/          (Spring Boot REST API)
│       ├── src/
│       │   ├── main/
│       │   │   ├── java/           (Backend code)
│       │   │   └── resources/      (Configuration)
│       │   └── test/
│       ├── pom.xml
│       └── mvnw
│
├── frontend/
│   └── dormitory_client/           (React + Vite)
│       ├── src/
│       │   ├── pages/              (React pages)
│       │   ├── components/         (React components)
│       │   ├── api/                (API clients)
│       │   └── routes/             (React Router)
│       ├── package.json
│       └── vite.config.js
│
└── database/                       (Database scripts)
```

## Getting Started

### Prerequisites

- Java 17+
- Node.js 16+
- PostgreSQL 12+
- npm

### Backend Setup

1. Navigate to backend directory:
   ```bash
   cd backend/dormitory_backend
   ```

2. Start the server:
   ```bash
   mvnw spring-boot:run
   ```

3. Server will run on: `http://localhost:8080/api`

### Frontend Setup

1. Navigate to frontend directory:
   ```bash
   cd frontend/dormitory_client
   ```

2. Install dependencies:
   ```bash
   npm install
   ```

3. Start development server:
   ```bash
   npm run dev
   ```

4. Open in browser: `http://localhost:5174`

## Database Configuration

Edit `backend/dormitory_backend/src/main/resources/application.yml`:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5434/dormitory_db
    username: postgres
    password: 19052004
```

## API Endpoints

### User Endpoints
- POST `/api/applications` - Register new application
- GET `/api/applications/status` - Get application status
- PUT `/api/applications/{id}/checkin` - Check in
- POST `/api/documents` - Upload document
- GET `/api/periods` - Get registration periods

### Admin Endpoints
- PUT `/api/applications/{id}/validate` - Validate application
- PUT `/api/applications/{id}/approve` - Approve application
- PUT `/api/applications/{id}/assign-bed` - Assign bed
- GET `/api/applications/admin/by-status` - Get applications by status

## Application States

1. PENDING - Initial state after registration
2. IN_REVIEW - Admin is reviewing
3. VALID - Application passed validation
4. INVALID - Application failed validation (can resubmit)
5. APPROVED - Approved and assigned bed
6. WAITING - Waiting for available bed
7. CHECKED_IN - Student checked in
8. REJECTED - Rejected after check-in
9. EXPIRED - Deadline passed without check-in

## Key Features

- Student registration with CCCD (ID card number)
- Document upload and verification
- Admin validation and approval workflow
- Bed assignment management
- Waiting list management
- Automatic expiration after 3-day deadline
- User account creation upon check-in

## Technology Stack

### Backend
- Spring Boot 3.3.0
- Spring Data JPA
- PostgreSQL
- Maven

### Frontend
- React 18+
- Vite
- Material-UI
- Axios

## Testing

### API Testing
Use curl or Postman to test endpoints:

```bash
# Test backend
curl http://localhost:8080/api/test

# Get periods
curl http://localhost:8080/api/periods

# Register application
curl -X POST http://localhost:8080/api/applications \
  -H "Content-Type: application/json" \
  -d '{"cccd":"123456789012","periodId":1}'
```

### Sample Test Data

Sample data is loaded from `backend/dormitory_backend/src/main/resources/data.sql`:
- 3 Registration periods (2 active, 1 inactive)
- 9 Eligible students
- Sample CCCD: 123456789012-123456789020

## Project Status

- Backend: 85% complete (core logic implemented)
- Frontend: 20% complete (registration page done)
- Database: 100% ready

## Next Steps

1. Build admin dashboard pages
2. Implement authentication and authorization
3. Add comprehensive testing
4. Deploy to production

## Development Notes

- Backend serves on port 8080 with context path `/api`
- Frontend serves on port 5174 (can be changed in vite.config.js)
- CORS is configured to allow localhost:5173, 5174
- Database auto-creates tables on startup (ddl-auto: update)

## Authors

Student Dormitory Management System Team

## License

Private Project

