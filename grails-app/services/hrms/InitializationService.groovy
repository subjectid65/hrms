package hrms

import org.springframework.beans.factory.InitializingBean
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.util.Date

@Service
class InitializationService implements InitializingBean {

    @Override
    void afterPropertiesSet() {
        seedData()
    }

    private void seedData() {
        // Seed default company if none exists
        if (Company.count() == 0) {
            Company defaultCompany = new Company(
                companyName: 'Demo Company LLC',
                companyCode: 'DEMO',
                companyShortName: 'Demo Co',
                legalName: 'Demo Company Limited Liability Company',
                tradeLicenseNumber: 'TL-123456',
                vatRegistrationNumber: '100200300400500',
                crNumber: 'CR-654321',
                address: 'Dubai Internet City',
                city: 'Dubai',
                state: 'Dubai',
                country: 'United Arab Emirates',
                postalCode: '12345',
                phoneNumber: '+971-4-123-4567',
                email: 'admin@democompany.ae',
                website: 'https://www.democompany.ae',
                currencyCode: 'AED',
                languageCode: 'en',
                timezone: 'Asia/Dubai',
                isActive: true,
                createdBy: null as Long
            )
            defaultCompany.save(flush: true)

            // Initialize default settings
            CompanySettingService settingService = new CompanySettingService()
            settingService.initializeDefaultSettings(defaultCompany.id)

            // Seed departments
            seedDepartments(defaultCompany)

            // Seed salary components for UAE
            seedSalaryComponents(defaultCompany)

            // Seed leave types for UAE
            seedLeaveTypes(defaultCompany)

            // Seed admin user
            seedAdminUser(defaultCompany)

            // Seed sample employees
            seedSampleEmployees(defaultCompany)
        }
    }

    private void seedDepartments(Company company) {
        def departments = [
            [name: 'Human Resources', code: 'HR', description: 'HR Department'],
            [name: 'Finance', code: 'FIN', description: 'Finance & Accounting'],
            [name: 'IT', code: 'IT', description: 'Information Technology'],
            [name: 'Sales', code: 'SAL', description: 'Sales & Business Development'],
            [name: 'Operations', code: 'OPS', description: 'Operations'],
            [name: 'Marketing', code: 'MKT', description: 'Marketing & Communications'],
            [name: 'Legal', code: 'LEG', description: 'Legal & Compliance'],
            [name: 'Admin', code: 'ADM', description: 'Administration']
        ]
        for (def dept : departments) {
            if (!Department.existsByCompanyAndCode(company, dept.code)) {
                Department d = new Department(
                    name: dept.name,
                    code: dept.code,
                    description: dept.description,
                    company: company,
                    isActive: true,
                    createdBy: null as Long
                )
                d.save(flush: false)
            }
        }
    }

    private void seedSalaryComponents(Company company) {
        def components = [
            [name: 'Basic Salary', code: 'BASIC_SALARY', componentType: 'EARNING', defaultValue: null, isStatutory: false],
            [name: 'Housing Allowance', code: 'HOUSING_ALLOWANCE', componentType: 'EARNING', defaultValue: null, isStatutory: false],
            [name: 'Transport Allowance', code: 'TRANSPORT_ALLOWANCE', componentType: 'EARNING', defaultValue: null, isStatutory: false],
            [name: 'Medical Allowance', code: 'MEDICAL_ALLOWANCE', componentType: 'EARNING', defaultValue: null, isStatutory: false],
            [name: 'Mobile Allowance', code: 'MOBILE_ALLOWANCE', componentType: 'EARNING', defaultValue: 500.0, isStatutory: false],
            [name: 'Education Allowance', code: 'EDUCATION_ALLOWANCE', componentType: 'EARNING', defaultValue: null, isStatutory: false],
            [name: 'Overtime', code: 'OVERTIME', componentType: 'EARNING', defaultValue: null, isStatutory: false],
            [name: 'Bonus', code: 'BONUS', componentType: 'EARNING', defaultValue: null, isStatutory: false],
            [name: 'Provident Fund (PF)', code: 'PROVIDENT_FUND', componentType: 'DEDUCTION', defaultValue: null, isStatutory: true],
            [name: 'Group Insurance (GI)', code: 'GROUP_INSURANCE', componentType: 'DEDUCTION', defaultValue: null, isStatutory: true],
            [name: 'Third Party Insurance', code: 'THIRD_PARTY_INSURANCE', componentType: 'DEDUCTION', defaultValue: null, isStatutory: true],
            [name: 'Income Tax (if applicable)', code: 'INCOME_TAX', componentType: 'DEDUCTION', defaultValue: null, isStatutory: true],
            [name: 'VAT (5%)', code: 'VAT', componentType: 'DEDUCTION', defaultValue: null, isStatutory: true],
            [name: 'Absences Deduction', code: 'ABSENCES_DEDUCTION', componentType: 'DEDUCTION', defaultValue: null, isStatutory: false],
            [name: 'Late Coming Deduction', code: 'LATE_COMING_DEDUCTION', componentType: 'DEDUCTION', defaultValue: null, isStatutory: false]
        ]
        for (def comp : components) {
            if (!SalaryComponent.existsByCompanyAndCode(company, comp.code)) {
                SalaryComponent s = new SalaryComponent(
                    name: comp.name,
                    code: comp.code,
                    componentType: comp.componentType,
                    company: company,
                    defaultValue: comp.defaultValue,
                    isStatutory: comp.isStatutory,
                    isActive: true,
                    createdBy: null as Long
                )
                s.save(flush: false)
            }
        }
    }

