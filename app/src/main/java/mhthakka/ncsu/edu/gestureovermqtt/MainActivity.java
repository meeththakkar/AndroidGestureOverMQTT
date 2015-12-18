package mhthakka.ncsu.edu.gestureovermqtt;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import mhthakka.ncsu.edu.gestureovermqtt.view.GestureRecognitionView;

public class MainActivity extends AppCompatActivity {


GestureRecognitionView gestureRecognitionView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}
