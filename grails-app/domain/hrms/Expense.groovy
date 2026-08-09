package hrms

import groovy.transform.CompileStatic
import java.time.LocalDate
import java.math.BigDecimal

@CompileStatic
class Expense {

    Employee employee
    String expenseType
    String description
    BigDecimal amount
    String currency = "AED"
    LocalDate expenseDate
    LocalDate claimDate
    String receiptUrl
    String status = 'PENDING'
    String rejectionReason
    BigDecimal approvedAmount
    LocalDate approvedDate
    User approvedBy
    String paymentMethod
    LocalDate paymentDate
    String remarks
    Boolean isActive = true
    LocalDate createdAt
    LocalDate updatedAt
    Long createdBy

    static constraints = {
        employee nullable: false
        expenseType blank: false, maxSize: 100
        description maxSize: 1000, nullable: true
        amount nullable: true
        currency maxSize: 3
        expenseDate nullable: false
        claimDate nullable: true
        receiptUrl maxSize: 500, nullable: true
        status blank: false, maxSize: 20
        rejectionReason maxSize: 1000, nullable: true
        approvedAmount nullable: true
        approvedDate nullable: true
        approvedBy nullable: true
        paymentMethod maxSize: 50, nullable: true
        paymentDate nullable: true
        remarks maxSize: 1000, nullable: true
        isActive nullable: false
    }

    static mapping = {
        table 'expense'
        id column: 'expense_id', generator: 'native'
        employee column: 'employee_id'
        expenseType column: 'expense_type'
        description column: 'description'
        amount column: 'amount', type: 'big_decimal'
        currency column: 'currency'
        expenseDate column: 'expense_date', type: 'date'
        claimDate column: 'claim_date', type: 'date'
        receiptUrl column: 'receipt_url'
        status column: 'status'
        rejectionReason column: 'rejection_reason'
        approvedAmount column: 'approved_amount', type: 'big_decimal'
        approvedDate column: 'approved_date', type: 'date'
        approvedBy column: 'approved_by'
        paymentMethod column: 'payment_method'
        paymentDate column: 'payment_date', type: 'date'
        remarks column: 'remarks'
        isActive column: 'is_active'
        createdAt column: 'created_at'
        updatedAt column: 'updated_at'
        createdBy column: 'created_by'
        autoTimestamp false
    }

    static belongsTo = [employee: Employee, approvedBy: User]

    def beforeInsert() {
        createdAt = LocalDate.now()
        updatedAt = LocalDate.now()
    }

    def beforeUpdate() {
        updatedAt = LocalDate.now()
    }

    String toString() {
        return "${employee}: ${expenseType} - ${amount} ${currency}"
    }
}