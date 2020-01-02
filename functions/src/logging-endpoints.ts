import * as functions from "firebase-functions";
const admin = require('firebase-admin');

exports.postLog = functions.https.onRequest( (request, response) => {
    let configId = request.query.id;
    let data = request.body;
    let db = admin.firestore();
    let logsRef = db.collection("logs").doc(configId).collection("logs");
    logsRef.add(data).then((doc: any) => {
        console.log("Added log with ID: ", doc.id, " - ", request.body);
        response.status(201).send(data);
    });
});

function getTestLog() {
    let testLog = {
        "date" : new Date(),
        "content" : "test log"
    };
    return testLog;
}