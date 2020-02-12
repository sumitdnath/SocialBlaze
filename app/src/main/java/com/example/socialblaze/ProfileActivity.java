package com.example.socialblaze;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    private String receiverUserID;
    private CircleImageView userProfileImage;
    private TextView userProfileName, userProfileStatus;
    private Button sendMessageRequestButton;

    private DatabaseReference UserRef;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        UserRef = FirebaseDatabase.getInstance().getReference().child("Users");

        receiverUserID = getIntent().getExtras().get("visit_user_id").toString();

        userProfileImage = (CircleImageView)findViewById(R.id.visit_profile_image);
        userProfileName = (TextView)findViewById(R.id.visit_user_name);
        userProfileStatus = (TextView)findViewById(R.id.visit_profile_status);
        sendMessageRequestButton = (Button)findViewById(R.id.send_message_request_button);

        RetriveUserInfo();
    }

    private void RetriveUserInfo()
    {
        UserRef.child(receiverUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if ((dataSnapshot.exists()) && (dataSnapshot.hasChild("image")))
                {
                    String userImage = dataSnapshot.child("image").getValue().toString();
                    String userName = dataSnapshot.child("name").getValue().toString();
                    String userStatus = dataSnapshot.child("status").getValue().toString();

                    Picasso.get().load(userImage).placeholder(R.drawable.profile_image).into(userProfileImage);
                    userProfileName.setText(userName);
                    userProfileStatus.setText(userStatus);
                }
                else
                {

                    String userName = dataSnapshot.child("name").getValue().toString();
                    String userStatus = dataSnapshot.child("status").getValue().toString();


                    userProfileName.setText(userName);
                    userProfileStatus.setText(userStatus);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {

            }
        });
    }
}
