import * as admin from "firebase-admin";
const uuidv3 = require('uuid/v3');

class Utils {

    public static addNewApiKey(userSnapshot: admin.database.DataSnapshot) {
        var apiKey = {
            key: uuidv3('sms-mailer.firebaseapp.com', uuidv3.DNS),
            enabled: true
        };
        userSnapshot.ref.child("api-keys").push(apiKey);
    }

    public static getUserInfo(phoneNumber: string, apiKey: string): Promise<any> {
        return new Promise(function (resolve, reject) {
            admin.database().ref().child("Users").orderByChild("info/phoneNumber").equalTo(phoneNumber).on("value", function (userSnapshot: admin.database.DataSnapshot | null) {
                if (userSnapshot && userSnapshot.val()) {
                    console.log(userSnapshot.val());
                    var userObject = userSnapshot.val();
                    var uid = Object.keys(userObject)[0];
                    var apiKeys = userObject[uid]["api-keys"];
                    Object.keys(apiKeys).forEach(function (key:any) {
                        console.log(apiKeys[key].key+" "+apiKey);
                        if(apiKeys[key].key == apiKey && apiKeys[key].enabled) {
                            resolve({
                                userId : uid
                            });
                        }
                    });
                }
                resolve({
                    "error" : "\"Phone number or API key is invalid.\""
                })
            });
        });
    }

}

export {
    Utils
}