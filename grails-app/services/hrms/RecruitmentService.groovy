package hrms

import groovy.transform.CompileStatic
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.time.LocalDateTime

@CompileStatic
@Transactional
class RecruitmentService {

    def createJobPosting(Long companyId, Map<String, Object> data, Long postedBy) {
        JobPosting posting = new JobPosting(
            title: data.title,
            jobCode: data.jobCode,
            department: data.department,
            location: data.location,
            employmentType: data.employmentType ?: 'FULL_TIME',
            experienceLevel: data.experienceLevel,
            noOfPositions: data.noOfPositions,
            qualifications: data.qualifications,
            responsibilities: data.responsibilities,
            description: data.description,
            minSalary: data.minSalary ? new java.math.BigDecimal(data.minSalary.toString()) : null,
            maxSalary: data.maxSalary ? new java.math.BigDecimal(data.maxSalary.toString()) : null,
            currency: data.currency ?: 'AED',
            status: data.status ?: 'DRAFT',
            company: Company.get(companyId),
            postedBy: User.get(postedBy),
            isActive: true,
            createdBy: postedBy
        )
        posting.save(flush: true, failOnError: true)
        return posting
    }

    def listJobPostings(Long companyId, Map params = [:]) {
        JobPosting.withCriteria {
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
        return JobPosting.count {
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
        }
    }

    def getJobPostingById(Long id) {
        return JobPosting.get(id)
    }

    def updateJobPosting(Long id, Map<String, Object> data) {
        JobPosting posting = JobPosting.get(id)
        if (!posting) {
            throw new NoSuchElementException("Job posting not found: ${id}")
        }
        posting.properties = data
        posting.save(flush: true, failOnError: true)
        return posting
    }

    def createCandidate(Long companyId, Map<String, Object> data, Long assignedTo) {
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
            source: data.source ?: 'WEBSITE',
            status: data.status ?: 'NEW',
            notes: data.notes,
            dateApplied: data.dateApplied ? LocalDate.parse(data.dateApplied) : LocalDate.now(),
            experienceYears: data.experienceYears,
            education: data.education,
            skills: data.skills,
            rating: data.rating,
            jobPosting: data.jobPostingId ? JobPosting.get(data.jobPostingId) : null,
            assignedTo: User.get(assignedTo),
            isActive: true,
            createdBy: assignedTo
        )
        candidate.save(flush: true, failOnError: true)
        return candidate
    }

    def listCandidates(Long companyId, Map params = [:]) {
        Candidate.withCriteria {
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
        return Candidate.count {
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
        }
    }

    def getCandidateById(Long id) {
        return Candidate.get(id)
    }

    def updateCandidate(Long id, Map<String, Object> data) {
        Candidate candidate = Candidate.get(id)
        if (!candidate) {
            throw new NoSuchElementException("Candidate not found: ${id}")
        }
        candidate.properties = data
        candidate.save(flush: true, failOnError: true)
        return candidate
    }

    def convertCandidateToEmployee(Long candidateId, Long companyId, Map<String, Object> data, Long createdBy) {
        Candidate candidate = Candidate.get(candidateId)
        if (!candidate) {
            throw new NoSuchElementException("Candidate not found: ${candidateId}")
        }

        Employee employee = new Employee(
            employeeCode: data.employeeCode ?: generateEmployeeCode(companyId),
            firstName: candidate.firstName,
            lastName: candidate.lastName,
            email: candidate.email,
            phone: candidate.phone,
            joiningDate: data.joiningDate ? Date.valueOf(data.joiningDate) : new Date(),
            employmentType: data.employmentType ?: 'FULL_TIME',
            workLocation: data.workLocation,
            grade: data.grade,
            jobTitle: data.jobTitle ?: candidate.currentJobTitle,
            isActive: true,
            isProbation: data.isProbation ?: true,
            probationEndDate: data.probationEndDate ? LocalDate.parse(data.probationEndDate) : null,
            company: Company.get(companyId),
            department: data.departmentId ? Department.get(data.departmentId) : null,
            designation: data.designationId ? Designation.get(data.designationId) : null,
            createdBy: createdBy
        )
        employee.save(flush: true, failOnError: true)

        // Update candidate status
        candidate.status = 'HIRED'
        candidate.joinDate = LocalDate.now()
        candidate.save(flush: true, failOnError: true)

        // Create onboarding tasks
        createOnboardingTasks(employee, createdBy)

        return employee
    }

    def createOnboardingTasks(Employee employee, Long createdBy) {
        List<String> defaultTasks = [
            'Complete probation documentation',
            'Attend orientation session',
            'Set up company email account',
            'Issue ID card and access pass',
            'Assign desk and equipment',
            'Complete tax form submission',
            'Enroll in insurance plan',
            'Complete UAE labor contract signing'
        ]

        for (String taskName : defaultTasks) {
            OnboardingTask task = new OnboardingTask(
                name: taskName,
                employee: employee,
                assignedTo: 'HR',
                priority: 'MEDIUM',
                status: 'PENDING',
                dueDate: LocalDate.now().plusDays(7),
                createdBy: createdBy
            )
            task.save(flush: false)
        }
    }

    def getOnboardingTasks(Long employeeId) {
        return OnboardingTask.findAll {
            eq('employee', Employee.get(employeeId))
            order('dueDate', 'asc')
        }
    }

    def completeOnboardingTask(Long taskId, Long completedBy) {
        OnboardingTask task = OnboardingTask.get(taskId)
        if (!task) {
            throw new NoSuchElementException("Task not found: ${taskId}")
        }
        task.status = 'COMPLETED'
        task.completedDate = LocalDate.now()
        task.save(flush: true, failOnError: true)
        return task
    }

    private String generateEmployeeCode(Long companyId) {
        Company company = Company.get(companyId)
        String prefix = company?.companyCode ?: 'EMP'
        Long count = Employee.countByCompany(company)
        return "${prefix}${String.format('%04d', count + 1)}"
    }

    def getRecruitmentStats(Long companyId) {
        def stats = [
            totalPostings: JobPosting.count { eq('company', Company.get(companyId)) },
            activePostings: JobPosting.count {
                eq('company', Company.get(companyId))
                eq('isActive', true)
            },
            totalCandidates: Candidate.count {
                isNull('jobPosting.company') ? true : eq('jobPosting.company', Company.get(companyId))
            },
            pendingCandidates: Candidate.count {
                isNull('jobPosting.company') ? true : eq('jobPosting.company', Company.get(companyId))
                eq('status', 'NEW')
            },
            hiredThisMonth: Candidate.count {
                isNull('jobPosting.company') ? true : eq('jobPosting.company', Company.get(companyId))
                eq('status', 'HIRED')
                ge('joinDate', LocalDate.now().withDayOfMonth(1))
            }
        ]
        return stats
    }
}