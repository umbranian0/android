package com.example.vasil.photolocalizator;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private String nome ;
    private String idade;
    private Button btnOk;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnOk = (Button)findViewById(R.id.btnOk);
        //precisamos que os dados sejam enviados para o proximo layout
        //entao usamos um listener
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // precisamos de um intent que mande os dados para outra atividade
                Intent envia = new Intent(getApplicationContext(),Activity2_Foto.class);


                nome = String.valueOf(((TextView) findViewById(R.id.txtNome)).getText());
                idade = String.valueOf(((TextView)findViewById(R.id.txtIdade)).getText());
                //adicionamos os dados que queremos enviar
                envia.putExtra("nome",nome);
                envia.putExtra("idade",idade);
                //damos inicio a atividade
                startActivity(envia);

            }
        });
    }


}
