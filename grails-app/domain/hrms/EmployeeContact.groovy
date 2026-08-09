package hrms

import groovy.transform.CompileStatic
import groovy.transform.TypeCheckingMode
import java.time.LocalDate

@CompileStatic
class EmployeeContact {

    Employee employee
    String relation
    String name
    String phone
    String email
    String address
    String city
    String country
    String postalCode
    Boolean isPrimary = false
    LocalDate createdAt
    LocalDate updatedAt
    Long createdBy

    @CompileStatic(TypeCheckingMode.SKIP)
    static constraints = {
        employee nullable: false
        relation maxSize: 50
        name maxSize: 200
        phone maxSize: 30, nullable: true
        email format: '^[A-Za-z0-9+_.-]+@(.+)$', maxSize: 100, nullable: true
        address maxSize: 500, nullable: true
        city maxSize: 100, nullable: true
        country maxSize: 100, nullable: true
        postalCode maxSize: 20, nullable: true
        isPrimary nullable: false
    }

    @CompileStatic(TypeCheckingMode.SKIP)
    static mapping = {
        table 'employee_contact'
        id column: 'employee_contact_id', generator: 'native'
        employee column: 'employee_id'
        relation column: 'relation'
        name column: 'name'
        phone column: 'phone'
        email column: 'email'
        address column: 'address'
        city column: 'city'
        country column: 'country'
        postalCode column: 'postal_code'
        isPrimary column: 'is_primary'
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

    String toString() { "${employee}: ${relation} - ${name}" }
}