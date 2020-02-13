package com.example.socialblaze;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class RequestsFragment extends Fragment {


    private View RequestsFragmentView;
    private RecyclerView myRequestsList;

    private DatabaseReference ChatRequestRef, UserRef;

    private FirebaseAuth mAuth;
    private String CurrentUserID;



    public RequestsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        RequestsFragmentView = inflater.inflate(R.layout.fragment_requests, container, false);
        UserRef = FirebaseDatabase.getInstance().getReference().child("Users");

        ChatRequestRef = FirebaseDatabase.getInstance().getReference().child("Chat Requests");
        mAuth = FirebaseAuth.getInstance();

        CurrentUserID = mAuth.getCurrentUser().getUid();

        myRequestsList = (RecyclerView)RequestsFragmentView.findViewById(R.id.chat_requests_list);
        myRequestsList.setLayoutManager(new LinearLayoutManager(getContext()));


        return  RequestsFragmentView;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Contacts> options =
                new FirebaseRecyclerOptions.Builder<Contacts>()
                .setQuery(ChatRequestRef.child(CurrentUserID), Contacts.class)
                .build();

        FirebaseRecyclerAdapter<Contacts, RequestViewHolder> adapter =
                new FirebaseRecyclerAdapter<Contacts, RequestViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull final RequestViewHolder holder, int position, @NonNull Contacts model)
                    {
                        holder.itemView.findViewById(R.id.requests_accept_button).setVisibility(View.VISIBLE);
                        holder.itemView.findViewById(R.id.requests_cancel_button).setVisibility(View.VISIBLE);


                        final String list_user_id = getRef(position).getKey();

                        DatabaseReference getTypeRef = getRef(position).child("request_type").getRef();
                        getTypeRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                            {

                                if (dataSnapshot.exists())
                                {
                                    String type = dataSnapshot.getValue().toString();
                                    if (type.equals("received"))
                                    {
                                        UserRef.child(list_user_id).addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                                            {
                                                if (dataSnapshot.hasChild("image"))
                                                {
                                                    final String requestUserName = dataSnapshot.child("name").getValue().toString();
                                                    final String requestUserStatus = dataSnapshot.child("status").getValue().toString();
                                                    final String requestProfileImage = dataSnapshot.child("image").getValue().toString();

                                                    holder.userName.setText(requestUserName);
                                                    holder.userStatus.setText(requestUserStatus);
                                                    Picasso.get().load(requestProfileImage).into(holder.profileImage);

                                                }
                                                else
                                                {
                                                    final String requestUserName = dataSnapshot.child("name").getValue().toString();
                                                    final String requestUserStatus = dataSnapshot.child("status").getValue().toString();

                                                    holder.userName.setText(requestUserName);
                                                    holder.userStatus.setText(requestUserStatus);

                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError)
                                            {

                                            }
                                        });
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError)
                            {

                            }
                        });


                    }

                    @NonNull
                    @Override
                    public RequestViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i)
                    {
                        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.users_display_layout, viewGroup,false);
                        RequestViewHolder holder = new RequestViewHolder(view);
                        return holder;
                    }
                };

        myRequestsList.setAdapter(adapter);
        adapter.startListening();
    }

    public  static class RequestViewHolder extends RecyclerView.ViewHolder
    {
        TextView userName, userStatus;
        CircleImageView profileImage;
        Button AcceptButton, CancelButton;


        public RequestViewHolder(@NonNull View itemView) {
            super(itemView);

            userName = itemView.findViewById(R.id.user_profile_name);
            userStatus = itemView.findViewById(R.id.user_status);
            profileImage = itemView.findViewById(R.id.users_profile_image);
            AcceptButton = itemView.findViewById(R.id.requests_accept_button);
            CancelButton = itemView.findViewById(R.id.requests_cancel_button);


        }
    }
}
