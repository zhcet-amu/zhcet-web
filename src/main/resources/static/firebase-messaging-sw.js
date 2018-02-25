// Give the service worker access to Firebase Messaging.
// Note that you can only use Firebase Messaging here, other Firebase libraries
// are not available in the service worker.
importScripts('https://www.gstatic.com/firebasejs/4.8.1/firebase-app.js');
importScripts('https://www.gstatic.com/firebasejs/4.8.1/firebase-messaging.js');

fetch('/firebase:config.js')
    .then(response => response.json())
    .then(config => {
        firebase.initializeApp(config);
        const messaging = firebase.messaging();
        console.log('Initialized Messaging')
    }).catch(error => console.log(error));