package com.uteq.uteqdevelopers.noticiasuteq.activity;

import android.content.DialogInterface;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;

import com.uteq.uteqdevelopers.noticiasuteq.fragments.InicioSesionFragment;
import com.uteq.uteqdevelopers.noticiasuteq.fragments.RolesPagoFragments;
import com.uteq.uteqdevelopers.noticiasuteq.fragments.VerNoticiaFragment;
import com.uteq.uteqdevelopers.noticiasuteq.notifications.app.Config;
import com.uteq.uteqdevelopers.noticiasuteq.notifications.utils.NotificationUtils;
import com.google.firebase.messaging.FirebaseMessaging;

import com.uteq.uteqdevelopers.noticiasuteq.fragments.ArchivoFragment;
import com.uteq.uteqdevelopers.noticiasuteq.fragments.InvestigacionFragment;
import com.uteq.uteqdevelopers.noticiasuteq.fragments.MainFragment;
import com.uteq.uteqdevelopers.noticiasuteq.fragments.NoticiaFragment;
import com.uteq.uteqdevelopers.noticiasuteq.fragments.OfertaFragment;
import com.uteq.uteqdevelopers.noticiasuteq.fragments.RendicionFragment;
import com.uteq.uteqdevelopers.noticiasuteq.fragments.TransparenciaFragment;
import com.uteq.uteqdevelopers.noticiasuteq.fragments.UniversidadFragment;
import com.uteq.uteqdevelopers.noticiasuteq.fragments.VinculacionFragment;
import com.uteq.uteqdevelopers.noticiasuteq.fragments.WebViewFragment;
import com.uteq.uteqdevelopers.noticiasuteq.R;
import com.uteq.uteqdevelopers.noticiasuteq.utils.Constants;
import com.uteq.uteqdevelopers.noticiasuteq.utils.UIUtil;
import com.uteq.uteqdevelopers.noticiasuteq.utils.WebServ.Asynchtask;
import com.uteq.uteqdevelopers.noticiasuteq.utils.WebServ.WebService;

import org.json.JSONException;

