const API = '/api/v1';
let currentCompanyId = parseInt(sessionStorage.getItem('companyId') || document.querySelector('meta[name="company-id"]')?.content || '1');

function showSection(name) {
    document.querySelectorAll('.section').forEach(s => s.style.display = 'none');
    document.querySelectorAll('.nav-item').forEach(n => n.classList.remove('active'));
    const section = document.getElementById(name + '-section');
    if (section) section.style.display = 'block';
    document.getElementById('page-title').textContent = name.charAt(0).toUpperCase() + name.slice(1);
    event.target.closest('.nav-item').classList.add('active');
    if (name === 'dashboard') loadDashboard();
    if (name === 'employees') loadEmployees();
    if (name === 'departments') loadDepartments();
    if (name === 'designations') loadDesignations();
    if (name === 'attendance') loadAttendance();
    if (name === 'payroll') loadPayroll();
    if (name === 'expenses') loadExpenses();
    if (name === 'recruitment') loadRecruitment();
    if (name === 'companies') loadCompanies();
}

function openModal(id) { 
    document.getElementById(id).classList.add('active'); 
    if (id === 'employee-modal') loadEmployeeModalOptions();
}
function closeModal(id) { document.getElementById(id).classList.remove('active'); }

async function api(url, options = {}) {
    const res = await fetch(url, { headers: { 'Content-Type': 'application/json', ...options.headers }, ...options });
    return res.json();
}

async function login(e) {
    e.preventDefault();
    const form = e.target;
    const role = form.role.value;
    const companyId = form.companyId.value;
    if (!companyId) {
        alert('Please select a company');
        return;
    }
    const data = { role: role, companyId: companyId };
    const result = await api(API + '/auth/login', { method: 'POST', body: JSON.stringify(data) });
    if (result.success) {
        sessionStorage.setItem('userRole', role);
        sessionStorage.setItem('companyId', companyId);
        const roleRedirects = {
            admin:    'admin-dashboard',
            hr:       'hr-dashboard',
            manager:  'manager-dashboard',
            employee: 'employee-dashboard'
        };
        location.href = roleRedirects[role] || 'index.gsp';
    } else {
        alert(result.message || 'Login failed');
    }
}

function logout() { fetch(API + '/auth/logout', { method: 'POST' }).then(() => location.reload()); }

function nowParts() {
    const d = new Date();
    return { year: d.getFullYear(), month: d.getMonth() + 1 };
}

async function loadDashboard() {
    try {
        const [emp, att, job] = await Promise.all([
            api(API + '/companies/' + currentCompanyId + '/employees?max=5'),
            api(API + '/companies/' + currentCompanyId + '/attendance/monthly/' + nowParts().year + '/' + nowParts().month),
            api(API + '/companies/' + currentCompanyId + '/recruitment/job-postings?max=5')
        ]);
        document.getElementById('stat-employees').textContent = emp.total || 0;
        document.getElementById('stat-present').textContent = att?.report?.present || 0;
        document.getElementById('stat-leave').textContent = att?.report?.onLeave || 0;
        document.getElementById('stat-jobs').textContent = job?.total || 0;
        const tbody = document.getElementById('recent-employees');
        if (emp.employees && emp.employees.length > 0) {
            let html = '';
            for (let i = 0; i < emp.employees.length; i++) {
                const e = emp.employees[i];
                html += '<tr>';
                html += '<td>' + (e.employeeCode || '-') + '</td>';
                html += '<td>' + (e.firstName || '') + ' ' + (e.lastName || '') + '</td>';
                html += '<td>' + (e.department && e.department.name ? e.department.name : '-') + '</td>';
                html += '<td>' + (e.designation && e.designation.name ? e.designation.name : '-') + '</td>';
                html += '<td><span class="badge ' + (e.isActive ? 'badge-success' : 'badge-danger') + '">' + (e.isActive ? 'Active' : 'Inactive') + '</span></td>';
                html += '</tr>';
            }
            tbody.innerHTML = html;
        }
    } catch(e) { console.log(e); }
}

