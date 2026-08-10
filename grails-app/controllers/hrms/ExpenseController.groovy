package hrms

import grails.gorm.transactions.Transactional
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Controller

@Controller
@Transactional
class ExpenseController {

    ExpenseService expenseService

    def index() {
        render view: 'index', model: [title: 'HRMS - Expense Management']
    }

    def list(Long companyId) {
        def expenses = expenseService.listExpenses(companyId, params)
        def total = expenseService.countExpenses(companyId, params)
        def serialized = expenses.collect { e -> [id: e.id, employee: e.employee ? [id: e.employee.id, firstName: e.employee.firstName, lastName: e.employee.lastName] : null, expenseType: e.expenseType, amount: e.amount, expenseDate: e.expenseDate, description: e.description, status: e.status] }
        render contentType: 'application/json', text: new groovy.json.JsonOutput().toJson([expenses: serialized, total: total, offset: params.offset, max: params.max])
    }

    def show(Long companyId, Long id) {
        def expense = expenseService.getExpenseById(id)
        if (!expense || expense.employee.company.id != companyId) {
            response.status = HttpStatus.NOT_FOUND.value()
            render contentType: 'application/json', text: new groovy.json.JsonOutput().toJson([message: 'Expense not found'])
            return
        }
        render contentType: 'application/json', text: new groovy.json.JsonOutput().toJson([id: expense.id, employee: expense.employee ? [id: expense.employee.id, firstName: expense.employee.firstName, lastName: expense.employee.lastName] : null, expenseType: expense.expenseType, amount: expense.amount, expenseDate: expense.expenseDate, description: expense.description, status: expense.status])
    }

    def create(Long companyId) {
        try {
            def expense = expenseService.createExpense(companyId, request.JSON, session?.user?.id)
            response.status = HttpStatus.CREATED.value()
            render contentType: 'application/json', text: new groovy.json.JsonOutput().toJson([message: 'Expense created successfully', expense: [id: expense.id, expenseType: expense.expenseType, amount: expense.amount, status: expense.status]])
        } catch (Exception e) {
            response.status = HttpStatus.BAD_REQUEST.value()
            render contentType: 'application/json', text: new groovy.json.JsonOutput().toJson([message: e.message])
        }
    }

    def approve(Long companyId, Long id) {
        try {
            def expense = expenseService.approveExpense(id, request.JSON, session?.user?.id)
            render contentType: 'application/json', text: new groovy.json.JsonOutput().toJson([message: 'Expense approved', expense: [id: expense.id, status: expense.status]])
        } catch (Exception e) {
            response.status = HttpStatus.BAD_REQUEST.value()
            render contentType: 'application/json', text: new groovy.json.JsonOutput().toJson([message: e.message])
        }
    }

    def reject(Long companyId, Long id) {
        try {
            def expense = expenseService.rejectExpense(id, params.reason, session?.user?.id)
            render contentType: 'application/json', text: new groovy.json.JsonOutput().toJson([message: 'Expense rejected', expense: [id: expense.id, status: expense.status]])
        } catch (Exception e) {
            response.status = HttpStatus.BAD_REQUEST.value()
            render contentType: 'application/json', text: new groovy.json.JsonOutput().toJson([message: e.message])
        }
    }

    def pay(Long companyId, Long id) {
        try {
            def expense = expenseService.processPayment(id, params.paymentMethod, session?.user?.id)
            render contentType: 'application/json', text: new groovy.json.JsonOutput().toJson([message: 'Expense paid', expense: [id: expense.id, status: expense.status]])
        } catch (Exception e) {
            response.status = HttpStatus.BAD_REQUEST.value()
            render contentType: 'application/json', text: new groovy.json.JsonOutput().toJson([message: e.message])
        }
    }

    def summary(Long companyId, Integer year, Integer month) {
        def summary = expenseService.getExpenseSummary(companyId, year, month)
        render contentType: 'application/json', text: new groovy.json.JsonOutput().toJson(summary)
    }
}