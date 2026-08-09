package hrms

import groovy.transform.CompileStatic
import groovy.transform.TypeCheckingMode
import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

@CompileStatic
@EqualsAndHashCode(includes = ['authority'])
@ToString(includeNames = true, includePackages = false)
class Authority {

    String authority

    @CompileStatic(TypeCheckingMode.SKIP)
    static constraints = {
        authority blank: false, maxSize: 50, unique: true
    }

    @CompileStatic(TypeCheckingMode.SKIP)
    static mapping = {
        table 'authority'
        id column: 'authority_id', generator: 'native'
        authority column: 'authority'
    }

    static belongsTo = [user: User]

    static hasMany = [users: User]

    String toString() { authority }
}