package com.example.wefor;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    TextView temperatureLabel;
    TextView weatherImage;
    TextView locationText;
    Typeface weatherFont;
    EditText zipcodeText;
    Button searchButton;

    private static String ZIP_CODE = "zip_code";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        temperatureLabel = findViewById(R.id.temperatureLabel);
        weatherImage = findViewById(R.id.weatherImage);
        locationText = findViewById(R.id.locationText);

        weatherFont = Typeface.createFromAsset(getAssets(), "weathericons-regular-webfont.ttf");
        weatherImage.setTypeface(weatherFont);

        zipcodeText = findViewById(R.id.zipcodeText);
        searchButton = findViewById(R.id.searchButton);

        //Перевірити чи ми маємо збережений поштовий код
        SharedPreferences prefs = getPreferences(Context.MODE_PRIVATE);
        String zipCode = prefs.getString(ZIP_CODE, null);
        if (zipCode != null)
        {
            updateWeather(zipCode);
        }
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String zipCode = zipcodeText.getText().toString();
                updateWeather(zipCode);
            }
        });
    }
    String getIconForCode(int code){

        //Handle special cases
        if (code == 800 )
            return "\uf00d";
        else if (code == 781)
            return "\uf056";
        else if (code == 762)
            return "/uf0c8";

        int hundreads = code /100;
        switch (hundreads){
            case 2:
                return "\uf00c";
            case 3:
                return "\uf00b";
            case 5:
                return "\uf008";
            case 6:
                return "\uf00a";
            case 7:
                return "\uf003";
            case 8:
                return "\uf002";
            default:
                return "\uf041";
        }
    }

    void updateWeather(final String zipCode){
        String apiUrl = String.format("https://api.openweathermap.org/data/2.5/weather?zip=%1$s,us&units=imperial&APPID=87926dd31ce06f2343826d99f3482c6b",zipCode);

        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, apiUrl, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                // Show the response on the screen
                try {

                    // Get the current temperature and store it in the label
                    double temp = response.getJSONObject("main").getDouble("temp");
                    String tempFormatted = getString(R.string.temp_format, temp);
                    temperatureLabel.setText(tempFormatted);

                    // Get the icon for the current weather
                    int weatherCode = response.getInt("cod");
                    String iconSymbol = getIconForCode(weatherCode);
                    weatherImage.setText(iconSymbol);

                    // Отримати місцезнаходження для нашого почтового коду
                    String location = response.getString("name");
                    locationText.setText(location);

                    SharedPreferences prefs = getPreferences(Context.MODE_PRIVATE);
                    SharedPreferences.Editor prefsEditor = prefs.edit();
                    prefsEditor.putString(ZIP_CODE,zipCode);
                    prefsEditor.apply();


                } catch (JSONException e) {
                    Toast errorToast = Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_LONG);
                    errorToast.show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast errorToast = Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_LONG);
                errorToast.show();
            }
        });

        queue.add(request);
    }
}
