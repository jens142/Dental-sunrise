document.addEventListener('DOMContentLoaded', () => {
    const form = document.getElementById('appointmentForm');
    if (!form) return;

    const dateInput = form.querySelector('[name="appointmentDate"]');
    const treatmentSelect = form.querySelector('[name="treatmentId"]');
    const dentistSelect = form.querySelector('[name="dentistId"]');
    const costPreview = document.getElementById('costPreview');

    if (dateInput) {
        const today = new Date().toISOString().split('T')[0];
        dateInput.setAttribute('min', today);
    }

    if (treatmentSelect && costPreview) {
        treatmentSelect.addEventListener('change', () => {
            const selected = treatmentSelect.options[treatmentSelect.selectedIndex];
            const match = selected && selected.text.match(/Rs\.\s*([\d,]+(\.\d+)?)/);
            if (match) {
                costPreview.textContent = 'Estimated cost: Rs. ' + match[1];
                costPreview.style.display = 'block';
            } else {
                costPreview.style.display = 'none';
            }
        });
    }

    if (dentistSelect && dateInput) {
        const recheck = () => {
            const key = 'lastSlotCheck';
            const current = dentistSelect.value + '|' + dateInput.value;
            const previous = sessionStorage ? sessionStorage.getItem(key) : null;
            if (previous === current && dentistSelect.value && dateInput.value) {
                if (typeof showToast === 'function') {
                    showToast('You selected this dentist and date before, double check the time slot is still free.', 'info', 5000);
                }
            }
            if (sessionStorage) sessionStorage.setItem(key, current);
        };
        dentistSelect.addEventListener('change', recheck);
        dateInput.addEventListener('change', recheck);
    }

    if (typeof attachAppointmentValidation === 'function') {
        attachAppointmentValidation('appointmentForm');
    }
});
