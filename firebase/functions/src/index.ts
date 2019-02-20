import * as functions from 'firebase-functions';
import * as admin from 'firebase-admin';

admin.initializeApp();

exports.onBusDepartureNotification = functions.firestore.document('buses/{busId}')
    .onUpdate((change, context) => {
        console.log('Bus has been updated');

        const bus = change.after ? change.after.data() : null;
        const busBefore = change.before ? change.before.data() : null
        
        if (bus && busBefore && (!bus.active || bus.active === busBefore.active)) {
            return;
        }
        
        console.log(bus);

        const payload = {
            data: {
                busId: context.params.busId,
                busName: bus ? bus.name : "Busz"
            },
            notification: {
                body: `${bus ? bus.name : "A"} járat már úton van.`,
            }
        };

        return admin.messaging().sendToTopic("departure", payload);
    });
