package com.example.djangotest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.util.Xml;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;

import java.io.IOException;
import 	java.nio.charset.Charset;

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
import org.json.JSONObject;
import java.io.OutputStream;
import android.util.Base64;

import 	java.net.HttpURLConnection;

public class UserActivity extends AppCompatActivity {
    EditText Firstname;
    EditText Lastname;
    Button ButtonUpdate;
    TextView ErrorLog;
    JSONObject User ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        setTitle("Update User data");



        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        Intent i = getIntent();
        Bundle extras = i.getExtras();
        final String JSONData = extras.getString("JSONData");
        final String basicAuth = extras.getString("Authorization");

        Firstname = (EditText)findViewById(R.id.editText_DjangoName);
        Lastname = (EditText)findViewById(R.id.editText_DjangoSurname);
        ButtonUpdate = (Button)findViewById(R.id.button_update);
        ErrorLog = (TextView) findViewById(R.id.textView_ErrorLog);

        checkNetworkConnection();



        try{
            User = new JSONObject(JSONData);
            Firstname.setText(User.getString("firstname"));
            Lastname.setText(User.getString("lastname"));
        } catch (JSONException e){
            e.printStackTrace();
        }

        Log.d("TAG", "Jsondata: " + JSONData);



        ButtonUpdate.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            String[] CurrentUser = Update("http://10.53.14.36:8000/login/refresh/", JSONData, basicAuth);

                            JSONObject jsonObject = new JSONObject(CurrentUser[1]);
                            Firstname.setText(jsonObject.getString("firstname"));
                            Lastname.setText(jsonObject.getString("lastname"));

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }  catch (IOException e) {
                            Log.i ("errorr","Unable to retrieve web page. URL may be invalid.");

                        }



                    }

                }
        );
    }

    private String[] Update(String myUrl, String Jsondata, String Authentification) throws IOException, JSONException {


        JSONObject JSONInput = new JSONObject(Jsondata);

        //Updating name
        JSONInput.put("firstname",Firstname.getText());
        JSONInput.put("Lastname",Lastname.getText());

        String refresh_token = "refresh_token=" + JSONInput.getString("refresh_token");
        Log.d("TAG", refresh_token);


        URL url = new URL(myUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestProperty ("Authorization", Authentification);
        conn.setDoOutput(true);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("refresh_token", Jsondata);
        conn.setRequestProperty("first_name", Jsondata);
        conn.setRequestProperty("lastname", Jsondata);
        // Set connection timeout and read timeout value.
        //conn.setRequestProperty("Content-Type", "application/json");
        //conn.setRequestProperty("Content-Length", "");

        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(conn.getOutputStream());
        outputStreamWriter.write(Jsondata);
        outputStreamWriter.flush();

        int responseCode = conn.getResponseCode();
        System.out.println("Response Code : " + responseCode);

        /***********************/
        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();  //UserData

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
        Log.d("TAG", "CurrentUserdata output stream: " + response.toString());

        //disconnect at the end
        conn.disconnect();

        return new String[] {Integer.toString(responseCode), response.toString()}; //1= errorcode , 2=token

    }

    public void UpdateButton(){
        ButtonUpdate.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {



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
            ErrorLog.setText("Connected "+networkInfo.getTypeName());
            // change background color to red
            ErrorLog.setBackgroundColor(0xFF7CCC26);


        } else {
            // show "Not Connected"
            ErrorLog.setText("Not Connected");
            // change background color to green
            ErrorLog.setBackgroundColor(0xFFFF0000);
        }

        return isConnected;
    }

}
