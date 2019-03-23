package com.example.pedrohenrique.cronusapp

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.DialogInterface
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.view.MenuItem
import android.view.View
import android.widget.*
import kotlinx.android.synthetic.main.activity_editar_atividade.*
import java.text.SimpleDateFormat
import java.util.*

class EditarAtividadeActivity : AppCompatActivity(), View.OnClickListener, DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    var data = "";
    var nomeOriginal = "";
    var idOriginal = 0;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editar_atividade)
        supportActionBar!!.title = "Editar Atividade"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        //Setar itens do spinner
        val arraySpinner = arrayOf("Nenhuma", "Diariamente", "Semanalmente", "Mensalmente")
        val spinner = findViewById<Spinner>(R.id.spinnerRepeticao)
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, arraySpinner)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
        (findViewById<Button>(R.id.btnCancelar)).setOnClickListener(this)
        (findViewById<Button>(R.id.btnEditar)).setOnClickListener(this)
        //Seta os valores atuais da atividade
        val bundle = intent.extras
        nomeOriginal = bundle!!.getString("nome", "")
        idOriginal = bundle.getInt("id")
        (findViewById<EditText>(R.id.txtNome)).setText(nomeOriginal)
        (findViewById<EditText>(R.id.txtDescricao)).setText(bundle.getString("descricao", "erro"))
        when (bundle.getString("repeticao")) {
            "Nenhuma" -> spinner.setSelection(0)
            "Diario" -> spinner.setSelection(1)
            "Semanal" -> spinner.setSelection(2)
            "Mensal" -> spinner.setSelection(3)
        }
        //Setar data
        val dateFormat = android.text.format.DateFormat.getDateFormat(applicationContext)
        var formatoData = (dateFormat as SimpleDateFormat).toLocalizedPattern().toUpperCase()
        var novaData: String = bundle.getString("data", "00000000")
        data = novaData
        formatoData = formatoData.replace("DD", "D").replace("MM", "M").replace("YYYY", "Y")
        novaData = formatoData.replace("D", novaData.substring(6)).replace("M", novaData.substring(4, 6)).replace("Y", novaData.substring(0, 4))
        txtData.text = novaData

        //Setar hora
        val hora = String.format("%04d", bundle.getInt("hora", 0))
        val txtHora = findViewById<TextView>(R.id.txtHora)
        txtHora.text = ((hora.substring(0, 2)) + ":" + hora.substring(2))
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item!!.itemId == android.R.id.home) this.finish()
        return super.onOptionsItemSelected(item)
    }

    fun mudarDia(view: View) {
        val fragment = DatePickerFragment();
        fragment.show(supportFragmentManager, "datePicker")//Chama o datepickerfragment para atualizar a data
    }

    fun mudarHora(view: View) {
        val fragment = TimePickerFragment();
        fragment.show(supportFragmentManager, "timePicker")//Chama o timepickerfragment para atualizar a hora
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btnEditar -> {
                val txtNome = findViewById<EditText>(R.id.txtNome)
                if (txtNome.text.toString() == "") {
                    //Verifica se o usuário setou o nome da atividade
                    Toast.makeText(this, "O nome é obrigatório", Toast.LENGTH_LONG).show()
                } else {
                    //Setando os dados na classe Atividade
                    val atividade = Atividade(this)
                    atividade.nome = txtNome.text.toString()//Salva os dados na classe atividade
                    atividade.descricao = (findViewById<EditText>(R.id.txtDescricao)).text.toString()
                    atividade.data = data
                    val hora = (findViewById<TextView>(R.id.txtHora)).text.toString().replace(":", "")
                    atividade.horario = Integer.parseInt(hora)
                    val spinner = findViewById<Spinner>(R.id.spinnerRepeticao)
                    if (spinner.selectedItemPosition != 0) {//Verifica qual a repetição selecionada pelo usuário
                        atividade.repeticao = 1
                        if (spinner.selectedItemPosition == 1) {
                            atividade.tipoRepeticao = "Diario"
                        } else if (spinner.selectedItemPosition == 2) {
                            atividade.tipoRepeticao = "Semanal"
                            val calendar = GregorianCalendar()
                            calendar.set(Integer.valueOf(data.substring(0, 4)), Integer.valueOf(data.substring(4, 6)) - 1, Integer.valueOf(data.substring(6)))
                            atividade.dadoRepeticao1 = calendar.get(Calendar.DAY_OF_WEEK).toString()
                        } else if (spinner.selectedItemPosition == 3) {
                            atividade.tipoRepeticao = "Mensal"
                            atividade.dadoRepeticao1 = data.substring(6)
                        }
                    } else {
                        atividade.repeticao = 0
                    }
                    val atividadeCancelar = Atividade(this)
                    atividadeCancelar.nome = nomeOriginal
                    atividadeCancelar.id = idOriginal
                    atividade.id = idOriginal
                    atividade.editar(atividade)
                    Alarme().cancelarAlarme(this, atividadeCancelar)//Cancela o alarme anterior
                    Alarme().setarAlarme(this)//Coloca o alarme para enviar a notificação
                    finish()//Finaliza a activity após alterar os dados
                }
            }
            R.id.btnCancelar -> finish() //Se o usuário clicar em cancelar, finaliza a activity
        }
    }

    override fun onDateSet(view: DatePicker, year: Int, month: Int, dayOfMonth: Int) {
        val txtData = findViewById<TextView>(R.id.txtData)
        val dateFormat = android.text.format.DateFormat.getDateFormat(applicationContext)
        var formatoData = (dateFormat as SimpleDateFormat).toLocalizedPattern().toUpperCase()//Pega o formato de data do usuário
        formatoData = formatoData.replace("DD", "D").replace("MM", "M").replace("YYYY", "YY").replace("YY", "Y");
        val novaData = formatoData.replace("D", String.format("%02d", dayOfMonth)).replace("M", String.format("%02d", month + 1)).replace("Y", String.format("%04d", year))
        data = String.format("%04d", year) + String.format("%02d", month + 1) + String.format("%02d", dayOfMonth)
        txtData.text = novaData //Seta no textview a data escolhida pelo usuário
    }


    override fun onTimeSet(view: TimePicker, hourOfDay: Int, minute: Int) {
        val txtHora = findViewById<TextView>(R.id.txtHora)
        txtHora.text = (String.format("%02d", hourOfDay) + ":" + String.format("%02d", minute)) //Seta no textview a hora escolhida pelo usuário
    }
}
