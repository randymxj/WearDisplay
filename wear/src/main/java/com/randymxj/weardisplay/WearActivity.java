package com.randymxj.weardisplay;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.wearable.view.CircledImageView;
import android.support.wearable.view.WatchViewStub;
import android.support.wearable.view.WearableListView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;

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

    private WearableListView mListView;
    private MyListAdapter mAdapter;

    private float mDefaultCircleRadius;
    private float mSelectedCircleRadius;

    // Class for the list nodes
    public class ListNode {
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

    public ArrayList<ListNode> listItems = new ArrayList<ListNode>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listview);

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
        listItems.add(new ListNode(R.drawable.ic_cvs, "CVS ExtraCare", "", "", R.drawable.img_barcode_cvs, TYPE_BARCODE));
        listItems.add(new ListNode(R.drawable.ic_card, "Stop&Shop", "", "", R.drawable.img_barcode_stopshop, TYPE_BARCODE));
        listItems.add(new ListNode(R.drawable.ic_avis, "Avis Preferred", "4EC55M", "", 0, TYPE_TEXT));
        listItems.add(new ListNode(R.drawable.ic_umbrella, "Green Color", "", "", R.color.green, TYPE_COLOR));
        listItems.add(new ListNode(R.drawable.ic_colorwheel, "Blue Blink", "", "", R.color.blue, TYPE_BLINK));
        listItems.add(new ListNode(R.drawable.ic_img, "Image", "", "", R.drawable.bg_cloud, TYPE_IMAGE));
        listItems.add(new ListNode(R.drawable.ic_qrcode, "My Profile", "", "", R.drawable.img_qrcode, TYPE_QRCODE));
        listItems.add(new ListNode(R.drawable.ic_setting, "Setting", "", "", R.drawable.img_qrcode, TYPE_SETTING));
    }

    @Override
    public void onClick(WearableListView.ViewHolder viewHolder) {

        ListNode node = listItems.get(viewHolder.getPosition());

        //Toast.makeText(this, String.format(node.title), Toast.LENGTH_SHORT).show();

        // Start an intent
        Intent intent = new Intent(this, ShowImageActivity.class);
        intent.putExtra("TYPE", node.type);
        intent.putExtra("TITLE", node.title);
        intent.putExtra("TEXT", node.text);
        intent.putExtra("RID", node.rid);
        startActivity(intent);
    }

    @Override
    public void onTopEmptyRegionClick() {
        //Toast.makeText(this, "You tapped Top empty area", Toast.LENGTH_SHORT).show();
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
            imgView.setImageResource(node.icon_id);
        }

        @Override
        public int getItemCount() {
            return listItems.size();
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
