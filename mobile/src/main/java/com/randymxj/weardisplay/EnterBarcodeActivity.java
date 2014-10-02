package com.randymxj.weardisplay;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Map;


public class EnterBarcodeActivity extends Activity implements View.OnClickListener {

    private Button capturecode_button, savecode_button;
    private Spinner type_spinner;
    private ArrayAdapter<CharSequence> type_spinner_adapter;
    private ImageView logo_imageView, barcode_imageView;
    private EditText value_editText, title_editText, description_editText;
    private Bitmap code_bitmap;

    // Variable
    private int selectedProviderIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enterbarcode);

        // Capture barcode Button
        capturecode_button = (Button) findViewById(R.id.capturecode_button);
        capturecode_button.setOnClickListener(this);

        // Type
        type_spinner = (Spinner) findViewById(R.id.code_type_spinner);
        type_spinner_adapter = ArrayAdapter.createFromResource(this, R.array.code_type_array, android.R.layout.simple_spinner_item);
        type_spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        type_spinner.setAdapter(type_spinner_adapter);

        // Logo image
        logo_imageView = (ImageView) findViewById(R.id.logo_imageView);

        // Barcode image
        barcode_imageView = (ImageView) findViewById(R.id.barcode_imageView);

        // Barcode value
        value_editText = (EditText) findViewById(R.id.code_value_editText);

        // Barcode title
        title_editText = (EditText) findViewById(R.id.code_title_editText);

        // Barcode description
        description_editText = (EditText) findViewById(R.id.code_description_editText);

        // Save barcode button
        savecode_button = (Button) findViewById(R.id.savecode_button);
        savecode_button.setOnClickListener(this);

        // Build Member Card Provider List
        String list[] = new String[PhoneActivity.providers.size()];
        for( int i = 0; i < PhoneActivity.providers.size(); i++ )
        {
            list[i] = PhoneActivity.providers.get(i).title;
        }

        // Pre Selection Dialog
        new AlertDialog.Builder(this)
                .setTitle("Pick A Member Card")
                .setItems(list, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // The 'which' argument contains the index position
                        // of the selected item

                        // Customize Card
                        if (which == 0)
                        {
                            ((LinearLayout) logo_imageView.getParent()).removeView(logo_imageView);
                            return;
                        }

                        selectedProviderIndex = which;

                        PhoneActivity.MemberCardProvider node = PhoneActivity.providers.get(which);
                        Log.e("@@@", node.title);

                        title_editText.setText(node.title);
                        description_editText.setText(node.text);
                        type_spinner.setSelection(type_spinner_adapter.getPosition(node.format));

                        if (node.icon_rid > 0)
                            logo_imageView.setImageResource(node.icon_rid);
                    }
                })
                .show();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        if( id == R.id.capturecode_button )
        {
            // Read data from ZXing
            try
            {
                Intent intent = new Intent("com.google.zxing.client.android.SCAN");
                //intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
                startActivityForResult(intent, 0);
            }
            catch (Exception e)
            {
                Uri marketUri = Uri.parse("market://details?id=com.google.zxing.client.android");
                Intent marketIntent = new Intent(Intent.ACTION_VIEW,marketUri);
                startActivity(marketIntent);
            }
        }
        else if( id == R.id.savecode_button )
        {
            int item_type = PhoneActivity.TYPE_BARCODE;
            String str_value = value_editText.getText().toString();
            String str_title = title_editText.getText().toString();
            String str_text = description_editText.getText().toString();
            String code_type = type_spinner.getSelectedItem().toString();

            if( str_title.length() == 0 )
            {
                new AlertDialog.Builder(this)
                        .setTitle("Incomplete Information")
                        .setMessage("Please enter a title for this item")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // Nothing
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();

                return;
            }
            else if( str_value.length() == 0 )
            {
                new AlertDialog.Builder(this)
                        .setTitle("Incomplete Information")
                        .setMessage("Please enter a card number or scan the card by camera for this item")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // Nothing
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();

                return;
            }

            Intent intent = new Intent();
            intent.putExtra("cmd", PhoneActivity.NEW_ITEM);
            intent.putExtra("icon_index", selectedProviderIndex);
            intent.putExtra("type", item_type);
            intent.putExtra("value", str_value);
            intent.putExtra("title", str_title);
            intent.putExtra("text", str_text);
            intent.putExtra("format", code_type);

            setResult(RESULT_OK, intent);
            finish();
        }
    }


    public void onActivityResult(int requestCode, int resultCode, Intent intent) {

        if (requestCode == 0)
        {
            if (resultCode == RESULT_OK)
            {
                String contents = intent.getStringExtra("SCAN_RESULT");
                String format = intent.getStringExtra("SCAN_RESULT_FORMAT");
                // Handle successful scan

                String barcode_data = contents;

                value_editText.setText(barcode_data);

                type_spinner.setSelection(type_spinner_adapter.getPosition(format));

                Log.e("@@@ Format", format);
                Log.e("@@@ Index", String.valueOf(type_spinner_adapter.getPosition(format)));

                try
                {
                    code_bitmap = PhoneActivity.encodeAsBitmap(barcode_data, format, 600, 300);
                    barcode_imageView.setImageBitmap(code_bitmap);

                }
                catch (WriterException e)
                {
                    e.printStackTrace();
                }

            }
            else if (resultCode == RESULT_CANCELED)
            {
                // Handle cancel
            }
        }
    }

    /*
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
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    */

}
