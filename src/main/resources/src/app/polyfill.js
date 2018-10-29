(function () {
    if ('Promise' in window && 'fetch' in window) {
        // Browser is modern
    } else {
        console.log('Browser is outdated. Loading polyfill');
        const s = document.createElement('script');
        s.src = 'https://cdn.polyfill.io/v2/polyfill.min.js';
        document.head.appendChild(s);
    }
}());