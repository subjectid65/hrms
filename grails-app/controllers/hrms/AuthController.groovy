package hrms

import grails.gorm.transactions.Transactional
import groovy.transform.CompileStatic
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Controller
import grails.converters.JSON

@Controller
@CompileStatic
@Transactional
class AuthController {

    UserService userService

    def login() {
        try {
            def result = userService.login(params.username, params.password)
            if (result.success) {
                session.currentUser = result.user
                session.companyId = result.user?.company?.id
                render JSON.encodeAsJSON([
                    success: true,
                    message: 'Login successful',
                    user: result.user,
                    token: result.token
                ])
            } else {
                response.status = HttpStatus.UNAUTHORIZED.value()
                render JSON.encodeAsJSON([success: false, message: result.message])
            }
        } catch (Exception e) {
            response.status = HttpStatus.UNAUTHORIZED.value()
            render JSON.encodeAsJSON([success: false, message: e.message])
        }
    }

    def logout() {
        session.invalidate()
        render JSON.encodeAsJSON([success: true, message: 'Logged out successfully'])
    }

    def register() {
        try {
            def user = userService.register(request.JSON)
            response.status = HttpStatus.CREATED.value()
            render JSON.encodeAsJSON([message: 'User registered successfully', user: user])
        } catch (Exception e) {
            response.status = HttpStatus.BAD_REQUEST.value()
            render JSON.encodeAsJSON([success: false, message: e.message])
        }
    }

    def profile() {
        if (session.currentUser) {
            render JSON.encodeAsJSON(session.currentUser)
        } else {
            response.status = HttpStatus.UNAUTHORIZED.value()
            render JSON.encodeAsJSON([success: false, message: 'Not logged in'])
        }
    }

    def updateProfile() {
        try {
            def user = userService.updateProfile(session.currentUser.id, request.JSON)
            session.currentUser = user
            render JSON.encodeAsJSON([message: 'Profile updated', user: user])
        } catch (Exception e) {
            response.status = HttpStatus.BAD_REQUEST.value()
            render JSON.encodeAsJSON([message: e.message])
        }
    }

    def changePassword() {
        try {
            userService.changePassword(session.currentUser.id, params.oldPassword, params.newPassword)
            render JSON.encodeAsJSON([message: 'Password changed successfully'])
        } catch (Exception e) {
            response.status = HttpStatus.BAD_REQUEST.value()
            render JSON.encodeAsJSON([message: e.message])
        }
    }
}