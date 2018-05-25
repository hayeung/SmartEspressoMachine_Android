package com.example.sem;

import android.annotation.SuppressLint;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.thetransactioncompany.jsonrpc2.JSONRPC2Request;
import com.thetransactioncompany.jsonrpc2.JSONRPC2Response;
import com.thetransactioncompany.jsonrpc2.client.JSONRPC2Session;
import com.thetransactioncompany.jsonrpc2.client.JSONRPC2SessionException;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



public class ScheduleListActivity extends AppCompatActivity {

    public static String mRPiAddress;
    public static String json = null;
    public static int alarm_num;

    @SuppressLint("ResourceType")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        mRPiAddress = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
        new get_schedule_list().execute();
        //setContentView(R.layout.activity_schedule_list);
        json = null;
        while(json == null){
            try {
                //set time in mili
                Thread.sleep(500);

            }catch (Exception e){
                e.printStackTrace();
            }
        }
        ScrollView sv = new ScrollView(this);
        LinearLayout ll = new LinearLayout(this);
        ll.setOrientation(LinearLayout.VERTICAL);
        sv.addView(ll);
        Pattern regex = Pattern.compile("\\{(.*?)\\}");
        Matcher regexMatcher = regex.matcher(json);

        while (regexMatcher.find()) {//Finds Matching Pattern in String
            String single_json = String.format("{%s}", regexMatcher.group(1));
            System.out.println(single_json);
            Json_data data = new Gson().fromJson(single_json, Json_data.class);
            System.out.println(data.cron);
            String cron_desc = cronParser(data.cron);
            System.out.println(cron_desc);
            TextView tv = new TextView(this);
            tv.setTextAppearance(this, android.R.style.TextAppearance_Large);
            tv.setText(String.format("%s. %s, %s", data.id, cron_desc, data.coffee_type));
            ll.addView(tv);
        }
        EditText et = new EditText(this);
        et.setHint("Alarm #");
        et.setId(1);
        ll.addView(et);
        Button b = new Button(this);
        b.setText("Delete");
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText et_2 = (EditText) findViewById(1);
                alarm_num = Integer.parseInt(et_2.getText().toString());
                new delete_schedule().execute();
                try {
                    //set time in mili
                    Thread.sleep(500);

                }catch (Exception e){
                    e.printStackTrace();
                }
                finish();
                startActivity(getIntent());
            }
        });
        ll.addView(b);
        this.setContentView(sv);
    }

    class Json_data{
        private int id;
        private int enabled;
        private String cron;
        private String coffee_type;

        public int getId(){return id;}
        public int getEnabled(){return enabled;}
        public String getCron(){return cron;}
        public String getCoffee(){return coffee_type;}

        public void setID(){this.id = id;}
        public void setEnabled(){this.enabled = enabled;}
        public void setCron(){this.cron = cron;}
        public void setCoffee(){this.coffee_type = coffee_type;}
    }

    public String cronParser(String s){
        String[] parts = s.split(" ");
        int min = Integer.parseInt(parts[0]);
        int hour = Integer.parseInt(parts[1]);
        int day = Integer.parseInt(parts[4]);
        String min_str;
        String day_str = null;
        String time = "AM";

        if(hour == 0){
            hour = 12;
        }
        else if(hour == 12){
            time = "PM";
        }
        else if(hour > 12){
            time = "PM";
            hour -= 12;
        }
        if(min < 10){
            min_str = String.format("0%s", parts[0]);
        }
        else{
            min_str = parts[0];
        }
        switch(day){
            case 0: day_str = "Sunday";
                    break;
            case 1: day_str = "Monday";
                    break;
            case 2: day_str = "Tuesday";
                    break;
            case 3: day_str = "Wednesday";
                    break;
            case 4: day_str = "Thursday";
                    break;
            case 5: day_str = "Friday";
                    break;
            case 6: day_str = "Saturday";
                    break;
        }
        String desc = String.format("%s, %s:%s %s", day_str, Integer.toString(hour), min_str, time);
        return desc;
    }

    private static class get_schedule_list extends AsyncTask<Void, Void, Void> {

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
            params.put("rpc_call", "get_schedule_list");
            long epoch = System.currentTimeMillis()/1000;
            params.put("ts", epoch);
            String str = String.format("rpc_call=get_schedule_list&ts=" +
                    "%d87677fc06b0afc08cb86e008183390e5", epoch);
            String sign = new String(Hex.encodeHex(DigestUtils.sha256(str)));
            params.put("sign", sign);
            String id = "06";
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
                    json = response.getResult().toString();
                    System.out.println(json);
                }
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

    private static class delete_schedule extends AsyncTask<Void, Void, Void> {

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
            params.put("id", alarm_num);
            params.put("rpc_call", "delete_schedule");
            long epoch = System.currentTimeMillis()/1000;
            params.put("ts", epoch);
            String str = String.format("id=%d&rpc_call=delete_schedule&ts=" +
                    "%d87677fc06b0afc08cb86e008183390e5", alarm_num, epoch);
            String sign = new String(Hex.encodeHex(DigestUtils.sha256(str)));
            params.put("sign", sign);
            String id = "04";
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
                }
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



