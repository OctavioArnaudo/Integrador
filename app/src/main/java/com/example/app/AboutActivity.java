package com.example.app;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

public class AboutActivity extends AppCompatActivity {
	private static final String TAG = "AboutActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        setupBackButton();
    }

    private void setupBackButton() {
        MaterialButton btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(view -> {
			Intent intent = new Intent(AboutActivity.this, ShowPasswordsActivity.class);
			startActivity(intent);
			finish();
		});
    }
}
