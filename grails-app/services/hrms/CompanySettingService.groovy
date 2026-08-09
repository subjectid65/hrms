package hrms

import groovy.transform.CompileStatic
import groovy.transform.TypeCheckingMode
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@CompileStatic
@Transactional
class CompanySettingService {

    @CompileStatic(TypeCheckingMode.SKIP)
    def listSettings(Long companyId) {
        return CompanySetting.findAll {
            eq('company', Company.get(companyId))
            order('settingKey', 'asc')
        }
    }

    @CompileStatic(TypeCheckingMode.SKIP)
    def getSetting(Long companyId, String key) {
        return CompanySetting.find {
            eq('company', Company.get(companyId))
            eq('settingKey', key)
        }
    }

    @CompileStatic(TypeCheckingMode.SKIP)
    def updateSetting(Long companyId, String key, Map<String, Object> data) {
        CompanySetting setting = CompanySetting.find {
            eq('company', Company.get(companyId))
            eq('settingKey', key)
        }

        if (!setting) {
            setting = new CompanySetting(
                company: Company.get(companyId),
                settingKey: key,
                settingValue: data.value?.toString(),
                settingType: data.type ?: 'STRING',
                description: data.description,
                isSystemSetting: data.isSystemSetting ?: false,
                createdBy: data.createdBy
            )
            setting.save(flush: true, failOnError: true)
        } else {
            setting.settingValue = data.value?.toString()
            setting.updatedAt = LocalDate.now()
            setting.save(flush: true, failOnError: true)
        }
        return setting
    }

    def getDefaultSettings(Long companyId) {
        return [
            ['key': 'company_name', 'value': null, 'type': 'STRING', 'description': 'Company display name'],
            ['key': 'logo_url', 'value': null, 'type': 'STRING', 'description': 'Company logo URL'],
            ['key': 'primary_color', 'value': '#1e3a5f', 'type': 'STRING', 'description': 'Primary brand color'],
            ['key': 'secondary_color', 'value': '#2d7dd2', 'type': 'STRING', 'description': 'Secondary brand color'],
            ['key': 'timezone', 'value': 'Asia/Dubai', 'type': 'STRING', 'description': 'Company timezone'],
            ['key': 'currency', 'value': 'AED', 'type': 'STRING', 'description': 'Default currency'],
            ['key': 'language', 'value': 'en', 'type': 'STRING', 'description': 'Default language'],
            ['key': 'working_hours_start', 'value': '09:00', 'type': 'STRING', 'description': 'Working hours start'],
            ['key': 'working_hours_end', 'value': '18:00', 'type': 'STRING', 'description': 'Working hours end'],
            ['key': 'working_days', 'value': 'Mon,Tue,Wed,Thu,Fri', 'type': 'STRING', 'description': 'Working days'],
            ['key': 'leave_encashment_rate', 'value': '1.0', 'type': 'DECIMAL', 'description': 'Leave encashment rate'],
            ['key': 'overtime_multiplier', 'value': '1.25', 'type': 'DECIMAL', 'description': 'Overtime pay multiplier'],
            ['key': 'vat_rate', 'value': '0.05', 'type': 'DECIMAL', 'description': 'VAT rate (UAE: 5%)'],
            ['key': 'provident_fund_rate', 'value': '0.125', 'type': 'DECIMAL', 'description': 'Provident fund contribution rate'],
            ['key': 'end_of_service_rate', 'value': '0.021', 'type': 'DECIMAL', 'description': 'EOSB calculation rate']
        ]
    }

    @CompileStatic(TypeCheckingMode.SKIP)
    def initializeDefaultSettings(Long companyId) {
        List<Map> defaults = getDefaultSettings(companyId)
        Company company = Company.get(companyId)
        for (Map setting : defaults) {
            CompanySetting existing = CompanySetting.find {
                eq('company', company)
                eq('settingKey', setting.key)
            }
            if (!existing) {
                CompanySetting cs = new CompanySetting(
                    company: company,
                    settingKey: setting.key,
                    settingValue: setting.value?.toString(),
                    settingType: setting.type,
                    description: setting.description,
                    isSystemSetting: true
                )
                cs.save(flush: false)
            }
        }
    }
}