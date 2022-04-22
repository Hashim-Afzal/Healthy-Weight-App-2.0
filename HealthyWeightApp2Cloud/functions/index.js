const functions = require("firebase-functions");

// The Firebase Admin SDK to access Cloud Firestore.
const admin = require("firebase-admin");
admin.initializeApp();

// Create and Deploy Your First Cloud Functions
// https://firebase.google.com/docs/functions/write-firebase-functions

exports.helloWorld = functions.https.onRequest((request, response) => {
  functions.logger.info("Hello logs!", {structuredData: true});
  response.send("Hello world");
});

exports.addUserToFirestoreUsers = functions.auth.user().onCreate((user) => {
  const usersRef = admin.firestore().collection("users");
  usersRef.doc(user.uid).set({
    currentWeight: 0,
    meetFriend: false,
    objective: "lose",
    public: true,
    uid: user.uid,
    userName: "",
    aboutMe: "",
  });
});

exports.onCreateReciprocrateFriendShip = functions.firestore.document("/friends/{uid1}/friendData/{uid2}")
    .onCreate((snap, context) => {
      const uid1 = context.params.uid1;
      const uid2 = context.params.uid2;
      //const friendData = snap.val();

      functions.logger.info(`Message data ${uid1}\\${uid2}`, {structuredData: true});
      const newMessageRef =  admin.firestore().collection(`/friends/${uid2}/friendData`);
      newMessageRef.doc(uid1).set({uid: uid1});

      const usersRef = admin.firestore().collection("users");
      return usersRef.doc(uid2).update({
        meetFriend: false,
      });
    });
