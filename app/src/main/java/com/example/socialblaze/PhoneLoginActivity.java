package com.example.socialblaze;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.hbb20.CountryCodePicker;

import java.util.concurrent.TimeUnit;


public class PhoneLoginActivity extends AppCompatActivity {

    private CountryCodePicker ccp;
    private EditText phoneText;
    private EditText codeText;
    private Button continueAndNextBtn;
    private String checker = "", phoneNumber = "";
    private RelativeLayout relativeLayout;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private FirebaseAuth mAuth;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendingToken;
    private ProgressDialog loadingbar;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_login);

        mAuth = FirebaseAuth.getInstance();
        loadingbar = new ProgressDialog(this);

    phoneText = findViewById(R.id.phoneText);
    codeText = findViewById(R.id.codeText);
    continueAndNextBtn = findViewById(R.id.continueNextButton);
    relativeLayout = findViewById(R.id.phoneAuth);
    ccp = (CountryCodePicker)findViewById(R.id.ccp);
    ccp.registerCarrierNumberEditText(phoneText);


    continueAndNextBtn.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (continueAndNextBtn.getText().equals("Submit") || checker.equals("Code Sent"))
            {
                String verificationCode = codeText.getText().toString();

                if (verificationCode.equals(""))
                {
                    Toast.makeText(PhoneLoginActivity.this, "Please Write The Verification Code First.", Toast.LENGTH_SHORT).show();

                }
                else
                {
                    loadingbar.setTitle("Verification Code");
                    loadingbar.setMessage("please wait, while we are verifying verification code...");
                    loadingbar.setCanceledOnTouchOutside(false);
                    loadingbar.show();

                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, verificationCode);
                    signInWithPhoneAuthCredential(credential);

                }

            }
            else
            {
                phoneNumber = ccp.getFullNumberWithPlus();
                if (!phoneNumber.equals(""))
                {
                    loadingbar.setTitle("Phone Number Verification");
                    loadingbar.setMessage("please wait, while we are authenticating your phone...");
                    loadingbar.setCanceledOnTouchOutside(false);
                    loadingbar.show();

                    PhoneAuthProvider.getInstance().verifyPhoneNumber(phoneNumber,60, TimeUnit.SECONDS,PhoneLoginActivity.this,mCallbacks);        // OnVerificationStateChangedCallbacks

                }
                else
                {
                    Toast.makeText(PhoneLoginActivity.this, "Please Write Valid Phone Number..", Toast.LENGTH_SHORT).show();
                }

            }
        }
    });

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential)
            {
                signInWithPhoneAuthCredential(phoneAuthCredential);

            }

            @Override
            public void onVerificationFailed(FirebaseException e)
            {
                Toast.makeText(PhoneLoginActivity.this, "Invalid Phone number", Toast.LENGTH_SHORT).show();
                loadingbar.dismiss();
                relativeLayout.setVisibility(View.VISIBLE);

                continueAndNextBtn.setText("Continue");
                codeText.setVisibility(View.GONE);

            }

            @Override
            public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);

                mVerificationId = s;
                mResendingToken = forceResendingToken;
                

                relativeLayout.setVisibility(View.GONE);

                checker = "Code Sent";
                continueAndNextBtn.setText("Submit");
                codeText.setVisibility(View.VISIBLE);
                loadingbar.dismiss();

                Toast.makeText(PhoneLoginActivity.this, "Code has been sent, Please check.", Toast.LENGTH_SHORT).show();

            }
        };


    }


    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful())
                        {
                            loadingbar.dismiss();
                            sendUserToMainActivity();


                        } else
                        {
                            loadingbar.dismiss();
                            String e = task.getException().toString();
                            Toast.makeText(PhoneLoginActivity.this, "Error: " + e, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private  void sendUserToMainActivity()
    {
        Intent intent = new Intent(PhoneLoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}



