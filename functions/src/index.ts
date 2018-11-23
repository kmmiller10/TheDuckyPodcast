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
	
		const answers = response.answers;	
		const inputs = response.inputs;
		
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
				const dailyResults: { [key: string]: { [key: string]: Array<string | number> } | Array<string> } = {};
				const answersMap: { [key: string]: Array<string | number> } = {};
				
				if(allDailyResponses == null) {
					// If null, then create the first results document for this daily
					let index = 0;
					for(const question in questions) {
						const answersAry: Array<string | number> = new Array<string | number>();

						if(inputs[index] === "") {
							// If inputs is empty, then the user did not choose "other" option.
							// Append answer index (add 1 to it since the first item in the key is the input type)
							answersAry.push(answers[index] + 1);
						} else {
							// If inputs is not empty, user chose other and provided a response
							answersAry.push(inputs[index]);
						}
						
						answersMap[questions[index]] = answersAry;
						index = index + 1;
					}
					
					// Create first additional comments array
					const additionalComments: Array<string> = new Array<string>();
					additionalComments.push(response.additionalComments);
					
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
						const answersAry: Array<string | number> = allDailyResponses.answers[questions[index]];
						
						if(inputs[index] === "") {
							answersAry.push(answers[index] + 1);
						} else {
							answersAry.push(inputs[index]);
						}
						
						answersMap[questions[index]] = answersAry;
						index = index + 1;
					}
					
					// Append user's additional comments to the current list of additional comments
					const currentComments: Array<string> = allDailyResponses.additionalComments;
					
					if(response.additionalComments === "") {
						// Nothing to append here
					} else {
						currentComments.push(response.additionalComments);
					}

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