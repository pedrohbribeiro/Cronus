package com.example.pedrohenrique.cronusapp;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

public class CalendarioActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    TextView[] textViews;
    Calendar calendarioAtual;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendario);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Calendário");
        //Organização do calendário
        textViews = new TextView[]{findViewById(R.id.txt11), findViewById(R.id.txt12), findViewById(R.id.txt13), findViewById(R.id.txt14), findViewById(R.id.txt15), findViewById(R.id.txt16), findViewById(R.id.txt17), findViewById(R.id.txt21), findViewById(R.id.txt22), findViewById(R.id.txt23), findViewById(R.id.txt24), findViewById(R.id.txt25), findViewById(R.id.txt26), findViewById(R.id.txt27), findViewById(R.id.txt31), findViewById(R.id.txt32), findViewById(R.id.txt33), findViewById(R.id.txt34), findViewById(R.id.txt35), findViewById(R.id.txt36), findViewById(R.id.txt37), findViewById(R.id.txt41), findViewById(R.id.txt42), findViewById(R.id.txt43), findViewById(R.id.txt44), findViewById(R.id.txt45), findViewById(R.id.txt46), findViewById(R.id.txt47), findViewById(R.id.txt51), findViewById(R.id.txt52), findViewById(R.id.txt53), findViewById(R.id.txt54), findViewById(R.id.txt55), findViewById(R.id.txt56), findViewById(R.id.txt57), findViewById(R.id.txt61), findViewById(R.id.txt62), findViewById(R.id.txt63), findViewById(R.id.txt64), findViewById(R.id.txt65), findViewById(R.id.txt66), findViewById(R.id.txt67)};
        calendarioAtual = Calendar.getInstance();
        setarDias(calendarioAtual);
        ((Button)findViewById(R.id.btnAumentar)).setOnClickListener(this);
        ((Button)findViewById(R.id.btnDiminuir)).setOnClickListener(this);
        //Drawer
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_atividades) {
            startActivity(new Intent(this,MainActivity.class));
            finish();
        } else if (id == R.id.nav_calendario) {
        } else if (id == R.id.nav_config) {
            startActivity(new Intent(this, ConfiguracaoActivity.class));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    protected void setarDias(Calendar calendario) {
        //Seta os dias no calendário com base no mês atual
        String mes = calendario.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault()) + " " + calendario.get(Calendar.YEAR);
        ((TextView) findViewById(R.id.txtData)).setText(mes.substring(0, 1).toUpperCase() + mes.substring(1).toLowerCase());
        calendario.set(Calendar.DAY_OF_MONTH, 1);
        int diaSemana = calendario.get(Calendar.DAY_OF_WEEK) - 1;
        for (int i = diaSemana; i < calendario.getActualMaximum(Calendar.DAY_OF_MONTH) + diaSemana; i++) {
            textViews[i].setText(String.valueOf(i - diaSemana + 1));
            textViews[i].setTextColor(Color.parseColor("#808080"));
            textViews[i].setBackground(new ColorDrawable(Color.TRANSPARENT));
            int ano = calendario.get(Calendar.YEAR);
            int mesInt = calendario.get(Calendar.MONTH);
            int dia = i - diaSemana + 1;
            Calendar calendar = new GregorianCalendar();
            calendar.set(ano,mesInt,dia);
            if(new Atividade(this).checkAtividadeDia(calendar)){

                textViews[i].setTextColor(Color.BLUE);
            }
        }
        int count = 1;
        for (int i = calendario.getActualMaximum(Calendar.DAY_OF_MONTH) + diaSemana; i < textViews.length; i++) {
            textViews[i].setText(String.valueOf(count));
            textViews[i].setTextColor(Color.parseColor("#D3D3D3"));
            textViews[i].setBackground(new ColorDrawable(Color.TRANSPARENT));
            count++;
        }
        calendario.add(Calendar.MONTH, -1);
        for (int i = diaSemana - 1; i >= 0; i--) {
            textViews[i].setText(String.valueOf(calendario.getActualMaximum(Calendar.DAY_OF_MONTH) - diaSemana + i + 1));
            textViews[i].setTextColor(Color.parseColor("#D3D3D3"));
            textViews[i].setBackground(new ColorDrawable(Color.TRANSPARENT));
        }
        calendario.add(Calendar.MONTH, 1);
        Calendar hoje = Calendar.getInstance();
        if (calendario.get(Calendar.MONTH) == hoje.get(Calendar.MONTH) && calendario.get(Calendar.YEAR) == hoje.get(Calendar.YEAR)) {
            int diaHoje = diaSemana + hoje.get(Calendar.DAY_OF_MONTH) - 1;
            textViews[diaHoje].setBackground(ContextCompat.getDrawable(this, R.drawable.rounded_textview));
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.btnAumentar:
                calendarioAtual.add(Calendar.MONTH,1);
                setarDias(calendarioAtual);
                break;
            case R.id.btnDiminuir:
                calendarioAtual.add(Calendar.MONTH,-1);
                setarDias(calendarioAtual);
                break;
        }
    }

    public void selecionarDia(View v){
        TextView textView = (TextView)v;
        int pos = Arrays.asList(textViews).indexOf(textView);
        if(pos >= calendarioAtual.get(Calendar.DAY_OF_WEEK)-1 && pos < calendarioAtual.getMaximum(Calendar.DAY_OF_MONTH)+calendarioAtual.get(Calendar.DAY_OF_WEEK)-1){
            Intent i = new Intent(this,MainActivity.class);
            calendarioAtual.set(Calendar.DAY_OF_MONTH,Integer.parseInt(textView.getText().toString()));
            i.putExtra("dia",calendarioAtual.get(Calendar.DAY_OF_MONTH));
            i.putExtra("mes",calendarioAtual.get(Calendar.MONTH));
            i.putExtra("ano",calendarioAtual.get(Calendar.YEAR));
            startActivity(i);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setarDias(calendarioAtual);
    }
}
