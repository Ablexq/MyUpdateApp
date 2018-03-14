package example.com.myupdateapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import example.com.myupdateapp.util.UpdateManager;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onclick(View view) {
        new UpdateManager(this).checkUpdateInfo();
    }

}
