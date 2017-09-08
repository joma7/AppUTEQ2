package com.uteq.uteqdevelopers.noticiasuteq.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by JOMA on 28/08/2017.
 */
public class Sesion {
    String usuarioWS;
    String claveWS;
    public boolean verificarUsuarioClave(String usuario, String clave){
        boolean r=false;
        if (usuario.equals(usuarioWS)&& clave.equals(claveWS)){
            r=true;
        }
        return r;
    }
    public void guardarSesion(Context context, String nombres, String usuario, String clave){
        SharedPreferences sharedPreferences= context.getSharedPreferences("Sesion", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putString("usuario", usuario);
        editor.putString("clave", clave);
        editor.putString("nombres", nombres);
        editor.commit();
    }
}
