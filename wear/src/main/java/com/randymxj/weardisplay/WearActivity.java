package com.randymxj.weardisplay;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.wearable.view.CircledImageView;
import android.support.wearable.view.WatchViewStub;
import android.support.wearable.view.WearableListView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Xiaojing on 9/28/2014.
 */
public class WearActivity extends Activity implements WearableListView.ClickListener {

    public static final int TYPE_IMAGE = 0;
    public static final int TYPE_QRCODE = 1;
    public static final int TYPE_BARCODE = 2;
    public static final int TYPE_TEXT = 3;
    public static final int TYPE_COLOR = 4;
    public static final int TYPE_BLINK = 5;
    public static final int TYPE_SETTING = 9;

    // View
    private WearableListView mListView;
    private MyListAdapter mAdapter;

    // Variable
    private float mDefaultCircleRadius;
    private float mSelectedCircleRadius;
    private Config config;

    // Class for the list nodes
    public static class ListNode {
        public int icon_index = 0;
        public String title = "";
        public String text = "";
        public String value = "";
        public String format = "CODE_128";
        public int rid = 0;
        public int type = TYPE_BARCODE;

        public ListNode( int i, String s1, String s2, String s3, String s4, int r, int t ) {
            this.icon_index = i;
            this.title = s1;
            this.text = s2;
            this.value = s3;
            this.format = s4;
            this.rid = r;
            this.type = t;
        }

    }

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

    public ArrayList<ListNode> listItems = new ArrayList<ListNode>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listview);

        // Config
        config = new Config(this);
        config.readItems();

        // Listener
        IntentFilter messageFilter = new IntentFilter(Intent.ACTION_SEND);
        MessageReceiver messageReceiver = new MessageReceiver();
        LocalBroadcastManager.getInstance(this).registerReceiver(messageReceiver, messageFilter);

        mDefaultCircleRadius = getResources().getDimension(R.dimen.default_settings_circle_radius);
        mSelectedCircleRadius = getResources().getDimension(R.dimen.selected_settings_circle_radius);
        mAdapter = new MyListAdapter();

        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                mListView = (WearableListView) stub.findViewById(R.id.listView1);
                mListView.setAdapter(mAdapter);
                mListView.setClickListener(WearActivity.this);
            }
        });

        // Construct the item list
        for( int i = 0; i < config.items.size(); i++ )
        {
            ListNode node = config.items.get(i);
            listItems.add(node);
        }

        //listItems.add(new ListNode(R.drawable.ic_cvs, "CVS ExtraCare", "", "4879038296855", "EAN_13", 0, TYPE_BARCODE));
        //listItems.add(new ListNode(R.drawable.ic_card, "Stop&Shop", "", "2212567294022", "EAN_13", 0, TYPE_BARCODE));
        //listItems.add(new ListNode(R.drawable.ic_card, "HMart", "", "403007927385", "UPC_A", 0, TYPE_BARCODE));
        //listItems.add(new ListNode(R.drawable.ic_avis, "Avis Preferred", "", "4EC55M", "", 0, TYPE_TEXT));
        //listItems.add(new ListNode(R.drawable.ic_umbrella, "Green Color", "", "", "", R.color.green, TYPE_COLOR));
        //listItems.add(new ListNode(R.drawable.ic_colorwheel, "Blue Blink", "", "", "", R.color.blue, TYPE_BLINK));
        //listItems.add(new ListNode(R.drawable.ic_img, "Image", "", "", "", R.drawable.bg_cloud, TYPE_IMAGE));
        //listItems.add(new ListNode(R.drawable.ic_qrcode, "My Profile", "", "", "", R.drawable.img_qrcode, TYPE_QRCODE));
        //listItems.add(new ListNode(R.drawable.ic_setting, "Setting", "", "", "", R.drawable.img_qrcode, TYPE_SETTING));
    }

    @Override
    public void onClick(WearableListView.ViewHolder viewHolder) {

        ListNode node = listItems.get(viewHolder.getPosition());

        //Toast.makeText(this, String.format(node.title), Toast.LENGTH_SHORT).show();

        // Start an intent
        Intent intent = new Intent(this, ShowImageActivity.class);
        intent.putExtra("title", node.title);
        intent.putExtra("text", node.text);
        intent.putExtra("value", node.value);
        intent.putExtra("format", node.format);
        intent.putExtra("rid", node.rid);
        intent.putExtra("type", node.type);
        startActivity(intent);
    }

    @Override
    public void onTopEmptyRegionClick() {
        //Toast.makeText(this, "You tapped Top empty area", Toast.LENGTH_SHORT).show();
    }

    public void makeToast(String str)
    {
        Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
    }

    public class MyListAdapter extends WearableListView.Adapter {

        @Override
        public WearableListView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            return new WearableListView.ViewHolder(new MyItemView(WearActivity.this));
        }

        @Override
        public void onBindViewHolder(WearableListView.ViewHolder viewHolder, int i) {
            MyItemView itemView = (MyItemView) viewHolder.itemView;

            ListNode node = listItems.get(i);

            TextView txtView = (TextView) itemView.findViewById(R.id.text);
            txtView.setText(node.title);

            CircledImageView imgView = (CircledImageView) itemView.findViewById(R.id.image);

            if( node.type == TYPE_BARCODE ) {
                if (node.icon_index > 0)
                    imgView.setImageResource(providers.get(node.icon_index).icon_rid);
                else
                    imgView.setImageResource(R.drawable.ic_card);
            }
            else
            {
                imgView.setImageResource(node.icon_index);
            }
        }

        @Override
        public int getItemCount() {
            return listItems.size();
        }
    }

    public class MessageReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            String message = intent.getStringExtra("message");
            // Display message in UI
            Log.e("@@@ REC", "Message received on Main Activity: " + message);
            config.writeStringJSON(message);
            makeToast("Item Synced");
        }
    }

    private final class MyItemView extends FrameLayout implements WearableListView.Item {

        final CircledImageView imgView;
        final TextView txtView;
        private float mScale;
        private final int mFadedCircleColor;
        private final int mChosenCircleColor;

        public MyItemView(Context context) {
            super(context);
            View.inflate(context, R.layout.row_advanced_item_layout, this);
            imgView = (CircledImageView) findViewById(R.id.image);
            txtView = (TextView) findViewById(R.id.text);
            mFadedCircleColor = getResources().getColor(android.R.color.darker_gray);
            mChosenCircleColor = getResources().getColor(android.R.color.holo_blue_dark);
        }

        @Override
        public float getProximityMinValue() {
            return mDefaultCircleRadius;
        }

        @Override
        public float getProximityMaxValue() {
            return mSelectedCircleRadius;
        }

        @Override
        public float getCurrentProximityValue() {
            return mScale;
        }

        @Override
        public void setScalingAnimatorValue(float value) {
            //mScale = value;
            //imgView.setCircleRadius(mScale);
            //imgView.setCircleRadiusPressed(mScale);
        }

        @Override
        public void onScaleUpStart() {
            imgView.setAlpha(1f);
            txtView.setAlpha(1f);
            imgView.setCircleColor(mChosenCircleColor);
        }

        @Override
        public void onScaleDownStart() {
            imgView.setAlpha(0.5f);
            txtView.setAlpha(0.5f);
            imgView.setCircleColor(mFadedCircleColor);
        }
    }

}
