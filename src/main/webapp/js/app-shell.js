function initHeaderClock(elementId) {
    const el = document.getElementById(elementId);
    if (!el) return;
    const render = () => {
        const now = new Date();
        el.textContent = now.toLocaleDateString(undefined, { weekday: 'short', month: 'short', day: 'numeric' }) +
            ' · ' + now.toLocaleTimeString(undefined, { hour: '2-digit', minute: '2-digit' });
    };
    render();
    setInterval(render, 30000);
}

function initDropdown(triggerId, panelId) {
    const trigger = document.getElementById(triggerId);
    const panel = document.getElementById(panelId);
    if (!trigger || !panel) return;

    trigger.addEventListener('click', (e) => {
        e.stopPropagation();
        document.querySelectorAll('.dropdown-panel.open').forEach((p) => {
            if (p !== panel) p.classList.remove('open');
        });
        panel.classList.toggle('open');
    });

    document.addEventListener('click', (e) => {
        if (!panel.contains(e.target) && e.target !== trigger) {
            panel.classList.remove('open');
        }
    });

    document.addEventListener('keydown', (e) => {
        if (e.key === 'Escape') panel.classList.remove('open');
    });
}

function openModal(modalId) {
    const modal = document.getElementById(modalId);
    if (modal) modal.classList.add('open');
}

function closeModal(modalId) {
    const modal = document.getElementById(modalId);
    if (modal) modal.classList.remove('open');
}

function initModalDismissal() {
    document.querySelectorAll('.modal-overlay').forEach((overlay) => {
        overlay.addEventListener('click', (e) => {
            if (e.target === overlay) overlay.classList.remove('open');
        });
    });
    document.addEventListener('keydown', (e) => {
        if (e.key === 'Escape') {
            document.querySelectorAll('.modal-overlay.open').forEach((o) => o.classList.remove('open'));
        }
    });
}

function initCommandPalette(items) {
    const overlay = document.getElementById('commandPaletteOverlay');
    const input = document.getElementById('commandPaletteInput');
    const results = document.getElementById('commandPaletteResults');
    const trigger = document.getElementById('commandPaletteTrigger');
    if (!overlay || !input || !results) return;

    const list = items || [];
    let selectedIndex = 0;
    let filtered = list;

    function render() {
        results.innerHTML = '';
        if (!filtered.length) {
            results.innerHTML = '<div class="command-palette-empty">No matching pages or actions.</div>';
            return;
        }
        filtered.forEach((item, i) => {
            const row = document.createElement('a');
            row.href = item.href;
            row.className = 'command-palette-item' + (i === selectedIndex ? ' selected' : '');
            row.innerHTML = '<span class="material-symbols-outlined">' + item.icon + '</span><span>' + item.label + '</span>';
            results.appendChild(row);
        });
    }

    function open() {
        overlay.classList.add('open');
        input.value = '';
        filtered = list;
        selectedIndex = 0;
        render();
        setTimeout(() => input.focus(), 30);
    }

    function close() {
        overlay.classList.remove('open');
    }

    if (trigger) trigger.addEventListener('click', open);

    document.addEventListener('keydown', (e) => {
        if ((e.metaKey || e.ctrlKey) && e.key.toLowerCase() === 'k') {
            e.preventDefault();
            open();
        }
        if (e.key === 'Escape') close();
    });

    overlay.addEventListener('click', (e) => {
        if (e.target === overlay) close();
    });

    input.addEventListener('input', () => {
        const q = input.value.trim().toLowerCase();
        filtered = list.filter((item) => item.label.toLowerCase().includes(q));
        selectedIndex = 0;
        render();
    });

    input.addEventListener('keydown', (e) => {
        if (e.key === 'ArrowDown') {
            e.preventDefault();
            selectedIndex = Math.min(selectedIndex + 1, filtered.length - 1);
            render();
        } else if (e.key === 'ArrowUp') {
            e.preventDefault();
            selectedIndex = Math.max(selectedIndex - 1, 0);
            render();
        } else if (e.key === 'Enter' && filtered[selectedIndex]) {
            window.location.href = filtered[selectedIndex].href;
        }
    });
}

function initBackToTop() {
    const btn = document.getElementById('backToTopBtn');
    if (!btn) return;
    window.addEventListener('scroll', () => {
        btn.classList.toggle('visible', window.scrollY > 400);
    });
    btn.addEventListener('click', () => {
        window.scrollTo({ top: 0, behavior: 'smooth' });
    });
}

