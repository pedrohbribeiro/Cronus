package com.example.pedrohenrique.cronusapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Calendar;

public class Atividade {
    private int id;
    private String nome;
    private String descricao;
    private int horario;
    private String data;
    private int repeticao;
    private String tipoRepeticao;
    private String dadoRepeticao1;
    private Context context;

    public Atividade(Context context) {
        this.context = context;
    }
    
    public int getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public int getHorario() {
        return horario;
    }

    public String getData() {
        return data;
    }

    public int getRepeticao() {
        return repeticao;
    }

    public String getTipoRepeticao() {
        return tipoRepeticao;
    }

    public String getDadoRepeticao1() {
        return dadoRepeticao1;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public void setHorario(int horario) {
        this.horario = horario;
    }

    public void setData(String data) {
        this.data = data;
    }

    public void setRepeticao(int repeticao) {
        this.repeticao = repeticao;
    }

    public void setTipoRepeticao(String tipoRepeticao) {
        this.tipoRepeticao = tipoRepeticao;
    }

    public void setDadoRepeticao1(String dadoRepeticao1) {
        this.dadoRepeticao1 = dadoRepeticao1;
    }

    private static final String TABELA1 = "atividade";
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

    public ArrayList<Atividade> buscarAtividades(Calendar dataCalendario, int periodo){
        //Busca todas as atividades do dia especificado pelo Calendar dataCalendario
        SQLiteDatabase db = new Banco(context).getReadableDatabase();
        ArrayList<Atividade> atividades = new ArrayList<Atividade>();
        String data = String.format("%04d",dataCalendario.get(Calendar.YEAR))+String.format("%02d",dataCalendario.get(Calendar.MONTH)+1)+String.format("%02d",dataCalendario.get(Calendar.DAY_OF_MONTH));
        int diaSemana = dataCalendario.get(Calendar.DAY_OF_WEEK);
        String select = "SELECT * FROM "+TABELA1+" WHERE ("+DATA+"="+data+" OR "+_CODIGO+" IN (SELECT "+ATIVIDADE_CODIGO+" FROM "+TABELA2+" WHERE (("+TIPO_REPETICAO+" = 'Diario') OR ("+TIPO_REPETICAO+" = 'Semanal' AND "+DADOTIPO1+" = "+diaSemana+") OR ("+TIPO_REPETICAO+" = 'Mensal' AND "+DADOTIPO1+" = "+String.format("%02d",dataCalendario.get(Calendar.DAY_OF_MONTH))+")))) ORDER BY "+HORARIO+" ASC;";
        Cursor cursor = db.rawQuery(select,null);
        if(cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                Atividade atividade = new Atividade(context);
                atividade.setNome(cursor.getString(cursor.getColumnIndex(NOME)));
                String dataAtividade = String.valueOf(cursor.getString(cursor.getColumnIndex(DATA)));
                atividade.setData(cursor.getString(cursor.getColumnIndex(DATA)));
                atividade.setHorario(cursor.getInt(cursor.getColumnIndex(HORARIO)));
                atividade.setDescricao(cursor.getString(cursor.getColumnIndex(DESCRICAO)));
                atividade.setId(cursor.getInt(cursor.getColumnIndex(_CODIGO)));
                atividade.setTipoRepeticao(cursor.getString(cursor.getColumnIndex(TIPO_REPETICAO)));
                atividades.add(atividade);
                cursor.moveToNext();
            }
        }
        return atividades;
    }

    public void inserir(Atividade atividade){
        //Insere os dados no banco
        ContentValues values = new ContentValues();
        values.put(NOME,atividade.getNome());
        values.put(DESCRICAO,atividade.getDescricao());
        values.put(HORARIO,atividade.getHorario());
        values.put(DATA,atividade.getData());
        values.put(REPETICAO,atividade.getRepeticao());
        values.put(TIPO_REPETICAO,atividade.getTipoRepeticao());
        SQLiteDatabase db = new Banco(context).getWritableDatabase();
        long id = db.insert(TABELA1, null, values);
        if(atividade.getRepeticao() == 1){
            ContentValues valuesRepeticao = new ContentValues();
            valuesRepeticao.put(ATIVIDADE_CODIGO,id);
            valuesRepeticao.put(TIPO_REPETICAO,atividade.getTipoRepeticao());
            valuesRepeticao.put(DADOTIPO1,atividade.getDadoRepeticao1());
            db.insert(TABELA2,null,valuesRepeticao);
        }
    }

