(function () {
    // Give the service worker access to Firebase Messaging.
    // Note that you can only use Firebase Messaging here, other Firebase libraries
    // are not available in the service worker.
    importScripts('https://www.gstatic.com/firebasejs/4.6.2/firebase-app.js');
    importScripts('https://www.gstatic.com/firebasejs/4.6.2/firebase-messaging.js');
    importScripts('/js/config.js');

    // Initialize the Firebase app in the service worker by passing in the
    // messagingSenderId.
    firebase.initializeApp({
        'messagingSenderId': config.messagingSenderId
    });

    const messaging = firebase.messaging();

}());