async function loadEmployees() {
    try {
        const data = await api(API + '/companies/' + currentCompanyId + '/employees?max=20');
        const tbody = document.getElementById('employee-list');
        if (data.employees && data.employees.length > 0) {
            let html = '';
            for (let i = 0; i < data.employees.length; i++) {
                const e = data.employees[i];
                html += '<tr>';
                html += '<td>' + (e.employeeCode || '-') + '</td>';
                html += '<td>' + (e.firstName || '') + ' ' + (e.lastName || '') + '</td>';
                html += '<td>' + (e.email || '-') + '</td>';
                html += '<td>' + (e.department && e.department.name ? e.department.name : '-') + '</td>';
                html += '<td>' + (e.designation && e.designation.name ? e.designation.name : '-') + '</td>';
                html += '<td><span class="badge ' + (e.isActive ? 'badge-success' : 'badge-danger') + '">' + (e.isActive ? 'Active' : 'Inactive') + '</span></td>';
                html += '<td><button class="btn btn-outline" onclick="editEmployee(' + e.id + ')">Edit</button></td>';
                html += '</tr>';
            }
            tbody.innerHTML = html;
        }
    } catch(e) { console.log(e); }
}

async function loadDepartments() {
    try {
        const data = await api(API + '/companies/' + currentCompanyId + '/departments');
        const tbody = document.getElementById('department-list');
        if (data.departments) {
            let html = '';
            for (let i = 0; i < data.departments.length; i++) {
                const d = data.departments[i];
                html += '<tr>';
                html += '<td>' + (d.code || '-') + '</td>';
                html += '<td>' + (d.name || '-') + '</td>';
                html += '<td>-</td>';
                html += '<td><span class="badge ' + (d.isActive ? 'badge-success' : 'badge-danger') + '">' + (d.isActive ? 'Active' : 'Inactive') + '</span></td>';
                html += '</tr>';
            }
            tbody.innerHTML = html;
        }
    } catch(e) { console.log(e); }
}

async function loadDesignations() {
    try {
        const data = await api(API + '/companies/' + currentCompanyId + '/designations');
        const tbody = document.getElementById('designation-list');
        if (data) {
            let html = '';
            for (let i = 0; i < data.length; i++) {
                const d = data[i];
                html += '<tr>';
                html += '<td>' + (d.code || '-') + '</td>';
                html += '<td>' + (d.name || '-') + '</td>';
                html += '<td>' + (d.department ? d.department.name : '-') + '</td>';
                html += '<td><span class="badge ' + (d.isActive ? 'badge-success' : 'badge-danger') + '">' + (d.isActive ? 'Active' : 'Inactive') + '</span></td>';
                html += '</tr>';
            }
            tbody.innerHTML = html;
        }
    } catch(e) { console.log(e); }
}

async function loadAttendance() {
    const p = nowParts();
    try {
        const data = await api(API + '/companies/' + currentCompanyId + '/attendance/monthly/' + p.year + '/' + p.month);
        if (data.report) {
            document.getElementById('att-checkin').textContent = data.report.present || 0;
            document.getElementById('att-checkout').textContent = data.report.present || 0;
            document.getElementById('att-late').textContent = data.report.late || 0;
            document.getElementById('att-leave').textContent = data.report.onLeave || 0;
        }
    } catch(e) { console.log(e); }
}

async function loadPayroll() {
    const p = nowParts();
    try {
        const data = await api(API + '/companies/' + currentCompanyId + '/payroll/payslips?year=' + p.year + '&month=' + p.month + '&max=10');
        const tbody = document.getElementById('payslip-list');
        if (data.payslips && data.payslips.length > 0) {
            let html = '';
            for (let i = 0; i < data.payslips.length; i++) {
                const pl = data.payslips[i];
                const badgeClass = pl.status === 'APPROVED' ? 'success' : pl.status === 'GENERATED' ? 'info' : 'warning';
                html += '<tr>';
                html += '<td>' + (pl.employee && pl.employee.firstName ? pl.employee.firstName : '') + ' ' + (pl.employee && pl.employee.lastName ? pl.employee.lastName : '') + '</td>';
                html += '<td>' + (pl.month || '') + '/' + (pl.year || '') + '</td>';
                html += '<td>' + (pl.grossSalary ? pl.grossSalary.toFixed(2) : '-') + '</td>';
                html += '<td>' + (pl.totalDeductions ? pl.totalDeductions.toFixed(2) : '-') + '</td>';
                html += '<td>' + (pl.netSalary ? pl.netSalary.toFixed(2) : '-') + '</td>';
                html += '<td><span class="badge badge-' + badgeClass + '">' + pl.status + '</span></td>';
                html += '</tr>';
            }
            tbody.innerHTML = html;
        }
    } catch(e) { console.log(e); }
}

