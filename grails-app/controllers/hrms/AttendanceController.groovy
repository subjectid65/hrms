package hrms

import grails.gorm.transactions.Transactional
import groovy.transform.CompileStatic
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Controller
import grails.converters.JSON
import java.time.LocalDate

@Controller
@CompileStatic
@Transactional
class AttendanceController {

    AttendanceService attendanceService

    def index() {
        render view: 'index', model: [title: 'HRMS - Attendance Management']
    }

    def checkIn(Long companyId, Long employeeId) {
        try {
            def log = attendanceService.checkIn(
                employeeId,
                params.source,
                params.deviceType,
                params.location,
                params.ipAddress,
                session?.user?.id
            )
            response.status = HttpStatus.OK.value()
            render JSON.encodeAsJSON([message: 'Check-in recorded', log: log])
        } catch (Exception e) {
            response.status = HttpStatus.BAD_REQUEST.value()
            render JSON.encodeAsJSON([message: e.message])
        }
    }

    def checkOut(Long companyId, Long employeeId) {
        try {
            def log = attendanceService.checkOut(
                employeeId,
                params.source,
                params.deviceType,
                params.location,
                params.ipAddress,
                session?.user?.id
            )
            response.status = HttpStatus.OK.value()
            render JSON.encodeAsJSON([message: 'Check-out recorded', log: log])
        } catch (Exception e) {
            response.status = HttpStatus.BAD_REQUEST.value()
            render JSON.encodeAsJSON([message: e.message])
        }
    }

    def daily(Long companyId, Integer year, Integer month, Integer day) {
        LocalDate date = LocalDate.of(year, month, day)
        def records = attendanceService.getDailyAttendance(companyId, date)
        render JSON.encodeAsJSON(records)
    }

    def monthly(Long companyId, Integer year, Integer month) {
        def records = attendanceService.getMonthlyAttendance(companyId, year, month)
        def report = attendanceService.getAttendanceReport(companyId, year, month)
        render JSON.encodeAsJSON([records: records, report: report])
    }

    def employee(Long companyId, Long employeeId, Integer year, Integer month) {
        def records = attendanceService.getEmployeeAttendance(employeeId, year, month)
        render JSON.encodeAsJSON(records)
    }

    def approve(Long recordId) {
        try {
            def record = attendanceService.approveAttendance(
                recordId,
                params.status,
                params.remarks,
                session?.user?.id
            )
            render JSON.encodeAsJSON([message: 'Attendance approved', record: record])
        } catch (Exception e) {
            response.status = HttpStatus.BAD_REQUEST.value()
            render JSON.encodeAsJSON([message: e.message])
        }
    }
}