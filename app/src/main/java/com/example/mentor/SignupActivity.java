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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.yarolegovich.lovelydialog.LovelyInfoDialog;
import com.yarolegovich.lovelydialog.LovelyProgressDialog;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.regex.Pattern;

import static com.basgeekball.awesomevalidation.ValidationStyle.BASIC;

public class SignupActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    private EditText fullNameEd;
    private EditText emailEd;
    private EditText pwdEd;
    private EditText enrollmentEd;
    private EditText mobileEd;
    private Spinner industrySp;
    private TextView alreadyRegisteredTv;
    private TextView facultyRegistrationTv;
    private Button signupBtn;
    private ImageView studentAvtarImg;
    private LovelyProgressDialog waitingDialog;


    //   Student String Variables
    private String fullname;
    private String email;
    private String password;
    private String enrollment;
    private String mobile;
    private String industry;
    private String semester;
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
        setContentView(R.layout.activity_signup);

        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();

        // Write a message to the database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        mDatabase = database.getReference(AppConstant.FIREBASE_TABLE_STUDNET);

        AwesomeValidation mAwesomeValidation = new AwesomeValidation(BASIC);


        initView();

        initValidationRules();


    }

    private void initValidationRules() {

        String regexPassword = "(?=.*[a-z])(?=.*[A-Z])(?=.*[\\d])(?=.*[~`!@#\\$%\\^&\\*\\(\\)\\-_\\+=\\{\\}\\[\\]\\|\\;:\"<>,./\\?]).{8,}";
        String TELEPHONE = "(^\\+\\d+)?[0-9\\s()-]*";

        mAwesomeValidation.addValidation(SignupActivity.this, R.id.activity_registration_name_ed, "[a-zA-Z\\s]+", R.string.err_name);
        mAwesomeValidation.addValidation(SignupActivity.this, R.id.activity_registration_email_ed, android.util.Patterns.EMAIL_ADDRESS, R.string.val_err_email);
        mAwesomeValidation.addValidation(SignupActivity.this, R.id.activity_registration_password_ed, regexPassword, R.string.err_password);
        mAwesomeValidation.addValidation(SignupActivity.this, R.id.activity_registration_mobile_ed, TELEPHONE, R.string.err_tel);

    }


    private void initView() {

        //Initilization of widgets

        waitingDialog = new LovelyProgressDialog(SignupActivity.this);


        studentAvtarImg = findViewById(R.id.activity_signup_student_avtar);
        fullNameEd = findViewById(R.id.activity_registration_name_ed);
        emailEd = findViewById(R.id.activity_registration_email_ed);
        pwdEd = findViewById(R.id.activity_registration_password_ed);
        mobileEd = findViewById(R.id.activity_registration_mobile_ed);
        industrySp = findViewById(R.id.activity_registration_department_spinner);

        alreadyRegisteredTv = findViewById(R.id.activity_signup_already_registered_txt);
        facultyRegistrationTv = findViewById(R.id.activity_signup_faculty_registration_tv);
        signupBtn = findViewById(R.id.activity_registration_btn);

        progressDialog = new ProgressDialog(SignupActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Singup");
        progressDialog.setMessage("Creating account");
//        Listener implement
        alreadyRegisteredTv.setOnClickListener(this);
        facultyRegistrationTv.setOnClickListener(this);
        signupBtn.setOnClickListener(this);
        studentAvtarImg.setOnClickListener(this);
        industrySp.setOnItemSelectedListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.activity_signup_already_registered_txt:
                finish();
                break;
            case R.id.activity_registration_btn:
                singupStudent();
                break;
            case R.id.activity_signup_faculty_registration_tv:
                Intent gotoFacultyRegistration = new Intent(SignupActivity.this, MentorRegistrationActivity.class);
                startActivity(gotoFacultyRegistration);
                finish();
                break;
            case R.id.activity_signup_student_avtar:
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
                Toast.makeText(SignupActivity.this, "Not Choosed ", Toast.LENGTH_LONG).show();
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
                studentAvtarImg.setImageDrawable(ImageUtils.roundedImage(SignupActivity.this, liteImage));
                waitingDialog.dismiss();
                new LovelyInfoDialog(SignupActivity.this)
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


    private void singupStudent() {

        fullname = fullNameEd.getText().toString().trim();
        email = emailEd.getText().toString().trim();
        password = pwdEd.getText().toString().trim();
        mobile = mobileEd.getText().toString().trim();


        if (mAwesomeValidation.validate()) {

            if (isValidMobile(mobile)) {


                waitingDialog.setCancelable(false)
                        .setTitle("Creating Profile..")
                        .setTopColorRes(R.color.colorPrimary)
                        .show();
                if (profilePic) {

                    auth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {

                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (!task.isSuccessful()) {
                                        if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                            Toast.makeText(SignupActivity.this, "User with this email already exist.", Toast.LENGTH_SHORT).show();
                                            progressDialog.dismiss();
                                        }
                                        progressDialog.dismiss();
                                        Toast.makeText(SignupActivity.this, task.getException() + "", Toast.LENGTH_SHORT).show();
                                        Log.e("TAG", task.getException() + "");

                                    } else {

                                        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                        String userId = user.getUid();


                                        mDatabase.child(userId)
                                                .setValue(new UserModel(fullname
                                                                , email
                                                                , password
                                                                , mobile
                                                                , industry, false, profileData),
                                                        new DatabaseReference.CompletionListener() {
                                                            @Override
                                                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                                                                if (databaseError != null) {
                                                                    Toast.makeText(SignupActivity.this, databaseError.toString(), Toast.LENGTH_SHORT).show();
                                                                } else {
                                                                    Toast.makeText(SignupActivity.this, "Success ! Faculty will Verify your account !", Toast.LENGTH_SHORT).show();
                                                                    finish();
                                                                    progressDialog.dismiss();
                                                                }
                                                            }
                                                        });


                                    }
                                }
                            });
                } else {
                    waitingDialog.dismiss();
                    new LovelyInfoDialog(SignupActivity.this)
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
        industry = adapterView.getSelectedItem().toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }


}
