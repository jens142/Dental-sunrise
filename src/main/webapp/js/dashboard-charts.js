function initDashboardCharts(data) {
    renderBarChart('appointmentsChart', normalizeDataset(data.appointmentsToday, 'count'), 'count', 'No appointment data yet', false);
    renderLineChart('revenueChart', normalizeDataset(data.revenueThisMonth, 'amount'), 'amount', 'No revenue data yet');
}

function normalizeDataset(dataset, valueKey) {
    return (Array.isArray(dataset) ? dataset : []).map((point) => ({
        label: String(point && point.label || ''),
        [valueKey]: Number(point && point[valueKey]) || 0
    }));
}

function svgEl(tag, attrs) {
    const el = document.createElementNS('http://www.w3.org/2000/svg', tag);
    Object.keys(attrs || {}).forEach((key) => el.setAttribute(key, attrs[key]));
    return el;
}

function svgTitle(text) {
    const title = svgEl('title', {});
    title.textContent = text;
    return title;
}

function truncateLabel(label, maxLength) {
    const full = String(label || '');
    return full.length > maxLength ? full.substring(0, maxLength - 1) + '\u2026' : full;
}

function labelStepFor(count, maxVisible) {
    return Math.max(1, Math.ceil(count / maxVisible));
}

function renderBarChart(containerId, dataset, valueKey, emptyMessage, isCurrency) {
    const container = document.getElementById(containerId);
    if (!container) return;

    container.replaceChildren();
    container.className = 'dashboard-chart';

    if (!dataset.length) {
        container.textContent = emptyMessage;
        container.className = 'dashboard-chart chart-empty';
        return;
    }

    const minimumWidth = 640;
    const widthPerBar = 90;
    const width = Math.max(minimumWidth, dataset.length * widthPerBar);
    const height = 260;
    const chartTop = 25;
    const chartBottom = 170;
    const chartHeight = chartBottom - chartTop;
    const leftPadding = 50;
    const rightPadding = 30;
    const chartWidth = width - leftPadding - rightPadding;
    const maxValue = Math.max(...dataset.map((point) => Number(point[valueKey]) || 0), 1);
    const slotWidth = chartWidth / dataset.length;
    const barWidth = Math.min(45, Math.max(20, slotWidth * 0.5));
    const labelStep = labelStepFor(dataset.length, 40);

    const svg = svgEl('svg', {
        viewBox: `0 0 ${width} ${height}`,
        width: width,
        height: height,
        role: 'img',
        'aria-label': isCurrency ? 'Revenue chart' : 'Appointments chart'
    });

    svg.appendChild(svgEl('line', { x1: leftPadding, y1: chartBottom, x2: width - rightPadding, y2: chartBottom, class: 'chart-axis' }));

    dataset.forEach((point, index) => {
        const value = Number(point[valueKey]) || 0;
        const barHeight = value > 0 ? Math.max((value / maxValue) * chartHeight, 4) : 2;
        const x = leftPadding + index * slotWidth + (slotWidth - barWidth) / 2;
        const y = chartBottom - barHeight;

        const rect = svgEl('rect', { x, y, width: barWidth, height: barHeight, rx: 5, class: 'chart-bar-shape' });
        rect.appendChild(svgTitle(`${point.label}: ${isCurrency ? 'Rs. ' + value.toLocaleString() : value}`));
        svg.appendChild(rect);

        const valueText = svgEl('text', { x: x + barWidth / 2, y: Math.max(y - 8, 14), 'text-anchor': 'middle', class: 'chart-value' });
        valueText.textContent = isCurrency ? 'Rs. ' + value.toLocaleString() : value.toLocaleString();
        svg.appendChild(valueText);

        if (index % labelStep === 0) {
            const label = svgEl('text', { x: x + barWidth / 2, y: 198, 'text-anchor': 'middle', class: 'chart-label' });
            label.textContent = truncateLabel(point.label, 12);
            label.appendChild(svgTitle(point.label));
            svg.appendChild(label);
        }
    });

    container.appendChild(svg);
}

function renderLineChart(containerId, dataset, valueKey, emptyMessage) {
    const container = document.getElementById(containerId);
    if (!container) return;

    container.replaceChildren();
    container.className = 'dashboard-chart';

    if (!dataset.length) {
        container.textContent = emptyMessage;
        container.className = 'dashboard-chart chart-empty';
        return;
    }

    const width = Math.max(640, dataset.length * 70);
    const height = 260;
    const padding = { top: 25, right: 30, bottom: 55, left: 60 };
    const chartWidth = width - padding.left - padding.right;
    const chartHeight = height - padding.top - padding.bottom;
    const maxValue = Math.max(...dataset.map((point) => Number(point[valueKey]) || 0), 1);
    const labelStep = labelStepFor(dataset.length, 30);

    const points = dataset.map((point, index) => {
        const x = padding.left + (chartWidth * index) / Math.max(dataset.length - 1, 1);
        const y = padding.top + chartHeight - ((Number(point[valueKey]) || 0) / maxValue) * chartHeight;
        return { ...point, x, y };
    });

    const svg = svgEl('svg', {
        viewBox: `0 0 ${width} ${height}`,
        width: width,
        height: height,
        role: 'img',
        'aria-label': 'Revenue trend'
    });

    svg.appendChild(svgEl('line', { x1: padding.left, y1: padding.top + chartHeight, x2: width - padding.right, y2: padding.top + chartHeight, class: 'chart-axis' }));

    [0.25, 0.5, 0.75].forEach((fraction) => {
        const y = padding.top + chartHeight * (1 - fraction);
        svg.appendChild(svgEl('line', { x1: padding.left, y1: y, x2: width - padding.right, y2: y, class: 'chart-gridline' }));
        const tick = svgEl('text', { x: padding.left - 10, y: y + 4, 'text-anchor': 'end', class: 'chart-tick' });
        tick.textContent = Math.round(maxValue * fraction).toLocaleString();
        svg.appendChild(tick);
    });

    const polyline = svgEl('polyline', {
        points: points.map((point) => `${point.x},${point.y}`).join(' '),
        class: 'chart-line'
    });
    svg.appendChild(polyline);

    points.forEach((point) => {
        const circle = svgEl('circle', { cx: point.x, cy: point.y, r: 4, class: 'chart-point' });
        circle.appendChild(svgTitle(`${point.label}: Rs. ${(Number(point[valueKey]) || 0).toLocaleString()}`));
        svg.appendChild(circle);
    });

    container.appendChild(svg);

    const labels = document.createElement('div');
    labels.className = 'chart-labels';
    labels.style.minWidth = width + 'px';
    points.forEach((point, index) => {
        const label = document.createElement('span');
        label.textContent = index % labelStep === 0 ? point.label : '';
        label.title = point.label;
        labels.appendChild(label);
    });
    container.appendChild(labels);
}
