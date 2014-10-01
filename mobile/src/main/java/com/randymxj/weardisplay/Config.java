package com.randymxj.weardisplay;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Created by 502021 on 9/30/2014.
 */
public class Config {

    public ArrayList<PhoneActivity.ListNode> items = new ArrayList<PhoneActivity.ListNode>();

    private PhoneActivity main;

    public Config( PhoneActivity m )
    {
        main = m;
    }

    public void writeItems()
    {
        String filename = "items.json";
        FileOutputStream outputStream;

        JSONObject root = new JSONObject();
        JSONArray item_array = new JSONArray();

        for( int i = 0; i < items.size(); i++ )
        {
            JSONObject item = new JSONObject();
            PhoneActivity.ListNode node = items.get(i);

            try
            {
                item.put("title", node.title);
                item.put("text", node.text);
                item.put("value", node.value);
                item.put("format", node.format);
                item.put("type", node.type);

                item_array.put(item);
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
        }

        try
        {
            root.put("items", item_array);
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }

        try
        {
            outputStream = main.openFileOutput(filename, Context.MODE_PRIVATE);
            outputStream.write(root.toString().getBytes());
            outputStream.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        //Log.e("@@@ WRITE", root.toString());

    }

    public void readTracker()
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

                String title = item.getString("title");
                String text = item.getString("text");
                String value = item.getString("value");
                String format = item.getString("format");
                int type = item.getInt("type");

                PhoneActivity.ListNode node = new PhoneActivity.ListNode(0, title, text, value, format, 0, type);
                items.add(node);
            }

            //Log.e("@@@ READ", root.toString());

            fis.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


}
