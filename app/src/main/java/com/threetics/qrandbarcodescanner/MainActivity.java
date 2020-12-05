package com.threetics.qrandbarcodescanner;

import androidx.appcompat.app.AppCompatActivity;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;

import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.Toast;
import android.widget.ToggleButton;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.client.RedisClient;

public class MainActivity extends AppCompatActivity {

    private Button scan;
    public String responseMessage = new String();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        scan = findViewById(R.id.scan);

        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ScanActivity.class);
                startActivity(intent);
            }
        });
        new RedisHelper().execute();



//        Thread thread = new Thread(new Runnable() {
//
//            @Override
//            public void run() {
//                try  {
//                    Jedis jedis = new Jedis("ec2-3-120-208-239.eu-central-1.compute.amazonaws.com");
//                    jedis.connect();
//                    String value = jedis.get("foo");
//                    JedisPubSub jedisPubSub = new JedisPubSub() {
//
//                        @Override
//                        public void onMessage(String channel, String message) {
//                            // TODO Auto-generated method stub
//
//                            runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//
//                                }
//                            });
//                        }
//
//                        @Override
//                        public void onSubscribe(String channel, int subscribedChannels) {
//                            // TODO Auto-generated method stub
//                            System.out.println("Client is Subscribed to channel : "+ channel);
//                        }
//
//                        @Override
//                        public void onUnsubscribe(String channel, int subscribedChannels) {
//                            // TODO Auto-generated method stub
//                            super.onUnsubscribe(channel, subscribedChannels);
//                        }
//
//                    };
//                    jedis.subscribe(jedisPubSub,"hello");
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        });
//
//        thread.start();
    }

    public class RedisHelper extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Void... values) {
//            System.out.println("In post execute : " + responseMessage);
////            Toast.makeText(getApplicationContext(), "channel says : " +responseMessage, Toast.LENGTH_LONG).show();
//            scan.setText(responseMessage);
        }

        @Override
        protected void onPostExecute(String s) {
            System.out.println("In post execute : " + s);

            String arr[] = responseMessage.split("::");
            switch (arr[0]) {
                case "switch":
                    Switch sw = findViewById(MainActivity.this.getResources().getIdentifier(arr[1], "id", getPackageName()));
                    sw.setChecked(Boolean.parseBoolean(arr[2]));
                    break;
                case "toggle":
                    ToggleButton tb = findViewById(MainActivity.this.getResources().getIdentifier(arr[1], "id", getPackageName()));
                    tb.setChecked(Boolean.parseBoolean(arr[2]));
                    break;
                case "button":
                    Button button = findViewById(MainActivity.this.getResources().getIdentifier(arr[1], "id", getPackageName()));
                    button.callOnClick();
                    break;


            }

            new RedisHelper().execute();
        }

        @Override
        protected String doInBackground(Void... strings) {
            try  {
                    Jedis jedis = new Jedis("ec2-18-196-101-166.eu-central-1.compute.amazonaws.com");
                    jedis.connect();
                    JedisPubSub jedisPubSub = new JedisPubSub() {

                        @Override
                        public void onMessage(String channel, String message) {
                            // TODO Auto-generated method stub
                            responseMessage = message;
                            onPostExecute(responseMessage);
                        }

                        @Override
                        public void onSubscribe(String channel, int subscribedChannels) {
                            // TODO Auto-generated method stub
                            System.out.println("Client is Subscribed to channel : "+ channel);
                        }

                        @Override
                        public void onUnsubscribe(String channel, int subscribedChannels) {
                            // TODO Auto-generated method stub
                            super.onUnsubscribe(channel, subscribedChannels);
                        }

                    };
                    jedis.subscribe(jedisPubSub,"hello");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            return null;
        }
    }
}