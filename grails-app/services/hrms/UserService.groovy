package hrms

import groovy.transform.CompileStatic
import groovy.transform.TypeCheckingMode
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@CompileStatic
@Transactional
class UserService {

    @CompileStatic(TypeCheckingMode.SKIP)
    def login(String username, String password) {
        User user = User.findByUsername(username)
        if (!user || !user.enabled) {
            return [success: false, message: 'Invalid credentials']
        }

        // Simple password check (in production, use proper password hashing)
        if (user.password != password) {
            return [success: false, message: 'Invalid credentials']
        }

        // Generate simple token (in production, use JWT)
        String token = generateToken(user)

        return [success: true, user: user, token: token]
    }

    @CompileStatic(TypeCheckingMode.SKIP)
    def register(Map<String, Object> data) {
        if (User.existsByUsername(data.username)) {
            throw new Exception('Username already exists')
        }

        User user = new User(
            username: data.username,
            password: data.password,
            firstName: data.firstName,
            lastName: data.lastName,
            email: data.email,
            phone: data.phone,
            company: data.companyId ? Company.get(data.companyId) : null,
            isAdmin: data.isAdmin ?: false,
            createdBy: null
        )
        user.save(flush: true, failOnError: true)

        // Create default authorities
        Authority adminAuth = Authority.findOrCreateByAuthority('ROLE_ADMIN')
        Authority userAuth = Authority.findOrCreateByAuthority('ROLE_USER')
        if (user.isAdmin) {
            userAuth.save(flush: false)
        }
        userAuth.save(flush: true)

        UserAuthority ua1 = new UserAuthority(user: user, authority: userAuth)
        ua1.save(flush: true, failOnError: true)

        return user
    }

    @CompileStatic(TypeCheckingMode.SKIP)
    def updateProfile(Long userId, Map<String, Object> data) {
        User user = User.get(userId)
        if (!user) {
            throw new NoSuchElementException('User not found')
        }
        if (data.firstName) user.firstName = data.firstName
        if (data.lastName) user.lastName = data.lastName
        if (data.email) user.email = data.email
        if (data.phone) user.phone = data.phone
        user.save(flush: true, failOnError: true)
        return user
    }

    @CompileStatic(TypeCheckingMode.SKIP)
    def changePassword(Long userId, String oldPassword, String newPassword) {
        User user = User.get(userId)
        if (!user) {
            throw new NoSuchElementException('User not found')
        }
        if (user.password != oldPassword) {
            throw new Exception('Current password is incorrect')
        }
        user.password = newPassword
        user.save(flush: true, failOnError: true)
        return user
    }

    @CompileStatic(TypeCheckingMode.SKIP)
    def listUsers(Long companyId) {
        return User.findAll {
            eq('company', Company.get(companyId))
            order('username', 'asc')
        }
    }

    private String generateToken(User user) {
        // Simple token generation (replace with JWT in production)
        String timestamp = System.currentTimeMillis().toString()
        return "${user.id}:${timestamp}:${user.username}"
    }
}