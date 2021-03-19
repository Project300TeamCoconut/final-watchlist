package com.project300.movieswipe;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

public class SettingsActivity extends AppCompatActivity {

    private EditText mNameField;

    private TextView mUserID;

    private Button mBack, mConfirm, deleteAccount, logOut;

    //get current user ID
    private FirebaseAuth mAuth;

    private DatabaseReference mCustomerDatabase;

    private String userID, name;

    private URI resultUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);


        mNameField = findViewById(R.id.name);
        mUserID = findViewById(R.id.userID);

        mBack = findViewById(R.id.back);
        mConfirm = findViewById(R.id.confirm);

        deleteAccount = findViewById(R.id.deleteBTN);
        logOut  = findViewById(R.id.LogoutBTN);


        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null){
            userID = user.getUid();
        }

        mUserID.setText(userID);

        mCustomerDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(userID);

        getUserInfo();

        mConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveUserInformation();
            }
        });


        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        deleteAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(SettingsActivity.this);
                dialog.setTitle("Are you sure?");
                dialog.setMessage("Delete this account will result in completely removing your"+ " account from the system and you will no longer be able to log in");
                dialog.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    Toast.makeText(SettingsActivity.this, "Account Deleted",
                                            Toast.LENGTH_LONG).show();

                                    Intent intent = new Intent(SettingsActivity.this, ChooseLoginRegistrationActivity.class);
                                    startActivity(intent);
                                }
                                else{
                                    Toast.makeText(SettingsActivity.this, task.getException().getMessage(),
                                            Toast.LENGTH_LONG).show();

                                }
                            }
                        });
                    }
                });

                dialog.setNegativeButton("Dismiss", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        dialogInterface.dismiss();
                    }
                });

                AlertDialog alertDialog = dialog.create();
                alertDialog.show();
            }
        });

        logOut.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                mAuth.getInstance().signOut();
                Intent intent = new Intent(SettingsActivity.this, ChooseLoginRegistrationActivity.class);
                startActivity(intent);

            }
        });


    }


    private void getUserInfo() {

        mCustomerDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists() && snapshot.getChildrenCount()> 0){
                    Map<String, Object> map = (Map<String, Object>) snapshot.getValue();

                    if(map.get("name")!=null){
                        name = map.get("name").toString();
                        mNameField.setText(name);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void saveUserInformation() {

        name = mNameField.getText().toString();

        Map userInfo = new HashMap();
        userInfo.put("name", name);


        mCustomerDatabase.updateChildren(userInfo);




    }

    public void copyToClipboard(View view) {

        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("label",userID);
        clipboard.setPrimaryClip(clip);
        Toast.makeText(getApplicationContext(), "Text copied to clipboard", Toast.LENGTH_SHORT).show();
    }
}