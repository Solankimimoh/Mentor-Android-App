package com.example.mentor;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.yarolegovich.lovelydialog.LovelyInfoDialog;
import com.yarolegovich.lovelydialog.LovelyProgressDialog;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.regex.Pattern;

import static com.basgeekball.awesomevalidation.ValidationStyle.BASIC;

public class MentorRegistrationActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    //    Component
    private EditText fulllnameEd;
    private EditText emailEd;
    private EditText passwordEd;
    private EditText mobileEd;
    private Spinner departmnetSp;
    private TextView gotoLogin;
    private Button facultySignupBtn;
    private ImageView mentorAvatarImg;
    private LovelyProgressDialog waitingDialog;


    //    Faculty String Variable

    private String fullname;
    private String email;
    private String password;
    private String mobile;
    private String department;
    private String profileData;
    private boolean profilePic = false;

    //    Firebase
    private FirebaseAuth auth;
    private DatabaseReference mDatabase;
    private ProgressDialog progressDialog;
    AwesomeValidation mAwesomeValidation = new AwesomeValidation(BASIC);
    private static final int PICK_IMAGE = 1994;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faculty_registration);

        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();

        // Write a message to the database
        mDatabase = FirebaseDatabase.getInstance().getReference(AppConstant.FIREBASE_TABLE_MENTOR);

        initView();
        initValidationRules();


    }

    private void initValidationRules() {

        String regexPassword = "(?=.*[a-z])(?=.*[A-Z])(?=.*[\\d])(?=.*[~`!@#\\$%\\^&\\*\\(\\)\\-_\\+=\\{\\}\\[\\]\\|\\;:\"<>,./\\?]).{8,}";
        String TELEPHONE = "(^\\+\\d+)?[0-9\\s()-]*";

        mAwesomeValidation.addValidation(MentorRegistrationActivity.this, R.id.activity_faculty_name_ed, "[a-zA-Z\\s]+", R.string.err_name);
        mAwesomeValidation.addValidation(MentorRegistrationActivity.this, R.id.activit_faculty_email_ed, android.util.Patterns.EMAIL_ADDRESS, R.string.val_err_email);
        mAwesomeValidation.addValidation(MentorRegistrationActivity.this, R.id.activity_faculty_password_ed, regexPassword, R.string.err_password);
        mAwesomeValidation.addValidation(MentorRegistrationActivity.this, R.id.activity_faculty_registration_mobile_ed, TELEPHONE, R.string.err_tel);

    }

    private void initView() {

        mentorAvatarImg = findViewById(R.id.activity_faculty_registration_avtar_img);
        fulllnameEd = findViewById(R.id.activity_faculty_name_ed);
        emailEd = findViewById(R.id.activit_faculty_email_ed);
        passwordEd = findViewById(R.id.activity_faculty_password_ed);
        mobileEd = findViewById(R.id.activity_faculty_registration_mobile_ed);
        departmnetSp = findViewById(R.id.activity_faculty_registration_department_spinner);
        gotoLogin = findViewById(R.id.activity_faculty_registration_already);
        facultySignupBtn = findViewById(R.id.activity_faculty_registration_btn);
        waitingDialog = new LovelyProgressDialog(MentorRegistrationActivity.this);


        //        Listener Initilization

        facultySignupBtn.setOnClickListener(this);
        gotoLogin.setOnClickListener(this);
        departmnetSp.setOnItemSelectedListener(this);
        mentorAvatarImg.setOnClickListener(this);

        //        Progressbar Dailog

        progressDialog = new ProgressDialog(MentorRegistrationActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Signup");
        progressDialog.setMessage("Creating account");
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.activity_faculty_registration_already:
                finish();
                break;
            case R.id.activity_faculty_registration_btn:
                signupFaculty();
                break;
            case R.id.activity_faculty_registration_avtar_img:
                getProfilePic();

                break;
        }
    }

    private void getProfilePic() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_PICK);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK) {
            if (data == null) {
                Toast.makeText(MentorRegistrationActivity.this, "Not Choosed ", Toast.LENGTH_LONG).show();
                return;
            }
            try {
                InputStream inputStream = getContentResolver().openInputStream(data.getData());

                Bitmap imgBitmap = BitmapFactory.decodeStream(inputStream);
                imgBitmap = ImageUtils.cropToSquare(imgBitmap);
                InputStream is = ImageUtils.convertBitmapToInputStream(imgBitmap);
                final Bitmap liteImage = ImageUtils.makeImageLite(is,
                        imgBitmap.getWidth(), imgBitmap.getHeight(),
                        ImageUtils.AVATAR_WIDTH, ImageUtils.AVATAR_HEIGHT);

                String imageBase64 = ImageUtils.encodeBase64(liteImage);
                profileData = imageBase64;

                waitingDialog.setCancelable(false)
                        .setTitle("Avatar updating....")
                        .setTopColorRes(R.color.colorPrimary)
                        .show();

//                SharedPreferenceHelper preferenceHelper = SharedPreferenceHelper.getInstance(context);
//                preferenceHelper.saveUserInfo(myAccount);
                mentorAvatarImg.setImageDrawable(ImageUtils.roundedImage(MentorRegistrationActivity.this, liteImage));
                waitingDialog.dismiss();
                new LovelyInfoDialog(MentorRegistrationActivity.this)
                        .setTopColorRes(R.color.colorPrimary)
                        .setTitle("Success")
                        .setMessage("Update avatar successfully!")
                        .show();
                profilePic = true;


            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }


    private boolean isValidMobile(String phone) {
        boolean check = false;
        if (!Pattern.matches("[a-zA-Z]+", phone)) {
            if (phone.length() < 6 || phone.length() > 13) {
                // if(phone.length() != 10) {
                check = false;
                mobileEd.setError("Not Valid Number");
            } else {
                check = true;
            }
        } else {
            check = false;
        }
        return check;
    }

    private void signupFaculty() {

        fullname = fulllnameEd.getText().toString().trim();
        email = emailEd.getText().toString().trim();
        password = passwordEd.getText().toString().trim();
        mobile = mobileEd.getText().toString().trim();


        if (mAwesomeValidation.validate()) {

            if (isValidMobile(mobile)) {
//                progressDialog.show();
                waitingDialog.setCancelable(false)
                        .setTitle("Creating Profile..")
                        .setTopColorRes(R.color.colorPrimary)
                        .show();
                if (profilePic) {

                    auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(MentorRegistrationActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            Log.e("TAG", "CREATE USER");
                            if (!task.isSuccessful()) {
                                if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                    Toast.makeText(MentorRegistrationActivity.this, "User with this email already exist.", Toast.LENGTH_SHORT).show();
                                    progressDialog.dismiss();
                                }
                            } else {
                                String userId = auth.getCurrentUser().getUid();
                                Log.e("TAG", "INSERT USER");
                                mDatabase.child(userId).setValue(new UserModel(fullname,
                                        email,
                                        password,
                                        mobile,
                                        department,
                                        true
                                        , profileData), new DatabaseReference.CompletionListener() {
                                    @Override
                                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                        if (databaseError != null) {
                                            Toast.makeText(MentorRegistrationActivity.this, databaseError.toString(), Toast.LENGTH_SHORT).show();
                                        } else {
                                            waitingDialog.dismiss();
                                            Toast.makeText(MentorRegistrationActivity.this, "Next Step !", Toast.LENGTH_SHORT).show();
                                            Intent gotoNextMentorInsertDetails = new Intent(MentorRegistrationActivity.this, MentorDetailsInsertActivity.class);
                                            startActivity(gotoNextMentorInsertDetails);
                                            progressDialog.dismiss();
                                            finish();
                                        }
                                    }
                                });
                            }
                        }
                    });
                } else {
                    waitingDialog.dismiss();
                    new LovelyInfoDialog(MentorRegistrationActivity.this)
                            .setTopColorRes(R.color.colorPrimary)
                            .setTitle("Profile pic")
                            .setMessage("Profile pic not set..")
                            .show();
                }

            }


        }


    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        department = adapterView.getSelectedItem().toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
