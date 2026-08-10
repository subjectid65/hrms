package hrms

class UrlMappings {

    static mappings = {
        // Root
        "/"(view: "/index")
        "/index"(view: "/index")
        "/admin-dashboard"(view: "/admin-dashboard")
        "/hr-dashboard"(view: "/hr-dashboard")
        "/manager-dashboard"(view: "/manager-dashboard")
        "/employee-dashboard"(view: "/employee-dashboard")
        "/api/v1/auth/login"(controller: "auth", action: "login")
        "/api/v1/auth/logout"(controller: "auth", action: "logout")
        "/api/v1/auth/register"(controller: "auth", action: "register")
        "/api/v1/auth/profile"(controller: "auth", action: "profile")
        "/api/v1/auth/profile/update"(controller: "auth", action: "updateProfile")
        "/api/v1/auth/password/change"(controller: "auth", action: "changePassword")

        // Company management
        "/api/v1/companies"(controller: "company", action: "list", method: "GET")
        "/api/v1/companies"(controller: "company", action: "create", method: "POST")
        "/api/v1/companies/seed"(controller: "company", action: "seed", method: "POST")
        "/api/v1/companies/${id}"(controller: "company", action: "show", method: "GET")
        "/api/v1/companies/${id}"(controller: "company", action: "update", method: "PUT")
        "/api/v1/companies/${id}"(controller: "company", action: "delete", method: "DELETE")
        "/api/v1/companies/${id}/settings"(controller: "company", action: "getSettings", method: "GET")
        "/api/v1/companies/${companyId}/settings/${key}"(controller: "company", action: "updateSetting", method: "PUT")

        // Employee management
        "/api/v1/companies/${companyId}/employees"(controller: "employee", action: "list", method: "GET")
        "/api/v1/companies/${companyId}/employees"(controller: "employee", action: "create", method: "POST")
        "/api/v1/companies/${companyId}/employees/${id}"(controller: "employee", action: "show", method: "GET")
        "/api/v1/companies/${companyId}/employees/${id}"(controller: "employee", action: "update", method: "PUT")
        "/api/v1/companies/${companyId}/employees/${id}/terminate"(controller: "employee", action: "terminate", method: "POST")
        "/api/v1/companies/${companyId}/employees/${id}/rehire"(controller: "employee", action: "rehire", method: "POST")
        "/api/v1/companies/${companyId}/employees/attendance-stats"(controller: "employee", action: "getAttendanceStats", method: "GET")
        "/api/v1/companies/${companyId}/employees/leave-stats"(controller: "employee", action: "getLeaveStats", method: "GET")

        // Departments
        "/api/v1/companies/${companyId}/departments"(controller: "employee", action: "listDepartments", method: "GET")
        "/api/v1/companies/${companyId}/departments"(controller: "employee", action: "createDepartment", method: "POST")
        "/api/v1/companies/${companyId}/departments/${id}"(controller: "employee", action: "updateDepartment", method: "PUT")

        // Designations
        "/api/v1/companies/${companyId}/designations"(controller: "employee", action: "listDesignations", method: "GET")
        "/api/v1/companies/${companyId}/designations"(controller: "employee", action: "createDesignation", method: "POST")

        // Attendance
        "/api/v1/companies/${companyId}/attendance/check-in"(controller: "attendance", action: "checkIn", method: "POST")
        "/api/v1/companies/${companyId}/attendance/check-in/${employeeId}"(controller: "attendance", action: "checkIn", method: "POST")
        "/api/v1/companies/${companyId}/attendance/check-out"(controller: "attendance", action: "checkOut", method: "POST")
        "/api/v1/companies/${companyId}/attendance/check-out/${employeeId}"(controller: "attendance", action: "checkOut", method: "POST")
        "/api/v1/companies/${companyId}/attendance/daily/${year}/${month}/${day}"(controller: "attendance", action: "daily", method: "GET")
        "/api/v1/companies/${companyId}/attendance/monthly/${year}/${month}"(controller: "attendance", action: "monthly", method: "GET")
        "/api/v1/companies/${companyId}/employees/${employeeId}/attendance/${year}/${month}"(controller: "attendance", action: "employee", method: "GET")
        "/api/v1/attendance/${recordId}/approve"(controller: "attendance", action: "approve", method: "PUT")

        // Payroll
        "/api/v1/companies/${companyId}/payroll/components"(controller: "payroll", action: "listComponents", method: "GET")
        "/api/v1/companies/${companyId}/payroll/components"(controller: "payroll", action: "createComponent", method: "POST")
        "/api/v1/companies/${companyId}/payroll/payslips"(controller: "payroll", action: "listPayslips", method: "GET")
        "/api/v1/companies/${companyId}/payroll/payslips/generate/${employeeId}/${year}/${month}"(controller: "payroll", action: "generate", method: "POST")
        "/api/v1/companies/${companyId}/payroll/payslips/${id}"(controller: "payroll", action: "show", method: "GET")
        "/api/v1/companies/${companyId}/payroll/payslips/${id}/approve"(controller: "payroll", action: "approve", method: "POST")
        "/api/v1/companies/${companyId}/payroll/payslips/${id}/reject"(controller: "payroll", action: "reject", method: "POST")
        "/api/v1/companies/${companyId}/payroll/summary/${year}/${month}"(controller: "payroll", action: "summary", method: "GET")

        // Expenses
        "/api/v1/companies/${companyId}/expenses"(controller: "expense", action: "list", method: "GET")
        "/api/v1/companies/${companyId}/expenses"(controller: "expense", action: "create", method: "POST")
        "/api/v1/companies/${companyId}/expenses/${id}"(controller: "expense", action: "show", method: "GET")
        "/api/v1/companies/${companyId}/expenses/${id}/approve"(controller: "expense", action: "approve", method: "POST")
        "/api/v1/companies/${companyId}/expenses/${id}/reject"(controller: "expense", action: "reject", method: "POST")
        "/api/v1/companies/${companyId}/expenses/${id}/pay"(controller: "expense", action: "pay", method: "POST")
        "/api/v1/companies/${companyId}/expenses/summary/${year}/${month}"(controller: "expense", action: "summary", method: "GET")

        // Recruitment
        "/api/v1/companies/${companyId}/recruitment/job-postings"(controller: "recruitment", action: "listJobPostings", method: "GET")
        "/api/v1/companies/${companyId}/recruitment/job-postings"(controller: "recruitment", action: "createJobPosting", method: "POST")
        "/api/v1/companies/${companyId}/recruitment/job-postings/${id}"(controller: "recruitment", action: "showJobPosting", method: "GET")
        "/api/v1/companies/${companyId}/recruitment/job-postings/${id}"(controller: "recruitment", action: "updateJobPosting", method: "PUT")
        "/api/v1/companies/${companyId}/recruitment/job-postings/${jobPostingId}/candidates"(controller: "recruitment", action: "listCandidates", method: "GET")
        "/api/v1/companies/${companyId}/recruitment/job-postings/${jobPostingId}/candidates"(controller: "recruitment", action: "createCandidate", method: "POST")
        "/api/v1/companies/${companyId}/recruitment/candidates/${id}"(controller: "recruitment", action: "showCandidate", method: "GET")
        "/api/v1/companies/${companyId}/recruitment/candidates/${id}"(controller: "recruitment", action: "updateCandidate", method: "PUT")
        "/api/v1/companies/${companyId}/recruitment/candidates/${candidateId}/hire"(controller: "recruitment", action: "hire", method: "POST")
        "/api/v1/employees/${employeeId}/onboarding-tasks"(controller: "recruitment", action: "onboardingTasks", method: "GET")
        "/api/v1/onboarding-tasks/${taskId}/complete"(controller: "recruitment", action: "completeOnboardingTask", method: "POST")
        "/api/v1/companies/${companyId}/recruitment/stats"(controller: "recruitment", action: "stats", method: "GET")

        // Dashboard / Reports
        "/api/v1/companies/${companyId}/dashboard"(controller: "dashboard", action: "index")

        // Catch-all
        "500"(view: '/error')
    }
}