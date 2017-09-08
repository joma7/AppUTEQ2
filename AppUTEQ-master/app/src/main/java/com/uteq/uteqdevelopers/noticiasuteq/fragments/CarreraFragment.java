package com.uteq.uteqdevelopers.noticiasuteq.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.uteq.uteqdevelopers.noticiasuteq.R;
import com.uteq.uteqdevelopers.noticiasuteq.utils.Constants;
import com.uteq.uteqdevelopers.noticiasuteq.utils.UIUtil;
import com.uteq.uteqdevelopers.noticiasuteq.utils.WebServ.Asynchtask;
import com.uteq.uteqdevelopers.noticiasuteq.utils.WebServ.WebService;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItem;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class CarreraFragment extends Fragment implements Asynchtask {

    View view;
    String id;
    String ip;

    public CarreraFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_carrera, container, false);
        Bundle b = getArguments();
        id = b.getString("id");
        return view;
    }

    private void ConectWSCarrera() {
        if (!UIUtil.verificaConexion(view.getContext())) {
            Toast.makeText(view.getContext(), Constants.MSG_COMPROBAR_CONEXION_INTERNET, Toast.LENGTH_LONG).show();
        } else {
            ip= "http://"+UIUtil.ipAConetarse(view.getContext());
            Map<String, String> params = new HashMap<>();
            WebService ws;
            ws = new WebService(ip+Constants.WS_CARRERA_ID + id, params, view.getContext(), CarreraFragment.this);
            ws.execute("");
        }
    }

    @Override
    public void onResume() {
        ConectWSCarrera();
        super.onResume();
    }

    @Override
    public void processFinish(String result) throws JSONException {
        JSONArray objdataarray = new JSONArray(result);
        JSONObject jsonObj = objdataarray.getJSONObject(0);
        if (jsonObj != null) {
            //Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
            //toolbar.setTitle(jsonObj.getString("nombre"));
            String nombre = jsonObj.getString("nombre");
            String descripcion = jsonObj.getString("descripcion") +
                    "<h5 style='margin: 0px;'>Título académico: </h5>" + jsonObj.getString("titulo") +
                    "<h5 style='margin: 0px;'>Modalidad: </h5>" + jsonObj.getString("modalidad") +
                    "<h5 style='margin: 0px;'>Duración: </h5>" + jsonObj.getString("tiempo") + ", " + jsonObj.getString("semestres") + " semestres";
            String mision = "<h5>Misión<h5>" + jsonObj.getString("mision") + "<h5>Visión<h5>" + jsonObj.getString("vision");
            String objetivos =jsonObj.getString("objetivos");
            String perfilprofesional = jsonObj.getString("perfilprofesional");
            String URLImg = Constants.URL_UTEQ + "/images/carreras/" + jsonObj.getString("unidad_id") + "/" + jsonObj.getString("nombre") + "-" +
                    jsonObj.getString("id") + ".jpg";
            String URLMalla =Constants.URL_UTEQ + "/"+jsonObj.getString("malla");
            Bundle CarreraDesc = new Bundle();
            CarreraDesc.putString("nombre", nombre);
            CarreraDesc.putString("descripcion", descripcion);
            CarreraDesc.putString("URLImg", URLImg);
            Bundle CarreraAcercade = new Bundle();
            CarreraAcercade.putString("nombre", nombre);
            CarreraAcercade.putString("mision", mision);
            CarreraAcercade.putString("objetivos", objetivos);
            Bundle CarreraPerfil = new Bundle();
            CarreraPerfil.putString("nombre", nombre);
            CarreraPerfil.putString("perfilprofesional", perfilprofesional);
            Bundle CarreraMalla = new Bundle();
            CarreraMalla.putString("nombre", nombre);
            CarreraMalla.putString("URLMalla", URLMalla);

            FragmentPagerItemAdapter adapter = new FragmentPagerItemAdapter(
                    getChildFragmentManager(), FragmentPagerItems.with(view.getContext())
                    .add(FragmentPagerItem.of("Descripción", CarreraDescFragment.class, CarreraDesc))
                    .add(FragmentPagerItem.of("Acerca de.", CarreraAcercadeFragment.class, CarreraAcercade))
                    .add(FragmentPagerItem.of("Perfil P.", CarreraPerfilFragment.class, CarreraPerfil))
                    .add(FragmentPagerItem.of("Malla curricular", CarreraMallaFragment.class, CarreraMalla))
                    .create());
            ViewPager viewPager = (ViewPager) view.findViewById(R.id.viewpager);
            viewPager.removeAllViews();
            viewPager.setAdapter(adapter);
            SmartTabLayout viewPagerTab = (SmartTabLayout) view.findViewById(R.id.viewpagertab);
            viewPagerTab.setViewPager(viewPager);
        }
    }

}