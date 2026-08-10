<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Manager Dashboard - HRMS</title>
    <link rel="stylesheet" href="${resource(dir: 'css', file: 'main.css')}">
    <link rel="stylesheet" href="${resource(dir: 'css', file: 'mobile.css')}">
    <meta name="company-id" content="${session.companyId ?: 1}"/>
    <asset:javascript src="application.js"/>
    <style>
        * { margin: 0; padding: 0; box-sizing: border-box; }
        body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background: #f5f7fa; }
        .sidebar { width: 260px; height: 100vh; background: linear-gradient(180deg, #e65100 0%, #f57c00 100%); position: fixed; left: 0; top: 0; overflow-y: auto; z-index: 100; }
        .sidebar-header { padding: 24px 20px; border-bottom: 1px solid rgba(255,255,255,0.1); }
        .sidebar-header h2 { color: #fff; font-size: 20px; font-weight: 600; }
        .sidebar-header p { color: rgba(255,255,255,0.6); font-size: 12px; margin-top: 4px; }
        .nav-menu { padding: 16px 0; }
        .nav-item { display: flex; align-items: center; padding: 12px 20px; color: rgba(255,255,255,0.8); text-decoration: none; transition: all 0.2s; cursor: pointer; }
        .nav-item:hover { background: rgba(255,255,255,0.1); color: #fff; }
        .nav-item.active { background: rgba(255,255,255,0.15); color: #fff; border-left: 3px solid #ffb74d; }
        .nav-item svg { width: 20px; height: 20px; margin-right: 12px; }
        .nav-section { padding: 8px 20px; color: rgba(255,255,255,0.4); font-size: 11px; text-transform: uppercase; letter-spacing: 1px; margin-top: 8px; }
        .main-content { margin-left: 260px; padding: 24px; }
        .top-bar { background: #fff; padding: 16px 24px; border-radius: 12px; margin-bottom: 24px; display: flex; justify-content: space-between; align-items: center; box-shadow: 0 1px 3px rgba(0,0,0,0.08); }
        .top-bar h1 { font-size: 24px; font-weight: 600; color: #e65100; }
        .user-info { display: flex; align-items: center; gap: 12px; }
        .user-avatar { width: 40px; height: 40px; border-radius: 50%; background: #e65100; color: #fff; display: flex; align-items: center; justify-content: center; font-weight: 600; }
        .stats-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(240px, 1fr)); gap: 20px; margin-bottom: 24px; }
        .stat-card { background: #fff; border-radius: 12px; padding: 24px; box-shadow: 0 1px 3px rgba(0,0,0,0.08); }
        .stat-card h3 { color: #666; font-size: 14px; font-weight: 500; margin-bottom: 8px; }
        .stat-card .value { font-size: 32px; font-weight: 700; color: #e65100; }
        .stat-card .change { font-size: 12px; color: #4caf50; margin-top: 4px; }
        .card { background: #fff; border-radius: 12px; padding: 24px; box-shadow: 0 1px 3px rgba(0,0,0,0.08); margin-bottom: 24px; }
        .card h2 { font-size: 18px; font-weight: 600; color: #e65100; margin-bottom: 16px; }
        .btn { padding: 10px 20px; border: none; border-radius: 8px; cursor: pointer; font-size: 14px; font-weight: 500; transition: all 0.2s; }
        .btn-primary { background: #e65100; color: #fff; }
        .btn-primary:hover { background: #f57c00; }
        .btn-success { background: #4caf50; color: #fff; }
        .btn-danger { background: #f44336; color: #fff; }
        .btn-outline { background: transparent; border: 1px solid #e65100; color: #e65100; }
        table { width: 100%; border-collapse: collapse; }
        th, td { padding: 12px 16px; text-align: left; border-bottom: 1px solid #eee; }
        th { font-weight: 600; color: #666; font-size: 13px; text-transform: uppercase; }
        td { color: #333; font-size: 14px; }
        .badge { padding: 4px 12px; border-radius: 20px; font-size: 12px; font-weight: 500; }
        .badge-success { background: #e8f5e9; color: #2e7d32; }
        .badge-warning { background: #fff3e0; color: #ef6c00; }
        .badge-danger { background: #ffebee; color: #c62828; }
        .badge-info { background: #e3f2fd; color: #1565c0; }
        .modal { display: none; position: fixed; top: 0; left: 0; width: 100%; height: 100%; background: rgba(0,0,0,0.5); z-index: 1000; align-items: center; justify-content: center; }
        .modal.active { display: flex; }
        .modal-content { background: #fff; border-radius: 16px; padding: 32px; max-width: 600px; width: 90%; max-height: 90vh; overflow-y: auto; }
        .modal-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 24px; }
        .modal-header h2 { font-size: 20px; color: #e65100; }
        .close-btn { background: none; border: none; font-size: 24px; cursor: pointer; color: #666; }
        .form-group { margin-bottom: 16px; }
        .form-group label { display: block; font-size: 14px; font-weight: 500; color: #333; margin-bottom: 6px; }
        .form-group input, .form-group select, .form-group textarea { width: 100%; padding: 10px 14px; border: 1px solid #ddd; border-radius: 8px; font-size: 14px; }
        .form-row { display: grid; grid-template-columns: 1fr 1fr; gap: 16px; }
        .empty-state { text-align: center; padding: 48px; color: #999; }
    </style>
</head>
<body>
    <div class="sidebar">
        <div class="sidebar-header">
            <h2>HRMS</h2>
            <p>Manager Dashboard</p>
        </div>
        <nav class="nav-menu">
            <div class="nav-section">Main</div>
            <a class="nav-item active" onclick="showSection('dashboard')">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M3 9l9-7 9 7v11a2 2 0 01-2 2H5a2 2 0 01-2-2z"/><polyline points="9 22 9 12 15 12 15 22"/></svg>
                Dashboard
            </a>
            <div class="nav-section">Team</div>
            <a class="nav-item" onclick="showSection('employees')">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M17 21v-2a4 4 0 00-4-4H5a4 4 0 00-4 4v2"/><circle cx="9" cy="7" r="4"/><path d="M23 21v-2a4 4 0 00-3-3.87M16 3.13a4 4 0 010 7.75"/></svg>
                My Team
            </a>
            <a class="nav-item" onclick="showSection('designations')">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M20 21v-2a4 4 0 00-4-4H8a4 4 0 00-4 4v2"/><circle cx="12" cy="7" r="4"/></svg>
                Designations
            </a>
            <div class="nav-section">Time</div>
            <a class="nav-item" onclick="showSection('attendance')">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="10"/><polyline points="12 6 12 12 16 14"/></svg>
                Attendance
            </a>
            <a class="nav-item" onclick="showSection('leaves')">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M14 2H6a2 2 0 00-2 2v16a2 2 0 002 2h12a2 2 0 002-2V8z"/><polyline points="14 2 14 8 20 8"/></svg>
                Leave Requests
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
                <span id="company-badge" style="background:#e65100;color:#fff;padding:4px 12px;border-radius:20px;font-size:12px;margin-right:8px">${session.currentUser?.company?.companyName ?: 'No Company'}</span>
                <span id="user-name-display">${session.currentUser?.getFullName() ?: 'Manager Demo'}</span>
                <div class="user-avatar" id="user-avatar-display">${session.currentUser?.firstName?.charAt(0) ?: 'M'}</div>
            </div>
        </div>

        <div id="dashboard-section" class="section active">
            <div class="stats-grid">
                <div class="stat-card"><h3>Team Members</h3><div class="value" id="stat-team">0</div><div class="change">Direct reports</div></div>
                <div class="stat-card"><h3>Present Today</h3><div class="value" id="stat-present">0</div><div class="change">On site</div></div>
                <div class="stat-card"><h3>On Leave</h3><div class="value" id="stat-leave">0</div><div class="change">Away today</div></div>
                <div class="stat-card"><h3>Pending Leaves</h3><div class="value" id="stat-pending-leaves">0</div><div class="change">To approve</div></div>
            </div>
            <div class="card">
                <h2>My Team</h2>
                <table>
                    <thead><tr><th>Code</th><th>Name</th><th>Department</th><th>Status</th></tr></thead>
                    <tbody id="team-list"><tr><td colspan="4" class="empty-state">No data available</td></tr></tbody>
                </table>
            </div>
        </div>

        <div id="employees-section" class="section" style="display:none">
            <div class="card">
                <h2>My Team</h2>
                <table>
                    <thead><tr><th>Code</th><th>Name</th><th>Email</th><th>Department</th><th>Status</th></tr></thead>
                    <tbody id="team-detail-list"></tbody>
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

        <div id="leaves-section" class="section" style="display:none">
            <div class="card">
                <h2>Leave Requests</h2>
                <table>
                    <thead><tr><th>Employee</th><th>Type</th><th>From</th><th>To</th><th>Status</th><th>Actions</th></tr></thead>
                    <tbody id="leave-list"></tbody>
                </table>
            </div>
        </div>
    </div>

    <script>
        const roleNames = { admin: 'Admin Demo', hr: 'HR Demo', manager: 'Manager Demo', employee: 'Employee Demo' };
        const roleAvatars = { admin: 'A', hr: 'H', manager: 'M', employee: 'E' };
        const role = sessionStorage.getItem('userRole') || 'manager';
        document.getElementById('user-name-display').textContent = roleNames[role] || role;
        document.getElementById('user-avatar-display').textContent = roleAvatars[role] || '?';
        function loadTeam() {
            api(API + '/companies/' + currentCompanyId + '/employees?max=20').then(data => {
                const tbody = document.getElementById('team-list');
                const tbody2 = document.getElementById('team-detail-list');
                if (data.employees && data.employees.length > 0) {
                    document.getElementById('stat-team').textContent = data.employees.length;
                    let html1 = '', html2 = '';
                    for (let i = 0; i < data.employees.length; i++) {
                        const e = data.employees[i];
                        html1 += '<tr><td>' + (e.employeeCode || '-') + '</td><td>' + (e.firstName || '') + ' ' + (e.lastName || '') + '</td><td>' + (e.department && e.department.name ? e.department.name : '-') + '</td><td><span class="badge ' + (e.isActive ? 'badge-success' : 'badge-danger') + '">' + (e.isActive ? 'Active' : 'Inactive') + '</span></td></tr>';
                        html2 += '<tr><td>' + (e.employeeCode || '-') + '</td><td>' + (e.firstName || '') + ' ' + (e.lastName || '') + '</td><td>' + (e.email || '-') + '</td><td>' + (e.department && e.department.name ? e.department.name : '-') + '</td><td><span class="badge ' + (e.isActive ? 'badge-success' : 'badge-danger') + '">' + (e.isActive ? 'Active' : 'Inactive') + '</span></td></tr>';
                    }
                    tbody.innerHTML = html1;
                    if (tbody2) tbody2.innerHTML = html2;
                }
            }).catch(e => console.log(e));
        }
        document.addEventListener('DOMContentLoaded', () => { loadDashboard(); loadTeam(); });
    </script>
</body>
</html>