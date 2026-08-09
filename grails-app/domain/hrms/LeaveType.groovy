package hrms

import groovy.transform.CompileStatic
import groovy.transform.TypeCheckingMode
import java.time.LocalDate

@CompileStatic
class LeaveType {

    String name
    String code
    String description
    Integer maxDaysPerYear
    Boolean isPaid = true
    Boolean requiresApproval = true
    Boolean accrues = false
    Integer accrualRate
    Company company
    Boolean isActive = true
    LocalDate createdAt
    LocalDate updatedAt
    Long createdBy

    @CompileStatic(TypeCheckingMode.SKIP)
    static constraints = {
        name blank: false, maxSize: 100
        code blank: false, maxSize: 20
        description maxSize: 500, nullable: true
        maxDaysPerYear nullable: true
        isPaid nullable: false
        requiresApproval nullable: false
        accrues nullable: false
        accrualRate nullable: true
        company nullable: false
        isActive nullable: false
    }

    @CompileStatic(TypeCheckingMode.SKIP)
    static mapping = {
        table 'leave_type'
        id column: 'leave_type_id', generator: 'native'
        name column: 'name'
        code column: 'code'
        description column: 'description'
        maxDaysPerYear column: 'max_days_per_year'
        isPaid column: 'is_paid'
        requiresApproval column: 'requires_approval'
        accrues column: 'accrues'
        accrualRate column: 'accrual_rate'
        company column: 'company_id'
        isActive column: 'is_active'
        createdAt column: 'created_at'
        updatedAt column: 'updated_at'
        createdBy column: 'created_by'
        autoTimestamp false
    }

    static belongsTo = [company: Company]

    def beforeInsert() {
        createdAt = LocalDate.now()
        updatedAt = LocalDate.now()
    }

    def beforeUpdate() {
        updatedAt = LocalDate.now()
    }

    String toString() { name }
}