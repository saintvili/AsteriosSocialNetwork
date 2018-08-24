package ru.asterios.open.service;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import ru.asterios.open.app.App;
import ru.asterios.open.constants.Constants;


public class MyService extends Service implements Constants {

    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;
    int serverResponseCode = 0;
    String upLoadServerUri = null;

    private static final String TAG = "ServiceExample";

    @Override
    public void onCreate() {

        Log.e(TAG, "Service onCreate");

        sharedPref = getSharedPreferences("myfiles", MODE_PRIVATE);

        /************* Php script path ****************/
        upLoadServerUri = "http://myserver/uploadimage.php";

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.e(TAG, "Service onStartCommand " + startId);

        final int currentId = startId;

        Runnable r = new Runnable() {

            public void run() {

                synchronized (this) {

                    try {

                        uploadFile("photo.jpg");

                    } catch (Exception e) {

                    }

                }

                Log.e(TAG, "Service running " + currentId);

                stopSelf();
            }
        };

        Thread t = new Thread(r);
        t.start();
        return Service.START_STICKY;
    }

    @Override
    public IBinder onBind(Intent arg0) {
        // TODO Auto-generated method stub
        Log.e(TAG, "Service onBind");
        return null;
    }

    @Override
    public void onDestroy() {

        Log.e(TAG, "Service onDestroy");
    }

    public int uploadFile(String sourceFileUri) {

        String fileName = sourceFileUri;

        HttpURLConnection conn = null;
        DataOutputStream dos = null;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;
        // File sourceFile = new
        // File(Environment.getExternalStorageDirectory(),sourceFileUri);
        File sourceFile = new File(Environment.getExternalStorageDirectory() + "/network/photo.jpg");

        if (!sourceFile.isFile()) {

            return 0;

        } else {

            try {

                // open a URL connection to the Servlet
                FileInputStream fileInputStream = new FileInputStream(sourceFile);
                URL url = new URL(METHOD_PROFILE_UPLOADPHOTO);

                // Open a HTTP connection to the URL
                conn = (HttpURLConnection) url.openConnection();
                conn.setDoInput(true); // Allow Inputs
                conn.setDoOutput(true); // Allow Outputs
                conn.setUseCaches(false); // Don't use a Cached Copy
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Connection", "Keep-Alive");
                conn.setRequestProperty("ENCTYPE", "multipart/form-data");
                conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                conn.setRequestProperty("uploaded_file", fileName);
                conn.setRequestProperty("accountId", Long.toString(App.getInstance().getId()));
                conn.setRequestProperty("accessToken", App.getInstance().getAccessToken());

                dos = new DataOutputStream(conn.getOutputStream());

                dos.writeBytes(twoHyphens + boundary + lineEnd);
                // dos.writeBytes("Content-Disposition: form-data; name="uploaded_file";filename=""+ fileName + """
                // + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\"" + fileName + "\"" + lineEnd);
                dos.writeBytes(lineEnd);

                // create a buffer of maximum size
                bytesAvailable = fileInputStream.available();

                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                buffer = new byte[bufferSize];

                // read file and write it into form...
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                while (bytesRead > 0) {

                    dos.write(buffer, 0, bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                }

                // send multipart form data necesssary after file data...
                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                // Responses from the server (code and message)
                serverResponseCode = conn.getResponseCode();
                String serverResponseMessage = conn.getResponseMessage();

                String jsonReply;

                if (conn.getResponseCode() == 201 || conn.getResponseCode() == 200) {

                    InputStream response = conn.getInputStream();
                    jsonReply = convertStreamToString(response);

                    Log.e("asd", jsonReply);
                }

                Log.i("uploadFile", "HTTP Response is : " + serverResponseMessage + ": " + serverResponseCode);

                if (serverResponseCode == 200) {

                }

                // close the streams //
                fileInputStream.close();
                dos.flush();
                dos.close();

            } catch (MalformedURLException ex) {

                ex.printStackTrace();

                Log.e("Upload file to server", "error: " + ex.getMessage(), ex);

            } catch (Exception e) {

                e.printStackTrace();

                Log.e("Upload Exception", "Exception : " + e.getMessage(), e);
            }

            return serverResponseCode;

        } // End else block
    }

    private static String convertStreamToString(InputStream is) {

        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }
}