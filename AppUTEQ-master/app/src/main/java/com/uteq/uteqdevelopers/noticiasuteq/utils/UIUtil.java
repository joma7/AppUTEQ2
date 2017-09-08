package com.uteq.uteqdevelopers.noticiasuteq.utils;

import android.animation.Animator;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.transition.Fade;
import android.transition.Slide;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.Window;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.RunnableFuture;

/**
 * Created by varsovski on 28-Sep-15.
 */
public class UIUtil {

    public static void SlideWindowTransition(Window window) {
        Slide slide = new Slide();
        slide.setDuration(1000);
        window.setExitTransition(slide);
    }

    public static void FadeWindowTransition(Window window) {
        Fade fade = new Fade();
        fade.setDuration(1000);
        window.setEnterTransition(fade);
    }


    // public static WebView webview;
    //metodo verficar conexion a internet
    public static boolean verificaConexion(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isAvailable() && networkInfo.isConnected();
    }
    public static boolean verificaConexion1(Context ctx) {
        boolean bConectado = false;
        ConnectivityManager connec = (ConnectivityManager) ctx
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        // No sólo wifi, también GPRS
        NetworkInfo[] redes = connec.getAllNetworkInfo();
        // este bucle debería no ser tan ñapa
        for (int i = 0; i < 2; i++) {
            // ¿Tenemos conexión? ponemos a true
            if ((redes[i].getState() == NetworkInfo.State.CONNECTED)) {
                bConectado = true;
            }
        }
        return bConectado;
    }
    public static boolean isOnlineNet() {

        try {
            Process p = java.lang.Runtime.getRuntime().getRuntime().exec(Constants.PING_INTERNET);
            int val           = p.waitFor();
            boolean reachable = (val == 0);
            return reachable;

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return false;
    }
    //fin de "metodo verficar conexion a internet"


    ///// Animacion para los CardView a medida que se cargan o eliminan de la Vista /////
    public static void animateCircularReveal(View view) {
        int centerX = 0;
        int centerY = 0;
        int startRadius = 0;
        int endRadius = Math.max(view.getWidth(), view.getHeight());
        Animator animation = ViewAnimationUtils.createCircularReveal(view, centerX, centerY, startRadius, endRadius);
        view.setVisibility(View.VISIBLE);
        animation.start();
    }
    /////// Fin Animacion ///////////////


    //Método temporal para la validación de la red a la que está conectado el usuario
    public static String ssid(Context ctx) {
        try {
            String ssid="";
            WifiManager wifiMgr = (WifiManager) ctx.getSystemService(Context.WIFI_SERVICE);
            if(wifiMgr.isWifiEnabled()) {
                WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
                ssid = wifiInfo.getSSID();
            }
            return ssid;
        } catch (Exception ex) {
            return "Error: " + ex;
        }
    }

    public static String ipAConetarse(Context ctx) {

        //String ssid = ssid(ctx);
        //ssid = (ssid.length() > 0)?ssid.substring(1, ssid.length() - 1):"";
        String ipRetorno = Constants.IP_PUBLICA;

        /*if (ssid.equals("Comedor Universitario") || ssid.equals("lidertics1") || ssid.equals("Wifi_UTEQ") || ssid.equals("Docentes")) {
            ipRetorno = Constants.IP_LOCAL;
        }*/
        return ipRetorno;
    }

    public static boolean isNumeric(String a) {
        boolean r = false;
        try {
            Integer.valueOf(a);
            r = true;
        } catch (Exception ignored) {
        }
        return r;
    }

    public static String longevidad(String publicacion) throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date fecha = format.parse(publicacion);
        String longevidad = "Publicado hace ";
        java.util.Date fechaActual = new Date();
        long diferenciaEn_ms = fechaActual.getTime() - fecha.getTime();
        long dias = diferenciaEn_ms / (1000 * 60 * 60 * 24);
        if (dias <= 30)
            longevidad += "días";
        else if (dias <= 45)
            longevidad += "más de un mes";
        else if (dias <= 360)
            longevidad += "meses";
        else if (dias <= 540)
            longevidad += "más de un año";
        else if (dias > 540)
            longevidad += "años";
        return longevidad;
    }


    public static boolean isOnline(Context context) {

        ConnectivityManager connectivityManager = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        RunnableFuture<Boolean> futureRun = new FutureTask<Boolean>(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                if ((networkInfo .isAvailable()) && (networkInfo .isConnected())) {
                    try {
                        HttpURLConnection urlc = (HttpURLConnection) (new URL("http://www.google.com").openConnection());
                        urlc.setRequestProperty("User-Agent", "Test");
                        urlc.setRequestProperty("Connection", "close");
                        urlc.setConnectTimeout(1500);
                        urlc.connect();
                        return (urlc.getResponseCode() == 200);
                    } catch (IOException e) {
                        //Log.e(TAG, "Error checking internet connection", e);
                    }
                } else {
                    //Log.d(TAG, "No network available!");
                }
                return false;
            }
        });

        new Thread(futureRun).start();


        try {
            return futureRun.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return false;
        }

    }
}
