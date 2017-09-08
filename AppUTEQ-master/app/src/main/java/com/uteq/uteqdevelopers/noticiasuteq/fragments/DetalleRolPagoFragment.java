package com.uteq.uteqdevelopers.noticiasuteq.fragments;


import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.uteq.uteqdevelopers.noticiasuteq.R;
import com.uteq.uteqdevelopers.noticiasuteq.utils.Constants;
import com.uteq.uteqdevelopers.noticiasuteq.utils.SaveImage;
import com.uteq.uteqdevelopers.noticiasuteq.utils.TablaRol;
import com.uteq.uteqdevelopers.noticiasuteq.utils.UIUtil;
import com.uteq.uteqdevelopers.noticiasuteq.utils.WebServ.Asynchtask;
import com.uteq.uteqdevelopers.noticiasuteq.utils.WebServ.WebService;

import org.json.JSONArray;
import org.json.JSONException;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class DetalleRolPagoFragment extends Fragment implements Asynchtask{


    View view;
    TextView lblTitulo;
    String anio;
    int periodo;
    String Mes;
    String idUser;
    String nombresApellidos;
    Bundle bundle;
    TablaRol tabla;
    TablaRol tabla2;
    private double totalIngresos=0;
    private double totalDescuentos=0;

    public DetalleRolPagoFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view=inflater.inflate(R.layout.fragment_detalle_rol_pago, container, false);
        String[] months = new DateFormatSymbols().getMonths();


        bundle = getArguments();

        anio=bundle.getString("idAno");
        Mes=bundle.getString("idPeriodo");
        //periodo=months.Mes.toLowerCase();
        idUser=bundle.getString("id_user");
        nombresApellidos=bundle.getString("nombresApellidos");
        for (int i=0;i<months.length;i++){
            if (months[i].equals(Mes.toLowerCase()))
                periodo=i+1;
        }
        lblTitulo=(TextView)view.findViewById(R.id.txtTransparencia);
        lblTitulo.setText("Año: "+anio+" - Periodo: "+Mes);
        tabla = new TablaRol(view.getContext(), (TableLayout)view.findViewById(R.id.tablaRol));
        tabla2 = new TablaRol(view.getContext(), (TableLayout)view.findViewById(R.id.tablaRol2));


        if (!UIUtil.verificaConexion(view.getContext())) {
            Toast.makeText(view.getContext(), Constants.MSG_COMPROBAR_CONEXION_INTERNET, Toast.LENGTH_LONG).show();
        } else {

            String url = "http://" + UIUtil.ipAConetarse(view.getContext())+ Constants.WS_ROL_PAGOS+idUser+"/"+anio+"/"+periodo;
            Map<String, String> params = new HashMap<>();
            WebService ws = new WebService(url, params, view.getContext(), DetalleRolPagoFragment.this);
            ws.execute("");
            Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
            toolbar.setTitle(nombresApellidos);
            TextView txtNombresUser=(TextView)view.findViewById(R.id.txtNombreUser);
            txtNombresUser.setText(nombresApellidos);
        }

        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                //convertir imagen a bitmap
                view.buildDrawingCache();
                Bitmap bmap = view.getDrawingCache();

                //guardar imagen
                SaveImage savefile = new SaveImage();
                savefile.SaveImage(getContext(), bmap);
                return true;
            }
        });
        return view;
    }

    @Override
    public void processFinish(String result) throws JSONException {
        JSONArray jsonArray = new JSONArray(result);


        int m;
        String mes;
        ArrayList<String> lsIngresos = new ArrayList<>();
        ArrayList<String> lsDescuentos = new ArrayList<>();
        try {
            tabla.agregarCabecera(R.array.cabecera_ingresos);
            tabla2.agregarCabecera(R.array.cabecera_descuentos);

            for (int i=0; i<jsonArray.length();i++){
                String tipoRubro=jsonArray.getJSONObject(i).getString("rol_tiporubro");
                String monto=jsonArray.getJSONObject(i).getString("rol_monto").replace(",",".");
                double v=Double.valueOf(monto);
                if(tipoRubro.equals("I")){
                    lsIngresos =new ArrayList<>();
                    lsIngresos.add(jsonArray.getJSONObject(i).getString("rol_rubro_desc"));
                    lsIngresos.add(String.valueOf(v));

                    totalIngresos+=v;

                    tabla.agregarFilaTabla(lsIngresos,false);
                }else if(tipoRubro.equals("D")){
                    lsDescuentos =new ArrayList<>();
                    lsDescuentos.add(jsonArray.getJSONObject(i).getString("rol_rubro_desc"));
                    lsDescuentos.add(String.valueOf(v));
                    totalDescuentos+=v;

                    tabla2.agregarFilaTabla(lsDescuentos,false);
                }
            }
            lsIngresos =new ArrayList<>();
            lsIngresos.add("TOTAL INGRESOS");
            lsIngresos.add(String.valueOf(totalIngresos));
            tabla.agregarFilaTabla(lsIngresos,true);

            lsDescuentos =new ArrayList<>();
            lsDescuentos.add("TOTAL DESCUENTOS");
            lsDescuentos.add(String.valueOf(totalDescuentos));
            tabla2.agregarFilaTabla(lsDescuentos,true);

            TextView txtLiquidoPagar=(TextView)view.findViewById(R.id.txtLiquidoPagar);
            txtLiquidoPagar.setText("LÍQUIDO PAGADO: "+String.valueOf(totalIngresos-totalDescuentos));

            Toast.makeText(view.getContext(), Constants.MSG_GUARDAR_IMAGEN, Toast.LENGTH_SHORT).show();
        } catch (Exception ignored) {
        }

    }

}
