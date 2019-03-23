package com.example.pedrohenrique.cronusapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class Banco extends SQLiteOpenHelper {

    private static final String NOME_BANCO = "AtividadesCronus.db";
    private static final String TABELA1 = "atividade";
    private static final int VERSAO = 1;
    private static final String _CODIGO = "codigo";
    private static final String NOME = "nome";
    private static final String DESCRICAO = "descricao";
    private static final String HORARIO = "horario";
    private static final String DATA = "data";
    private static final String REPETICAO = "repeticao";
    private static final String TABELA2 = "repeticao";
    private static final String ATIVIDADE_CODIGO = "Atividade_Codigo";
    private static final String TIPO_REPETICAO = "tipoRepeticao";
    private static final String DADOTIPO1 = "dadoTipo1";
    private static final String DADOTIPO2 = "dadoTipo2";
    public Banco(Context context) {
        super(context, NOME_BANCO, null, VERSAO);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABELA1 + " ("+_CODIGO+" INTEGER PRIMARY KEY AUTOINCREMENT,"+NOME+" TEXT,"+DESCRICAO+" TEXT ,"+HORARIO+" INTEGER,"+DATA+" INTEGER,"+REPETICAO+" INTEGER, "+TIPO_REPETICAO+" TEXT);");
        db.execSQL("CREATE TABLE " + TABELA2 + " ("+ATIVIDADE_CODIGO+" INTEGER ," + TIPO_REPETICAO + " TEXT," + DADOTIPO1 + " TEXT," + DADOTIPO2 + " TEXT);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABELA1);
        db.execSQL("DROP TABLE IF EXISTS " + TABELA2);
        onCreate(db);
    }


}
