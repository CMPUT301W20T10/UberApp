package com.cmput301w20t10.uberapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.cmput301w20t10.uberapp.R;

public class BalanceActivity extends AppCompatActivity implements AddFundsFragment.OnFragmentInteractionListener {
    private TextView totalBalance;
    private Float balanceSum = 0f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.balance_page);

        totalBalance = findViewById(R.id.balance);
        totalBalance.setText("$" + String.format("%.2f",balanceSum));

        Button addFundsButton = findViewById(R.id.add_funds_button);
        addFundsButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                new AddFundsFragment().show(getSupportFragmentManager(), "ADD_FUNDS");
            }
        });
    }

    public void updateBalance(Float addAmount) {
        balanceSum += addAmount;
        totalBalance.setText("$" + String.format("%.2f",balanceSum));
    }

}
