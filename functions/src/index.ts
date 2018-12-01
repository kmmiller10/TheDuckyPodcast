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
	.document('dailies-responses/{userId}/{dailyId}/{id}')
	.onCreate((snap, context) => {
		// Get the user's responses
		const response = snap.data();
	
		const userId = response.userId;
		const dailyId = response.dailyId;
		const userAnswers = response.answers; // { [key: string]: Array<string | number> }
		
		// Grab the daily questionnaire to assemble the collected results
		firestore.doc('dailies/'+dailyId).get().then(dDoc => {
			const dailyDoc = dDoc.data();
			
			// Get the master list of all collected results
			firestore.doc('dailies-responses/collected-responses').get().then(crDoc => {
				const masterDoc = crDoc.data();
				const allDailyResponses = masterDoc[dailyId];
				
				// Sort the questions - they don't come in order
				const questionsUnsorted = Object.keys(dailyDoc.questionAnswers);
				const questions = questionsUnsorted.sort((n1,n2) =>{
					if(n1 > n2) {
						return 1;
					}
					if(n1 < n2) {
						return -1;
					}
					return 0;
				});
				
				// This is the object to be submitted
				const dailyResults: { [key: string]: { [key: string]: { [key: string]: Array<string | number> } | string } } = {};
				const answersMap: { [key: string]: { [key: string]: Array<string | number> } } = {};
				
				if(allDailyResponses == null) {
					// If null, then create the first results document for this daily
					let index = 0;
					for(const question in questions) {
						const answers: { [key: string]: Array<string | number> } = {};
						
						// Add user answer
						const answer = userAnswers[(index.toString())];
						
						answers[userId] = answer;
						
						answersMap[questions[index]] = answers;
						index = index + 1;
					}
					
					// Create first additional comments map
					const additionalComments: { [key: string]: string } = {};
					additionalComments[userId] = response.additionalComments;
					
					// Create daily results for this user
					dailyResults.additionalComments = additionalComments;
					dailyResults.keyQuestionAnswers = dailyDoc.questionAnswers; // Only need to include on creation of first results doc
					dailyResults.answers = answersMap;
			
					firestore.doc('dailies-responses/collected-responses').set({
						[dailyId]: dailyResults
					});
				} else {
					// Otherwise, collect current results and append this user's results to them
					let index = 0;
					for(const question in questions) {
						// Get current answers array
						const answers = allDailyResponses.answers[questions[index]];
						
						// Append users answer
						const answer = userAnswers[(index.toString())];
						answers[userId] = answer;
						
						answersMap[questions[index]] = answers;
						index = index + 1;
					}
					
					// Append user's additional comments to the current list of additional comments
					const currentComments = allDailyResponses.additionalComments;
					currentComments[userId] = response.additionalComments;

					dailyResults.additionalComments = currentComments;
					dailyResults.keyQuestionAnswers = allDailyResponses.keyQuestionAnswers;
					dailyResults.answers = answersMap;
					
					firestore.doc('dailies-responses/collected-responses').set({
						[dailyId]: dailyResults
					});
				}			
			});
		});
	});