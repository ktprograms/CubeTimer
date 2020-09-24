const functions = require('firebase-functions');

const admin = require('firebase-admin');
admin.initializeApp();

exports.initDatabase = functions.auth.user().onCreate((user) => {
    var ref = admin.database().ref();
    var uid = user.uid;
    ref.update({
        [uid]: {
            Two: "",
            Three: "",
            Four: "",
            Five: "",
            Six: "",
            Seven: "",
            Pyra: "",
            Squan: "",
            Mega: "",
            Clock: "",
            Skewb: "",
        }
    })
});

// // Create and Deploy Your First Cloud Functions
// // https://firebase.google.com/docs/functions/write-firebase-functions
//
// exports.helloWorld = functions.https.onRequest((request, response) => {
//   functions.logger.info("Hello logs!", {structuredData: true});
//   response.send("Hello from Firebase!");
// });

