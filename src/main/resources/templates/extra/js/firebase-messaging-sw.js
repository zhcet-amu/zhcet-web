// Give the service worker access to Firebase Messaging.
// Note that you can only use Firebase Messaging here, other Firebase libraries
// are not available in the service worker.
importScripts('https://www.gstatic.com/firebasejs/4.10.1/firebase-app.js');
importScripts('https://www.gstatic.com/firebasejs/4.10.1/firebase-messaging.js');

var config = /*[(${config})]*/ null;
const defaultConfig = {
    apiKey: "AIzaSyDPzDrlYgC1GlPooyvCmEOEBeQhLeJuggU",
    authDomain: "zhcet-web-amu.firebaseapp.com",
    databaseURL: "https://zhcet-web-amu.firebaseio.com",
    projectId: "zhcet-web-amu",
    storageBucket: "zhcet-web-amu.appspot.com",
    messagingSenderId: "632575632330"
};

if (config == null)
    config = defaultConfig;
firebase.initializeApp(config);
firebase.messaging();
console.log('Initialized Messaging');