package com.example.mentor;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class RequestPostMentorActivity extends AppCompatActivity {

    private EditText emailIdEd;
    private EditText messageEd;
    private Button send;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_post_mentor);

        initView();
    }

    private void initView() {
        emailIdEd = findViewById(R.id.mentor_mailid);
        messageEd = findViewById(R.id.message_for_mentor);
        send = findViewById(R.id.send_btn);


        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String to=emailIdEd.getText().toString();
                Intent email = new Intent(Intent.ACTION_SEND);
                email.putExtra(Intent.EXTRA_EMAIL, new String[]{ to});
                email.putExtra(Intent.EXTRA_SUBJECT, "Student Request FROM GOD");
                email.putExtra(Intent.EXTRA_TEXT, messageEd.getText());

//need this to prompts email client only
                email.setType("message/rfc822");

                startActivity(Intent.createChooser(email, "Choose an Email client :"));
            }
        });
    }
}
