export function getNormalizedErrorClass(type) {
    type = type.toLowerCase();
    if (type === 'error')
        return 'danger';
    else
        return type;
}

export function extractHeaders(items) {
    return items
        .map(item => item.item)
        .map(item => Object.keys(item))
        .reduce((accumulator, current) => {
            current.forEach(item => accumulator.add(item));
            return accumulator;
        }, new Set());
}
