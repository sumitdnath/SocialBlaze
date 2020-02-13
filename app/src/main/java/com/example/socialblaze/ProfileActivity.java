package com.example.socialblaze;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    private String receiverUserID,senderUserID, Current_State;
    private CircleImageView userProfileImage;
    private TextView userProfileName, userProfileStatus;
    private Button sendMessageRequestButton, DeclinedMessageRequestButton;

    private DatabaseReference UserRef, chatRequestRef, ContactsRef;

    private FirebaseAuth mAuth;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);


        mAuth = FirebaseAuth.getInstance();

        UserRef = FirebaseDatabase.getInstance().getReference().child("Users");
        chatRequestRef = FirebaseDatabase.getInstance().getReference().child("Chat Requests");
        ContactsRef = FirebaseDatabase.getInstance().getReference().child("Contacts");



        receiverUserID = getIntent().getExtras().get("visit_user_id").toString();

        senderUserID = mAuth.getCurrentUser().getUid();

        userProfileImage = (CircleImageView)findViewById(R.id.visit_profile_image);
        userProfileName = (TextView)findViewById(R.id.visit_user_name);
        userProfileStatus = (TextView)findViewById(R.id.visit_profile_status);
        sendMessageRequestButton = (Button)findViewById(R.id.send_message_request_button);
        DeclinedMessageRequestButton = (Button)findViewById(R.id.declined_message_request_button);

        Current_State = "new";


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

                    MenageChatRequest();
                }
                else
                {

                    String userName = dataSnapshot.child("name").getValue().toString();
                    String userStatus = dataSnapshot.child("status").getValue().toString();


                    userProfileName.setText(userName);
                    userProfileStatus.setText(userStatus);

                    MenageChatRequest();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {

            }
        });
    }

    private void MenageChatRequest()
    {
        chatRequestRef.child(senderUserID)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                    {
                        if (dataSnapshot.hasChild(receiverUserID))
                        {
                            String request_type = dataSnapshot.child(receiverUserID).child("request_type").getValue().toString();

                            if (request_type.equals("sent"))
                            {
                                Current_State = "request_sent";
                                sendMessageRequestButton.setText("Cancel Chat Request");
                            }
                            else if (request_type.equals("received"))
                            {
                                Current_State = "request_received";
                                sendMessageRequestButton.setText("Accept Chat Request");

                                DeclinedMessageRequestButton.setVisibility(View.VISIBLE);
                                DeclinedMessageRequestButton.setEnabled(true);

                                DeclinedMessageRequestButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v)
                                    {
                                        CancelChatRequests();

                                    }
                                });
                            }

                        }
                        else
                        {
                            ContactsRef.child(senderUserID).child(receiverUserID)
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                                        {
                                        if (dataSnapshot.hasChild(receiverUserID))
                                        {
                                            Current_State = "frienda";
                                            sendMessageRequestButton.setText("Remove this contact");
                                        }

                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

        if (!senderUserID.equals(receiverUserID))
        {
            sendMessageRequestButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    sendMessageRequestButton.setEnabled(false);
                    if (Current_State.equals("new"))
                    {
                        SendChatRequest();
                    }
                    if (Current_State.equals("request_sent"))
                    {
                        CancelChatRequests();
                    }
                    if (Current_State.equals("request_received"))
                    {
                        AcceptChatRequest();
                    }
                }
            });
        }
        else
            {
                sendMessageRequestButton.setVisibility(View.INVISIBLE);
            }

    }

    private void AcceptChatRequest()

    {
        ContactsRef.child(senderUserID).child(receiverUserID)
                .child("Contacts").setValue("Saved")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        if (task.isSuccessful())
                        {
                            ContactsRef.child(receiverUserID).child(senderUserID)
                                    .child("Contacts").setValue("Saved")
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task)
                                        {
                                            if (task.isSuccessful())
                                            {
                                                chatRequestRef.child(senderUserID).child(receiverUserID)
                                                        .removeValue()
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task)
                                                            {
                                                                if (task.isSuccessful())
                                                                {
                                                                    chatRequestRef.child(receiverUserID).child(senderUserID)
                                                                            .removeValue()
                                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<Void> task)
                                                                                {
                                                                                    sendMessageRequestButton.setEnabled(true);
                                                                                    Current_State = "friends";
                                                                                    sendMessageRequestButton.setText("Remove this contact");

                                                                                    DeclinedMessageRequestButton.setVisibility(View.INVISIBLE);
                                                                                    DeclinedMessageRequestButton.setEnabled(false);

                                                                                }
                                                                            });
                                                                }
                                                            }
                                                        });
                                            }
                                        }
                                    });
                        }
                    }
                });
    }


    private void CancelChatRequests()

    {
        chatRequestRef.child(senderUserID).child(receiverUserID).removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {

                        if (task.isSuccessful())
                        {
                            chatRequestRef.child(receiverUserID).child(senderUserID).removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task)
                                        {
                                            if (task.isSuccessful())
                                            {
                                                sendMessageRequestButton.setEnabled(true);
                                                Current_State = "new";
                                                sendMessageRequestButton.setText("Send Message");

                                                DeclinedMessageRequestButton.setVisibility(View.INVISIBLE);
                                                DeclinedMessageRequestButton.setEnabled(false);

                                            }

                                        }
                                    });
                        }

                    }
                });
    }



    private void SendChatRequest()
    {
        chatRequestRef.child(senderUserID).child(receiverUserID)
                .child("request_type").setValue("sent")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        if (task.isSuccessful())
                        {
                            chatRequestRef.child(receiverUserID).child(senderUserID)
                                    .child("request_type").setValue("received")
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task)
                                        {

                                            if (task.isSuccessful())
                                            {
                                                sendMessageRequestButton.setEnabled(true);
                                                Current_State = "request_sent";
                                                sendMessageRequestButton.setText("Cancel Chat Request");
                                            }
                                        }
                                    });
                        }
                    }
                });
    }


}
