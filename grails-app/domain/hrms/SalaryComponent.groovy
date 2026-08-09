package hrms

import groovy.transform.CompileStatic
import groovy.transform.TypeCheckingMode
import java.time.LocalDate

@CompileStatic
class SalaryComponent {

    String name
    String code
    String description
    String componentType = 'EARNING'
    Company company
    Double defaultValue
    Boolean isStatutory = false
    Boolean isActive = true
    LocalDate createdAt
    LocalDate updatedAt
    Long createdBy

    @CompileStatic(TypeCheckingMode.SKIP)
    static constraints = {
        name blank: false, maxSize: 100
        code blank: false, maxSize: 20
        description maxSize: 500, nullable: true
        componentType blank: false, maxSize: 20
        defaultValue nullable: true
        isStatutory nullable: false
        company nullable: false
        isActive nullable: false
    }

    @CompileStatic(TypeCheckingMode.SKIP)
    static mapping = {
        table 'salary_component'
        id column: 'salary_component_id', generator: 'native'
        name column: 'name'
        code column: 'code'
        description column: 'description'
        componentType column: 'component_type'
        defaultValue column: 'default_value'
        isStatutory column: 'is_statutory'
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

    String toString() { "${name} (${code})" }
}