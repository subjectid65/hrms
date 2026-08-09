package hrms

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

@EqualsAndHashCode(includes = ['authority'])
@ToString(includeNames = true)
class Authority {

    String authority

    static constraints = {
        authority blank: false, maxSize: 50, unique: true
    }

    static mapping = {
        table 'authority'
        id column: 'authority_id', generator: 'native'
        authority column: 'authority'
    }


    String toString() { authority }
}