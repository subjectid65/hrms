package hrms

import groovy.transform.CompileStatic

@CompileStatic
class UserAuthority {

    User user
    Authority authority

    static constraints = {
        user nullable: false
        authority nullable: false
    }

    static mapping = {
        table 'user_authority'
        id column: 'user_authority_id', generator: 'native'
        user column: 'user_id'
        authority column: 'authority_id'
    }

    static belongsTo = [user: User, authority: Authority]

    String toString() { "${user} - ${authority}" }
}