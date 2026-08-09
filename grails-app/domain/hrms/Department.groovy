package hrms

import groovy.transform.CompileStatic
import groovy.transform.TypeCheckingMode
import java.time.LocalDate

@CompileStatic
class Department {

    String name
    String code
    String description
    Company company
    Department parentDepartment
    Integer sortOrder
    Boolean isActive = true
    LocalDate createdAt
    LocalDate updatedAt
    Long createdBy

    static transients = []

    @CompileStatic(TypeCheckingMode.SKIP)
    static constraints = {
        name blank: false, maxSize: 100
        code blank: false, maxSize: 20
        description maxSize: 500, nullable: true
        company nullable: false
        parentDepartment nullable: true
        sortOrder nullable: true
        isActive nullable: false
        code validator: @CompileStatic(TypeCheckingMode.SKIP) { val, obj ->
            if (Department.countByCompanyAndCode(obj.company, val) > 0) {
                return 'department.code.exists'
            }
        }
    }

    @CompileStatic(TypeCheckingMode.SKIP)
    static mapping = {
        table 'department'
        id column: 'department_id', generator: 'native'
        name column: 'name'
        code column: 'code'
        description column: 'description'
        company column: 'company_id'
        parentDepartment column: 'parent_department_id'
        sortOrder column: 'sort_order'
        isActive column: 'is_active'
        createdAt column: 'created_at'
        updatedAt column: 'updated_at'
        createdBy column: 'created_by'
        autoTimestamp false
    }

    static belongsTo = [company: Company, parentDepartment: Department]

    static hasMany = [subDepartments: Department, employees: Employee]

    def beforeInsert() {
        createdAt = LocalDate.now()
        updatedAt = LocalDate.now()
    }

    def beforeUpdate() {
        updatedAt = LocalDate.now()
    }

    String toString() { name }
}