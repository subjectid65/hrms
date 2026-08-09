package hrms

import groovy.transform.CompileStatic
import java.time.LocalDate

@CompileStatic
class OnboardingTask {

    String name
    String description
    Employee employee
    String assignedTo
    String priority = 'MEDIUM'
    String status = 'PENDING'
    LocalDate dueDate
    LocalDate completedDate
    LocalDate createdAt
    LocalDate updatedAt
    Long createdBy

    static constraints = {
        name blank: false, maxSize: 200
        description maxSize: 1000, nullable: true
        employee nullable: false
        assignedTo maxSize: 100, nullable: true
        priority blank: false, maxSize: 20
        status blank: false, maxSize: 20
        dueDate nullable: true
        completedDate nullable: true
    }

    static mapping = {
        table 'onboarding_task'
        id column: 'onboarding_task_id', generator: 'native'
        name column: 'name'
        description column: 'description'
        employee column: 'employee_id'
        assignedTo column: 'assigned_to'
        priority column: 'priority'
        status column: 'status'
        dueDate column: 'due_date', type: 'date'
        completedDate column: 'completed_date', type: 'date'
        createdAt column: 'created_at'
        updatedAt column: 'updated_at'
        createdBy column: 'created_by'
        autoTimestamp false
    }

    static belongsTo = [employee: Employee]

    def beforeInsert() {
        createdAt = LocalDate.now()
        updatedAt = LocalDate.now()
    }

    def beforeUpdate() {
        updatedAt = LocalDate.now()
    }

    String toString() { name }
}