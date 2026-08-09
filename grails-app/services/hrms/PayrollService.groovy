package hrms

import groovy.transform.CompileStatic
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.time.LocalDate

@CompileStatic
@Transactional
class PayrollService {

    def createSalaryComponent(Long companyId, Map<String, Object> data, Long createdBy) {
        SalaryComponent component = new SalaryComponent(
            name: data.name,
            code: data.code,
            description: data.description,
            componentType: data.componentType ?: 'EARNING',
            company: Company.get(companyId),
            defaultValue: data.defaultValue,
            isStatutory: data.isStatutory ?: false,
            isActive: data.isActive != false,
            createdBy: createdBy
        )
        component.save(flush: true, failOnError: true)
        return component
    }

    def listSalaryComponents(Long companyId) {
        return SalaryComponent.findAllByCompany(Company.get(companyId), [sort: 'name', order: 'asc'])
    }

    def getPayslipById(Long id) {
        return Payslip.get(id)
    }

    def listPayslips(Long companyId, Map params = [:]) {
        Payslip.withCriteria {
            eq('employee.company', Company.get(companyId))
            if (params.employeeId) {
                eq('employee', Employee.get(params.employeeId))
            }
            if (params.year) {
                eq('year', params.year as Integer)
            }
            if (params.month) {
                eq('month', params.month as Integer)
            }
            if (params.status) {
                eq('status', params.status)
            }
            order('year', 'desc')
            order('month', 'desc')
            firstResult params.offset ?: 0
            maxResults params.max ?: 20
        }
    }

    def countPayslips(Long companyId, Map params = [:]) {
        return Payslip.count {
            eq('employee.company', Company.get(companyId))
            if (params.employeeId) {
                eq('employee', Employee.get(params.employeeId))
            }
            if (params.year) {
                eq('year', params.year as Integer)
            }
            if (params.month) {
                eq('month', params.month as Integer)
            }
            if (params.status) {
                eq('status', params.status)
            }
        }
    }

    def generatePayslip(Long employeeId, Integer year, Integer month, Long createdBy) {
        Employee employee = Employee.get(employeeId)
        if (!employee) {
            throw new NoSuchElementException("Employee not found: ${employeeId}")
        }

        // Calculate working days
        def workingDays = calculateWorkingDays(year, month)
        def dailySalary = (employee.salary ?: BigDecimal.ZERO).divide(new BigDecimal(workingDays), 2, BigDecimal.ROUND_HALF_UP)

        // Calculate earnings
        BigDecimal grossSalary = BigDecimal.ZERO
        def earningComponents = SalaryComponent.findAllByCompanyAndComponentType(
            employee.company, 'EARNING', [sort: 'name', order: 'asc'])
        for (SalaryComponent component : earningComponents) {
            BigDecimal amount = calculateComponentAmount(component, dailySalary, employee)
            grossSalary = grossSalary.add(amount)

            PayslipComponent pc = new PayslipComponent(
                payslip: null, // Will be set below
                salaryComponent: component,
                amount: amount,
                remarks: null
            )
            pc.save(flush: false)
        }

        // Calculate deductions
        BigDecimal totalDeductions = BigDecimal.ZERO
        def deductionComponents = SalaryComponent.findAllByCompanyAndComponentType(
            employee.company, 'DEDUCTION', [sort: 'name', order: 'asc'])
        for (SalaryComponent component : deductionComponents) {
            BigDecimal amount = calculateComponentAmount(component, dailySalary, employee)
            totalDeductions = totalDeductions.add(amount)

            PayslipComponent pc = new PayslipComponent(
                payslip: null,
                salaryComponent: component,
                amount: amount,
                remarks: null
            )
            pc.save(flush: false)
        }

        BigDecimal netSalary = grossSalary.subtract(totalDeductions)

        Payslip payslip = new Payslip(
            employee: employee,
            year: year,
            month: month,
            grossSalary: grossSalary,
            totalDeductions: totalDeductions,
            netSalary: netSalary,
            status: 'GENERATED',
            isGenerated: true,
            createdBy: createdBy
        )
        payslip.save(flush: true, failOnError: true)

        return payslip
    }

    def approvePayslip(Long id) {
        Payslip payslip = Payslip.get(id)
        if (!payslip) {
            throw new NoSuchElementException("Payslip not found: ${id}")
        }
        payslip.status = 'APPROVED'
        payslip.paymentDate = LocalDate.now()
        payslip.save(flush: true, failOnError: true)
        return payslip
    }

    def rejectPayslip(Long id, String reason) {
        Payslip payslip = Payslip.get(id)
        if (!payslip) {
            throw new NoSuchElementException("Payslip not found: ${id}")
        }
        payslip.status = 'REJECTED'
        payslip.remarks = reason
        payslip.save(flush: true, failOnError: true)
        return payslip
    }

    private BigDecimal calculateComponentAmount(SalaryComponent component, BigDecimal dailySalary, Employee employee) {
        switch (component.code) {
            case 'BASIC_SALARY':
                return employee.salary ?: BigDecimal.ZERO
            case 'HOUSING_ALLOWANCE':
                return (employee.salary ?: BigDecimal.ZERO).multiply(new BigDecimal('0.5'))
            case 'TRANSPORT_ALLOWANCE':
                return (employee.salary ?: BigDecimal.ZERO).multiply(new BigDecimal('0.1'))
            case 'MEDICAL_ALLOWANCE':
                return (employee.salary ?: BigDecimal.ZERO).multiply(new BigDecimal('0.05'))
            case 'PROVIDENT_FUND':
                return (employee.salary ?: BigDecimal.ZERO).multiply(new BigDecimal('0.125'))
            case 'THIRD_PARTY_INSURANCE':
                return (employee.salary ?: BigDecimal.ZERO).multiply(new BigDecimal('0.01'))
            case 'ABSENCES_DEDUCTION':
                // Deduct for absences
                return BigDecimal.ZERO
            default:
                return component.defaultValue ?: BigDecimal.ZERO
        }
    }

    private int calculateWorkingDays(Integer year, Integer month) {
        LocalDate firstDay = LocalDate.of(year, month, 1)
        int workingDays = 0
        LocalDate current = firstDay
        while (current.month == firstDay.month) {
            if (current.getDayOfWeek().value() <= 5) { // Monday to Friday
                workingDays++
            }
            current = current.plusDays(1)
        }
        return workingDays > 0 ? workingDays : 21
    }

    def getPayrollSummary(Long companyId, Integer year, Integer month) {
        List<Payslip> payslips = listPayslips(companyId, [year: year, month: month])

        def summary = [
            totalEmployees: payslips.size(),
            totalGross: BigDecimal.ZERO,
            totalDeductions: BigDecimal.ZERO,
            totalNet: BigDecimal.ZERO
        ]

        for (Payslip p : payslips) {
            if (p.grossSalary) summary.totalGross = summary.totalGross.add(p.grossSalary)
            if (p.totalDeductions) summary.totalDeductions = summary.totalDeductions.add(p.totalDeductions)
            if (p.netSalary) summary.totalNet = summary.totalNet.add(p.netSalary)
        }

        return summary
    }
}