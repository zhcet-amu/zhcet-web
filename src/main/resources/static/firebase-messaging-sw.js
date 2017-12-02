(function () {
    // Give the service worker access to Firebase Messaging.
    // Note that you can only use Firebase Messaging here, other Firebase libraries
    // are not available in the service worker.
    importScripts('https://www.gstatic.com/firebasejs/4.6.2/firebase-app.js');
    importScripts('https://www.gstatic.com/firebasejs/4.6.2/firebase-messaging.js');
    importScripts('/js/config.js');

    const messaging = firebase.messaging();

}());