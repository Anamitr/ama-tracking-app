import * as functions from "firebase-functions";
const admin = require('firebase-admin');

exports.postLog = functions.https.onRequest( (request, response) => {
    let data = request.body;
    let db = admin.firestore();

    console.log("postLog: attempting to add log : ", data);

    let logsRef = db.collection("logs").doc(data.configId).collection("logs");
    let externalId = logsRef.doc().id;

    let geoLogToInsert = {
        "externalId" : externalId,
        "configId" : data.configId,
        "date" : new Date(data.date),
        "content" : data.content
    };

    logsRef.add(geoLogToInsert).then((doc: any) => {
        let responseMessage = ('Added log with ID: ${doc.id}' + doc.id + " - " + request.body);
        console.log(responseMessage);
        response.status(201).send(doc.id);
    });
});

function getTestLog() {
    let testLog = {
        "date" : new Date(),
        "content" : "test log"
    };
    return testLog;
}

function getEmptyLog() {
    let testLog = {
        "date" : "",
        "content" : "test log"
    };
    return testLog;
}

exports.getNextLogId = functions.https.onRequest(((request, response) => {
    let configId = request.query.id;
    let db = admin.firestore();
    let logsRef = db.collection("logs").doc(configId).collection("logs").doc();
    let nextLogId = logsRef.id
    console.log("Returning next nextLogId: ", nextLogId)
    response.send(nextLogId);
}));