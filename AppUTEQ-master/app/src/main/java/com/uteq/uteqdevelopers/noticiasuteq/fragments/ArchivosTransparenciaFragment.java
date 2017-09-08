package com.uteq.uteqdevelopers.noticiasuteq.fragments;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.uteq.uteqdevelopers.noticiasuteq.R;
import com.uteq.uteqdevelopers.noticiasuteq.adapters.ArchivosTransparenciaAdapter;
import com.uteq.uteqdevelopers.noticiasuteq.model.ArchivoTransparencia;
import com.uteq.uteqdevelopers.noticiasuteq.utils.Constants;
import com.uteq.uteqdevelopers.noticiasuteq.utils.UIUtil;
import com.uteq.uteqdevelopers.noticiasuteq.utils.WebServ.Asynchtask;
import com.uteq.uteqdevelopers.noticiasuteq.utils.WebServ.WebService;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class ArchivosTransparenciaFragment extends android.support.v4.app.Fragment implements Asynchtask {


    View view;
    String tipoFragment;
    public ArchivosTransparenciaFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_archivos_transparencia, container, false);

        Bundle bundle = getArguments();
        tipoFragment=bundle.getString("tipoFragment");

        TextView txtEncabezado=(TextView) view.findViewById(R.id.txtEncabezadoTransparencia);
        txtEncabezado.setText(bundle.getString("encabezado"));
        String anio="";
        String ip= "http://"+ UIUtil.ipAConetarse(view.getContext());
        String urlWs="";
        if(tipoFragment.equals("tr")){
            anio=bundle.getString("idAno");
            urlWs=ip+ Constants.WS_LOTAIP_FECHA+anio+"/"+bundle.get("idMes").toString();}
        else if(tipoFragment.equals("rc")){
            String fase=bundle.getString("idMes").substring(5);
            anio=bundle.getString("idAno");
            urlWs=ip+ Constants.WS_RC_FECHA+anio+"/"+fase;
        }else if(tipoFragment.equals("ar")){
            urlWs=ip+Constants.WS_ARCHIVOS+bundle.getString("idCategoria");
        }



        Map<String, String> params = new HashMap<>();
        WebService ws= new WebService(urlWs, params,view.getContext(), ArchivosTransparenciaFragment.this);
        ws.execute("");

        return view;
    }

    @Override
    public void processFinish(String result) throws JSONException {
        JSONArray objdataarray= new JSONArray (result);
        ArrayList<ArchivoTransparencia> noticias = ArchivoTransparencia.JsonObjectsBuild(objdataarray,tipoFragment);

        ArchivosTransparenciaAdapter adaptadornoticias = new ArchivosTransparenciaAdapter(view.getContext(), noticias,tipoFragment);
        ListView lstOpciones = (ListView)view.findViewById(R.id.lstOpcionesAT);
        lstOpciones.setAdapter(adaptadornoticias);
    }

}
