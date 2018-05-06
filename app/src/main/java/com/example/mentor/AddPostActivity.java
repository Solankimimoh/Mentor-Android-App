package com.example.mentor;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.shockwave.pdfium.PdfDocument;
import com.shockwave.pdfium.PdfiumCore;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

public class AddPostActivity extends AppCompatActivity implements View.OnClickListener {

    //This is the pic pdf code used in file chooser
    final static int PICK_PDF_CODE = 2342;

    private EditText postTitleEd;
    private EditText postDescriptionEd;
    private EditText postImageNameEd;
    private Button postChooseFileBtn;
    private Button postSendPostBtn;
    private TextView postFileNameTv;
    private TextView postUploadStatusTv;
    private ProgressBar postUploadProgressBar;
    private TextView textViewStatus;


    private String industryName;
    private String userName;
    private String userAvatar;

    //The firebase objects for storage and database
    private StorageReference mStorageReference;
    private DatabaseReference mDatabase;
    private FirebaseAuth firebaseAuth;
    private Uri selectedFileIntent;
    private Uri bookThumb;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        mStorageReference = FirebaseStorage.getInstance().getReference();
        initView();

        final Intent intent = getIntent();

        if (intent.hasExtra("KEY_INDUSTRY") && intent.hasExtra("KEY_USERNAME")) {
            industryName = intent.getStringExtra("KEY_INDUSTRY");
            userName = intent.getStringExtra("KEY_USERNAME");
            userAvatar = intent.getStringExtra("KEY_USER_AVATAR");
        }
    }


    private void initView() {

//        textViewStatus = findViewById(R.id.activity_add_post_upload_file_status_tv);
        postTitleEd = findViewById(R.id.activity_add_post_title_ed);
        postDescriptionEd = findViewById(R.id.activity_add_post_description_ed);
        postImageNameEd = findViewById(R.id.activity_send_notification_file_name_ed);
        postChooseFileBtn = findViewById(R.id.activity_add_post_choose_file_btn);
        postSendPostBtn = findViewById(R.id.activity_add_post_send_btn);
        postFileNameTv = findViewById(R.id.activity_add_post_file_path_tv);
        postUploadStatusTv = findViewById(R.id.activity_add_post_upload_file_status_tv);
        postUploadProgressBar = findViewById(R.id.notification_progressbar);


        postChooseFileBtn.setOnClickListener(this);
        postSendPostBtn.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.activity_add_post_choose_file_btn:
                choosePostImageFile();
                break;
            case R.id.activity_add_post_send_btn:
                sendPost();
                break;
        }
    }

    private void sendPost() {
        if (selectedFileIntent != null) {
            uploadFile(selectedFileIntent);
        }
    }

    private void choosePostImageFile() {
//for greater than lolipop versions we need the permissions asked on runtime
        //so if the permission is not available user will go to the screen to allow storage permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            return;
        } //creating an intent for file chooser

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            return;
        } //creating an intent for file chooser

        Intent intent = new Intent();
        intent.setType("*/*");
        String[] mimetypes = {"image/*", "application/pdf"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimetypes);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select File"), PICK_PDF_CODE);
    }

    void generateImageFromPdf(Uri pdfUri) {
        int pageNumber = 0;
        PdfiumCore pdfiumCore = new PdfiumCore(this);
        try {
            //http://www.programcreek.com/java-api-examples/index.php?api=android.os.ParcelFileDescriptor
            ParcelFileDescriptor fd = getContentResolver().openFileDescriptor(pdfUri, "r");
            PdfDocument pdfDocument = pdfiumCore.newDocument(fd);
            pdfiumCore.openPage(pdfDocument, pageNumber);
            int width = pdfiumCore.getPageWidthPoint(pdfDocument, pageNumber);
            int height = pdfiumCore.getPageHeightPoint(pdfDocument, pageNumber);
            Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            pdfiumCore.renderPageBitmap(pdfDocument, bmp, pageNumber, 0, 0, width, height);
            saveImage(bmp);
            pdfiumCore.closeDocument(pdfDocument); // important!
        } catch (Exception e) {
            //todo with exception
        }
    }

    public final static String FOLDER = Environment.getExternalStorageDirectory() + "/PDF";

    private void saveImage(Bitmap bmp) {
        Log.e("LOCATION", FOLDER);
        FileOutputStream out = null;
        try {
            File folder = new File(FOLDER);
            if (!folder.exists())
                folder.mkdirs();
            File file = new File(folder, random() + ".png");
            out = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 100, out); // bmp is your Bitmap instance

            bookThumb = Uri.fromFile(file);
//            uploadThumbFile(Uri.fromFile(file));

        } catch (Exception e) {
            //todo with exception
        } finally {
            try {
                if (out != null)
                    out.close();
            } catch (Exception e) {
                //todo with exception
            }
        }
    }

    @NonNull
    public static String random() {
        final String ALLOWED_CHARACTERS = "0123456789qwertyuiopasdfghjklzxcvbnm";

        final Random random = new Random();
        final StringBuilder sb = new StringBuilder(5);
        for (int i = 0; i < 5; ++i)
            sb.append(ALLOWED_CHARACTERS.charAt(random.nextInt(ALLOWED_CHARACTERS.length())));
        return sb.toString();
    }


    //this method is uploading the file
    //the code is same as the previous tutorial
    //so we are not explaining it
    private void uploadFile(final Uri data) {
        postUploadProgressBar.setVisibility(View.VISIBLE);

        ContentResolver cR = getContentResolver();
        String mime = cR.getType(data);

        Toast.makeText(this, "" + mime, Toast.LENGTH_SHORT).show();

        final String userNamePost = firebaseAuth.getCurrentUser().getEmail().replace("@gmail.com", "");

        if (mime.equals("image/jpeg") || mime.equals("image/png")) {

            Toast.makeText(this, "Image", Toast.LENGTH_SHORT).show();
            final StorageReference sref = FirebaseStorage.getInstance().getReference().child(AppConstant.FIREBASE_STORAGE_PATH + System.currentTimeMillis());

            sref.putFile(data).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    final String title = postTitleEd.getText().toString().trim();
                    final String description = postDescriptionEd.getText().toString().trim();
                    final String filename = postImageNameEd.getText().toString().trim();
                    final String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

                    AddPostModel addPostModel = new AddPostModel(title, description, filename, taskSnapshot.getDownloadUrl().toString(), taskSnapshot.getDownloadUrl().toString(), userNamePost, industryName, date, "", userAvatar);

                    mDatabase.child(AppConstant.FIREBASE_TABLE_POSTS).child(mDatabase.push().getKey()).setValue(addPostModel, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            if (databaseError != null) {
                                Toast.makeText(AddPostActivity.this, databaseError.toString(), Toast.LENGTH_SHORT).show();
                            } else {
                                sendFCMPush();
                                finish();
                            }
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    Log.e("UPLOAD", exception.getMessage());
                    Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_LONG).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @SuppressWarnings("VisibleForTests")
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                    double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                    Log.e("TAG UPLOAD", taskSnapshot.getBytesTransferred() + "  " + progress + "====" + (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount());
                    postUploadStatusTv.setText("" + progress + "% Uploading...");
                }
            });


        } else if (mime.equals("application/pdf")) {
            Toast.makeText(this, "PDF FILE", Toast.LENGTH_SHORT).show();
            generateImageFromPdf(data);
            final StorageReference sref = FirebaseStorage.getInstance().getReference().child(AppConstant.FIREBASE_STORAGE_PATH + System.currentTimeMillis());

            sref.putFile(data).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    final String fileUrl = taskSnapshot.getDownloadUrl().toString();

                    final StorageReference sRefimg = FirebaseStorage.getInstance().getReference().child(AppConstant.FIREBASE_STORAGE_PATH + System.currentTimeMillis() + ".png");
                    sRefimg.putFile(bookThumb).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot filethumb) {

                            final String filethumburl = filethumb.getDownloadUrl().toString();

                            final String title = postTitleEd.getText().toString().trim();
                            final String description = postDescriptionEd.getText().toString().trim();
                            final String filename = postImageNameEd.getText().toString().trim();
                            final String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

                            AddPostModel addPostModel = new AddPostModel(title, description, filename, fileUrl, filethumburl, userNamePost, industryName, date, "", userAvatar);

                            mDatabase.child(AppConstant.FIREBASE_TABLE_POSTS).child(mDatabase.push().getKey()).setValue(addPostModel, new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                    if (databaseError != null) {
                                        Toast.makeText(AddPostActivity.this, databaseError.toString(), Toast.LENGTH_SHORT).show();
                                    } else {
                                        sendFCMPush();
                                        finish();
                                    }
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            Log.e("UPLOAD", exception.getMessage());
                            Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @SuppressWarnings("VisibleForTests")
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                            double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                            Log.e("TAG UPLOAD", taskSnapshot.getBytesTransferred() + "  " + progress + "====" + (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount());
                            postUploadStatusTv.setText("" + progress + "% Uploading...");
                        }
                    });


                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    Log.e("UPLOAD", exception.getMessage());
                    Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_LONG).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @SuppressWarnings("VisibleForTests")
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                    double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                    Log.e("TAG UPLOAD", taskSnapshot.getBytesTransferred() + "  " + progress + "====" + (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount());
                    postUploadStatusTv.setText("" + progress + "% Uploading...");
                }
            });
            ;
        }


