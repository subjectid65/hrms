package hrms

import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.util.Date

@Transactional
class EmployeeService {

    def getCompanyById(Long id) {
        return Company.get(id)
    }

    def listCompanies(Map params = [:]) {
        def results = Company.findAll()
        if (params.isActive != null) results = results.findAll { it.isActive == params.isActive }
        if (params.search) {
            def s = params.search.toLowerCase()
            results = results.findAll { it.companyName?.toLowerCase().contains(s) || it.companyCode?.toLowerCase().contains(s) }
        }
        results = results.sort { it.companyName ?: '' }
        def offset = params.offset ? params.offset.toInteger() : 0
        def max = params.max ? params.max.toInteger() : 10
        return results.size() > offset ? results.subList(offset, Math.min(offset + max, results.size())) : []
    }

    def countCompanies(Map params = [:]) {
        def q = [:]
        if (params.isActive != null) q.isActive = params.isActive
        if (params.search) {
            def s = "%${params.search}%"
            return Company.findAll { companyName =~ ~/.*${params.search}.*/ || companyCode =~ ~/.*${params.search}.*/ }?.size() ?: 0
        }
        return Company.findAll(q)?.size() ?: 0
    }

    def createCompany(Map<String, Object> data, Long createdBy) {
        Company company = new Company(
            companyName: data.companyName,
            companyCode: data.companyCode,
            companyShortName: data.companyShortName,
            legalName: data.legalName,
            tradeLicenseNumber: data.tradeLicenseNumber,
            vatRegistrationNumber: data.vatRegistrationNumber,
            crNumber: data.crNumber,
            incorporationDate: data.incorporationDate,
            noOfEmployees: data.noOfEmployees,
            address: data.address,
            city: data.city,
            state: data.state,
            country: data.country,
            postalCode: data.postalCode,
            phoneNumber: data.phoneNumber,
            email: data.email,
            currencyCode: data.currencyCode ?: 'AED',
            languageCode: data.languageCode ?: 'en',
            timezone: data.timezone ?: 'Asia/Dubai',
            fiscalYearStart: data.fiscalYearStart,
            fiscalYearEnd: data.fiscalYearEnd,
            isActive: data.isActive != false,
            createdBy: createdBy
        )
        company.save(flush: true, failOnError: true)
        return company
    }

    def updateCompany(Long id, Map<String, Object> data, Long updatedBy) {
        Company company = Company.get(id)
        if (!company) {
            throw new NoSuchElementException("Company not found: ${id}")
        }
        company.companyName = data.companyName
        company.companyShortName = data.companyShortName
        company.legalName = data.legalName
        company.address = data.address
        company.city = data.city
        company.state = data.state
        company.country = data.country
        company.postalCode = data.postalCode
        company.phoneNumber = data.phoneNumber
        company.email = data.email
        company.currencyCode = data.currencyCode
        company.languageCode = data.languageCode
        company.timezone = data.timezone
        company.isActive = data.isActive != false
        company.updatedBy = updatedBy
        company.save(flush: true, failOnError: true)
        return company
    }

    def deleteCompany(Long id) {
        Company company = Company.get(id)
        if (!company) {
            throw new NoSuchElementException("Company not found: ${id}")
        }
        company.delete(flush: true)
    }

    def listEmployees(Long companyId, Map params = [:]) {
        def company = Company.findById(companyId)
        if (!company) {
            def all = Company.findAll()
            if (all.isEmpty()) return []
            company = all.get(0)
        }
        def results = Employee.findAllByCompany(company)
        if (params.search) {
            results = results.findAll { it.firstName?.toLowerCase().contains(params.search) || it.lastName?.toLowerCase().contains(params.search) || it.employeeCode?.toLowerCase().contains(params.search) }
        }
        if (params.departmentId) results = results.findAll { it.department?.id == params.departmentId as Long }
        if (params.designationId) results = results.findAll { it.designation?.id == params.designationId as Long }
        if (params.isActive != null) results = results.findAll { it.isActive == params.isActive }
        results = results.sort { it.firstName ?: '' }
        def offset = params.offset ? params.offset.toInteger() : 0
        def max = params.max ? params.max.toInteger() : 50
        return results.size() > offset ? results.subList(offset, Math.min(offset + max, results.size())) : []
    }

    def countEmployees(Long companyId, Map params = [:]) {
        def company = Company.findById(companyId)
        if (!company) {
            def all = Company.findAll()
            if (all.isEmpty()) return 0
            company = all.get(0)
        }
        def q = [company: company]
        if (params.departmentId) q.department = Department.get(params.departmentId)
        if (params.designationId) q.designation = Designation.get(params.designationId)
        if (params.isActive != null) q.isActive = params.isActive
        return Employee.findAll(q).size()
    }

    def getEmployeeById(Long id) {
        return Employee.get(id)
    }

