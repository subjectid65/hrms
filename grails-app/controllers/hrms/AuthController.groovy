package hrms

import grails.gorm.transactions.Transactional
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Controller
import grails.converters.JSON

@Controller
@Transactional
class AuthController {

    UserService userService

    def login() {
        try {
            def result = userService.login(params.username, params.password)
            if (result.success) {
                session.currentUser = result.user
                session.companyId = result.user?.company?.id
                render contentType: 'application/json', text: new groovy.json.JsonOutput().toJson([
                    success: true,
                    message: 'Login successful',
                    user: result.user,
                    token: result.token
                ])
            } else {
                response.status = HttpStatus.UNAUTHORIZED.value()
                render contentType: 'application/json', text: new groovy.json.JsonOutput().toJson([success: false, message: result.message])
            }
        } catch (Exception e) {
            response.status = HttpStatus.UNAUTHORIZED.value()
            render contentType: 'application/json', text: new groovy.json.JsonOutput().toJson([success: false, message: e.message])
        }
    }

    def logout() {
        session.invalidate()
        render contentType: 'application/json', text: new groovy.json.JsonOutput().toJson([success: true, message: 'Logged out successfully'])
    }

    def register() {
        try {
            def user = userService.register(request.JSON)
            response.status = HttpStatus.CREATED.value()
            render contentType: 'application/json', text: new groovy.json.JsonOutput().toJson([message: 'User registered successfully', user: user])
        } catch (Exception e) {
            response.status = HttpStatus.BAD_REQUEST.value()
            render contentType: 'application/json', text: new groovy.json.JsonOutput().toJson([success: false, message: e.message])
        }
    }

    def profile() {
        if (session.currentUser) {
            render contentType: 'application/json', text: new groovy.json.JsonOutput().toJson(session.currentUser)
        } else {
            response.status = HttpStatus.UNAUTHORIZED.value()
            render contentType: 'application/json', text: new groovy.json.JsonOutput().toJson([success: false, message: 'Not logged in'])
        }
    }

    def updateProfile() {
        try {
            def user = userService.updateProfile(session.currentUser.id, request.JSON)
            session.currentUser = user
            render contentType: 'application/json', text: new groovy.json.JsonOutput().toJson([message: 'Profile updated', user: user])
        } catch (Exception e) {
            response.status = HttpStatus.BAD_REQUEST.value()
            render contentType: 'application/json', text: new groovy.json.JsonOutput().toJson([message: e.message])
        }
    }

    def changePassword() {
        try {
            userService.changePassword(session.currentUser.id, params.oldPassword, params.newPassword)
            render contentType: 'application/json', text: new groovy.json.JsonOutput().toJson([message: 'Password changed successfully'])
        } catch (Exception e) {
            response.status = HttpStatus.BAD_REQUEST.value()
            render contentType: 'application/json', text: new groovy.json.JsonOutput().toJson([message: e.message])
        }
    }
}