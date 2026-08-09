# White-Label HRMS for Dubai Resale

A complete, production-ready HR Management System built on Apache Grails (Groovy) with multi-company support, ready for white-label licensing and resale to corporate clients in Dubai and the wider GCC region.

## Features

### Core Modules
- **Employee Management** - Full employee lifecycle from hire to termination, with UAE-specific fields (Emirates ID, Visa, Sponsor, etc.)
- **Payroll Processing** - Salary components, payslip generation, UAE statutory deductions (PF, Insurance, VAT)
- **Attendance & Time-Tracking** - Check-in/out, working hours calculation, late/early detection
- **Leave Management** - UAE-compliant leave types (Annual, Sick, Maternity, Hajj, etc.)
- **Expense Tracking** - Multi-stage approval workflow with payment processing
- **Recruitment & Onboarding** - Job postings, candidate management, automated onboarding tasks

### Multi-Company Support
- Complete data isolation between companies
- Per-company branding (colors, logo, timezone)
- Company-specific settings and configurations
- White-label ready: customize brand for each client

### Dubai/UAE Compliance
- AED currency default
- 5% VAT support
- UAE working hours (Mon-Fri, 9AM-6PM)
- Emirates ID, Visa, Sponsor management
- EOSB (End of Service Benefits) calculations
- UAE leave types and entitlements
- Arabic language support ready

## Tech Stack
- **Framework**: Apache Grails 6.x (Groovy)
- **Database**: H2 (dev/test), PostgreSQL/MySQL (production)
- **Build**: Gradle
- **JDK**: Java 17+
- **Runtime**: Embedded Tomcat

## Getting Started

### Prerequisites
- JDK 17 or higher
- Gradle 8.x

### Development
```bash
# Clone and build
./gradlew run
```

Visit `http://localhost:8080` to access the application.

### Default Credentials
- Username: `admin`
- Password: `admin123`

### Production Build
```bash
# Build WAR for deployment
./gradlew war

# Or build executable JAR
./gradlew bootWar
```

## API Endpoints

### Authentication
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/v1/auth/login` | User login |
| POST | `/api/v1/auth/logout` | User logout |
| POST | `/api/v1/auth/register` | User registration |
| GET | `/api/v1/auth/profile` | Get current user profile |

### Companies
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/v1/companies` | List companies |
| POST | `/api/v1/companies` | Create company |
| GET | `/api/v1/companies/{id}` | Get company |
| PUT | `/api/v1/companies/{id}` | Update company |
| DELETE | `/api/v1/companies/{id}` | Delete company |
| GET | `/api/v1/companies/{id}/settings` | Get company settings |

### Employees
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/v1/companies/{companyId}/employees` | List employees |
| POST | `/api/v1/companies/{companyId}/employees` | Create employee |
| GET | `/api/v1/companies/{companyId}/employees/{id}` | Get employee |
| PUT | `/api/v1/companies/{companyId}/employees/{id}` | Update employee |
| POST | `/api/v1/companies/{companyId}/employees/{id}/terminate` | Terminate employee |
| POST | `/api/v1/companies/{companyId}/employees/{id}/rehire` | Rehire employee |

### Departments
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/v1/companies/{companyId}/departments` | List departments |
| POST | `/api/v1/companies/{companyId}/departments` | Create department |
| PUT | `/api/v1/companies/{companyId}/departments/{id}` | Update department |

### Attendance
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/v1/companies/{companyId}/attendance/check-in/{employeeId}` | Check in |
| POST | `/api/v1/companies/{companyId}/attendance/check-out/{employeeId}` | Check out |
| GET | `/api/v1/companies/{companyId}/attendance/monthly/{year}/{month}` | Monthly attendance |

### Payroll
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/v1/companies/{companyId}/payroll/components` | List salary components |
| POST | `/api/v1/companies/{companyId}/payroll/components` | Create component |
| GET | `/api/v1/companies/{companyId}/payroll/payslips` | List payslips |
| POST | `/api/v1/companies/{companyId}/payroll/payslips/generate/{employeeId}/{year}/{month}` | Generate payslip |
| POST | `/api/v1/companies/{companyId}/payroll/payslips/{id}/approve` | Approve payslip |
| POST | `/api/v1/companies/{companyId}/payroll/payslips/{id}/reject` | Reject payslip |

### Expenses
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/v1/companies/{companyId}/expenses` | List expenses |
| POST | `/api/v1/companies/{companyId}/expenses` | Create expense |
| POST | `/api/v1/companies/{companyId}/expenses/{id}/approve` | Approve expense |
| POST | `/api/v1/companies/{companyId}/expenses/{id}/reject` | Reject expense |
| POST | `/api/v1/companies/{companyId}/expenses/{id}/pay` | Process payment |

### Recruitment
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/v1/companies/{companyId}/recruitment/job-postings` | List job postings |
| POST | `/api/v1/companies/{companyId}/recruitment/job-postings` | Create job posting |
| GET | `/api/v1/companies/{companyId}/recruitment/job-postings/{id}` | Get job posting |
| PUT | `/api/v1/companies/{companyId}/recruitment/job-postings/{id}` | Update job posting |
| POST | `/api/v1/companies/{companyId}/recruitment/candidates/{candidateId}/hire` | Hire candidate |

## White-Label Customization

Each company can customize their branding:
- Company name and logo
- Primary and secondary colors
- Timezone (default: Asia/Dubai)
- Currency (default: AED)
- Language (default: English)
- Working hours and days
- VAT rate (default: 5% for UAE)

## Database Schema

### Core Entities
- `company` - Organization data with white-label settings
- `department` - Organizational structure
- `designation` - Job titles within departments
- `employee` - Employee records (UAE-specific fields)
- `attendance_record` - Daily attendance tracking
- `attendance_log` - Check-in/out logs
- `leave_application` - Leave requests
- `leave_type` - Leave categories
- `salary_component` - Pay components (earnings/deductions)
- `payslip` - Monthly payslips
- `payslip_component` - Payslip line items
- `expense` - Expense claims
- `job_posting` - Open positions
- `candidate` - Job applicants
- `onboarding_task` - New hire tasks
- `user` - System users
- `authority` - Role definitions
- `user_authority` - User-role mappings
- `company_setting` - Per-company configuration

## Deployment

### Docker
```bash
docker build -t hrms .
docker run -p 8080:8080 hrms
```

### JAR Deployment
```bash
java -jar build/libs/hrms-*.war
```

### Configuration for Production
Update `grails-app/conf/application.yml`:
```yaml
dataSource:
    driverClassName: org.postgresql.Driver
    url: jdbc:postgresql://localhost:5432/hrms
    username: hrms_user
    password: ${DB_PASSWORD}
```

## License

This software is available for white-label licensing. Contact for reseller agreements and licensing terms.