package hrms

import groovy.transform.CompileStatic
import groovy.transform.TypeCheckingMode
import java.time.LocalDate

@CompileStatic
class Company {

    String companyName
    String companyCode
    String companyShortName
    String legalName
    String tradeLicenseNumber
    String vatRegistrationNumber
    String crNumber
    String incorporationDate
    Integer noOfEmployees
    String address
    String city
    String state
    String country
    String postalCode
    String phoneNumber
    String email
    String website
    String currencyCode = "AED"
    String languageCode = "en"
    String timezone = "Asia/Dubai"
    String fiscalYearStart = "01-01"
    String fiscalYearEnd = "12-31"
    String logoUrl
    String primaryColor = "#1e3a5f"
    String secondaryColor = "#2d7dd2"
    Boolean isMultiBranch = false
    Boolean isActive = true
    LocalDate createdAt
    LocalDate updatedAt
    Long createdBy

    static transients = ['logoUrl', 'primaryColor', 'secondaryColor']

    @CompileStatic(TypeCheckingMode.SKIP)
    static constraints = {
        companyName blank: false, maxSize: 200
        companyCode blank: false, maxSize: 20, unique: true
        companyShortName maxSize: 50
        legalName maxSize: 200
        tradeLicenseNumber maxSize: 50
        vatRegistrationNumber maxSize: 50
        crNumber maxSize: 50
        email format: '^[A-Za-z0-9+_.-]+@(.+)$', nullable: true
        phoneNumber maxSize: 30, nullable: true
        currencyCode maxSize: 3, nullable: false
        languageCode maxSize: 5
        timezone maxSize: 50
        isActive nullable: false
    }

    @CompileStatic(TypeCheckingMode.SKIP)
    static mapping = {
        table 'company'
        id column: 'company_id', generator: 'native'
        companyName column: 'company_name'
        companyCode column: 'company_code'
        companyShortName column: 'company_short_name'
        legalName column: 'legal_name'
        tradeLicenseNumber column: 'trade_license_number'
        vatRegistrationNumber column: 'vat_registration_number'
        crNumber column: 'cr_number'
        incorporationDate column: 'incorporation_date', type: 'date'
        noOfEmployees column: 'no_of_employees'
        currencyCode column: 'currency_code'
        languageCode column: 'language_code'
        timezone column: 'timezone'
        fiscalYearStart column: 'fiscal_year_start'
        fiscalYearEnd column: 'fiscal_year_end'
        logoUrl column: 'logo_url'
        primaryColor column: 'primary_color'
        secondaryColor column: 'secondary_color'
        isMultiBranch column: 'is_multi_branch'
        isActive column: 'is_active'
        createdAt column: 'created_at'
        updatedAt column: 'updated_at'
        createdBy column: 'created_by'
        autoTimestamp false
    }

    static belongsTo = [:]

    def beforeInsert() {
        createdAt = LocalDate.now()
        updatedAt = LocalDate.now()
        if (!companyShortName) companyShortName = companyName
    }

    def beforeUpdate() {
        updatedAt = LocalDate.now()
    }

    String toString() { "${companyName} (${companyCode})" }
}