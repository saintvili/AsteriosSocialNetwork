package ru.asterios.open.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

import ru.asterios.open.AppActivity;
import ru.asterios.open.ChatFragment;
import ru.asterios.open.DialogsActivity;
import ru.asterios.open.FriendsActivity;
import ru.asterios.open.MainActivity;
import ru.asterios.open.NotificationsActivity;
import ru.asterios.open.R;
import ru.asterios.open.app.App;
import ru.asterios.open.constants.Constants;

public class MyFcmListenerService extends FirebaseMessagingService implements Constants {

    @Override
    public void onMessageReceived(RemoteMessage message) {

        String from = message.getFrom();
        Map data = message.getData();

        Log.e("Message", "Could not parse malformed JSON: \"" + data.toString() + "\"");

        generateNotification(getApplicationContext(), data);
    }

//    @Override
//    public void onMessageReceived(String from, Bundle data) {
//
//        generateNotification(getApplicationContext(), data);
//        Log.e("Message", "Could not parse malformed JSON: \"" + data.toString() + "\"");
//    }

    @Override
    public void onDeletedMessages() {
        sendNotification("Deleted messages on server");
    }

    @Override
    public void onMessageSent(String msgId) {

        sendNotification("Upstream message sent. Id=" + msgId);
    }

    // Put the message into a notification and post it.
    // This is just one simple example of what you might choose to do with
    // a GCM message.
    private void sendNotification(String msg) {

        Log.e("Message", "Could not parse malformed JSON: \"" + msg + "\"");
    }

    /**
     * Create a notification to inform the user that server has sent a message.
     */
    private static void generateNotification(Context context, Map data) {

        String CHANNEL_ID = "my_channel_01"; // id for channel.

        CharSequence name = context.getString(R.string.channel_name);     // user visible name of channel.

        NotificationChannel mChannel;

        String msgId = "0";
        String msgFromUserId = "0";
        String msgFromUserState = "0";
        String msgFromUserVerify = "0";
        String msgFromUserUsername = "";
        String msgFromUserFullname = "";
        String msgFromUserPhotoUrl = "";
        String msgMessage = "";
        String msgImgUrl = "";
        String msgCreateAt = "0";
        String msgDate = "";
        String msgTimeAgo = "";
        String msgRemoveAt = "0";

        String message = data.get("msg").toString();
        String type = data.get("type").toString();
        String actionId = data.get("id").toString();
        String accountId = data.get("accountId").toString();

     
        if (Integer.valueOf(type) == GCM_NOTIFY_MESSAGE) {

            msgId = data.get("msgId").toString();
            msgFromUserId = data.get("msgFromUserId").toString();
            msgFromUserState = data.get("msgFromUserState").toString();
            msgFromUserVerify = data.get("msgFromUserVerify").toString();
            if (data.containsKey("msgFromUserUsername")) {

    msgFromUserUsername = data.get("msgFromUserUsername").toString();
}

if (data.containsKey("msgFromUserFullname")) {

    msgFromUserFullname = data.get("msgFromUserFullname").toString();
}

if (data.containsKey("msgFromUserPhotoUrl")) {

    msgFromUserPhotoUrl = data.get("msgFromUserPhotoUrl").toString();
}

if (data.containsKey("msgMessage")) {

    msgMessage = data.get("msgMessage").toString();
}

if (data.containsKey("msgImgUrl")) {

    msgImgUrl = data.get("msgImgUrl").toString();
}
            
            msgRemoveAt = data.get("msgRemoveAt").toString();
        }

        int icon = R.drawable.ic_action_push_notification;
        long when = System.currentTimeMillis();
        String title = context.getString(R.string.app_name);

//        App.getInstance().reload();

        switch (Integer.valueOf(type)) {

            case GCM_NOTIFY_SYSTEM: {

                NotificationCompat.Builder mBuilder =
                        new NotificationCompat.Builder(context, CHANNEL_ID)
                                .setSmallIcon(R.drawable.ic_action_push_notification)
                                .setContentTitle(title)
                                .setContentText(message);

                Intent resultIntent;

                if (App.getInstance().getId() != 0) {

                    resultIntent = new Intent(context, MainActivity.class);

                } else {

                    resultIntent = new Intent(context, AppActivity.class);
                }

                resultIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

                TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
                stackBuilder.addParentStack(MainActivity.class);
                stackBuilder.addNextIntent(resultIntent);
                PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
                mBuilder.setContentIntent(resultPendingIntent);

                NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                    int importance = NotificationManager.IMPORTANCE_HIGH;

                    mChannel = new NotificationChannel(CHANNEL_ID, name, importance);

                    mNotificationManager.createNotificationChannel(mChannel);
                }

                mBuilder.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);
                mBuilder.setAutoCancel(true);
                mNotificationManager.notify(0, mBuilder.build());

                break;
            }

