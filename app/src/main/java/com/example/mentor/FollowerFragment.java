package com.example.mentor;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class FollowerFragment extends Fragment implements AdapterView.OnItemClickListener {

    //    Componenet Init
    private ListView studentListview;
    private ArrayList<String> studentArrayList;
    private ProgressDialog progressDialog;
    private RelativeLayout relativeLayout;
    private Dialog studentDetailsDialog;


    private ArrayAdapter<String> studentListArrayAdapter;


    private String departmentName = null;

    //Firebase
    private DatabaseReference mDatabase;
    private FirebaseAuth firebaseAuth;


    //    String Variable for Dailog
    private String studentName;
    private String studentEmail;
    private String studentEnrollment;
    private String studentMobile;
    private String studentPushKey;
    private String key;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        return inflater.inflate(R.layout.following_fragment_layout, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();

        studentListview = view.findViewById(R.id.activity_view_category_list_lv);
        relativeLayout = view.findViewById(R.id.student_request_list_layout);

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Loading");
        progressDialog.setMessage("getting students name....");

        studentListview.setOnItemClickListener(this);
        progressDialog.show();
        studentArrayList = new ArrayList<>();

        studentListArrayAdapter = new ArrayAdapter<String>(getActivity(), R.layout.row_student_request_layout, R.id.row_layout_category_categoryname_tv, studentArrayList);
        studentListview.setAdapter(studentListArrayAdapter);


        mDatabase.child(AppConstant.FIREBASE_TABLE_FOLLOWERS).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (studentArrayList.size() > 0) {
                    studentArrayList.clear();
                }


                for (DataSnapshot list : dataSnapshot.getChildren()) {

                    boolean status = (boolean) list.child("followABoolean").getValue();
                    Log.e("FOLLOW", status + "");
                    if (!status) {
                        studentArrayList.add(list.child("name").getValue().toString());
                    }


                }
                studentListArrayAdapter.notifyDataSetChanged();
                progressDialog.dismiss();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Toast.makeText(getActivity(), "ITEM CLICKED", Toast.LENGTH_SHORT).show();
        studentDetailsDialog = new AppCompatDialog(getActivity(), R.style.Theme_AppCompat_Light_Dialog_MinWidth);
        studentDetailsDialog.setContentView(R.layout.dialog_layout);
        studentDetailsDialog.setTitle("Student Details");

        studentDetailsDialog.show();

        final String email = parent.getItemAtPosition(position).toString();


        final TextView studentNameTv = studentDetailsDialog.findViewById(R.id.dialog_layout_student_name);
        final TextView studentEmailTv = studentDetailsDialog.findViewById(R.id.dialog_layout_student_email);
        final TextView studentEnrollmentTv = studentDetailsDialog.findViewById(R.id.dialog_layout_student_mobile);
        final TextView studentMobileTv = studentDetailsDialog.findViewById(R.id.dialog_layout_student_enrollment);
        final Button studentCancleBtn = studentDetailsDialog.findViewById(R.id.dialog_layout_student_cancle_btn);
        final Button studentAproveBtn = studentDetailsDialog.findViewById(R.id.dialog_layout_student_aprove_btn);

        studentAproveBtn.setText("Follow");
        studentAproveBtn.setPadding(10, 10, 10, 10);
        mDatabase.child(AppConstant.FIREBASE_TABLE_MENTOR).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot student : dataSnapshot.getChildren()) {
                    if (student.child(AppConstant.FIREBASE_TABLE_EMAIL).getValue().equals(email)) {
                        Log.e("TAG-KEY", student.getKey());
                        studentPushKey = student.getKey();
                        studentName = student.child(AppConstant.FIREBASE_TABLE_FULLNAME).getValue().toString();
                        studentEmail = student.child(AppConstant.FIREBASE_TABLE_EMAIL).getValue().toString();
                        studentMobile = student.child(AppConstant.FIREBASE_TABLE_MOBILE).getValue().toString();
                        studentEnrollment = student.child(AppConstant.FIREBASE_INDUSTRY).getValue().toString();

                        studentNameTv.setText(studentName);
                        studentEmailTv.setText(studentEmail);
                        studentEnrollmentTv.setText(studentEnrollment);
                        studentMobileTv.setText(studentMobile);
                        Toast.makeText(getActivity(), studentPushKey + "", Toast.LENGTH_SHORT).show();

                        mDatabase.child(AppConstant.FIREBASE_TABLE_FOLLOWERS).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for (DataSnapshot list : dataSnapshot.getChildren()) {

                                    if (list.child("name").getValue().equals(email)) {
                                        key = list.getKey();


                                    }


                                }

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                    }
                }
            }

            public void onCancelled(DatabaseError databaseError) {
            }
        });


        studentCancleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                studentDetailsDialog.hide();
            }
        });

        studentAproveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDatabase.child(AppConstant.FIREBASE_TABLE_FOLLOWERS).child(key).child("followABoolean").setValue(true);
                studentDetailsDialog.dismiss();
                studentArrayList.clear();

                studentListArrayAdapter.notifyDataSetChanged();
            }
        });
    }
}