# CareerBridge

CareerBridge is a Java and JavaFX desktop application for connecting job seekers with companies. The project was built for an SDA course and follows a layered architecture with separate packages for user interface, controllers, services, data access, domain models, interfaces, and utilities.

## Project Overview

CareerBridge helps job seekers browse job and internship opportunities, create profiles, view match scores, filter listings, and apply for platform posted offerings. Companies can register, post openings, view applicants, rank candidates, update application status, and view basic recruitment analytics. Admin users can approve or reject companies, deactivate users, and view trending skills.

## Main Features

- Role based login and registration for job seekers, companies, and admins
- JavaFX screens for dashboards, profile setup, job browsing, company posting, candidate viewing, and admin management
- MySQL database integration for users, companies, job seekers, profiles, offerings, applications, and trending skills
- Job and internship listing support from platform postings and scraped dataset or feed sources
- Filtering by location, CGPA, stipend, experience, and role
- AI based match scoring between job seeker profiles and offerings
- Candidate ranking for company applicants using profile, CV, skills, education, and experience data
- Trending skill calculation from offering descriptions
- Company analytics such as total applications, average CGPA, match distribution, top skills, and applications by offering

## Architecture

The project uses a layered architecture.

### UI Layer

The UI layer is built with JavaFX and contains screens such as LoginScreen, RegisterScreen, JobSeekerDashboard, CompanyDashboard, AdminDashboard, ProfileSetupScreen, PostOfferingScreen, ViewCandidatesScreen, MyApplicationsScreen, ViewProfileScreen, FilterPanel, and TrendingSkillsScreen. These screens handle user interaction and call controllers for application logic.

### Controller Layer

The controller layer handles use case flow. AuthController manages login and registration. JobSeekerController handles profile, recommendations, applications, and match scores. CompanyController handles offerings, applicants, ranking, and analytics. AdminController handles company approvals, user management, and trending skills. OfferingController, ApplicationController, and JobFeedController coordinate offering, application, and feed related operations.

### Service Layer

The service layer contains business logic. AIMatchingService computes match scores, skill gaps, and ranking support. FilterService applies job filters. JobFeedService fetches and deduplicates external listings. TrendingSkillService extracts and saves trending skills. SmileOLSModel supports the matching model.

### DAO Layer

The DAO layer is responsible for database access. UserDAO, JobSeekerDAO, CompanyDAO, ProfileDAO, OfferingDAO, ApplicationDAO, and TrendingSkillDAO contain SQL queries, prepared statements, CRUD operations, and result set mapping. This keeps SQL out of the UI and business logic layers.

### Domain Layer

The domain layer contains core business classes such as User, Admin, Company, JobSeeker, Profile, Offering, Application, FilterCandidate, TrendingSkill, JobFeed, ModelTrainer, ApplicantRanker, and OfferingRanker.

### Interface Layer

The interface layer defines stable contracts such as IFeedFetchable, IRankable, IFilterable, IUserDAO, and IDashboard. These interfaces support polymorphism and reduce coupling between classes.

### Utility Layer

The utility layer contains shared infrastructure such as DatabaseConnection, SessionManager, RSSParser, JobAPIClient, dataset loaders, CVTextExtractor, AppPaths, and AnalyticsData.

## Design Patterns and Principles

CareerBridge applies several object oriented design patterns and principles.

### Singleton

DatabaseConnection, SessionManager, and AuthController provide controlled shared access through getInstance methods.

### DAO Pattern

All database operations are isolated in DAO classes. Controllers and services work with Java objects instead of writing SQL directly.

### Strategy

IFeedFetchable allows multiple feed sources such as RSSParser and JobAPIClient. IRankable allows ApplicantRanker and OfferingRanker to be used through one ranking contract. IFilterable allows filtering behavior to be used through a stable interface.

### Template Method

AbstractDatasetLoader defines the common dataset loading workflow. IndeedDatasetLoader and RozeeDatasetLoader implement the source specific field extraction details.

### Factory Like Creation

DatasetController uses a loader registry to select and create the correct dataset loader based on file prefix.

### Adapter Like Conversion

RSSParser, JobAPIClient, dataset loaders, and CVTextExtractor convert external formats into internal data that the rest of the system can use.

### Facade Like Controllers

Controllers act as simple interfaces for the UI. For example, JobSeekerController hides ProfileDAO, OfferingDAO, ApplicationDAO, and AIMatchingService behind simple methods such as getRecommendedOfferings and applyForOffering.

## Database

The project uses a MySQL database named careerbridge. The schema includes users, admins, job_seekers, profiles, companies, offerings, applications, trending_skills, and job_feeds. The database files are included in the src folder as database.sql and database.txt.txt.

## Important Use Cases

- Job seeker registration and login
- Company registration and admin approval
- Job seeker profile creation and update
- Browse, search, filter, and rank offerings
- Apply for platform posted offerings
- View and withdraw applications
- Company posts and manages offerings
- Company views and ranks candidates
- Company accepts or rejects applications
- Admin approves or rejects companies
- Admin deactivates users
- Trending skills are computed from job data

## Technologies Used

- Java
- JavaFX
- MySQL
- MySQL Connector J
- JSON processing library
- Apache PDFBox
- Smile based model support

## Project Structure

```text
src
  controller
  dao
  domain
  interfaces
  service
  ui
  utility
  datasets
lib
data
bin
out
```

## How to Run

1. Install Java and MySQL.
2. Create the careerbridge database using src/database.sql.
3. Update database credentials in src/utility/DatabaseConnection.java if needed.
4. Make sure the required jar files in lib are included in the classpath.
5. Run src/App.java or the required JavaFX screen from the IDE.

## Notes

The project is designed for demonstration and academic defense. The main focus is clean layered architecture, object oriented design, JavaFX interface implementation, MySQL persistence, and design pattern usage.
