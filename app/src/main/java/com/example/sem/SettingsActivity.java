package com.example.sem;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class SettingsActivity extends AppCompatActivity {
    public static String mRPiAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
    }

   /* class sensor_data{
        private int present;
        private int water_enough;
        private int water_level;

        public int getPresent(){return present;}
        public int water_enough;
        public int water_level;

        public int getId(){return id;}
        public int getEnabled(){return enabled;}
        public String getCron(){return cron;}
        public String getCoffee(){return coffee_type;}

        public void setID(){this.id = id;}
        public void setEnabled(){this.enabled = enabled;}
        public void setCron(){this.cron = cron;}
        public void setCoffee(){this.coffee_type = coffee_type;}
    }*/
}
