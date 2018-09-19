package com.example.giovannihidalgo.moka_logo;


import android.os.Bundle;
import android.app.Fragment;
import android.app.Activity;
//import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import Moka7.*;


/**
 * A simple {@link Fragment} subclass.
 */
public class ConnectionFragment extends Fragment {


    boolean isConnected;
    View mainView;
    PlcCon plc;
    String szIP;
    int iRack;
    int iSlot;
    int iMemoryAdd;
    Byte bMemoryVal;
    EditText txtMsgBox;

    public ConnectionFragment() {
        // Required empty public constructor
        plc = new PlcCon();
    }

    @Override
    public void onDestroyView()
    {
       super.onDestroyView();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mainView = inflater.inflate(R.layout.fragment_connection, container, false);

        Button bnConnect = (Button)mainView.findViewById(R.id.btnConnect);
        bnConnect.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                EditText ipAdd = (EditText) mainView.findViewById(R.id.txtIpAddress);
                EditText rack = (EditText) mainView.findViewById(R.id.txtRackNo);
                EditText slot = (EditText) mainView.findViewById(R.id.txtSlotNo);
                txtMsgBox = (EditText)mainView.findViewById(R.id.txtMsg);

                Button bn = (Button) mainView.findViewById(R.id.btnUpdate);
                szIP = ipAdd.getText().toString();
                iRack = Integer.parseInt(rack.getText().toString());
                iSlot = Integer.parseInt(slot.getText().toString());

                new Thread(plc).start();
            }
        });

        Button bnUpdate = (Button)mainView.findViewById(R.id.btnUpdate);
        bnUpdate.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                int retVal;

                EditText mwAdd = (EditText)mainView.findViewById(R.id.txtMWAddress);
                EditText mwVal = (EditText)mainView.findViewById(R.id.txtValue);
                if (mwAdd.getText().toString()!="") {
                    iMemoryAdd = Integer.parseInt(mwAdd.getText().toString());
                }
                else
                {
                    iMemoryAdd =0;
                }
                if (mwVal.getText().toString()!="")
                {
                    bMemoryVal =Byte.parseByte(mwVal.getText().toString());
                }
                else {
                    bMemoryVal = 0;
                }

                // write to plc
                plc.isWrite = true;
                new Thread(plc).start();
            }
        });
        return mainView;
        //return inflater.inflate(R.layout.fragment_connection, container, false);

    }




    private class PlcCon implements Runnable{

        protected final S7Client client;
        byte[] buffer;
        Boolean isConnected;
        public Boolean isWrite;

        public PlcCon()
        {
            client = new S7Client();
            buffer = new byte[1];
            isConnected = false;
            isWrite =false;
        }

        public void run(){
            if (isConnected==false) {
                connectPLC(szIP, iRack, iSlot);
                updateGUI();
            }
            updateValue();
        }

        private void updateGUI() {
            try {
                if (isConnected) {
                    Button bn = (Button) mainView.findViewById(R.id.btnUpdate);
                    bn.setEnabled(true);
                    txtMsgBox.setText("Successful, PLC connected.");
                    //txtMsgBox.invalidate();
                } else {
                    Button bn = (Button) mainView.findViewById(R.id.btnUpdate);
                    bn.setEnabled(false);
                    txtMsgBox.setText("Fail to connect PLC.");
                    //txtMsgBox.invalidate();
                }
            } // end try
            catch (Exception exp) {

            }
        }

        public void connectPLC(String szIpAddress, int iRack, int iSlot)
        {

            try {
                int retVal=0;
                client.SetConnectionType(S7.S7_BASIC);
                retVal = client.ConnectTo(szIpAddress, iRack, iSlot);
                if (retVal==0)
                {
                    // connection successful
                    isConnected = true;
                }
                else
                {
                    // connection fail
                    isConnected = false;
                }
            } // end try
            catch (RuntimeException exp) {
                isConnected = false;
            }// end catch

        }

        public void updateValue()
        {
            try
            {
                int retVal;
                if (isWrite && isConnected){
                    // send data
                    buffer[0] = bMemoryVal;
                    retVal = client.WriteArea(S7.S7AreaMK, 0, iMemoryAdd,1, buffer);
                    if (retVal==0)
                    {
                        txtMsgBox.setText("Write Successful.");
                        //txtMsgBox.invalidate();
                    }
                    else {
                        txtMsgBox.setText("Write fail.");
                        //txtMsgBox.invalidate();
                    }
                    isWrite = false;
                }
                isWrite = false;
            }// end try
            catch (Exception exp){

            }
        }
    }

}
