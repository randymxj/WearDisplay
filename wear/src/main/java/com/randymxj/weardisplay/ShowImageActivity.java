package com.randymxj.weardisplay;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by Xiaojing on 9/28/2014.
 */
public class ShowImageActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get intent information
        Intent intent = getIntent();
        int type = intent.getIntExtra("TYPE", 0);
        String title = intent.getStringExtra("TITLE");
        int rid = intent.getIntExtra("RID", 0);

        if( type == WearActivity.TYPE_BARCODE )
        {
            setContentView(R.layout.box_barcode);
            TextView tv = (TextView) findViewById(R.id.barcode_textView);
            tv.setText(title);
            ImageView iv = (ImageView) findViewById(R.id.barcode_imageView);
            iv.setImageResource(rid);
        }
        else if( type == WearActivity.TYPE_QRCODE )
        {
            setContentView(R.layout.box_qrcode);
            ImageView iv = (ImageView) findViewById(R.id.qrcode_imageView);
            iv.setImageResource(rid);
        }
        else if( type == WearActivity.TYPE_IMAGE )
        {
            setContentView(R.layout.box_image);
            ImageView iv = (ImageView) findViewById(R.id.image_imageView);
            iv.setImageResource(rid);
        }
        else if( type == WearActivity.TYPE_COLOR )
        {
            setContentView(R.layout.box_color);
            LinearLayout layout = (LinearLayout) findViewById(R.id.color_layout);

            layout.setBackgroundColor(getResources().getColor(rid));
        }
        else if( type == WearActivity.TYPE_BLINK )
        {
            setContentView(R.layout.box_color);
            LinearLayout layout = (LinearLayout) findViewById(R.id.color_layout);

            final AnimationDrawable drawable = new AnimationDrawable();
            final Handler handler = new Handler();

            drawable.addFrame(new ColorDrawable(getResources().getColor(rid)), 200);
            drawable.addFrame(new ColorDrawable(Color.BLACK), 200);
            drawable.setOneShot(false);

            layout.setBackground(drawable);

            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    drawable.start();
                }
            }, 100);
        }

    }
}
