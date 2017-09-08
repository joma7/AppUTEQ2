package com.uteq.uteqdevelopers.noticiasuteq.fragments;

import android.app.Dialog;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.uteq.uteqdevelopers.noticiasuteq.R;
import com.uteq.uteqdevelopers.noticiasuteq.utils.Constants;
import com.uteq.uteqdevelopers.noticiasuteq.utils.Sesion;
import com.uteq.uteqdevelopers.noticiasuteq.utils.UIUtil;
import com.uteq.uteqdevelopers.noticiasuteq.utils.WebServ.Asynchtask;
import com.uteq.uteqdevelopers.noticiasuteq.utils.WebServ.WebService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by JOMA on 28/08/2017.
 */
public class InicioSesionFragment extends DialogFragment implements Asynchtask {

    private EditText usuario;
    private EditText clave;
    private TextView olvidoContrasena;
    private Button ingresar;
    private CheckBox recordarme;
    private ArrayList<String> lsAniosS=new ArrayList<>();
    private ArrayList<String> lsDatosPersonales=new ArrayList<>();
    private ArrayList<List<String>> lstMeses=new ArrayList<>();
    private FragmentTransaction fragmentTransaction;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View item = inflater.inflate(R.layout.inicio_sesion, null);
        builder.setView(item);
        usuario= (EditText) item.findViewById(R.id.txtUsuario);
        clave = (EditText) item.findViewById(R.id.txtClave);
        //recordarme = (CheckBox) item.findViewById(R.id.cbxRecordarme);
        ingresar = (Button)item.findViewById(R.id.btnIngresar);
        //olvidoContrasena = (TextView) item.findViewById(R.id.txtOlvideContrasena);
        ingresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String url = "http://" + UIUtil.ipAConetarse(v.getContext())+ Constants.WS_INICIO_SESION+ usuario.getText().toString().trim()+"/"+clave.getText().toString().trim();
                Map<String, String> params = new HashMap<>();
                WebService ws = new WebService(url, params, v.getContext(), InicioSesionFragment.this);
                ws.execute("");

             }
        });
        /*olvidoContrasena.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "Contáctate con: ", Toast.LENGTH_SHORT).show();
            }
        });*/
        return builder.create();
    }

    @Override
    public void processFinish(String result) throws JSONException, ParseException {JSONObject jsonObject = new JSONObject(result);
        String[] months = new DateFormatSymbols().getMonths();
        int m;
        String mes;
        lsAniosS=new ArrayList<>();

        JSONArray objArrayDatosP = new JSONArray(jsonObject.getString("datos"));

        try {
            lsDatosPersonales.add(objArrayDatosP.getJSONObject(0).getString("rol_usr_cedula"));
            lsDatosPersonales.add(objArrayDatosP.getJSONObject(0).getString("rol_usr_apellidosnombre"));
            if (lsDatosPersonales.get(0).equals(usuario.getText().toString().trim())) {
                /*JSONArray objArrayDatosRol=new JSONArray(jsonObject.getString("lista"));
                for (int i = 0 ; i < objArrayDatosRol.length(); i++) {
                    List<String> meses=new ArrayList<>();
                    String anio=objArrayDatosRol.getJSONObject(i).getString("ano");

                    lsAniosS.add(anio);

                    JSONArray objArrayMeses=new JSONArray(objArrayDatosRol.getJSONObject(i).getString("meses"));
                    for(int j=0;j<objArrayMeses.length();j++){
                        mes=objArrayMeses.getJSONObject(j).getString("mes");
                        if(UIUtil.isNumeric(mes)) {
                            m = Integer.valueOf(mes);
                            mes = months[m - 1].toUpperCase();
                        }
                        meses.add(mes);
                    }
                    lstMeses.add(meses);

                }*/

                Sesion sesion= new Sesion();
                sesion.guardarSesion(getContext(),lsDatosPersonales.get(1),lsDatosPersonales.get(0),clave.getText().toString().trim() );


                Toast.makeText(getContext(), "Bienvenido: " +lsDatosPersonales.get(1), Toast.LENGTH_SHORT).show();
               Bundle b = new Bundle();
                b.putString("json", result);
                FragmentManager fragmentManager = getFragmentManager();
                RolesPagoFragments fragment = new RolesPagoFragments();
                fragment.setArguments(b);
                fragmentTransaction = fragmentManager.beginTransaction().addToBackStack(null);
                fragmentTransaction.replace(R.id.fragment, fragment);
                fragmentTransaction.commit();
                dismiss();

            } else {
                Toast.makeText(getContext(), "Usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show();

            }
        }catch (Exception e){
            Toast.makeText(getContext(), "Usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show();
        }
    }
}