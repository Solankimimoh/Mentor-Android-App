package com.example.mentor;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.customtabs.CustomTabsIntent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, RecyclerViewAdapter.ItemListener, AddPostAdapter.ItemListener {

    //    Component Initlization
    private TextView userNameTv;
    private TextView userEmailTv;
    private ImageView profileAvatarImg;
    private RecyclerView recyclerView;
    private FloatingActionButton addPostFloatingActionButton;
    private ArrayList<HomeMenuItemModel> arrayList;
    private ArrayList<AddPostModel> addPostModelArrayList;
    private AddPostAdapter addPostAdapter;
    private ProgressDialog progressDialog;

    //    Firebase Init
    private DatabaseReference DataRef;
    private FirebaseAuth auth;


    //    variable
    private String loginType;
    private String userName;
    private String loginTypeString;
    private String industryName;
    private String postPushKey;
    private String postUserAvatar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        auth = FirebaseAuth.getInstance();
        DataRef = FirebaseDatabase.getInstance().getReference();

        initView();

        final Intent intent = getIntent();


        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0);

        SharedPreferences.Editor editor = pref.edit();

        loginType = pref.getString("KEY_LOGIN_TYPE", null);

        if (loginType != null) {

            Log.e("LOGIN", loginType);
            if (loginType.equals(AppConstant.FIREBASE_TABLE_STUDNET)) {

                navigationView.getMenu().clear();
                navigationView.inflateMenu(R.menu.student_menu);

                DataRef.child(loginType)
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                addPostFloatingActionButton.setVisibility(View.INVISIBLE);

                                Toast.makeText(HomeActivity.this, "Welcome " + dataSnapshot.child(auth.getCurrentUser().getUid()).child(AppConstant.FIREBASE_TABLE_FULLNAME).getValue().toString(), Toast.LENGTH_SHORT).show();
                                final String userName = dataSnapshot.child(auth.getCurrentUser().getUid()).child(AppConstant.FIREBASE_TABLE_FULLNAME).getValue().toString();
                                final String userEmail = dataSnapshot.child(auth.getCurrentUser().getUid()).child(AppConstant.FIREBASE_TABLE_EMAIL).getValue().toString();
                                industryName = dataSnapshot.child(auth.getCurrentUser().getUid()).child(AppConstant.FIREBASE_INDUSTRY).getValue().toString();
//                                final String department = dataSnapshot.child(auth.getCurrentUser().getUid()).child(AppConstant.FIREBASE_DEPARTMENT).getValue().toString();
                                userNameTv.setText(userName);
                                userEmailTv.setText(userEmail);
                                Bitmap src;
                                postUserAvatar = dataSnapshot.child(auth.getCurrentUser().getUid()).child("avatar").getValue().toString();
                                String imgBase64 = dataSnapshot.child(auth.getCurrentUser().getUid()).child("avatar").getValue().toString();
                                byte[] decodedString = Base64.decode(imgBase64, Base64.DEFAULT);
                                src = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                                profileAvatarImg.setImageDrawable(ImageUtils.roundedImage(HomeActivity.this, src));

                            }

                            @Override
                            public void onCancelled(DatabaseError error) {
                                // Failed to read value
                                Log.e("TAG", "Failed to read value.", error.toException());
                            }
                        });
            } else if (loginType.equals(AppConstant.FIREBASE_TABLE_MENTOR)) {


                DataRef.child(loginType)
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                addPostFloatingActionButton.setVisibility(View.VISIBLE);

                                industryName = dataSnapshot.child(auth.getCurrentUser().getUid()).child(AppConstant.FIREBASE_INDUSTRY).getValue().toString();
                                userNameTv.setText(dataSnapshot.child(auth.getCurrentUser().getUid()).child(AppConstant.FIREBASE_TABLE_FULLNAME).getValue().toString());
                                userEmailTv.setText(dataSnapshot.child(auth.getCurrentUser().getUid()).child(AppConstant.FIREBASE_TABLE_EMAIL).getValue().toString());
                                Bitmap src;
                                postUserAvatar = dataSnapshot.child(auth.getCurrentUser().getUid()).child("avatar").getValue().toString();

                                String imgBase64 = dataSnapshot.child(auth.getCurrentUser().getUid()).child("avatar").getValue().toString();
                                byte[] decodedString = Base64.decode(imgBase64, Base64.DEFAULT);
                                src = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                                profileAvatarImg.setImageDrawable(ImageUtils.roundedImage(HomeActivity.this, src));


                            }

                            @Override
                            public void onCancelled(DatabaseError error) {
                                // Failed to read value
                                Log.e("TAG", "Failed to read value.", error.toException());
                            }
                        });
            }

        }


        if (intent.hasExtra("KEY_LOGIN_TYPE")) {

            loginType = intent.getStringExtra("KEY_LOGIN_TYPE");


            if (loginType.equals(AppConstant.FIREBASE_TABLE_STUDNET)) {

                navigationView.getMenu().clear();
                navigationView.inflateMenu(R.menu.student_menu);
                DataRef.child(loginType)
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                addPostFloatingActionButton.setVisibility(View.INVISIBLE);

                                Toast.makeText(HomeActivity.this, "Welcome " + dataSnapshot.child(auth.getCurrentUser().getUid()).child(AppConstant.FIREBASE_TABLE_FULLNAME).getValue().toString(), Toast.LENGTH_SHORT).show();
                                final String userName = dataSnapshot.child(auth.getCurrentUser().getUid()).child(AppConstant.FIREBASE_TABLE_FULLNAME).getValue().toString();
                                final String userEmail = dataSnapshot.child(auth.getCurrentUser().getUid()).child(AppConstant.FIREBASE_TABLE_EMAIL).getValue().toString();
                                industryName = dataSnapshot.child(auth.getCurrentUser().getUid()).child(AppConstant.FIREBASE_INDUSTRY).getValue().toString();
//                                final String department = dataSnapshot.child(auth.getCurrentUser().getUid()).child(AppConstant.FIREBASE_DEPARTMENT).getValue().toString();
                                userNameTv.setText(userName);
                                userEmailTv.setText(userEmail);
                                Bitmap src;
                                postUserAvatar = dataSnapshot.child(auth.getCurrentUser().getUid()).child("avatar").getValue().toString();

                                String imgBase64 = dataSnapshot.child(auth.getCurrentUser().getUid()).child("avatar").getValue().toString();
                                byte[] decodedString = Base64.decode(imgBase64, Base64.DEFAULT);
                                src = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                                profileAvatarImg.setImageDrawable(ImageUtils.roundedImage(HomeActivity.this, src));

                                final String topicName = industryName.replace(" ", "_");
                                FirebaseMessaging.getInstance().subscribeToTopic(topicName);
                            }

                            @Override
                            public void onCancelled(DatabaseError error) {
                                // Failed to read value
                                Log.e("TAG", "Failed to read value.", error.toException());
                            }
                        });
            } else if (loginType.equals(AppConstant.FIREBASE_TABLE_MENTOR)) {
                loginType = intent.getStringExtra("KEY_LOGIN_TYPE");

                DataRef.child(loginType)
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                addPostFloatingActionButton.setVisibility(View.VISIBLE);


                                Toast.makeText(HomeActivity.this, "Welcome " + dataSnapshot.child(auth.getCurrentUser().getUid()).child(AppConstant.FIREBASE_TABLE_FULLNAME).getValue().toString(), Toast.LENGTH_SHORT).show();
                                userName = dataSnapshot.child(auth.getCurrentUser().getUid()).child(AppConstant.FIREBASE_TABLE_FULLNAME).getValue().toString();
                                final String userEmail = dataSnapshot.child(auth.getCurrentUser().getUid()).child(AppConstant.FIREBASE_TABLE_EMAIL).getValue().toString();
                                industryName = dataSnapshot.child(auth.getCurrentUser().getUid()).child(AppConstant.FIREBASE_INDUSTRY).getValue().toString();
                                userNameTv.setText(userName);
                                userEmailTv.setText(userEmail);
                                Bitmap src;
                                postUserAvatar = dataSnapshot.child(auth.getCurrentUser().getUid()).child("avatar").getValue().toString();

                                String imgBase64 = dataSnapshot.child(auth.getCurrentUser().getUid()).child("avatar").getValue().toString();
                                byte[] decodedString = Base64.decode(imgBase64, Base64.DEFAULT);
                                src = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                                profileAvatarImg.setImageDrawable(ImageUtils.roundedImage(HomeActivity.this, src));


                                final String topicName = industryName.replace(" ", "_");
                                FirebaseMessaging.getInstance().subscribeToTopic(topicName);

                            }

                            @Override
                            public void onCancelled(DatabaseError error) {
                                // Failed to read value
                                Log.e("TAG", "Failed to read value.", error.toException());
                            }
                        });
            }
        }

        DataRef.child(AppConstant.FIREBASE_TABLE_POSTS).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (!addPostModelArrayList.isEmpty()) {
                    addPostModelArrayList.clear();
                }
                for (DataSnapshot addPostModelDataSnapshot : dataSnapshot.getChildren()) {
                    postPushKey = addPostModelDataSnapshot.getKey();
                    AddPostModel addPostModel = new AddPostModel();
                    addPostModel.setTitle(addPostModelDataSnapshot.child("title").getValue().toString());
                    addPostModel.setDescription(addPostModelDataSnapshot.child("description").getValue().toString());
                    addPostModel.setFilename(addPostModelDataSnapshot.child("filename").getValue().toString());
                    addPostModel.setFileThumburl(addPostModelDataSnapshot.child("fileThumburl").getValue().toString());
                    addPostModel.setFileurl(addPostModelDataSnapshot.child("fileurl").getValue().toString());
                    addPostModel.setUsername(addPostModelDataSnapshot.child("username").getValue().toString());
                    addPostModel.setIndustry(addPostModelDataSnapshot.child("industry").getValue().toString());
                    addPostModel.setPostdate(addPostModelDataSnapshot.child("postdate").getValue().toString());
                    addPostModel.setPostPushKey(addPostModelDataSnapshot.getKey());
                    addPostModel.setPostUserAvatar(addPostModelDataSnapshot.child("postUserAvatar").getValue().toString());
                    Log.e("NAME", addPostModel.getTitle());
                    addPostModelArrayList.add(addPostModel);
                    addPostAdapter.notifyDataSetChanged();


                }
                progressDialog.dismiss();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("ERROR", databaseError.getMessage());
                Toast.makeText(HomeActivity.this, "EROR" + databaseError.getDetails(), Toast.LENGTH_SHORT).show();
            }
        });


        addPostAdapter = new AddPostAdapter(HomeActivity.this, addPostModelArrayList, this);
        recyclerView.setAdapter(addPostAdapter);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);


        addPostFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(HomeActivity.this, "" + industryName, Toast.LENGTH_SHORT).show();
                final Intent gotoAddPost = new Intent(HomeActivity.this, AddPostActivity.class);
                gotoAddPost.putExtra("KEY_INDUSTRY", industryName);
                gotoAddPost.putExtra("KEY_USERNAME", userName);
                gotoAddPost.putExtra("KEY_USER_AVATAR", postUserAvatar);
                startActivity(gotoAddPost);
            }
        });

        final View headerView = navigationView.getHeaderView(0);

        userNameTv = headerView.findViewById(R.id.nav_header_home_username);
        userEmailTv = headerView.findViewById(R.id.nav_header_home_email);
        profileAvatarImg = headerView.findViewById(R.id.user_avtar);


        arrayList = new ArrayList<>();
        arrayList.add(new HomeMenuItemModel(getString(R.string.department), R.drawable.vector_department, "#09A9FF"));
        arrayList.add(new HomeMenuItemModel(getString(R.string.gallery), R.drawable.vector_gallery, "#3E51B1"));
        arrayList.add(new HomeMenuItemModel(getString(R.string.sbifees), R.drawable.vector_fees, "#673BB7"));
        arrayList.add(new HomeMenuItemModel(getString(R.string.notification), R.drawable.vector_notification, "#4BAA50"));
        arrayList.add(new HomeMenuItemModel(getString(R.string.aboutus), R.drawable.vector_about_us, "#F94336"));
        arrayList.add(new HomeMenuItemModel(getString(R.string.contactus), R.drawable.vector_location, "#0A9B88"));

        RecyclerViewAdapter adapter = new RecyclerViewAdapter(this, arrayList, this);
