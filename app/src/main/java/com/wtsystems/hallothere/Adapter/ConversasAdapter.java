package com.wtsystems.hallothere.Adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.wtsystems.hallothere.Model.Conversa;
import com.wtsystems.hallothere.Model.Usuario;
import com.wtsystems.hallothere.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ConversasAdapter extends RecyclerView.Adapter<ConversasAdapter.MyViewHolder> {

    private List<Conversa> conversas;
    private Context context;

    public ConversasAdapter(List<Conversa> talks , Context c) {
        this.conversas = talks;
        this.context = c;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemLista = LayoutInflater.from(parent.getContext()).inflate(R.layout.adaptater_contatos, parent, false);

        return new MyViewHolder(itemLista);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Conversa conversa = conversas.get(position);
        holder.lastMsg.setText(conversa.getLastMsg());

        Usuario user  = conversa.getUserExib();
        holder.name.setText(user.getNome());
        if(user.getPicture()!= null){
            Uri uri = Uri.parse(user.getPicture());
            Glide.with(context).load(uri).into(holder.picture);
        }else{
            holder.picture.setImageResource(R.mipmap.ic_logo);
        }

    }

    @Override
    public int getItemCount() {
        return conversas.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder{

        CircleImageView picture;
        TextView name, lastMsg;
        public MyViewHolder(View itemView){
            super(itemView);

            picture = itemView.findViewById(R.id.imgContato);
            name = itemView.findViewById(R.id.txtUserContato);
            lastMsg = itemView.findViewById(R.id.txtUserEmail);

        }

    }

}
