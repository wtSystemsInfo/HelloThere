package com.wtsystems.hallothere.Adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.wtsystems.hallothere.Helper.UserFireBase;
import com.wtsystems.hallothere.Model.Mensagem;
import com.wtsystems.hallothere.R;

import java.util.List;

public class MensagemAdapter extends RecyclerView.Adapter<MensagemAdapter.MyViewHolder> {


    private List<Mensagem> mensagens;
    private Context context;

    private static final int TIPO_REMETENTE = 0;
    private static final int TIPO_DEST = 1;



    public MensagemAdapter(List<Mensagem> list, Context c) {
        this.mensagens = list;
        this.context = c;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View item = null;

        if(viewType == TIPO_REMETENTE){

            item = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_msg_remetente, parent, false);

        } else if (viewType == TIPO_DEST) {
            item = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_msg_destinatario, parent, false);
        }

        return new MyViewHolder(item);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Mensagem msg = mensagens.get(position);
        String msgText = msg.getMensagem();
        String msgImg = msg.getImage();


        // Restaurar o estado padrão
        holder.foto.setVisibility(View.VISIBLE);
        holder.mensagem.setVisibility(View.VISIBLE);


        if(msgImg != null){
            Uri url = Uri.parse(msgImg);
            Glide.with(context).load(url).into(holder.foto);

            //esconder o texto
            holder.mensagem.setVisibility(View.GONE);

        }else{
            holder.mensagem.setText(msgText);

            //esconder a imagem
            holder.foto.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {

        return mensagens.size();
    }


    @Override
    public int getItemViewType(int position){
        Mensagem msg = mensagens.get(position);
        String idUser = UserFireBase.getIdUser();

        if(idUser.equals(msg.getIdUser())){
            return TIPO_REMETENTE;
        }

        return TIPO_DEST;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView mensagem;
        ImageView foto;
        public MyViewHolder ( View itemView){
            super(itemView);

            mensagem = itemView.findViewById(R.id.txtMsg);
            foto = itemView.findViewById(R.id.imgMsgFoto);
        }
    }
}