    private void seedLeaveTypes(Company company) {
        def leaveTypes = [
            [name: 'Annual Leave', code: 'ANNUAL', maxDaysPerYear: 30, isPaid: true, accrues: true, accrualRate: 2.5],
            [name: 'Sick Leave', code: 'SICK', maxDaysPerYear: 90, isPaid: true, accrues: false],
            [name: 'Maternity Leave', code: 'MATERNITY', maxDaysPerYear: 45, isPaid: true, accrues: false],
            [name: 'Paternity Leave', code: 'PATERNITY', maxDaysPerYear: 5, isPaid: true, accrues: false],
            [name: 'Public Holiday', code: 'PUBLIC', maxDaysPerYear: null, isPaid: true, accrues: false],
            [name: 'Unpaid Leave', code: 'UNPAID', maxDaysPerYear: null, isPaid: false, accrues: false],
            [name: 'Emergency Leave', code: 'EMERGENCY', maxDaysPerYear: 3, isPaid: false, accrues: false],
            [name: ' Hajj Leave', code: 'HAJJ', maxDaysPerYear: 30, isPaid: false, accrues: false],
            [name: 'Education Leave', code: 'EDUCATION', maxDaysPerYear: 10, isPaid: false, accrues: false],
            [name: 'Garden Leave', code: 'GARDEN', maxDaysPerYear: 30, isPaid: true, accrues: false]
        ]
        for (def lt : leaveTypes) {
            if (!LeaveType.existsByCompanyAndCode(company, lt.code)) {
                LeaveType l = new LeaveType(
                    name: lt.name,
                    code: lt.code,
                    maxDaysPerYear: lt.maxDaysPerYear,
                    isPaid: lt.isPaid,
                    requiresApproval: lt.code != 'PUBLIC' && lt.code != 'UNPAID',
                    accrues: lt.accrues ?: false,
                    accrualRate: lt.accrualRate,
                    company: company,
                    isActive: true,
                    createdBy: null as Long
                )
                l.save(flush: false)
            }
        }
    }

    private void seedAdminUser(Company company) {
        if (!User.existsByUsername('admin')) {
            User admin = new User(
                username: 'admin',
                password: 'admin123',
                firstName: 'Admin',
                lastName: 'User',
                email: 'admin@democompany.ae',
                phone: '+971-50-123-4567',
                company: company,
                isAdmin: true,
                enabled: true,
                createdBy: null as Long
            )
            admin.save(flush: true, failOnError: true)

            Authority userAuth = Authority.findOrCreateByAuthority('ROLE_USER')
            userAuth.save(flush: false)
            UserAuthority ua = new UserAuthority(user: admin, authority: userAuth)
            ua.save(flush: true, failOnError: true)
        }
    }

    private void seedSampleEmployees(Company company) {
        if (Employee.count() > 0) return

        def employees = [
            [firstName: 'Ahmed', lastName: 'Al Maktoum', email: 'ahmed@demo.ae', phone: '+971-50-111-1111', jobTitle: 'CEO', department: 'ADM', salary: 50000.00],
            [firstName: 'Mohammed', lastName: 'Hassan', email: 'mohammed@demo.ae', phone: '+971-50-222-2222', jobTitle: 'HR Manager', department: 'HR', salary: 25000.00],
            [firstName: 'Fatima', lastName: 'Ahmed', email: 'fatima@demo.ae', phone: '+971-50-333-3333', jobTitle: 'Finance Manager', department: 'FIN', salary: 28000.00],
            [firstName: 'Ali', lastName: 'Khan', email: 'ali@demo.ae', phone: '+971-50-444-4444', jobTitle: 'IT Lead', department: 'IT', salary: 22000.00],
            [firstName: 'Sara', lastName: 'Mohamed', email: 'sara@demo.ae', phone: '+971-50-555-5555', jobTitle: 'Sales Executive', department: 'SAL', salary: 15000.00],
            [firstName: 'Omar', lastName: 'Said', email: 'omar@demo.ae', phone: '+971-50-666-6666', jobTitle: 'Marketing Manager', department: 'MKT', salary: 20000.00],
            [firstName: 'Layla', lastName: 'Rashid', email: 'layla@demo.ae', phone: '+971-50-777-7777', jobTitle: 'Legal Advisor', department: 'LEG', salary: 25000.00],
            [firstName: 'Khalid', lastName: 'Ahmad', email: 'khalid@demo.ae', phone: '+971-50-888-8888', jobTitle: 'Operations Manager', department: 'OPS', salary: 22000.00]
        ]

        for (int i = 0; i < employees.size(); i++) {
            def emp = employees[i]
            def dept = Department.findByCompanyAndCode(company, emp.department)
            if (dept) {
                Employee e = new Employee(
                    employeeCode: "EMP${String.format('%04d', i + 1)}",
                    firstName: emp.firstName,
                    lastName: emp.lastName,
                    email: emp.email,
                    primaryPhone: emp.phone,
                    jobTitle: emp.jobTitle,
                    employmentType: 'FULL_TIME',
                    nationality: 'UAE',
                    emiratesId: "784-${(100000000000L + i * 111111111L).toString()}",
                    joiningDate: Date.valueOf(LocalDate.of(2024, 1, 15).plusMonths(i)),
                    salary: emp.salary,
                    isActive: true,
                    company: company,
                    department: dept,
                    createdBy: null as Long
                )
                e.save(flush: false)
            }
        }
    }
}