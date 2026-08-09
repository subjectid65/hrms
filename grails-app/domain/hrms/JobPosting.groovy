package hrms

import groovy.transform.CompileStatic
import groovy.transform.TypeCheckingMode
import java.time.LocalDate
import java.time.LocalDateTime

@CompileStatic
class JobPosting {

    String title
    String jobCode
    String department
    String location
    String employmentType = 'FULL_TIME'
    String experienceLevel
    Integer noOfPositions
    String qualifications
    String responsibilities
    String description
    BigDecimal minSalary
    BigDecimal maxSalary
    String currency = "AED"
    LocalDateTime postDate
    LocalDateTime closeDate
    String status = 'DRAFT'
    Company company
    User postedBy
    Boolean isActive = true
    LocalDate createdAt
    LocalDate updatedAt
    Long createdBy

    @CompileStatic(TypeCheckingMode.SKIP)
    static constraints = {
        title blank: false, maxSize: 200
        jobCode maxSize: 50, nullable: true
        department maxSize: 100, nullable: true
        location maxSize: 200, nullable: true
        employmentType nullable: true
        experienceLevel maxSize: 50, nullable: true
        noOfPositions nullable: true
        qualifications maxSize: 2000, nullable: true
        responsibilities maxSize: 2000, nullable: true
        description maxSize: 5000, nullable: true
        minSalary nullable: true
        maxSalary nullable: true
        currency maxSize: 3
        postDate nullable: true
        closeDate nullable: true
        status blank: false, maxSize: 20
        company nullable: false
        postedBy nullable: true
        isActive nullable: false
    }

    @CompileStatic(TypeCheckingMode.SKIP)
    static mapping = {
        table 'job_posting'
        id column: 'job_posting_id', generator: 'native'
        title column: 'title'
        jobCode column: 'job_code'
        department column: 'department'
        location column: 'location'
        employmentType column: 'employment_type'
        experienceLevel column: 'experience_level'
        noOfPositions column: 'no_of_positions'
        qualifications column: 'qualifications'
        responsibilities column: 'responsibilities'
        description column: 'description'
        minSalary column: 'min_salary', type: 'big_decimal'
        maxSalary column: 'max_salary', type: 'big_decimal'
        currency column: 'currency'
        postDate column: 'post_date', type: 'timestamp'
        closeDate column: 'close_date', type: 'timestamp'
        status column: 'status'
        company column: 'company_id'
        postedBy column: 'posted_by'
        isActive column: 'is_active'
        createdAt column: 'created_at'
        updatedAt column: 'updated_at'
        createdBy column: 'created_by'
        autoTimestamp false
    }

    static belongsTo = [company: Company, postedBy: User]

    static hasMany = [candidates: Candidate]

    def beforeInsert() {
        createdAt = LocalDate.now()
        updatedAt = LocalDate.now()
        if (!postDate) postDate = LocalDateTime.now()
    }

    def beforeUpdate() {
        updatedAt = LocalDate.now()
    }

    String toString() { "${title} (${jobCode})" }
}