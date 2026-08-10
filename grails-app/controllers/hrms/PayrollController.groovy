package hrms

import grails.gorm.transactions.Transactional
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Controller

@Controller
@Transactional
class PayrollController {

    PayrollService payrollService

    def index() {
        render view: 'index', model: [title: 'HRMS - Payroll Management']
    }

    def listComponents(Long companyId) {
        def components = payrollService.listSalaryComponents(companyId)
        def serialized = components.collect { c -> [id: c.id, name: c.name, type: c.type, amount: c.amount, isTaxable: c.isTaxable] }
        render contentType: 'application/json', text: new groovy.json.JsonOutput().toJson(serialized)
    }

    def createComponent(Long companyId) {
        try {
            def component = payrollService.createSalaryComponent(companyId, request.JSON, session?.user?.id)
            response.status = HttpStatus.CREATED.value()
            render contentType: 'application/json', text: new groovy.json.JsonOutput().toJson([message: 'Salary component created', component: [id: component.id, name: component.name, type: component.type]])
        } catch (Exception e) {
            response.status = HttpStatus.BAD_REQUEST.value()
            render contentType: 'application/json', text: new groovy.json.JsonOutput().toJson([message: e.message])
        }
    }

    def listPayslips(Long companyId, Integer year, Integer month) {
        def payslips = payrollService.listPayslips(companyId, params)
        def total = payrollService.countPayslips(companyId, params)
        def serialized = payslips.collect { p -> [id: p.id, employee: p.employee ? [id: p.employee.id, firstName: p.employee.firstName, lastName: p.employee.lastName] : null, month: p.month, year: p.year, grossSalary: p.grossSalary, totalDeductions: p.totalDeductions, netSalary: p.netSalary, status: p.status] }
        render contentType: 'application/json', text: new groovy.json.JsonOutput().toJson([payslips: serialized, total: total])
    }

    def show(Long companyId, Long id) {
        def payslip = payrollService.getPayslipById(id)
        if (!payslip || payslip.employee.company.id != companyId) {
            response.status = HttpStatus.NOT_FOUND.value()
            render contentType: 'application/json', text: new groovy.json.JsonOutput().toJson([message: 'Payslip not found'])
            return
        }
        render contentType: 'application/json', text: new groovy.json.JsonOutput().toJson([id: payslip.id, employee: payslip.employee ? [id: payslip.employee.id, firstName: payslip.employee.firstName, lastName: payslip.employee.lastName] : null, month: payslip.month, year: payslip.year, grossSalary: payslip.grossSalary, totalDeductions: payslip.totalDeductions, netSalary: payslip.netSalary, status: payslip.status])
    }

    def generate(Long companyId, Long employeeId, Integer year, Integer month) {
        try {
            def payslip = payrollService.generatePayslip(employeeId, year, month, session?.user?.id)
            response.status = HttpStatus.CREATED.value()
            render contentType: 'application/json', text: new groovy.json.JsonOutput().toJson([message: 'Payslip generated', payslip: [id: payslip.id, month: payslip.month, year: payslip.year, netSalary: payslip.netSalary, status: payslip.status]])
        } catch (Exception e) {
            response.status = HttpStatus.BAD_REQUEST.value()
            render contentType: 'application/json', text: new groovy.json.JsonOutput().toJson([message: e.message])
        }
    }

    def approve(Long id) {
        try {
            def payslip = payrollService.approvePayslip(id)
            render contentType: 'application/json', text: new groovy.json.JsonOutput().toJson([message: 'Payslip approved', payslip: [id: payslip.id, status: payslip.status]])
        } catch (Exception e) {
            response.status = HttpStatus.BAD_REQUEST.value()
            render contentType: 'application/json', text: new groovy.json.JsonOutput().toJson([message: e.message])
        }
    }

    def reject(Long id) {
        try {
            def payslip = payrollService.rejectPayslip(id, params.reason)
            render contentType: 'application/json', text: new groovy.json.JsonOutput().toJson([message: 'Payslip rejected', payslip: [id: payslip.id, status: payslip.status]])
        } catch (Exception e) {
            response.status = HttpStatus.BAD_REQUEST.value()
            render contentType: 'application/json', text: new groovy.json.JsonOutput().toJson([message: e.message])
        }
    }

    def summary(Long companyId, Integer year, Integer month) {
        def summary = payrollService.getPayrollSummary(companyId, year, month)
        render contentType: 'application/json', text: new groovy.json.JsonOutput().toJson(summary)
    }
}