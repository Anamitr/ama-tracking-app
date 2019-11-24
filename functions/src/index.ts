import * as functions from 'firebase-functions';
// import firestore from "firebase-admin";
// import * as firestore from "firebase-admin";
// import FirebaseFirestore from ;

const admin = require('firebase-admin');
admin.initializeApp();

function createNewConfig(configRef: any, configId:String) {
    let defaultConfigData = {
        "id" : configId,
        "name": "Test",
        "token": "",
        "trackedObjectId": "",
        "positionIntervalInMinutes": 15
    };
    configRef.set(defaultConfigData);
    return defaultConfigData
}

exports.getConfig = functions.https.onRequest((request, response) => {
    let configId = request.query.id;
    let db = admin.firestore();
    let configRef = db.collection("configurations").doc(configId);
    let getConfig = configRef.get().then((doc: any) => {
        if (!doc.exists) {
            let newConfigData = createNewConfig(configRef, configId);
            response.status(201).send(newConfigData)
        } else {
            console.log('Document data:', doc.data());
            response.send(doc.data())
        }
    }).catch((err: any) => {
        console.log('Error getting config', err);
        response.send('Error getting config: ' + err)
    })
});

function updateConfig(configRef: FirebaseFirestore.DocumentReference, data: any) {
    configRef.set(data)
        .then((docRef:any) => {
        return
    }).catch((err:any) => {
        throw err
    })
}

exports.updateConfig = functions.https.onRequest((request, response) => {
    if (request.method !== "PUT") {
        response.status(400).send('Please send a PUT request');
        return
    }
    let configId = request.query.id;
    let data = request.body;

    let db = admin.firestore();
    let configRef = db.collection("configurations").doc(configId);

    configRef.get().then((doc: any) => {
        if (doc.exists) {
            console.log("Previous config " + configId + ": " + doc.data().toString);
            console.log("New config " + configId + ":" + data.toString())
            updateConfig(configRef, data)
            response.status(200).send("Successfully updated config with id " + configId)
        } else {
            console.log("No such config:" + configId);
            response.status(404).send("No such config with id " + configId);
            return
        }
    }).catch((err: any) => {
        let errorLog = "Error updating config with id " + configId + ": " + err;
        console.log(errorLog);
        response.status(500).send(errorLog)
    });
});