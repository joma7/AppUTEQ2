package com.uteq.uteqdevelopers.noticiasuteq.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.Bundler;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItem;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;

import com.uteq.uteqdevelopers.noticiasuteq.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class FacultadFragment extends Fragment {

    View view;
    private String id;

    public FacultadFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        id = getArguments().getString("id");
        view = inflater.inflate(R.layout.fragment_facultad, container, false);
        return view;
    }

    @Override
    public void onResume() {

        FragmentPagerItemAdapter adapter = new FragmentPagerItemAdapter(
                getChildFragmentManager(), FragmentPagerItems.with(view.getContext())
                .add(FragmentPagerItem.of("Inicio", FacultadInicioFragment.class, new Bundler().putString("id", id + "/3").get()))
                .add(FragmentPagerItem.of("Acerca de.", FacultadAcercadeFragment.class, new Bundler().putString("id", id).get()))
                .add(FragmentPagerItem.of("Carreras", FacultadCarrerasFragment.class, new Bundler().putString("id", id).get()))
                .add(FragmentPagerItem.of("Noticias", FacultadNoticiasFragment.class, new Bundler().putString("id", id).get()))
                .create());
        ViewPager viewPager = (ViewPager) view.findViewById(R.id.viewpager);
        viewPager.setAdapter(adapter);
        SmartTabLayout viewPagerTab = (SmartTabLayout) view.findViewById(R.id.viewpagertab);
        viewPagerTab.setViewPager(viewPager);

        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        toolbar.setTitle("Facultad - UTEQ");
        super.onResume();
    }
}