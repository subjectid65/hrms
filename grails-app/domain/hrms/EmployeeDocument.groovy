package hrms

import groovy.transform.CompileStatic
import groovy.transform.TypeCheckingMode
import java.time.LocalDate

@CompileStatic
class EmployeeDocument {

    Employee employee
    String documentType
    String documentName
    String documentNumber
    String issueDate
    String expiryDate
    String issuingAuthority
    String documentUrl
    String remarks
    LocalDate createdAt
    LocalDate updatedAt
    Long createdBy

    @CompileStatic(TypeCheckingMode.SKIP)
    static constraints = {
        employee nullable: false
        documentType blank: false, maxSize: 100
        documentName maxSize: 200, nullable: true
        documentNumber maxSize: 100, nullable: true
        issueDate nullable: true
        expiryDate nullable: true
        issuingAuthority maxSize: 200, nullable: true
        documentUrl maxSize: 500, nullable: true
        remarks maxSize: 500, nullable: true
    }

    @CompileStatic(TypeCheckingMode.SKIP)
    static mapping = {
        table 'employee_document'
        id column: 'employee_document_id', generator: 'native'
        employee column: 'employee_id'
        documentType column: 'document_type'
        documentName column: 'document_name'
        documentNumber column: 'document_number'
        issueDate column: 'issue_date', type: 'date'
        expiryDate column: 'expiry_date', type: 'date'
        issuingAuthority column: 'issuing_authority'
        documentUrl column: 'document_url'
        remarks column: 'remarks'
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

    String toString() { "${employee}: ${documentType}" }
}