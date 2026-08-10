package hrms

import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.time.LocalDate

@Transactional
class ExpenseService {

    def createExpense(Long companyId, Map<String, Object> data, Long createdBy) {
        Expense expense = new Expense(
            employee: Employee.get(data.employeeId),
            expenseType: data.expenseType,
            description: data.description,
            amount: data.amount ? new BigDecimal(data.amount.toString()) : null,
            currency: data.currency ?: 'AED',
            expenseDate: data.expenseDate ? LocalDate.parse(data.expenseDate) : LocalDate.now(),
            claimDate: data.claimDate ? LocalDate.parse(data.claimDate) : LocalDate.now(),
            receiptUrl: data.receiptUrl,
            status: 'PENDING',
            isActive: true,
            createdBy: createdBy
        )
        expense.save(flush: true, failOnError: true)
        return expense
    }

    def listExpenses(Long companyId, Map params = [:]) {
        Expense.withCriteria {
            eq('employee.company', Company.get(companyId))
            if (params.employeeId) {
                eq('employee', Employee.get(params.employeeId))
            }
            if (params.status) {
                eq('status', params.status)
            }
            if (params.expenseType) {
                eq('expenseType', params.expenseType)
            }
            if (params.dateFrom) {
                gte('expenseDate', LocalDate.parse(params.dateFrom))
            }
            if (params.dateTo) {
                lte('expenseDate', LocalDate.parse(params.dateTo))
            }
            order('expenseDate', 'desc')
            firstResult params.offset ?: 0
            maxResults params.max ?: 20
        }
    }

    def countExpenses(Long companyId, Map params = [:]) {
        def company = Company.findById(companyId)
        if (!company) {
            def all = Company.findAll()
            if (all.isEmpty()) return 0
            company = all.get(0)
        }
        def q = [employee: Employee.findAll { company == it.company }]
        if (params.employeeId) q.employee = Employee.get(params.employeeId)
        if (params.status) q.status = params.status
        if (params.expenseType) q.expenseType = params.expenseType
        if (params.dateFrom || params.dateTo) {
            def from = params.dateFrom ? LocalDate.parse(params.dateFrom) : LocalDate.MIN
            def to = params.dateTo ? LocalDate.parse(params.dateTo) : LocalDate.MAX
            q.expenseDate = [from: from, to: to]
        }
        return Expense.findAll(q)?.size() ?: 0
    }

    def getExpenseById(Long id) {
        return Expense.get(id)
    }

    def approveExpense(Long id, Map<String, Object> data, Long approvedBy) {
        Expense expense = Expense.get(id)
        if (!expense) {
            throw new NoSuchElementException("Expense not found: ${id}")
        }
        expense.status = 'APPROVED'
        expense.approvedAmount = data.approvedAmount ? new BigDecimal(data.approvedAmount.toString()) : expense.amount
        expense.approvedDate = LocalDate.now()
        expense.approvedBy = User.get(approvedBy)
        expense.remarks = data.remarks
        expense.save(flush: true, failOnError: true)
        return expense
    }

    def rejectExpense(Long id, String reason, Long approvedBy) {
        Expense expense = Expense.get(id)
        if (!expense) {
            throw new NoSuchElementException("Expense not found: ${id}")
        }
        expense.status = 'REJECTED'
        expense.rejectionReason = reason
        expense.approvedBy = User.get(approvedBy)
        expense.save(flush: true, failOnError: true)
        return expense
    }

    def processPayment(Long id, String paymentMethod, Long approvedBy) {
        Expense expense = Expense.get(id)
        if (!expense) {
            throw new NoSuchElementException("Expense not found: ${id}")
        }
        expense.status = 'PAID'
        expense.paymentMethod = paymentMethod
        expense.paymentDate = LocalDate.now()
        expense.save(flush: true, failOnError: true)
        return expense
    }

    def getExpenseSummary(Long companyId, Integer year, Integer month) {
        LocalDate from = LocalDate.of(year, month, 1)
        LocalDate to = from.plusMonths(1).minusDays(1)

        def summary = [
            totalClaims: BigDecimal.ZERO,
            totalApproved: BigDecimal.ZERO,
            totalPaid: BigDecimal.ZERO,
            pendingCount: 0,
            approvedCount: 0,
            paidCount: 0
        ]

        List<Expense> expenses = Expense.findAll {
            eq('employee.company', Company.get(companyId))
            between('expenseDate', from, to)
        }

        for (Expense e : expenses) {
            if (e.amount) {
                summary.totalClaims = summary.totalClaims.add(e.amount)
            }
            if (e.approvedAmount) {
                summary.totalApproved = summary.totalApproved.add(e.approvedAmount)
            }
            if (e.status == 'PAID' && e.approvedAmount) {
                summary.totalPaid = summary.totalPaid.add(e.approvedAmount)
            }
            if (e.status == 'PENDING') summary.pendingCount++
            if (e.status == 'APPROVED') summary.approvedCount++
            if (e.status == 'PAID') summary.paidCount++
        }

        return summary
    }
}