package com.wtsystems.hallothere.Fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.wtsystems.hallothere.Adapter.ConversasAdapter;
import com.wtsystems.hallothere.ChatActivity;
import com.wtsystems.hallothere.FireBase.FireBaseConfig;
import com.wtsystems.hallothere.Helper.RecyclerItemClickListener;
import com.wtsystems.hallothere.Helper.UserFireBase;
import com.wtsystems.hallothere.Model.Conversa;
import com.wtsystems.hallothere.Model.Usuario;
import com.wtsystems.hallothere.R;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ConversasFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ConversasFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private RecyclerView recyclerViewTalk;
    private List<Conversa> listTalks = new ArrayList<>();
    private ConversasAdapter adapter;

    private DatabaseReference database;

    private DatabaseReference conversasRef;
    private DatabaseReference ultimamsgRef;
    private DatabaseReference userPicRef;

    private ChildEventListener childEventListenerTalks;

    private FirebaseUser actualUser;




    public ConversasFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ConversasFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ConversasFragment newInstance(String param1, String param2) {
        ConversasFragment fragment = new ConversasFragment();
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
        View view = inflater.inflate(R.layout.fragment_conversas, container, false);

        recyclerViewTalk = view.findViewById(R.id.recyclerListTalk);
        actualUser = UserFireBase.getActualUser();


        //Configurar o adapter
        adapter = new ConversasAdapter(listTalks, getActivity());

        //Configurar o recyclerView
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerViewTalk.setLayoutManager(layoutManager);
        recyclerViewTalk.setHasFixedSize(true);
        recyclerViewTalk.setAdapter(adapter);

        //Configurando o evento de clique no item
        recyclerViewTalk.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), recyclerViewTalk,
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Conversa conversaSel  = listTalks.get(position);
                        Intent i = new Intent(getActivity(), ChatActivity.class);
                        i.putExtra("chatContato", conversaSel.getUserExib());
                        startActivity(i);
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {

                    }

                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                    }
                }));



        //Configura Conversas Reference
        String idUser = UserFireBase.getIdUser();
        database = FireBaseConfig.getFireBaseDatabase();
        conversasRef = database.child("Conversas")
                .child(idUser);



        return view;
    }


    private void recuperarConversa(){
        
        childEventListenerTalks = conversasRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                //Recuperar conversas
                listTalks.clear();
                Conversa conversa = snapshot.getValue(Conversa.class);
                listTalks.add(conversa);
                adapter.notifyDataSetChanged();

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                for(DataSnapshot dados: snapshot.getChildren()){
                    listTalks.clear();
                    Conversa conversa = snapshot.getValue(Conversa.class);
                    listTalks.add(conversa);
                    adapter.notifyDataSetChanged();

                }

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void recuperarDadosConversa(){}



    @Override
    public void onStart(){

        super.onStart();
        listTalks.clear();
        recuperarConversa();
    }

    @Override
    public void onStop(){

        super.onStop();
        conversasRef.removeEventListener(childEventListenerTalks);
    }

}