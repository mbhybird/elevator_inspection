package com.mbhybird.elevatorinspection;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class FunctionActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_function);

        Button btnBack = (Button) findViewById(R.id.btnFunctionBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(FunctionActivity.this, MainActivity.class));
                FunctionActivity.this.finish();
            }
        });

        Button btnBalanceTest = (Button) findViewById(R.id.btnBalanceTest);
        btnBalanceTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(FunctionActivity.this, BalanceOneActivity.class));
                FunctionActivity.this.finish();
            }
        });
    }
}
