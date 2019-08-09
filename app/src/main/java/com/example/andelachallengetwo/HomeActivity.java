package com.example.andelachallengetwo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class HomeActivity extends AppCompatActivity {

    @BindView(R.id.rv_holidays_list)
    RecyclerView mRecyclerView;

    private HolidayAdapter mHolidayAdapter;

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReferenceHolidays;
    private DatabaseReference mDatabaseReferenceUsers;

    @BindView(R.id.fab)
    FloatingActionButton mFloatingActionButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);

        mFloatingActionButton.hide();

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReferenceHolidays = mFirebaseDatabase.getReference().child("holiday_offers").child(FirebaseAuth.getInstance().getUid());

        mDatabaseReferenceUsers = mFirebaseDatabase.getReference().child("users").child(FirebaseAuth.getInstance().getUid());

        mDatabaseReferenceUsers.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (user != null && user.isAdmin()){
                    mFloatingActionButton.show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this,AdminActivity.class);
                startActivity(intent);
            }
        });

        mHolidayAdapter = new HolidayAdapter();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);

        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(mHolidayAdapter);

        String TAG = HomeActivity.class.getSimpleName();

        mDatabaseReferenceHolidays.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mHolidayAdapter.clear();

                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                    HolidayDeal holidayDeal1 = dataSnapshot1.getValue(HolidayDeal.class);
                    mHolidayAdapter.addHolidayChilc(holidayDeal1);
                }
                mHolidayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

//        ChildEventListener childEventListener = new ChildEventListener() {
//            @Override
//            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
//
//                Log.d(TAG, "onChildAdded:" + dataSnapshot.getKey());
//                HolidayDeal holidayDeal = dataSnapshot.getValue(HolidayDeal.class);
//                mHolidayAdapter.addHolidayChilc(holidayDeal);
//
//            }
//
//            @Override
//            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//
//            }
//
//            @Override
//            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
//
//            }
//
//            @Override
//            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                Log.w(TAG, "postComments:onCancelled", databaseError.toException());
//                Toast.makeText(getApplicationContext(), "Failed to load comments.",
//                        Toast.LENGTH_SHORT).show();
//            }
//        };
//        mDatabaseReferenceHolidays.addChildEventListener(childEventListener);
    }
}
