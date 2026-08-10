package hrms

import grails.gorm.transactions.Transactional
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Controller

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
        def serialized = employees.collect { e -> [id: e.id, employeeCode: e.employeeCode, firstName: e.firstName, lastName: e.lastName, email: e.email, jobTitle: e.jobTitle, department: e.department ? [id: e.department.id, name: e.department.name] : null, designation: e.designation ? [id: e.designation.id, name: e.designation.name] : null, isActive: e.isActive] }
        render contentType: 'application/json', text: new groovy.json.JsonOutput().toJson([employees: serialized, total: total, offset: params.offset, max: params.max])
    }

    def show(Long companyId, Long id) {
        def employee = employeeService.getEmployeeById(id)
        if (!employee || employee.company.id != companyId) {
            response.status = HttpStatus.NOT_FOUND.value()
            render contentType: 'application/json', text: new groovy.json.JsonOutput().toJson([message: 'Employee not found'])
            return
        }
        render contentType: 'application/json', text: new groovy.json.JsonOutput().toJson([id: employee.id, employeeCode: employee.employeeCode, firstName: employee.firstName, lastName: employee.lastName, email: employee.email, jobTitle: employee.jobTitle, department: employee.department ? [id: employee.department.id, name: employee.department.name] : null, designation: employee.designation ? [id: employee.designation.id, name: employee.designation.name] : null, isActive: employee.isActive])
    }

    def create(Long companyId) {
        try {
            def employee = employeeService.createEmployee(companyId, request.JSON, session?.user?.id)
            response.status = HttpStatus.CREATED.value()
            render contentType: 'application/json', text: new groovy.json.JsonOutput().toJson([message: 'Employee created successfully', employee: [id: employee.id, employeeCode: employee.employeeCode, firstName: employee.firstName, lastName: employee.lastName, email: employee.email, jobTitle: employee.jobTitle, isActive: employee.isActive]])
        } catch (Exception e) {
            response.status = HttpStatus.BAD_REQUEST.value()
            render contentType: 'application/json', text: new groovy.json.JsonOutput().toJson([message: e.message])
        }
    }

    def update(Long companyId, Long id) {
        try {
            def employee = employeeService.updateEmployee(id, request.JSON, session?.user?.id)
            render contentType: 'application/json', text: new groovy.json.JsonOutput().toJson([message: 'Employee updated successfully', employee: [id: employee.id, employeeCode: employee.employeeCode, firstName: employee.firstName, lastName: employee.lastName, email: employee.email, jobTitle: employee.jobTitle, isActive: employee.isActive]])
        } catch (Exception e) {
            response.status = HttpStatus.BAD_REQUEST.value()
            render contentType: 'application/json', text: new groovy.json.JsonOutput().toJson([message: e.message])
        }
    }

    def terminate(Long companyId, Long id) {
        try {
            def employee = employeeService.terminateEmployee(id, request.JSON, session?.user?.id)
            render contentType: 'application/json', text: new groovy.json.JsonOutput().toJson([message: 'Employee terminated successfully', employee: [id: employee.id, employeeCode: employee.employeeCode, firstName: employee.firstName, lastName: employee.lastName, isActive: employee.isActive, terminationDate: employee.terminationDate]])
        } catch (Exception e) {
            response.status = HttpStatus.BAD_REQUEST.value()
            render contentType: 'application/json', text: new groovy.json.JsonOutput().toJson([message: e.message])
        }
    }

    def rehire(Long companyId, Long id) {
        try {
            def employee = employeeService.rehireEmployee(id, session?.user?.id)
            render contentType: 'application/json', text: new groovy.json.JsonOutput().toJson([message: 'Employee rehired successfully', employee: [id: employee.id, employeeCode: employee.employeeCode, firstName: employee.firstName, lastName: employee.lastName, isActive: employee.isActive]])
        } catch (Exception e) {
            response.status = HttpStatus.BAD_REQUEST.value()
            render contentType: 'application/json', text: new groovy.json.JsonOutput().toJson([message: e.message])
        }
    }

    def getAttendanceStats(Long companyId, Integer year, Integer month) {
        def stats = employeeService.getAttendanceStats(companyId, year, month)
        render contentType: 'application/json', text: new groovy.json.JsonOutput().toJson(stats)
    }

    def getLeaveStats(Long companyId, Integer year, Integer month) {
        def stats = employeeService.getLeaveStats(companyId, year, month)
        render contentType: 'application/json', text: new groovy.json.JsonOutput().toJson(stats)
    }

    def listDepartments(Long companyId) {
        try {
            def departments = employeeService.listDepartments(companyId, params)
            def total = employeeService.countDepartments(companyId, params)
            def serialized = departments.collect { d -> [id: d.id, name: d.name, code: d.code, isActive: d.isActive] }
            render contentType: 'application/json', text: new groovy.json.JsonOutput().toJson([departments: serialized, total: total])
        } catch (NoSuchElementException e) {
            response.status = HttpStatus.NOT_FOUND.value()
            render contentType: 'application/json', text: new groovy.json.JsonOutput().toJson([message: e.message])
        }
    }

    def createDepartment(Long companyId) {
        try {
            def dept = employeeService.createDepartment(companyId, request.JSON, session?.user?.id)
            response.status = HttpStatus.CREATED.value()
            render contentType: 'application/json', text: new groovy.json.JsonOutput().toJson([message: 'Department created successfully', department: [id: dept.id, name: dept.name, code: dept.code, isActive: dept.isActive]])
        } catch (NoSuchElementException e) {
            response.status = HttpStatus.NOT_FOUND.value()
            render contentType: 'application/json', text: new groovy.json.JsonOutput().toJson([message: e.message])
        } catch (Exception e) {
            response.status = HttpStatus.BAD_REQUEST.value()
            render contentType: 'application/json', text: new groovy.json.JsonOutput().toJson([message: e.message])
        }
    }

    def updateDepartment(Long companyId, Long id) {
        try {
            def dept = employeeService.updateDepartment(id, request.JSON)
            render contentType: 'application/json', text: new groovy.json.JsonOutput().toJson([message: 'Department updated successfully', department: [id: dept.id, name: dept.name, code: dept.code, isActive: dept.isActive]])
        } catch (Exception e) {
            response.status = HttpStatus.BAD_REQUEST.value()
            render contentType: 'application/json', text: new groovy.json.JsonOutput().toJson([message: e.message])
        }
    }

    def listDesignations(Long companyId) {
        def designations = employeeService.listDesignations(companyId, params)
        def serialized = designations.collect { d -> [id: d.id, name: d.name, code: d.code, isActive: d.isActive] }
        render contentType: 'application/json', text: new groovy.json.JsonOutput().toJson(serialized)
    }

    def createDesignation(Long companyId) {
        try {
            def designation = employeeService.createDesignation(companyId, request.JSON, session?.user?.id)
            response.status = HttpStatus.CREATED.value()
            render contentType: 'application/json', text: new groovy.json.JsonOutput().toJson([message: 'Designation created successfully', designation: [id: designation.id, name: designation.name, code: designation.code, isActive: designation.isActive]])
        } catch (Exception e) {
            response.status = HttpStatus.BAD_REQUEST.value()
            render contentType: 'application/json', text: new groovy.json.JsonOutput().toJson([message: e.message])
        }
    }
}