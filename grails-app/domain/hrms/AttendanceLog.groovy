package hrms

import java.time.LocalDate
import java.time.LocalTime

class AttendanceLog {

    Employee employee
    LocalDate logDate
    LocalTime logTime
    String logType = 'CHECK_IN'
    String source = 'MANUAL'
    String deviceType
    String ipAddress
    String location
    String remarks
    LocalDate createdAt
    Long createdBy

    static constraints = {
        employee nullable: false
        logDate nullable: false
        logTime nullable: true
        logType blank: false, maxSize: 20
        source maxSize: 20
        deviceType maxSize: 50, nullable: true
        ipAddress maxSize: 50, nullable: true
        location maxSize: 200, nullable: true
        remarks maxSize: 500, nullable: true
    }

    static mapping = {
        table 'attendance_log'
        id column: 'attendance_log_id', generator: 'native'
        employee column: 'employee_id'
        logDate column: 'log_date', type: 'date'
        logTime column: 'log_time', type: 'time'
        logType column: 'log_type'
        source column: 'source'
        deviceType column: 'device_type'
        ipAddress column: 'ip_address'
        location column: 'location'
        remarks column: 'remarks'
        createdAt column: 'created_at'
        createdBy column: 'created_by'
        autoTimestamp false
    }

    static belongsTo = [employee: Employee]

    def beforeInsert() {
        createdAt = LocalDate.now()
    }

    String toString() {
        return "${employee}: ${logDate} ${logTime} (${logType})"
    }
}