import * as functions from "firebase-functions";
const admin = require('firebase-admin');

exports.postLog = functions.https.onRequest( (request, response) => {
    let configId = request.query.id;
    let data = request.body;
    let db = admin.firestore();
    let logsRef = db.collection("logs").doc(configId).collection("logs").doc(data.id);

    data.date = new Date(data.date);

    logsRef.set(data).then((doc: any) => {
        let responseMessage = ('Added log with ID: ${doc.id}' + doc.id + " - " + request.body);
        console.log(responseMessage);
        response.status(201).send(responseMessage);
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

    // logsRef.add(getEmptyLog()).then((doc: any) => {
    //     console.log("Created empty log with id: ", doc.id);
    //     response.send(doc.id);
    // });

    // console.log(logsRef.id);
    // response.send(logsRef.doc().id);
}));