package com.example.weather;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.room.Room;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private LinearLayout citiesLayout;
    private Button addCityButton;
    private WeatherDataFetcher weatherDataFetcher;

    private FirebaseAuth mAuth;
    private static final String PREFS_NAME = "MyPrefsFile";
    private static final String KEY_IS_DATA_LOADED = "isDataLoaded";
    private boolean isDataLoaded = false;
    private NavigationView navigationView;
    private DrawerLayout drawer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }
        if (getIntent().hasExtra("isDataLoaded")) {
            isDataLoaded = getIntent().getBooleanExtra("isDataLoaded", false);
        }
        setContentView(R.layout.activity_main);
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        drawer = findViewById(R.id.drawer_layout);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.drawer_open, R.string.drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        citiesLayout = findViewById(R.id.cities_layout);
        addCityButton = findViewById(R.id.add_city_button);

        weatherDataFetcher = new WeatherDataFetcher(this);
        addCityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddCityActivity.class);
                startActivityForResult(intent, 1);
            }
        });

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            final String cityName = data.getStringExtra("cityName");
            weatherDataFetcher.fetchWeatherData(cityName, new WeatherDataFetcher.WeatherDataListener() {
                @Override
                public void onWeatherDataReceived(String temperature) {
                    addCityView(cityName, temperature);
                }

                @Override
                public void onError(String message) {
                    Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void addCityView(final String cityName, String temperature) {
        View cityView = getLayoutInflater().inflate(R.layout.city_view, citiesLayout, false);
        TextView cityTextView = cityView.findViewById(R.id.city_name);
        TextView tempTextView = cityView.findViewById(R.id.city_temperature);

        cityTextView.setText(cityName);
        tempTextView.setText(temperature);

        cityView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CityDetailActivity.class);
                intent.putExtra("cityName", cityName);
                startActivity(intent);
            }
        });

        citiesLayout.addView(cityView);
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_view) {
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            } else {
                drawer.openDrawer(GravityCompat.START);
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
            if (id == R.id.about_author)
            {
                Intent intent = new Intent(this, FragmentActivity.class);
                intent.putExtra("key",0);
                startActivity(intent);
            }
            else if(id == R.id.about_program) {
                Intent intent = new Intent(this, FragmentActivity.class);
                intent.putExtra("key",1);
                startActivity(intent);
            }
            else if(id == R.id.exit)
            {
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
            }
        drawer.closeDrawer(GravityCompat.START); // Закрытие NavigationDrawer после выбора элемента
        return true;
    }
    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putBoolean("isDataLoaded", isDataLoaded);
        super.onSaveInstanceState(savedInstanceState);

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            isDataLoaded = savedInstanceState.getBoolean("isDataLoaded");
        }
    }
    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(KEY_IS_DATA_LOADED, isDataLoaded);
        editor.apply();
    }
}