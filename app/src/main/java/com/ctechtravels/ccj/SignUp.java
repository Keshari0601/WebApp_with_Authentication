package com.ctechtravels.ccj;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.TimeUnit;

public class SignUp extends AppCompatActivity {
    EditText name,phone,age,city,otp;
    Button register,resend,verify;
    FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;
    private String mVerificationID;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    DatabaseReference mDatabase;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        firebaseAuth = FirebaseAuth.getInstance();
        if(firebaseAuth.getCurrentUser() != null){
            //that means user is already logged in
            //so close this activity
            finish();

            //and open profile activity
        }

        name=findViewById(R.id.name);
        age=findViewById(R.id.age);
        phone=findViewById(R.id.phone);
        city=findViewById(R.id.city);
        otp=findViewById(R.id.otp);
        register=findViewById(R.id.btnRegister);
       // resend=findViewById(R.id.resend);
        verify=findViewById(R.id.verify);
        otp.setVisibility(View.INVISIBLE);
        verify.setVisibility(View.INVISIBLE);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!Patterns.PHONE.matcher(phone.getText().toString()).matches())
                {
                    Toast.makeText(SignUp.this,"Please enter a valid Phone Number",Toast.LENGTH_SHORT).show();
                    phone.requestFocus();
                }

                else if(name.getText().toString().equals(""))
                {
                    Toast.makeText(SignUp.this,"Please Enter Your Name",Toast.LENGTH_SHORT).show();
                    name.requestFocus();
                }
                else if(age.getText().toString().equals("")||Integer.parseInt(age.getText().toString())>130||Integer.parseInt(age.getText().toString())<5){
                    Toast.makeText(SignUp.this,"Please Enter a valid age",Toast.LENGTH_SHORT).show();
                    age.requestFocus();

                }
                else if (city.getText().toString().equals(""))
                {
                    Toast.makeText(SignUp.this,"Please Enter your current city",Toast.LENGTH_SHORT).show();
                    city.requestFocus();

                }
                else
                {

                    registerUser();

                }
            }
        });
        verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String code=otp.getText().toString();
                progressDialog.show();
                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationID, code);
                signInWithPhoneAuthCredential(credential);

            }
        });




    }


    private void registerUser() {

        //getting email and password from edit texts
        String num=phone.getText().toString();


        progressDialog=new ProgressDialog(SignUp.this);
        progressDialog.setMessage("Registering Please Wait...");
        progressDialog.show();



        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verification without
                //     user action.
                signInWithPhoneAuthCredential(credential);
                progressDialog.show();
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                    Toast.makeText(SignUp.this,"Verification code is invalid",Toast.LENGTH_SHORT).show();
                    progressDialog.hide();
                    // ...
                } else {
                    // The SMS quota for the project has been exceeded
                    Toast.makeText(SignUp.this,"An error occured",Toast.LENGTH_SHORT).show();
                    progressDialog.hide();
                }

                // Show a message and update the UI
                // ...
            }

            @Override
            public void onCodeSent(String verificationId,
                                   PhoneAuthProvider.ForceResendingToken token) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.

                // Save verification ID and resending token so we can use them later
                otp.setVisibility(View.VISIBLE);
                verify.setVisibility(View.VISIBLE);

                progressDialog.hide();
                mVerificationID=verificationId;
                mResendToken=token;
                //editTextVerification=(EditText)findViewById(R.id.editTextVerification);
                //buttonVerification=(Button)findViewById(R.id.buttonVerification);
                verify.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String code=otp.getText().toString();
                        progressDialog.show();
                        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationID, code);
                        signInWithPhoneAuthCredential(credential);

                    }
                });


                // ...
            }
        };
        //creating a new user
        PhoneAuthProvider.getInstance().verifyPhoneNumber(phone.getText().toString(),60, TimeUnit.SECONDS,SignUp.this,mCallbacks);

    }
    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = task.getResult().getUser();
                            Toast.makeText(SignUp.this,"Registration successful",Toast.LENGTH_SHORT).show();
                            DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
                            DatabaseReference userNameRef = rootRef.child("Users").child(firebaseAuth.getCurrentUser().getPhoneNumber());

                            ValueEventListener eventListener = new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if(dataSnapshot.exists())
                                    {


                                        progressDialog.hide();
                                    finish();




                                    }
                                    else{
                                        progressDialog.hide();


                                    }

                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {



                                }
                            };
                            userNameRef.addListenerForSingleValueEvent(eventListener);

                            progressDialog.setMessage("Please wait...");
                            progressDialog.show();

                            mDatabase= FirebaseDatabase.getInstance().getReference();






                            mDatabase.child("Users").child(firebaseAuth.getCurrentUser().getPhoneNumber()).child("Name").setValue(name.getText().toString());
                            mDatabase.child("Users").child(firebaseAuth.getCurrentUser().getPhoneNumber()).child("Age").setValue(age.getText().toString());

                            mDatabase.child("Users").child(firebaseAuth.getCurrentUser().getPhoneNumber()).child("City").setValue(city.getText().toString()).addOnCompleteListener(SignUp.this, new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {


                                    progressDialog.hide();
                                    finish();

                                }
                            });



                        } else {
                            // Sign in failed, display a message and update the UI
                            Toast.makeText(SignUp.this,task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                            progressDialog.hide();
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                Toast.makeText(SignUp.this,"Please Enter a Valid verification code",Toast.LENGTH_SHORT).show();
                                progressDialog.hide();// The verification code entered was invalid
                            }
                        }
                    }
                });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }
}
