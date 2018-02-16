package com.example.vasil.photolocalizator;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class Activity2_Foto extends AppCompatActivity {

    //declaramos uma variavel predefinida para poder comecar a actividade
    private static final int INICIAR_ACTIVIDADE = 0;

    //declaramos os location manager e listener
    //criar o reciever do broadcast
    private BroadcastReceiver broadcastReceiver;

    //declarar a view da fotoTirada
    private ImageView fotoTirada;
    private TextView txtLocalizacao;
    //ler butão de Tirar foto
    private Button btnTirar ;
    private Button btnLocalizacao;
    private Button btnStop;

    //declarar as variaveis para as idades
    private TextView txtNome ;
    private TextView txtIdade ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activity2__foto);
        //ler butão de Tirar foto
        btnTirar = (Button) findViewById(R.id.btnTirar);
        btnLocalizacao = (Button) findViewById(R.id.btnLoc);
        btnStop = (Button) findViewById(R.id.btnStop);
        txtLocalizacao = (TextView) findViewById(R.id.txtLocalizacao);
        txtNome =(TextView)findViewById(R.id.txtNome);
        txtIdade = (TextView)findViewById(R.id.txtIdade);
        //receber o intent anterior
        Intent intentReceber = getIntent();

        String nome = intentReceber.getExtras().getString("nome");
        txtNome.append(nome);
        String idade = intentReceber.getExtras().getString("idade");
        txtIdade.append(idade);

        //Tratar de tirar a foto
        //listner ao click no botão
        btnTirar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //criamos o intent para a foto
                Intent foto = new Intent();
                //dizer o que o intent faz (tirar foto)
                foto.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
                //iniciar atividade
                startActivityForResult(foto, INICIAR_ACTIVIDADE);
                  //iniciamos o serviço de ir buscar localização
                startService(new Intent(getBaseContext(), ServicoLocalizacao.class));
            }
        });

        //guarda imagem
        fotoTirada = (ImageView) findViewById(R.id.verFoto);

//serviço de localização

        if(!runtime_permissions()){//caso nao seja preciso permissoes
            enable_button();
        }

    }

    //metodo de iiciar o botao
    public void enable_button(){
        //criamos um evento de resposta ao click no botao da localização
        btnLocalizacao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent inte = new Intent (getApplicationContext(),ServicoLocalizacao.class);
                startService(inte);
            }
        });
        //para o serviço
        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent inte = new Intent(getApplicationContext(),ServicoLocalizacao.class);
                stopService(inte);
            }
        });
    }

    public boolean runtime_permissions(){
        if(Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){

            //pede as permissoes
            requestPermissions(new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION
            },100);//request code
            return true;
        }
        return false;
    }


    //resultado da atividade
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //verificamos 2 parametros
        if(requestCode==INICIAR_ACTIVIDADE && resultCode == RESULT_OK){
            Bundle extras = data.getExtras();//armazeno a inf
            //criar um bitmap pa imagem
            Bitmap photoBitmap = (Bitmap) extras.get("data");
            //da set na imagem sobre o view
            fotoTirada.setImageBitmap(photoBitmap);
            //mensagem de confirmacao
            //  Toast.makeText(this,"pic taken ",Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // faz algo se o codigo for 100
        if(requestCode == 100){
            //ve se os resultados correspondem ao que e necessario
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED){
                //chama o metodo para iniciar os botoes
                enable_button();
            }
            else{
                runtime_permissions();
            }
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        if(broadcastReceiver == null) {
            broadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    //por a inf no text view
                    txtLocalizacao.append(" " + intent.getExtras().get("coordenadas"));

                }
            };
        }
        registerReceiver(broadcastReceiver,new IntentFilter("location_update"));
    }

    //fazer o destroy do servico para o parar
    //precisamos de um ondestroy method

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(broadcastReceiver != null)
            unregisterReceiver(broadcastReceiver);

    }

}
