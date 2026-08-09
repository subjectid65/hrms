package hrms

import groovy.transform.CompileStatic
import groovy.transform.TypeCheckingMode
import java.math.BigDecimal

@CompileStatic
class PayslipComponent {

    Payslip payslip
    SalaryComponent salaryComponent
    BigDecimal amount
    String remarks

    @CompileStatic(TypeCheckingMode.SKIP)
    static constraints = {
        payslip nullable: false
        salaryComponent nullable: false
        amount nullable: true
        remarks maxSize: 500, nullable: true
    }

    @CompileStatic(TypeCheckingMode.SKIP)
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