import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements Asynchtask {
    Toolbar toolbar;
    private DrawerLayout drawerLayout;
    Fragment fragment;
    private FragmentTransaction fragmentTransaction;
    private FragmentManager fragmentManager;
    private static final String TAG = MainActivity.class.getSimpleName();
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private String usuario;
    private String clave;
    private String nombres;
    private MenuItem itemSesion;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Constants.fragmentManager = getSupportFragmentManager();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        initNavigationDrawer();
        fragmentManager = getSupportFragmentManager();
        String menu = getIntent().getExtras() != null ? getIntent().getExtras().getString("menu") : "";
        //getSupportActionBar().setTitle(menu);
        Bundle b = new Bundle();
        b.putString("tipo", "1");
        fragment = new MainFragment();
        fragment.setArguments(b);
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment, fragment);
        fragmentTransaction.commit();
        Intent intent = getIntent();
        b = intent.getExtras();

        if (b!=null && b.getString("id") != null) {

            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            VerNoticiaFragment fragment = new VerNoticiaFragment();
            Bundle b1 = new Bundle();
            b1.putString("titulo", "Noticias");
            b1.putString("id",b.getString("id"));
            fragment.setArguments(b1);
            fragmentTransaction.replace(R.id.fragment, fragment).addToBackStack(null);
            fragmentTransaction.commit();
        }

        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                // checking for type intent filter
                if (intent.getAction().equals(Config.REGISTRATION_COMPLETE)) {
                    // gcm successfully registered
                    // now subscribe to `global` topic to receive app wide notifications
                    FirebaseMessaging.getInstance().subscribeToTopic(Config.TOPIC_GLOBAL);

                    displayFirebaseRegId();

                } else if (intent.getAction().equals(Config.PUSH_NOTIFICATION)) {
                    // new push notification is received


                    final String id = intent.getStringExtra("id");

                    AlertDialog.Builder dConfirmacionD= new AlertDialog.Builder(MainActivity.this);
                    dConfirmacionD.setTitle("Nueva noticia: "+intent.getStringExtra("message"));
                    dConfirmacionD.setMessage("¿Desea ver esta noticia?");
                    dConfirmacionD.setPositiveButton("Si", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                            VerNoticiaFragment fragment = new VerNoticiaFragment();
                            Bundle b1 = new Bundle();
                            b1.putString("titulo", "Noticias");
                            b1.putString("id",id);
                            fragment.setArguments(b1);
                            fragmentTransaction.replace(R.id.fragment, fragment).addToBackStack(null);
                            fragmentTransaction.commit();
                        }
                    });
                    dConfirmacionD.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    dConfirmacionD.show();
                }
            }
        };


        displayFirebaseRegId();
        sharedPreferences = getSharedPreferences("Sesion", MODE_PRIVATE);
        nombres = sharedPreferences.getString("nombres","");
        usuario = sharedPreferences.getString("usuario","");
        clave = sharedPreferences.getString("clave","");
    }

    // Fetches reg id from shared preferences
    // and displays on the screen
    private void displayFirebaseRegId() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
        String regId = pref.getString("regId", null);

        Log.e(TAG, "Firebase reg id: " + regId);

        if (!TextUtils.isEmpty(regId))
            Log.e(TAG, "Firebase reg id: " + regId);
        else
            Log.e(TAG,"Firebase Reg Id is not received yet!");
    }

    @Override
    protected void onResume() {
        super.onResume();

        // register GCM registration complete receiver
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.REGISTRATION_COMPLETE));

        // register new push message receiver
        // by doing this, the activity will be notified each time a new message arrives
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.PUSH_NOTIFICATION));

        // clear the notification area when the app is opened
        NotificationUtils.clearNotifications(getApplicationContext());
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }

    public void initNavigationDrawer() {
        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                int id = menuItem.getItemId();
                //getSupportActionBar().setTitle(menuItem.getTitle());

                Bundle b = new Bundle();
                switch (id) {
                    case R.id.nav_home:
                        menuItem.setChecked(true);
                        fragment = new MainFragment();
                        fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.fragment, fragment).addToBackStack(null);
                        fragmentTransaction.commit();
                        drawerLayout.closeDrawer(GravityCompat.START);
                        break;
                    case R.id.nav_noticias:
                        menuItem.setChecked(true);
                        b = new Bundle();
                        b.putString("tipo", "1");
                        b.putString("titulo", "Noticias");
                        fragment = new NoticiaFragment();
                        fragment.setArguments(b);
                        fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.fragment, fragment).addToBackStack(null);
                        fragmentTransaction.commit();
                        drawerLayout.closeDrawer(GravityCompat.START);
                        break;
                    case R.id.nav_universidad:
                        menuItem.setChecked(true);
                        fragment = new UniversidadFragment();
                        fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.fragment, fragment).addToBackStack(null);
                        fragmentTransaction.commit();
                        drawerLayout.closeDrawer(GravityCompat.START);
                        break;
                    case R.id.nav_investigacion:
                        menuItem.setChecked(true);
                        fragment = new InvestigacionFragment();
                        fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.fragment, fragment).addToBackStack(null);
                        fragmentTransaction.commit();
                        drawerLayout.closeDrawer(GravityCompat.START);
                        break;
                    case R.id.nav_vinculacion:
                        menuItem.setChecked(true);
                        fragment = new VinculacionFragment();
                        fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.fragment, fragment).addToBackStack(null);
                        fragmentTransaction.commit();
                        drawerLayout.closeDrawer(GravityCompat.START);
                        break;
                    case R.id.nav_ofertaacademica:
                        menuItem.setChecked(true);
                        fragment = new OfertaFragment();
                        fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.fragment, fragment).addToBackStack(null);
                        fragmentTransaction.commit();
                        drawerLayout.closeDrawer(GravityCompat.START);
                        break;
                    case R.id.nav_transparencia:
                        menuItem.setChecked(true);
                        b = new Bundle();
                        b.putString("tipoFragment", "tr");
                        fragment = new TransparenciaFragment();
                        fragment.setArguments(b);
                        fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.fragment, fragment).addToBackStack(null);
                        fragmentTransaction.commit();
                        drawerLayout.closeDrawer(GravityCompat.START);
                        break;
                    case R.id.nav_rendicioncuentas:
                        menuItem.setChecked(true);
                        b = new Bundle();
                        b.putString("tipoFragment", "rc");
                        fragment = new RendicionFragment();
                        fragment.setArguments(b);
                        fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.fragment, fragment).addToBackStack(null);
                        fragmentTransaction.commit();
                        drawerLayout.closeDrawer(GravityCompat.START);
                        break;
                    case R.id.nav_archivos:
                        menuItem.setChecked(true);
                        b = new Bundle();
                        b.putString("tipoFragment", "ar");
                        fragment = new ArchivoFragment();
                        fragment.setArguments(b);
                        fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.fragment, fragment).addToBackStack(null);
                        fragmentTransaction.commit();
                        drawerLayout.closeDrawer(GravityCompat.START);
                        break;
                    case R.id.nav_roles:
                        menuItem.setChecked(true);
                        nombres = sharedPreferences.getString("nombres","");
                        usuario = sharedPreferences.getString("usuario","");
                        clave = sharedPreferences.getString("clave","");

                        if (usuario!=""){


                            String url = "http://" + UIUtil.ipAConetarse(MainActivity.this)+ Constants.WS_INICIO_SESION+ usuario+"/"+clave;
                            Map<String, String> params = new HashMap<>();
                            WebService ws = new WebService(url, params, MainActivity.this, MainActivity.this);
                            ws.execute("");
                        }
                        else {
                            InicioSesionFragment inicioSesionFragment = new InicioSesionFragment();
                            inicioSesionFragment.show(fragmentManager, "InicioSesionFragment");
                            }
                        drawerLayout.closeDrawer(GravityCompat.START);
                        break;
                    case R.id.nav_consultanotas:
                        menuItem.setChecked(true);
                        b = new Bundle();
                        b.putString("titulo", "Consulta de notas");
                        b.putString("url", Constants.URL_CONSULTA_NOTAS);
                        b.putBoolean("zoom",true);
                        fragment = new WebViewFragment();
                        fragment.setArguments(b);
                        fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.fragment, fragment).addToBackStack(null);
                        fragmentTransaction.commit();
                        drawerLayout.closeDrawer(GravityCompat.START);
                        break;
                    case R.id.nav_videosuteq:
                        b = new Bundle();
                        b.putString("titulo", "Videos UTEQ");
                        b.putString("url", Constants.URL_CANAL_UTEQ);
                        fragment = new WebViewFragment();
                        fragment.setArguments(b);
                        fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.fragment, fragment, "webview").addToBackStack(null);
                        fragmentTransaction.commit();
                        drawerLayout.closeDrawer(GravityCompat.START);
                        break;
                    case R.id.nav_encuesta:
                        b = new Bundle();
                        b.putString("titulo", "Encuesta de satisfacción");
                        b.putString("url", Constants.URL_ENCUESTA_APP_UTEQ);
                        fragment = new WebViewFragment();
                        fragment.setArguments(b);
                        fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.fragment, fragment, "webview").addToBackStack(null);
                        fragmentTransaction.commit();
                        drawerLayout.closeDrawer(GravityCompat.START);
                        break;
                    case R.id.nav_exit:
                        usuario = sharedPreferences.getString("usuario","");
                        if(usuario!=""){
                            AlertDialog.Builder dConfirmacionD= new AlertDialog.Builder(MainActivity.this);
                            dConfirmacionD.setTitle("Salir");
                            dConfirmacionD.setMessage("¿También desea cerrar sesión?");
                            dConfirmacionD.setPositiveButton("Si", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    SharedPreferences.Editor editor=sharedPreferences.edit();
                                    editor.clear();
                                    editor.commit();
                                    finish();
                                }
                            });
                            dConfirmacionD.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    finish();
                                }
                            });
                            dConfirmacionD.setNeutralButton("Cancelar", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();

                                }
                            });

                            dConfirmacionD.show();
                        }else{
                            AlertDialog.Builder dConfirmacionD= new AlertDialog.Builder(MainActivity.this);
                            dConfirmacionD.setTitle("Salir");
                            dConfirmacionD.setMessage("¿Está seguro que desea salir?");
                            dConfirmacionD.setPositiveButton("Si", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    finish();
                                }
                            });
                            dConfirmacionD.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });


                            dConfirmacionD.show();
                        }

                }

                return true;
            }
        });



        drawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerClosed(View v) {
                super.onDrawerClosed(v);
            }

            @Override
            public void onDrawerOpened(View v) {
                super.onDrawerOpened(v);
            }
        };
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void processFinish(String result) throws JSONException, ParseException {
        Bundle b = new Bundle();
        b.putString("json", result);
        FragmentManager fragmentManager = getSupportFragmentManager();
        RolesPagoFragments fragment = new RolesPagoFragments();
        fragment.setArguments(b);
        fragmentTransaction = fragmentManager.beginTransaction().addToBackStack(null);
        fragmentTransaction.replace(R.id.fragment, fragment);

        fragmentTransaction.commit();
    }
}