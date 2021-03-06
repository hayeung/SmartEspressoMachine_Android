package com.example.sem;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
// The Client sessions package
import com.thetransactioncompany.jsonrpc2.client.*;

// The Base package for representing JSON-RPC 2.0 messages
import com.thetransactioncompany.jsonrpc2.*;

// The JSON Smart package for JSON encoding/decoding (optional)

// For creating URLs
import java.net.*;
import java.util.HashMap;
import java.util.Map;
import java.lang.Object;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.codec.binary.Hex;
import android.content.Context;
import android.net.nsd.NsdServiceInfo;
import android.net.nsd.NsdManager;
import android.util.Log;


public class MainActivity extends AppCompatActivity {
    public static final String EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE";
    private NsdManager mNsdManager;
    private NsdManager.DiscoveryListener mDiscoveryListener;
    private NsdManager.ResolveListener mResolveListener;
    private NsdServiceInfo mServiceInfo;
    public static String mRPiAddress;
    public static String coffee_status = "";
    public static String coffee_type = "ESPRESSO";
    private static final String SERVICE_TYPE = "_sem._tcp.";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRPiAddress = "";
        mNsdManager = (NsdManager)(getApplicationContext().getSystemService(Context.NSD_SERVICE));
        initializeResolveListener();
        initializeDiscoveryListener();
        mNsdManager.discoverServices(SERVICE_TYPE, NsdManager.PROTOCOL_DNS_SD, mDiscoveryListener);
        try {
            //set time in mili
            Thread.sleep(1000);

        }catch (Exception e){
            e.printStackTrace();
        }
        if(mRPiAddress == ""){
            server_issue();
        }
    }

    private void initializeDiscoveryListener() {
        // Instantiate a new DiscoveryListener
        mDiscoveryListener = new NsdManager.DiscoveryListener() {

            //  Called as soon as service discovery begins.
            @Override
            public void onDiscoveryStarted(String regType) {
            }

            @Override
            public void onServiceFound(NsdServiceInfo service) {
                // A service was found!  Do something with it.
                String name = service.getServiceName();
                String type = service.getServiceType();
                Log.d("NSD", "Service Name=" + name);
                Log.d("NSD", "Service Type=" + type);
                if (type.equals(SERVICE_TYPE)) {
                    Log.d("NSD", "Service Found @ '" + name + "'");
                    mNsdManager.resolveService(service, mResolveListener);
                }
            }

            @Override
            public void onServiceLost(NsdServiceInfo service) {
                // When the network service is no longer available.
                // Internal bookkeeping code goes here.
            }

            @Override
            public void onDiscoveryStopped(String serviceType) {
            }

            @Override
            public void onStartDiscoveryFailed(String serviceType, int errorCode) {
                mNsdManager.stopServiceDiscovery(this);
                server_issue();
            }

            @Override
            public void onStopDiscoveryFailed(String serviceType, int errorCode) {
                mNsdManager.stopServiceDiscovery(this);
            }
        };
    }

    private void initializeResolveListener() {
        mResolveListener = new NsdManager.ResolveListener() {

            @Override
            public void onResolveFailed(NsdServiceInfo serviceInfo, int errorCode) {
                // Called when the resolve fails.  Use the error code to debug.
                Log.e("NSD", "Resolve failed" + errorCode);
            }

            @Override
            public void onServiceResolved(NsdServiceInfo serviceInfo) {
                mServiceInfo = serviceInfo;
                // Port is being returned as 9. Not needed.
                //int port = mServiceInfo.getPort();
                InetAddress host = mServiceInfo.getHost();
                String address = host.getHostAddress();
                Log.d("NSD", "Resolved address = " + address);
                mRPiAddress = address;
            }
        };
    }

    private void server_issue(){
        Intent intent = new Intent (this, IssueActivity.class);
        startActivity(intent);
        finish();
    }

    public void make_espresso(View view){
        coffee_type = "ESPRESSO";
        new make_coffee().execute();
        make_helper();
    }

    public void make_cap(View view){
        coffee_type = "CAPPUCCINO";
        new make_coffee().execute();
        make_helper();
    }

    public void make_latte(View view){
        coffee_type = "LATTE";
        new make_coffee().execute();
        make_helper();
    }

    public void make_helper(){
        try {
            //set time in mili
            Thread.sleep(1000);

        }catch (Exception e){
            e.printStackTrace();
        }
        Intent intent = new Intent (this, MakeEspressoActivity.class);
        String message = coffee_status;
        intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
    }

    public void schedule_list(View view){
        Intent intent = new Intent(this, ScheduleListActivity.class);
        intent.putExtra(EXTRA_MESSAGE, mRPiAddress);
        startActivity(intent);
    }

    public void scheduler_menu(View view){
        Intent intent = new Intent(this, SchedulerActivity.class);
        intent.putExtra(EXTRA_MESSAGE, mRPiAddress);
        startActivity(intent);
    }

    public void settings(View view){
        Intent intent = new Intent(this, SettingsActivity.class);
        intent.putExtra(EXTRA_MESSAGE, mRPiAddress);
        startActivity(intent);
    }

    private static class json_client_ping_test extends AsyncTask<Void, Void, Void>{

        @Override
        protected Void doInBackground(Void... voids) {
            // The JSON-RPC 2.0 server URL
            URL serverURL = null;
            try {
                while(mRPiAddress.isEmpty()){
                    assert true;
                }
                String url = String.format("http://%s:9999/jsonrpc", mRPiAddress);
                serverURL = new URL(url);
            } catch (MalformedURLException e) {
                // handle exception...
            }
            // Create new JSON-RPC 2.0 client session
            JSONRPC2Session mySession = new JSONRPC2Session(serverURL);
            // Construct new request
            String method = "sem_do";
            Map<String,Object> params = new HashMap<String,Object>();
            params.put("rpc_call", "ping");
            long epoch = System.currentTimeMillis()/1000;
            params.put("ts", epoch);
            String str = String.format("rpc_call=ping&ts=%d87677fc06b0afc08cb86e008183390e5", epoch);
            String sign = new String(Hex.encodeHex(DigestUtils.sha256(str)));
            params.put("sign", sign);
            String id = "01";
            JSONRPC2Request request = new JSONRPC2Request(method, params, id);
            JSONRPC2Response response = null;
            try {
                response = mySession.send(request);
            } catch (JSONRPC2SessionException e) {
            /*System.err.println(e.getMessage());*/
                // handle exception...
            }
            if(response != null) {
                if (response.indicatesSuccess())
                    System.out.println(response.getResult());
                else
                    System.out.println(response.getError().getMessage());
            }
            else{
                System.out.println("ERROR");
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }

    private static class json_client_pingparam_test extends AsyncTask<Void, Void, Void>{

        @Override
        protected Void doInBackground(Void... voids) {
            // The JSON-RPC 2.0 server URL
            URL serverURL = null;
            try {
                while(mRPiAddress.isEmpty()){
                    assert true;
                }
                String url = String.format("http://%s:9999/jsonrpc", mRPiAddress);
                serverURL = new URL(url);
            } catch (MalformedURLException e) {
                // handle exception...
            }
            // Create new JSON-RPC 2.0 client session
            JSONRPC2Session mySession = new JSONRPC2Session(serverURL);
            // Construct new request
            String method = "sem_do";
            Map<String,Object> params = new HashMap<String,Object>();
            params.put("param1", "SEM");
            params.put("param2", "Rocks .");
            params.put("rpc_call", "ping_param");
            long epoch = System.currentTimeMillis()/1000;
            params.put("ts", epoch);
            String str = String.format("param1=SEM&param2=Rocks%%20.&rpc_call=ping_param" +
                    "&ts=%d87677fc06b0afc08cb86e008183390e5", epoch);
            String sign = new String(Hex.encodeHex(DigestUtils.sha256(str)));
            params.put("sign", sign);
            String id = "02";
            JSONRPC2Request request = new JSONRPC2Request(method, params, id);
            JSONRPC2Response response = null;
            try {
                response = mySession.send(request);
            } catch (JSONRPC2SessionException e) {
            /*System.err.println(e.getMessage());*/
                // handle exception...
            }
            if(response != null) {
                if (response.indicatesSuccess())
                    System.out.println(response.getResult());
                else
                    System.out.println(response.getError().getMessage());
            }
            else{
                System.out.println("ERROR");
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }

    private static class make_coffee extends AsyncTask<Void, Void, Void>{

        @Override
        protected Void doInBackground(Void... voids) {
            // The JSON-RPC 2.0 server URL
            URL serverURL = null;
            try {
                while(mRPiAddress.isEmpty()){
                    assert true;
                }
                String url = String.format("http://%s:9999/jsonrpc", mRPiAddress);
                serverURL = new URL(url);
            } catch (MalformedURLException e) {
                // handle exception...
            }
            // Create new JSON-RPC 2.0 client session
            JSONRPC2Session mySession = new JSONRPC2Session(serverURL);
            // Construct new request
            String method = "sem_do";
            Map<String,Object> params = new HashMap<String,Object>();
            params.put("coffee_type", coffee_type);
            params.put("rpc_call", "make_coffee_now");
            long epoch = System.currentTimeMillis()/1000;
            params.put("ts", epoch);
            String str = String.format("coffee_type=%s&rpc_call=make_coffee_now&ts=" +
                    "%d87677fc06b0afc08cb86e008183390e5", coffee_type, epoch);
            String sign = new String(Hex.encodeHex(DigestUtils.sha256(str)));
            params.put("sign", sign);
            String id = "09";
            JSONRPC2Request request = new JSONRPC2Request(method, params, id);
            JSONRPC2Response response = null;
            try {
                response = mySession.send(request);
            } catch (JSONRPC2SessionException e) {
            /*System.err.println(e.getMessage());*/
                // handle exception...
            }
            if(response != null) {
                if (response.indicatesSuccess()) {
                    System.out.println(response.getResult());
                    coffee_status = response.getResult().toString();
                }
                else
                    System.out.println(response.getError().getMessage());
            }
            else{
                System.out.println("ERROR");
                coffee_status = "ERROR: Server Issue";
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }

    private static class sort_form extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            // The JSON-RPC 2.0 server URL
            URL serverURL = null;
            try {
                while(mRPiAddress.isEmpty()){
                    assert true;
                }
                String url = String.format("http://%s:9999/jsonrpc", mRPiAddress);
                serverURL = new URL(url);
            } catch (MalformedURLException e) {
                // handle exception...
            }
            // Create new JSON-RPC 2.0 client session
            JSONRPC2Session mySession = new JSONRPC2Session(serverURL);
            // Construct new request
            String method = "param_sort_and_format";
            Map<String,Object> params = new HashMap<String,Object>();
            params.put("cron_text", "20 6 * * 2");
            params.put("enabled", 1);
            params.put("id", 0);
            params.put("rpc_call", "add_schedule");
            long epoch = System.currentTimeMillis()/1000;
            params.put("ts", epoch);
            String id = "99";
            JSONRPC2Request request = new JSONRPC2Request(method, params, id);
            JSONRPC2Response response = null;
            try {

                response = mySession.send(request);
            } catch (JSONRPC2SessionException e) {
            /*System.err.println(e.getMessage());*/
                // handle exception...
            }
            if(response != null) {
                if (response.indicatesSuccess())
                    System.out.println(response.getResult());
                else
                    System.out.println(response.getError().getMessage());
            }
            else{
                System.out.println("ERROR");
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }
}
