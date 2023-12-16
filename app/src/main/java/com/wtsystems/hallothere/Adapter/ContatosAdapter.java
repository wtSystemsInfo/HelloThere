package com.wtsystems.hallothere.Adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.wtsystems.hallothere.Fragment.ContatosFragment;
import com.wtsystems.hallothere.Model.Usuario;
import com.wtsystems.hallothere.R;

import org.w3c.dom.Text;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ContatosAdapter extends RecyclerView.Adapter<ContatosAdapter.MyViewHolder> {

    private List<Usuario> contatos;
    private Context context;

    public ContatosAdapter(List<Usuario> listContatos, Context c) {
        this.contatos = listContatos;
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

        Usuario user = contatos.get(position);
        holder.nome.setText(user.getNome());
        holder.email.setText(user.getEmail());

        if(user.getPicture() != null){
            Uri uri = Uri.parse(user.getPicture());
            Glide.with(context).load(uri).into(holder.contatoImage);
        }else{
            holder.contatoImage.setImageResource(R.mipmap.ic_logo);
        }

    }

    @Override
    public int getItemCount() {
        return contatos.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        CircleImageView contatoImage;
        TextView nome, email;

        public MyViewHolder(View itemView){
            super(itemView);

            contatoImage = itemView.findViewById(R.id.imgContato);
            nome = itemView.findViewById(R.id.txtUserContato);
            email = itemView.findViewById(R.id.txtUserEmail);
        }

    }

}
