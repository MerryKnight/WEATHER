package com.example.weather;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class WeatherNotificationReceiver extends BroadcastReceiver {
    private static final String CHANNEL_ID = "WeatherChannel";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("RRR", "onReceive called");
        String cityName = intent.getStringExtra("cityName");
        Log.d("RRR", "City: " + cityName);

        WeatherDataFetcher weatherDataFetcher = new WeatherDataFetcher(context);
        weatherDataFetcher.fetchWeatherData(cityName, new WeatherDataFetcher.WeatherDataListener() {
            @Override
            public void onWeatherDataReceived(String temperature) {
                showNotification(context, cityName, temperature);
            }
            @Override
            public void onError(String message) {
            }
        });
    }

    private void showNotification(Context context, String cityName, String temperature) {
        createNotificationChannel(context);

        Intent intent = new Intent(context, CityDetailActivity.class);
        intent.putExtra("cityName", cityName);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Погода в " + cityName)
                .setContentText("Температура: " + temperature)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(1, builder.build());
    }

    private void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Weather Channel";
            String description = "Channel for weather notifications";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
