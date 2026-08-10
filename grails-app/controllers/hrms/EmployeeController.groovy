package hrms

import grails.gorm.transactions.Transactional
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Controller
import grails.converters.JSON

@Controller
@Transactional
class EmployeeController {

    EmployeeService employeeService

    def index() {
        render view: 'index', model: [title: 'HRMS - Employee Management']
    }

    def list(Long companyId) {
        def employees = employeeService.listEmployees(companyId, params)
        def total = employeeService.countEmployees(companyId, params)
        render JSON.encodeAsJSON([employees: employees, total: total, offset: params.offset, max: params.max])
    }

    def show(Long companyId, Long id) {
        def employee = employeeService.getEmployeeById(id)
        if (!employee || employee.company.id != companyId) {
            response.status = HttpStatus.NOT_FOUND.value()
            render JSON.encodeAsJSON([message: 'Employee not found'])
            return
        }
        render JSON.encodeAsJSON(employee)
    }

    def create(Long companyId) {
        try {
            def employee = employeeService.createEmployee(companyId, request.JSON, session?.user?.id)
            response.status = HttpStatus.CREATED.value()
            render JSON.encodeAsJSON([message: 'Employee created successfully', employee: employee])
        } catch (Exception e) {
            response.status = HttpStatus.BAD_REQUEST.value()
            render contentType: 'application/json', text: new groovy.json.JsonOutput().toJson([message: e.message])
        }
    }

    def update(Long companyId, Long id) {
        try {
            def employee = employeeService.updateEmployee(id, request.JSON, session?.user?.id)
            render JSON.encodeAsJSON([message: 'Employee updated successfully', employee: employee])
        } catch (Exception e) {
            response.status = HttpStatus.BAD_REQUEST.value()
            render contentType: 'application/json', text: new groovy.json.JsonOutput().toJson([message: e.message])
        }
    }

    def terminate(Long companyId, Long id) {
        try {
            def employee = employeeService.terminateEmployee(id, request.JSON, session?.user?.id)
            render JSON.encodeAsJSON([message: 'Employee terminated successfully', employee: employee])
        } catch (Exception e) {
            response.status = HttpStatus.BAD_REQUEST.value()
            render JSON.encodeAsJSON([message: e.message])
        }
    }

    def rehire(Long companyId, Long id) {
        try {
            def employee = employeeService.rehireEmployee(id, session?.user?.id)
            render JSON.encodeAsJSON([message: 'Employee rehired successfully', employee: employee])
        } catch (Exception e) {
            response.status = HttpStatus.BAD_REQUEST.value()
            render JSON.encodeAsJSON([message: e.message])
        }
    }

    def getAttendanceStats(Long companyId, Integer year, Integer month) {
        def stats = employeeService.getAttendanceStats(companyId, year, month)
        render JSON.encodeAsJSON(stats)
    }

    def getLeaveStats(Long companyId, Integer year, Integer month) {
        def stats = employeeService.getLeaveStats(companyId, year, month)
        render JSON.encodeAsJSON(stats)
    }

    def listDepartments(Long companyId) {
        def departments = employeeService.listDepartments(companyId, params)
        def total = employeeService.countDepartments(companyId, params)
        render JSON.encodeAsJSON([departments: departments, total: total])
    }

    def createDepartment(Long companyId) {
        try {
            def dept = employeeService.createDepartment(companyId, request.JSON, session?.user?.id)
            response.status = HttpStatus.CREATED.value()
            render JSON.encodeAsJSON([message: 'Department created successfully', department: dept])
        } catch (Exception e) {
            response.status = HttpStatus.BAD_REQUEST.value()
            render contentType: 'application/json', text: new groovy.json.JsonOutput().toJson([message: e.message])
        }
    }

    def updateDepartment(Long companyId, Long id) {
        try {
            def dept = employeeService.updateDepartment(id, request.JSON)
            render JSON.encodeAsJSON([message: 'Department updated successfully', department: dept])
        } catch (Exception e) {
            response.status = HttpStatus.BAD_REQUEST.value()
            render JSON.encodeAsJSON([message: e.message])
        }
    }

    def listDesignations(Long companyId) {
        def designations = employeeService.listDesignations(companyId, params)
        render JSON.encodeAsJSON(designations)
    }

    def createDesignation(Long companyId) {
        try {
            def designation = employeeService.createDesignation(companyId, request.JSON, session?.user?.id)
            response.status = HttpStatus.CREATED.value()
            render JSON.encodeAsJSON([message: 'Designation created successfully', designation: designation])
        } catch (Exception e) {
            response.status = HttpStatus.BAD_REQUEST.value()
            render JSON.encodeAsJSON([message: e.message])
        }
    }
}