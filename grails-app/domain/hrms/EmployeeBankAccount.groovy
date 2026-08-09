package hrms

import groovy.transform.CompileStatic
import java.time.LocalDate

@CompileStatic
class EmployeeBankAccount {

    Employee employee
    String bankName
    String bankBranch
    String accountHolderName
    String accountNumber
    String iban
    String swiftCode
    String accountType = 'SAVINGS'
    Boolean isPrimary = false
    String remarks
    LocalDate createdAt
    LocalDate updatedAt
    Long createdBy

    static constraints = {
        employee nullable: false
        bankName maxSize: 200
        bankBranch maxSize: 200, nullable: true
        accountHolderName maxSize: 200
        accountNumber maxSize: 50
        iban maxSize: 50, nullable: true
        swiftCode maxSize: 20, nullable: true
        accountType maxSize: 20
        remarks maxSize: 500, nullable: true
        isPrimary nullable: false
    }

    static mapping = {
        table 'employee_bank_account'
        id column: 'employee_bank_account_id', generator: 'native'
        employee column: 'employee_id'
        bankName column: 'bank_name'
        bankBranch column: 'bank_branch'
        accountHolderName column: 'account_holder_name'
        accountNumber column: 'account_number'
        iban column: 'iban'
        swiftCode column: 'swift_code'
        accountType column: 'account_type'
        remarks column: 'remarks'
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

    String toString() { "${employee}: ${bankName} - ${accountNumber}" }
}