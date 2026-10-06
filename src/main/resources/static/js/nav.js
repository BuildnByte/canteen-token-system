/**
 * nav.js — Shared auth/navigation logic for all role-specific pages.
 *
 * ARCHITECTURE NOTE:
 * Spring Security permits all page (HTML) routes — they're shells.
 * This JS module enforces auth/role client-side on every page load.
 * Actual data security is enforced on the /api/** endpoints via JWT.
 */

window.AUTH = (() => {
    const token = () => localStorage.getItem('jwt_token');
    const role  = () => localStorage.getItem('user_role');
    const name  = () => localStorage.getItem('user_name') || localStorage.getItem('user_email') || '';
    const id    = () => localStorage.getItem('user_id');

    function headers() {
        const t = token();
        const h = { 'Content-Type': 'application/json' };
        if (t) h['Authorization'] = 'Bearer ' + t;
        return h;
    }

    /**
     * Call at the top of every protected page.
     * If no token: redirect to /login.
     * If wrong role: redirect to their home page.
     * @param {...string} allowedRoles - e.g. requireRole('ADMIN') or requireRole('STAFF','ADMIN')
     * @returns {boolean} true if access is granted
     */
    function requireRole(...allowedRoles) {
        const t = token();
        const r = role();

        if (!t || !r) {
            // Not logged in → go to login
            if (!window.location.pathname.startsWith('/login')) {
                window.location.replace('/login');
            }
            return false;
        }

        if (allowedRoles.length > 0 && !allowedRoles.includes(r)) {
            // Logged in but wrong role → redirect to their own home
            const home = r === 'ADMIN' ? '/dashboard'
                       : r === 'STAFF' ? '/staff/queue'
                       : '/menu';
            if (window.location.pathname !== home) {
                window.location.replace(home);
            }
            return false;
        }

        return true;
    }

    function logout() {
        localStorage.removeItem('jwt_token');
        localStorage.removeItem('user_role');
        localStorage.removeItem('user_id');
        localStorage.removeItem('user_name');
        localStorage.removeItem('user_email');
        // Clear JWT cookie
        document.cookie = 'JWT_TOKEN=; path=/; expires=Thu, 01 Jan 1970 00:00:00 UTC; SameSite=Strict';
        window.location.replace('/login');
    }

    /**
     * Builds role-specific nav links and injects user info into the nav bar.
     * @param {string} activeLink - key to highlight the active nav link
     */
    function initNav(activeLink) {
        const r       = role();
        const n       = name();
        const linksEl = document.getElementById('nav-links-dynamic');
        const actEl   = document.getElementById('nav-actions');

        // ── Role-specific nav links ─────────────────────────────────────
        if (linksEl) {
            let links = '';

            if (r === 'STUDENT') {
                links += navLink('/menu',           'menu',   '🍽️ Menu',       activeLink);
                links += navLink('/student/orders', 'orders', '🧾 My Orders',  activeLink);
            } else if (r === 'STAFF') {
                links += navLink('/staff/queue',    'global', '📋 New Orders', activeLink);
                links += navLink('/staff/my-queue', 'myq',    '🍳 My Queue',   activeLink);
            } else if (r === 'ADMIN') {
                links += navLink('/dashboard',      'dash',   '📊 Dashboard',  activeLink);
                links += navLink('/admin/menu',     'amenu',  '🍽️ Menu Mgmt', activeLink);
                links += navLink('/admin/orders',   'aord',   '📋 All Orders', activeLink);
                links += navLink('/admin/users',    'ausers', '👥 Users',      activeLink);
                links += navLink('/staff/queue',    'aqueue', '👁️ Live Queue', activeLink);
            } else {
                // Guest / not logged in
                links += navLink('/menu', 'menu', '🍽️ Menu', activeLink);
            }

            linksEl.innerHTML = links;
        }

        // ── User info + Sign out button ─────────────────────────────────
        if (actEl) {
            if (token()) {
                const display = n.includes('@') ? n.split('@')[0] : n;
                actEl.innerHTML = `
                    <div class="nav-user">
                        <span class="fw-700" style="font-size:0.88rem;">${display}</span>
                        <span class="role-badge role-${r}">${r}</span>
                    </div>
                    <button onclick="AUTH.logout()" class="btn btn-ghost btn-sm">Sign out</button>
                `;
            } else {
                actEl.innerHTML = `
                    <a href="/login"    class="btn btn-ghost btn-sm">Sign in</a>
                    <a href="/register" class="btn btn-primary btn-sm">Sign up</a>
                `;
            }
        }
    }

    function navLink(href, key, label, active) {
        const cls = active === key ? ' class="active"' : '';
        return `<a href="${href}"${cls}>${label}</a>`;
    }

    return { token, role, name, id, headers, requireRole, logout, initNav };
})();

// ── Global toast notification helper ────────────────────────────────────────
window.showToast = function(message, type = 'default') {
    let container = document.getElementById('toast-container');
    if (!container) {
        container = document.createElement('div');
        container.id = 'toast-container';
        container.className = 'toast-container';
        document.body.appendChild(container);
    }
    const toast = document.createElement('div');
    toast.className = 'toast ' + type;
    const icon = type === 'success' ? '✅' : type === 'error' ? '❌' : 'ℹ️';
    toast.innerHTML = `<span>${icon}</span>&nbsp;${message}`;
    container.appendChild(toast);
    setTimeout(() => {
        toast.style.opacity = '0';
        toast.style.transition = 'opacity 0.35s';
        setTimeout(() => toast.remove(), 380);
    }, 3200);
};