            case GCM_NOTIFY_CUSTOM: {

                if (App.getInstance().getId() != 0) {

                    NotificationCompat.Builder mBuilder =
                            new NotificationCompat.Builder(context, CHANNEL_ID)
                                    .setSmallIcon(R.drawable.ic_action_push_notification)
                                    .setContentTitle(title)
                                    .setContentText(message);

                    Intent resultIntent = new Intent(context, MainActivity.class);
                    resultIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

                    TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
                    stackBuilder.addParentStack(MainActivity.class);
                    stackBuilder.addNextIntent(resultIntent);
                    PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
                    mBuilder.setContentIntent(resultPendingIntent);

                    NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                        int importance = NotificationManager.IMPORTANCE_HIGH;

                        mChannel = new NotificationChannel(CHANNEL_ID, name, importance);

                        mNotificationManager.createNotificationChannel(mChannel);
                    }

                    mBuilder.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);
                    mBuilder.setAutoCancel(true);
                    mNotificationManager.notify(0, mBuilder.build());
                }

                break;
            }

            case GCM_NOTIFY_PERSONAL: {

                if (App.getInstance().getId() != 0 && Long.toString(App.getInstance().getId()).equals(accountId)) {

                    NotificationCompat.Builder mBuilder =
                            new NotificationCompat.Builder(context, CHANNEL_ID)
                                    .setSmallIcon(R.drawable.ic_action_push_notification)
                                    .setContentTitle(title)
                                    .setContentText(message);

                    Intent resultIntent = new Intent(context, MainActivity.class);
                    resultIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

                    TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
                    stackBuilder.addParentStack(MainActivity.class);
                    stackBuilder.addNextIntent(resultIntent);
                    PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
                    mBuilder.setContentIntent(resultPendingIntent);

                    NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                        int importance = NotificationManager.IMPORTANCE_HIGH;

                        mChannel = new NotificationChannel(CHANNEL_ID, name, importance);

                        mNotificationManager.createNotificationChannel(mChannel);
                    }

                    mBuilder.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);
                    mBuilder.setAutoCancel(true);
                    mNotificationManager.notify(0, mBuilder.build());
                }

                break;
            }

            case GCM_NOTIFY_LIKE: {

                if (App.getInstance().getId() != 0 && Long.toString(App.getInstance().getId()).equals(accountId)) {

                    App.getInstance().setNotificationsCount(App.getInstance().getNotificationsCount() + 1);

                    message = context.getString(R.string.label_gcm_like);

                    NotificationCompat.Builder mBuilder =
                            new NotificationCompat.Builder(context, CHANNEL_ID)
                                    .setSmallIcon(R.drawable.ic_action_push_notification)
                                    .setContentTitle(title)
                                    .setContentText(message);

                    Intent resultIntent = new Intent(context, NotificationsActivity.class);
                    resultIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

                    TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
                    stackBuilder.addParentStack(NotificationsActivity.class);
                    stackBuilder.addNextIntent(resultIntent);
                    PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
                    mBuilder.setContentIntent(resultPendingIntent);

                    NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                        int importance = NotificationManager.IMPORTANCE_HIGH;

                        mChannel = new NotificationChannel(CHANNEL_ID, name, importance);

                        mNotificationManager.createNotificationChannel(mChannel);
                    }

                    mBuilder.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);
                    mBuilder.setAutoCancel(true);
                    mNotificationManager.notify(0, mBuilder.build());
                }

                break;
            }

            case GCM_NOTIFY_IMAGE_LIKE: {

                if (App.getInstance().getId() != 0 && Long.toString(App.getInstance().getId()).equals(accountId)) {

                    App.getInstance().setNotificationsCount(App.getInstance().getNotificationsCount() + 1);

                    message = context.getString(R.string.label_gcm_like);

                    NotificationCompat.Builder mBuilder =
                            new NotificationCompat.Builder(context, CHANNEL_ID)
                                    .setSmallIcon(R.drawable.ic_action_push_notification)
                                    .setContentTitle(title)
                                    .setContentText(message);

                    Intent resultIntent = new Intent(context, NotificationsActivity.class);
                    resultIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

                    TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
                    stackBuilder.addParentStack(NotificationsActivity.class);
                    stackBuilder.addNextIntent(resultIntent);
                    PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
                    mBuilder.setContentIntent(resultPendingIntent);

                    NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                        int importance = NotificationManager.IMPORTANCE_HIGH;

                        mChannel = new NotificationChannel(CHANNEL_ID, name, importance);

                        mNotificationManager.createNotificationChannel(mChannel);
                    }

                    mBuilder.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);
                    mBuilder.setAutoCancel(true);
                    mNotificationManager.notify(0, mBuilder.build());
                }

                break;
            }

            case GCM_NOTIFY_VIDEO_LIKE: {

                if (App.getInstance().getId() != 0 && Long.toString(App.getInstance().getId()).equals(accountId)) {

                    App.getInstance().setNotificationsCount(App.getInstance().getNotificationsCount() + 1);

                    message = context.getString(R.string.label_gcm_like);

                    NotificationCompat.Builder mBuilder =
                            new NotificationCompat.Builder(context, CHANNEL_ID)
                                    .setSmallIcon(R.drawable.ic_action_push_notification)
                                    .setContentTitle(title)
                                    .setContentText(message);

                    Intent resultIntent = new Intent(context, NotificationsActivity.class);
                    resultIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

                    TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
                    stackBuilder.addParentStack(NotificationsActivity.class);
                    stackBuilder.addNextIntent(resultIntent);
                    PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
                    mBuilder.setContentIntent(resultPendingIntent);

                    NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                        int importance = NotificationManager.IMPORTANCE_HIGH;

                        mChannel = new NotificationChannel(CHANNEL_ID, name, importance);

                        mNotificationManager.createNotificationChannel(mChannel);
                    }

                    mBuilder.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);
                    mBuilder.setAutoCancel(true);
                    mNotificationManager.notify(0, mBuilder.build());
                }

                break;
            }

            case GCM_NOTIFY_FOLLOWER: {

                if (App.getInstance().getId() != 0 && Long.toString(App.getInstance().getId()).equals(accountId)) {

                    App.getInstance().setNotificationsCount(App.getInstance().getNotificationsCount() + 1);

                    message = context.getString(R.string.label_gcm_friend_request);

                    NotificationCompat.Builder mBuilder =
                            new NotificationCompat.Builder(context, CHANNEL_ID)
                                    .setSmallIcon(R.drawable.ic_action_push_notification)
                                    .setContentTitle(title)
                                    .setContentText(message);

                    Intent resultIntent = new Intent(context, NotificationsActivity.class);
                    resultIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

                    TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
                    stackBuilder.addParentStack(NotificationsActivity.class);
                    stackBuilder.addNextIntent(resultIntent);
                    PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
                    mBuilder.setContentIntent(resultPendingIntent);

                    NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                        int importance = NotificationManager.IMPORTANCE_HIGH;

                        mChannel = new NotificationChannel(CHANNEL_ID, name, importance);

                        mNotificationManager.createNotificationChannel(mChannel);
                    }

                    mBuilder.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);
                    mBuilder.setAutoCancel(true);
                    mNotificationManager.notify(0, mBuilder.build());
                }

                break;
            }

            case GCM_FRIEND_REQUEST_ACCEPTED: {

                if (App.getInstance().getId() != 0 && Long.toString(App.getInstance().getId()).equals(accountId)) {

                    App.getInstance().setNewFriendsCount(App.getInstance().getNewFriendsCount() + 1);

                    message = context.getString(R.string.label_gcm_friend_request_accepted);

                    NotificationCompat.Builder mBuilder =
                            new NotificationCompat.Builder(context, CHANNEL_ID)
                                    .setSmallIcon(R.drawable.ic_action_push_notification)
                                    .setContentTitle(title)
                                    .setContentText(message);

                    Intent resultIntent = new Intent(context, FriendsActivity.class);
                    resultIntent.putExtra("gcm", true);
                    resultIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

                    TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
                    stackBuilder.addParentStack(FriendsActivity.class);
                    stackBuilder.addNextIntent(resultIntent);
                    PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
                    mBuilder.setContentIntent(resultPendingIntent);

                    NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                        int importance = NotificationManager.IMPORTANCE_HIGH;

                        mChannel = new NotificationChannel(CHANNEL_ID, name, importance);

                        mNotificationManager.createNotificationChannel(mChannel);
                    }

                    mBuilder.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);
                    mBuilder.setAutoCancel(true);
                    mNotificationManager.notify(0, mBuilder.build());
                }

                break;
            }

            case GCM_NOTIFY_COMMENT: {

                if (App.getInstance().getId() != 0 && Long.toString(App.getInstance().getId()).equals(accountId)) {

                    App.getInstance().setNotificationsCount(App.getInstance().getNotificationsCount() + 1);

                    message = context.getString(R.string.label_gcm_comment);

                    NotificationCompat.Builder mBuilder =
                            new NotificationCompat.Builder(context, CHANNEL_ID)
                                    .setSmallIcon(R.drawable.ic_action_push_notification)
                                    .setContentTitle(title)
                                    .setContentText(message);

                    Intent resultIntent = new Intent(context, NotificationsActivity.class);
                    resultIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

                    TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
                    stackBuilder.addParentStack(NotificationsActivity.class);
                    stackBuilder.addNextIntent(resultIntent);
                    PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
                    mBuilder.setContentIntent(resultPendingIntent);

                    NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                        int importance = NotificationManager.IMPORTANCE_HIGH;

                        mChannel = new NotificationChannel(CHANNEL_ID, name, importance);

                        mNotificationManager.createNotificationChannel(mChannel);
                    }

                    mBuilder.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);
                    mBuilder.setAutoCancel(true);
                    mNotificationManager.notify(0, mBuilder.build());
                }

                break;
            }

            case GCM_NOTIFY_IMAGE_COMMENT: {

                if (App.getInstance().getId() != 0 && Long.toString(App.getInstance().getId()).equals(accountId)) {

                    App.getInstance().setNotificationsCount(App.getInstance().getNotificationsCount() + 1);

                    message = context.getString(R.string.label_gcm_comment);

                    NotificationCompat.Builder mBuilder =
                            new NotificationCompat.Builder(context, CHANNEL_ID)
                                    .setSmallIcon(R.drawable.ic_action_push_notification)
                                    .setContentTitle(title)
                                    .setContentText(message);

                    Intent resultIntent = new Intent(context, NotificationsActivity.class);
                    resultIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

                    TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
                    stackBuilder.addParentStack(NotificationsActivity.class);
                    stackBuilder.addNextIntent(resultIntent);
                    PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
                    mBuilder.setContentIntent(resultPendingIntent);

                    NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                        int importance = NotificationManager.IMPORTANCE_HIGH;

                        mChannel = new NotificationChannel(CHANNEL_ID, name, importance);

                        mNotificationManager.createNotificationChannel(mChannel);
                    }

                    mBuilder.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);
                    mBuilder.setAutoCancel(true);
                    mNotificationManager.notify(0, mBuilder.build());
                }

                break;
            }

            case GCM_NOTIFY_VIDEO_COMMENT: {

                if (App.getInstance().getId() != 0 && Long.toString(App.getInstance().getId()).equals(accountId)) {

                    App.getInstance().setNotificationsCount(App.getInstance().getNotificationsCount() + 1);

                    message = context.getString(R.string.label_gcm_comment);

                    NotificationCompat.Builder mBuilder =
                            new NotificationCompat.Builder(context, CHANNEL_ID)
                                    .setSmallIcon(R.drawable.ic_action_push_notification)
                                    .setContentTitle(title)
                                    .setContentText(message);

                    Intent resultIntent = new Intent(context, NotificationsActivity.class);
                    resultIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

                    TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
                    stackBuilder.addParentStack(NotificationsActivity.class);
                    stackBuilder.addNextIntent(resultIntent);
                    PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
                    mBuilder.setContentIntent(resultPendingIntent);

                    NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                        int importance = NotificationManager.IMPORTANCE_HIGH;

                        mChannel = new NotificationChannel(CHANNEL_ID, name, importance);

                        mNotificationManager.createNotificationChannel(mChannel);
                    }

                    mBuilder.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);
                    mBuilder.setAutoCancel(true);
                    mNotificationManager.notify(0, mBuilder.build());
                }

                break;
            }

            case GCM_NOTIFY_COMMENT_REPLY: {

                if (App.getInstance().getId() != 0 && Long.toString(App.getInstance().getId()).equals(accountId)) {

                    App.getInstance().setNotificationsCount(App.getInstance().getNotificationsCount() + 1);

                    message = context.getString(R.string.label_gcm_comment_reply);

                    NotificationCompat.Builder mBuilder =
                            new NotificationCompat.Builder(context, CHANNEL_ID)
                                    .setSmallIcon(R.drawable.ic_action_push_notification)
                                    .setContentTitle(title)
                                    .setContentText(message);

                    Intent resultIntent = new Intent(context, NotificationsActivity.class);
                    resultIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

                    TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
                    stackBuilder.addParentStack(NotificationsActivity.class);
                    stackBuilder.addNextIntent(resultIntent);
                    PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
                    mBuilder.setContentIntent(resultPendingIntent);

                    NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                        int importance = NotificationManager.IMPORTANCE_HIGH;

                        mChannel = new NotificationChannel(CHANNEL_ID, name, importance);

                        mNotificationManager.createNotificationChannel(mChannel);
                    }

                    mBuilder.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);
                    mBuilder.setAutoCancel(true);
                    mNotificationManager.notify(0, mBuilder.build());
                }

                break;
            }

            case GCM_NOTIFY_IMAGE_COMMENT_REPLY: {

                if (App.getInstance().getId() != 0 && Long.toString(App.getInstance().getId()).equals(accountId)) {

                    App.getInstance().setNotificationsCount(App.getInstance().getNotificationsCount() + 1);

                    message = context.getString(R.string.label_gcm_comment_reply);

                    NotificationCompat.Builder mBuilder =
                            new NotificationCompat.Builder(context, CHANNEL_ID)
                                    .setSmallIcon(R.drawable.ic_action_push_notification)
                                    .setContentTitle(title)
                                    .setContentText(message);

                    Intent resultIntent = new Intent(context, NotificationsActivity.class);
                    resultIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

                    TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
                    stackBuilder.addParentStack(NotificationsActivity.class);
                    stackBuilder.addNextIntent(resultIntent);
                    PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
                    mBuilder.setContentIntent(resultPendingIntent);

                    NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                        int importance = NotificationManager.IMPORTANCE_HIGH;

                        mChannel = new NotificationChannel(CHANNEL_ID, name, importance);

                        mNotificationManager.createNotificationChannel(mChannel);
                    }

                    mBuilder.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);
                    mBuilder.setAutoCancel(true);
                    mNotificationManager.notify(0, mBuilder.build());
                }

                break;
            }

            case GCM_NOTIFY_VIDEO_COMMENT_REPLY: {

                if (App.getInstance().getId() != 0 && Long.toString(App.getInstance().getId()).equals(accountId)) {

                    App.getInstance().setNotificationsCount(App.getInstance().getNotificationsCount() + 1);

                    message = context.getString(R.string.label_gcm_comment_reply);

                    NotificationCompat.Builder mBuilder =
                            new NotificationCompat.Builder(context, CHANNEL_ID)
                                    .setSmallIcon(R.drawable.ic_action_push_notification)
                                    .setContentTitle(title)
                                    .setContentText(message);

                    Intent resultIntent = new Intent(context, NotificationsActivity.class);
                    resultIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

                    TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
                    stackBuilder.addParentStack(NotificationsActivity.class);
                    stackBuilder.addNextIntent(resultIntent);
                    PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
                    mBuilder.setContentIntent(resultPendingIntent);

                    NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                        int importance = NotificationManager.IMPORTANCE_HIGH;

                        mChannel = new NotificationChannel(CHANNEL_ID, name, importance);

                        mNotificationManager.createNotificationChannel(mChannel);
                    }

                    mBuilder.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);
                    mBuilder.setAutoCancel(true);
                    mNotificationManager.notify(0, mBuilder.build());
                }

                break;
            }

            case GCM_NOTIFY_GIFT: {

                if (App.getInstance().getId() != 0 && Long.toString(App.getInstance().getId()).equals(accountId)) {

                    App.getInstance().setNotificationsCount(App.getInstance().getNotificationsCount() + 1);

                    message = context.getString(R.string.label_gcm_gift);

                    NotificationCompat.Builder mBuilder =
                            new NotificationCompat.Builder(context, CHANNEL_ID)
                                    .setSmallIcon(R.drawable.ic_action_push_notification)
                                    .setContentTitle(title)
                                    .setContentText(message);

                    Intent resultIntent = new Intent(context, NotificationsActivity.class);
                    resultIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

                    TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
                    stackBuilder.addParentStack(NotificationsActivity.class);
                    stackBuilder.addNextIntent(resultIntent);
                    PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
                    mBuilder.setContentIntent(resultPendingIntent);

                    NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                        int importance = NotificationManager.IMPORTANCE_HIGH;

                        mChannel = new NotificationChannel(CHANNEL_ID, name, importance);

                        mNotificationManager.createNotificationChannel(mChannel);
                    }

                    mBuilder.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);
                    mBuilder.setAutoCancel(true);
                    mNotificationManager.notify(0, mBuilder.build());
                }

                break;
            }

            case GCM_NOTIFY_MESSAGE: {

                Log.e("FCM", "New Message");

                if (App.getInstance().getId() != 0 && Long.valueOf(accountId) == App.getInstance().getId()) {

                    if (App.getInstance().getCurrentChatId() == Integer.valueOf(actionId)) {

                        Intent i = new Intent(ChatFragment.BROADCAST_ACTION);
                        i.putExtra(ChatFragment.PARAM_TASK, 0);
                        i.putExtra(ChatFragment.PARAM_STATUS, ChatFragment.STATUS_START);

                        i.putExtra("msgId", Integer.valueOf(msgId));
                        i.putExtra("msgFromUserId", Long.valueOf(msgFromUserId));
                        i.putExtra("msgFromUserState", Integer.valueOf(msgFromUserState));
                        i.putExtra("msgFromUserVerify", Integer.valueOf(msgFromUserVerify));
                        i.putExtra("msgFromUserUsername", String.valueOf(msgFromUserUsername));
                        i.putExtra("msgFromUserFullname", String.valueOf(msgFromUserFullname));
                        i.putExtra("msgFromUserPhotoUrl", String.valueOf(msgFromUserPhotoUrl));
                        i.putExtra("msgMessage", String.valueOf(msgMessage));
                        i.putExtra("msgImgUrl", String.valueOf(msgImgUrl));
                        i.putExtra("msgCreateAt", Integer.valueOf(msgCreateAt));
                        i.putExtra("msgDate", String.valueOf(msgDate));
                        i.putExtra("msgTimeAgo", String.valueOf(msgTimeAgo));

                        context.sendBroadcast(i);

                    } else {

                        if (App.getInstance().getMessagesCount() == 0) App.getInstance().setMessagesCount(App.getInstance().getMessagesCount() + 1);

                        if (App.getInstance().getAllowMessagesGCM() == ENABLED) {

                            message = context.getString(R.string.label_gcm_message);

                            NotificationCompat.Builder mBuilder =
                                    new NotificationCompat.Builder(context, CHANNEL_ID)
                                            .setSmallIcon(R.drawable.ic_action_push_notification)
                                            .setContentTitle(title)
                                            .setContentText(message);

                            Intent resultIntent = new Intent(context, DialogsActivity.class);
                            resultIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

                            TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
                            stackBuilder.addParentStack(DialogsActivity.class);
                            stackBuilder.addNextIntent(resultIntent);
                            PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
                            mBuilder.setContentIntent(resultPendingIntent);

                            NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                                int importance = NotificationManager.IMPORTANCE_HIGH;

                                mChannel = new NotificationChannel(CHANNEL_ID, name, importance);

                                mNotificationManager.createNotificationChannel(mChannel);
                            }

                            mBuilder.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);
                            mBuilder.setAutoCancel(true);
                            mNotificationManager.notify(0, mBuilder.build());
                        }
                    }
                }

                break;
            }

            case GCM_NOTIFY_SEEN: {

                if (App.getInstance().getId() != 0 && Long.valueOf(accountId) == App.getInstance().getId()) {

                    Log.e("SEEN", "IN LISTENER");

                    if (App.getInstance().getCurrentChatId() == Integer.valueOf(actionId)) {

                        Intent i = new Intent(ChatFragment.BROADCAST_ACTION_SEEN);
                        i.putExtra(ChatFragment.PARAM_TASK, 0);
                        i.putExtra(ChatFragment.PARAM_STATUS, ChatFragment.STATUS_START);
                        context.sendBroadcast(i);
                    }

                    break;
                }
            }

            case GCM_NOTIFY_TYPING_START: {

                if (App.getInstance().getId() != 0 && Long.valueOf(accountId) == App.getInstance().getId()) {

                    if (App.getInstance().getCurrentChatId() == Integer.valueOf(actionId)) {

                        Intent i = new Intent(ChatFragment.BROADCAST_ACTION_TYPING_START);
                        i.putExtra(ChatFragment.PARAM_TASK, 0);
                        i.putExtra(ChatFragment.PARAM_STATUS, ChatFragment.STATUS_START);
                        context.sendBroadcast(i);
                    }

                    break;
                }
            }

            case GCM_NOTIFY_TYPING_END: {

                if (App.getInstance().getId() != 0 && Long.valueOf(accountId) == App.getInstance().getId()) {

                    if (App.getInstance().getCurrentChatId() == Integer.valueOf(actionId)) {

                        Intent i = new Intent(ChatFragment.BROADCAST_ACTION_TYPING_END);
                        i.putExtra(ChatFragment.PARAM_TASK, 0);
                        i.putExtra(ChatFragment.PARAM_STATUS, ChatFragment.STATUS_START);
                        context.sendBroadcast(i);
                    }

                    break;
                }
            }

            default: {

                break;
            }
        }
    }
}
