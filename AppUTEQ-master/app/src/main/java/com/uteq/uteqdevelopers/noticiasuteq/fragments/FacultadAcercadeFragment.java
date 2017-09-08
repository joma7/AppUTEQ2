package com.uteq.uteqdevelopers.noticiasuteq.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.uteq.uteqdevelopers.noticiasuteq.utils.Constants;
import com.uteq.uteqdevelopers.noticiasuteq.utils.UIUtil;
import com.uteq.uteqdevelopers.noticiasuteq.utils.WebServ.Asynchtask;
import com.uteq.uteqdevelopers.noticiasuteq.utils.WebServ.WebService;
import com.uteq.uteqdevelopers.noticiasuteq.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class FacultadAcercadeFragment extends Fragment implements Asynchtask {

    View view;
    String id;
    TextView txtFacultad;
    TextView txtMision;
    TextView txtVision;
    TextView txtObjetivo;
    ImageView imgLogo;
    String ip;

    public FacultadAcercadeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_facultad_acercade, container, false);
        Bundle b = getArguments();
        id = b.getString("id");
        ConectWSUnidad();
        txtFacultad = (TextView) view.findViewById(R.id.txtFacultad);
        txtMision = (TextView) view.findViewById(R.id.txtMision);
        txtVision = (TextView) view.findViewById(R.id.txtVision);
        txtObjetivo = (TextView) view.findViewById(R.id.txtObjetivo);
        imgLogo = (ImageView) view.findViewById(R.id.imgLogo);

        return view;
    }

    private void ConectWSUnidad() {
        if (!UIUtil.verificaConexion(view.getContext())) {
            Toast.makeText(view.getContext(), Constants.MSG_COMPROBAR_CONEXION_INTERNET, Toast.LENGTH_LONG).show();
        } else {
            ip= "http://"+UIUtil.ipAConetarse(view.getContext());
            Map<String, String> params = new HashMap<>();
            WebService ws;
            ws = new WebService(ip+Constants.WS_UNIDAD_ID + id, params, view.getContext(), FacultadAcercadeFragment.this);
            ws.execute("");
        }
    }

    @Override
    public void processFinish(String result) throws JSONException {
        JSONArray objdataarray = new JSONArray(result);
        JSONObject jsonObj = objdataarray.getJSONObject(0);
        if (jsonObj != null) {
            Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
            String titulo = jsonObj.getString("titulo");
            if (id!="12" && id!="9")
                toolbar.setTitle(titulo != "null" ? titulo : "Facultad");
            txtFacultad.setText(Html.fromHtml(jsonObj.getString("titulo")));
            txtMision.setText(Html.fromHtml(jsonObj.getString("mision")));
            txtVision.setText(Html.fromHtml(jsonObj.getString("vision")));
            if (jsonObj.getString("objetivos") != "null") {
                txtObjetivo.setText(Html.fromHtml(jsonObj.getString("objetivos")));
            } else {
                TextView lblObjetivo = (TextView) view.findViewById(R.id.lblObjetivo);
                lblObjetivo.setVisibility(View.INVISIBLE);
                txtObjetivo.setVisibility(View.INVISIBLE);
            }
            Glide.with(view.getContext())
                    .load(Constants.URL_UTEQ + "/" + jsonObj.getString("url"))
                    .crossFade()
                    .error(R.drawable.escudouteq)
                    .into(imgLogo);
        }
    }
}