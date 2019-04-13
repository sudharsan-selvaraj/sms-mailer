import * as functions from 'firebase-functions';
import {Utils} from "./utils/utils";
import * as admin from "firebase-admin";
import * as bodyParser from "body-parser";
import * as express from "express"
import DataSnapshot = admin.database.DataSnapshot;

admin.initializeApp();

const api = express();
const main = express();

api.post("/get-token", async (req: express.Request, res: express.Response, next: express.NextFunction) => {
    var phoneNumber = req.body.phoneNumber;
    var apiKey = req.body.apiKey;

    if (!phoneNumber || !apiKey) {
        res.status(403).send("Mobile number or API key is missing");
        return;
    }

    var userInfo = await Utils.getUserInfo(phoneNumber, apiKey);
    if (userInfo.error) {
        res.status(403).json(userInfo)
    } else {
        admin.auth().createCustomToken(userInfo.userId).then(function (token) {
            res.json({
                token : token
            })
        });
    }

});

main.use('/v1', api);
main.use(bodyParser.json());
main.use(bodyParser.urlencoded({extended: false}));

exports.api = functions.https.onRequest(main);

exports.createApiKey = functions.database.ref("Users/{pushId}").onCreate(function (snapshot: DataSnapshot, context: functions.EventContext) {

    //whenever a new user is created, add a new API access key to their account
    Utils.addNewApiKey(snapshot);
});



