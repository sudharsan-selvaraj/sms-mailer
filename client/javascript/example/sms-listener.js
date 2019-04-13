var smsMailer = require("../index").SmMailer;

var smsListener = new smsMailer({
    "phoneNumber" : "+919677730317",
    "apiKey" :"ba5498ae-8f70-3b71-a150-63f3a0305338"
});

smsListener.on("success",function () {
    console.log("Connection made")
});

smsListener.on("error",function (err) {
    console.log(err)
});

smsListener.on("message",function (message) {
    console.log(message)
});

smsListener.addFilter({
    from : "+15555215556",
    messageFilter : function (message) {
        if (message.includes("sudharsan")) {
            return true;
        } else {
            return false;
        }
    }
});

smsListener.startListening();
