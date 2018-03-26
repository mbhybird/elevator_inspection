package com.mbhybird.elevatorinspection;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class BalanceTwoActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_balance_two);

        Button btnBack = (Button) findViewById(R.id.btnBalanceTwoBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(BalanceTwoActivity.this, BalanceOneActivity.class));
                BalanceTwoActivity.this.finish();
            }
        });

        Button btnBalanceThree = (Button) findViewById(R.id.btnBalanceThree);
        btnBalanceThree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(BalanceTwoActivity.this, BalanceThreeActivity.class));
                BalanceTwoActivity.this.finish();
            }
        });
    }
}
