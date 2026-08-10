<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Admin Dashboard - HRMS</title>
    <link rel="stylesheet" href="${resource(dir: 'css', file: 'main.css')}">
    <link rel="stylesheet" href="${resource(dir: 'css', file: 'mobile.css')}">
    <meta name="company-id" content="${session.companyId ?: 1}"/>
    <asset:javascript src="application.js"/>
    <style>
        * { margin: 0; padding: 0; box-sizing: border-box; }
        body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background: #f5f7fa; }
        .sidebar { width: 260px; height: 100vh; background: linear-gradient(180deg, #1a237e 0%, #0d47a1 100%); position: fixed; left: 0; top: 0; overflow-y: auto; z-index: 100; }
        .sidebar-header { padding: 24px 20px; border-bottom: 1px solid rgba(255,255,255,0.1); }
        .sidebar-header h2 { color: #fff; font-size: 20px; font-weight: 600; }
        .sidebar-header p { color: rgba(255,255,255,0.6); font-size: 12px; margin-top: 4px; }
        .nav-menu { padding: 16px 0; }
        .nav-item { display: flex; align-items: center; padding: 12px 20px; color: rgba(255,255,255,0.8); text-decoration: none; transition: all 0.2s; cursor: pointer; }
        .nav-item:hover { background: rgba(255,255,255,0.1); color: #fff; }
        .nav-item.active { background: rgba(255,255,255,0.15); color: #fff; border-left: 3px solid #64b5f6; }
        .nav-item svg { width: 20px; height: 20px; margin-right: 12px; }
        .nav-section { padding: 8px 20px; color: rgba(255,255,255,0.4); font-size: 11px; text-transform: uppercase; letter-spacing: 1px; margin-top: 8px; }
        .main-content { margin-left: 260px; padding: 24px; }
        .top-bar { background: #fff; padding: 16px 24px; border-radius: 12px; margin-bottom: 24px; display: flex; justify-content: space-between; align-items: center; box-shadow: 0 1px 3px rgba(0,0,0,0.08); }
        .top-bar h1 { font-size: 24px; font-weight: 600; color: #1a237e; }
        .user-info { display: flex; align-items: center; gap: 12px; }
        .user-avatar { width: 40px; height: 40px; border-radius: 50%; background: #1a237e; color: #fff; display: flex; align-items: center; justify-content: center; font-weight: 600; }
        .stats-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(240px, 1fr)); gap: 20px; margin-bottom: 24px; }
        .stat-card { background: #fff; border-radius: 12px; padding: 24px; box-shadow: 0 1px 3px rgba(0,0,0,0.08); }
        .stat-card h3 { color: #666; font-size: 14px; font-weight: 500; margin-bottom: 8px; }
        .stat-card .value { font-size: 32px; font-weight: 700; color: #1a237e; }
        .stat-card .change { font-size: 12px; color: #4caf50; margin-top: 4px; }
        .card { background: #fff; border-radius: 12px; padding: 24px; box-shadow: 0 1px 3px rgba(0,0,0,0.08); margin-bottom: 24px; }
        .card h2 { font-size: 18px; font-weight: 600; color: #1a237e; margin-bottom: 16px; }
        .btn { padding: 10px 20px; border: none; border-radius: 8px; cursor: pointer; font-size: 14px; font-weight: 500; transition: all 0.2s; }
        .btn-primary { background: #1a237e; color: #fff; }
        .btn-primary:hover { background: #0d47a1; }
        .btn-success { background: #4caf50; color: #fff; }
        .btn-danger { background: #f44336; color: #fff; }
        .btn-outline { background: transparent; border: 1px solid #1a237e; color: #1a237e; }
        table { width: 100%; border-collapse: collapse; }
        th, td { padding: 12px 16px; text-align: left; border-bottom: 1px solid #eee; }
        th { font-weight: 600; color: #666; font-size: 13px; text-transform: uppercase; }
        td { color: #333; font-size: 14px; }
        .badge { padding: 4px 12px; border-radius: 20px; font-size: 12px; font-weight: 500; }
        .badge-success { background: #e8f5e9; color: #2e7d32; }
        .badge-warning { background: #fff3e0; color: #ef6c00; }
        .badge-danger { background: #ffebee; color: #c62828; }
        .badge-info { background: #e3f2fd; color: #1565c0; }
        .form-group { margin-bottom: 16px; }
        .form-group label { display: block; font-size: 14px; font-weight: 500; color: #333; margin-bottom: 6px; }
        .form-group input, .form-group select, .form-group textarea { width: 100%; padding: 10px 14px; border: 1px solid #ddd; border-radius: 8px; font-size: 14px; }
        .form-row { display: grid; grid-template-columns: 1fr 1fr; gap: 16px; }
        .modal { display: none; position: fixed; top: 0; left: 0; width: 100%; height: 100%; background: rgba(0,0,0,0.5); z-index: 1000; align-items: center; justify-content: center; }
        .modal.active { display: flex; }
        .modal-content { background: #fff; border-radius: 16px; padding: 32px; max-width: 600px; width: 90%; max-height: 90vh; overflow-y: auto; }
        .modal-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 24px; }
        .modal-header h2 { font-size: 20px; color: #1a237e; }
        .close-btn { background: none; border: none; font-size: 24px; cursor: pointer; color: #666; }
        .empty-state { text-align: center; padding: 48px; color: #999; }
    </style>
</head>
<body>
    <div class="sidebar">
        <div class="sidebar-header">
            <h2>HRMS</h2>
            <p>Admin Dashboard</p>
        </div>
        <nav class="nav-menu">
            <div class="nav-section">Main</div>
            <a class="nav-item active" onclick="showSection('dashboard')">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M3 9l9-7 9 7v11a2 2 0 01-2 2H5a2 2 0 01-2-2z"/><polyline points="9 22 9 12 15 12 15 22"/></svg>
                Dashboard
            </a>
            <div class="nav-section">Employees</div>
            <a class="nav-item" onclick="showSection('employees')">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M17 21v-2a4 4 0 00-4-4H5a4 4 0 00-4 4v2"/><circle cx="9" cy="7" r="4"/><path d="M23 21v-2a4 4 0 00-3-3.87M16 3.13a4 4 0 010 7.75"/></svg>
                Employees
            </a>
            <a class="nav-item" onclick="showSection('departments')">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="2" y="7" width="20" height="14" rx="2" ry="2"/><path d="M16 21V5a2 2 0 00-2-2h-4a2 2 0 00-2 2v16"/></svg>
                Departments
            </a>
            <a class="nav-item" onclick="showSection('designations')">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M20 21v-2a4 4 0 00-4-4H8a4 4 0 00-4 4v2"/><circle cx="12" cy="7" r="4"/></svg>
                Designations
            </a>
            <div class="nav-section">Attendance</div>
            <a class="nav-item" onclick="showSection('attendance')">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="10"/><polyline points="12 6 12 12 16 14"/></svg>
                Time & Attendance
            </a>
            <a class="nav-item" onclick="showSection('leaves')">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M14 2H6a2 2 0 00-2 2v16a2 2 0 002 2h12a2 2 0 002-2V8z"/><polyline points="14 2 14 8 20 8"/></svg>
                Leave Management
            </a>
            <div class="nav-section">Payroll</div>
            <a class="nav-item" onclick="showSection('payroll')">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><line x1="12" y1="1" x2="12" y2="23"/><path d="M17 5H9.5a3.5 3.5 0 000 7h5a3.5 3.5 0 010 7H6"/></svg>
                Payroll
            </a>
            <a class="nav-item" onclick="showSection('expenses')">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="1" y="4" width="22" height="16" rx="2" ry="2"/><line x1="1" y1="10" x2="23" y2="10"/></svg>
                Expenses
            </a>
            <div class="nav-section">Recruitment</div>
            <a class="nav-item" onclick="showSection('recruitment')">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M20 21v-2a4 4 0 00-4-4H8a4 4 0 00-4 4v2"/><circle cx="12" cy="7" r="4"/></svg>
                Recruitment
            </a>
            <div class="nav-section">Settings</div>
            <a class="nav-item" onclick="showSection('companies')">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M3 9l9-7 9 7v11a2 2 0 01-2 2H5a2 2 0 01-2-2z"/></svg>
                Companies
            </a>
            <a class="nav-item" onclick="showSection('settings')">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="3"/><path d="M19.4 15a1.65 1.65 0 00.33 1.82l.06.06a2 2 0 010 2.83 2 2 0 01-2.83 0l-.06-.06a1.65 1.65 0 00-1.82-.33 1.65 1.65 0 00-1 1.51V21a2 2 0 01-2 2 2 2 0 01-2-2v-.09A1.65 1.65 0 009 19.4a1.65 1.65 0 00-1.82.33l-.06.06a2 2 0 01-2.83 0 2 2 0 010-2.83l.06-.06A1.65 1.65 0 004.68 15a1.65 1.65 0 00-1.51-1H3a2 2 0 01-2-2 2 2 0 012-2h.09A1.65 1.65 0 004.6 9a1.65 1.65 0 00-.33-1.82l-.06-.06a2 2 0 010-2.83 2 2 0 012.83 0l.06.06A1.65 1.65 0 009 4.68a1.65 1.65 0 001-1.51V3a2 2 0 012-2 2 2 0 012 2v.09a1.65 1.65 0 001 1.51 1.65 1.65 0 001.82-.33l.06-.06a2 2 0 012.83 0 2 2 0 010 2.83l-.06.06a1.65 1.65 0 00-.33 1.82V9a1.65 1.65 0 001.51 1H21a2 2 0 012 2 2 2 0 01-2 2h-.09a1.65 1.65 0 00-1.51 1z"/></svg>
                Settings
            </a>
            <a class="nav-item" onclick="logout()">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M9 21H5a2 2 0 01-2-2V5a2 2 0 012-2h4"/><polyline points="16 17 21 12 16 7"/><line x1="21" y1="12" x2="9" y2="12"/></svg>
                Logout
            </a>
        </nav>
    </div>

    <div class="main-content">
        <div class="top-bar">
            <h1 id="page-title">Dashboard</h1>
            <div class="user-info">
                <span id="company-badge" style="background:#1a237e;color:#fff;padding:4px 12px;border-radius:20px;font-size:12px;margin-right:8px">${session.currentUser?.company?.companyName ?: 'No Company'}</span>
                <span id="user-name-display">${session.currentUser?.getFullName() ?: 'Admin Demo'}</span>
                <div class="user-avatar" id="user-avatar-display">${session.currentUser?.firstName?.charAt(0) ?: 'A'}</div>
            </div>
        </div>

        <div id="dashboard-section" class="section active">
            <div class="stats-grid">
                <div class="stat-card"><h3>Total Employees</h3><div class="value" id="stat-employees">0</div><div class="change">Active workforce</div></div>
                <div class="stat-card"><h3>Present Today</h3><div class="value" id="stat-present">0</div><div class="change">On site</div></div>
                <div class="stat-card"><h3>On Leave</h3><div class="value" id="stat-leave">0</div><div class="change">Away today</div></div>
                <div class="stat-card"><h3>Open Positions</h3><div class="value" id="stat-jobs">0</div><div class="change">Recruiting</div></div>
            </div>
            <div class="card">
                <h2>Recent Employees</h2>
                <table>
                    <thead><tr><th>Code</th><th>Name</th><th>Department</th><th>Designation</th><th>Status</th></tr></thead>
                    <tbody id="recent-employees"><tr><td colspan="5" class="empty-state">No data available</td></tr></tbody>
                </table>
            </div>
        </div>

        <div id="employees-section" class="section" style="display:none">
            <div class="card">
                <div style="display:flex;justify-content:space-between;align-items:center;margin-bottom:16px">
                    <h2 style="margin:0">Employee List</h2>
                    <button class="btn btn-primary" onclick="openModal('employee-modal')">+ Add Employee</button>
                </div>
                <table>
                    <thead><tr><th>Code</th><th>Name</th><th>Email</th><th>Department</th><th>Designation</th><th>Status</th><th>Actions</th></tr></thead>
                    <tbody id="employee-list"></tbody>
                </table>
            </div>
        </div>

        <div id="departments-section" class="section" style="display:none">
            <div class="card">
                <div style="display:flex;justify-content:space-between;align-items:center;margin-bottom:16px">
                    <h2 style="margin:0">Departments</h2>
                    <button class="btn btn-primary" onclick="openModal('department-modal')">+ Add Department</button>
                </div>
                <table>
                    <thead><tr><th>Code</th><th>Name</th><th>Employees</th><th>Status</th></tr></thead>
                    <tbody id="department-list"></tbody>
                </table>
            </div>
        </div>

        <div id="designations-section" class="section" style="display:none">
            <div class="card">
                <div style="display:flex;justify-content:space-between;align-items:center;margin-bottom:16px">
                    <h2 style="margin:0">Designations</h2>
                    <button class="btn btn-primary" onclick="openModal('designation-modal')">+ Add Designation</button>
                </div>
                <table>
                    <thead><tr><th>Code</th><th>Name</th><th>Department</th><th>Status</th></tr></thead>
                    <tbody id="designation-list"></tbody>
                </table>
            </div>
        </div>

        <div id="attendance-section" class="section" style="display:none">
            <div class="card">
                <h2>Attendance Overview</h2>
                <div class="stats-grid" style="margin-top:16px">
                    <div class="stat-card"><h3>Check-ins Today</h3><div class="value" id="att-checkin">0</div></div>
                    <div class="stat-card"><h3>Check-outs Today</h3><div class="value" id="att-checkout">0</div></div>
                    <div class="stat-card"><h3>Late Today</h3><div class="value" id="att-late">0</div></div>
                    <div class="stat-card"><h3>On Leave</h3><div class="value" id="att-leave">0</div></div>
                </div>
            </div>
        </div>

        <div id="payroll-section" class="section" style="display:none">
            <div class="card">
                <div style="display:flex;justify-content:space-between;align-items:center;margin-bottom:16px">
                    <h2 style="margin:0">Payroll</h2>
                    <button class="btn btn-primary" onclick="generatePayslip()">Generate Payslip</button>
                </div>
                <table>
                    <thead><tr><th>Employee</th><th>Month</th><th>Gross</th><th>Deductions</th><th>Net</th><th>Status</th></tr></thead>
                    <tbody id="payslip-list"></tbody>
                </table>
            </div>
        </div>

        <div id="expenses-section" class="section" style="display:none">
            <div class="card">
                <div style="display:flex;justify-content:space-between;align-items:center;margin-bottom:16px">
                    <h2 style="margin:0">Expenses</h2>
                    <button class="btn btn-primary" onclick="openModal('expense-modal')">+ Add Expense</button>
                </div>
                <table>
                    <thead><tr><th>Employee</th><th>Type</th><th>Amount</th><th>Date</th><th>Status</th></tr></thead>
                    <tbody id="expense-list"></tbody>
                </table>
            </div>
        </div>

        <div id="recruitment-section" class="section" style="display:none">
            <div class="card">
                <div style="display:flex;justify-content:space-between;align-items:center;margin-bottom:16px">
                    <h2 style="margin:0">Recruitment</h2>
                    <button class="btn btn-primary" onclick="openModal('job-modal')">+ Post Job</button>
                </div>
                <table>
                    <thead><tr><th>Job Title</th><th>Department</th><th>Location</th><th>Positions</th><th>Status</th></tr></thead>
                    <tbody id="job-list"></tbody>
                </table>
            </div>
        </div>

        <div id="companies-section" class="section" style="display:none">
            <div class="card">
                <div style="display:flex;justify-content:space-between;align-items:center;margin-bottom:16px">
                    <h2 style="margin:0">Companies</h2>
                    <button class="btn btn-primary" onclick="openModal('company-modal')">+ Add Company</button>
                </div>
                <table>
                    <thead><tr><th>Code</th><th>Company Name</th><th>Email</th><th>Employees</th><th>Status</th></tr></thead>
                    <tbody id="company-list"></tbody>
                </table>
            </div>
        </div>

        <div id="settings-section" class="section" style="display:none">
            <div class="card">
                <h2>Company Settings</h2>
                <div class="form-row">
                    <div class="form-group"><label>Timezone</label>
                        <select id="setting-timezone">
                            <option value="Asia/Dubai" selected>Asia/Dubai (UAE)</option>
                            <option value="Asia/Riyadh">Asia/Riyadh (Saudi)</option>
                            <option value="Asia/Qatar">Asia/Qatar</option>
                            <option value="Asia/Kuwait">Asia/Kuwait</option>
                        </select>
                    </div>
                    <div class="form-group"><label>Currency</label>
                        <select id="setting-currency">
                            <option value="AED" selected>AED</option>
                            <option value="SAR">SAR</option>
                            <option value="QAR">QAR</option>
                            <option value="KWD">KWD</option>
                            <option value="USD">USD</option>
                        </select>
                    </div>
                </div>
                <div class="form-row">
                    <div class="form-group"><label>VAT Rate (%)</label><input type="number" id="setting-vat" value="5" step="0.01"></div>
                    <div class="form-group"><label>Working Hours Start</label><input type="time" id="setting-work-start" value="09:00"></div>
                </div>
                <div class="form-row">
                    <div class="form-group"><label>Working Hours End</label><input type="time" id="setting-work-end" value="18:00"></div>
                    <div class="form-group"><label>Working Days</label>
                        <select id="setting-working-days">
                            <option value="Mon,Tue,Wed,Thu,Fri" selected>Mon-Fri (UAE)</option>
                            <option value="Sun,Mon,Tue,Wed,Thu">Sun-Thu (Saudi)</option>
                        </select>
                    </div>
                </div>
                <button class="btn btn-primary" onclick="saveSettings()">Save Settings</button>
            </div>
        </div>
    </div>

    <!-- Modals -->
    <div id="employee-modal" class="modal">
        <div class="modal-content">
            <div class="modal-header"><h2>Add Employee</h2><button class="close-btn" onclick="closeModal('employee-modal')">&times;</button></div>
            <form id="employee-form" onsubmit="saveEmployee(event)">
                <div class="form-row">
                    <div class="form-group"><label>First Name *</label><input name="firstName" required></div>
                    <div class="form-group"><label>Last Name *</label><input name="lastName" required></div>
                </div>
                <div class="form-row">
                    <div class="form-group"><label>Email</label><input name="email" type="email"></div>
                    <div class="form-group"><label>Phone</label><input name="primaryPhone"></div>
                </div>
                <div class="form-row">
                    <div class="form-group"><label>Employee Code</label><input name="employeeCode"></div>
                    <div class="form-group"><label>Job Title</label><input name="jobTitle"></div>
                </div>
                <div class="form-row">
                    <div class="form-group"><label>Department</label><select name="departmentId"><option value="">-- Select --</option></select></div>
                    <div class="form-group"><label>Designation</label><select name="designationId"><option value="">-- Select --</option></select></div>
                </div>
                <div class="form-row">
                    <div class="form-group"><label>Joining Date</label><input name="joiningDate" type="date"></div>
                    <div class="form-group"><label>Employment Type</label>
                        <select name="employmentType">
                            <option value="FULL_TIME">Full Time</option>
                            <option value="PART_TIME">Part Time</option>
                            <option value="CONTRACT">Contract</option>
                        </select>
                    </div>
                </div>
                <button type="submit" class="btn btn-primary" style="width:100%">Save Employee</button>
            </form>
        </div>
    </div>

    <div id="department-modal" class="modal">
        <div class="modal-content">
            <div class="modal-header"><h2>Add Department</h2><button class="close-btn" onclick="closeModal('department-modal')">&times;</button></div>
            <form onsubmit="saveDepartment(event)">
                <div class="form-group"><label>Department Name *</label><input name="name" required></div>
                <div class="form-group"><label>Code *</label><input name="code" required></div>
                <div class="form-group"><label>Description</label><textarea name="description" rows="3"></textarea></div>
                <button type="submit" class="btn btn-primary" style="width:100%">Save Department</button>
            </form>
        </div>
    </div>

    <div id="company-modal" class="modal">
        <div class="modal-content">
            <div class="modal-header"><h2>Add Company</h2><button class="close-btn" onclick="closeModal('company-modal')">&times;</button></div>
            <form onsubmit="saveCompany(event)">
                <div class="form-row">
                    <div class="form-group"><label>Company Name *</label><input name="companyName" required></div>
                    <div class="form-group"><label>Company Code *</label><input name="companyCode" required></div>
                </div>
                <div class="form-group"><label>Email</label><input name="email" type="email"></div>
                <div class="form-group"><label>Phone</label><input name="phoneNumber"></div>
                <div class="form-row">
                    <div class="form-group"><label>City</label><input name="city" value="Dubai"></div>
                    <div class="form-group"><label>Country</label><input name="country" value="UAE"></div>
                </div>
                <button type="submit" class="btn btn-primary" style="width:100%">Save Company</button>
            </form>
        </div>
    </div>

    <div id="job-modal" class="modal">
        <div class="modal-content">
            <div class="modal-header"><h2>Post Job</h2><button class="close-btn" onclick="closeModal('job-modal')">&times;</button></div>
            <form onsubmit="saveJob(event)">
                <div class="form-group"><label>Job Title *</label><input name="title" required></div>
                <div class="form-row">
                    <div class="form-group"><label>Department</label><input name="department"></div>
                    <div class="form-group"><label>Location</label><input name="location" value="Dubai, UAE"></div>
                </div>
                <div class="form-row">
                    <div class="form-group"><label>Employment Type</label>
                        <select name="employmentType">
                            <option value="FULL_TIME">Full Time</option>
                            <option value="PART_TIME">Part Time</option>
                            <option value="CONTRACT">Contract</option>
                        </select>
                    </div>
                    <div class="form-group"><label>Positions</label><input name="noOfPositions" type="number"></div>
                </div>
                <div class="form-row">
                    <div class="form-group"><label>Min Salary (AED)</label><input name="minSalary" type="number"></div>
                    <div class="form-group"><label>Max Salary (AED)</label><input name="maxSalary" type="number"></div>
                </div>
                <div class="form-group"><label>Description</label><textarea name="description" rows="3"></textarea></div>
                <button type="submit" class="btn btn-primary" style="width:100%">Post Job</button>
            </form>
        </div>
    </div>

    <div id="expense-modal" class="modal">
        <div class="modal-content">
            <div class="modal-header"><h2>Add Expense</h2><button class="close-btn" onclick="closeModal('expense-modal')">&times;</button></div>
            <form onsubmit="saveExpense(event)">
                <div class="form-group"><label>Employee</label><select name="employeeId"></select></div>
                <div class="form-group"><label>Expense Type *</label>
                    <select name="expenseType">
                        <option value="TRAVEL">Travel</option>
                        <option value="MEALS">Meals</option>
                        <option value="OFFICE_SUPPLIES">Office Supplies</option>
                        <option value="UTILITIES">Utilities</option>
                        <option value="MAINTENANCE">Maintenance</option>
                        <option value="MARKETING">Marketing</option>
                        <option value="OTHER">Other</option>
                    </select>
                </div>
                <div class="form-row">
                    <div class="form-group"><label>Amount (AED)</label><input name="amount" type="number" step="0.01"></div>
                    <div class="form-group"><label>Expense Date</label><input name="expenseDate" type="date"></div>
                </div>
                <div class="form-group"><label>Description</label><textarea name="description" rows="2"></textarea></div>
                <button type="submit" class="btn btn-primary" style="width:100%">Submit Expense</button>
            </form>
        </div>
    </div>

    <script>
        const roleNames = { admin: 'Admin Demo', hr: 'HR Demo', manager: 'Manager Demo', employee: 'Employee Demo' };
        const roleAvatars = { admin: 'A', hr: 'H', manager: 'M', employee: 'E' };
        const role = sessionStorage.getItem('userRole') || 'admin';
        document.getElementById('user-name-display').textContent = roleNames[role] || role;
        document.getElementById('user-avatar-display').textContent = roleAvatars[role] || '?';
        async function saveDepartment(e) {
            e.preventDefault();
            const form = e.target;
            const data = new FormData(form);
            const obj = Object.fromEntries(data);
            const result = await api(API + '/companies/' + currentCompanyId + '/departments', { method: 'POST', body: JSON.stringify(obj) });
            if (result.department) { closeModal('department-modal'); loadDepartments(); }
            else alert(result.message || 'Failed to create department');
        }
        document.addEventListener('DOMContentLoaded', () => { loadDashboard(); });
    </script>
</body>
</html>