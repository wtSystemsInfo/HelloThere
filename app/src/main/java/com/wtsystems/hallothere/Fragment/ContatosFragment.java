package com.wtsystems.hallothere.Fragment;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.wtsystems.hallothere.Adapter.ContatosAdapter;
import com.wtsystems.hallothere.ChatActivity;
import com.wtsystems.hallothere.FireBase.FireBaseConfig;
import com.wtsystems.hallothere.Helper.RecyclerItemClickListener;
import com.wtsystems.hallothere.Helper.UserFireBase;
import com.wtsystems.hallothere.Model.Usuario;
import com.wtsystems.hallothere.R;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ContatosFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ContatosFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private RecyclerView recyclerContatos;

    private ContatosAdapter contatosAdapter;

    private ArrayList<Usuario> listContatos = new ArrayList<>();

    private DatabaseReference userRef;

    private ValueEventListener eventListenerContatos;

    private FirebaseUser actualUser;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ContatosFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ContatosFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ContatosFragment newInstance(String param1, String param2) {
        ContatosFragment fragment = new ContatosFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_contatos, container, false);

        //Configurações iniciais
        recyclerContatos = view.findViewById(R.id.recyclerContato);
        userRef = FireBaseConfig.getFireBaseDatabase().child("users");
        actualUser = UserFireBase.getActualUser();


        //Configurar o adapter
        contatosAdapter = new ContatosAdapter(listContatos, getActivity());


        //Configurar o recyclerview
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerContatos.setLayoutManager(layoutManager);
        recyclerContatos.setHasFixedSize(true);
        recyclerContatos.setAdapter(contatosAdapter);


        //Configurar o evento do clique no RecyclerView
        recyclerContatos.addOnItemTouchListener(
                new RecyclerItemClickListener(getActivity(),
                        recyclerContatos,
                        new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                Usuario userSelected = listContatos.get(position);
                                Intent i = new Intent(getActivity(), ChatActivity.class);
                                i.putExtra("chatContato", userSelected);
                                startActivity(i);
                            }

                            @Override
                            public void onLongItemClick(View view, int position) {

                            }

                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                            }
                        }
                ));



        return view;
    }


    @Override
    public void onStart() {
        super.onStart();
        listContatos.clear();
        recuperarContatos();
    }






    @Override
    public void onStop() {
        super.onStop();
        userRef.removeEventListener(eventListenerContatos);
    }

    public void recuperarContatos(){

        eventListenerContatos = userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dados: snapshot.getChildren()){
                    Usuario user = dados.getValue(Usuario.class);

                    if(!actualUser.getEmail().equals(user.getEmail())){

                        int index = -1;
                        for (int i = 0; i < listContatos.size(); i++) {
                            if (listContatos.get(i).getEmail().equals(user.getEmail())) {
                                index = i;
                                break;
                            }
                        }

                        if (index != -1) {
                            // Se o usuário já está na lista, substitui o objeto existente
                            listContatos.set(index, user);
                        } else {
                            // Se o usuário não está na lista, adiciona o novo objeto
                            listContatos.add(user);
                        }
                    }

                }

                contatosAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(TAG, "loadPost:onCancelled", error.toException());
            }
        });
    }
}