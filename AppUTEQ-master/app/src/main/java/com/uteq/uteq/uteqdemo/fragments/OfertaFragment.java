package com.uteq.uteq.uteqdemo.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.uteq.uteq.uteqdemo.R;
import com.uteq.uteq.uteqdemo.activity.MainActivity;
import com.uteq.uteq.uteqdemo.adapters.OfertaAdapter;
import com.uteq.uteq.uteqdemo.model.Oferta;
import com.uteq.uteq.uteqdemo.utils.Constants;
import com.uteq.uteq.uteqdemo.utils.UIUtil;
import com.uteq.uteq.uteqdemo.utils.WebServ.Asynchtask;
import com.uteq.uteq.uteqdemo.utils.WebServ.WebService;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class OfertaFragment extends Fragment implements Asynchtask {


    RecyclerView recyclerView;
    LinearLayoutManager linearLayoutManager;
    SwipeRefreshLayout swipeRefreshLayout;
    ArrayList<Oferta> facultades;
    View view;
    String ip;

    public OfertaFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_oferta, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.rvMenu);
        linearLayoutManager = new LinearLayoutManager(view.getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        ConectWSMenu();
        //"refrescar deslizando"
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeLayoutN1);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent, R.color.colorAccent);//Aqui podemos cponer los colores que queremos
        swipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        ConectWSMenu();
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
        return view;
    }

    private void ConectWSMenu() {
        if (!UIUtil.verificaConexion(view.getContext())) {
            Toast.makeText(view.getContext(), Constants.MSG_COMPROBAR_CONEXION_INTERNET, Toast.LENGTH_LONG).show();
        } else {
            ip= "http://"+UIUtil.ipAConetarse(view.getContext());
            Map<String, String> params = new HashMap<>();
            WebService ws = new WebService(ip+Constants.WS_MENU_OFERTA , params, view.getContext(), OfertaFragment.this);
            ws.execute("");
        }
    }

    @Override
    public void processFinish(String result) throws JSONException {
        JSONArray objdataarray = new JSONArray(result);
        facultades = Oferta.JsonObjectsBuild(objdataarray);
        OfertaAdapter adapater = new OfertaAdapter(view.getContext(), facultades, (MainActivity) getActivity());
        recyclerView.setAdapter(adapater);

    }

    @Override
    public void onResume() {
        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        toolbar.setTitle("Oferta academica - UTEQ");
        super.onResume();
    }

}