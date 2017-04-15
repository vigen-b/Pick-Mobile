package com.spiderslab.pick.pickmobile.notification;

import android.location.Location;
import android.os.Handler;

import com.spiderslab.pick.pickmobile.Order;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;

public class WorkerThread extends Thread {
    private static final String TAG = WorkerThread.class.getSimpleName();

    private Handler responseHandler;
    private Callback mCallback;


    public WorkerThread(Handler responseHandler, Callback callback) {
        super(TAG);
        this.responseHandler = responseHandler;
        mCallback = callback;
    }

    public void run() {
        if (null == mCallback) {
            return;
        }
        getOrders();
        sendGpsCoordinates();
    }

    private void getOrders() {
        String urlString = "http://localhost/order";
        InputStream in = null;
        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            in = new BufferedInputStream(connection.getInputStream());
            JSONObject orders = readStream(in);
            JSONObject newOrderJson = orders.getJSONObject("new_order");
            JSONObject waitingOrderJson = orders.getJSONObject("waiting_order");
            final Order newOrder = new Order(newOrderJson);
            final Order waitingOrder = new Order(waitingOrderJson);
            responseHandler.post(new Runnable() {
                @Override
                public void run() {
                    mCallback.onOrdersAvailable(newOrder, waitingOrder);
                }
            });
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != in) {
                    in.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void sendGpsCoordinates() {
        Location location = LocationManager.INSTANCE.getLocation();
        try {
            String urlParameters  = "latitude=" + location.getLatitude()+ "&longitude=" + location.getLongitude();
            byte[] postData       = urlParameters.getBytes(Charset.forName("UTF-8"));
            int    postDataLength = postData.length;
            String request        = "http://localhost/gps";
            URL    url            = new URL( request );
            HttpURLConnection conn= (HttpURLConnection) url.openConnection();
            conn.setDoOutput( true );
            conn.setInstanceFollowRedirects( false );
            conn.setRequestMethod( "POST" );
            conn.setRequestProperty( "Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestProperty( "charset", "utf-8");
            conn.setRequestProperty( "Content-Length", Integer.toString( postDataLength ));
            conn.setUseCaches( false );
            DataOutputStream wr = new DataOutputStream( conn.getOutputStream());
            wr.write( postData );
            conn.connect();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private JSONObject readStream(InputStream in) throws JSONException, IOException {
        BufferedReader streamReader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
        StringBuilder responseStrBuilder = new StringBuilder();
        String inputStr;
        while ((inputStr = streamReader.readLine()) != null) {
            responseStrBuilder.append(inputStr);
        }
        return new JSONObject(responseStrBuilder.toString());
    }

    interface Callback {
        void onOrdersAvailable(Order newOrder, Order availableOrder);
    }
}
