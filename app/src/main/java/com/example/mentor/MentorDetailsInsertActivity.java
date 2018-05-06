package com.example.mentor;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static com.basgeekball.awesomevalidation.ValidationStyle.BASIC;

public class MentorDetailsInsertActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, View.OnClickListener {


    //    Component Init
    private EditText skillEd;
    private EditText qualificationEd;
    private EditText experienceEd;
    private Spinner mentorTypeSp;
    private Button mentorSignupBtn;

    private String mentorTypeString;

    //    Firebase
    private FirebaseAuth auth;
    private DatabaseReference mDatabase;
    private ProgressDialog progressDialog;

    AwesomeValidation mAwesomeValidation = new AwesomeValidation(BASIC);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mentor_details_insert);

        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();
        // Write a message to the database
        mDatabase = FirebaseDatabase.getInstance().getReference(AppConstant.FIREBASE_TABLE_MENTOR_DETAILS);

        AwesomeValidation mAwesomeValidation = new AwesomeValidation(BASIC);


        initView();

        initValidationRules();


    }


    private void initValidationRules() {

        mAwesomeValidation.addValidation(MentorDetailsInsertActivity.this, R.id.activity_mentor_details_insert_skill_ed, "[a-zA-Z\\s]+", R.string.valid_value);
        mAwesomeValidation.addValidation(MentorDetailsInsertActivity.this, R.id.activity_mentor_details_insert_qualification_ed, "[a-zA-Z\\s]+", R.string.valid_value);
        mAwesomeValidation.addValidation(MentorDetailsInsertActivity.this, R.id.activity_mentor_details_insert_experience, "[a-zA-Z\\s]+", R.string.valid_value);

    }


    private void initView() {

        skillEd = findViewById(R.id.activity_mentor_details_insert_skill_ed);
        qualificationEd = findViewById(R.id.activity_mentor_details_insert_qualification_ed);
        experienceEd = findViewById(R.id.activity_mentor_details_insert_experience);
        mentorTypeSp = findViewById(R.id.activity_mentor_details_insert_mentor_type_sp);
        mentorSignupBtn = findViewById(R.id.activity_mentor_details_insert_signup_btn);


        mentorTypeSp.setOnItemSelectedListener(this);
        mentorSignupBtn.setOnClickListener(this);

        progressDialog = new ProgressDialog(MentorDetailsInsertActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Create Mentor profile");
        progressDialog.setMessage("Creating account...");

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        mentorTypeString = parent.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.activity_mentor_details_insert_signup_btn:
                insertMentorDetails();
                break;
        }

    }

    private void insertMentorDetails() {

        final String skill = skillEd.getText().toString().trim();
        final String qualification = qualificationEd.getText().toString().trim();
        final String experience = experienceEd.getText().toString().trim();

        if (!skill.isEmpty() && !qualification.isEmpty() && !experience.isEmpty()) {
            mDatabase.child(auth.getCurrentUser().getUid()).setValue(new MentorDetailsModel(skill, qualification, experience, mentorTypeString), new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    if (databaseError != null) {
                        Toast.makeText(MentorDetailsInsertActivity.this, databaseError.toString(), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MentorDetailsInsertActivity.this, "Success ! Your Mentor Account Created", Toast.LENGTH_SHORT).show();
                        Intent gotoLoginScreen = new Intent(MentorDetailsInsertActivity.this, LoginActivity.class);
                        gotoLoginScreen.putExtra("KEY_LOGIN",true);
                        startActivity(gotoLoginScreen);
                        progressDialog.dismiss();
                        finish();
                    }
                }
            });
        } else {
            Toast.makeText(this, "Pleas Enter all value", Toast.LENGTH_SHORT).show();
        }

    }
}
