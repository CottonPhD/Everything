package com.ptc.ptcnet.everything;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.thingworx.common.RESTAPIConstants;
import com.thingworx.communications.client.AndroidConnectedThingClient;
import com.thingworx.communications.client.ClientConfigurator;
import com.thingworx.communications.client.things.VirtualThing;

/**
 * Created by pdahmen on 01.02.2016.
 */
public class ThingworxActivity extends AppCompatActivity {

    private final String TAG = ThingworxActivity.class.getName();
    private ProgressDialog connectionProgressDialog;
    private Thread scanningThread;
    private long pollingRate;
    private boolean lastConnectionState = false;
    protected ConnectionStateObserver connectionStateObserver;

    protected AndroidConnectedThingClient client;
    protected String uri ;
    protected String appKey;
    protected SharedPreferences sharedPrefs;
    protected enum ConnectionState {DISCONNECTED,CONNECTING,CONNECTED};

    public ConnectionState getConnectionState() {
        return connectionState;
    }

    protected ConnectionState connectionState = ConnectionState.DISCONNECTED;
    /**
     * This task attempts to establish a connection and then calls either onConnectionFailed() or
     * onConnectionEstablished() based on the outcome.
     */
    protected final Runnable connectionEstablishedMonitorTask = new Runnable() {

        @Override
        public void run() {
            try {

                showProgressDialog();

                // Bind your thing to your connection and start it
                client.start();

                int counter = 0;
                boolean isConnected;
                while (counter < 10) {
                    Log.d(TAG, "Waiting for initial connection...");

                    try {
                        isConnected = client.getEndpoint().isConnected();
                        if(isConnected){
                            onConnectionEstablished();
                            return;
                        }
                    } catch (Exception e) {}
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {}

                    counter++;
                }
                onConnectionFailed("Timeout exceeded.");

            } catch (Exception e) {
                Log.e(TAG, "Failed to connect.",e);
                onConnectionFailed(e.getLocalizedMessage());
            }
        }
    };

    protected void startProcessScanRequestThread(long pollingRate,ConnectionStateObserver connectionStateObserver) {
        this.connectionStateObserver=connectionStateObserver;
        this.pollingRate = pollingRate;
        scanningThread = new Thread(updateProcessScanRequestTask);
        scanningThread.start();
    }


    protected boolean hasConnectionPreferences() {
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        uri = sharedPrefs.getString(getString(R.string.pref_uri_key), "");
        appKey = sharedPrefs.getString(getString(R.string.pref_appKey_key), "");
        return !(uri.equals("")||appKey.equals(""));
    }

    private AndroidConnectedThingClient buildClientFromSettings() throws Exception {
        // Determine if we have settings
        if(!hasConnectionPreferences())
            return null;

        ClientConfigurator config = new ClientConfigurator();
        System.out.println(uri  + " URI is here");
        config.setUri(uri);

        /* ReconnectInterval is the max time in seconds waited after a connection is
          dropped before a reconnect attempt is made to the ThingWorx server */
        config.setReconnectInterval(15);
        System.out.println(appKey + "AppKey hier");
        config.getSecurityClaims().addClaim(RESTAPIConstants.PARAM_APPKEY, appKey);

        /* ignoreSSLErrors - Accept self signed certs if using wss protocol */
        config.ignoreSSLErrors(true);

        /* How long in milliseconds to wait before giving up establishing a connection. */
        config.setConnectTimeout(10000);
        return new AndroidConnectedThingClient(config);
    }

    protected void connect(VirtualThing[] things) throws Exception {

        if(hasConnectionPreferences()){
            connectionState = ConnectionState.CONNECTING;

            client = buildClientFromSettings();
            for(VirtualThing thing:things) {
                thing.setClient(client); // If you create a thing before you create a client, the client must be set before binding
                client.bindThing(thing); // You can bind before or after a connection is established
            }
            Thread connectionThread = new Thread(this.connectionEstablishedMonitorTask,"connectionEstablishedMonitor");
            connectionThread.start();
        } else {
            disconnect();
        }
    }

    protected void disconnect() {
        try {
            connectionState = ConnectionState.DISCONNECTED;
            client.disconnect();
        } catch (Exception e) {
            Log.i(TAG,"Disconnecting.");
        }
    }

    /**
     * Called when a connection to the server is broken or fails to be created.
     */
    protected void onConnectionFailed(String localizedMessage) {
        connectionState = ConnectionState.DISCONNECTED;
        dismissProgressDialog();
        displayErrorDialog(localizedMessage);
    }

    private void displayErrorDialog(final String localizedMessage) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new AlertDialog.Builder(ThingworxActivity.this)
                        .setTitle("Error")
                        .setMessage(localizedMessage)
                        .setCancelable(false)
                        .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        }).create().show();
            }
        });

    }

    /**
     * Called when a connection is established by the client.
     */
    protected void onConnectionEstablished() {
        connectionState = ConnectionState.CONNECTED;
        connectionProgressDialog.dismiss();
    }

    /**
     * Shows a dialog indicating that a connection is in progress.
     */
    private void showProgressDialog() {
        if (connectionProgressDialog == null) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    connectionProgressDialog = ProgressDialog.show(ThingworxActivity.this, "Please wait ...", "Connecting to server ...", true);
                    connectionProgressDialog.setCancelable(false);
                }
            });
        }

    }

    /**
     * Dismisses the connection in progress dialog.
     */
    protected void dismissProgressDialog(){
        if(connectionProgressDialog!=null) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(connectionProgressDialog!=null) {
                        connectionProgressDialog.dismiss();
                        connectionProgressDialog = null;
                    }
                }
            });
        }
    }

    /**
     * Called when the application is closed.
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        dismissProgressDialog();
        Log.d(TAG, "Destroy");
        try {
            client.shutdown();
        } catch (Exception e) {
        }
    }

    /**
     * This thread is responsible for periodically calling update scan request to generate new
     * simulated data from our thing. It also updates the UI with the connection status.
     */
    private Runnable updateProcessScanRequestTask = new Runnable() {

        @Override
        public void run() {
            boolean isConnected;
            try {
                while (client==null || !client.isShutdown()) {
                    if (client !=null && client.isConnected()) {
                        isConnected = true;
                        for (VirtualThing thing : client.getThings().values()) {
                            try {
                                Log.v(TAG, "Scanning device");
                                thing.processScanRequest();
                            } catch (Exception eProcessing) {
                                Log.e(TAG, "Error Processing Scan Request for [" + thing.getName() + "] : " + eProcessing.getMessage());
                            }
                        }
                    } else {
                        isConnected = false;
                    }

                    if(isConnected!=lastConnectionState){
                        if(connectionStateObserver!=null) {
                            connectionStateObserver.onConnectionStateChanged(isConnected);
                            lastConnectionState = isConnected;
                        }
                    }

                    Thread.sleep(pollingRate);
                }
            } catch (InterruptedException e) {
                Log.e(TAG, "Polling thread exiting." + e.getMessage());
            }
        }
    };

    protected interface ConnectionStateObserver {
        void onConnectionStateChanged(boolean connected);
    }

}
