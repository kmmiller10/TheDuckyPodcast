import * as functions from 'firebase-functions';

// // Start writing Firebase Functions
// // https://firebase.google.com/docs/functions/typescript
//
// export const helloWorld = functions.https.onRequest((request, response) => {
//  response.send("Hello from Firebase!");
// });

// The Cloud Functions for Firebase SDK to create Cloud Functions and setup triggers.
const funcs = require('firebase-functions');

// The Firebase Admin SDK to access the Firebase Realtime Database.
const admin = require('firebase-admin');
admin.initializeApp();

// When a user submits a dailies document, merge the results into the master results sheet
exports.onDailySubmit = funcs.firestore
	.document('dailies-responses/{userId}')
	.onCreate((snap, context) => {
		// Access the firestore
		const firestore = admin.firestore();
		
		// Get the users responses
		const response = snap.data();
		const additionalComments = response.additionalComments;
		const answers = response.answers;
		const inputs = response.inputs;
		
		const getDailyId = firestore.doc('dailies-responses/get-daily-id').get();
		const dailyId = getDailyId.id
		
		const masterList = firestore.doc('dailies-responses/collected-responses')
		masterList.set({
			answers: 'test'
		});
		//ref.set({
		//	test: 'test'
		//});
		
	});