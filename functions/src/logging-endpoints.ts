import * as functions from "firebase-functions";

const admin = require('firebase-admin');

exports.postLog = functions.https.onRequest((request, response) => {
    let data = request.body;
    let db = admin.firestore();

    console.log("postLog: attempting to add log : ", data);

    let logsRef = db.collection("logs").doc(data.configId).collection("logs");
    let externalId = logsRef.doc().id;

    let geoLogToInsert = {
        "externalId": externalId,
        "configId": data.configId,
        "date": new Date(data.date),
        "content": data.content
    };

    logsRef.add(geoLogToInsert).then((doc: any) => {
        let responseMessage = ('Added log with ID: ${doc.id}' + doc.id + " - " + request.body);
        console.log(responseMessage);
        response.status(201).send(doc.id);
    });
});

function getTestLog() {
    let testLog = {
        "date": new Date(),
        "content": "test log"
    };
    return testLog;
}

function getEmptyLog() {
    let testLog = {
        "date": "",
        "content": "test log"
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


//TODO: Doesnt work
exports.clearLogs = functions.https.onRequest(((request, response) => {
    let configId = request.query.id;
    let db = admin.firestore();
    let path = "logs/" + configId + "/logs";

    console.log(`Requested to delete path ${path}`);

    let ref = db.collection("logs").doc(configId).collection("logs");

    deleteCollection(db, ref)
        .then(() => {
            response.status(200).send()
        })
        .catch((err: Error) => {
            console.log(err.message)
        });
}));

function deleteCollection(db:any, collectionRef:any, batchSize = 100) {
    let query = collectionRef.orderBy('__name__').limit(batchSize);

    return new Promise((resolve, reject) => {
        deleteQueryBatch(db, query, batchSize, resolve, reject);
    });
}

function deleteQueryBatch(db:any, query:any, batchSize:any, resolve:any, reject:any) {
    query.get()
        .then((snapshot: any) => {
            // When there are no documents left, we are done
            if (snapshot.size == 0) {
                return 0;
            }

            // Delete documents in a batch QueryDocumentSnapshot
            let batch = db.batch();
            snapshot.docs.forEach((doc:any) => {
                batch.delete(doc.ref);
            });

            return batch.commit().then(() => {
                return snapshot.size;
            });
        }).then((numDeleted: any) => {
        if (numDeleted === 0) {
            resolve();
            return;
        }

        // Recurse on the next process tick, to avoid
        // exploding the stack.
        process.nextTick(() => {
            deleteQueryBatch(db, query, batchSize, resolve, reject);
        });
    })
        .catch(reject);
}