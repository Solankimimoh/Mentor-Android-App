package com.example.mentor;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView profileAvatarImg;
    private TextView profileNameTv;
    private TextView profileEmailTv;
    private TextView profileMobileTv;
    private TextView profileIndustryTv;
    private TextView profileExperienceTv;
    private TextView profileEducationTv;
    private TextView profileMentorTypeTv;
    private TextView profileMentorSkillTv;
    private Button profileFollwingBtn;
    private Button profileFollwerBtn;

    private String loginType;

    //    Firebase Init
    private DatabaseReference DataRef;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        auth = FirebaseAuth.getInstance();
        DataRef = FirebaseDatabase.getInstance().getReference();


        initView();

        DataRef.child(AppConstant.FIREBASE_TABLE_FOLLOWERS).removeValue();
        DataRef.child(AppConstant.FIREBASE_TABLE_MENTOR).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot list : dataSnapshot.getChildren()) {
                    if (list.child(AppConstant.FIREBASE_TABLE_EMAIL).getValue().equals(auth.getCurrentUser().getEmail())) {

                    } else {

                        FollowingModel followingModel = new FollowingModel(list.child(AppConstant.FIREBASE_TABLE_FULLNAME).getValue().toString(), list.child(AppConstant.FIREBASE_INDUSTRY).getValue().toString(), false, false);

                        DataRef.child(AppConstant.FIREBASE_TABLE_FOLLOWERS).child(DataRef.push().getKey()).setValue(followingModel).addOnCompleteListener(ProfileActivity.this, new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                            }
                        });
                    }
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        Intent intent = getIntent();

        if (intent.hasExtra("KEY_LOGIN_TYPE")) {
            loginType = intent.getStringExtra("KEY_LOGIN_TYPE");

            if (loginType.equals(AppConstant.FIREBASE_TABLE_STUDNET)) {

                profileExperienceTv.setVisibility(View.INVISIBLE);
                profileEducationTv.setVisibility(View.INVISIBLE);
                profileMentorTypeTv.setVisibility(View.INVISIBLE);
                profileMentorSkillTv.setVisibility(View.INVISIBLE);

                DataRef.child(loginType)
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {


                                final String userName = dataSnapshot.child(auth.getCurrentUser().getUid()).child(AppConstant.FIREBASE_TABLE_FULLNAME).getValue().toString();
                                final String userEmail = dataSnapshot.child(auth.getCurrentUser().getUid()).child(AppConstant.FIREBASE_TABLE_EMAIL).getValue().toString();
//                                final String department = dataSnapshot.child(auth.getCurrentUser().getUid()).child(AppConstant.FIREBASE_DEPARTMENT).getValue().toString();
                                profileNameTv.setText(dataSnapshot.child(auth.getCurrentUser().getUid()).child(AppConstant.FIREBASE_TABLE_FULLNAME).getValue().toString());
                                profileEmailTv.setText(dataSnapshot.child(auth.getCurrentUser().getUid()).child(AppConstant.FIREBASE_TABLE_EMAIL).getValue().toString());
                                profileMobileTv.setText(dataSnapshot.child(auth.getCurrentUser().getUid()).child(AppConstant.FIREBASE_TABLE_MOBILE).getValue().toString());
                                profileIndustryTv.setText(dataSnapshot.child(auth.getCurrentUser().getUid()).child(AppConstant.FIREBASE_INDUSTRY).getValue().toString());
                                Bitmap src;
                                String imgBase64 = dataSnapshot.child(auth.getCurrentUser().getUid()).child("avatar").getValue().toString();
                                byte[] decodedString = Base64.decode(imgBase64, Base64.DEFAULT);
                                src = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                                profileAvatarImg.setImageDrawable(ImageUtils.roundedImage(ProfileActivity.this, src));

                            }

                            @Override
                            public void onCancelled(DatabaseError error) {
                                // Failed to read value
                                Log.e("TAG", "Failed to read value.", error.toException());
                            }
                        });

            } else if (loginType.equals(AppConstant.FIREBASE_TABLE_MENTOR)) {

//                profileFollwingBtn.setVisibility(View.INVISIBLE);
                profileFollwerBtn.setVisibility(View.INVISIBLE);

                DataRef.child(loginType)
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {


                                final String userName = dataSnapshot.child(auth.getCurrentUser().getUid()).child(AppConstant.FIREBASE_TABLE_FULLNAME).getValue().toString();
                                final String userEmail = dataSnapshot.child(auth.getCurrentUser().getUid()).child(AppConstant.FIREBASE_TABLE_EMAIL).getValue().toString();
//                                final String department = dataSnapshot.child(auth.getCurrentUser().getUid()).child(AppConstant.FIREBASE_DEPARTMENT).getValue().toString();
                                profileNameTv.setText(dataSnapshot.child(auth.getCurrentUser().getUid()).child(AppConstant.FIREBASE_TABLE_FULLNAME).getValue().toString());
                                profileEmailTv.setText(dataSnapshot.child(auth.getCurrentUser().getUid()).child(AppConstant.FIREBASE_TABLE_EMAIL).getValue().toString());
                                profileMobileTv.setText(dataSnapshot.child(auth.getCurrentUser().getUid()).child(AppConstant.FIREBASE_TABLE_MOBILE).getValue().toString());
                                profileIndustryTv.setText(dataSnapshot.child(auth.getCurrentUser().getUid()).child(AppConstant.FIREBASE_INDUSTRY).getValue().toString());
                                Bitmap src;
                                String imgBase64 = dataSnapshot.child(auth.getCurrentUser().getUid()).child("avatar").getValue().toString();
                                byte[] decodedString = Base64.decode(imgBase64, Base64.DEFAULT);
                                src = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                                profileAvatarImg.setImageDrawable(ImageUtils.roundedImage(ProfileActivity.this, src));

                            }

                            @Override
                            public void onCancelled(DatabaseError error) {
                                // Failed to read value
                                Log.e("TAG", "Failed to read value.", error.toException());
                            }
                        });

                DataRef.child(AppConstant.FIREBASE_TABLE_MENTOR_DETAILS).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        profileMentorSkillTv.setText(dataSnapshot.child(auth.getCurrentUser().getUid()).child("skill").getValue().toString());
                        profileExperienceTv.setText(dataSnapshot.child(auth.getCurrentUser().getUid()).child(AppConstant.FIREBASE_TABLE_EXPERIENCE).getValue().toString());
                        profileEducationTv.setText(dataSnapshot.child(auth.getCurrentUser().getUid()).child(AppConstant.FIREBASE_TABLE_EDUCATION).getValue().toString());
                        profileMentorTypeTv.setText(dataSnapshot.child(auth.getCurrentUser().getUid()).child(AppConstant.FIREBASE_TABLE_MENTOR_TYPE).getValue().toString());

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

        }

    }

    private void initView() {

        profileAvatarImg = findViewById(R.id.activity_profile_user_avatar_img);
        profileNameTv = findViewById(R.id.activity_profile_name);
        profileEmailTv = findViewById(R.id.activity_profile_email);
        profileMobileTv = findViewById(R.id.activity_profile_mobile);
        profileIndustryTv = findViewById(R.id.activity_profile_industry);
        profileExperienceTv = findViewById(R.id.activity_profile_experience);
        profileEducationTv = findViewById(R.id.activity_profile_education);
        profileMentorTypeTv = findViewById(R.id.activity_profile_mentor_type);
        profileMentorSkillTv = findViewById(R.id.activity_profile_mentor_skill_tv);

//        profileFollwingBtn = findViewById(R.id.activity_profile_following_btn);
        profileFollwerBtn = findViewById(R.id.activity_profile_follower_btn);


//        profileFollwingBtn.setOnClickListener(this);
        profileFollwerBtn.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
//            case R.id.activity_profile_following_btn:
//                final Intent gotoFOllowIntent = new Intent(ProfileActivity.this, FollowingFollowerActivity.class);
//                startActivity(gotoFOllowIntent);
//                break;
            case R.id.activity_profile_follower_btn:
                final Intent gotoFollowerIntent = new Intent(ProfileActivity.this, FollowingFollowerActivity.class);
                startActivity(gotoFollowerIntent);
                break;
        }
    }
}
