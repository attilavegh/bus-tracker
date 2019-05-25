import * as functions from 'firebase-functions';
import * as admin from 'firebase-admin';

admin.initializeApp();

exports.onBusNotification = functions.firestore.document('buses/{busId}')
    .onUpdate((change, context) => {
        console.log('Bus has been updated');

        const bus = change.after ? change.after.data() : null;
        const busBefore = change.before ? change.before.data() : null
        
        if (bus && busBefore && bus.active === busBefore.active) {
            return;
        }
        
        console.log(bus);

        const notificationType = (bus && bus.active) ? "departure" : "arrival";
        const busId = context.params.busId;
        const busName = (bus) ? bus.name : "Busz"
        const notificationTitle = (bus && bus.active) ? `${bus.name} járat elindult` : "";

        const payload = {
            data: {
                type: notificationType,
                busId: busId,
                busName: busName,
                title: notificationTitle,
            }
        };

        return admin.messaging().sendToTopic("bus_notification", payload);
    });
