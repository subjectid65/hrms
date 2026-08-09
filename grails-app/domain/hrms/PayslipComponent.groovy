package hrms

import java.math.BigDecimal

class PayslipComponent {

    Payslip payslip
    SalaryComponent salaryComponent
    BigDecimal amount
    String remarks

    static constraints = {
        payslip nullable: false
        salaryComponent nullable: false
        amount nullable: true
        remarks maxSize: 500, nullable: true
    }

    static mapping = {
        table 'payslip_component'
        id column: 'payslip_component_id', generator: 'native'
        payslip column: 'payslip_id'
        salaryComponent column: 'salary_component_id'
        amount column: 'amount', type: 'big_decimal'
        remarks column: 'remarks'
    }

    static belongsTo = [payslip: Payslip, salaryComponent: SalaryComponent]

    String toString() {
        return "${salaryComponent}: ${amount}"
    }
}