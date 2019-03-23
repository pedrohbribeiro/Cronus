package com.example.pedrohenrique.cronusapp;

import android.app.DatePickerDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, AdapterView.OnItemClickListener, DatePickerDialog.OnDateSetListener, View.OnClickListener {
    Calendar dataVisualizada;
    ArrayList<Atividade> atividades;
    AtividadeAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Atividades");
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, AdicionarActivity.class));
            }
        });

        //Drawer
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Cria o canal de notificação
            CharSequence name = "Cronus";
            String description = "Canal de notificação para o app Cronus";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("CronusNotification", name, importance);
            channel.setDescription(description);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
        //Setando as atividades para exibir no ListView
        atividades = new ArrayList<Atividade>();
        adapter = new AtividadeAdapter(getApplicationContext(), R.layout.activity_item_atividade, atividades);
        ListView listaAtividades = (ListView) findViewById(R.id.listaAtividades);
        listaAtividades.setAdapter(adapter);
        listaAtividades.setOnItemClickListener(this);
        dataVisualizada = Calendar.getInstance();
        ArrayList<Atividade> atividadeList = (new Atividade(this)).buscarAtividades(dataVisualizada, 7);
        for (Atividade atividade : atividadeList) {
            atividades.add(atividade);
        }
        adapter.notifyDataSetChanged();
        ((Button) findViewById(R.id.btnAumentar)).setOnClickListener(this);
        ((Button) findViewById(R.id.btnDiminuir)).setOnClickListener(this);

        if (atividades.size() == 0) {//Se não tiver atividades no dia exibe a mensagem
            ((TextView) findViewById(R.id.txtNadaHoje)).setVisibility(View.VISIBLE);
        } else {
            ((TextView) findViewById(R.id.txtNadaHoje)).setVisibility(View.GONE);
        }

        if (getIntent().getExtras() != null) {
            Bundle bundle = getIntent().getExtras();
            int dia = bundle.getInt("dia");
            int mes = bundle.getInt("mes");
            int ano = bundle.getInt("ano");
            atualizarAtividades(dia, mes, ano, 1);
        }
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
        } else if (id == R.id.nav_calendario) {
            startActivity(new Intent(this,CalendarioActivity.class));
            finish();
        } else if (id == R.id.nav_config) {
            startActivity(new Intent(this, ConfiguracaoActivity.class));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        final Atividade atividade = (Atividade) parent.getAdapter().getItem(position);
        //Mostra os dados da atividade selecionada
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Visualizar Atividade");

        String horario = String.format("%04d",atividade.getHorario());

        Format dateFormat = android.text.format.DateFormat.getDateFormat(getApplicationContext());
        String formatoData = ((SimpleDateFormat) dateFormat).toLocalizedPattern().toUpperCase();
        String novaData = atividade.getData();
        formatoData = formatoData.replace("DD","D").replace("MM","M").replace("YYYY","Y");
        novaData = formatoData.replace("D", novaData.substring(6)).replace("M",novaData.substring(4,6)).replace("Y",novaData.substring(0,4));
        builder.setMessage("Nome: "+atividade.getNome()+"\nDescrição: "+atividade.getDescricao()+"\nData: "+novaData+"\nHora: "+horario.substring(0,2)+":"+horario.substring(2));
        builder.setPositiveButton("Editar", new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface arg0, int arg1) {
                Intent i = new Intent(MainActivity.this, EditarAtividadeActivity.class);
                i.putExtra("nome",atividade.getNome());
                i.putExtra("id",atividade.getId());
                i.putExtra("descricao",atividade.getDescricao());
                i.putExtra("data",atividade.getData());
                i.putExtra("hora",atividade.getHorario());
                i.putExtra("repeticao",atividade.getTipoRepeticao());
                startActivity(i);
            }
        });
        builder.setNegativeButton("Excluir", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                new Atividade(getApplicationContext()).excluir(atividade.getId());
                atualizarAtividades(dataVisualizada.get(Calendar.DAY_OF_MONTH),dataVisualizada.get(Calendar.MONTH),dataVisualizada.get(Calendar.YEAR),1);
                new Alarme().cancelarAlarme(MainActivity.this,atividade);
            }
        });
        builder.create().show();
    }

    public void mudarDia(View view){
        DialogFragment fragment = new DatePickerFragment();
        fragment.show(getSupportFragmentManager(),"datePicker");
    }

    public void atualizarAtividades(int dia, int mes, int ano,int periodo){

        //Setando texto do dia
        final Calendar calendario = Calendar.getInstance();
        int anoAtual = calendario.get(Calendar.YEAR);
        int mesAtual = calendario.get(Calendar.MONTH);
        int diaAtual = calendario.get(Calendar.DAY_OF_MONTH);
        TextView txtData = findViewById(R.id.txtData);
        if(anoAtual == ano && mesAtual == mes && diaAtual == dia){
            txtData.setText(R.string.txt_hoje);
        }
        else{
            Format dateFormat = android.text.format.DateFormat.getDateFormat(getApplicationContext());
            String formatoData = ((SimpleDateFormat) dateFormat).toLocalizedPattern().toUpperCase();
            formatoData = formatoData.replace("DD","D").replace("MM","M").replace("YYYY","YY").replace("YY","Y");
            String novaData = formatoData.replace("D",String.format("%02d",dia)).replace("M",String.format("%02d",mes+1)).replace("Y",String.format("%04d",ano));
            txtData.setText(novaData);
        }

        //Setando atividades na lista
        Calendar calendar = new GregorianCalendar();
        calendar.set(ano,mes,dia);
        dataVisualizada = calendar;
        atividades.clear();
        ArrayList<Atividade> atividadeList = (new Atividade(this)).buscarAtividades(calendar,1);
        for (Atividade atividade:atividadeList){
            atividades.add(atividade);
        }
        adapter.notifyDataSetChanged();

        if(atividades.size() == 0){
            ((TextView)findViewById(R.id.txtNadaHoje)).setVisibility(View.VISIBLE);
        } else{
            ((TextView)findViewById(R.id.txtNadaHoje)).setVisibility(View.GONE);
        }
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        TextView txtData = (TextView)findViewById(R.id.txtData);
        final Calendar calendar = Calendar.getInstance();
        int ano = calendar.get(Calendar.YEAR);
        int mes = calendar.get(Calendar.MONTH);
        int dia = calendar.get(Calendar.DAY_OF_MONTH);
        Calendar dataNova = new GregorianCalendar();
        dataNova.set(year,month,dayOfMonth);
        dataVisualizada = dataNova;
        if(year == ano && month == mes && dayOfMonth == dia){
            txtData.setText(R.string.txt_hoje);
        }
        else{
            Format dateFormat = android.text.format.DateFormat.getDateFormat(getApplicationContext());
            String formatoData = ((SimpleDateFormat) dateFormat).toLocalizedPattern().toUpperCase();
            formatoData = formatoData.replace("DD","D").replace("MM","M").replace("YYYY","YY").replace("YY","Y");
            String novaData = formatoData.replace("D",String.format("%02d",dayOfMonth)).replace("M",String.format("%02d",month+1)).replace("Y",String.format("%04d",year));
            txtData.setText(novaData);
        }
        atualizarAtividades(dayOfMonth,month,year,1);
    }
    @Override
    protected void onResume() {
        super.onResume();
        atualizarAtividades(dataVisualizada.get(Calendar.DAY_OF_MONTH),dataVisualizada.get(Calendar.MONTH),dataVisualizada.get(Calendar.YEAR),1);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnAumentar:
                dataVisualizada.add(Calendar.DAY_OF_MONTH,1);
                atualizarAtividades(dataVisualizada.get(Calendar.DAY_OF_MONTH),dataVisualizada.get(Calendar.MONTH),dataVisualizada.get(Calendar.YEAR),1);
                break;
            case R.id.btnDiminuir:
                dataVisualizada.add(Calendar.DAY_OF_MONTH,-1);
                atualizarAtividades(dataVisualizada.get(Calendar.DAY_OF_MONTH),dataVisualizada.get(Calendar.MONTH),dataVisualizada.get(Calendar.YEAR),1);
                break;
        }
    }
}