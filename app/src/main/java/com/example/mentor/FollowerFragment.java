package com.example.mentor;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatDialog;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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
    private String studentSkill;
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

        final SharedPreferences pref = getActivity().getSharedPreferences("MyPref", 0);

        SharedPreferences.Editor editor = pref.edit();

        departmentName = pref.getString("KEY_DEPARTMENT", null);

        mDatabase.child(AppConstant.FIREBASE_TABLE_FOLLOWERS).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (studentArrayList.size() > 0) {
                    studentArrayList.clear();
                }


                for (DataSnapshot list : dataSnapshot.getChildren()) {

                    boolean status = (boolean) list.child("followABoolean").getValue();
                    Log.e("FOLLOW===-", pref.getString("KEY_DEPARTMENT", null).toString());

                    if (!status && list.child(AppConstant.FIREBASE_INDUSTRY).getValue().toString().equals(pref.getString("KEY_DEPARTMENT", null))) {
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
        studentDetailsDialog = new AppCompatDialog(getActivity(), R.style.Theme_AppCompat_Light_Dialog_MinWidth);
        studentDetailsDialog.setContentView(R.layout.dialog_layout);
        studentDetailsDialog.setTitle("Mentor  Details");

        studentDetailsDialog.show();

        final String email = parent.getItemAtPosition(position).toString();

        final ImageView profilePic = studentDetailsDialog.findViewById(R.id.dialog_layout_profile_img);
        final TextView studentNameTv = studentDetailsDialog.findViewById(R.id.dialog_layout_student_name);
        final TextView studentEmailTv = studentDetailsDialog.findViewById(R.id.dialog_layout_student_email);
        final TextView studentEnrollmentTv = studentDetailsDialog.findViewById(R.id.dialog_layout_student_mobile);
        final TextView studentMobileTv = studentDetailsDialog.findViewById(R.id.dialog_layout_student_enrollment);
        final TextView studentSkillTv = studentDetailsDialog.findViewById(R.id.dialog_layout_student_skill);
//        final Button studentCancleBtn = studentDetailsDialog.findViewById(R.id.dialog_layout_student_cancle_btn);
        final Button studentAproveBtn = studentDetailsDialog.findViewById(R.id.dialog_layout_student_aprove_btn);
        final Button studentRequestBtn = studentDetailsDialog.findViewById(R.id.dialog_layout_student_send_mail_btn);

        studentAproveBtn.setText("Follow");
        studentAproveBtn.setPadding(10, 10, 10, 10);

        mDatabase.child(AppConstant.FIREBASE_TABLE_MENTOR).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot student : dataSnapshot.getChildren()) {
                    if (student.child(AppConstant.FIREBASE_TABLE_FULLNAME).getValue().equals(email)) {

                        Log.e("TAG-KEY", student.getKey());

                        studentPushKey = student.getKey();


                        studentName = student.child(AppConstant.FIREBASE_TABLE_FULLNAME).getValue().toString();
                        studentEmail = student.child(AppConstant.FIREBASE_TABLE_EMAIL).getValue().toString();
                        studentMobile = student.child(AppConstant.FIREBASE_TABLE_MOBILE).getValue().toString();
                        studentEnrollment = student.child(AppConstant.FIREBASE_INDUSTRY).getValue().toString();

                        mDatabase.child(AppConstant.FIREBASE_TABLE_MENTOR_DETAILS).child(studentPushKey).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                studentSkill = dataSnapshot.child("skill").getValue().toString();
                                Log.e("MENTOR", dataSnapshot.child("skill").getValue() + "");
                                studentSkillTv.setText(studentSkill);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                        Bitmap src;
                        String imgBase64 = student.child("avatar").getValue().toString();
                        byte[] decodedString = Base64.decode(imgBase64, Base64.DEFAULT);
                        src = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                        profilePic.setImageDrawable(ImageUtils.roundedImage(getActivity(), src));

                        studentNameTv.setText(studentName);
                        studentEmailTv.setText(studentEmail);
                        studentEnrollmentTv.setText(studentEnrollment);
                        studentMobileTv.setText(studentMobile);


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


//        studentCancleBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                studentDetailsDialog.dismiss();
//            }
//        });

        studentAproveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

                mDatabase.child(AppConstant.FIREBASE_TABLE_FOLLOWERS).child(key).child("followABoolean").setValue(true);
                studentDetailsDialog.dismiss();
                studentArrayList.clear();
                studentListArrayAdapter.notifyDataSetChanged();

                sendFCMPush();
//                NotificationCompat.Builder builder =
//                        new NotificationCompat.Builder(getActivity())
//                                .setSmallIcon(R.mipmap.ic_launcher_round)
//                                .setContentTitle("Mentor Following ")
//                                .setContentText("New Following Request")
//                                .setSound(defaultSoundUri);
//
//                Intent notificationIntent = new Intent(getActivity(), LoginActivity.class);
//                PendingIntent contentIntent = PendingIntent.getActivity(getActivity(), 0, notificationIntent,
//                        PendingIntent.FLAG_UPDATE_CURRENT);
//                builder.setContentIntent(contentIntent);
//
//                // Add as notification
//                NotificationManager manager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
//                manager.notify(0, builder.build());
            }
        });

        studentRequestBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                studentDetailsDialog.dismiss();
                Intent intent = new Intent(getActivity(), RequestPostMentorActivity.class);
                intent.putExtra("EMAIL_KEY", studentEmail);
                startActivity(intent);
            }
        });


    }


    private void sendFCMPush() {

        final String Legacy_SERVER_KEY = "AIzaSyBNcLw8JdbE8OERrFjfVXLAsTT5_Siqo14";
        String msg = "New Following ";
        String title = "Student Follow you";
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

//            final String topicname = industryName.replace(" ", "_");

            obj.put("to", "all");
//            obj.put("to", token);
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
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        int socketTimeout = 1000 * 60;// 60 seconds
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsObjRequest.setRetryPolicy(policy);
        requestQueue.add(jsObjRequest);
    }

}