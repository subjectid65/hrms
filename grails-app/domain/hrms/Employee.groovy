package hrms

import groovy.transform.CompileStatic
import java.time.LocalDate
import java.util.Date

@CompileStatic
class Employee {

    String employeeCode
    String firstName
    String lastName
    String otherNames
    String nationalId
    String passportNumber
    String visaNumber
    String visaExpiryDate
    String emiratesId
    Date dateOfBirth
    String gender
    String maritalStatus
    String nationality
    String bloodGroup
    String primaryPhone
    String secondaryPhone
    String email
    String emergencyContactName
    String emergencyContactPhone
    String emergencyContactRelation
    String address
    String city
    String state
    String country
    String postalCode
    String profilePictureUrl
    Date joiningDate
    Date terminationDate
    String terminationReason
    String employmentType = 'FULL_TIME'
    String workLocation
    String language
    String religion
    String educationLevel
    String university
    String specialization
    String highestGrade
    String iraanNumber
    String iqamaNumber
    String drivingLicenseNumber
    String drivingLicenseExpiry
    String drivingLicenseCategory
    String nationalityCode
    String passportExpiryDate
    String visaType
    String sponsorName
    String sponsorEmail
    String sponsorPhone
    String sponsorNationalId
    String passportCountry
    String photoUrl
    String photoFrontUrl
    String photoBackUrl
    String visaCopyUrl
    String emiratesIdCopyUrl
    String contractUrl
    String cvUrl
    BigDecimal salary
    String grade
    String jobTitle
    String jobFamily
    String reportingManager
    String hrManager
    String remarks
    Boolean isActive = true
    Boolean isProbation = false
    LocalDate probationEndDate
    LocalDate createdAt
    LocalDate updatedAt
    Long createdBy
    Long updatedBy

    static transients = ['fullName', 'photoUrl', 'photoFrontUrl', 'photoBackUrl',
                        'visaCopyUrl', 'emiratesIdCopyUrl', 'contractUrl', 'cvUrl',
                        'profilePictureUrl']

    static constraints = {
        employeeCode blank: false, maxSize: 20
        firstName blank: false, maxSize: 100
        lastName blank: false, maxSize: 100
        nationalId maxSize: 50, nullable: true
        passportNumber maxSize: 50, nullable: true
        visaNumber maxSize: 50, nullable: true
        emiratesId maxSize: 50, nullable: true
        dateOfBirth nullable: true
        gender nullable: true
        maritalStatus nullable: true
        nationality maxSize: 100, nullable: true
        bloodGroup maxSize: 10, nullable: true
        primaryPhone maxSize: 30, nullable: true
        email format: '^[A-Za-z0-9+_.-]+@(.+)$', nullable: true
        emergencyContactName maxSize: 100, nullable: true
        emergencyContactPhone maxSize: 30, nullable: true
        emergencyContactRelation maxSize: 50, nullable: true
        address maxSize: 500, nullable: true
        city maxSize: 100, nullable: true
        state maxSize: 100, nullable: true
        country maxSize: 100, nullable: true
        postalCode maxSize: 20, nullable: true
        profilePictureUrl maxSize: 500, nullable: true
        joiningDate nullable: true
        terminationDate nullable: true
        terminationReason maxSize: 500, nullable: true
        employmentType nullable: true
        workLocation maxSize: 100, nullable: true
        language maxSize: 50, nullable: true
        religion maxSize: 50, nullable: true
        educationLevel maxSize: 100, nullable: true
        university maxSize: 200, nullable: true
        specialization maxSize: 200, nullable: true
        highestGrade maxSize: 50, nullable: true
        iraanNumber maxSize: 50, nullable: true
        iqamaNumber maxSize: 50, nullable: true
        drivingLicenseNumber maxSize: 50, nullable: true
        drivingLicenseExpiry nullable: true
        drivingLicenseCategory maxSize: 20, nullable: true
        nationalityCode maxSize: 10, nullable: true
        passportExpiryDate nullable: true
        visaType maxSize: 50, nullable: true
        sponsorName maxSize: 200, nullable: true
        sponsorEmail maxSize: 100, nullable: true
        sponsorPhone maxSize: 30, nullable: true
        sponsorNationalId maxSize: 50, nullable: true
        passportCountry maxSize: 100, nullable: true
        photoUrl maxSize: 500, nullable: true
        photoFrontUrl maxSize: 500, nullable: true
        photoBackUrl maxSize: 500, nullable: true
        visaCopyUrl maxSize: 500, nullable: true
        emiratesIdCopyUrl maxSize: 500, nullable: true
        contractUrl maxSize: 500, nullable: true
        cvUrl maxSize: 500, nullable: true
        salary nullable: true
        grade maxSize: 50, nullable: true
        jobTitle maxSize: 200, nullable: true
        jobFamily maxSize: 100, nullable: true
        reportingManager maxSize: 200, nullable: true
        hrManager maxSize: 200, nullable: true
        remarks maxSize: 1000, nullable: true
        isActive nullable: false
        isProbation nullable: false
        probationEndDate nullable: true
    }

