package com.example.weather;

import static java.lang.Integer.parseInt;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class WeatherDataFetcher {
    private static final String API_KEY = "563178df3b34d2fd23a27664196f6717";


    private static final String BASE_URL = "http://api.openweathermap.org/data/2.5/weather?q=%s&units=metric&appid=%s";
    private RequestQueue requestQueue;

    public WeatherDataFetcher(Context context) {
        requestQueue = Volley.newRequestQueue(context);
    }

    public interface WeatherDataListener {
        void onWeatherDataReceived(String temperature);
        void onError(String message);
    }

    public void fetchWeatherData(String cityName, final WeatherDataListener listener) {
        String url = "https://api.openweathermap.org/data/2.5/weather?q=" + cityName
                + "&units=metric&appid=563178df3b34d2fd23a27664196f6717";
        System.out.println(url);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject main = response.getJSONObject("main");
                            double a = Double.parseDouble(main.getString("temp"));
                            int b =  (int) Math.round(a);
                            String temperature = b + "°C";
                            listener.onWeatherDataReceived(temperature);
                        } catch (JSONException e) {
                            System.out.println(e);
                            listener.onError("Ошибка обработки данных о погоде.");
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        listener.onError("Ошибка запроса данных о погоде.");
                    }
                }
        );
        requestQueue.add(jsonObjectRequest);
    }
}
