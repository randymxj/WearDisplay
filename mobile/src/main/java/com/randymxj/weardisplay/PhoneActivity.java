package com.randymxj.weardisplay;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.EnumMap;
import java.util.Map;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import org.w3c.dom.Text;


public class PhoneActivity extends Activity implements View.OnClickListener {

    public static final int TYPE_IMAGE = 0;
    public static final int TYPE_QRCODE = 1;
    public static final int TYPE_BARCODE = 2;
    public static final int TYPE_TEXT = 3;
    public static final int TYPE_COLOR = 4;
    public static final int TYPE_BLINK = 5;
    public static final int TYPE_SETTING = 9;

    Config config;

    // Class for the list nodes
    public static class ListNode {
        public int icon_id = 0;
        public String title = "";
        public String text = "";
        public String value = "";
        public int rid = 0;
        public int type = TYPE_BARCODE;

        public ListNode( int i, String s1, String s2, String s3, int r, int t ) {
            this.icon_id = i;
            this.title = s1;
            this.text = s2;
            this.value = s3;
            this.rid = r;
            this.type = t;
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone);

        config = new Config(this);
        config.readTracker();

        LinearLayout itemlist_layout = (LinearLayout) findViewById(R.id.itemlist_LinearLayout);

        for( int i = 0; i < config.items.size(); i++ )
        {
            ListNode node = config.items.get(i);

            LinearLayout layout = (LinearLayout)getLayoutInflater().inflate(R.layout.item_layout, null);
            TextView tv = (TextView) layout.findViewById(R.id.item_title_textView);
            tv.setText(node.value);

            itemlist_layout.addView(layout);
        }
    }

    @Override
    public void onClick(View view) {

    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {

        if (requestCode == 0)
        {
            if (resultCode == RESULT_OK)
            {
                int item_type = intent.getIntExtra("item_type", 0);

                if( item_type == TYPE_BARCODE )
                {
                    String value = intent.getStringExtra("value");
                    String title = intent.getStringExtra("title");
                    String description = intent.getStringExtra("description");

                    ListNode node = new ListNode(0, title, description, value, 0, item_type);
                    config.items.add(node);
                    config.writeItems();
                }

            }
            else if (resultCode == RESULT_CANCELED)
            {
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.phone, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if( id == R.id.action_add )
        {
            // EnterBarcode Activity
            Intent intent = new Intent(PhoneActivity.this, EnterBarcodeActivity.class);
            startActivityForResult(intent, 0);
        }
        else if( id == R.id.action_settings )
        {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
