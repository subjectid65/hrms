package hrms

import java.time.LocalDate
import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

@EqualsAndHashCode(includes = ['username'])
@ToString(includeNames = true)
class User {

    String username
    String password
    String firstName
    String lastName
    String email
    String phone
    Boolean enabled = true
    Boolean accountNonExpired = true
    Boolean accountNonLocked = true
    Boolean credentialsNonExpired = true
    Boolean isAdmin = false
    Company company
    Employee employee
    LocalDate createdAt
    LocalDate updatedAt
    Long createdBy

    static constraints = {
        username blank: false, maxSize: 50, unique: true
        password blank: false, maxSize: 255
        firstName maxSize: 100
        lastName maxSize: 100
        email format: '^[A-Za-z0-9+_.-]+@(.+)$', maxSize: 100, nullable: true
        phone maxSize: 30, nullable: true
        enabled nullable: false
        accountNonExpired nullable: false
        accountNonLocked nullable: false
        credentialsNonExpired nullable: false
        isAdmin nullable: false
        company nullable: true
        employee nullable: true
    }

    static mapping = {
        table 'usr'
        id column: 'usr_id', generator: 'native'
        username column: 'username'
        password column: 'password'
        firstName column: 'first_name'
        lastName column: 'last_name'
        email column: 'email'
        phone column: 'phone'
        enabled column: 'enabled'
        accountNonExpired column: 'account_non_expired'
        accountNonLocked column: 'account_non_locked'
        credentialsNonExpired column: 'credentials_non_expired'
        isAdmin column: 'is_admin'
        company column: 'company_id'
        employee column: 'employee_id'
        createdAt column: 'created_at'
        updatedAt column: 'updated_at'
        createdBy column: 'created_by'
        autoTimestamp false
    }

    static belongsTo = [company: Company, employee: Employee]

    static hasMany = [authorities: Authority]

    def beforeInsert() {
        createdAt = LocalDate.now()
        updatedAt = LocalDate.now()
    }

    def beforeUpdate() {
        updatedAt = LocalDate.now()
    }

    String getFullName() {
        StringBuilder sb = new StringBuilder()
        if (firstName) sb.append(firstName)
        if (lastName) sb.append(" ").append(lastName)
        return sb.toString().trim()
    }

    String toString() { username }
}