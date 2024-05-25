package com.example.weather;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class CityDetailActivity extends AppCompatActivity {

    private TextView cityNameTextView;
    private TextView cityDetailTextView;
    private WeatherDataFetcher weatherDataFetcher;
    private EditText intervalEditText;
    private Button setNotificationButton;
        private String cityName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_detail);

        cityNameTextView = findViewById(R.id.city_name_text_view);
        cityDetailTextView = findViewById(R.id.city_detail_text_view);
        weatherDataFetcher = new WeatherDataFetcher(this);
        intervalEditText = findViewById(R.id.interval_edit_text);
        setNotificationButton = findViewById(R.id.set_notification_button);

        final String cityName = getIntent().getStringExtra("cityName");
        cityNameTextView.setText(cityName);

        weatherDataFetcher.fetchWeatherData(cityName, new WeatherDataFetcher.WeatherDataListener() {
            @Override
            public void onWeatherDataReceived(String temperature) {
                cityDetailTextView.setText("Температура: " + temperature);
                // Здесь вы можете добавить дополнительную информацию
            }

            @Override
            public void onError(String message) {
                Toast.makeText(CityDetailActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
        setNotificationButton.setOnClickListener(v -> {
            String intervalString = intervalEditText.getText().toString();
            if (!intervalString.isEmpty()) {
                int interval = Integer.parseInt(intervalString);
                setWeatherNotification(interval, cityName);
                Toast.makeText(CityDetailActivity.this, "Уведомление установлено на каждые " + interval + " минут", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(CityDetailActivity.this, "Пожалуйста, введите интервал", Toast.LENGTH_SHORT).show();
            }
        }); }
    private void setWeatherNotification(int interval, String name) {
        Intent intent = new Intent(CityDetailActivity.this, WeatherNotificationReceiver.class);

        intent.putExtra("cityName", name);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(CityDetailActivity.this,
                0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        long intervalMillis = interval * 60 * 1000;
        long triggerAtMillis = System.currentTimeMillis() + intervalMillis;

        if (alarmManager != null) {
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, triggerAtMillis, intervalMillis, pendingIntent);
        }
    }
}

