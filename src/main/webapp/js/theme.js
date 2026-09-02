const SDC_THEME_KEY = 'sdc-theme';
const SDC_SIDEBAR_KEY = 'sdc-sidebar';

function sdcApplyTheme(theme) {
    document.documentElement.setAttribute('data-theme', theme);
    document.querySelectorAll('.theme-toggle-input').forEach((el) => {
        el.checked = theme === 'dark';
    });
}

function sdcInitTheme() {
    const saved = localStorage.getItem(SDC_THEME_KEY);
    const prefersDark = window.matchMedia && window.matchMedia('(prefers-color-scheme: dark)').matches;
    const theme = saved || (prefersDark ? 'dark' : 'light');
    sdcApplyTheme(theme);
}

function sdcToggleTheme() {
    const current = document.documentElement.getAttribute('data-theme') === 'dark' ? 'dark' : 'light';
    const next = current === 'dark' ? 'light' : 'dark';
    localStorage.setItem(SDC_THEME_KEY, next);
    sdcApplyTheme(next);
}

function sdcInitSidebar() {
    const saved = localStorage.getItem(SDC_SIDEBAR_KEY);
    if (saved === 'collapsed') {
        document.body.classList.add('sidebar-collapsed');
    }
}

function sdcToggleSidebar() {
    document.body.classList.toggle('sidebar-collapsed');
    const collapsed = document.body.classList.contains('sidebar-collapsed');
    localStorage.setItem(SDC_SIDEBAR_KEY, collapsed ? 'collapsed' : 'expanded');
}

sdcInitTheme();
document.addEventListener('DOMContentLoaded', sdcInitSidebar);
