package com.randymxj.weardisplay;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

/**
 * Created by Xiaojing on 9/28/2014.
 */
public class ShowImageActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.box_image);

        Intent intent = getIntent();

        ImageView BoxImage = (ImageView) findViewById(R.id.box_imageView);
        BoxImage.setImageResource(R.drawable.img_qrcode);
    }
}
