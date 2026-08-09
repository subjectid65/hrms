package hrms

import groovy.transform.CompileStatic
import java.time.LocalDate
import java.util.Date

@CompileStatic
class Candidate {

    String firstName
    String lastName
    String email
    String phone
    String currentJobTitle
    String currentCompany
    String expectedSalary
    String resumeUrl
    String coverLetter
    String linkedInProfile
    String source = 'WEBSITE'
    String status = 'NEW'
    String notes
    LocalDate dateApplied
    LocalDate lastContactDate
    LocalDate interviewDate
    LocalDate offerDate
    LocalDate joinDate
    Integer experienceYears
    String education
    String skills
    Integer rating
    JobPosting jobPosting
    User assignedTo
    Boolean isActive = true
    LocalDate createdAt
    LocalDate updatedAt
    Long createdBy

    static constraints = {
        firstName blank: false, maxSize: 100
        lastName blank: false, maxSize: 100
        email format: '^[A-Za-z0-9+_.-]+@(.+)$', maxSize: 100, nullable: true
        phone maxSize: 30, nullable: true
        currentJobTitle maxSize: 100, nullable: true
        currentCompany maxSize: 200, nullable: true
        expectedSalary maxSize: 50, nullable: true
        resumeUrl maxSize: 500, nullable: true
        coverLetter maxSize: 2000, nullable: true
        linkedInProfile maxSize: 500, nullable: true
        source maxSize: 50
        status maxSize: 20
        notes maxSize: 2000, nullable: true
        dateApplied nullable: true
        lastContactDate nullable: true
        interviewDate nullable: true
        offerDate nullable: true
        joinDate nullable: true
        experienceYears nullable: true
        education maxSize: 200, nullable: true
        skills maxSize: 500, nullable: true
        rating nullable: true
        jobPosting nullable: true
        assignedTo nullable: true
        isActive nullable: false
    }

    static mapping = {
        table 'candidate'
        id column: 'candidate_id', generator: 'native'
        firstName column: 'first_name'
        lastName column: 'last_name'
        email column: 'email'
        phone column: 'phone'
        currentJobTitle column: 'current_job_title'
        currentCompany column: 'current_company'
        expectedSalary column: 'expected_salary'
        resumeUrl column: 'resume_url'
        coverLetter column: 'cover_letter'
        linkedInProfile column: 'linked_in_profile'
        source column: 'source'
        status column: 'status'
        notes column: 'notes'
        dateApplied column: 'date_applied', type: 'date'
        lastContactDate column: 'last_contact_date', type: 'date'
        interviewDate column: 'interview_date', type: 'date'
        offerDate column: 'offer_date', type: 'date'
        joinDate column: 'join_date', type: 'date'
        experienceYears column: 'experience_years'
        education column: 'education'
        skills column: 'skills'
        rating column: 'rating'
        jobPosting column: 'job_posting_id'
        assignedTo column: 'assigned_to'
        isActive column: 'is_active'
        createdAt column: 'created_at'
        updatedAt column: 'updated_at'
        createdBy column: 'created_by'
        autoTimestamp false
    }

    static belongsTo = [jobPosting: JobPosting, assignedTo: User]

    def beforeInsert() {
        createdAt = LocalDate.now()
        updatedAt = LocalDate.now()
        if (!dateApplied) dateApplied = LocalDate.now()
    }

    def beforeUpdate() {
        updatedAt = LocalDate.now()
    }

    String getFullName() {
        return "${firstName} ${lastName}".trim()
    }

    String toString() { "${firstName} ${lastName}" }
}