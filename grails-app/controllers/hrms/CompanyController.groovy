package hrms

import grails.gorm.transactions.Transactional
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Controller

@Controller
@Transactional
class CompanyController {

    EmployeeService employeeService
    CompanySettingService companySettingService

    def index() {
        render view: 'index', model: [title: 'HRMS - Company Management']
    }

    def list() {
        def companies = employeeService.listCompanies(params)
        def total = employeeService.countCompanies(params)
        def serialized = companies.collect { c -> [id: c.id, companyName: c.companyName, companyCode: c.companyCode, isActive: c.isActive] }
        render contentType: 'application/json', text: new groovy.json.JsonOutput().toJson([companies: serialized, total: total, offset: params.offset, max: params.max])
    }

    def show(Long id) {
        def company = employeeService.getCompanyById(id)
        if (!company) {
            response.status = HttpStatus.NOT_FOUND.value()
            render contentType: 'application/json', text: new groovy.json.JsonOutput().toJson([message: 'Company not found'])
            return
        }
        render contentType: 'application/json', text: new groovy.json.JsonOutput().toJson([id: company.id, companyName: company.companyName, companyCode: company.companyCode, isActive: company.isActive])
    }

    def create() {
        try {
            def company = employeeService.createCompany(request.JSON, session?.user?.id)
            response.status = HttpStatus.CREATED.value()
            render contentType: 'application/json', text: new groovy.json.JsonOutput().toJson([message: 'Company created successfully', company: [id: company.id, companyName: company.companyName, companyCode: company.companyCode, isActive: company.isActive]])
        } catch (Exception e) {
            response.status = HttpStatus.BAD_REQUEST.value()
            render contentType: 'application/json', text: new groovy.json.JsonOutput().toJson([message: e.message])
        }
    }

    def update(Long id) {
        try {
            def company = employeeService.updateCompany(id, request.JSON, session?.user?.id)
            render contentType: 'application/json', text: new groovy.json.JsonOutput().toJson([message: 'Company updated successfully', company: company])
        } catch (Exception e) {
            response.status = HttpStatus.BAD_REQUEST.value()
            render contentType: 'application/json', text: new groovy.json.JsonOutput().toJson([message: e.message])
        }
    }

    def delete(Long id) {
        try {
            employeeService.deleteCompany(id)
            render contentType: 'application/json', text: new groovy.json.JsonOutput().toJson([message: 'Company deleted successfully'])
        } catch (Exception e) {
            response.status = HttpStatus.BAD_REQUEST.value()
            render contentType: 'application/json', text: new groovy.json.JsonOutput().toJson([message: e.message])
        }
    }

    def seed() {
        if (Company.count() > 0) {
            def c = Company.get(1)
            render contentType: 'application/json', text: new groovy.json.JsonOutput().toJson([message: 'Company already exists', company: [id: c.id, companyName: c.companyName, companyCode: c.companyCode, isActive: c.isActive]])
            return
        }
        try {
            def company = employeeService.createCompany([
                companyName: 'Demo Company LLC',
                companyCode: 'DEMO',
                companyShortName: 'Demo Co',
                legalName: 'Demo Company Limited Liability Company',
                tradeLicenseNumber: 'TL-123456',
                vatRegistrationNumber: '100200300400500',
                crNumber: 'CR-654321',
                address: 'Dubai Internet City',
                city: 'Dubai',
                state: 'Dubai',
                country: 'United Arab Emirates',
                postalCode: '12345',
                phoneNumber: '+971-4-123-4567',
                email: 'admin@democompany.ae',
                currencyCode: 'AED',
                languageCode: 'en',
                timezone: 'Asia/Dubai'
            ], null)

            def settingService = new CompanySettingService()
            settingService.initializeDefaultSettings(company.id)

            response.status = HttpStatus.CREATED.value()
            render contentType: 'application/json', text: new groovy.json.JsonOutput().toJson([message: 'Seed company created successfully', company: [id: company.id, companyName: company.companyName, companyCode: company.companyCode, isActive: company.isActive]])
        } catch (Exception e) {
            response.status = HttpStatus.BAD_REQUEST.value()
            render contentType: 'application/json', text: new groovy.json.JsonOutput().toJson([message: e.message])
        }
    }

    def getSettings(Long id) {
        def settings = companySettingService.listSettings(id)
        render contentType: 'application/json', text: new groovy.json.JsonOutput().toJson(settings)
    }

    def updateSetting(Long companyId, String key) {
        try {
            def setting = companySettingService.updateSetting(companyId, key, request.JSON)
            render contentType: 'application/json', text: new groovy.json.JsonOutput().toJson(setting)
        } catch (Exception e) {
            response.status = HttpStatus.BAD_REQUEST.value()
            render contentType: 'application/json', text: new groovy.json.JsonOutput().toJson([message: e.message])
        }
    }
}