package com.example.mentor;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class PostDetailsActivity extends AppCompatActivity implements View.OnClickListener, PostReviewAdapter.ItemListener {


    private ImageView fileThumbImg;
    private TextView fileNameTv;
    private TextView industryTv;
    private Button downloadFileBtn;
    private Button likeFileBtn;
    private EditText postCommentEd;
    private Button postCommentBtn;

    private Intent intent;
    private String postPushKey;
    private String downloadUrl;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private ArrayList<PostReviewModel> postReviewModelArrayList;
    private PostReviewAdapter postReviewAdapter;
    private RecyclerView reviewRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_details);
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();


        initView();

        intent = getIntent();


        downloadUrl = intent.getStringExtra("KEY_FILE_URL");
        postPushKey = intent.getStringExtra("KEY_POST");
        Glide.with(PostDetailsActivity.this).load(intent.getStringExtra("KEY_FILE_THUMB"))
                .placeholder(R.drawable.no_img)
                .crossFade()
                .error(android.R.color.holo_red_light)
                .fallback(android.R.color.holo_orange_light)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(fileThumbImg);

        fileNameTv.setText(intent.getStringExtra("KEY_FILE_NAME"));
        industryTv.setText(intent.getStringExtra("KEY_INDUSTRY"));

        postReviewAdapter = new PostReviewAdapter(PostDetailsActivity.this, postReviewModelArrayList, this);

        reviewRecyclerView.setNestedScrollingEnabled(false);
        reviewRecyclerView.setAdapter(postReviewAdapter);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(PostDetailsActivity.this, LinearLayoutManager.VERTICAL, false);
        reviewRecyclerView.setLayoutManager(layoutManager);
        reviewRecyclerView.addItemDecoration(new DividerItemDecoration(PostDetailsActivity.this,
                DividerItemDecoration.VERTICAL));

        databaseReference.child(AppConstant.FIREBASE_TABLE_POST_LIKES).child(postPushKey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot likeList : dataSnapshot.getChildren()) {
                    if (likeList.child("likerUser").getValue().equals(firebaseAuth.getCurrentUser().getUid()))
                    {
                        likeFileBtn.setEnabled(false);
                        likeFileBtn.setBackgroundColor(Color.LTGRAY);
                    }
                }

                likeFileBtn.setText(dataSnapshot.getChildrenCount() + " Likes");

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        databaseReference.child(AppConstant.FIREBASE_TABLE_POST_REVIEW).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (postReviewModelArrayList.size() > 0) {
                    postReviewModelArrayList.clear();
                }
                for (DataSnapshot bookReviewList : dataSnapshot.getChildren()) {

                    if (bookReviewList.child("postPushKey").getValue().equals(postPushKey)) {

                        PostReviewModel postReviewModel = new PostReviewModel();
                        postReviewModel.setReviewAuthor(bookReviewList.child("reviewAuthor").getValue().toString());
                        postReviewModel.setReviewComment(bookReviewList.child("reviewComment").getValue().toString());
                        postReviewModelArrayList.add(postReviewModel);
                        postReviewAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void initView() {
        fileThumbImg = findViewById(R.id.activity_book_details_thumb_img);
        fileNameTv = findViewById(R.id.activity_book_details_book_name_tv);
        industryTv = findViewById(R.id.activity_book_details_book_category);
        downloadFileBtn = findViewById(R.id.activity_book_details_online_read_btn);
        likeFileBtn = findViewById(R.id.activity_book_details_download_btn);
        postCommentEd = findViewById(R.id.activity_book_details_review_ed);
        postCommentBtn = findViewById(R.id.activity_book_details_review_btn);
        reviewRecyclerView = findViewById(R.id.activity_book_details_review_rc);

        postReviewModelArrayList = new ArrayList<>();


        postCommentBtn.setOnClickListener(this);
        downloadFileBtn.setOnClickListener(this);
        likeFileBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.activity_book_details_online_read_btn:
                downloadFile();
                break;
            case R.id.activity_book_details_review_btn:
                postComment();
                break;
            case R.id.activity_book_details_download_btn:
                likePost();
                break;
        }
    }

    private void likePost() {

        PostLikeModel postLikeModel = new PostLikeModel(firebaseAuth.getCurrentUser().getUid());
        databaseReference.child(AppConstant.FIREBASE_TABLE_POST_LIKES).child(postPushKey).push().setValue(postLikeModel).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {


            }
        });
    }

    private void downloadFile() {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(downloadUrl));
        startActivity(intent);
    }

    private void postComment() {
        final String bookReview = postCommentEd.getText().toString().trim();

        if (bookReview.isEmpty()) {
            Toast.makeText(this, "Please Enter Review", Toast.LENGTH_SHORT).show();
        } else {
            final String reviewAuthor = firebaseAuth.getCurrentUser().getEmail().replace("@gmail.com", "");
            PostReviewModel postReviewModel = new PostReviewModel(postPushKey, reviewAuthor, bookReview);
            postReviewModelArrayList.add(postReviewModel);
            postReviewAdapter.notifyDataSetChanged();

            databaseReference.child(AppConstant.FIREBASE_TABLE_POST_REVIEW).push().setValue(postReviewModel).addOnCompleteListener(PostDetailsActivity.this, new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                    Toast.makeText(PostDetailsActivity.this, "Thank You for review", Toast.LENGTH_SHORT).show();
                    postCommentEd.setText("");
                }
            });
        }

    }

    @Override
    public void onItemClick(PostReviewModel item) {

    }
}
