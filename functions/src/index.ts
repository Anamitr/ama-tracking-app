import * as functions from 'firebase-functions';
// import firestore from "firebase-admin";
// import * as firestore from "firebase-admin";
// import FirebaseFirestore from ;

const admin = require('firebase-admin');
admin.initializeApp();

exports.getConfig = require('./config-endpoints').getConfig;
exports.updateConfig = require('./config-endpoints').updateConfig;
exports.postLog = require('./logging-endpoints').postLog;
exports.getNextLogId = require('./logging-endpoints').getNextLogId;
