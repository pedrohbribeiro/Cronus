package com.example.pedrohenrique.cronusapp;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class AdicionarActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener, View.OnClickListener {

    String data;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adicionar);
        getSupportActionBar().setTitle("Adicionar atividade");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //Setar itens do spinner
        String[] arraySpinner = new String[]{"Nenhuma","Diariamente","Semanalmente","Mensalmente"};
        Spinner spinner = (Spinner) findViewById(R.id.spinnerRepeticao);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, arraySpinner);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        //Setar data do dia
        Calendar calendar = Calendar.getInstance();
        TextView txtData = (TextView) findViewById(R.id.txtData);
        Format dateFormat = android.text.format.DateFormat.getDateFormat(getApplicationContext());
        String formatoData = ((SimpleDateFormat) dateFormat).toLocalizedPattern().toUpperCase(); //Pega o formato de data do usuário
        formatoData = formatoData.replace("DD","D").replace("MM","M").replace("YYYY","YY").replace("YY","Y");
        String novaData = formatoData.replace("D", String.format("%02d", calendar.get(Calendar.DAY_OF_MONTH))).replace("M", String.format("%02d", calendar.get(Calendar.MONTH) + 1)).replace("Y", String.format("%04d", calendar.get(Calendar.YEAR))); //Insere a data no formato encontrado
        txtData.setText(novaData);
        data=String.format("%04d", calendar.get(Calendar.YEAR))+String.format("%02d", calendar.get(Calendar.MONTH) + 1)+String.format("%02d", calendar.get(Calendar.DAY_OF_MONTH));

        //Setar hora atual
        TextView txtHora = (TextView)findViewById(R.id.txtHora);
        txtHora.setText(String.format("%02d",calendar.get(Calendar.HOUR_OF_DAY)) +":"+ String.format("%02d",calendar.get(Calendar.MINUTE)));

        //Botões
        ((Button)findViewById(R.id.btnSalvar)).setOnClickListener(this);
        ((Button)findViewById(R.id.btnCancelar)).setOnClickListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public void mudarDia(View view){
        DialogFragment fragment = new DatePickerFragment();
        fragment.show(getSupportFragmentManager(),"datePicker");//Chama o datepickerfragment para atualizar a data
    }

    public void mudarHora(View view){
        DialogFragment fragment = new TimePickerFragment();
        fragment.show(getSupportFragmentManager(),"timePicker");//Chama o timepickerfragment para atualizar a hora
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        TextView txtData = (TextView) findViewById(R.id.txtData);
        Format dateFormat = android.text.format.DateFormat.getDateFormat(getApplicationContext());
        String formatoData = ((SimpleDateFormat) dateFormat).toLocalizedPattern().toUpperCase();//Pega o formato de data do usuário
        formatoData = formatoData.replace("DD","D").replace("MM","M").replace("YYYY","YY").replace("YY","Y");
        String novaData = formatoData.replace("D", String.format("%02d", dayOfMonth)).replace("M", String.format("%02d", month + 1)).replace("Y", String.format("%04d", year));
        data = String.format("%04d",year)+String.format("%02d",month+1)+String.format("%02d",dayOfMonth);
        txtData.setText(novaData);
    }


    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        TextView txtHora = (TextView)findViewById(R.id.txtHora);
        txtHora.setText(String.format("%02d",hourOfDay) +":"+ String.format("%02d",minute));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnSalvar:
                EditText txtNome = (EditText)findViewById(R.id.txtNome);
                if(txtNome.getText().toString().equals("")){//Verifica se o usuário setou o nome da atividade
                    Toast.makeText(this,"O nome é obrigatório",Toast.LENGTH_LONG).show();
                }else{
                    Atividade atividade = new Atividade(this);
                    atividade.setNome(txtNome.getText().toString());//Salva os dados na classe atividade
                    atividade.setDescricao(((EditText)findViewById(R.id.txtDescricao)).getText().toString());
                    atividade.setData(data);
                    String hora = ((TextView)findViewById(R.id.txtHora)).getText().toString().replace(":","");
                    atividade.setHorario(Integer.parseInt(hora));
                    Spinner spinner = (Spinner)findViewById(R.id.spinnerRepeticao);
                    if(spinner.getSelectedItemPosition() != 0){//Verifica qual a repetição selecionada pelo usuário
                        atividade.setRepeticao(1);
                        if(spinner.getSelectedItemPosition() == 1){
                            atividade.setTipoRepeticao("Diario");
                        } else if(spinner.getSelectedItemPosition() == 2){
                            atividade.setTipoRepeticao("Semanal");
                            Calendar calendar = new GregorianCalendar();
                            calendar.set(Integer.valueOf(data.substring(0,4)),Integer.valueOf(data.substring(4,6))-1,Integer.valueOf(data.substring(6)));
                            atividade.setDadoRepeticao1(String.valueOf(calendar.get(Calendar.DAY_OF_WEEK)));
                        } else if(spinner.getSelectedItemPosition() == 3){
                            atividade.setTipoRepeticao("Mensal");
                            atividade.setDadoRepeticao1(data.substring(6));
                        }
                    }else {
                        atividade.setRepeticao(0);
                        atividade.setTipoRepeticao("Nenhuma");
                    }
                    atividade.inserir(atividade);//Insere os dados no banco de dados
                    new Alarme().setarAlarme(this);//Coloca o alarme para enviar a notificação
                    finish();
                }
                break;
            case R.id.btnCancelar:
                finish();
                break;
        }
    }
}
