package com.example.vcardapp;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.StrictMode;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONObject;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class MyFirebaseMessagingService extends FirebaseMessagingService {
    public static final String FCM_API = "https://fcm.googleapis.com/fcm/send";
    public static final String SERVER_KEY = BuildConfig.FCM_SERVER_KEY;

    private static final String CHANNEL_ID = "Default"; // Change this to your desired channel ID


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        String title = remoteMessage.getData().get("title");
        String body = remoteMessage.getData().get("body");
        String valor = remoteMessage.getData().get("value");


        // Define the static parts of the message
        String prefix = "Recebeu ";
        String euroSymbol = "€";

        // Find the index of the euro symbol
        int euroIndex = title.indexOf(euroSymbol);
        valor = title.substring(prefix.length(), euroIndex).trim();

        //showNotification(title, body, this);

        String ultimosNoveCaracteres = title.substring(Math.max(0, title.length() - 9));

        // broadcast the notification to the activity
        Intent intent = new Intent("incomingNotification");
        intent.putExtra("title", ultimosNoveCaracteres);
        intent.putExtra("body", body);
        intent.putExtra("value", valor);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);

    }

    private void showNotification(String title, String body, Context context) {
        // Build the notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(body)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence channelName = "Default";
            String channelDescription = "My Channel";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;

            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            // Check if the channel already exists
            NotificationChannel existingChannel = notificationManager.getNotificationChannel(CHANNEL_ID);
            if (existingChannel == null) {
                // Create the channel if it doesn't exist
                NotificationChannel channel = new NotificationChannel(CHANNEL_ID, channelName, importance);
                channel.setDescription(channelDescription);
                notificationManager.createNotificationChannel(channel);
            }

            // Show the notification
            notificationManager.notify(0, builder.build());
        }
    }

    public boolean sendNotification(String title, String body, String token, String value, Boolean notificationsEnabled, Context context) {

        OkHttpClient client = new OkHttpClient();
        MediaType mediaType = MediaType.parse("application/json");
        JSONObject jsonNotif = new JSONObject();
        JSONObject jsonObj = new JSONObject();

        try {
            if (notificationsEnabled) {
                jsonNotif.put("title", title);
                jsonNotif.put("body", body);
                jsonNotif.put("value", value);
                jsonObj.put("notification", jsonNotif);
            }
            jsonObj.put("to", token);
            jsonObj.put("data", new JSONObject()); // Empty "data" object to send a data-only message
            jsonObj.getJSONObject("data").put("title", title);
            jsonObj.getJSONObject("data").put("body", body);
            jsonObj.getJSONObject("data").put("value", value);

        } catch (Exception e) {
            e.printStackTrace();
        }

        RequestBody bodyReq = RequestBody.create(mediaType, jsonObj.toString());
        Request request = new Request.Builder().url(FCM_API)
                .post(bodyReq)
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Bearer " + SERVER_KEY)
                .build();

        try {

            if (android.os.Build.VERSION.SDK_INT > 9) {
                StrictMode.ThreadPolicy gfgPolicy =
                        new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(gfgPolicy);
            }
            Response response = client.newCall(request).execute();
            //RemoteRefresh(token, title, body, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    /*private void RemoteRefresh(String token, String title, String body, String value) {
        OkHttpClient client = new OkHttpClient();
        MediaType mediaType = MediaType.parse("application/json");

        // Construct the JSON payload for the data message
        JSONObject jsonData = new JSONObject();
        try {
            jsonData.put("to", token); // Add the token to send the message to a specific device
            jsonData.put("data", new JSONObject()); // Empty "data" object to send a data-only message
            jsonData.getJSONObject("data").put("title", title);
            jsonData.getJSONObject("data").put("body", body);
            jsonData.getJSONObject("data").put("value", value);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Construct the request
        RequestBody bodyReq = RequestBody.create(mediaType, jsonData.toString());
        Request request = new Request.Builder()
                .url(FCM_API)
                .post(bodyReq)
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Bearer " + SERVER_KEY)
                .build();

        try {
            if (android.os.Build.VERSION.SDK_INT > 9) {
                StrictMode.ThreadPolicy gfgPolicy =
                        new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(gfgPolicy);
            }
            Response response = client.newCall(request).execute();
            // Handle the response if needed
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/
}