package hrms

import groovy.transform.CompileStatic
import java.time.LocalDate
import java.math.BigDecimal

@CompileStatic
class Payslip {

    Employee employee
    Integer year
    Integer month
    BigDecimal grossSalary
    BigDecimal totalDeductions
    BigDecimal netSalary
    String status = 'DRAFT'
    LocalDate paymentDate
    String remarks
    Boolean isGenerated = false
    LocalDate createdAt
    LocalDate updatedAt
    Long createdBy

    static constraints = {
        employee nullable: false
        year nullable: false
        month nullable: false
        grossSalary nullable: true
        totalDeductions nullable: true
        netSalary nullable: true
        status blank: false, maxSize: 20
        paymentDate nullable: true
        remarks maxSize: 1000, nullable: true
        isGenerated nullable: false
    }

    static mapping = {
        table 'payslip'
        id column: 'payslip_id', generator: 'native'
        employee column: 'employee_id'
        year column: 'year'
        month column: 'month'
        grossSalary column: 'gross_salary', type: 'big_decimal'
        totalDeductions column: 'total_deductions', type: 'big_decimal'
        netSalary column: 'net_salary', type: 'big_decimal'
        status column: 'status'
        paymentDate column: 'payment_date', type: 'date'
        remarks column: 'remarks'
        isGenerated column: 'is_generated'
        createdAt column: 'created_at'
        updatedAt column: 'updated_at'
        createdBy column: 'created_by'
        autoTimestamp false
    }

    static belongsTo = [employee: Employee]

    static hasMany = [payslipComponents: PayslipComponent]

    def beforeInsert() {
        createdAt = LocalDate.now()
        updatedAt = LocalDate.now()
    }

    def beforeUpdate() {
        updatedAt = LocalDate.now()
    }

    String toString() {
        return "${employee}: ${month}/${year}"
    }
}