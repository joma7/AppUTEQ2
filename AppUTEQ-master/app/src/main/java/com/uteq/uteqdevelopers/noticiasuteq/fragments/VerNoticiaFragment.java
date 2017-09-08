package com.uteq.uteqdevelopers.noticiasuteq.fragments;


import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.uteq.uteqdevelopers.noticiasuteq.activity.MainActivity;
import com.uteq.uteqdevelopers.noticiasuteq.utils.Constants;
import com.uteq.uteqdevelopers.noticiasuteq.utils.SaveImage;
import com.uteq.uteqdevelopers.noticiasuteq.utils.UIUtil;
import com.uteq.uteqdevelopers.noticiasuteq.utils.WebServ.Asynchtask;
import com.uteq.uteqdevelopers.noticiasuteq.utils.WebServ.WebService;
import com.uteq.uteqdevelopers.noticiasuteq.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class VerNoticiaFragment extends Fragment implements Asynchtask {

    View view;
    String id;
    private String idNoti;
    private ImageView imgNoticia;
    private TextView txtTitulo;
    String ip;

    private static final String TAG = MainActivity.class.getSimpleName();
    public VerNoticiaFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_ver_noticia, container, false);
        Bundle b = getArguments();
        id = b.getString("id");
        Log.e(TAG, "ID: " + id);
        ConectWSNoticias();
        return view;
    }

    private void ConectWSNoticias() {
        if (!UIUtil.verificaConexion(view.getContext())) {
            Toast.makeText(view.getContext(), Constants.MSG_COMPROBAR_CONEXION_INTERNET, Toast.LENGTH_LONG).show();
        } else {
            ip= "http://"+UIUtil.ipAConetarse(view.getContext());
            Map<String, String> params = new HashMap<>();
            WebService ws;
            ws = new WebService(ip+Constants.WS_NOTICIAID + id, params, view.getContext(), VerNoticiaFragment.this);
            ws.execute("");
        }
    }

    @Override
    public void processFinish(String result) throws JSONException, ParseException {

        JSONArray objdataarray = new JSONArray(result);

        TextView txtCategoria = (TextView) view.findViewById(R.id.TxtCategoria);
        TextView txtFecha = (TextView) view.findViewById(R.id.txtFecha);
        txtTitulo = (TextView) view.findViewById(R.id.txtTitulo);
        TextView txtIntro = (TextView) view.findViewById(R.id.txtIntroduccion);
        TextView txtContenido = (TextView) view.findViewById(R.id.txtcontenido);
        imgNoticia = (ImageView) view.findViewById(R.id.imgNoti);
        JSONObject jsonObj = objdataarray.getJSONObject(0);
        //Extrayendo datos del JSON e insertando en cada campo
        txtCategoria.setText("Noticia " + jsonObj.getString("categoria"));
        txtFecha.setText(jsonObj.getString("publicacion").substring(0, 10).concat(" | ").concat(UIUtil.longevidad(jsonObj.getString("publicacion").substring(0, 10))));
        txtTitulo.setText(Html.fromHtml(jsonObj.getString("titulo")));
        txtIntro.setText(Html.fromHtml(jsonObj.getString("intro")));
        txtContenido.setText(Html.fromHtml(jsonObj.getString("texto")));

        idNoti = jsonObj.getString("url");
        if (!idNoti.substring(0,1).equals("/")){
            idNoti = "/"+idNoti;
        }
        Glide.with(this)
                .load(Constants.URL_UTEQ.concat(idNoti))
                .crossFade()
                .error(R.drawable.logouteqminres)
                .into(imgNoticia);
        imgNoticia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager=getFragmentManager();
                DialogoFragment fragment =(DialogoFragment) fragmentManager.findFragmentByTag(DialogoFragment.TAG);
                if(fragment==null){
                    fragment=new DialogoFragment();
                    Bundle b = new Bundle();
                    b.putString("url",Constants.URL_UTEQ.concat(idNoti));
                    b.putString("titulo",txtTitulo.getText().toString());
                    fragment.setArguments(b);
                }
                fragment.show(getFragmentManager(), DialogoFragment.TAG);
                Toast.makeText(view.getContext(), Constants.MSG_CARGANDO_IMAGEN,Toast.LENGTH_SHORT).show();
            }
        });
        imgNoticia.setOnLongClickListener(new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View arg0) {

                //convertir imagen a bitmap
                imgNoticia.buildDrawingCache();
                Bitmap bmap = imgNoticia.getDrawingCache();

                //guardar imagen
                SaveImage savefile = new SaveImage();
                savefile.SaveImage(getContext(), bmap);
                return true;
            }
        });
    }

    public String TiempoPasado(){
        return "Publicado hace mucho tiempo";
    }

    @Override
    public void onResume() {
        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        toolbar.setTitle("Noticia - UTEQ");
        super.onResume();
    }

}