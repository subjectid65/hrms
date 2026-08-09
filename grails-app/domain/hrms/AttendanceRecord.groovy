package hrms

import java.time.LocalDate
import java.time.LocalTime

class AttendanceRecord {

    Employee employee
    LocalDate attendanceDate
    LocalTime checkInTime
    LocalTime checkOutTime
    String checkInSource = 'MANUAL'
    String checkOutSource = 'MANUAL'
    Integer lateMinutes
    Integer earlyMinutes
    Integer totalWorkingHours
    String status = 'PRESENT'
    String remarks
    Boolean isHalfDay = false
    Boolean isAbsent = false
    Boolean isOnLeave = false
    LocalDate createdAt
    LocalDate updatedAt
    Long createdBy

    static constraints = {
        employee nullable: false
        attendanceDate nullable: false
        checkInTime nullable: true
        checkOutTime nullable: true
        checkInSource maxSize: 20, nullable: true
        checkOutSource maxSize: 20, nullable: true
        lateMinutes nullable: true
        earlyMinutes nullable: true
        totalWorkingHours nullable: true
        status maxSize: 20, nullable: true
        remarks maxSize: 500, nullable: true
        isHalfDay nullable: false
        isAbsent nullable: false
        isOnLeave nullable: false
    }

    static mapping = {
        table 'attendance_record'
        id column: 'attendance_record_id', generator: 'native'
        employee column: 'employee_id'
        attendanceDate column: 'attendance_date', type: 'date'
        checkInTime column: 'check_in_time', type: 'time'
        checkOutTime column: 'check_out_time', type: 'time'
        checkInSource column: 'check_in_source'
        checkOutSource column: 'check_out_source'
        lateMinutes column: 'late_minutes'
        earlyMinutes column: 'early_minutes'
        totalWorkingHours column: 'total_working_hours'
        status column: 'status'
        remarks column: 'remarks'
        isHalfDay column: 'is_half_day'
        isAbsent column: 'is_absent'
        isOnLeave column: 'is_on_leave'
        createdAt column: 'created_at'
        updatedAt column: 'updated_at'
        createdBy column: 'created_by'
        autoTimestamp false
    }

    static belongsTo = [employee: Employee]

    def beforeInsert() {
        createdAt = LocalDate.now()
        updatedAt = LocalDate.now()
    }

    def beforeUpdate() {
        updatedAt = LocalDate.now()
    }

    String toString() {
        return "${employee}: ${attendanceDate} (${checkInTime ?: 'N/A'} - ${checkOutTime ?: 'N/A'})"
    }
}