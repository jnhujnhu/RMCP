package com.example.kevin.mmcclient;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import java.lang.reflect.Field;


public class m_Dialog {

    private static LayoutInflater inflater;
    private static View layout;
    private UDPConnection con;
    private String ServerIP = "Not Connected";
    private Button rsButton;
    private Button ipButton;
    private final MainActivity handle;
    private int ChooseMode;
    public boolean DialogIsClosed;

    public View getView() {
        return layout;
    }

    public String getServerIP() {
        return ServerIP;
    }

    public int getMode() {
        return ChooseMode;
    }

    private void SearchServer() {
        String TempStr = con.StartSearching();
        if (TempStr != null) {
            ServerIP = TempStr;
        }
    }

    public m_Dialog(final MainActivity hand) {

        handle = hand;
        ChooseMode = 0;
        DialogIsClosed = false;
        inflater = LayoutInflater.from(handle);
        layout = inflater.inflate(R.layout.dialog, (ViewGroup)handle.findViewById(R.id.dialog));
        final AlertDialog.Builder builder = new AlertDialog.Builder(handle);
        builder.setTitle("Control Your Mouse Use:");
        builder.setView(layout);
        builder.setSingleChoiceItems(new String[]{"Touch Screen", "Gravity", "Acceleration(Unworkable)"}, 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ChooseMode = which;
            }
        });
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                canCloseDialog(dialog, false);
                if (con.isConnected) {
                    if(ServerIP.equals("Not Connected")) {
                        ServerIP = con.getServerIP();
                    }
                    Message msg = new Message();
                    msg.obj = "Engine Start.";
                    handle.HandleEntry.sendMessage(msg);
                    canCloseDialog(dialog, true);
                    DialogIsClosed = true;
                } else {
                    new AlertDialog.Builder(handle).setTitle("Warning").setMessage("No Connection!")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }).create().show();
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                canCloseDialog(dialog, false);
                DialogIsClosed = true;
                handle.finish();
            }
        });
        builder.create();
        builder.setCancelable(false);
        builder.show();


        con = new UDPConnection(handle);
        SearchServer();
        rsButton = (Button) layout.findViewById(R.id.rsbutton);

        rsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SearchServer();
            }
        });

        ipButton = (Button) layout.findViewById(R.id.IPbutton);

        ipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               IPButtonImp();
            }
        });

    }

    private void IPButtonImp() {

        final View IPinput = inflater.inflate(R.layout.ipinput, (ViewGroup)handle.findViewById(R.id.ipinput));
        new AlertDialog.Builder(handle).setTitle("Please enter the Server IP")
                .setView(IPinput)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        EditText et_ipinput = (EditText) IPinput.findViewById(R.id.IPinput);
                        InputMethodManager imm = (InputMethodManager)handle.getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(et_ipinput.getWindowToken(), 0);
                        String SavedInputIP = et_ipinput.getText().toString();
                        con.StartSearchingByIP(SavedInputIP);
                    }
                })
                .setNegativeButton("取消", null).show();
    }

    private void canCloseDialog(DialogInterface dialogInterface, boolean close) {
        try {
            Field field = dialogInterface.getClass().getSuperclass().getDeclaredField("mShowing");
            field.setAccessible(true);
            field.set(dialogInterface, close);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
