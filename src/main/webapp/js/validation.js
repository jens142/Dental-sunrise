function showFieldError(inputId, message) {
    const errorEl = document.getElementById(inputId + '-error');
    const inputEl = document.getElementById(inputId);
    if (errorEl) {
        errorEl.textContent = message;
        errorEl.style.display = 'block';
    }
    if (inputEl) {
        inputEl.classList.add('field-invalid');
    }
}

function clearFieldError(inputId) {
    const errorEl = document.getElementById(inputId + '-error');
    const inputEl = document.getElementById(inputId);
    if (errorEl) {
        errorEl.textContent = '';
        errorEl.style.display = 'none';
    }
    if (inputEl) {
        inputEl.classList.remove('field-invalid');
    }
}

function attachLoginValidation(formId) {
    const form = document.getElementById(formId);
    if (!form) return;

    const username = document.getElementById('username');
    const password = document.getElementById('password');

    function validateUsername() {
        if (!username.value.trim()) {
            showFieldError('username', 'Username is required.');
            return false;
        }
        clearFieldError('username');
        return true;
    }

    function validatePassword() {
        if (!password.value) {
            showFieldError('password', 'Password is required.');
            return false;
        }
        clearFieldError('password');
        return true;
    }

    username.addEventListener('blur', validateUsername);
    password.addEventListener('blur', validatePassword);

    form.addEventListener('submit', (e) => {
        const validUsername = validateUsername();
        const validPassword = validatePassword();
        if (!validUsername || !validPassword) {
            e.preventDefault();
            if (typeof showToast === 'function') {
                showToast('Please fill in all required fields.', 'error');
            }
        }
    });
}

function attachAppointmentValidation(formId) {
    const form = document.getElementById(formId);
    if (!form) return;

    form.addEventListener('submit', (e) => {
        const dentistId = form.querySelector('[name="dentistId"]');
        const treatmentId = form.querySelector('[name="treatmentId"]');
        const date = form.querySelector('[name="appointmentDate"]');
        const time = form.querySelector('[name="appointmentTime"]');

        let valid = true;

        if (!dentistId.value) {
            highlightInvalid(dentistId, 'Please select a dentist.');
            valid = false;
        } else {
            clearInvalid(dentistId);
        }

        if (!treatmentId.value) {
            highlightInvalid(treatmentId, 'Please select a treatment.');
            valid = false;
        } else {
            clearInvalid(treatmentId);
        }

        if (date.value) {
            const chosen = new Date(date.value);
            const today = new Date();
            today.setHours(0, 0, 0, 0);
            if (chosen < today) {
                highlightInvalid(date, 'Date cannot be in the past.');
                valid = false;
            } else {
                clearInvalid(date);
            }
        }

        if (!time.value) {
            highlightInvalid(time, 'Please choose a time.');
            valid = false;
        } else {
            clearInvalid(time);
        }

        if (!valid) {
            e.preventDefault();
            if (typeof showToast === 'function') {
                showToast('Please check the highlighted fields.', 'error');
            }
        }
    });
}

function highlightInvalid(el, message) {
    el.classList.add('field-invalid');
    let hint = el.parentElement.querySelector('.inline-field-error');
    if (!hint) {
        hint = document.createElement('span');
        hint.className = 'inline-field-error field-error';
        el.parentElement.appendChild(hint);
    }
    hint.textContent = message;
}

function clearInvalid(el) {
    el.classList.remove('field-invalid');
    const hint = el.parentElement.querySelector('.inline-field-error');
    if (hint) hint.remove();
}

function isValidEmail(value) {
    return /^[\w.+-]+@[\w-]+\.[a-zA-Z]{2,}$/.test(value);
}

function isValidPhone(value) {
    return /^0\d{9}$/.test(value);
}