//        final StorageReference sRef = FirebaseStorage.getInstance().getReference().child(AppConstant.FIREBASE_STORAGE_PATH + System.currentTimeMillis());
//        sRef.putFile(data)
//                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                    @SuppressWarnings("VisibleForTests")
//                    @Override
//                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                        postUploadProgressBar.setVisibility(View.GONE);
//                        postUploadStatusTv.setText("File upload successfully");
//                        getContentResolver().getType(data);
//
//                        final String title = postTitleEd.getText().toString().trim();
//                        final String description = postDescriptionEd.getText().toString().trim();
//                        final String filename = postImageNameEd.getText().toString().trim();
//                        final String fileUrl = taskSnapshot.getDownloadUrl().toString();
//                        final String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
//
//
//                        AddPostModel addPostModel = new AddPostModel(title, description, filename, fileUrl, userName, industryName, date);
//
//                        mDatabase.child(AppConstant.FIREBASE_TABLE_POSTS).child(mDatabase.push().getKey()).setValue(addPostModel, new DatabaseReference.CompletionListener() {
//                            @Override
//                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
//                                if (databaseError != null) {
//                                    Toast.makeText(AddPostActivity.this, databaseError.toString(), Toast.LENGTH_SHORT).show();
//                                } else {
//                                    sendFCMPush();
//                                    finish();
//                                }
//                            }
//                        });
//
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception exception) {
//                        Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_LONG).show();
//                    }
//                })
//                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
//                    @SuppressWarnings("VisibleForTests")
//                    @Override
//                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
//
//                        double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
//                        Log.e("TAG UPLOAD", taskSnapshot.getBytesTransferred() + "  " + progress + "====" + (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount());
//                        postUploadStatusTv.setText("" + progress + "% Uploading...");
//                    }
//                });

    }

    private void sendFCMPush() {

        final String Legacy_SERVER_KEY = "AIzaSyBNcLw8JdbE8OERrFjfVXLAsTT5_Siqo14";
        String msg = postTitleEd.getText().toString().trim();
        String title = postDescriptionEd.getText().toString().trim();
        //  String token = FCM_RECEIVER_TOKEN;

        JSONObject obj = null;
        JSONObject objData = null;
        JSONObject dataobjData = null;

        try {
            obj = new JSONObject();
            objData = new JSONObject();

            objData.put("body", msg);
            objData.put("title", title);
            objData.put("sound", "default");
            objData.put("icon", "icon_name"); //   icon_name image must be there in drawable
            // objData.put("tag", token);

            objData.put("priority", "high");

            dataobjData = new JSONObject();
            dataobjData.put("text", msg);
            dataobjData.put("title", title);

            final String topicname = industryName.replace(" ", "_");

            obj.put("to", "/topics/" + topicname);
            //obj.put("to", token);
            obj.put("priority", "high");

            obj.put("notification", objData);
            obj.put("data", dataobjData);
            Log.e("!_@rj@_@@_PASS:>", obj.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST, "https://fcm.googleapis.com/fcm/send", obj,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e("!_@@_SUCESS", response + "");
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("!_@@_Errors--", error + "");
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", "key=" + Legacy_SERVER_KEY);
                params.put("Content-Type", "application/json");
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        int socketTimeout = 1000 * 60;// 60 seconds
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsObjRequest.setRetryPolicy(policy);
        requestQueue.add(jsObjRequest);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //when the user choses the file
        if (requestCode == PICK_PDF_CODE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            //if a file is selected
            if (data.getData() != null) {
                //uploading the file
                File file = new File(data.getData().getPathSegments().toString());
                postFileNameTv.setText(file.getName().substring(0, file.getName().length() - 1));
                selectedFileIntent = data.getData();
            } else {
                Toast.makeText(this, "No file chosen", Toast.LENGTH_SHORT).show();
            }
        }
    }


}
