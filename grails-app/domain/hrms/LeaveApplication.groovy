package hrms

import groovy.transform.CompileStatic
import groovy.transform.TypeCheckingMode
import java.time.LocalDate

@CompileStatic
class LeaveApplication {

    Employee employee
    String leaveType
    LocalDate fromDate
    LocalDate toDate
    Integer totalDays
    String reason
    String attachedDocuments
    String status = 'PENDING'
    String rejectedReason
    LocalDate approvedDate
    LocalDate rejectedDate
    User approvedBy
    LocalDate createdAt
    LocalDate updatedAt
    Long createdBy

    @CompileStatic(TypeCheckingMode.SKIP)
    static constraints = {
        employee nullable: false
        leaveType blank: false, maxSize: 50
        fromDate nullable: false
        toDate nullable: false
        totalDays nullable: true
        reason maxSize: 1000, nullable: true
        attachedDocuments maxSize: 500, nullable: true
        status blank: false, maxSize: 20
        rejectedReason maxSize: 1000, nullable: true
        approvedDate nullable: true
        rejectedDate nullable: true
        approvedBy nullable: true
    }

    @CompileStatic(TypeCheckingMode.SKIP)
    static mapping = {
        table 'leave_application'
        id column: 'leave_application_id', generator: 'native'
        employee column: 'employee_id'
        leaveType column: 'leave_type'
        fromDate column: 'from_date', type: 'date'
        toDate column: 'to_date', type: 'date'
        totalDays column: 'total_days'
        reason column: 'reason'
        attachedDocuments column: 'attached_documents'
        status column: 'status'
        rejectedReason column: 'rejected_reason'
        approvedDate column: 'approved_date', type: 'date'
        rejectedDate column: 'rejected_date', type: 'date'
        approvedBy column: 'approved_by'
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
        return "${employee}: ${leaveType} (${fromDate} to ${toDate})"
    }
}