var Authentication = (function ($, App, firebase) {
    var googleProvider = new firebase.auth.GoogleAuthProvider();
    var userInformation;

    function callIfFunction(func, arg1, arg2) {
        if ($.isFunction(func))
            func(arg1, arg2);
    }

    /**
     * Google Authentication and linking module
     * @returns {{link: function:object, login: function(callback}}
     */
    function google() {

        function postToken(idToken) {
            App.postToServer("/login/api/token", idToken, function(action) {
                window.location = action;
            });
        }

        function checkUser(result) {
            if (!result || !result.user)
                return false;

            result.user.getIdToken(false).then(postToken);

            return true;
        }

        /**
         * Google Login Module
         * @param callback
         */
        function googleLogin(callback) {
            firebase.auth().signInWithPopup(googleProvider)
                .then(function (result) {
                    var check = checkUser(result);
                    callIfFunction(callback, check);
                }).catch(function(error) {
                    callIfFunction(callback, false, error.message);
                });
        }

        /**
         * Google Linking Module
         * @returns {{linkGoogle: function(callback), unlinkGoogle: function(callback), isGoogleLinked: function(callback}}
         */
        function link() {

            function getProviderData(providerDataArray, providerId) {
                if (!providerDataArray)
                    return null;

                for (var i = 0; i < providerDataArray.length; i++)
                    if (providerDataArray[i].providerId === providerId)
                        return providerDataArray[i];

                return null;
            }

            function isProvider(providerData, providerId) {
                return getProviderData(providerData, providerId) !== null;
            }

            function updateUser(user, providerData) {
                if (!providerData)
                    return;

                var updateUser = {};
                if (!user.displayName)
                    updateUser.displayName = providerData.displayName;
                if (!user.photoURL)
                    updateUser.photoURL = providerData.photoURL;
                user.updateProfile(updateUser)
                    .then(function () {
                        return firebase.auth().currentUser.getIdToken(false);
                    })
                    .then(function (token) {
                        App.postToServer('/profile/api/link', token, function (response) {
                            if (response === 'OK') {
                                setTimeout(function () {
                                    window.location = '/profile?refresh';
                                }, 5000);
                            }
                        });
                    });
            }

            function accountLinked(linkCallback, user) {
                callIfFunction(linkCallback, true);
                updateUser(user, getProviderData(user.providerData, googleProvider.providerId));
            }

            function deletePreviousUser(prevUser, credential) {
                var auth = firebase.auth();
                return auth.signInWithCredential(credential)
                    .then(function(user) {
                        return user.delete();
                    }).then(function() {
                        return prevUser.linkWithCredential(credential);
                    }).then(function() {
                        return auth.signInWithCredential(credential);
                    });
            }

            return {
                linkGoogle: function (linkCallback) {
                    firebase.auth().currentUser.linkWithPopup(googleProvider)
                        .then(function(result) {
                            accountLinked(linkCallback, result.user);
                        }).catch(function(error) {
                            if (error.code === 'auth/credential-already-in-use') {
                                var prevUser = firebase.auth().currentUser;
                                var credential = error.credential;
                                deletePreviousUser(prevUser, credential)
                                    .then(function (user) {
                                        accountLinked(linkCallback, user);
                                    }).catch(function(error) {
                                        callIfFunction(linkCallback, false, error);
                                    });
                            } else {
                                callIfFunction(linkCallback, false, error);
                            }
                        });
                },

                unlinkGoogle: function (unlinkCallback) {
                    firebase.auth().currentUser.unlink(googleProvider.providerId)
                        .then(function () {
                            callIfFunction(unlinkCallback, true);
                        }).catch(function (error) {
                            callIfFunction(unlinkCallback, false, error);
                        });
                },

                isGoogleLinked: function (linked) {
                    firebase.auth().onAuthStateChanged(function (user) {
                        if (!$.isFunction(linked))
                            return;

                        if (user) {
                            linked(isProvider(user.providerData, googleProvider.providerId));
                        } else {
                            linked(); // Trigger hiding the button
                        }
                    });
                }
            };
        }

        return {
            link: link,
            login: googleLogin
        }
    }

    /**
     * General Authentication Module
     * @returns {{init: function, signOut: function}}
     */
    function auth() {
        function signOut() {
            return firebase.auth().signOut();
        }

        function signIn(token) {
            return firebase.auth().signInWithCustomToken(token);
        }

        function attemptSignIn() {
            $.ajax({
                url: "/profile/api/token",
                success: function(user) {
                    userInformation = user;
                    if (user.authenticated) signIn(user.token);
                }});
        }

        function init() {
            $(document).ready(function () {
                firebase.auth().onAuthStateChanged(function (user) {
                    if (!user) attemptSignIn();
                });
            });
        }

        return {
            init: init,
            signOut: signOut
        }
    }

    return {
        auth: auth,
        google: google
    }
}(jQuery, App, firebase));
