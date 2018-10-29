import { postToServer } from "../app/utils";

const messaging = firebase.messaging();

function getToken() {
    messaging.getToken()
        .then(function(currentToken) {
            if (currentToken) {
                sendTokenToServer(currentToken);
            } else {
                console.log('No Instance ID token available. Request permission to generate one.');
                setTokenSentToServer(false);
            }
        })
        .catch(function(err) {
            console.log('An error occurred while retrieving token. ', err);
            setTokenSentToServer(false);
        });
}

function sendTokenToServer(currentToken) {
    if (isTokenSentToServer(currentToken))
        return;
    postToServer('/profile/api/messaging_token', currentToken)
        .then(function (result) {
            if (result === 'OK')
                setTokenSentToServer(currentToken);
        });
}

function isTokenSentToServer(token) {
    return parseInt(window.localStorage.getItem('fcmTokenSentToServer')) === token.hashCode();
}

function setTokenSentToServer(token) {
    window.localStorage.setItem('fcmTokenSentToServer', token.hashCode());
}

function playSound() {
    const audio = new Audio('/notification.mp3');
    audio.play();
}

function addNotification(payload) {
    const template = $('#notification-template').html();
    const rendered = tmpl(template, payload.data);
    $('#notification-panel').prepend(rendered);
}

function showNotification(payload) {
    playSound();
    toastr.info(payload.notification.body, payload.notification.title);
    const countHolder = $('#notification-count');
    const notificationCount = parseInt(countHolder.text()) + 1;
    if (notificationCount === 1)
        countHolder.attr('hidden', false);
    countHolder.text(notificationCount);
    $('#new-count').text(notificationCount + ' NEW');
    addNotification(payload);
}

String.prototype.hashCode = function() {
    let hash = 0, i = 0, len = this.length;
    while ( i < len ) {
        hash  = ((hash << 5) - hash + this.charCodeAt(i++)) << 0;
    }
    return hash;
};

messaging.requestPermission()
    .then(function() {
        getToken();
    })
    .catch(function(err) {
        console.log('Unable to get permission to notify.', err);
    });

messaging.onTokenRefresh(function() {
    messaging.getToken()
        .then(function(refreshedToken) {
            setTokenSentToServer(false);
            sendTokenToServer(refreshedToken);
        })
        .catch(function(err) {
            console.log('Unable to retrieve refreshed token ', err);
        });
});

messaging.onMessage(function(payload) {
    showNotification(payload);
});