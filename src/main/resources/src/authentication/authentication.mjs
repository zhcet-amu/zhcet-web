import google from './google'

let userInformation;

export function signOut() {
    // Clear the FCM token
    window.localStorage.removeItem('fcmTokenSentToServer');
    return firebase.auth().signOut();
}

export function signIn(token) {
    return firebase.auth().signInWithCustomToken(token);
}

export function attemptSignIn() {
    $.ajax({
        url: "/profile/api/token",
        success: function(user) {
            userInformation = user;
            if (user.authenticated) signIn(user.token);
        }});
}

export function init() {
    $(document).ready(function () {
        if (!firebase.auth().currentUser)
            attemptSignIn();
    });
}

export default {
    auth: {
        init,
        signOut
    },
    google
}