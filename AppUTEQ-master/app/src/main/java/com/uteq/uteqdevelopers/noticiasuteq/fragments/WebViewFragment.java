package com.uteq.uteqdevelopers.noticiasuteq.fragments;


import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.uteq.uteqdevelopers.noticiasuteq.utils.Constants;
import com.uteq.uteqdevelopers.noticiasuteq.utils.SaveImage;
import com.uteq.uteqdevelopers.noticiasuteq.utils.UIUtil;
import com.ogaclejapan.smarttablayout.utils.v4.Bundler;
import com.uteq.uteqdevelopers.noticiasuteq.R;


public class WebViewFragment extends android.support.v4.app.Fragment {

    View view;
    Bundle bundle;
    private WebView webview;


    public WebViewFragment() {
        // Required empty public constructor
    }

    public static WebViewFragment newInstance(String url,boolean zoom) {
        WebViewFragment f = new WebViewFragment();
        f.setArguments(arguments(url,zoom));
        return f;
    }

    public static Bundle arguments(String url, boolean zoom) {
        return new Bundler()
                .putString("url", url)
                .putBoolean("zoom",zoom)
                .get();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_web_view, container, false);
        bundle = getArguments();
        if (!UIUtil.verificaConexion(view.getContext())) {
            Toast.makeText(view.getContext(), Constants.MSG_COMPROBAR_CONEXION_INTERNET, Toast.LENGTH_SHORT).show();
        } else {
            final ProgressDialog progressDialog = new ProgressDialog(view.getContext());
            progressDialog.setProgressStyle(ProgressDialog.BUTTON_POSITIVE);//Aqui podemos cambiar el estilo del mensaje de carga
            progressDialog.setIcon(R.mipmap.ic_launcher);
            progressDialog.setMessage("Cargando...");
            progressDialog.show();
            webview = (WebView) view.findViewById(R.id.wvMenuUniversidaad);

            String url = bundle.getString("url");

            //JavaScript
            webview.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
            webview.getSettings().setCacheMode(WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE);
            webview.getSettings().setDomStorageEnabled(true);
            webview.getSettings().setDatabaseEnabled(true);
            webview.getSettings().setAppCacheEnabled(true);
            webview.getSettings().setJavaScriptEnabled(true);
            webview.setFocusable(true);
            webview.setFocusableInTouchMode(true);


            webview.loadUrl(url);
            webview.setWebViewClient(new WebViewClient() {
                // android:configChanges="keyboard|keyboardHidden|orientation|screenLayout|uiMode|screenSize|smallestScreenSize"
                //android:configChanges="keyboard|keyboardHidden|orientation|screenSize">

                @Override
                public void onPageFinished(WebView view, String url) {
                    super.onPageFinished(view, url);
                    //esto elimina ProgressBar.
                    progressDialog.dismiss();
                }

            });
            if (bundle.getBoolean("zoom")){
                webview.getSettings().setBuiltInZoomControls(true);
                webview.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        //convertir imagen a bitmap
                        webview.buildDrawingCache();
                        Bitmap bmap = webview.getDrawingCache();

                        //guardar imagen
                        SaveImage savefile = new SaveImage();
                        savefile.SaveImage(getContext(), bmap);
                        return true;
                    }
                });
            }
            webview.setOnKeyListener(new View.OnKeyListener() {
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if ((keyCode == KeyEvent.KEYCODE_BACK) && webview.canGoBack()) {
                        webview.goBack();
                        return true;
                    }
                    return false;
                }
            });


        }
        return view;
    }




    @Override
    public void onResume() {
        if(bundle.getString("titulo")!=null){
            Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
            toolbar.setTitle(bundle.getString("titulo"));
        }
        super.onResume();
    }

    public boolean canGoBack() {
        return webview.canGoBack();
    }
}