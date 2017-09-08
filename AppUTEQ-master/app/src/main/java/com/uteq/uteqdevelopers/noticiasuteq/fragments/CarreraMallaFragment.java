package com.uteq.uteqdevelopers.noticiasuteq.fragments;


import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.uteq.uteqdevelopers.noticiasuteq.R;
import com.uteq.uteqdevelopers.noticiasuteq.utils.Constants;
import com.uteq.uteqdevelopers.noticiasuteq.utils.SaveImage;

/**
 * A simple {@link Fragment} subclass.
 */
public class CarreraMallaFragment extends Fragment {


    View view;
    TextView txtCarrera;
    ImageView imgMalla;
    private String URLMalla;

    public CarreraMallaFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_carrera_malla, container, false);

        txtCarrera = (TextView) view.findViewById(R.id.txtCarrera);
        imgMalla = (ImageView) view.findViewById(R.id.imgMalla);
        URLMalla= getArguments().getString("URLMalla");

        txtCarrera.setText(getArguments().getString("nombre"));
            Glide.with(view.getContext())
                    .load(URLMalla)
                    .crossFade()
                    .error(R.drawable.logouteqminres)
                    .into(imgMalla);

        Toast.makeText(view.getContext(), Constants.MSG_CARGANDO_IMAGEN,Toast.LENGTH_SHORT).show();


        imgMalla.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager=getFragmentManager();
                DialogoFragment fragment =(DialogoFragment) fragmentManager.findFragmentByTag(DialogoFragment.TAG);
                if(fragment==null){
                    fragment=new DialogoFragment();
                    Bundle b = new Bundle();
                    b.putString("url", URLMalla);
                    b.putString("titulo",txtCarrera.getText().toString());
                    fragment.setArguments(b);
                }
                fragment.show(getFragmentManager(), DialogoFragment.TAG);
                Toast.makeText(view.getContext(), Constants.MSG_CARGANDO_IMAGEN,Toast.LENGTH_SHORT).show();
            }
        });
        imgMalla.setOnLongClickListener(new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View arg0) {

                //convertir imagen a bitmap
                imgMalla.buildDrawingCache();
                Bitmap bmap = imgMalla.getDrawingCache();

                //guardar imagen
                SaveImage savefile = new SaveImage();
                savefile.SaveImage(getContext(), bmap);
                return true;
            }
        });
        return view;
    }

}