    def createEmployee(Long companyId, Map<String, Object> data, Long createdBy) {
        Employee employee = new Employee(
            employeeCode: data.employeeCode,
            firstName: data.firstName,
            lastName: data.lastName,
            otherNames: data.otherNames,
            nationalId: data.nationalId,
            passportNumber: data.passportNumber,
            visaNumber: data.visaNumber,
            emiratesId: data.emiratesId,
            dateOfBirth: data.dateOfBirth ? Date.valueOf(LocalDate.parse(data.dateOfBirth)) : null,
            gender: data.gender,
            maritalStatus: data.maritalStatus,
            nationality: data.nationality,
            bloodGroup: data.bloodGroup,
            primaryPhone: data.primaryPhone,
            secondaryPhone: data.secondaryPhone,
            email: data.email,
            emergencyContactName: data.emergencyContactName,
            emergencyContactPhone: data.emergencyContactPhone,
            emergencyContactRelation: data.emergencyContactRelation,
            address: data.address,
            city: data.city,
            state: data.state,
            country: data.country,
            postalCode: data.postalCode,
            profilePictureUrl: data.profilePictureUrl,
            joiningDate: data.joiningDate ? Date.valueOf(LocalDate.parse(data.joiningDate)) : new Date(),
            terminationDate: null,
            terminationReason: null,
            employmentType: data.employmentType ?: 'FULL_TIME',
            workLocation: data.workLocation,
            language: data.language,
            religion: data.religion,
            educationLevel: data.educationLevel,
            university: data.university,
            specialization: data.specialization,
            highestGrade: data.highestGrade,
            iraanNumber: data.iraanNumber,
            iqamaNumber: data.iqamaNumber,
            drivingLicenseNumber: data.drivingLicenseNumber,
            drivingLicenseExpiry: data.drivingLicenseExpiry ? Date.valueOf(LocalDate.parse(data.drivingLicenseExpiry)) : null,
            drivingLicenseCategory: data.drivingLicenseCategory,
            nationalityCode: data.nationalityCode,
            passportExpiryDate: data.passportExpiryDate ? Date.valueOf(LocalDate.parse(data.passportExpiryDate)) : null,
            visaType: data.visaType,
            sponsorName: data.sponsorName,
            sponsorEmail: data.sponsorEmail,
            sponsorPhone: data.sponsorPhone,
            sponsorNationalId: data.sponsorNationalId,
            passportCountry: data.passportCountry,
            photoUrl: data.photoUrl,
            photoFrontUrl: data.photoFrontUrl,
            photoBackUrl: data.photoBackUrl,
            visaCopyUrl: data.visaCopyUrl,
            emiratesIdCopyUrl: data.emiratesIdCopyUrl,
            contractUrl: data.contractUrl,
            cvUrl: data.cvUrl,
            salary: data.salary ? new BigDecimal(data.salary.toString()) : null,
            grade: data.grade,
            jobTitle: data.jobTitle,
            jobFamily: data.jobFamily,
            reportingManager: data.reportingManager,
            hrManager: data.hrManager,
            remarks: data.remarks,
            isActive: data.isActive != false,
            isProbation: data.isProbation ?: false,
            probationEndDate: data.probationEndDate ? LocalDate.parse(data.probationEndDate) : null,
            department: data.departmentId ? Department.get(data.departmentId) : null,
            designation: data.designationId ? Designation.get(data.designationId) : null,
            company: Company.get(companyId),
            createdBy: createdBy
        )
        employee.save(flush: true, failOnError: true)
        return employee
    }

    def updateEmployee(Long id, Map<String, Object> data, Long updatedBy) {
        Employee employee = Employee.get(id)
        if (!employee) {
            throw new NoSuchElementException("Employee not found: ${id}")
        }
        employee.firstName = data.firstName
        employee.lastName = data.lastName
        employee.email = data.email
        employee.primaryPhone = data.primaryPhone
        employee.secondaryPhone = data.secondaryPhone
        employee.address = data.address
        employee.city = data.city
        employee.state = data.state
        employee.country = data.country
        employee.postalCode = data.postalCode
        employee.jobTitle = data.jobTitle
        employee.grade = data.grade
        employee.salary = data.salary ? new BigDecimal(data.salary.toString()) : employee.salary
        employee.department = data.departmentId ? Department.get(data.departmentId) : employee.department
        employee.designation = data.designationId ? Designation.get(data.designationId) : employee.designation
        employee.updatedBy = updatedBy
        employee.save(flush: true, failOnError: true)
        return employee
    }

    def terminateEmployee(Long id, Map<String, Object> data, Long updatedBy) {
        Employee employee = Employee.get(id)
        if (!employee) {
            throw new NoSuchElementException("Employee not found: ${id}")
        }
        employee.terminationDate = data.terminationDate ? Date.valueOf(LocalDate.parse(data.terminationDate)) : new Date()
        employee.terminationReason = data.terminationReason
        employee.isActive = false
        employee.updatedBy = updatedBy
        employee.save(flush: true, failOnError: true)
        return employee
    }