    static mapping = {
        table 'employee'
        id column: 'employee_id', generator: 'native'
        employeeCode column: 'employee_code'
        firstName column: 'first_name'
        lastName column: 'last_name'
        otherNames column: 'other_names'
        nationalId column: 'national_id'
        passportNumber column: 'passport_number'
        visaNumber column: 'visa_number'
        visaExpiryDate column: 'visa_expiry_date', type: 'date'
        emiratesId column: 'emirates_id'
        dateOfBirth column: 'date_of_birth', type: 'date'
        gender column: 'gender'
        maritalStatus column: 'marital_status'
        nationality column: 'nationality'
        bloodGroup column: 'blood_group'
        primaryPhone column: 'primary_phone'
        secondaryPhone column: 'secondary_phone'
        email column: 'email'
        emergencyContactName column: 'emergency_contact_name'
        emergencyContactPhone column: 'emergency_contact_phone'
        emergencyContactRelation column: 'emergency_contact_relation'
        address column: 'address'
        city column: 'city'
        state column: 'state'
        country column: 'country'
        postalCode column: 'postal_code'
        profilePictureUrl column: 'profile_picture_url'
        joiningDate column: 'joining_date', type: 'date'
        terminationDate column: 'termination_date', type: 'date'
        terminationReason column: 'termination_reason'
        employmentType column: 'employment_type'
        workLocation column: 'work_location'
        language column: 'language'
        religion column: 'religion'
        educationLevel column: 'education_level'
        university column: 'university'
        specialization column: 'specialization'
        highestGrade column: 'highest_grade'
        iraanNumber column: 'iraan_number'
        iqamaNumber column: 'iqama_number'
        drivingLicenseNumber column: 'driving_license_number'
        drivingLicenseExpiry column: 'driving_license_expiry', type: 'date'
        drivingLicenseCategory column: 'driving_license_category'
        nationalityCode column: 'nationality_code'
        passportExpiryDate column: 'passport_expiry_date', type: 'date'
        visaType column: 'visa_type'
        sponsorName column: 'sponsor_name'
        sponsorEmail column: 'sponsor_email'
        sponsorPhone column: 'sponsor_phone'
        sponsorNationalId column: 'sponsorNationalId'
        passportCountry column: 'passport_country'
        photoUrl column: 'photo_url'
        photoFrontUrl column: 'photo_front_url'
        photoBackUrl column: 'photo_back_url'
        visaCopyUrl column: 'visa_copy_url'
        emiratesIdCopyUrl column: 'emirates_id_copy_url'
        contractUrl column: 'contract_url'
        cvUrl column: 'cv_url'
        salary column: 'salary', type: 'big_decimal'
        grade column: 'grade'
        jobTitle column: 'job_title'
        jobFamily column: 'job_family'
        reportingManager column: 'reporting_manager'
        hrManager column: 'hr_manager'
        remarks column: 'remarks'
        isActive column: 'is_active'
        isProbation column: 'is_probation'
        probationEndDate column: 'probation_end_date', type: 'date'
        createdAt column: 'created_at'
        updatedAt column: 'updated_at'
        createdBy column: 'created_by'
        updatedBy column: 'updated_by'
        autoTimestamp false
    }

    static belongsTo = [company: Company, department: Department, designation: Designation]

    static hasMany = [
        attendanceRecords: AttendanceRecord,
        leaveApplications: LeaveApplication,
        payslips: Payslip,
        expenses: Expense,
        employeeDocuments: EmployeeDocument,
        employeeContacts: EmployeeContact,
        employeeBankAccounts: EmployeeBankAccount
    ]

    def beforeInsert() {
        createdAt = LocalDate.now()
        updatedAt = LocalDate.now()
    }

    def beforeUpdate() {
        updatedAt = LocalDate.now()
    }

    String getFullName() {
        StringBuilder sb = new StringBuilder()
        if (firstName) sb.append(firstName)
        if (otherNames) sb.append(" $otherNames")
        if (lastName) sb.append(" $lastName")
        return sb.toString().trim()
    }

    String toString() {
        return "${employeeCode}: ${getFullName()}"
    }
}