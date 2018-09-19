package com.example.giovannihidalgo.moka_logo;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;


public class MainActivity extends AppCompatActivity {

    Button botontemp, BotonPantalla;


    ////////////////////////////////////////////////////////////////
    FragmentManager fm;
    FragmentTransaction ft;
    ConnectionFragment cf;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cf = new ConnectionFragment();

        fm = getFragmentManager();
        ft = fm.beginTransaction();
        ft.add(R.id.MainLayout, cf);
        ft.commit();
        ////////////////////////////////////////////////////////////
    }


}
