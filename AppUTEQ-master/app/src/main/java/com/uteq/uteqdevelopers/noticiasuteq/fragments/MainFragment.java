package com.uteq.uteqdevelopers.noticiasuteq.fragments;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.Toast;

import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.uteq.uteqdevelopers.noticiasuteq.adapters.NoticiaAdapter;
import com.uteq.uteqdevelopers.noticiasuteq.model.Noticia;
import com.uteq.uteqdevelopers.noticiasuteq.model.Slider;
import com.uteq.uteqdevelopers.noticiasuteq.utils.Constants;
import com.uteq.uteqdevelopers.noticiasuteq.utils.UIUtil;
import com.uteq.uteqdevelopers.noticiasuteq.utils.WebServ.Asynchtask;
import com.uteq.uteqdevelopers.noticiasuteq.utils.WebServ.WebService;
import com.uteq.uteqdevelopers.noticiasuteq.R;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragment extends Fragment implements Asynchtask, BaseSliderView.OnSliderClickListener {
    ViewPager pager;
    RecyclerView recyclerView;
    ScrollView scrollView;
    SwipeRefreshLayout swipeRefreshLayout;
    LinearLayoutManager linearLayoutManager;
    View view;
    private ArrayList<Slider> sliders;
    private CountDownTimer countDownTimer;
    SliderLayout mDemoSlider;
    HashMap<String, String> url_maps = new HashMap<>();

    String ip;

    public MainFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_main, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.rvNoticia);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeLayoutN);
        scrollView = (ScrollView) view.findViewById(R.id.scrollView);
        linearLayoutManager = new LinearLayoutManager(view.getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        ip= "http://"+UIUtil.ipAConetarse(view.getContext());
        ConectWSSlider();
        ConectWSNoticias();
        //"refrescar deslizando"
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent, R.color.colorAccent);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                ConectWSNoticias();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        mDemoSlider = (SliderLayout) view.findViewById(R.id.slider);

        return view;
    }

    private void ConectWSNoticias() {
        if (!UIUtil.verificaConexion(view.getContext())) {
            Toast.makeText(view.getContext(), Constants.MSG_COMPROBAR_CONEXION_INTERNET, Toast.LENGTH_LONG).show();
        } else {

            Map<String, String> params = new HashMap<>();
            WebService ws = new WebService(ip+Constants.WS_NOTICIALIMIT + "3", params, view.getContext(), MainFragment.this);
            ws.execute("");
        }
    }

    private void ConectWSSlider() {
        if (!UIUtil.verificaConexion(view.getContext())) {
            Toast.makeText(view.getContext(), Constants.MSG_COMPROBAR_CONEXION_INTERNET, Toast.LENGTH_LONG).show();
        } else {
            Map<String, String> params = new HashMap<>();
            WebService ws = new WebService(ip+Constants.WS_SLIDER_ESTADO, params, view.getContext(), MainFragment.this);
            ws.execute("");
        }
    }

    @Override
    public void processFinish(String result) throws JSONException {
        JSONArray objdataarray = new JSONArray(result);
        try {
            url_maps.clear();
            for (int i = 0; i < objdataarray.length(); i++) {
                url_maps.put(objdataarray.getJSONObject(i).getString("nombre"), objdataarray.getJSONObject(i).getString("img"));
                //url_maps.put(objdataarray.getJSONObject(i).getString("id"),objdataarray.getJSONObject(i).getString("titulo"));
            }

            /////////////SLIDER//////////
            mDemoSlider.removeAllSliders();
            for (String name : url_maps.keySet()) {
                TextSliderView textSliderView = new TextSliderView(view.getContext());
                // initialize a SliderLayout
                textSliderView
                        //.description(url_maps.keySet().toString())
                        .image(Constants.URL_IMAGE_SLIDER +"/" + url_maps.get(name).replace(" ","%20"))
                        .setScaleType(BaseSliderView.ScaleType.Fit)
                        .setOnSliderClickListener(MainFragment.this);

                //add your extra information
                textSliderView.bundle(new Bundle());
                textSliderView.getBundle()
                        .putString("extra", name);
              // textSliderView.getBundle().putString("url",url_maps.get("url"));
                mDemoSlider.addSlider(textSliderView);
            }

            mDemoSlider.setPresetTransformer(SliderLayout.Transformer.DepthPage);
            mDemoSlider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
            mDemoSlider.setCustomAnimation(new DescriptionAnimation());
            mDemoSlider.setDuration(3000);
            /////////////////FIN SLIDER//////////////////////////////
        } catch (Exception x) {
            ArrayList<Noticia> noticias = Noticia.JsonObjectsBuild(objdataarray);
            NoticiaAdapter noticiaAdapter = new NoticiaAdapter(view.getContext(), noticias, getFragmentManager());
            recyclerView.setAdapter(noticiaAdapter);
        }
        //scrollView.scrollTo(0, 0);
    }

    @Override
    public void onSliderClick(BaseSliderView slider) {
        //Toast.makeText(view.getContext(), slider.getBundle().get("extra") + "", Toast.LENGTH_SHORT).show();

        String url=slider.getBundle().get("extra").toString();
        String urlSub;
        if(!url.equals("home")&&!url.equals("#")){
            urlSub=url.substring(0,2);
            if(!urlSub.equals("ht")){
                if(!urlSub.substring(0,1).equals("/"))
                    url="/"+url;
                url=Constants.URL_UTEQ+url;

            }
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);
        }
    }

    @Override
    public void onResume() {
        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        toolbar.setTitle("Inicio - UTEQ");
        scrollView.scrollTo(0,0);



        super.onResume();
    }
}