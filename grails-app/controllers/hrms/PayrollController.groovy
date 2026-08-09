package hrms

import grails.gorm.transactions.Transactional
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Controller
import grails.converters.JSON

@Controller
@Transactional
class PayrollController {

    PayrollService payrollService

    def index() {
        render view: 'index', model: [title: 'HRMS - Payroll Management']
    }

    def listComponents(Long companyId) {
        def components = payrollService.listSalaryComponents(companyId)
        render JSON.encodeAsJSON(components)
    }

    def createComponent(Long companyId) {
        try {
            def component = payrollService.createSalaryComponent(companyId, request.JSON, session?.user?.id)
            response.status = HttpStatus.CREATED.value()
            render JSON.encodeAsJSON([message: 'Salary component created', component: component])
        } catch (Exception e) {
            response.status = HttpStatus.BAD_REQUEST.value()
            render JSON.encodeAsJSON([message: e.message])
        }
    }

    def listPayslips(Long companyId, Integer year, Integer month) {
        def payslips = payrollService.listPayslips(companyId, params)
        def total = payrollService.countPayslips(companyId, params)
        render JSON.encodeAsJSON([payslips: payslips, total: total])
    }

    def show(Long companyId, Long id) {
        def payslip = payrollService.getPayslipById(id)
        if (!payslip || payslip.employee.company.id != companyId) {
            response.status = HttpStatus.NOT_FOUND.value()
            render JSON.encodeAsJSON([message: 'Payslip not found'])
            return
        }
        render JSON.encodeAsJSON(payslip)
    }

    def generate(Long companyId, Long employeeId, Integer year, Integer month) {
        try {
            def payslip = payrollService.generatePayslip(employeeId, year, month, session?.user?.id)
            response.status = HttpStatus.CREATED.value()
            render JSON.encodeAsJSON([message: 'Payslip generated', payslip: payslip])
        } catch (Exception e) {
            response.status = HttpStatus.BAD_REQUEST.value()
            render JSON.encodeAsJSON([message: e.message])
        }
    }

    def approve(Long id) {
        try {
            def payslip = payrollService.approvePayslip(id)
            render JSON.encodeAsJSON([message: 'Payslip approved', payslip: payslip])
        } catch (Exception e) {
            response.status = HttpStatus.BAD_REQUEST.value()
            render JSON.encodeAsJSON([message: e.message])
        }
    }

    def reject(Long id) {
        try {
            def payslip = payrollService.rejectPayslip(id, params.reason)
            render JSON.encodeAsJSON([message: 'Payslip rejected', payslip: payslip])
        } catch (Exception e) {
            response.status = HttpStatus.BAD_REQUEST.value()
            render JSON.encodeAsJSON([message: e.message])
        }
    }

    def summary(Long companyId, Integer year, Integer month) {
        def summary = payrollService.getPayrollSummary(companyId, year, month)
        render JSON.encodeAsJSON(summary)
    }
}