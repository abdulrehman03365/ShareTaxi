package com.example.abdul.sharetaxi;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private String Status;
int parity=1;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        final Button Loginbt = findViewById(R.id.Loginbt);
        Button Signupbt =  findViewById(R.id.Signupbt);
        final Button Loginwithemailbt =  findViewById(R.id.LoginwithEmailbt);
        final Button Loginwithphonebt =  findViewById(R.id.LoginwithPhonebt);
    Loginwithemailbt.setVisibility(View.GONE);
    Loginwithphonebt.setVisibility(View.GONE);
        Loginbt.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Loginwithemailbt.setVisibility(View.VISIBLE);
            Loginwithphonebt.setVisibility(View.VISIBLE);
            Loginbt.setVisibility(View.GONE);

        }
    });
Loginwithemailbt.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        Intent intent = new Intent(MainActivity.this,LoginwithEmail.class);
        startActivity(intent);
    }
});
        Loginwithphonebt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Status ="Login";
                Intent intent = new Intent(MainActivity.this,PhoneAuthentication.class);


                //bundle.putInt("Status",Status);
                intent.putExtra("Loginstatus",Status);
                startActivity(intent);
            }
        });
    Signupbt.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            Intent intent = new Intent(MainActivity.this,SignupOption.class);
            startActivity(intent);
        }
    });
    }
}
