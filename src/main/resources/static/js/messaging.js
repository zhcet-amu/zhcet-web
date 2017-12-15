(function (App, firebase) {
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
        App.postToServer('/profile/api/messaging_token', currentToken)
            .then(function (result) {
                if (result === 'OK')
                    setTokenSentToServer(currentToken);
            });
    }

    function isTokenSentToServer(token) {
        return window.localStorage.getItem('fcmTokenSentToServer') === token;
    }

    function setTokenSentToServer(token) {
        window.localStorage.setItem('fcmTokenSentToServer', token);
    }

    function playSound() {
        var audio = new Audio('/notification.mp3');
        audio.play();
    }

    function addNotification(payload) {
        var template = $('#notification-template').html();
        var rendered = tmpl(template, payload.data);
        $('#notification-panel').prepend(rendered);
    }

    function showNotification(payload) {
        playSound();
        toastr.info(payload.notification.body, payload.notification.title);
        var countHolder = $('#notification-count');
        var notificationCount = parseInt(countHolder.text().replace('+', '')) + 1;
        countHolder.text(notificationCount);
        $('#new-count').text(notificationCount + ' NEW');
        addNotification(payload);
    }

    /* main */ (function () {
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
    })();

}(App, firebase));