function initIdleTimeout(minutesUntilWarning, logoutUrl) {
    const modal = document.getElementById('idleWarningModal');
    const countdownEl = document.getElementById('idleCountdown');
    if (!modal || !countdownEl) return;

    let warningTimer;
    let countdownTimer;
    const warnAfterMs = (minutesUntilWarning || 20) * 60 * 1000;
    const countdownSeconds = 60;

    function startCountdown() {
        modal.classList.add('open');
        let remaining = countdownSeconds;
        countdownEl.textContent = remaining;
        countdownTimer = setInterval(() => {
            remaining -= 1;
            countdownEl.textContent = remaining;
            if (remaining <= 0) {
                clearInterval(countdownTimer);
                window.location.href = logoutUrl;
            }
        }, 1000);
    }

    function resetTimer() {
        clearTimeout(warningTimer);
        clearInterval(countdownTimer);
        modal.classList.remove('open');
        warningTimer = setTimeout(startCountdown, warnAfterMs);
    }

    ['mousemove', 'keydown', 'click', 'scroll'].forEach((evt) => {
        document.addEventListener(evt, resetTimer, { passive: true });
    });

    const stayBtn = document.getElementById('idleStayBtn');
    if (stayBtn) stayBtn.addEventListener('click', resetTimer);

    resetTimer();
}

function copyToClipboard(text, label) {
    if (navigator.clipboard) {
        navigator.clipboard.writeText(text).then(() => {
            showToast((label || 'Copied') + ' to clipboard', 'success');
        }).catch(() => {
            showToast('Could not copy to clipboard', 'error');
        });
    }
}

function exportTableToCSV(tableId, filename) {
    const table = document.getElementById(tableId);
    if (!table) return;
    const rows = Array.from(table.querySelectorAll('tr'));
    const csv = rows.map((row) => {
        return Array.from(row.querySelectorAll('th, td'))
            .map((cell) => '"' + cell.textContent.trim().replace(/"/g, '""') + '"')
            .join(',');
    }).join('\n');

    const blob = new Blob([csv], { type: 'text/csv;charset=utf-8;' });
    const link = document.createElement('a');
    link.href = URL.createObjectURL(blob);
    link.download = filename || 'export.csv';
    link.click();
    URL.revokeObjectURL(link.href);
    showToast('Export downloaded', 'success');
}

function makeTableSortable(tableId) {
    const table = document.getElementById(tableId);
    if (!table) return;
    const headers = table.querySelectorAll('thead th[data-sort-index]');
    let currentCol = -1;
    let ascending = true;

    headers.forEach((th) => {
        th.classList.add('sortable');
        th.addEventListener('click', () => {
            const colIndex = parseInt(th.getAttribute('data-sort-index'), 10);
            ascending = currentCol === colIndex ? !ascending : true;
            currentCol = colIndex;

            const tbody = table.querySelector('tbody');
            const rows = Array.from(tbody.querySelectorAll('tr'));
            rows.sort((a, b) => {
                const cellA = a.children[colIndex] ? a.children[colIndex].textContent.trim() : '';
                const cellB = b.children[colIndex] ? b.children[colIndex].textContent.trim() : '';
                return ascending ? cellA.localeCompare(cellB, undefined, { numeric: true })
                                  : cellB.localeCompare(cellA, undefined, { numeric: true });
            });
            rows.forEach((row) => tbody.appendChild(row));
        });
    });
}

function initStatusFilterChips(chipContainerId, tableId, statusColumnIndex) {
    const container = document.getElementById(chipContainerId);
    const table = document.getElementById(tableId);
    if (!container || !table) return;

    container.querySelectorAll('.chip').forEach((chip) => {
        chip.addEventListener('click', () => {
            container.querySelectorAll('.chip').forEach((c) => c.classList.remove('active'));
            chip.classList.add('active');
            const status = chip.getAttribute('data-status');
            const rows = table.querySelectorAll('tbody tr');
            rows.forEach((row) => {
                const cell = row.children[statusColumnIndex];
                const rowStatus = cell ? cell.textContent.trim().toUpperCase() : '';
                row.style.display = (status === 'ALL' || rowStatus.includes(status)) ? '' : 'none';
            });
        });
    });
}

function animateCounter(el, target, prefix, suffix) {
    if (!el) return;
    const duration = 900;
    const start = performance.now();
    function step(now) {
        const progress = Math.min((now - start) / duration, 1);
        const eased = 1 - Math.pow(1 - progress, 3);
        const value = Math.round(target * eased);
        el.textContent = (prefix || '') + value.toLocaleString() + (suffix || '');
        if (progress < 1) requestAnimationFrame(step);
    }
    requestAnimationFrame(step);
}

document.addEventListener('DOMContentLoaded', () => {
    initBackToTop();
    initModalDismissal();
});
