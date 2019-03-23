package com.example.pedrohenrique.cronusapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class AtividadeAdapter extends ArrayAdapter<Atividade> {

    Context context;
    int layoutResourceId;
    List<Atividade> dados;

    public AtividadeAdapter(Context context, int layoutResourceId, List<Atividade> dados) {
        super(context, layoutResourceId, dados);
        this.context = context;
        this.layoutResourceId = layoutResourceId;
        this.dados = dados;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //Seta os dados no layout para exibir no listview
        View view = convertView;
        if (view == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            view = layoutInflater.inflate(layoutResourceId, parent, false);
        }
        TextView txtNome = (TextView)view.findViewById(R.id.txtNomeAtividade);
        TextView txtHorario = (TextView)view.findViewById(R.id.txtHorarioAtividade);
        TextView txtDescricao = (TextView)view.findViewById(R.id.txtDescricao);

        Atividade atividade = dados.get(position);
        txtNome.setText(atividade.getNome());
        String horario = String.format("%04d",atividade.getHorario());
        txtHorario.setText(horario.substring(0,2)+":"+horario.substring(2));
        txtDescricao.setText(atividade.getDescricao());
        if(atividade.getDescricao().equals("") || atividade.getDescricao()==null){
            txtDescricao.setVisibility(View.GONE);
        } else{
            txtDescricao.setVisibility(View.VISIBLE);
        }

        return view;
    }

    @Override
    public long getItemId(int position) {
        return dados.get(position).getId();
    }
}
