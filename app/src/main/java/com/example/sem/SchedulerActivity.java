package com.example.sem;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.cronutils.builder.CronBuilder;
import com.cronutils.model.Cron;
import com.cronutils.model.definition.CronDefinitionBuilder;
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

import static com.cronutils.model.CronType.UNIX;
import static com.cronutils.model.field.expression.FieldExpressionFactory.*;



public class SchedulerActivity extends AppCompatActivity {

    ToggleButton tb_sun, tb_mon, tb_tue, tb_wed, tb_thu, tb_fri, tb_sat;
    Button add_espresso, add_cap, add_latte;
    TimePicker time;
    int i_sun, i_mon, i_tue, i_wed, i_thu, i_fri, i_sat, min, hour;
    public static String mRPiAddress;
    public static String cronText;
    public static String cronURL;
    public static String drinkType = "ESPRESSO";
    public static CharSequence text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scheduler);
        Intent intent = getIntent();
        mRPiAddress = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
        time = findViewById(R.id.timePicker);
        time.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker timePicker, int i, int i1) {
                min = i1;
                hour = i;
            }
        });
        tb_sun = findViewById(R.id.Sunday);
        tb_sun.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // The toggle is enabled
                    i_sun = 1;
                } else {
                    // The toggle is disabled
                    i_sun = 0;
                }
            }
        });
        tb_mon = findViewById(R.id.Monday);
        tb_mon.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // The toggle is enabled
                    i_mon = 1;
                } else {
                    // The toggle is disabled
                    i_mon = 0;
                }
            }
        });
        tb_tue = findViewById(R.id.Tuesday);
        tb_tue.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // The toggle is enabled
                    i_tue = 1;
                } else {
                    // The toggle is disabled
                    i_tue = 0;
                }
            }
        });
        tb_wed = findViewById(R.id.Wednesday);
        tb_wed.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // The toggle is enabled
                    i_wed = 1;
                } else {
                    // The toggle is disabled
                    i_wed = 0;
                }
            }
        });
        tb_thu = findViewById(R.id.Thursday);
        tb_thu.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // The toggle is enabled
                    i_thu = 1;
                } else {
                    // The toggle is disabled
                    i_thu = 0;
                }
            }
        });
        tb_fri = findViewById(R.id.Friday);
        tb_fri.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // The toggle is enabled
                    i_fri = 1;
                } else {
                    // The toggle is disabled
                    i_fri = 0;
                }
            }
        });
        tb_sat = findViewById(R.id.Saturday);
        tb_sat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // The toggle is enabled
                    i_sat = 1;
                } else {
                    // The toggle is disabled
                    i_sat = 0;
                }
            }
        });
        add_espresso = findViewById(R.id.espressoButton);
        add_espresso.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Code here executes on main thread after user presses button
                drinkType = "ESPRESSO";
                execute_schedule(i_sun, i_mon, i_tue, i_wed, i_thu, i_fri, i_sat);
            }
        });
        add_cap = findViewById(R.id.capButton);
        add_cap.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Code here executes on main thread after user presses button
                drinkType = "CAPPUCCINO";
                execute_schedule(i_sun, i_mon, i_tue, i_wed, i_thu, i_fri, i_sat);
            }
        });
        add_latte = findViewById(R.id.latteButton);
        add_latte.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Code here executes on main thread after user presses button
                drinkType = "LATTE";
                execute_schedule(i_sun, i_mon, i_tue, i_wed, i_thu, i_fri, i_sat);
            }
        });
    }

    private void execute_schedule(int i_sun, int i_mon, int i_tue, int i_wed, int i_thu, int i_fri, int i_sat){
        if(i_sun == 1){
            Cron unixBuiltCronExpression = buildCronSun(min, hour);
            cronText = unixBuiltCronExpression.asString();
            cronURL = cron_url(cronText);
            execute_helper();
        }
        if(i_mon == 1){
            Cron unixBuiltCronExpression = buildCronMon(min, hour);
            cronText = unixBuiltCronExpression.asString();
            cronURL = cron_url(cronText);
            execute_helper();
        }
        if(i_tue == 1){
            Cron unixBuiltCronExpression = buildCronTue(min, hour);
            cronText = unixBuiltCronExpression.asString();
            cronURL = cron_url(cronText);
            execute_helper();
        }
        if(i_wed == 1){
            Cron unixBuiltCronExpression = buildCronWed(min, hour);
            cronText = unixBuiltCronExpression.asString();
            cronURL = cron_url(cronText);
            execute_helper();
        }
        if(i_thu == 1){
            Cron unixBuiltCronExpression = buildCronThu(min, hour);
            cronText = unixBuiltCronExpression.asString();
            cronURL = cron_url(cronText);
            execute_helper();
        }
        if(i_fri == 1){
            Cron unixBuiltCronExpression = buildCronFri(min, hour);
            cronText = unixBuiltCronExpression.asString();
            cronURL = cron_url(cronText);
            execute_helper();
        }
        if(i_sat == 1){
            Cron unixBuiltCronExpression = buildCronSat(min, hour);
            cronText = unixBuiltCronExpression.asString();
            cronURL = cron_url(cronText);
            execute_helper();
        }
    }

    private void execute_helper(){
        new schedule().execute();
        try {
            //set time in mili
            Thread.sleep(1000);

        }catch (Exception e){
            e.printStackTrace();
        }
        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    private static String cron_url(String s){
        StringBuilder sB = new StringBuilder();
        for(int i = 0; i < s.length(); i++){
            char c = s.charAt(i);
            if(c == ' '){
                sB.append("%20");
            }
            else if(c == '*'){
                sB.append("%2A");
            }
            else{
                sB.append(c);
            }
        }
        String cron = sB.toString();
        return cron;
    }

    private class schedule extends AsyncTask<Void, Void, Void> {

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
            params.put("coffee_type", drinkType);
            params.put("cron_text", cronText);
            params.put("enabled", 1);
            params.put("id", 0);
            params.put("rpc_call", "add_schedule");
            long epoch = System.currentTimeMillis()/1000;
            params.put("ts", epoch);
            String str = String.format("coffee_type=%s&cron_text=%s&enabled=1&id=0&rpc_call=add_schedule" +
                    "&ts=%d87677fc06b0afc08cb86e008183390e5", drinkType, cronURL, epoch);
            String sign = new String(Hex.encodeHex(DigestUtils.sha256(str)));
            params.put("sign", sign);
            String id = "03";
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
                    text = response.getResult().toString();
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

    private static Cron buildCronSun(int min, int hour){
        //Create a cron expression. CronMigrator will ensure you remain cron provider agnostic
        return CronBuilder.cron(CronDefinitionBuilder.instanceDefinitionFor(UNIX))
                .withDoM(always())
                .withMonth(always())
                .withDoW(on(0))
                .withHour(on(hour))
                .withMinute(on(min))
                .instance();
    }

    private static Cron buildCronMon(int min, int hour){
        //Create a cron expression. CronMigrator will ensure you remain cron provider agnostic
        return CronBuilder.cron(CronDefinitionBuilder.instanceDefinitionFor(UNIX))
                .withDoM(always())
                .withMonth(always())
                .withDoW(on(1))
                .withHour(on(hour))
                .withMinute(on(min))
                .instance();
    }

    private static Cron buildCronTue(int min, int hour){
        //Create a cron expression. CronMigrator will ensure you remain cron provider agnostic
        return CronBuilder.cron(CronDefinitionBuilder.instanceDefinitionFor(UNIX))
                .withDoM(always())
                .withMonth(always())
                .withDoW(on(2))
                .withHour(on(hour))
                .withMinute(on(min))
                .instance();
    }

    private static Cron buildCronWed(int min, int hour){
        //Create a cron expression. CronMigrator will ensure you remain cron provider agnostic
        return CronBuilder.cron(CronDefinitionBuilder.instanceDefinitionFor(UNIX))
                .withDoM(always())
                .withMonth(always())
                .withDoW(on(3))
                .withHour(on(hour))
                .withMinute(on(min))
                .instance();
    }

    private static Cron buildCronThu(int min, int hour){
        //Create a cron expression. CronMigrator will ensure you remain cron provider agnostic
        return CronBuilder.cron(CronDefinitionBuilder.instanceDefinitionFor(UNIX))
                .withDoM(always())
                .withMonth(always())
                .withDoW(on(4))
                .withHour(on(hour))
                .withMinute(on(min))
                .instance();
    }

    private static Cron buildCronFri(int min, int hour){
        //Create a cron expression. CronMigrator will ensure you remain cron provider agnostic
        return CronBuilder.cron(CronDefinitionBuilder.instanceDefinitionFor(UNIX))
                .withDoM(always())
                .withMonth(always())
                .withDoW(on(5))
                .withHour(on(hour))
                .withMinute(on(min))
                .instance();
    }

    private static Cron buildCronSat(int min, int hour){
        //Create a cron expression. CronMigrator will ensure you remain cron provider agnostic
        return CronBuilder.cron(CronDefinitionBuilder.instanceDefinitionFor(UNIX))
                .withDoM(always())
                .withMonth(always())
                .withDoW(on(6))
                .withHour(on(hour))
                .withMinute(on(min))
                .instance();
    }
}
