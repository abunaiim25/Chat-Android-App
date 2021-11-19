package com.example.chatsapp.Activites;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.chatsapp.databinding.ActivityPhoneNumberBinding;
import com.google.firebase.auth.FirebaseAuth;

public class PhoneNumber extends AppCompatActivity {

    ActivityPhoneNumberBinding binding;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityPhoneNumberBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getSupportActionBar().hide();//actionBar hide


        //show keyboard number
        binding.number.requestFocus();

        auth=FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null)
        {
            Intent intent = new Intent(PhoneNumber.this,MainActivity.class);
            startActivity(intent);
            finish();
        }


        binding.continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent (PhoneNumber.this,OTPActivity.class);
                intent.putExtra("phoneNumber",binding.number.getText().toString());
                startActivity(intent);
            }
        });
    }
}