package hrms

import groovy.transform.CompileStatic
import groovy.transform.TypeCheckingMode
import java.time.LocalDate

@CompileStatic
class Designation {

    String name
    String code
    String description
    Company company
    Department department
    Integer grade
    Integer sortOrder
    Boolean isActive = true
    LocalDate createdAt
    LocalDate updatedAt
    Long createdBy

    @CompileStatic(TypeCheckingMode.SKIP)
    static constraints = {
        name blank: false, maxSize: 150
        code blank: false, maxSize: 20
        description maxSize: 500, nullable: true
        company nullable: false
        department nullable: true
        grade nullable: true
        sortOrder nullable: true
        isActive nullable: false
    }

    @CompileStatic(TypeCheckingMode.SKIP)
    static mapping = {
        table 'designation'
        id column: 'designation_id', generator: 'native'
        name column: 'name'
        code column: 'code'
        description column: 'description'
        company column: 'company_id'
        department column: 'department_id'
        grade column: 'grade'
        sortOrder column: 'sort_order'
        isActive column: 'is_active'
        createdAt column: 'created_at'
        updatedAt column: 'updated_at'
        createdBy column: 'created_by'
        autoTimestamp false
    }

    static belongsTo = [company: Company, department: Department]

    static hasMany = [employees: Employee]

    def beforeInsert() {
        createdAt = LocalDate.now()
        updatedAt = LocalDate.now()
    }

    def beforeUpdate() {
        updatedAt = LocalDate.now()
    }

    String toString() { name }
}