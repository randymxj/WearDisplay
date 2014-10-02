package com.randymxj.weardisplay;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import org.w3c.dom.Text;


public class PhoneActivity extends Activity implements View.OnClickListener, View.OnLongClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener  {

    // CMD
    public static final int NOTHING = 0;
    public static final int NEW_ITEM = 1;
    public static final int REMOVE_ITEM = 2;

    // Type
    public static final int TYPE_IMAGE = 0;
    public static final int TYPE_QRCODE = 1;
    public static final int TYPE_BARCODE = 2;
    public static final int TYPE_TEXT = 3;
    public static final int TYPE_COLOR = 4;
    public static final int TYPE_BLINK = 5;
    public static final int TYPE_SETTING = 9;

    // Instance
    Config config;
    GoogleApiClient googleClient;

    // Variables

    // Pre load member card providers
    public  static class MemberCardProvider {
        public String title, text, format;
        public int icon_rid = 0;

        public MemberCardProvider(int i, String s1, String s2, String s3)
        {
            icon_rid = i;
            title = s1;
            text = s2;
            format = s3;
        }
    }

    public static ArrayList<MemberCardProvider> providers = new ArrayList<MemberCardProvider>()
    {
        {
            // Build Member Card Provider List
            add(new MemberCardProvider(0, "Others", "", ""));
            add(new MemberCardProvider(R.drawable.ic_cvs, "CVS", "Extra Care", "EAN_13"));
            add(new MemberCardProvider(0, "Shaws", "Reward Card", "UPC_A"));
            add(new MemberCardProvider(0, "Stop&Shop", "Reward Card", "EAN_13"));
            add(new MemberCardProvider(0, "IKEA", "Family", "CODE_128"));
            add(new MemberCardProvider(0, "DICK'S", "Score Card Rewards", "UPC_A"));
        }
    };

    // Class for the list nodes
    public static class ListNode {
        public int icon_index = 0;
        public String title = "";
        public String text = "";
        public String value = "";
        public String format = "CODE_128";
        public int rid = 0;
        public int type = TYPE_BARCODE;

        public View nodeView;

        public ListNode( int i, String s1, String s2, String s3, String s4, int r, int t ) {
            this.icon_index = i;
            this.title = s1;
            this.text = s2;
            this.value = s3;
            this.format = s4;
            this.rid = r;
            this.type = t;
        }

        public void setView(View v)
        {
            nodeView = v;
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone);

        config = new Config(this);
        config.readItems();

        if( config.items.size() > 0 )
            updateItemList();

        // Build a new GoogleApiClient
        googleClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    public void makeToast(String str)
    {
        Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
    }

    public void updateItemList()
    {
        // LayoutInflater
        LayoutInflater layoutInflater = LayoutInflater.from(this);

        // Parent Layout
        LinearLayout itemlist_layout = (LinearLayout) findViewById(R.id.itemlist_LinearLayout);
        itemlist_layout.removeAllViewsInLayout();

        // Insertion
        for( int i = 0; i < config.items.size(); i++ )
        {
            ListNode node = config.items.get(i);

            LinearLayout layout = (LinearLayout)getLayoutInflater().inflate(R.layout.item_layout, null);
            LinearLayout module = (LinearLayout) layout.findViewById(R.id.module_item_layout);
            TextView tv_title = (TextView) module.findViewById(R.id.item_title_textView);
            tv_title.setText(node.title);
            TextView tv_text = (TextView) module.findViewById(R.id.item_text_textView);
            tv_text.setText(node.text);

            module.setOnClickListener(this);

            node.setView(module);

            itemlist_layout.addView(layout);
        }
    }

    @Override
    public void onClick(View v) {

        if( v.getId() == R.id.module_item_layout )
        {
            // Search for data
            for( int i = 0; i < config.items.size(); i++ )
            {
                ListNode node = config.items.get(i);

                if( node.nodeView == v )
                {
                    // ViewBarcode Activity
                    Intent intent = new Intent(PhoneActivity.this, ViewBarcodeActivity.class);

                    intent.putExtra("icon_index", node.icon_index);
                    intent.putExtra("title", node.title);
                    intent.putExtra("value", node.value);
                    intent.putExtra("text", node.text);
                    intent.putExtra("format", node.format);
                    intent.putExtra("type", node.type);

                    startActivityForResult(intent, 0);
                    break;
                }
            }
        }

    }