async function loadExpenses() {
    try {
        const data = await api(API + '/companies/' + currentCompanyId + '/expenses?max=10');
        const tbody = document.getElementById('expense-list');
        if (data.expenses && data.expenses.length > 0) {
            let html = '';
            for (let i = 0; i < data.expenses.length; i++) {
                const e = data.expenses[i];
                const badgeClass = e.status === 'PAID' ? 'success' : e.status === 'APPROVED' ? 'info' : 'warning';
                html += '<tr>';
                html += '<td>' + (e.employee && e.employee.firstName ? e.employee.firstName : '') + ' ' + (e.employee && e.employee.lastName ? e.employee.lastName : '') + '</td>';
                html += '<td>' + (e.expenseType || '-') + '</td>';
                html += '<td>' + (e.amount ? e.amount.toFixed(2) : '0') + ' AED</td>';
                html += '<td>' + (e.expenseDate || '-') + '</td>';
                html += '<td><span class="badge badge-' + badgeClass + '">' + e.status + '</span></td>';
                html += '</tr>';
            }
            tbody.innerHTML = html;
        }
    } catch(e) { console.log(e); }
}

async function loadRecruitment() {
    try {
        const data = await api(API + '/companies/' + currentCompanyId + '/recruitment/job-postings?max=10');
        const tbody = document.getElementById('job-list');
        if (data.postings && data.postings.length > 0) {
            let html = '';
            for (let i = 0; i < data.postings.length; i++) {
                const j = data.postings[i];
                const badgeClass = j.status === 'OPEN' ? 'success' : j.status === 'CLOSED' ? 'danger' : 'warning';
                html += '<tr>';
                html += '<td>' + (j.title || '-') + '</td>';
                html += '<td>' + (j.department || '-') + '</td>';
                html += '<td>' + (j.location || '-') + '</td>';
                html += '<td>' + (j.noOfPositions || '-') + '</td>';
                html += '<td><span class="badge badge-' + badgeClass + '">' + j.status + '</span></td>';
                html += '</tr>';
            }
            tbody.innerHTML = html;
        }
    } catch(e) { console.log(e); }
}

async function loadCompanies() {
    try {
        const data = await api(API + '/companies?max=10');
        const tbody = document.getElementById('company-list');
        if (data.companies && data.companies.length > 0) {
            let html = '';
            for (let i = 0; i < data.companies.length; i++) {
                const c = data.companies[i];
                html += '<tr>';
                html += '<td>' + (c.companyCode || '-') + '</td>';
                html += '<td>' + (c.companyName || '-') + '</td>';
                html += '<td>' + (c.email || '-') + '</td>';
                html += '<td>' + (c.noOfEmployees || 0) + '</td>';
                html += '<td><span class="badge ' + (c.isActive ? 'badge-success' : 'badge-danger') + '">' + (c.isActive ? 'Active' : 'Inactive') + '</span></td>';
                html += '</tr>';
            }
            tbody.innerHTML = html;
        }
    } catch(e) { console.log(e); }
}

async function saveEmployee(e) {
    e.preventDefault();
    const form = e.target;
    const data = new FormData(form);
    const obj = Object.fromEntries(data);
    const result = await api(API + '/companies/' + currentCompanyId + '/employees', { method: 'POST', body: JSON.stringify(obj) });
    if (result.employee) { closeModal('employee-modal'); loadEmployees(); loadDashboard(); }
    else alert(result.message || 'Failed');
}

async function saveDepartment(e) {
    e.preventDefault();
    const form = e.target;
    const data = new FormData(form);
    const obj = Object.fromEntries(data);
    try {
        const result = await api(API + '/companies/' + currentCompanyId + '/departments', { method: 'POST', body: JSON.stringify(obj) });
        if (result.department) { closeModal('department-modal'); loadDepartments(); }
        else alert(result.message || 'Failed to create department');
    } catch(err) {
        alert('Error: ' + (err.message || 'Unknown error'));
    }
}

async function saveDesignation(e) {
    e.preventDefault();
    const form = e.target;
    const data = new FormData(form);
    const obj = Object.fromEntries(data);
    try {
        const result = await api(API + '/companies/' + currentCompanyId + '/designations', { method: 'POST', body: JSON.stringify(obj) });
        if (result.designation) { closeModal('designation-modal'); loadDesignations(); }
        else alert(result.message || 'Failed to create designation');
    } catch(err) {
        alert('Error: ' + (err.message || 'Unknown error'));
    }
}

async function saveCompany(e) {
    e.preventDefault();
    const form = e.target;
    const data = new FormData(form);
    const obj = Object.fromEntries(data);
    const result = await api(API + '/companies', { method: 'POST', body: JSON.stringify(obj) });
    if (result.company) { closeModal('company-modal'); loadCompanies(); }
    else alert(result.message || 'Failed');
}

