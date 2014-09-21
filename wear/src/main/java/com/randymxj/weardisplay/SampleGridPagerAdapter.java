package com.randymxj.weardisplay;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.support.wearable.view.CardFragment;
import android.support.wearable.view.FragmentGridPagerAdapter;
import android.support.wearable.view.ImageReference;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Xiaojing on 9/19/2014.
 */
public class SampleGridPagerAdapter extends FragmentGridPagerAdapter {

    private final Context mContext;
    private ArrayList<SimpleRow> mPages;

    public SampleGridPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
        initPages();
    }

    private void initPages() {
        mPages = new ArrayList<SimpleRow>();

        Log.d("@@@", "SampleGridPagerAdapter 1");

        SimpleRow row1 = new SimpleRow();
        row1.addPages(new SimplePage("Card1-Index", "Supermarket Cards", 0, R.drawable.bg_cloud));
        row1.addPages(new SimplePage("Card1-1", "", 0, R.drawable.bg_qrcode));
        row1.addPages(new SimplePage("Card1-2", "", 0, R.drawable.bg_barcode));

        SimpleRow row2 = new SimpleRow();
        row2.addPages(new SimplePage("Card2-Others", "Minor Cards", R.drawable.ic_launcher, R.drawable.bg_cloud));
        row2.addPages(new SimplePage("Card2-1", "", R.drawable.ic_launcher, R.drawable.bg_cloud));
        row2.addPages(new SimplePage("Card2-2", "", R.drawable.ic_launcher, R.drawable.bg_cloud));

        mPages.add(row1);
        mPages.add(row2);

        Log.d("@@@", "SampleGridPagerAdapter 3");
    }

    @Override
    public Fragment getFragment(int row, int col) {
        SimplePage page = mPages.get(row).getPages(col);

        Log.d("@@@", "getFragment: " + page.mTitle);

        //SimpleCardFragment fragment =  SimpleCardFragment.newInstance(page.mTitle, page.mText, page.mIconId);

        CardFragment fragment =  CardFragment.create(page.mTitle, page.mText, page.mIconId);

        return fragment;
    }

    @Override
    public ImageReference getBackground(int row, int col) {
        SimplePage page = mPages.get(row).getPages(col);
        return ImageReference.forDrawable(page.mBackgroundId);
    }

    @Override
    public int getRowCount() {
        return mPages.size();
    }

    @Override
    public int getColumnCount(int row) {
        return mPages.get(row).size();
    }

    public static class SimpleCardFragment extends CardFragment {

        private static String mTitle = "";

        @Override
        public View onCreateContentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

            TextView text = new TextView(this.getActivity());
            text.setText(mTitle);
            text.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("@@@", "Click: " + mTitle);
                }
            });
            return text;
        }

        public static SimpleCardFragment newInstance( String title, String text, int iconId ) {
            mTitle = title;

            return new SimpleCardFragment();
        }
    }
}