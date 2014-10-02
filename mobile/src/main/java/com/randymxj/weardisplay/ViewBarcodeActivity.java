package com.randymxj.weardisplay;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.zxing.WriterException;

/**
 * Created by 502021 on 10/1/2014.
 */
public class ViewBarcodeActivity extends Activity {

    private String title, text, value, format;
    private int type;
    private int icon_index = 0;
    private Bitmap code_bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewbarcode);

        // Get intent information
        Intent intent = getIntent();

        icon_index = intent.getIntExtra("icon_index", 0);
        title = intent.getStringExtra("title");
        text = intent.getStringExtra("text");
        value = intent.getStringExtra("value");
        format = intent.getStringExtra("format");
        type = intent.getIntExtra("type", 0);

        TextView tv_title = (TextView) findViewById(R.id.view_barcode_title_textView);
        tv_title.setText(title);

        TextView tv_text = (TextView) findViewById(R.id.view_barcode_text_textView);
        tv_text.setText(text);

        TextView tv_value = (TextView) findViewById(R.id.view_barcode_value_textView);
        tv_value.setText(value);

        TextView tv_format = (TextView) findViewById(R.id.view_barcode_format_textView);
        tv_format.setText(format);

        ImageView iv_logo = (ImageView) findViewById(R.id.view_logo_imageView);
        if( icon_index > 0 )
            iv_logo.setImageResource(PhoneActivity.providers.get(icon_index).icon_rid);
        else
            ((LinearLayout) iv_logo.getParent()).removeView(iv_logo);

        ImageView iv_barcode = (ImageView) findViewById(R.id.view_barcode_imageView);
        try
        {
            code_bitmap = PhoneActivity.encodeAsBitmap(value, format, 600, 300);
            iv_barcode.setImageBitmap(code_bitmap);

        }
        catch (WriterException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.viewbarcodeactivity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if( id == R.id.action_remove )
        {
            // Remove Barcode Activity
            new AlertDialog.Builder(this)
                    .setTitle("Remove Item")
                    .setMessage("Please confirm to remove this item")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // Remove item
                            Intent intent = new Intent();
                            intent.putExtra("cmd", PhoneActivity.REMOVE_ITEM);

                            intent.putExtra("value", value);
                            intent.putExtra("title", title);

                            setResult(RESULT_OK, intent);
                            finish();
                        }
                    })
                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // Nothing
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }

        return super.onOptionsItemSelected(item);
    }
}
