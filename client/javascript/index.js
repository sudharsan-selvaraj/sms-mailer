const request = require("request-promise");
var firebase = require('firebase/app');
require('firebase/auth');
require('firebase/database');

const SERVER_URL = "https://us-central1-sms-mailer.cloudfunctions.net/api/v1";

const firebaseConfig = {
    apiKey: "AIzaSyBXTBNte3cezhQNCSnRsDbLR6C2q7cJ8co",
    authDomain: "sms-mailer.firebaseapp.com",
    databaseURL: "https://sms-mailer.firebaseio.com",
    projectId: "sms-mailer",
    storageBucket: "sms-mailer.appspot.com",
    messagingSenderId: "777485434307"
};

function SmsMailer(Config) {

    var listenters = {
        "message": [],
        "connection": [],
        "success": [],
        "error": []
    };

    var firebaseApp = firebase.initializeApp(firebaseConfig);
    var smsFilters = {};

    if (!Config.phoneNumber) {
        throw new Error("Please provide a valid phone number");
    } else if (!Config.apiKey) {
        throw new Error("Please provide a valid API key");
    }

    this.startListening = function () {
        getToken().then(function (token) {
            firebaseApp.auth().signInWithCustomToken(token).then(function () {
                broadcastEventToListeners("success", "");
                registerMessageListeners();
            }, function (err) {
                broadcastEventToListeners("error", err.error);
            })
        },function (err) {
            broadcastEventToListeners("error", err.error);
        });
    };

    this.stopListening = function () {
        getMessageDBRef().off("child_added");
    };

    this.on = function (event, callback) {
        if (listenters[event] && typeof callback == "function") {
            listenters[event].push(callback);
        }
    };

    this.off = function (event) {
        if (listenters[event]) {
            listenters[event] = [];
        }
    };

    this.addFilter = function (filterCriteria) {
        if (Object.keys(filterCriteria)) {
            smsFilters = filterCriteria
        }
    };

    var getMessageDBRef = function () {
        return firebaseApp.database().ref("Users/" + firebase.auth().currentUser.uid + "/Messages");
    };

    var registerMessageListeners = function () {
        getMessageDBRef().limitToLast(1).on("child_added", function (snapshot) {
            filterAndBroadcastMessages(snapshot.val());
        });
    };

    var filterAndBroadcastMessages = function (messageObject) {
        var isFromAddressMathesFilter = true, isMessageMatchesFilter = true;

        if (smsFilters.from) {
            isFromAddressMathesFilter = Array.isArray(smsFilters.from) ? smsFilters.from.indexOf(messageObject.fromNumber) > -1 : smsFilters.from == messageObject.fromNumber;
        }

        if (smsFilters.messageFilter && typeof smsFilters.messageFilter == "function") {
            isMessageMatchesFilter = smsFilters.messageFilter(messageObject.message)
        }

        if (isFromAddressMathesFilter && isMessageMatchesFilter) {
            broadcastEventToListeners("message", messageObject);
        }
    };

    var getToken = function () {
        var options = {
            method: 'POST',
            uri: SERVER_URL + "/get-token",
            body: Config,
            json: true
        };
        return request(options).then(function (response) {
            return response.token;
        });
    };

    var broadcastEventToListeners = function (eventName, message) {
        if (eventName === "error" && !listenters[eventName].length) {
            throw new Error(JSON.stringify(message));
        }
        listenters[eventName].length && listenters[eventName].forEach(callback => callback(message))
    }

}

exports.SmMailer = SmsMailer;