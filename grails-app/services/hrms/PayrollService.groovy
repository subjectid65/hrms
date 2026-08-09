package hrms

import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.time.LocalDate
import java.time.temporal.WeekFields
import java.util.Locale

@Transactional
class PayrollService {

    def listSalaryComponents(Long companyId) {
        return SalaryComponent.findAllByCompany(Company.get(companyId), [sort: 'name', order: 'asc'])
    }

    def createSalaryComponent(Long companyId, Map<String, Object> data, Long createdBy) {
        SalaryComponent component = new SalaryComponent(
            name: data.name,
            code: data.code,
            description: data.description,
            componentType: data.componentType,
            defaultValue: data.defaultValue,
            isStatutory: data.isStatutory ?: false,
            company: Company.get(companyId),
            isActive: data.isActive != false,
            createdBy: createdBy
        )
        component.save(flush: true, failOnError: true)
        return component
    }

    def listPayslips(Long companyId, Map params = [:]) {
        return Payslip.withCriteria {
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

    def getPayslipById(Long id) {
        return Payslip.get(id)
    }

    def generatePayslip(Long employeeId, Integer year, Integer month, Long generatedBy) {
        Employee employee = Employee.get(employeeId)
        if (!employee) {
            throw new NoSuchElementException("Employee not found: ${employeeId}")
        }

        // Get earnings and deductions
        def earningComponents = SalaryComponent.findAllByCompanyAndComponentType(
            Company.get(employee.company.id), 'EARNING', [sort: 'name', order: 'asc'])
        def deductionComponents = SalaryComponent.findAllByCompanyAndComponentType(
            Company.get(employee.company.id), 'DEDUCTION', [sort: 'name', order: 'asc'])

        BigDecimal grossSalary = BigDecimal.ZERO
        BigDecimal totalDeductions = BigDecimal.ZERO

        for (SalaryComponent component : earningComponents) {
            if (component.defaultValue) {
                BigDecimal value = component.defaultValue
                grossSalary = grossSalary.add(value)
            }
        }

        for (SalaryComponent component : deductionComponents) {
            if (component.defaultValue) {
                BigDecimal value = component.defaultValue
                totalDeductions = totalDeductions.add(value)
            }
        }

        BigDecimal netSalary = grossSalary.subtract(totalDeductions)

        Payslip payslip = new Payslip(
            employee: employee,
            year: year,
            month: month,
            grossSalary: grossSalary,
            totalDeductions: totalDeductions,
            netSalary: netSalary,
            status: 'PENDING',
            isGenerated: true,
            createdBy: generatedBy
        )
        payslip.save(flush: true, failOnError: true)
        return payslip
    }

    def approvePayslip(Long payslipId) {
        Payslip payslip = Payslip.get(payslipId)
        if (!payslip) {
            throw new NoSuchElementException("Payslip not found: ${payslipId}")
        }
        payslip.status = 'APPROVED'
        payslip.paymentDate = LocalDate.now()
        payslip.save(flush: true, failOnError: true)
        return payslip
    }

    def rejectPayslip(Long payslipId, String reason) {
        Payslip payslip = Payslip.get(payslipId)
        if (!payslip) {
            throw new NoSuchElementException("Payslip not found: ${payslipId}")
        }
        payslip.status = 'REJECTED'
        payslip.remarks = reason
        payslip.save(flush: true, failOnError: true)
        return payslip
    }

    def getPayrollSummary(Long companyId, Integer year, Integer month) {
        LocalDate from = LocalDate.of(year, month, 1)
        LocalDate to = from.plusMonths(1).minusDays(1)

        List<Payslip> payslips = listPayslips(companyId, [year: year, month: month])

        BigDecimal totalGross = BigDecimal.ZERO
        BigDecimal totalDeductions = BigDecimal.ZERO
        BigDecimal totalNet = BigDecimal.ZERO
        int approvedCount = 0
        int pendingCount = 0

        for (Payslip p : payslips) {
            if (p.grossSalary) totalGross = totalGross.add(p.grossSalary)
            if (p.totalDeductions) totalDeductions = totalDeductions.add(p.totalDeductions)
            if (p.netSalary) totalNet = totalNet.add(p.netSalary)
            if (p.status == 'APPROVED') approvedCount++
            if (p.status == 'PENDING') pendingCount++
        }

        return [
            totalGross: totalGross,
            totalDeductions: totalDeductions,
            totalNet: totalNet,
            approvedCount: approvedCount,
            pendingCount: pendingCount,
            totalPayslips: payslips.size()
        ]
    }

    private LocalDate getWorkingDays(LocalDate from, LocalDate to) {
        return to
    }
}