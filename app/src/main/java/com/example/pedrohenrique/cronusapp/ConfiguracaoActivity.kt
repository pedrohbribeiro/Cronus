package com.example.pedrohenrique.cronusapp

import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.provider.Settings
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.CheckBox

class ConfiguracaoActivity : AppCompatActivity(), View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_configuracao)
        supportActionBar!!.title = "Configurações"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {//Só mostra a configuração de canal de notificações para dispositivos com android 8 ou superior
            (findViewById<View>(R.id.btnCanal) as Button).visibility = View.GONE
        }

        val sharedPreferences = getSharedPreferences("configuracoes",0)
        (findViewById<CheckBox>(R.id.chkNotificar)).isChecked = sharedPreferences.getBoolean("notificar",true)//Seta o valor do checkbox de acordo com a configuração selecionada
        (findViewById<CheckBox>(R.id.chkNotificar)).setOnCheckedChangeListener{_, isChecked ->
            sharedPreferences.edit().putBoolean("notificar",isChecked).apply()
            if(isChecked){
                Alarme().setarAlarme(this)
            }
        }
        (findViewById<View>(R.id.btnCanal) as Button).setOnClickListener(this)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item!!.itemId == android.R.id.home) this.finish()
        return super.onOptionsItemSelected(item)
    }

    override fun onClick(v: View) {
        if (v.id == R.id.btnCanal) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val intent = Intent(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS)//Chama a configuração de canal de notificações do próprio celular
                intent.putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
                intent.putExtra(Settings.EXTRA_CHANNEL_ID, "CronusNotification")
                startActivity(intent)
            }
        }
    }
}
