package com.example.vasil.photolocalizator;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.widget.Toast;

public class ServicoLocalizacao extends Service {
    //objetos necessarios para a localização
    private LocationManager manager;
    private LocationListener listener;
    @Override
    public IBinder onBind(Intent intent) {
              return null;
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onCreate() {
        listener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
            // transferir dados deste atividade para a main usando o broadcast
                Intent i = new Intent("location_update");
                //por inf ao longo do intent
                i.putExtra("coordenadas","long : "+location.getLongitude()+ "| lat :  " + location.getLatitude());
                //mandamos a mensagem
                sendBroadcast(i);
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override // caso o gps ta destivado
            public void onProviderDisabled(String s) {
            Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
            }
        };
//iniciar o location manager
        manager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        // o manager so vai ser updated de 3 em 3 seg
        manager.requestLocationUpdates(LocationManager.GPS_PROVIDER,3000,0,listener);

    }

  public void onDestroy(){
        super.onDestroy();
        if(manager != null){
            manager.removeUpdates(listener);
        }
  }
}
