package com.uteq.uteq.uteqdemo.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.uteq.uteq.uteqdemo.R;
import com.uteq.uteq.uteqdemo.utils.Constants;
import com.uteq.uteq.uteqdemo.utils.UIUtil;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.Bundler;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItem;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;

/**
 * A simple {@link Fragment} subclass.
 */
public class InvestigacionFragment extends Fragment {


    private View view;

    public InvestigacionFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_investigacion, container, false);
        return view;
    }

    @Override
    public void onResume() {
        String ip= "http://"+ UIUtil.ipAConetarse(view.getContext());
        FragmentPagerItemAdapter adapter = new FragmentPagerItemAdapter(
                getChildFragmentManager(), FragmentPagerItems.with(view.getContext())
                .add(FragmentPagerItem.of("Inicio", FacultadInicioFragment.class, new Bundler().putString("id", "12").get()))    //  2 es el maximo de noticias
                .add(FragmentPagerItem.of("Acerca de.", FacultadAcercadeFragment.class, new Bundler().putString("id", "12").get()))
                .add(FragmentPagerItem.of("Noticias", NoticiaFragment.class, new Bundler().putString("tipo", "2").get()))
                .add(FragmentPagerItem.of("Líneas", WebViewFragment.class, WebViewFragment.arguments(ip + Constants.WS_INVESTIGACION_LINEAS, false)))
                .add(FragmentPagerItem.of("Proyectos", WebViewFragment.class, WebViewFragment.arguments(ip + Constants.WS_INVESTIGACION_PROYECTOS, true)))
                .add(FragmentPagerItem.of("Investigadores", WebViewFragment.class, WebViewFragment.arguments(ip + Constants.WS_INVESTIGACION_INVESTIGADORES, false)))
                .create());
        ViewPager viewPager = (ViewPager) view.findViewById(R.id.viewpager);
        viewPager.removeAllViews();
        viewPager.setAdapter(adapter);

        SmartTabLayout viewPagerTab = (SmartTabLayout) view.findViewById(R.id.viewpagertab);
        viewPagerTab.setViewPager(viewPager);

        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        toolbar.setTitle("Investigación - UTEQ");
        viewPagerTab.setOnTabClickListener(new SmartTabLayout.OnTabClickListener() {
            @Override
            public void onTabClicked(int position) {
                if (position==4){
                    Toast.makeText(getContext(), "Para una mejorar experiencia gire el dispositivo", Toast.LENGTH_SHORT).show();
                }
            }
        });
        super.onResume();
    }

}
