package hrms

import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@Transactional
class RecruitmentService {

    def listJobPostings(Long companyId, Map params = [:]) {
        return JobPosting.withCriteria {
            eq('company', Company.get(companyId))
            if (params.status) {
                eq('status', params.status)
            }
            if (params.isActive != null) {
                eq('isActive', params.isActive)
            }
            if (params.search) {
                or {
                    ilike('title', "%${params.search}%")
                    ilike('jobCode', "%${params.search}%")
                    ilike('department', "%${params.search}%")
                }
            }
            order('postDate', 'desc')
            firstResult params.offset ?: 0
            maxResults params.max ?: 20
        }
    }

    def countJobPostings(Long companyId, Map params = [:]) {
        def company = Company.get(companyId)
        def q = [company: company]
        if (params.status) q.status = params.status
        if (params.isActive != null) q.isActive = params.isActive
        return JobPosting.findAll(q)?.size() ?: 0
    }

    def getJobPostingById(Long id) {
        return JobPosting.get(id)
    }

    def createJobPosting(Long companyId, Map<String, Object> data, Long createdBy) {
        JobPosting posting = new JobPosting(
            title: data.title,
            jobCode: data.jobCode,
            department: data.department,
            location: data.location,
            employmentType: data.employmentType,
            experienceLevel: data.experienceLevel,
            noOfPositions: data.noOfPositions,
            qualifications: data.qualifications,
            responsibilities: data.responsibilities,
            description: data.description,
            minSalary: data.minSalary,
            maxSalary: data.maxSalary,
            currency: data.currency ?: 'AED',
            status: 'OPEN',
            company: Company.get(companyId),
            postedBy: User.get(createdBy),
            isActive: true,
            createdBy: createdBy
        )
        posting.save(flush: true, failOnError: true)
        return posting
    }

    def updateJobPosting(Long id, Map<String, Object> data) {
        JobPosting posting = JobPosting.get(id)
        if (!posting) {
            throw new NoSuchElementException("Job posting not found: ${id}")
        }
        posting.title = data.title
        posting.description = data.description
        posting.status = data.status
        posting.save(flush: true, failOnError: true)
        return posting
    }

    def listCandidates(Long companyId, Map params = [:]) {
        return Candidate.withCriteria {
            if (params.jobPostingId) {
                eq('jobPosting', JobPosting.get(params.jobPostingId))
            }
            if (params.status) {
                eq('status', params.status)
            }
            if (params.assignedTo) {
                eq('assignedTo', User.get(params.assignedTo))
            }
            if (params.search) {
                or {
                    ilike('firstName', "%${params.search}%")
                    ilike('lastName', "%${params.search}%")
                    ilike('email', "%${params.search}%")
                }
            }
            order('dateApplied', 'desc')
            firstResult params.offset ?: 0
            maxResults params.max ?: 20
        }
    }

    def countCandidates(Long companyId, Map params = [:]) {
        def q = [:]
        if (params.jobPostingId) q.jobPosting = JobPosting.get(params.jobPostingId)
        if (params.status) q.status = params.status
        if (params.assignedTo) q.assignedTo = User.get(params.assignedTo)
        return Candidate.findAll(q)?.size() ?: 0
    }

    def getCandidateById(Long id) {
        return Candidate.get(id)
    }

    def createCandidate(Long companyId, Map<String, Object> data, Long createdBy) {
        Candidate candidate = new Candidate(
            firstName: data.firstName,
            lastName: data.lastName,
            email: data.email,
            phone: data.phone,
            currentJobTitle: data.currentJobTitle,
            currentCompany: data.currentCompany,
            expectedSalary: data.expectedSalary,
            resumeUrl: data.resumeUrl,
            coverLetter: data.coverLetter,
            linkedInProfile: data.linkedInProfile,
            source: data.source,
            status: 'NEW',
            dateApplied: data.dateApplied ? LocalDate.parse(data.dateApplied) : LocalDate.now(),
            jobPosting: data.jobPostingId ? JobPosting.get(data.jobPostingId) : null,
            assignedTo: data.assignedTo ? User.get(data.assignedTo) : null,
            isActive: true,
            createdBy: createdBy
        )
        candidate.save(flush: true, failOnError: true)
        return candidate
    }

    def updateCandidate(Long id, Map<String, Object> data) {
        Candidate candidate = Candidate.get(id)
        if (!candidate) {
            throw new NoSuchElementException("Candidate not found: ${id}")
        }
        candidate.firstName = data.firstName
        candidate.lastName = data.lastName
        candidate.status = data.status
        candidate.assignedTo = data.assignedTo ? User.get(data.assignedTo as Long) : null
        candidate.save(flush: true, failOnError: true)
        return candidate
    }

    def convertCandidateToEmployee(Long candidateId, Long companyId, Map<String, Object> data, Long createdBy) {
        Candidate candidate = Candidate.get(candidateId)
        if (!candidate) {
            throw new NoSuchElementException("Candidate not found: ${candidateId}")
        }

        Employee employee = new Employee(
            employeeCode: generateEmployeeCode(companyId),
            firstName: candidate.firstName,
            lastName: candidate.lastName,
            email: candidate.email,
            phone: candidate.phone,
            joiningDate: candidate.joinDate != null ? java.sql.Date.valueOf(candidate.joinDate) : new java.sql.Date(System.currentTimeMillis()),
            employmentType: data.employmentType ?: 'FULL_TIME',
            department: data.departmentId ? Department.get(data.departmentId) : null,
            designation: data.designationId ? Designation.get(data.designationId) : null,
            salary: data.salary,
            probationEndDate: data.probationEndDate ? LocalDate.parse(data.probationEndDate) : null,
            company: Company.get(companyId),
            createdBy: createdBy
        )
        employee.save(flush: true, failOnError: true)

        candidate.status = 'HIRED'
        candidate.joinDate = employee.joiningDate?.toLocalDate()
        candidate.save(flush: true, failOnError: true)

        return employee
    }

    def getOnboardingTasks(Long employeeId) {
        return OnboardingTask.withCriteria {
            eq('employee', Employee.get(employeeId))
            order('dueDate', 'asc')
        }
    }

    def completeOnboardingTask(Long taskId, Long completedBy) {
        OnboardingTask task = OnboardingTask.get(taskId)
        if (!task) {
            throw new NoSuchElementException("Onboarding task not found: ${taskId}")
        }
        task.status = 'COMPLETED'
        task.completedDate = LocalDate.now()
        task.save(flush: true, failOnError: true)
        return task
    }

    def getRecruitmentStats(Long companyId) {
        def company = Company.get(companyId)
        def postings = JobPosting.findAllByCompany(company)
        def candidates = Candidate.findAll { jobPosting.company == company }
        def startDate = LocalDate.now().withDayOfMonth(1)
        return [
            totalPostings: postings?.size() ?: 0,
            activePostings: postings?.findAll { it.isActive }?.size() ?: 0,
            totalCandidates: candidates?.size() ?: 0,
            pendingCandidates: candidates?.findAll { it.status == 'NEW' }?.size() ?: 0,
            hiredThisMonth: candidates?.findAll { it.status == 'HIRED' && it.joinDate >= startDate.toDate() }?.size() ?: 0
        ]
    }

    private String generateEmployeeCode(Long companyId) {
        Long empCount = Employee.countByCompany(Company.get(companyId))
        return String.format("EMP-%04d", empCount + 1)
    }
}