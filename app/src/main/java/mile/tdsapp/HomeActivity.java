package mile.tdsapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.util.Date;

import mile.tdsapp.Model.Data;

public class HomeActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private FloatingActionButton btnFab;
    private RecyclerView recyclerView;
    // firebase
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    private String post_key;
    private String name;
    private String description;
   @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser mUser = mAuth.getCurrentUser();
        String uid = mUser.getUid();

        mDatabase = FirebaseDatabase.getInstance().getReference().child("All Data").child(uid);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //getSupportActionBar().setTitle("TDS APP");
        // recycler view
        recyclerView = findViewById(R.id.recycler_id);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

        btnFab = findViewById(R.id.fab_add);
        btnFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddData();
            }
        });
    }
    private void AddData(){
        AlertDialog.Builder mDialogue = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
        View mView = inflater.inflate(R.layout.inputlayout,null);
        mDialogue.setView(mView);
        final AlertDialog dialog = mDialogue.create();
        dialog.setCancelable(false);

        final EditText name = mView.findViewById(R.id.name);
        final EditText description = mView.findViewById(R.id.description);
        Button btnSave = mView.findViewById(R.id.btn_save);
        Button btnCancel = mView.findViewById(R.id.btn_cancel);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mName = name.getText().toString().trim();
                String mDescription = description.getText().toString().trim();
                if(mName.isEmpty()){
                    name.setError("Name is required.");
                    return;
                }
                if(mDescription.isEmpty()){
                    description.setError("Description is required");
                    return;
                }
                String mId = mDatabase.push().getKey();
                String mDate = DateFormat.getDateInstance().format(new Date());

                Data data = new Data(mName,mDescription,mId,mDate);
                mDatabase.child(mId).setValue(data);
                Toast.makeText(getApplicationContext(),"Data uploaded.",Toast.LENGTH_SHORT);

                dialog.dismiss();
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public void updateData(){
        AlertDialog.Builder mDialogue = new AlertDialog.Builder(this);
        LayoutInflater inflater= LayoutInflater.from(getApplicationContext());
        View mView = inflater.inflate(R.layout.updatelayout,null);
        mDialogue.setView(mView);
        final AlertDialog dialog = mDialogue.create();

        final EditText mName = mView.findViewById(R.id.name);
        final EditText mDescription = mView.findViewById(R.id.description);

        mName.setText(name);
        mName.setSelection(name.length());
        mDescription.setText(description);
        mDescription.setSelection(description.length());

        Button btnDelete = mView.findViewById(R.id.btn_delete);
        Button btnUpdate = mView.findViewById(R.id.btn_update);

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name = mName.getText().toString().trim();
                description = mDescription.getText().toString().trim();
                String date = DateFormat.getDateInstance().format(new Date());
                Data data = new Data(name,description,post_key,date);
                mDatabase.child(post_key).setValue(data);
                dialog.dismiss();
            }
        });
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDatabase.child(post_key).removeValue();
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        //Log.d("OnStart","Calling OnStart.");
        FirebaseRecyclerOptions<Data> options = new FirebaseRecyclerOptions.Builder<Data>().setQuery(mDatabase, new SnapshotParser<Data>() {
            @NonNull
            @Override
            public Data parseSnapshot(@NonNull DataSnapshot snapshot) {
                Log.d("parseSnapshot",snapshot.child("name").getValue().toString());
                return new Data(snapshot.child("name").getValue().toString(),
                        snapshot.child("description").getValue().toString(),
                        snapshot.child("id").getValue().toString(),
                        snapshot.child("date").getValue().toString());
            }
        }).build();
        FirebaseRecyclerAdapter<Data,MyViewHolder> adapter = new FirebaseRecyclerAdapter<Data, MyViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull MyViewHolder myViewHolder, final int i, @NonNull final Data data) {
                //Log.d("OnBindViewHolder",data.getName());
                myViewHolder.setName(data.getName());
                myViewHolder.setDescription(data.getDescription());
                myViewHolder.setDate(data.getDate());
                myViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        post_key = getRef(i).getKey();
                        name = data.getName();
                        description = data.getDescription();
                        updateData();
                    }
                });
            }

            @NonNull
            @Override
            public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                //Log.d("OnCreateViewHolder","Creating the viewHolder");
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.itemlayout,parent,false);
                return new MyViewHolder(view);
            }

            @Override
            public int getItemCount() {
                return super.getItemCount();
            }
        };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
        //Log.d("OnStart","Exiting OnStart.");
    }
    public static class MyViewHolder extends RecyclerView.ViewHolder{
        private View mView;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
        }
        public void setName(String name){
            TextView mName = mView.findViewById(R.id.item_name);
            mName.setText(name);
        }
        public void setDescription(String description){
            TextView mDescription = mView.findViewById(R.id.item_description);
            mDescription.setText(description);
        }
        public void setDate(String date){
            TextView mDate = mView.findViewById(R.id.item_date);
            mDate.setText(date);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
       getMenuInflater().inflate(R.menu.mainmenu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.logout:
                mAuth.signOut();
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}