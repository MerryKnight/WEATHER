package com.example.weather;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class AddCityActivity extends AppCompatActivity {
    private EditText cityNameEditText;
    private Button confirmButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_city);

        cityNameEditText = findViewById(R.id.city_name_edit_text);
        confirmButton = findViewById(R.id.confirm_button);

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String cityName = cityNameEditText.getText().toString();
                Intent resultIntent = new Intent();
                resultIntent.putExtra("cityName", cityName);
                setResult(RESULT_OK, resultIntent);
                finish();
            }
        });
    }
}
