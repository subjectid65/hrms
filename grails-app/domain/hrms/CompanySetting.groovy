package hrms

import groovy.transform.CompileStatic
import groovy.transform.TypeCheckingMode
import java.time.LocalDate

@CompileStatic
class CompanySetting {

    Company company
    String settingKey
    String settingValue
    String settingType = 'STRING'
    String description
    Boolean isSystemSetting = false
    LocalDate createdAt
    LocalDate updatedAt
    Long createdBy

    @CompileStatic(TypeCheckingMode.SKIP)
    static constraints = {
        company nullable: false
        settingKey blank: false, maxSize: 100
        settingValue maxSize: 2000, nullable: true
        settingType blank: false, maxSize: 50
        description maxSize: 500, nullable: true
        isSystemSetting nullable: false
    }

    @CompileStatic(TypeCheckingMode.SKIP)
    static mapping = {
        table 'company_setting'
        id column: 'company_setting_id', generator: 'native'
        company column: 'company_id'
        settingKey column: 'setting_key'
        settingValue column: 'setting_value'
        settingType column: 'setting_type'
        description column: 'description'
        isSystemSetting column: 'is_system_setting'
        createdAt column: 'created_at'
        updatedAt column: 'updated_at'
        createdBy column: 'created_by'
        autoTimestamp false
    }

    static belongsTo = [company: Company]

    static hasMany = [companySettings: CompanySetting]

    def beforeInsert() {
        createdAt = LocalDate.now()
        updatedAt = LocalDate.now()
    }

    def beforeUpdate() {
        updatedAt = LocalDate.now()
    }

    String toString() { "${settingKey}: ${settingValue}" }
}