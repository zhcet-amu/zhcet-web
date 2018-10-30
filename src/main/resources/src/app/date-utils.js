const days = [
    'Sunday',
    'Monday',
    'Tuesday',
    'Wednesday',
    'Thursday',
    'Friday',
    'Saturday'
];

const months = [
    'January',
    'February',
    'March',
    'April',
    'May',
    'June',
    'July',
    'August',
    'September',
    'October',
    'November',
    'December'
];

const pad = (n, s) => ('000000000' + n).substr(-s);

export function formatDate(date) {
    const day = days[date.getDay()];
    const month = months[date.getMonth()];

    const hourIn24 = date.getHours();
    const am = hourIn24 < 12;
    const hour = hourIn24 === 0 ? 12 : (hourIn24 > 12 ? hourIn24 - 12 : hourIn24);

    const formattedDate = `${ day }, ${ month } ${ date.getDate() } ${ date.getFullYear() }`;
    const formattedTime = `${ hour }:${ pad(date.getMinutes(), 2) }:${ pad(date.getSeconds(), 2) } ${ am ? 'AM' : 'PM' }`;
    return `${ formattedDate }, ${ formattedTime }`
}

export default {
    formatDate
}
