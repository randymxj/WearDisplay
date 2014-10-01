package com.randymxj.weardisplay;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.util.EnumMap;
import java.util.Map;

/**
 * Created by Xiaojing on 9/28/2014.
 */
public class ShowImageActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get intent information
        Intent intent = getIntent();
        String title = intent.getStringExtra("title");
        String text = intent.getStringExtra("text");
        String value = intent.getStringExtra("value");
        String format = intent.getStringExtra("format");
        int rid = intent.getIntExtra("rid", 0);
        int type = intent.getIntExtra("type", 0);

        if( type == WearActivity.TYPE_BARCODE )
        {
            setContentView(R.layout.box_barcode);
            TextView tv_title = (TextView) findViewById(R.id.barcode_title_textView);
            tv_title.setText(title);
            TextView tv_value = (TextView) findViewById(R.id.barcode_value_textView);
            tv_value.setText(value);

            ImageView iv = (ImageView) findViewById(R.id.barcode_imageView);

            BarcodeFormat code_format;
            if( format.compareToIgnoreCase("CODE_128") == 0 )
            {
                code_format = BarcodeFormat.CODE_128;
            }
            else if( format.compareToIgnoreCase("CODE_39") == 0 )
            {
                code_format = BarcodeFormat.CODE_39;
            }
            else if( format.compareToIgnoreCase("CODE_93") == 0 )
            {
                code_format = BarcodeFormat.CODE_93;
            }
            else if( format.compareToIgnoreCase("EAN_13") == 0 )
            {
                code_format = BarcodeFormat.EAN_13;
            }
            else if( format.compareToIgnoreCase("EAN_8") == 0 )
            {
                code_format = BarcodeFormat.EAN_8;
            }
            else if( format.compareToIgnoreCase("UPC_A") == 0 )
            {
                code_format = BarcodeFormat.UPC_A;
            }
            else if( format.compareToIgnoreCase("UPC_E") == 0 )
            {
                code_format = BarcodeFormat.UPC_E;
            }
            else if( format.compareToIgnoreCase("QR_CODE") == 0 )
            {
                code_format = BarcodeFormat.QR_CODE;
            }
            else
            {
                code_format = BarcodeFormat.CODE_128;
            }

            try
            {

                Bitmap code_bitmap = encodeAsBitmap(value, code_format, 226, 100);
                iv.setImageBitmap(code_bitmap);

            }
            catch (WriterException e)
            {
                e.printStackTrace();
            }
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
        else if( type == WearActivity.TYPE_TEXT )
        {
            setContentView(R.layout.box_text);
            TextView tv_title = (TextView) findViewById(R.id.text_title_textView);
            tv_title.setText(title);
            TextView tv_value = (TextView) findViewById(R.id.text_value_textView);
            tv_value.setText(value);
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

            drawable.addFrame(new ColorDrawable(getResources().getColor(rid)), 100);
            drawable.addFrame(new ColorDrawable(Color.BLACK), 100);
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

    /*
    ZXing barcode/qrcode generator
     */

    private static final int WHITE = 0xFFFFFFFF;
    private static final int BLACK = 0xFF000000;

    Bitmap encodeAsBitmap(String contents, BarcodeFormat format, int img_width, int img_height) throws WriterException {

        String contentsToEncode = contents;
        if (contentsToEncode == null) {
            return null;
        }

        Map<EncodeHintType, Object> hints = null;
        String encoding = guessAppropriateEncoding(contentsToEncode);

        if (encoding != null) {
            hints = new EnumMap<EncodeHintType, Object>(EncodeHintType.class);
            hints.put(EncodeHintType.CHARACTER_SET, encoding);
        }

        MultiFormatWriter writer = new MultiFormatWriter();
        BitMatrix result;

        try {
            result = writer.encode(contentsToEncode, format, img_width, img_height, hints);
        } catch (IllegalArgumentException iae) {
            // Unsupported format
            return null;
        }

        int width = result.getWidth();
        int height = result.getHeight();
        int[] pixels = new int[width * height];

        for (int y = 0; y < height; y++) {
            int offset = y * width;
            for (int x = 0; x < width; x++) {
                pixels[offset + x] = result.get(x, y) ? BLACK : WHITE;
            }
        }

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);

        return bitmap;
    }

    private static String guessAppropriateEncoding(CharSequence contents) {

        // Very crude at the moment
        for (int i = 0; i < contents.length(); i++) {
            if (contents.charAt(i) > 0xFF) {
                return "UTF-8";
            }
        }
        return null;
    }
}
