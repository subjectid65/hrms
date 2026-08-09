package hrms

import grails.gorm.transactions.Transactional
import groovy.transform.CompileStatic
import groovy.transform.TypeCheckingMode
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Controller
import grails.converters.JSON

@Controller
@CompileStatic
@Transactional
class ExpenseController {

    ExpenseService expenseService

    def index() {
        render view: 'index', model: [title: 'HRMS - Expense Management']
    }

    @CompileStatic(TypeCheckingMode.SKIP)
    def list(Long companyId) {
        def expenses = expenseService.listExpenses(companyId, params)
        def total = expenseService.countExpenses(companyId, params)
        render JSON.encodeAsJSON([expenses: expenses, total: total, offset: params.offset, max: params.max])
    }

    @CompileStatic(TypeCheckingMode.SKIP)
    def show(Long companyId, Long id) {
        def expense = expenseService.getExpenseById(id)
        if (!expense || expense.employee.company.id != companyId) {
            response.status = HttpStatus.NOT_FOUND.value()
            render JSON.encodeAsJSON([message: 'Expense not found'])
            return
        }
        render JSON.encodeAsJSON(expense)
    }

    @CompileStatic(TypeCheckingMode.SKIP)
    def create(Long companyId) {
        try {
            def expense = expenseService.createExpense(companyId, request.JSON, session?.user?.id)
            response.status = HttpStatus.CREATED.value()
            render JSON.encodeAsJSON([message: 'Expense created successfully', expense: expense])
        } catch (Exception e) {
            response.status = HttpStatus.BAD_REQUEST.value()
            render JSON.encodeAsJSON([message: e.message])
        }
    }

    @CompileStatic(TypeCheckingMode.SKIP)
    def approve(Long companyId, Long id) {
        try {
            def expense = expenseService.approveExpense(id, request.JSON, session?.user?.id)
            render JSON.encodeAsJSON([message: 'Expense approved', expense: expense])
        } catch (Exception e) {
            response.status = HttpStatus.BAD_REQUEST.value()
            render JSON.encodeAsJSON([message: e.message])
        }
    }

    @CompileStatic(TypeCheckingMode.SKIP)
    def reject(Long companyId, Long id) {
        try {
            def expense = expenseService.rejectExpense(id, params.reason, session?.user?.id)
            render JSON.encodeAsJSON([message: 'Expense rejected', expense: expense])
        } catch (Exception e) {
            response.status = HttpStatus.BAD_REQUEST.value()
            render JSON.encodeAsJSON([message: e.message])
        }
    }

    @CompileStatic(TypeCheckingMode.SKIP)
    def pay(Long companyId, Long id) {
        try {
            def expense = expenseService.processPayment(id, params.paymentMethod, session?.user?.id)
            render JSON.encodeAsJSON([message: 'Expense paid', expense: expense])
        } catch (Exception e) {
            response.status = HttpStatus.BAD_REQUEST.value()
            render JSON.encodeAsJSON([message: e.message])
        }
    }

    @CompileStatic(TypeCheckingMode.SKIP)
    def summary(Long companyId, Integer year, Integer month) {
        def summary = expenseService.getExpenseSummary(companyId, year, month)
        render JSON.encodeAsJSON(summary)
    }
}