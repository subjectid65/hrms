package hrms

import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@Transactional
class EmployeeService {

    def getCompanyById(Long id) {
        return Company.get(id)
    }

    def listCompanies(Map params = [:]) {
        Company.withCriteria {
            if (params.isActive != null) {
                eq('isActive', params.isActive)
            }
            if (params.search) {
                or {
                    ilike('companyName', "%${params.search}%")
                    ilike('companyCode', "%${params.search}%")
                }
            }
            order('companyName', 'asc')
            firstResult params.offset ?: 0
            maxResults params.max ?: 10
        }
    }

    def countCompanies(Map params = [:]) {
        Company.count {
            if (params.isActive != null) {
                eq('isActive', params.isActive)
            }
            if (params.search) {
                or {
                    ilike('companyName', "%${params.search}%")
                    ilike('companyCode', "%${params.search}%")
                }
            }
        }
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
            website: data.website,
            currencyCode: data.currencyCode ?: 'AED',
            languageCode: data.languageCode ?: 'en',
            timezone: data.timezone ?: 'Asia/Dubai',
            fiscalYearStart: data.fiscalYearStart ?: '01-01',
            fiscalYearEnd: data.fiscalYearEnd ?: '12-31',
            logoUrl: data.logoUrl,
            primaryColor: data.primaryColor ?: '#1e3a5f',
            secondaryColor: data.secondaryColor ?: '#2d7dd2',
            isMultiBranch: data.isMultiBranch ?: false,
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
        company.properties = data
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

    def getDepartmentById(Long id) {
        return Department.get(id)
    }

    def listDepartments(Long companyId, Map params = [:]) {
        Department.withCriteria {
            eq('company', Company.get(companyId))
            if (params.isActive != null) {
                eq('isActive', params.isActive)
            }
            if (params.search) {
                or {
                    ilike('name', "%${params.search}%")
                    ilike('code', "%${params.search}%")
                }
            }
            if (params.parentDepartmentId) {
                eq('parentDepartment', Department.get(params.parentDepartmentId))
            } else {
                eq('parentDepartment', null)
            }
            order('sortOrder', 'asc')
            order('name', 'asc')
            firstResult params.offset ?: 0
            maxResults params.max ?: 50
        }
    }

    def countDepartments(Long companyId, Map params = [:]) {
        Department.count {
            eq('company', Company.get(companyId))
            if (params.isActive != null) {
                eq('isActive', params.isActive)
            }
            if (params.parentDepartmentId) {
                eq('parentDepartment', Department.get(params.parentDepartmentId))
            } else {
                eq('parentDepartment', null)
            }
        }
    }

    def createDepartment(Long companyId, Map<String, Object> data, Long createdBy) {
        Department dept = new Department(
            name: data.name,
            code: data.code,
            description: data.description,
            company: Company.get(companyId),
            parentDepartment: data.parentDepartmentId ? Department.get(data.parentDepartmentId) : null,
            sortOrder: data.sortOrder,
            isActive: data.isActive != false,
            createdBy: createdBy
        )
        dept.save(flush: true, failOnError: true)
        return dept
    }

    def updateDepartment(Long id, Map<String, Object> data) {
        Department dept = Department.get(id)
        if (!dept) {
            throw new NoSuchElementException("Department not found: ${id}")
        }
        dept.properties = data
        dept.save(flush: true, failOnError: true)
        return dept
    }

    def getDesignationById(Long id) {
        return Designation.get(id)
    }

    def listDesignations(Long companyId, Map params = [:]) {
        Designation.withCriteria {
            eq('company', Company.get(companyId))
            if (params.departmentId) {
                eq('department', Department.get(params.departmentId))
            }
            if (params.isActive != null) {
                eq('isActive', params.isActive)
            }
            if (params.search) {
                or {
                    ilike('name', "%${params.search}%")
                    ilike('code', "%${params.search}%")
                }
            }
            order('sortOrder', 'asc')
            order('name', 'asc')
            firstResult params.offset ?: 0
            maxResults params.max ?: 50
        }
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

    def getEmployeeById(Long id) {
        return Employee.get(id)
    }

    def listEmployees(Long companyId, Map params = [:]) {
        Employee.withCriteria {
            eq('company', Company.get(companyId))
            if (params.isActive != null) {
                eq('isActive', params.isActive)
            }
            if (params.departmentId) {
                eq('department', Department.get(params.departmentId))
            }
            if (params.designationId) {
                eq('designation', Designation.get(params.designationId))
            }
            if (params.employmentType) {
                eq('employmentType', params.employmentType)
            }
            if (params.search) {
                or {
                    ilike('firstName', "%${params.search}%")
                    ilike('lastName', "%${params.search}%")
                    ilike('employeeCode', "%${params.search}%")
                    ilike('email', "%${params.search}%")
                }
            }
            if (params.hireDateFrom) {
                gte('joiningDate', LocalDate.parse(params.hireDateFrom))
            }
            if (params.hireDateTo) {
                lte('joiningDate', LocalDate.parse(params.hireDateTo))
            }
            order('firstName', 'asc')
            firstResult params.offset ?: 0
            maxResults params.max ?: 20
        }
    }

    def countEmployees(Long companyId, Map params = [:]) {
        return Employee.countByCompany(Company.get(companyId))
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
            visaExpiryDate: data.visaExpiryDate ? LocalDate.parse(data.visaExpiryDate) : null,
            emiratesId: data.emiratesId,
            dateOfBirth: data.dateOfBirth ? Date.valueOf(data.dateOfBirth) : null,
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
            joiningDate: data.joiningDate ? Date.valueOf(data.joiningDate) : null,
            terminationDate: data.terminationDate ? Date.valueOf(data.terminationDate) : null,
            terminationReason: data.terminationReason,
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
            drivingLicenseExpiry: data.drivingLicenseExpiry ? LocalDate.parse(data.drivingLicenseExpiry) : null,
            drivingLicenseCategory: data.drivingLicenseCategory,
            nationalityCode: data.nationalityCode,
            passportExpiryDate: data.passportExpiryDate ? LocalDate.parse(data.passportExpiryDate) : null,
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
            grade: data.grade,
            jobTitle: data.jobTitle,
            jobFamily: data.jobFamily,
            reportingManager: data.reportingManager,
            hrManager: data.hrManager,
            remarks: data.remarks,
            isActive: data.isActive != false,
            isProbation: data.isProbation ?: false,
            probationEndDate: data.probationEndDate ? LocalDate.parse(data.probationEndDate) : null,
            company: Company.get(companyId),
            department: data.departmentId ? Department.get(data.departmentId) : null,
            designation: data.designationId ? Designation.get(data.designationId) : null,
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
        employee.properties = data
        employee.updatedBy = updatedBy
        employee.save(flush: true, failOnError: true)
        return employee
    }

    def terminateEmployee(Long id, Map<String, Object> data, Long updatedBy) {
        Employee employee = Employee.get(id)
        if (!employee) {
            throw new NoSuchElementException("Employee not found: ${id}")
        }
        employee.isActive = false
        employee.terminationDate = data.terminationDate ? Date.valueOf(data.terminationDate) : new Date()
        employee.terminationReason = data.terminationReason
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
        employee.terminationReason = null
        employee.updatedBy = updatedBy
        employee.save(flush: true, failOnError: true)
        return employee
    }

    def getAttendanceStats(Long companyId, Integer year, Integer month) {
        Date from = Date.valueOf(LocalDate.of(year, month, 1))
        Date to = Date.valueOf(LocalDate.of(year, month, 1).plusMonths(1).minusDays(1))

        def stats = [
            totalDays: AttendanceRecord.count {
                eq('employee.company', Company.get(companyId))
                between('attendanceDate', from, to)
            },
            present: AttendanceRecord.count {
                eq('employee.company', Company.get(companyId))
                eq('status', 'PRESENT')
                between('attendanceDate', from, to)
            },
            absent: AttendanceRecord.count {
                eq('employee.company', Company.get(companyId))
                eq('status', 'ABSENT')
                between('attendanceDate', from, to)
            },
            late: AttendanceRecord.count {
                eq('employee.company', Company.get(companyId))
                gt('lateMinutes', 0)
                between('attendanceDate', from, to)
            },
            halfDay: AttendanceRecord.count {
                eq('employee.company', Company.get(companyId))
                eq('isHalfDay', true)
                between('attendanceDate', from, to)
            }
        ]
        return stats
    }

    def getLeaveStats(Long companyId, Integer year, Integer month) {
        Date from = Date.valueOf(LocalDate.of(year, month, 1))
        Date to = Date.valueOf(LocalDate.of(year, month, 1).plusMonths(1).minusDays(1))

        def stats = [
            totalApplications: LeaveApplication.count {
                eq('employee.company', Company.get(companyId))
                between('fromDate', from, to)
            },
            pending: LeaveApplication.count {
                eq('employee.company', Company.get(companyId))
                eq('status', 'PENDING')
                between('fromDate', from, to)
            },
            approved: LeaveApplication.count {
                eq('employee.company', Company.get(companyId))
                eq('status', 'APPROVED')
                between('fromDate', from, to)
            },
            rejected: LeaveApplication.count {
                eq('employee.company', Company.get(companyId))
                eq('status', 'REJECTED')
                between('fromDate', from, to)
            }
        ]
        return stats
    }
}