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

  const friendRef = admin.firestore().collection("friends")
      .doc(user.uid)
      .collection("friendData");
  friendRef.doc().set({
    uid: user.uid,
  });
});

exports.impersonateMakeUpperCase = functions.database
    .ref("/conversations/{uid1}/{uid2}/{msgId}")
    .onCreate((snap, context) => {
      const uid1 = context.params.uid1;
      const uid2 = context.params.uid2;
      const msgId = context.params.msgId;

      const messageData = snap.val();
      admin.firestore().collection(`/conversations/${uid2}/${uid1})`)
          .doc(`/${msgId}`)
          .set({messageData});
    });

/*
exports.deleteUserToFirestore = functions.auth.user().onDelete((user) => {
  const usersRef = admin.firestore().collection("users").doc(user.uid);
  const friendRef = admin.firestore().collection("friends").doc(user.uid);
  const weightRef = admin.firestore().collection("weight").doc(user.uid);

  usersRef.delete().then((message) => {
    functions.logger.info("User's user data deleted", {structuredData: true});
  }).catch((error) =>{
    functions.logger.info("User's user data could not be deleted",
        {structuredData: true});
  });

  friendRef.delete().then((message) => {
    functions.logger.info("User's friend data deleted", {structuredData: true});
  }).catch((error) =>{
    functions.logger.info("User's friend data could not be deleted",
        {structuredData: true});
  });

  weightRef.delete().then((message) => {
    functions.logger.info("User's weight data deleted", {structuredData: true});
  }).catch((error) =>{
    functions.logger.info("User's weight data could not be deleted",
        {structuredData: true});
  });
});
*/

