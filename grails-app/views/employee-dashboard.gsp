<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Employee Dashboard - HRMS</title>
    <link rel="stylesheet" href="${resource(dir: 'css', file: 'main.css')}">
    <link rel="stylesheet" href="${resource(dir: 'css', file: 'mobile.css')}">
    <meta name="company-id" content="${session.companyId ?: 1}"/>
    <asset:javascript src="application.js"/>
    <style>
        * { margin: 0; padding: 0; box-sizing: border-box; }
        body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background: #f5f7fa; }
        .sidebar { width: 260px; height: 100vh; background: linear-gradient(180deg, #4a148c 0%, #7b1fa2 100%); position: fixed; left: 0; top: 0; overflow-y: auto; z-index: 100; }
        .sidebar-header { padding: 24px 20px; border-bottom: 1px solid rgba(255,255,255,0.1); }
        .sidebar-header h2 { color: #fff; font-size: 20px; font-weight: 600; }
        .sidebar-header p { color: rgba(255,255,255,0.6); font-size: 12px; margin-top: 4px; }
        .nav-menu { padding: 16px 0; }
        .nav-item { display: flex; align-items: center; padding: 12px 20px; color: rgba(255,255,255,0.8); text-decoration: none; transition: all 0.2s; cursor: pointer; }
        .nav-item:hover { background: rgba(255,255,255,0.1); color: #fff; }
        .nav-item.active { background: rgba(255,255,255,0.15); color: #fff; border-left: 3px solid #ce93d8; }
        .nav-item svg { width: 20px; height: 20px; margin-right: 12px; }
        .nav-section { padding: 8px 20px; color: rgba(255,255,255,0.4); font-size: 11px; text-transform: uppercase; letter-spacing: 1px; margin-top: 8px; }
        .main-content { margin-left: 260px; padding: 24px; }
        .top-bar { background: #fff; padding: 16px 24px; border-radius: 12px; margin-bottom: 24px; display: flex; justify-content: space-between; align-items: center; box-shadow: 0 1px 3px rgba(0,0,0,0.08); }
        .top-bar h1 { font-size: 24px; font-weight: 600; color: #4a148c; }
        .user-info { display: flex; align-items: center; gap: 12px; }
        .user-avatar { width: 40px; height: 40px; border-radius: 50%; background: #4a148c; color: #fff; display: flex; align-items: center; justify-content: center; font-weight: 600; }
        .stats-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(240px, 1fr)); gap: 20px; margin-bottom: 24px; }
        .stat-card { background: #fff; border-radius: 12px; padding: 24px; box-shadow: 0 1px 3px rgba(0,0,0,0.08); }
        .stat-card h3 { color: #666; font-size: 14px; font-weight: 500; margin-bottom: 8px; }
        .stat-card .value { font-size: 32px; font-weight: 700; color: #4a148c; }
        .stat-card .change { font-size: 12px; color: #4caf50; margin-top: 4px; }
        .card { background: #fff; border-radius: 12px; padding: 24px; box-shadow: 0 1px 3px rgba(0,0,0,0.08); margin-bottom: 24px; }
        .card h2 { font-size: 18px; font-weight: 600; color: #4a148c; margin-bottom: 16px; }
        .btn { padding: 10px 20px; border: none; border-radius: 8px; cursor: pointer; font-size: 14px; font-weight: 500; transition: all 0.2s; }
        .btn-primary { background: #4a148c; color: #fff; }
        .btn-primary:hover { background: #7b1fa2; }
        .btn-success { background: #4caf50; color: #fff; }
        .btn-danger { background: #f44336; color: #fff; }
        .btn-outline { background: transparent; border: 1px solid #4a148c; color: #4a148c; }
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
        .modal-header h2 { font-size: 20px; color: #4a148c; }
        .close-btn { background: none; border: none; font-size: 24px; cursor: pointer; color: #666; }
        .empty-state { text-align: center; padding: 48px; color: #999; }
    </style>
</head>
<body>
    <div class="sidebar">
        <div class="sidebar-header">
            <h2>HRMS</h2>
            <p>Employee Dashboard</p>
        </div>
        <nav class="nav-menu">
            <div class="nav-section">My Workspace</div>
            <a class="nav-item active" onclick="showSection('dashboard')">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M3 9l9-7 9 7v11a2 2 0 01-2 2H5a2 2 0 01-2-2z"/><polyline points="9 22 9 12 15 12 15 22"/></svg>
                Dashboard
            </a>
            <div class="nav-section">Time</div>
            <a class="nav-item" onclick="showSection('attendance')">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="10"/><polyline points="12 6 12 12 16 14"/></svg>
                My Attendance
            </a>
            <a class="nav-item" onclick="showSection('leaves')">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M14 2H6a2 2 0 00-2 2v16a2 2 0 002 2h12a2 2 0 002-2V8z"/><polyline points="14 2 14 8 20 8"/></svg>
                My Leaves
            </a>
            <div class="nav-section">Pay</div>
            <a class="nav-item" onclick="showSection('payroll')">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><line x1="12" y1="1" x2="12" y2="23"/><path d="M17 5H9.5a3.5 3.5 0 000 7h5a3.5 3.5 0 010 7H6"/></svg>
                My Payslips
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
                <span id="company-badge" style="background:#4a148c;color:#fff;padding:4px 12px;border-radius:20px;font-size:12px;margin-right:8px">${session.currentUser?.company?.companyName ?: 'No Company'}</span>
                <span id="user-name-display">${session.currentUser?.getFullName() ?: 'Employee Demo'}</span>
                <div class="user-avatar" id="user-avatar-display">${session.currentUser?.firstName?.charAt(0) ?: 'E'}</div>
            </div>
        </div>

        <div id="dashboard-section" class="section active">
            <div class="stats-grid">
                <div class="stat-card"><h3>Today's Check-in</h3><div class="value" id="stat-checkin">-</div><div class="change">Time</div></div>
                <div class="stat-card"><h3>Days Present (Month)</h3><div class="value" id="stat-present">0</div><div class="change">This month</div></div>
                <div class="stat-card"><h3>Late Days (Month)</h3><div class="value" id="stat-late">0</div><div class="change">This month</div></div>
                <div class="stat-card"><h3>Leave Balance</h3><div class="value" id="stat-leave-balance">0</div><div class="change">Days remaining</div></div>
            </div>
            <div class="card">
                <h2>Quick Actions</h2>
                <div style="display:flex;gap:12px;flex-wrap:wrap">
                    <button class="btn btn-primary" onclick="checkIn()">Clock In</button>
                    <button class="btn btn-outline" onclick="showSection('leaves')">Request Leave</button>
                    <button class="btn btn-outline" onclick="showSection('payroll')">View Payslips</button>
                </div>
            </div>
        </div>

        <div id="attendance-section" class="section" style="display:none">
            <div class="card">
                <h2>My Attendance</h2>
                <div class="stats-grid" style="margin-top:16px">
                    <div class="stat-card"><h3>Today's Check-in</h3><div class="value" id="att-my-checkin">-</div></div>
                    <div class="stat-card"><h3>Today's Check-out</h3><div class="value" id="att-my-checkout">-</div></div>
                    <div class="stat-card"><h3>Days Present (Month)</h3><div class="value" id="att-my-present">0</div></div>
                    <div class="stat-card"><h3>Late Days (Month)</h3><div class="value" id="att-my-late">0</div></div>
                </div>
                <button class="btn btn-primary" onclick="checkIn()" style="margin-top:16px">Clock In</button>
                <button class="btn btn-outline" onclick="checkOut()" style="margin-top:16px;margin-left:8px">Clock Out</button>
            </div>
        </div>

        <div id="leaves-section" class="section" style="display:none">
            <div class="card">
                <div style="display:flex;justify-content:space-between;align-items:center;margin-bottom:16px">
                    <h2 style="margin:0">My Leave Requests</h2>
                    <button class="btn btn-primary" onclick="openModal('leave-modal')">+ Request Leave</button>
                </div>
                <table>
                    <thead><tr><th>Type</th><th>From</th><th>To</th><th>Days</th><th>Status</th></tr></thead>
                    <tbody id="my-leave-list"></tbody>
                </table>
            </div>
        </div>

        <div id="payroll-section" class="section" style="display:none">
            <div class="card">
                <h2>My Payslips</h2>
                <table>
                    <thead><tr><th>Month</th><th>Gross</th><th>Deductions</th><th>Net</th><th>Status</th></tr></thead>
                    <tbody id="my-payslip-list"></tbody>
                </table>
            </div>
        </div>
    </div>

    <!-- Leave Modal -->
    <div id="leave-modal" class="modal">
        <div class="modal-content">
            <div class="modal-header"><h2>Request Leave</h2><button class="close-btn" onclick="closeModal('leave-modal')">&times;</button></div>
            <form onsubmit="submitLeaveRequest(event)">
                <div class="form-row">
                    <div class="form-group"><label>Leave Type *</label>
                        <select name="leaveTypeId" required>
                            <option value="">-- Select --</option>
                            <option value="1">Annual Leave</option>
                            <option value="2">Sick Leave</option>
                            <option value="3">Emergency Leave</option>
                        </select>
                    </div>
                    <div class="form-group"><label>From Date *</label><input name="fromDate" type="date" required></div>
                </div>
                <div class="form-row">
                    <div class="form-group"><label>To Date *</label><input name="toDate" type="date" required></div>
                    <div class="form-group"><label>Reason</label><textarea name="reason" rows="2"></textarea></div>
                </div>
                <button type="submit" class="btn btn-primary" style="width:100%">Submit Request</button>
            </form>
        </div>
    </div>

    <script>
        const roleNames = { admin: 'Admin Demo', hr: 'HR Demo', manager: 'Manager Demo', employee: 'Employee Demo' };
        const roleAvatars = { admin: 'A', hr: 'H', manager: 'M', employee: 'E' };
        const role = sessionStorage.getItem('userRole') || 'employee';
        document.getElementById('user-name-display').textContent = roleNames[role] || role;
        document.getElementById('user-avatar-display').textContent = roleAvatars[role] || '?';
        async function checkIn() {
            try {
                const result = await api(API + '/companies/' + currentCompanyId + '/attendance/check-in', { method: 'POST' });
                if (result.success) alert('Checked in at ' + (result.checkInTime || new Date().toLocaleTimeString()));
                else alert(result.message || 'Check-in failed');
            } catch(e) { alert('Check-in failed'); }
        }
        async function checkOut() {
            try {
                const result = await api(API + '/companies/' + currentCompanyId + '/attendance/check-out', { method: 'POST' });
                if (result.success) alert('Checked out at ' + (result.checkOutTime || new Date().toLocaleTimeString()));
                else alert(result.message || 'Check-out failed');
            } catch(e) { alert('Check-out failed'); }
        }
        async function submitLeaveRequest(e) {
            e.preventDefault();
            const form = e.target;
            const data = {
                leaveTypeId: parseInt(form.leaveTypeId.value),
                fromDate: form.fromDate.value,
                toDate: form.toDate.value,
                reason: form.reason.value
            };
            const result = await api(API + '/companies/' + currentCompanyId + '/leaves', { method: 'POST', body: JSON.stringify(data) });
            if (result.leaveApplication) {
                closeModal('leave-modal');
                loadMyLeaves();
            } else {
                alert(result.message || 'Failed');
            }
        }
        async function loadMyLeaves() {
            try {
                const data = await api(API + '/companies/' + currentCompanyId + '/employees/' + (session.currentUser?.id) + '/leaves?max=10');
                const tbody = document.getElementById('my-leave-list');
                if (data.leaveApplications && data.leaveApplications.length > 0) {
                    let html = '';
                    for (let i = 0; i < data.leaveApplications.length; i++) {
                        const l = data.leaveApplications[i];
                        const badgeClass = l.status === 'APPROVED' ? 'success' : l.status === 'PENDING' ? 'warning' : 'danger';
                        html += '<tr><td>' + (l.leaveType?.name || '-') + '</td><td>' + (l.fromDate || '-') + '</td><td>' + (l.toDate || '-') + '</td><td>' + (l.noOfDays || 0) + '</td><td><span class="badge badge-' + badgeClass + '">' + l.status + '</span></td></tr>';
                    }
                    tbody.innerHTML = html;
                }
            } catch(e) { console.log(e); }
        }
        async function loadMyPayslips() {
            const p = nowParts();
            try {
                const data = await api(API + '/companies/' + currentCompanyId + '/payroll/payslips?year=' + p.year + '&month=' + p.month + '&max=12');
                const tbody = document.getElementById('my-payslip-list');
                if (data.payslips && data.payslips.length > 0) {
                    let html = '';
                    for (let i = 0; i < data.payslips.length; i++) {
                        const pl = data.payslips[i];
                        const badgeClass = pl.status === 'APPROVED' ? 'success' : pl.status === 'GENERATED' ? 'info' : 'warning';
                        html += '<tr><td>' + (pl.month || '') + '/' + (pl.year || '') + '</td><td>' + (pl.grossSalary ? pl.grossSalary.toFixed(2) : '-') + '</td><td>' + (pl.totalDeductions ? pl.totalDeductions.toFixed(2) : '-') + '</td><td>' + (pl.netSalary ? pl.netSalary.toFixed(2) : '-') + '</td><td><span class="badge badge-' + badgeClass + '">' + pl.status + '</span></td></tr>';
                    }
                    tbody.innerHTML = html;
                }
            } catch(e) { console.log(e); }
        }
        document.addEventListener('DOMContentLoaded', () => {
            loadMyLeaves();
            loadMyPayslips();
        });
    </script>
</body>
</html>