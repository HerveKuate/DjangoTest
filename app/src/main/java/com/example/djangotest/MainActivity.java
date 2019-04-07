package com.example.djangotest;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import android.content.Intent;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.os.StrictMode;
import android.os.Handler;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.lang.String;

import android.util.Base64;

import 	java.net.HttpURLConnection;

public class MainActivity extends AppCompatActivity  {
    private static EditText username;
    private static EditText password;
    private static TextView attempt;
    private static Button login_button;
    TextView tvResult;
    TextView textView_loginpage;
    String[] result = new String[3];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Login to Django API");

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        setContentView(R.layout.activity_main);
        textView_loginpage = (TextView) findViewById(R.id.textView_loginpage);
        checkNetworkConnection();
        LoginButton();
    }

    public void LoginButton(){
        username = (EditText)findViewById(R.id.editText_user);
        password = (EditText)findViewById(R.id.editText_password);
        attempt = (TextView)findViewById(R.id.textView_attempt);
        login_button = (Button)findViewById(R.id.Button_login);
        tvResult = (TextView) findViewById(R.id.textView_report);
        attempt.setText("");

        login_button.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try{
                            setContentView(R.layout.activity_main);
                            result = httpPost("http://10.53.14.36:8000/login/");
                            tvResult.setText(result[0]);

                            //Start activity User activity
                            if (Integer.valueOf(result[0]) == 200) {
                                Intent i = new Intent(MainActivity.this, UserActivity.class);
                                i.putExtra("JSONData", result[1]);
                                i.putExtra("Authorization", result[2]);
                                i.putExtra("OutputCode", result[0]);
                                startActivity(i);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }  catch (IOException e) {
                           Log.i ("errorr","Unable to retrieve web page. URL may be invalid.");
                        }



                }

        }
        );
    }


    // check network connection
    public boolean checkNetworkConnection() {
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        boolean isConnected = false;
        if (networkInfo != null && (isConnected = networkInfo.isConnected())) {
            // show "Connected" & type of network "WIFI or MOBILE"
            textView_loginpage.setText("Connected "+networkInfo.getTypeName());
            // change background color to red
            textView_loginpage.setBackgroundColor(0xFF7CCC26);


        } else {
            // show "Not Connected"
            textView_loginpage.setText("Not Connected");
            // change background color to green
            textView_loginpage.setBackgroundColor(0xFFFF0000);
        }

        return isConnected;
    }

    private String[] httpPost(String myUrl) throws IOException, JSONException {


        //String cyanuser = "Basic Y3lhbl9hbHZlaW46Q3lhbm5hZ2FzYWtpOTM="; //FOR TEST
        String userCredentials = username.getText().toString() + ":" + password.getText().toString();

        URL url = new URL(myUrl);

        //create HttpURLConnection
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();


        String basicAuth = "Basic " + Base64.encodeToString(userCredentials.getBytes(), Base64.DEFAULT);

        //basicAuth = cyanuser; //FOR TEST

        conn.setRequestProperty ("Authorization", basicAuth);
        conn.setRequestMethod("POST");
        // Set connection timeout and read timeout value.
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        conn.setRequestProperty("Content-Length", "");
        conn.setDoOutput(true);
        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(conn.getOutputStream());
        outputStreamWriter.write("");
        outputStreamWriter.flush();


        int responseCode = conn.getResponseCode();
        System.out.println("Response Code : " + responseCode);

        /***********************/
        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();  //authentification token

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        outputStreamWriter.close();

        System.out.println(response.toString());
        /***********************/
        //conn.connect();
        //String ConnResponse = conn.getResponseMessage()+"";
        //String line = bufferedReader.readLine();
        Log.d("TAG", "output stream: " + response.toString());

        //disconnect at the end
        conn.disconnect();
        Log.d("TAG", "base64: " + basicAuth);
        return new String[] {Integer.toString(responseCode), response.toString(), basicAuth}; //1= errorcode , 2=token

    }
}