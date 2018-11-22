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
// Access the firestore
const firestore = admin.firestore();
const settings = {timestampsInSnapshots: true}; // Needed for Date timestamps
firestore.settings(settings);

// When a user submits a dailies document, merge the results into the master results sheet
exports.onDailySubmit = funcs.firestore
	.document('dailies-responses/{userId}')
	.onCreate((snap, context) => {
		// Get the user's responses
		const response = snap.data();
		const dailyId: string = response.dailyId
		const additionalComments: Array<string> = new Array<string>();
		additionalComments.push(response.additionalComments);
		const answers = response.answers;	
		const inputs = response.inputs;
		
		// Grab the daily questionnaire to assemble the collected results
		firestore.doc('dailies/'+dailyId).get().then(doc => {
			const dailyDoc = doc.data();
			
			const questions = Object.keys(dailyDoc.questionAnswers)
			
			const answersMap: { [key: string]: Array<string | number> } = {};
			let index = 0;
			for(const question in questions) {
				if(inputs[index] === "") {
					answersMap[question] = answers[index] + 1;
				} else {
					answersMap[question] = inputs[index];
				}
				
				index = index + 1;
			}
			
			const dailyResults: { [key: string]: { [key: string]: Array<string | number> } | Array<string> } = {};
			dailyResults.additionalComments = additionalComments;
			dailyResults.keyQuestionAnswers = dailyDoc.questionAnswers;
			dailyResults.answers = answersMap;
			
			const masterList = firestore.doc('dailies-responses/collected-responses');
			masterList.set({
				dailyId: dailyResults
			});
		});
		
	});