package com.randymxj.weardisplay;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Created by 502021 on 9/30/2014.
 */
public class Config {

    public ArrayList<WearActivity.ListNode> items = new ArrayList<WearActivity.ListNode>();

    private WearActivity main;

    public Config( WearActivity m )
    {
        main = m;
    }

    public void writeStringJSON( String str )
    {
        String filename = "items.json";
        FileOutputStream outputStream;

        try
        {
            outputStream = main.openFileOutput(filename, Context.MODE_PRIVATE);
            outputStream.write(str.getBytes());
            outputStream.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void readItems()
    {
        items.clear();
        String filename = "items.json";

        try
        {
            FileInputStream fis = main.openFileInput( filename );

            if( fis.available() == 0 )
            {
                return;
            }

            StringBuffer buffer = new StringBuffer();
            BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
            String receiveString = "";

            while ((receiveString = reader.readLine()) != null)
            {
                buffer.append(receiveString + "\n" );
            }

            JSONObject root = new JSONObject(buffer.toString());
            JSONArray item_array = root.getJSONArray("items");

            for( int i = 0; i < item_array.length(); i++ )
            {
                JSONObject item = item_array.getJSONObject(i);

                int icon_index = item.getInt("icon_index");
                String title = item.getString("title");
                String text = item.getString("text");
                String value = item.getString("value");
                String format = item.getString("format");
                int type = item.getInt("type");

                WearActivity.ListNode node = new WearActivity.ListNode(icon_index, title, text, value, format, 0, type);
                items.add(node);
            }

            fis.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

}
