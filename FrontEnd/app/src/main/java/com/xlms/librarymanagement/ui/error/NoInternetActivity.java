package com.xlms.librarymanagement.ui.error;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import com.xlms.librarymanagement.R;

public class NoInternetActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_internet);

        Button btnReconnect = findViewById(R.id.btnReconnect);
        Button btnExit = findViewById(R.id.btnExit);

        btnReconnect.setOnClickListener(v -> {
            finish(); // Simply return to the previous screen to retry
        });

        btnExit.setOnClickListener(v -> {
            finishAffinity(); // Close the entire app
        });
    }

    @Override
    public void onBackPressed() {
        // Prevent back button from bypassing the error screen
    }
}
