package com.iam2kabhishek.apodapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

public class APODActivity extends AppCompatActivity {
    String downloadURL = "";
    String date = LocalDate.now(ZoneId.of( "America/Montreal" )).toString();

    public static Bitmap getBitmapFromURL(String src) {
        try {
            Log.e("src", src);
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            Log.e("Bitmap", "returned");
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("Exception", e.getMessage());
            return null;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apodactivity);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        updateImages("");

        Button buttonShow = (Button) findViewById(R.id.buttonShow), buttonDownload = (Button) findViewById(R.id.buttonDownload);
        EditText editDate = (EditText) findViewById(R.id.editTextDate);

        buttonShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String inputDate = editDate.getText().toString();
                if (inputDate.length() > 1) {
                    date = inputDate;
                } else {
                    date = ""+((int)(Math.random()*21+1996))+"-"+((int)(Math.random()*12+1))+"-"+((int)(Math.random()*28+1));
                }
                updateImages(date);
            }
        });

        buttonDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(downloadURL));
                startActivity(browserIntent);
            }
        });
    }

    public void updateImages(String inputDate) {
        ImageView imageView = (ImageView) findViewById(R.id.imageView);
        TextView textTitle = (TextView) findViewById(R.id.textTitle), textDate = (TextView) findViewById(R.id.textDate), textDesc = (TextView) findViewById(R.id.textDesc);
        if (inputDate.length() > 1) {
            date = inputDate;
        }
        getCard(imageView, textTitle, textDate, textDesc, date);
    }

    public void getCard(ImageView imageView, TextView textTitle, TextView textDate, TextView textDesc, String date) {
        JSONParser jsonParser = new JSONParser();
        JSONObject jsonObject = jsonParser.getJSONFromUrl("https://api.nasa.gov/planetary/apod?api_key=uZvWdb4HL99nymXFPQlFfUBVZSuumS0MvqlVUHXs&date=" + date, null);
        Log.e("DATE", date);
        String desc = "No description.", url = "https://fabiusmaximus.files.wordpress.com/2012/12/20121230-no-error.png", text = "Title not found", author = "unknown", captureDate = "Date not found";
        try {
            desc = jsonObject.get("explanation").toString();
            url = jsonObject.get("url").toString();
            text = jsonObject.get("title").toString();
            captureDate = jsonObject.get("date").toString();
            downloadURL = url;
            downloadURL = jsonObject.get("hdurl").toString();
            author = jsonObject.get("copyright").toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.e("url=", url);
        imageView.setImageBitmap(getBitmapFromURL(url));
        textTitle.setText(text);
        textDesc.setText("    " + desc);
        textDesc.setMovementMethod(new ScrollingMovementMethod());
        textDesc.scrollTo(0, 0);
        textDate.setText("Published by " + author + " on " + captureDate);
    }
}