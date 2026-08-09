package hrms

import grails.gorm.transactions.Transactional
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Controller
import grails.converters.JSON

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
        render JSON.encodeAsJSON([postings: postings, total: total, offset: params.offset, max: params.max])
    }

    def showJobPosting(Long companyId, Long id) {
        def posting = recruitmentService.getJobPostingById(id)
        if (!posting || posting.company.id != companyId) {
            response.status = HttpStatus.NOT_FOUND.value()
            render JSON.encodeAsJSON([message: 'Job posting not found'])
            return
        }
        render JSON.encodeAsJSON(posting)
    }

    def createJobPosting(Long companyId) {
        try {
            def posting = recruitmentService.createJobPosting(companyId, request.JSON, session?.user?.id)
            response.status = HttpStatus.CREATED.value()
            render JSON.encodeAsJSON([message: 'Job posting created', posting: posting])
        } catch (Exception e) {
            response.status = HttpStatus.BAD_REQUEST.value()
            render JSON.encodeAsJSON([message: e.message])
        }
    }

    def updateJobPosting(Long companyId, Long id) {
        try {
            def posting = recruitmentService.updateJobPosting(id, request.JSON)
            render JSON.encodeAsJSON([message: 'Job posting updated', posting: posting])
        } catch (Exception e) {
            response.status = HttpStatus.BAD_REQUEST.value()
            render JSON.encodeAsJSON([message: e.message])
        }
    }

    def listCandidates(Long companyId, Long jobPostingId) {
        def candidates = recruitmentService.listCandidates(companyId, params)
        def total = recruitmentService.countCandidates(companyId, params)
        render JSON.encodeAsJSON([candidates: candidates, total: total])
    }

    def showCandidate(Long companyId, Long id) {
        def candidate = recruitmentService.getCandidateById(id)
        if (!candidate) {
            response.status = HttpStatus.NOT_FOUND.value()
            render JSON.encodeAsJSON([message: 'Candidate not found'])
            return
        }
        render JSON.encodeAsJSON(candidate)
    }

    def createCandidate(Long companyId, Long jobPostingId) {
        try {
            def candidate = recruitmentService.createCandidate(companyId, request.JSON, session?.user?.id)
            response.status = HttpStatus.CREATED.value()
            render JSON.encodeAsJSON([message: 'Candidate created', candidate: candidate])
        } catch (Exception e) {
            response.status = HttpStatus.BAD_REQUEST.value()
            render JSON.encodeAsJSON([message: e.message])
        }
    }

    def updateCandidate(Long companyId, Long id) {
        try {
            def candidate = recruitmentService.updateCandidate(id, request.JSON)
            render JSON.encodeAsJSON([message: 'Candidate updated', candidate: candidate])
        } catch (Exception e) {
            response.status = HttpStatus.BAD_REQUEST.value()
            render JSON.encodeAsJSON([message: e.message])
        }
    }

    def hire(Long candidateId, Long companyId) {
        try {
            def employee = recruitmentService.convertCandidateToEmployee(candidateId, companyId, request.JSON, session?.user?.id)
            response.status = HttpStatus.CREATED.value()
            render JSON.encodeAsJSON([message: 'Candidate hired successfully', employee: employee])
        } catch (Exception e) {
            response.status = HttpStatus.BAD_REQUEST.value()
            render JSON.encodeAsJSON([message: e.message])
        }
    }

    def onboardingTasks(Long employeeId) {
        def tasks = recruitmentService.getOnboardingTasks(employeeId)
        render JSON.encodeAsJSON(tasks)
    }

    def completeOnboardingTask(Long taskId) {
        try {
            def task = recruitmentService.completeOnboardingTask(taskId, session?.user?.id)
            render JSON.encodeAsJSON([message: 'Task completed', task: task])
        } catch (Exception e) {
            response.status = HttpStatus.BAD_REQUEST.value()
            render JSON.encodeAsJSON([message: e.message])
        }
    }

    def stats(Long companyId) {
        def stats = recruitmentService.getRecruitmentStats(companyId)
        render JSON.encodeAsJSON(stats)
    }
}