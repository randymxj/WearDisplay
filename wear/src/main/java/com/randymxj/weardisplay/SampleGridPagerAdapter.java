/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.randymxj.weardisplay;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.support.wearable.view.CardFragment;
import android.support.wearable.view.FragmentGridPagerAdapter;
import android.support.wearable.view.ImageReference;
import android.view.Gravity;

/**
 * Constructs fragments as requested by the GridViewPager. For each row a
 * different background is provided.
 */
public class SampleGridPagerAdapter extends FragmentGridPagerAdapter {

    private final Context mContext;

    public SampleGridPagerAdapter(Context ctx, FragmentManager fm) {
        super(fm);
        mContext = ctx;
    }

    static final int BG_DEFAULT = R.drawable.bg_cloud;

    static final int[] BG_IMAGES = new int[] {
            R.drawable.debug_background_1,
            R.drawable.debug_background_2,
            R.drawable.debug_background_3,
            R.drawable.debug_background_4,
            R.drawable.debug_background_5
    };

    /** A simple container for static data in each page */
    private static class Page {
        String titleRes;
        String textRes;
        int iconRes;
        int bg;
        int cardGravity = Gravity.BOTTOM;
        boolean expansionEnabled = true;
        float expansionFactor = 1.0f;
        int expansionDirection = CardFragment.EXPAND_DOWN;

        /*
        public Page(String titleRes, String textRes, boolean expansion) {
            this(titleRes, textRes, 0);
            this.expansionEnabled = expansion;
        }

        public Page(String titleRes, String textRes, boolean expansion, float expansionFactor) {
            this(titleRes, textRes, 0);
            this.expansionEnabled = expansion;
            this.expansionFactor = expansionFactor;
        }
        */

        public Page(String titleRes, String textRes, int iconRes, int bg) {
            this.titleRes = titleRes;
            this.textRes = textRes;
            this.iconRes = iconRes;
            this.bg = bg;
        }

        public Page(String titleRes, String textRes, int iconRes, int bg, int gravity) {
            this.titleRes = titleRes;
            this.textRes = textRes;
            this.iconRes = iconRes;
            this.bg = bg;
            this.cardGravity = gravity;
        }
    }

    private final Page[][] PAGES = {
            {
                    new Page("Wear Display", "To display a QRcode, Barcode or any img you want", R.drawable.bugdroid, 0, Gravity.TOP),
            },
            {
                    new Page("Member Cards", "", R.drawable.bugdroid, 0),
                    new Page("Card 1 - QRCode", "", R.drawable.bugdroid, R.drawable.bg_qrcode),
                    new Page("Card 2 - Barcode", "", R.drawable.bugdroid, R.drawable.bg_barcode),
                    new Page("Card 3", "", 0, 0),
                    new Page("Card 4", "", 0, 0),
            },
            {
                    new Page("Page 2", "Other 0", 0, 0),
                    new Page("Page 2", "Other 1", 0, 0),
            },
            {
                    new Page("Page 3", "Other 0", 0, 0),
                    new Page("Page 3", "Other 1", 0, 0),
            },
            {
                    new Page("Page 4", "This is the last page, swipe right to exit", R.drawable.bugdroid, 0, Gravity.TOP),
            },

    };

    @Override
    public Fragment getFragment(int row, int col) {
        Page page = PAGES[row][col];
        String title = page.titleRes;
        String text = page.textRes;
        CardFragment fragment = CardFragment.create(title, text, page.iconRes);
        // Advanced settings
        fragment.setCardGravity(page.cardGravity);
        fragment.setExpansionEnabled(page.expansionEnabled);
        fragment.setExpansionDirection(page.expansionDirection);
        fragment.setExpansionFactor(page.expansionFactor);
        return fragment;
    }

    @Override
    public ImageReference getBackground(int row, int col) {
        Page page = PAGES[row][col];

        if( page.bg > 0 )
            return ImageReference.forDrawable(page.bg);
        else
            return ImageReference.forDrawable(BG_DEFAULT);
    }

    @Override
    public int getRowCount() {
        return PAGES.length;
    }

    @Override
    public int getColumnCount(int rowNum) {
        return PAGES[rowNum].length;
    }
}