    @Override
    public boolean onLongClick(View v) {
        Log.e("@@@ Long", String.valueOf(v.getId()));

        return false;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {

        if (requestCode == 0)
        {
            if (resultCode == RESULT_OK)
            {
                int cmd = intent.getIntExtra("cmd", 0);

                if( cmd == NEW_ITEM )
                {
                    int type = intent.getIntExtra("type", 0);

                    if (type == TYPE_BARCODE)
                    {
                        int icon_index = intent.getIntExtra("icon_index", 0);
                        String value = intent.getStringExtra("value");
                        String title = intent.getStringExtra("title");
                        String text = intent.getStringExtra("text");
                        String format = intent.getStringExtra("format");

                        ListNode node = new ListNode(icon_index, title, text, value, format, 0, type);
                        config.items.add(node);
                        config.writeItems();

                        updateItemList();
                    }
                }
                else if( cmd == REMOVE_ITEM )
                {
                    String value = intent.getStringExtra("value");
                    String title = intent.getStringExtra("title");

                    // Search for data
                    for( int i = 0; i < config.items.size(); i++ )
                    {
                        ListNode node = config.items.get(i);

                        if( ( node.title.compareToIgnoreCase(title) == 0 ) && ( node.value.compareToIgnoreCase(value) == 0 ) )
                        {
                            // Remove node and layout
                            ((LinearLayout)node.nodeView.getParent()).removeView(node.nodeView);
                            config.items.remove(node);
                            config.writeItems();
                            break;
                        }
                    }
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
        else if( id == R.id.action_sync )
        {
            makeToast("Pushing Items to Wear Device");
            googleClient.connect();
        }
        else if( id == R.id.action_settings )
        {
        }

        return super.onOptionsItemSelected(item);
    }

    /*
    Android Wear Message Api
     */

    // Connect to the data layer when the Activity starts
    @Override
    protected void onStart() {
        super.onStart();
        //googleClient.connect();
    }

    // Disconnect from the data layer when the Activity stops
    @Override
    protected void onStop() {
        /*
        if (null != googleClient && googleClient.isConnected()) {
            googleClient.disconnect();
        }
        */
        super.onStop();
    }

    // Placeholders for required connection callbacks
    @Override
    public void onConnected(Bundle connectionHint) {

        String message = config.readStringJSON();

        //Requires a new thread to avoid blocking the UI
        new SendToDataLayerThread("/message_path", message).start();
    }

    @Override
    public void onConnectionSuspended(int cause) { }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        makeToast("Google API Connection Failed");
    }

    class SendToDataLayerThread extends Thread {
        String path;
        String message;

        // Constructor to send a message to the data layer
        SendToDataLayerThread(String p, String msg) {
            path = p;
            message = msg;
        }

        public void run() {
            NodeApi.GetConnectedNodesResult nodes = Wearable.NodeApi.getConnectedNodes(googleClient).await();
            for (Node node : nodes.getNodes()) {
                MessageApi.SendMessageResult result = Wearable.MessageApi.sendMessage(googleClient, node.getId(), path, message.getBytes()).await();
                if (result.getStatus().isSuccess()) {
                    Log.e("@@@", "Message: {" + message + "} sent to: " + node.getDisplayName());
                }
                else {
                    // Log an error
                    Log.e("@@@", "ERROR: Failed to push items to Wear Device");
                }

                if (null != googleClient && googleClient.isConnected()) {
                    googleClient.disconnect();
                }
            }
        }
    }

    /*
    ZXing barcode/qrcode generator
     */

    private static final int WHITE = 0xFFFFFFFF;
    private static final int BLACK = 0xFF000000;

    public static Bitmap encodeAsBitmap(String contents, String str_format, int img_width, int img_height) throws WriterException {

        BarcodeFormat format;
        if( str_format.compareToIgnoreCase("CODE_128") == 0 )
        {
            format = BarcodeFormat.CODE_128;
        }
        else if( str_format.compareToIgnoreCase("CODE_39") == 0 )
        {
            format = BarcodeFormat.CODE_39;
        }
        else if( str_format.compareToIgnoreCase("CODE_93") == 0 )
        {
            format = BarcodeFormat.CODE_93;
        }
        else if( str_format.compareToIgnoreCase("EAN_13") == 0 )
        {
            format = BarcodeFormat.EAN_13;
        }
        else if( str_format.compareToIgnoreCase("EAN_8") == 0 )
        {
            format = BarcodeFormat.EAN_8;
        }
        else if( str_format.compareToIgnoreCase("UPC_A") == 0 )
        {
            format = BarcodeFormat.UPC_A;
        }
        else if( str_format.compareToIgnoreCase("UPC_E") == 0 )
        {
            format = BarcodeFormat.UPC_E;
        }
        else if( str_format.compareToIgnoreCase("QR_CODE") == 0 )
        {
            format = BarcodeFormat.QR_CODE;
        }
        else
        {
            format = BarcodeFormat.CODE_128;
        }

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
