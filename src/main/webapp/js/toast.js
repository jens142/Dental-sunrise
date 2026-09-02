function sdcGetToastStack() {
    let stack = document.querySelector('.toast-stack');
    if (!stack) {
        stack = document.createElement('div');
        stack.className = 'toast-stack no-print';
        document.body.appendChild(stack);
    }
    return stack;
}

function showToast(message, type, duration) {
    const stack = sdcGetToastStack();
    const kind = type || 'info';
    const icons = { success: 'check_circle', error: 'error', info: 'info' };

    const toast = document.createElement('div');
    toast.className = 'toast toast-' + kind;
    toast.innerHTML =
        '<span class="material-symbols-outlined">' + (icons[kind] || 'info') + '</span>' +
        '<span>' + message + '</span>';

    stack.appendChild(toast);

    setTimeout(() => {
        toast.classList.add('leaving');
        setTimeout(() => toast.remove(), 220);
    }, duration || 3500);
}
