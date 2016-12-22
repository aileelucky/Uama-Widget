package gu.hangzhou.uama.uama;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.facebook.drawee.backends.pipeline.Fresco;

import java.util.ArrayList;
import java.util.List;

import uama.hangzhou.gu.photochoose.ImagePagerActivity;

public class MainActivity extends AppCompatActivity {

    private Button open;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        Fresco.initialize(MainActivity.this);
        setContentView(R.layout.activity_main);
        open = (Button) findViewById(R.id.btn_open);
        open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<String> imageList = new ArrayList<String>();
                imageList.add("http://e.hiphotos.baidu.com/image/pic/item/f11f3a292df5e0fed5affb26596034a85edf7265.jpg");
                imageList.add("http://b.hiphotos.baidu.com/image/pic/item/b8389b504fc2d5627c886ee4e51190ef76c66c33.jpg");
                Intent intent = new Intent(MainActivity.this, ImagePagerActivity.class);
                intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_INDEX, 1);
                intent.putStringArrayListExtra(ImagePagerActivity.EXTRA_IMAGE_URLS, (ArrayList<String>) imageList);
                startActivity(intent);
            }
        });
    }
}
