package hrms

import groovy.transform.CompileStatic
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.time.LocalTime

@CompileStatic
@Transactional
class AttendanceService {

    def checkIn(Long employeeId, String source, String deviceType, String location, String ipAddress, Long userId) {
        Employee employee = Employee.get(employeeId)
        if (!employee) {
            throw new NoSuchElementException("Employee not found: ${employeeId}")
        }

        AttendanceLog log = new AttendanceLog(
            employee: employee,
            logDate: LocalDate.now(),
            logTime: LocalTime.now(),
            logType: 'CHECK_IN',
            source: source ?: 'MANUAL',
            deviceType: deviceType,
            ipAddress: ipAddress,
            location: location,
            createdBy: userId
        )
        log.save(flush: true, failOnError: true)

        // Update or create attendance record
        AttendanceRecord record = AttendanceRecord.findByEmployeeIdAndAttendanceDate(employeeId, LocalDate.now())
        if (!record) {
            record = new AttendanceRecord(
                employee: employee,
                attendanceDate: LocalDate.now(),
                checkInTime: LocalTime.now(),
                checkInSource: source ?: 'MANUAL',
                status: 'PRESENT'
            )
        } else {
            record.checkInTime = LocalTime.now()
            record.checkInSource = source ?: 'MANUAL'
            record.status = 'PRESENT'
        }
        record.save(flush: true, failOnError: true)

        return log
    }

    def checkOut(Long employeeId, String source, String deviceType, String location, String ipAddress, Long userId) {
        Employee employee = Employee.get(employeeId)
        if (!employee) {
            throw new NoSuchElementException("Employee not found: ${employeeId}")
        }

        AttendanceLog log = new AttendanceLog(
            employee: employee,
            logDate: LocalDate.now(),
            logTime: LocalTime.now(),
            logType: 'CHECK_OUT',
            source: source ?: 'MANUAL',
            deviceType: deviceType,
            ipAddress: ipAddress,
            location: location,
            createdBy: userId
        )
        log.save(flush: true, failOnError: true)

        // Update attendance record
        AttendanceRecord record = AttendanceRecord.findByEmployeeIdAndAttendanceDate(employeeId, LocalDate.now())
        if (record) {
            record.checkOutTime = LocalTime.now()
            record.checkOutSource = source ?: 'MANUAL'

            // Calculate working hours
            if (record.checkInTime && record.checkOutTime) {
                long minutes = java.time.Duration.between(record.checkInTime, record.checkOutTime).toMinutes()
                record.totalWorkingHours = (int) (minutes / 60)
                record.lateMinutes = calculateLateMinutes(record.checkInTime)
                record.earlyMinutes = calculateEarlyMinutes(record.checkOutTime)
            }
            record.save(flush: true, failOnError: true)
        }

        return log
    }

    def getDailyAttendance(Long companyId, LocalDate date) {
        return AttendanceRecord.findAll {
            eq('employee.company', Company.get(companyId))
            eq('attendanceDate', date)
        }
    }

    def getMonthlyAttendance(Long companyId, Integer year, Integer month) {
        LocalDate from = LocalDate.of(year, month, 1)
        LocalDate to = from.plusMonths(1).minusDays(1)
        return AttendanceRecord.findAll {
            eq('employee.company', Company.get(companyId))
            between('attendanceDate', from, to)
        }
    }

    def getEmployeeAttendance(Long employeeId, Integer year, Integer month) {
        LocalDate from = LocalDate.of(year, month, 1)
        LocalDate to = from.plusMonths(1).minusDays(1)
        return AttendanceRecord.findAll {
            eq('employee', Employee.get(employeeId))
            between('attendanceDate', from, to)
        }
    }

    def approveAttendance(Long recordId, String status, String remarks, Long approvedBy) {
        AttendanceRecord record = AttendanceRecord.get(recordId)
        if (!record) {
            throw new NoSuchElementException("Attendance record not found: ${recordId}")
        }
        record.status = status
        record.remarks = remarks
        record.save(flush: true, failOnError: true)
        return record
    }

    def getAttendanceReport(Long companyId, Integer year, Integer month) {
        def records = getMonthlyAttendance(companyId, year, month)

        def report = [
            totalDays: records.size(),
            present: records.count { it.status == 'PRESENT' },
            absent: records.count { it.status == 'ABSENT' || it.isAbsent },
            late: records.count { it.lateMinutes > 0 },
            halfDay: records.count { it.isHalfDay },
            onLeave: records.count { it.isOnLeave }
        ]
        return report
    }

    private int calculateLateMinutes(LocalTime checkInTime) {
        // Assume standard check-in time is 9:00 AM
        LocalTime standardCheckIn = LocalTime.of(9, 0)
        if (checkInTime.isAfter(standardCheckIn)) {
            return (int) java.time.Duration.between(standardCheckIn, checkInTime).toMinutes()
        }
        return 0
    }

    private int calculateEarlyMinutes(LocalTime checkOutTime) {
        // Assume standard check-out time is 6:00 PM
        LocalTime standardCheckOut = LocalTime.of(18, 0)
        if (checkOutTime.isBefore(standardCheckOut)) {
            return (int) java.time.Duration.between(checkOutTime, standardCheckOut).toMinutes()
        }
        return 0
    }
}