async function saveJob(e) {
    e.preventDefault();
    const form = e.target;
    const data = new FormData(form);
    const obj = Object.fromEntries(data);
    const result = await api(API + '/companies/' + currentCompanyId + '/recruitment/job-postings', { method: 'POST', body: JSON.stringify(obj) });
    if (result.posting) { closeModal('job-modal'); loadRecruitment(); }
    else alert(result.message || 'Failed');
}

async function saveExpense(e) {
    e.preventDefault();
    const form = e.target;
    const data = new FormData(form);
    const obj = Object.fromEntries(data);
    const result = await api(API + '/companies/' + currentCompanyId + '/expenses', { method: 'POST', body: JSON.stringify(obj) });
    if (result.expense) { closeModal('expense-modal'); loadExpenses(); }
    else alert(result.message || 'Failed');
}

async function generatePayslip() {
    const p = nowParts();
    try {
        await api(API + '/companies/' + currentCompanyId + '/payroll/payslips/generate/1/' + p.year + '/' + p.month, { method: 'POST' });
        loadPayroll();
    } catch(e) { alert('Payslip generation failed'); }
}

function saveSettings() {
    const settings = {
        timezone: document.getElementById('setting-timezone').value,
        currency: document.getElementById('setting-currency').value,
        vatRate: document.getElementById('setting-vat').value,
        workingHoursStart: document.getElementById('setting-work-start').value,
        workingHoursEnd: document.getElementById('setting-work-end').value,
        workingDays: document.getElementById('setting-working-days').value
    };
    alert('Settings saved! (In production, this would call the API)');
}

function createCompanyFromLogin() {
    document.getElementById('create-company-form').style.display = 'block';
}

function hideCreateCompanyForm() {
    document.getElementById('create-company-form').style.display = 'none';
}

async function saveCompanyFromLogin(e) {
    e.preventDefault();
    const form = e.target;
    const data = new FormData(form);
    const obj = Object.fromEntries(data);
    const result = await api(API + '/companies', { method: 'POST', body: JSON.stringify(obj) });
    if (result.company) {
        alert('Company created: ' + result.company.companyName);
        hideCreateCompanyForm();
        loadCompaniesForLogin();
    } else {
        alert(result.message || 'Failed to create company');
    }
}

async function loadCompaniesForLogin() {
    const select = document.getElementById('login-company');
    if (!select) return;
    try {
        const data = await api(API + '/companies?max=100');
        if (data.companies && data.companies.length > 0) {
            select.innerHTML = '';
            for (let i = 0; i < data.companies.length; i++) {
                const c = data.companies[i];
                const opt = document.createElement('option');
                opt.value = c.id;
                opt.textContent = c.companyName + ' (' + c.companyCode + ')';
                select.appendChild(opt);
            }
        } else {
            select.innerHTML = '<option value="">No companies found - please seed first</option>';
        }
    } catch(e) {
        select.innerHTML = '<option value="">Error loading companies</option>';
    }
}

document.addEventListener('DOMContentLoaded', () => {
    if (document.querySelector('.login-container')) {
        loadCompaniesForLogin();
        return;
    }
    loadDashboard();
});

async function loadEmployeeModalOptions() {
    try {
        const deptData = await api(API + '/companies/' + currentCompanyId + '/departments?max=200&isActive=true');
        const deptSelect = document.querySelector('#employee-modal select[name="departmentId"]');
        if (deptSelect && deptData.departments) {
            deptSelect.innerHTML = '<option value="">-- Select --</option>';
            for (let i = 0; i < deptData.departments.length; i++) {
                const d = deptData.departments[i];
                const opt = document.createElement('option');
                opt.value = d.id;
                opt.textContent = d.name;
                deptSelect.appendChild(opt);
            }
        }
        const desigData = await api(API + '/companies/' + currentCompanyId + '/designations?max=200&isActive=true');
        const desigSelect = document.querySelector('#employee-modal select[name="designationId"]');
        if (desigSelect && Array.isArray(desigData)) {
            desigSelect.innerHTML = '<option value="">-- Select --</option>';
            for (let i = 0; i < desigData.length; i++) {
                const d = desigData[i];
                const opt = document.createElement('option');
                opt.value = d.id;
                opt.textContent = d.name;
                desigSelect.appendChild(opt);
            }
        }
    } catch(e) { console.log('Failed to load modal options', e); }
}