//        recyclerView.setAdapter(adapter);

//        AutoFitGridLayoutManager layoutManager = new AutoFitGridLayoutManager(this, 300);
//        recyclerView.setLayoutManager(layoutManager);


    }

    private void initView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        addPostFloatingActionButton = findViewById(R.id.app_bar_home_addpost_floating);

        progressDialog = new ProgressDialog(HomeActivity.this);
        progressDialog.setTitle("Faculty Details");
        progressDialog.setMessage("Loading.....");
        progressDialog.show();

        addPostModelArrayList = new ArrayList<>();

        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id) {

            case R.id.menu_signout:
                auth.signOut();
                finish();
                break;

            case R.id.menu_profile:
                final Intent gotoProfile = new Intent(HomeActivity.this, ProfileActivity.class);
                gotoProfile.putExtra("KEY_LOGIN_TYPE", loginType);
                startActivity(gotoProfile);
                break;

            case R.id.menu_being_mentor:
                final Intent gotoBeMentor = new Intent(HomeActivity.this, MentorDetailsInsertActivity.class);
                startActivity(gotoBeMentor);
                break;

            case R.id.menu_aboutapp:
                final Intent gotoAboutApp = new Intent(HomeActivity.this, AboutAppActivity.class);
                startActivity(gotoAboutApp);
                break;

            case R.id.menu_developer:
                final Intent gotoDeveloper = new Intent(HomeActivity.this, DeveloperActivity.class);
                startActivity(gotoDeveloper);
                break;

//            case R.id.menu_request:
//                final Intent gotoMentorRequest = new Intent(HomeActivity.this, RequestPostMentorActivity.class);
//                startActivity(gotoMentorRequest);
//                break;


//            case R.id.menu_aprove_student:
//                final Intent gotoStudentRequest = new Intent(HomeActivity.this, StudentReuqestListActivity.class);
//                startActivity(gotoStudentRequest);
//                break;

        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return false;
    }

    @Override
    public void onItemClick(HomeMenuItemModel item) {


    }

    private void openSBITab() {
        String url = "https://www.onlinesbi.com/prelogin/institutiontypedisplay.htm";
        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        builder.setToolbarColor(getResources().getColor(R.color.colorPrimary));
        CustomTabsIntent customTabsIntent = builder.build();
        customTabsIntent.launchUrl(this, Uri.parse(url));
    }


    @Override
    public void onItemClick(AddPostModel item) {

        final Intent gotoBookDetails = new Intent(HomeActivity.this, PostDetailsActivity.class);

        gotoBookDetails.putExtra("KEY_POST", item.getPostPushKey());
        gotoBookDetails.putExtra("KEY_FILE_NAME", item.getFilename());
        gotoBookDetails.putExtra("KEY_INDUSTRY", item.getIndustry());
        gotoBookDetails.putExtra("KEY_FILE_URL", item.getFileurl());
        gotoBookDetails.putExtra("KEY_FILE_THUMB", item.getFileThumburl());

        startActivity(gotoBookDetails);


    }
}