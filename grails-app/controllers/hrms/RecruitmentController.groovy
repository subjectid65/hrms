package hrms

import grails.gorm.transactions.Transactional
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Controller

@Controller
@Transactional
class RecruitmentController {

    RecruitmentService recruitmentService

    def index() {
        render view: 'index', model: [title: 'HRMS - Recruitment Management']
    }

    def listJobPostings(Long companyId) {
        def postings = recruitmentService.listJobPostings(companyId, params)
        def total = recruitmentService.countJobPostings(companyId, params)
        def serialized = postings.collect { p -> [id: p.id, title: p.title, department: p.department, location: p.location, employmentType: p.employmentType, noOfPositions: p.noOfPositions, minSalary: p.minSalary, maxSalary: p.maxSalary, status: p.status, postedDate: p.postedDate] }
        render contentType: 'application/json', text: new groovy.json.JsonOutput().toJson([postings: serialized, total: total, offset: params.offset, max: params.max])
    }

    def showJobPosting(Long companyId, Long id) {
        def posting = recruitmentService.getJobPostingById(id)
        if (!posting || posting.company.id != companyId) {
            response.status = HttpStatus.NOT_FOUND.value()
            render contentType: 'application/json', text: new groovy.json.JsonOutput().toJson([message: 'Job posting not found'])
            return
        }
        render contentType: 'application/json', text: new groovy.json.JsonOutput().toJson([id: posting.id, title: posting.title, department: posting.department, location: posting.location, employmentType: posting.employmentType, noOfPositions: posting.noOfPositions, minSalary: posting.minSalary, maxSalary: posting.maxSalary, status: posting.status, postedDate: posting.postedDate, description: posting.description])
    }

    def createJobPosting(Long companyId) {
        try {
            def posting = recruitmentService.createJobPosting(companyId, request.JSON, session?.user?.id)
            response.status = HttpStatus.CREATED.value()
            render contentType: 'application/json', text: new groovy.json.JsonOutput().toJson([message: 'Job posting created', posting: [id: posting.id, title: posting.title, department: posting.department, status: posting.status]])
        } catch (Exception e) {
            response.status = HttpStatus.BAD_REQUEST.value()
            render contentType: 'application/json', text: new groovy.json.JsonOutput().toJson([message: e.message])
        }
    }

    def updateJobPosting(Long companyId, Long id) {
        try {
            def posting = recruitmentService.updateJobPosting(id, request.JSON)
            render contentType: 'application/json', text: new groovy.json.JsonOutput().toJson([message: 'Job posting updated', posting: [id: posting.id, title: posting.title, status: posting.status]])
        } catch (Exception e) {
            response.status = HttpStatus.BAD_REQUEST.value()
            render contentType: 'application/json', text: new groovy.json.JsonOutput().toJson([message: e.message])
        }
    }

    def listCandidates(Long companyId, Long jobPostingId) {
        def candidates = recruitmentService.listCandidates(companyId, params)
        def total = recruitmentService.countCandidates(companyId, params)
        def serialized = candidates.collect { c -> [id: c.id, firstName: c.firstName, lastName: c.lastName, email: c.email, phone: c.phone, position: c.position, status: c.status, appliedDate: c.appliedDate] }
        render contentType: 'application/json', text: new groovy.json.JsonOutput().toJson([candidates: serialized, total: total])
    }

    def showCandidate(Long companyId, Long id) {
        def candidate = recruitmentService.getCandidateById(id)
        if (!candidate) {
            response.status = HttpStatus.NOT_FOUND.value()
            render contentType: 'application/json', text: new groovy.json.JsonOutput().toJson([message: 'Candidate not found'])
            return
        }
        render contentType: 'application/json', text: new groovy.json.JsonOutput().toJson([id: candidate.id, firstName: candidate.firstName, lastName: candidate.lastName, email: candidate.email, phone: candidate.phone, position: candidate.position, status: candidate.status, appliedDate: candidate.appliedDate])
    }

    def createCandidate(Long companyId, Long jobPostingId) {
        try {
            def candidate = recruitmentService.createCandidate(companyId, request.JSON, session?.user?.id)
            response.status = HttpStatus.CREATED.value()
            render contentType: 'application/json', text: new groovy.json.JsonOutput().toJson([message: 'Candidate created', candidate: [id: candidate.id, firstName: candidate.firstName, lastName: candidate.lastName, email: candidate.email, status: candidate.status]])
        } catch (Exception e) {
            response.status = HttpStatus.BAD_REQUEST.value()
            render contentType: 'application/json', text: new groovy.json.JsonOutput().toJson([message: e.message])
        }
    }

    def updateCandidate(Long companyId, Long id) {
        try {
            def candidate = recruitmentService.updateCandidate(id, request.JSON)
            render contentType: 'application/json', text: new groovy.json.JsonOutput().toJson([message: 'Candidate updated', candidate: [id: candidate.id, firstName: candidate.firstName, lastName: candidate.lastName, status: candidate.status]])
        } catch (Exception e) {
            response.status = HttpStatus.BAD_REQUEST.value()
            render contentType: 'application/json', text: new groovy.json.JsonOutput().toJson([message: e.message])
        }
    }

    def hire(Long candidateId, Long companyId) {
        try {
            def employee = recruitmentService.convertCandidateToEmployee(candidateId, companyId, request.JSON, session?.user?.id)
            response.status = HttpStatus.CREATED.value()
            render contentType: 'application/json', text: new groovy.json.JsonOutput().toJson([message: 'Candidate hired successfully', employee: [id: employee.id, employeeCode: employee.employeeCode, firstName: employee.firstName, lastName: employee.lastName]])
        } catch (Exception e) {
            response.status = HttpStatus.BAD_REQUEST.value()
            render contentType: 'application/json', text: new groovy.json.JsonOutput().toJson([message: e.message])
        }
    }

    def onboardingTasks(Long employeeId) {
        def tasks = recruitmentService.getOnboardingTasks(employeeId)
        def serialized = tasks.collect { t -> [id: t.id, title: t.title, description: t.description, isCompleted: t.isCompleted] }
        render contentType: 'application/json', text: new groovy.json.JsonOutput().toJson(serialized)
    }

    def completeOnboardingTask(Long taskId) {
        try {
            def task = recruitmentService.completeOnboardingTask(taskId, session?.user?.id)
            render contentType: 'application/json', text: new groovy.json.JsonOutput().toJson([message: 'Task completed', task: [id: task.id, title: task.title, isCompleted: task.isCompleted]])
        } catch (Exception e) {
            response.status = HttpStatus.BAD_REQUEST.value()
            render contentType: 'application/json', text: new groovy.json.JsonOutput().toJson([message: e.message])
        }
    }

    def stats(Long companyId) {
        def stats = recruitmentService.getRecruitmentStats(companyId)
        render contentType: 'application/json', text: new groovy.json.JsonOutput().toJson(stats)
    }
}