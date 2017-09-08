package com.uteq.uteqdevelopers.noticiasuteq.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.uteq.uteqdevelopers.noticiasuteq.R;
import com.uteq.uteqdevelopers.noticiasuteq.adapters.ExpandableListAdapter;
import com.uteq.uteqdevelopers.noticiasuteq.utils.UIUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class RolesPagoFragments extends Fragment {


    private ArrayList<String> lsAniosS=new ArrayList<>();
    private ArrayList<String> lsDatosPersonales=new ArrayList<>();
    private ArrayList<List<String>> lstMeses=new ArrayList<>();

    private ExpandableListView listView;
    private List<String> listDataHeader;
    private HashMap<String, List<String>> listHash;
    private TextView txtTituloMenu;
    View view;

    private FragmentTransaction fragmentTransaction;

    public RolesPagoFragments() {
    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_roles_pago_fragments, container, false);
        listView = (ExpandableListView) view.findViewById(R.id.lvExp);
        Bundle bundle = getArguments();
        String json=bundle.getString("json");
        try {
            ObtenerDatos(json);
            initData(lsAniosS,lstMeses);
            ExpandableListAdapter listAdapter = new ExpandableListAdapter(view.getContext(), listDataHeader, listHash);
            listView.setAdapter(listAdapter);

            /*txtTituloMenu= (TextView) view.findViewById(R.id.txtTituloMenu);
            txtTituloMenu.setText(lsDatosPersonales.get(1));*/


        } catch (JSONException e) {
            e.printStackTrace();
        }
        return view;
    }

    public void ObtenerDatos(String result) throws JSONException {
        JSONObject jsonObject = new JSONObject(result);
        String[] months = new DateFormatSymbols().getMonths();
        int m;
        String mes;
        lsAniosS=new ArrayList<>();

        JSONArray objArrayDatosP = new JSONArray(jsonObject.getString("datos"));

        try {
            lsDatosPersonales.add(objArrayDatosP.getJSONObject(0).getString("rol_usr_cedula"));
            lsDatosPersonales.add(objArrayDatosP.getJSONObject(0).getString("rol_usr_apellidosnombre"));
            lsDatosPersonales.add(objArrayDatosP.getJSONObject(0).getString("rol_usr_id"));
                JSONArray objArrayDatosRol=new JSONArray(jsonObject.getString("lista"));
                for (int i = 0 ; i < objArrayDatosRol.length(); i++) {
                    List<String> meses = new ArrayList<>();
                    String anio = objArrayDatosRol.getJSONObject(i).getString("ano");

                    lsAniosS.add(anio);

                    JSONArray objArrayMeses = new JSONArray(objArrayDatosRol.getJSONObject(i).getString("meses"));
                    for (int j = 0; j < objArrayMeses.length(); j++) {
                        mes = objArrayMeses.getJSONObject(j).getString("mes");
                        if (UIUtil.isNumeric(mes)) {
                            m = Integer.valueOf(mes);
                            mes = months[m - 1].toUpperCase();
                        }
                        meses.add(mes);
                    }
                    lstMeses.add(meses);

                }
            Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
            toolbar.setTitle(lsDatosPersonales.get(1));

        }catch (Exception ignored){
        }
    }

    private void initData(ArrayList<String> lsAnios, final ArrayList<List<String>> lstMeses) {
        listDataHeader = new ArrayList<>();
        listHash = new HashMap<>();

        listDataHeader.addAll(lsAnios);

        int nDeAnios = lsAnios.size();

        for (int i = 0; i < nDeAnios; i++) {
            listHash.put(listDataHeader.get(i), lstMeses.get(i));
        }
        listView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
               //Intent intent = new Intent(getView().getContext(), TransparenciaActivity.class);

                Bundle b = new Bundle();
                b.putString("id_user", lsDatosPersonales.get(2));
                b.putString("idAno", lsAniosS.get(groupPosition));
                b.putString("nombresApellidos",lsDatosPersonales.get(1));
                b.putString("idPeriodo", lstMeses.get(groupPosition).get(childPosition));


                // intent.putExtras(b);

                //startActivity(intent);

                FragmentManager fragmentManager = getFragmentManager();
                Fragment fragment = new DetalleRolPagoFragment();
                fragment.setArguments(b);
                fragmentTransaction = fragmentManager.beginTransaction().addToBackStack(null);
                fragmentTransaction.replace(R.id.fragment, fragment);
                fragmentTransaction.commit();
                return true;
            }
        });
    }



}
