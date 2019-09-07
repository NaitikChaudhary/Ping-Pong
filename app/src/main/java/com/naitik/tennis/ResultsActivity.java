package com.naitik.tennis;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ResultsActivity extends AppCompatActivity {

    TextView finalResults, winner;
    Button playAgain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        String result = getIntent().getStringExtra("result");
        String winningPlayer = getIntent().getStringExtra("winningPlayer");

        finalResults = (TextView) findViewById(R.id.final_results);
        winner = (TextView) findViewById(R.id.winner);
        playAgain = (Button) findViewById(R.id.playAgain);

        finalResults.setText(result);
        winner.setText(winningPlayer);

        playAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ResultsActivity.this, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
            }
        });

    }
}
