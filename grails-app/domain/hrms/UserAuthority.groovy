package hrms

import groovy.transform.CompileStatic
import groovy.transform.TypeCheckingMode

@CompileStatic
class UserAuthority {

    User user
    Authority authority

    @CompileStatic(TypeCheckingMode.SKIP)
    static constraints = {
        user nullable: false
        authority nullable: false
    }

    @CompileStatic(TypeCheckingMode.SKIP)
    static mapping = {
        table 'user_authority'
        id column: 'user_authority_id', generator: 'native'
        user column: 'user_id'
        authority column: 'authority_id'
    }

    static belongsTo = [user: User, authority: Authority]

    String toString() { "${user} - ${authority}" }
}