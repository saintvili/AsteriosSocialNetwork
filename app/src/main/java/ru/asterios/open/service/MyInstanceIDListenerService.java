package ru.asterios.open.service;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import ru.asterios.open.app.App;


/**
 * Created by Администратор on 23.07.2015.
 */
public class MyInstanceIDListenerService extends FirebaseInstanceIdService {

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is also called
     * when the InstanceID token is initially generated, so this is where
     * you retrieve the token.
     */
    // [START refresh_token]

    @Override
    public void onTokenRefresh() {

        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.e("FCM", "Refreshed token: " + refreshedToken);
        // TODO: Implement this method to send any registration to your app's servers.

//        startService(new Intent(this, RegistrationIntentService.class));
//        sendRegistrationToServer(refreshedToken);

        App.getInstance().setGcmToken(refreshedToken);
    }

}