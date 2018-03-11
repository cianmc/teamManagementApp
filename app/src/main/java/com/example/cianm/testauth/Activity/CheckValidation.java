package com.example.cianm.testauth.Activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cianm.testauth.Entity.User;
import com.example.cianm.testauth.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CheckValidation extends AppCompatActivity {

    private static final String TAG = "CheckValidation";

    TextView mUser;
    Button mVerifyAccount;

    private FirebaseAuth mAuth;
    private FirebaseUser fbUser;
    private DatabaseReference mDatabase;

    boolean emailVerified;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_validation);

        mUser = (TextView) findViewById(R.id.verifyAccount);
        mVerifyAccount = (Button) findViewById(R.id.verifyAccountBtn);
//        mDeleteAcc = (Button) findViewById(R.id.deleteAccount);

        mDatabase = FirebaseDatabase.getInstance().getReference("User");

        mAuth = FirebaseAuth.getInstance();
        fbUser = FirebaseAuth.getInstance().getCurrentUser();

        // Check if user's email is verified
        emailVerified = fbUser.isEmailVerified();

        if(emailVerified == true) {
            mVerifyAccount.setVisibility(View.GONE);
            mUser.setVisibility(View.GONE);
           startActivity(new Intent(CheckValidation.this, SelectTeam.class));
//            mDatabase.child(fbUser.getUid()).addValueEventListener(new ValueEventListener() {
//                @Override
//                public void onDataChange(DataSnapshot dataSnapshot) {
//
//                    user = dataSnapshot.getValue(User.class);
//                    if (user.getType().equalsIgnoreCase("Manager")) {
//                        startActivity(new Intent(CheckValidation.this, ManagerHome.class));
//                    } else if (user.getType().equalsIgnoreCase("Player")){
//                        startActivity(new Intent(CheckValidation.this, PlayerHome.class));
//                    }
//                }
//
//                @Override
//                public void onCancelled(DatabaseError error) {
//                    // Failed to read value
//                    Log.w(TAG, "Failed to read value.", error.toException());
//                }
//            });
        }

        mVerifyAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendVerificationEmail();
            }
        });

        }

//        deleteAcc.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                progressBar.setVisibility(View.VISIBLE);
//                //               mDatabase.child(fbUser.getUid()).removeValue();
//                if (fbUser !=null){
//                    mDatabase.child(fbUser.getUid()).removeValue();
//                    fbUser.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
//                        @Override
//                        public void onComplete(@NonNull Task<Void> task) {
//                            if (task.isSuccessful()){
//                                startActivity(new Intent(CheckValidation.this, RegisterUser.class));
//                                finish();
//                                progressBar.setVisibility(View.GONE);
//                            } else {
//                                Toast.makeText(CheckValidation.this, "Failed to delete your account!", Toast.LENGTH_SHORT).show();
//                                progressBar.setVisibility(View.GONE);
//                            }
//                        }
//                    });
//                }
//            }
//        });


    public void sendVerificationEmail() {
        fbUser.sendEmailVerification()
                .addOnCompleteListener(this, new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(CheckValidation.this,"Verification email sent to " + fbUser.getEmail(), Toast.LENGTH_SHORT).show();
                            mAuth.signOut();
                            startActivity(new Intent(CheckValidation.this, MainActivity.class));
                        } else {
                            Log.e(TAG, "sendEmailVerification", task.getException());
                            Toast.makeText(CheckValidation.this,"Failed to send verification email.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
