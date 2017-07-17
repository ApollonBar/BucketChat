package us.xingkong.bucketchat.app.activity.view;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import us.xingkong.bucketchat.R;

public class TestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);



        Intent intent = new Intent(TestActivity.this,LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