    def rehireEmployee(Long id, Long updatedBy) {
        Employee employee = Employee.get(id)
        if (!employee) {
            throw new NoSuchElementException("Employee not found: ${id}")
        }
        employee.isActive = true
        employee.terminationDate = null
        employee.updatedBy = updatedBy
        employee.save(flush: true, failOnError: true)
        return employee
    }

    def getAttendanceStats(Long companyId, Integer year, Integer month) {
        LocalDate from = LocalDate.of(year, month, 1)
        LocalDate to = from.plusMonths(1).minusDays(1)
        Date fromDate = Date.valueOf(from)
        Date toDate = Date.valueOf(to)

        return [
            totalDays: { def c = Company.findById(companyId) ?: Company.findAll().get(0); c ? Employee.findAll { company == c }?.size() : 0 } as Long,
            present: 0,
            absent: 0,
            late: 0,
            halfDay: 0
        ]
    }

    def getLeaveStats(Long companyId, Integer year, Integer month) {
        LocalDate from = LocalDate.of(year, month, 1)
        LocalDate to = from.plusMonths(1).minusDays(1)
        Date fromDate = Date.valueOf(from)
        Date toDate = Date.valueOf(to)

        return [
            totalApplications: 0,
            pending: 0,
            approved: 0,
            rejected: 0
        ]
    }

    def listDepartments(Long companyId, Map params = [:]) {
        def company = Company.findById(companyId)
        if (!company) {
            def all = Company.findAll()
            if (all.isEmpty()) {
                throw new NoSuchElementException("No company found for companyId: ${companyId}")
            }
            company = all.get(0)
        }
        def results = Department.findAllByCompany(company)
        if (params.isActive != null) results = results.findAll { it.isActive == params.isActive }
        results = results.sort { it.sortOrder ?: 0 }
        def offset = (params.offset ?: 0) as int
        def max = (params.max ?: 50) as int
        return results.size() > offset ? results.subList(offset, Math.min(offset + max, results.size())) : []
    }

    def countDepartments(Long companyId, Map params = [:]) {
        def company = Company.findById(companyId)
        if (!company) {
            def all = Company.findAll()
            if (all.isEmpty()) {
                throw new NoSuchElementException("No company found for companyId: ${companyId}")
            }
            company = all.get(0)
        }
        def deptList = Department.findAllByCompany(company)
        if (params.isActive != null) {
            deptList = deptList.findAll { it.isActive == params.isActive }
        }
        return deptList.size() ?: 0
    }

    def createDepartment(Long companyId, Map<String, Object> data, Long createdBy) {
        def company = Company.findById(companyId)
        if (!company) {
            def all = Company.findAll()
            if (all.isEmpty()) {
                throw new NoSuchElementException("No company found for companyId: ${companyId}")
            }
            company = all.get(0)
        }
        Department department = new Department(
            name: data.name,
            code: data.code,
            description: data.description,
            company: company,
            parentDepartment: data.parentDepartmentId ? Department.get(data.parentDepartmentId) : null,
            sortOrder: data.sortOrder,
            isActive: data.isActive != false,
            createdBy: createdBy
        )
        department.save(flush: true, failOnError: true)
        return department
    }

    def updateDepartment(Long id, Map<String, Object> data) {
        Department department = Department.get(id)
        if (!department) {
            throw new NoSuchElementException("Department not found: ${id}")
        }
        department.name = data.name
        department.code = data.code
        department.description = data.description
        department.parentDepartment = data.parentDepartmentId ? Department.get(data.parentDepartmentId) : null
        department.sortOrder = data.sortOrder
        department.isActive = data.isActive != false
        department.save(flush: true, failOnError: true)
        return department
    }

    def listDesignations(Long companyId, Map params = [:]) {
        def company = Company.findById(companyId) ?: Company.findAll().get(0)
        if (!company) return []
        def results = Designation.findAllByCompany(company)
        if (params.departmentId) results = results.findAll { it.department?.id == params.departmentId as Long }
        if (params.isActive != null) results = results.findAll { it.isActive == params.isActive }
        results = results.sort { it.sortOrder ?: 0 }
        def offset = (params.offset ?: 0) as int
        def max = (params.max ?: 50) as int
        return results.size() > offset ? results.subList(offset, Math.min(offset + max, results.size())) : []
    }

    def createDesignation(Long companyId, Map<String, Object> data, Long createdBy) {
        Designation designation = new Designation(
            name: data.name,
            code: data.code,
            description: data.description,
            company: Company.get(companyId),
            department: data.departmentId ? Department.get(data.departmentId) : null,
            grade: data.grade,
            sortOrder: data.sortOrder,
            isActive: data.isActive != false,
            createdBy: createdBy
        )
        designation.save(flush: true, failOnError: true)
        return designation
    }
}