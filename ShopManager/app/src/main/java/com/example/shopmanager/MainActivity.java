package com.example.shopmanager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.database.annotations.NotNull;

import java.io.Serializable;

public class MainActivity extends AppCompatActivity {

    @IgnoreExtraProperties
    static  class Item implements Serializable {
        public  String name;
        public int prise;

        public Item() { }

        public Item(String name, int prise) {
            this.name = name;
            this.prise = prise;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String url = "https://shopmanager-20cd6-default-rtdb.firebaseio.com/";
        FirebaseDatabase db = FirebaseDatabase.getInstance(url);
        db.setPersistenceEnabled(true);
        DatabaseReference dbReference = db.getReference("sales");


        final EditText name = (EditText) findViewById(R.id.item_name_field);
        final EditText prise = (EditText) findViewById(R.id.item_prise_field);
        final Button add = (Button) findViewById(R.id.add_btn);
        //final Button delete = (Button) findViewById(R.id.btn_delete);
        final ListView items = (ListView) findViewById(R.id.items_list);

        final ItemsAdapter adapter = new ItemsAdapter();
        items.setAdapter(adapter);

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String sailName = name.getText().toString();
                int silPrise = Integer.parseInt(prise.getText().toString());
                Item item = new Item(sailName, silPrise);
                //adapter.add(item);
                dbReference.push().setValue(item);
            }
        });

        /*
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });
        */

        dbReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Item item = snapshot.getValue(Item.class);
                adapter.add(item);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                Item item = snapshot.getValue(Item.class);
                adapter.remove(item);
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private class ItemsAdapter extends ArrayAdapter<Item> {

        public ItemsAdapter() {
            super(MainActivity.this, R.layout.item_view);
        }

        @NotNull
        @Override
        public View getView(int position, @Nullable View convertView, @NotNull ViewGroup parent) {
            if (convertView == null) {
                LayoutInflater inflater = getLayoutInflater();
                convertView = inflater.inflate(R.layout.item_view, parent, false);
                Item item = getItem(position);
                ((TextView) convertView.findViewById(R.id.item_name)).setText(item.name);
                ((TextView) convertView.findViewById(R.id.item_prise)).setText( String.valueOf(item.prise));
            }
            return convertView;
        }
    }

}