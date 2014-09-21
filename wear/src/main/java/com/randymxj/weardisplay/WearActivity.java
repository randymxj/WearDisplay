package com.randymxj.weardisplay;

import android.app.Activity;
import android.os.Bundle;
import android.support.wearable.view.GridViewPager;
import android.util.Log;

public class WearActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("@@@", "Wear Display Started 1");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.round_activity_wear);

        final GridViewPager mGridPager = (GridViewPager) findViewById(R.id.pager);
        mGridPager.setAdapter(new SampleGridPagerAdapter(this, getFragmentManager()));

        Log.d("@@@", "Wear Display Started 2");
    }
}
