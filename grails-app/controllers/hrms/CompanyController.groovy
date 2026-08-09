package hrms

import grails.gorm.transactions.Transactional
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Controller
import grails.converters.JSON

@Controller
@Transactional
class CompanyController {

    EmployeeService employeeService
    CompanySettingService companySettingService

    def index() {
        render view: 'index', model: [title: 'HRMS - Company Management']
    }

    @CompileStatic(TypeCheckingMode.SKIP)
    def list() {
        def companies = employeeService.listCompanies(params)
        def total = employeeService.countCompanies(params)
        render JSON.encodeAsJSON([companies: companies, total: total, offset: params.offset, max: params.max])
    }

    @CompileStatic(TypeCheckingMode.SKIP)
    def show(Long id) {
        def company = employeeService.getCompanyById(id)
        if (!company) {
            response.status = HttpStatus.NOT_FOUND.value()
            render JSON.encodeAsJSON([message: 'Company not found'])
            return
        }
        render JSON.encodeAsJSON(company)
    }

    @CompileStatic(TypeCheckingMode.SKIP)
    def create() {
        try {
            def company = employeeService.createCompany(request.JSON, session?.user?.id)
            response.status = HttpStatus.CREATED.value()
            render JSON.encodeAsJSON([message: 'Company created successfully', company: company])
        } catch (Exception e) {
            response.status = HttpStatus.BAD_REQUEST.value()
            render JSON.encodeAsJSON([message: e.message])
        }
    }

    @CompileStatic(TypeCheckingMode.SKIP)
    def update(Long id) {
        try {
            def company = employeeService.updateCompany(id, request.JSON, session?.user?.id)
            render JSON.encodeAsJSON([message: 'Company updated successfully', company: company])
        } catch (Exception e) {
            response.status = HttpStatus.BAD_REQUEST.value()
            render JSON.encodeAsJSON([message: e.message])
        }
    }

    @CompileStatic(TypeCheckingMode.SKIP)
    def delete(Long id) {
        try {
            employeeService.deleteCompany(id)
            render JSON.encodeAsJSON([message: 'Company deleted successfully'])
        } catch (Exception e) {
            response.status = HttpStatus.BAD_REQUEST.value()
            render JSON.encodeAsJSON([message: e.message])
        }
    }

    @CompileStatic(TypeCheckingMode.SKIP)
    def getSettings(Long id) {
        def settings = companySettingService.listSettings(id)
        render JSON.encodeAsJSON(settings)
    }

    @CompileStatic(TypeCheckingMode.SKIP)
    def updateSetting(Long companyId, String key) {
        try {
            def setting = companySettingService.updateSetting(companyId, key, request.JSON)
            render JSON.encodeAsJSON(setting)
        } catch (Exception e) {
            response.status = HttpStatus.BAD_REQUEST.value()
            render JSON.encodeAsJSON([message: e.message])
        }
    }
}