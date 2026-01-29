package com.example.contact_firebase;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ListView listView;
    private EditText etSearch;
    private Button btnAdd;
    private DatabaseReference dbRef;
    private List<Contact> contactList;
    private ArrayAdapter<String> adapter;
    private List<String> contactNames;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Liens avec les composants UI
        listView = findViewById(R.id.listView);
        etSearch = findViewById(R.id.etSearch);
        btnAdd = findViewById(R.id.btnAdd);

        // Référence Firebase
        dbRef = FirebaseDatabase.getInstance().getReference("contacts");

        // Initialisation des listes
        contactList = new ArrayList<>();
        contactNames = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, contactNames);
        listView.setAdapter(adapter);

        // Charger les contacts
        loadContacts();

        // Bouton Ajouter
        btnAdd.setOnClickListener(v -> showAddDialog());

        // Cliquer sur un contact pour modifier ou supprimer
        listView.setOnItemClickListener((parent, view, position, id) -> {
            Contact contact = contactList.get(position);
            showEditDialog(contact);
        });

        // Recherche dynamique
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchContacts(s.toString());
            }
        });
    }

    // Charger tous les contacts depuis Firebase
    private void loadContacts() {
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                contactList.clear();
                contactNames.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Contact contact = ds.getValue(Contact.class);
                    if (contact != null) {
                        contactList.add(contact);
                        contactNames.add(contact.getNom() + " - " + contact.getTel());
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    // Filtrer les contacts selon la recherche
    private void searchContacts(String keyword) {
        contactNames.clear();
        for (Contact c : contactList) {
            if (c.getNom().toLowerCase().contains(keyword.toLowerCase())) {
                contactNames.add(c.getNom() + " - " + c.getTel());
            }
        }
        adapter.notifyDataSetChanged();
    }

    // Ajouter un contact
    private void showAddDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Ajouter Contact");

        View view = LayoutInflater.from(this).inflate(R.layout.dialog_add_edit_contact, null);
        EditText etNom = view.findViewById(R.id.etNom);
        EditText etTel = view.findViewById(R.id.etTel);
        builder.setView(view);

        builder.setPositiveButton("Ajouter", (dialog, which) -> {
            String nom = etNom.getText().toString().trim();
            String tel = etTel.getText().toString().trim();
            if (!nom.isEmpty() && !tel.isEmpty()) {
                String id = dbRef.push().getKey();
                if (id != null) {
                    Contact contact = new Contact(id, nom, tel);
                    dbRef.child(id).setValue(contact);
                }
            }
        });

        builder.setNegativeButton("Annuler", null);
        builder.show();
    }

    // Modifier ou supprimer un contact existant
    private void showEditDialog(Contact contact) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Modifier / Supprimer Contact");

        View view = LayoutInflater.from(this).inflate(R.layout.dialog_add_edit_contact, null);
        EditText etNom = view.findViewById(R.id.etNom);
        EditText etTel = view.findViewById(R.id.etTel);
        etNom.setText(contact.getNom());
        etTel.setText(contact.getTel());
        builder.setView(view);

        builder.setPositiveButton("Modifier", (dialog, which) -> {
            String newNom = etNom.getText().toString().trim();
            String newTel = etTel.getText().toString().trim();
            if (!newNom.isEmpty() && !newTel.isEmpty()) {
                contact.setNom(newNom);
                contact.setTel(newTel);
                dbRef.child(contact.getId()).setValue(contact);
            }
        });

        builder.setNeutralButton("Supprimer", (dialog, which) -> {
            dbRef.child(contact.getId()).removeValue();
            contactList.remove(contact);
            contactNames.remove(contact.getNom() + " - " + contact.getTel());
            adapter.notifyDataSetChanged();
        });

        builder.setNegativeButton("Annuler", null);
        builder.show();
    }
}