    public void excluir(long id){
        //Deleta os dados do banco
        SQLiteDatabase db = new Banco(context).getWritableDatabase();
        String whereClause = ATIVIDADE_CODIGO+"=?";
        String[] whereArgs = new String[] { String.valueOf(id) };
        db.delete(TABELA2, whereClause, whereArgs);
        String whereClause1 = _CODIGO+"=?";
        String[] whereArgs1 = new String[] { String.valueOf(id) };
        db.delete(TABELA1, whereClause1, whereArgs1);
    }

    public boolean checkAtividadeDia(Calendar calendar){
        //Verifica se no dia especificado pelo Calendar calendar existe alguma atividade
        SQLiteDatabase db = new Banco(context).getReadableDatabase();
        ArrayList<Atividade> atividades = new ArrayList<Atividade>();
        String data = String.format("%04d",calendar.get(Calendar.YEAR))+String.format("%02d",calendar.get(Calendar.MONTH)+1)+String.format("%02d",calendar.get(Calendar.DAY_OF_MONTH));
        int diaSemana = calendar.get(Calendar.DAY_OF_WEEK);
        String select = "SELECT * FROM "+TABELA1+" WHERE ("+DATA+"="+data+" OR "+_CODIGO+" IN (SELECT "+ATIVIDADE_CODIGO+" FROM "+TABELA2+" WHERE (("+TIPO_REPETICAO+" = 'Semanal' AND "+DADOTIPO1+" = "+diaSemana+") OR ("+TIPO_REPETICAO+" = 'Mensal' AND "+DADOTIPO1+" = "+String.format("%02d",calendar.get(Calendar.DAY_OF_MONTH))+"))));";
        Cursor cursor = db.rawQuery(select,null);
        cursor.moveToFirst();
        return (cursor.getCount() != 0);
    }

    public void editar(Atividade atividade){
        ContentValues values = new ContentValues();
        values.put(NOME,atividade.getNome());
        values.put(DESCRICAO,atividade.getDescricao());
        values.put(HORARIO,atividade.getHorario());
        values.put(DATA,atividade.getData());
        values.put(TIPO_REPETICAO,atividade.getTipoRepeticao());
        values.put(REPETICAO,atividade.getRepeticao());
        SQLiteDatabase db = new Banco(context).getWritableDatabase();
        db.update(TABELA1,values,_CODIGO+" == ?",new String[]{String.valueOf(atividade.getId())});
        if(atividade.getRepeticao() == 1) {
            String select = "SELECT * FROM " + TABELA2 + " WHERE (" + ATIVIDADE_CODIGO + " = " + atividade.getId() + ");";
            Cursor cursor = db.rawQuery(select, null);
            cursor.moveToFirst();
            if (cursor.getCount() != 0) {
                ContentValues valuesRepeticao = new ContentValues();
                valuesRepeticao.put(TIPO_REPETICAO, atividade.getTipoRepeticao());
                valuesRepeticao.put(DADOTIPO1, atividade.getDadoRepeticao1());
                db.update(TABELA2, valuesRepeticao, ATIVIDADE_CODIGO + " == ?", new String[]{String.valueOf(atividade.getId())});
            } else {
                ContentValues valuesRepeticao = new ContentValues();
                valuesRepeticao.put(ATIVIDADE_CODIGO,id);
                valuesRepeticao.put(TIPO_REPETICAO,atividade.getTipoRepeticao());
                valuesRepeticao.put(DADOTIPO1,atividade.getDadoRepeticao1());
                db.insert(TABELA2,null,valuesRepeticao);
            }
        }

    }
}
