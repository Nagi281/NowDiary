package com.example.naginowdiary;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.naginowdiary.customAdapter.JournalAdapter;
import com.example.naginowdiary.customAdapter.RecyclerItemClickListener;
import com.example.naginowdiary.model.JournalItem;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    public final static int REQUEST_ADD_JOURNAL = 113;
    public final static int REQUEST_EDIT_JOURNAL = 114;
    public final static int REQUEST_DELETE_JOURNAL = 911;

    private FloatingActionButton mFabAddJournal;
    private RecyclerView mRvJournalList;
    private List<JournalItem> journalList;
    private JournalAdapter adapter;
    private LinearLayoutManager mLayoutManager;
    private DatabaseReference mDbRoot, mDbRef, mDbJournal;
    private FirebaseDatabase firebaseDb;
    private ImageButton mBtnLogOut;
    private String userId;
    private int editIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        firebaseDb = FirebaseDatabase.getInstance();
        mDbRoot = firebaseDb.getReference();
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Log.d("login123", userId);
        journalList = new ArrayList<>();
        checkForUserIdInDatabase();
        addComponents();
        addEventListeners();
    }

    public void checkForUserIdInDatabase() {
        Log.d("login123", "checking for journal");
        mDbRoot.child(userId).child("journal")
                .addListenerForSingleValueEvent(
                        new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                Log.d("login123", "D123");
                                if (!dataSnapshot.exists()) {
                                    JournalItem item1 = new JournalItem(new Date(),
                                            "First time Using Journal App!",
                                            "Auto generated Journal!",
                                            123123);
                                    item1.setId("foo");
                                    journalList.add(item1);
                                    Log.d("login", "D123");
                                    mDbRoot.child(userId).child("journal").setValue(journalList)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        journalList.clear();
                                                        Toast.makeText(MainActivity.this,
                                                                "Login successfully",
                                                                Toast.LENGTH_SHORT).show();
                                                    } else {
                                                        Toast.makeText(MainActivity.this,
                                                                "Add userId failed",
                                                                Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                } else {
                                    Log.d("journal exist", "Hello");
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        }
                );
        mDbRef = mDbRoot.child(userId);
        mDbJournal = mDbRef.child("journal");
    }

    public void addComponents() {
        Toolbar toolbar = findViewById(R.id.toolbar_for_main);
        setSupportActionBar(toolbar);
        mBtnLogOut = findViewById(R.id.btn_logout);
        mFabAddJournal = findViewById(R.id.fab_add_journal);
        mRvJournalList = findViewById(R.id.rv_journal);
        firebaseDb = FirebaseDatabase.getInstance();
        mDbRoot = firebaseDb.getReference();
        adapter = new JournalAdapter(this, journalList);
        mLayoutManager = new LinearLayoutManager(this);
        mRvJournalList.setLayoutManager(mLayoutManager);
        mRvJournalList.setAdapter(adapter);
    }

    public void addEventListeners() {
        mBtnLogOut.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        logoutConfirm();
                    }
                }
        );
        mFabAddJournal.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(MainActivity.this,
                                AddJournalActivity.class);
                        intent.putExtra("request", REQUEST_ADD_JOURNAL);
                        startActivityForResult(intent, REQUEST_ADD_JOURNAL);
                    }
                }
        );
        mDbJournal.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d("load Data", "D121231231231233");
                journalList.clear();

                for (DataSnapshot d : dataSnapshot.getChildren()) {
                    JournalItem tD = d.getValue(JournalItem.class);
                    journalList.add(tD);
                }
                sortJournal(journalList);
                mDbJournal.removeEventListener(this);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        mRvJournalList.addOnItemTouchListener(
                new RecyclerItemClickListener(this, mRvJournalList,
                        new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                Intent intent = new Intent(MainActivity.this,
                                        AddJournalActivity.class);
                                Bundle bundle = new Bundle();
                                bundle.putSerializable("journalItem", journalList.get(position));
                                intent.putExtra("package", bundle);
                                intent.putExtra("request", REQUEST_EDIT_JOURNAL);
                                editIndex = position;
                                startActivityForResult(intent, REQUEST_EDIT_JOURNAL);
                            }

                            @Override
                            public void onLongItemClick(View view, int position) {
                                // do whatever
                            }
                        })
        );
    }

    private void logoutConfirm() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Logout Confirm!");
        builder.setMessage("Are you sure to logout?");
        builder.setCancelable(false);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                FirebaseAuth.getInstance().signOut();
                LoginActivity.mGoogleSignInClient.signOut()
                        .addOnCompleteListener(MainActivity.this, new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {

                                }
                                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                            }
                        });
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        builder.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ADD_JOURNAL && resultCode == RESULT_OK) {
            Bundle bundle = data.getBundleExtra("bundle");
            final JournalItem journalItem = (JournalItem) bundle.getSerializable("journalItem");
            DatabaseReference mDbJournal = mDbRef.child("journal");
            String id = mDbJournal.push().getKey();
            journalItem.setId(id);

            mDbJournal.child(id).setValue(journalItem).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        journalList.add(journalItem);
                        sortJournal(journalList);
                        adapter.notifyDataSetChanged();
                        Toast.makeText(MainActivity.this,
                                "Add Journal Complete", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MainActivity.this,
                                "Add failed", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        if (requestCode == REQUEST_EDIT_JOURNAL && resultCode == RESULT_OK) {
            int requestConfirm = data.getExtras().getInt("requestConfirm");
            if (requestConfirm == REQUEST_EDIT_JOURNAL) {
                Bundle bundle = data.getBundleExtra("bundle");
                final JournalItem journalItem = (JournalItem) bundle.getSerializable("journalItem");
                DatabaseReference mDbJournal = mDbRef.child("journal");
                mDbJournal.child(journalItem.getId()).setValue(journalItem).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            journalList.get(editIndex).setDate(journalItem.getDate());
                            journalList.get(editIndex).setColor(journalItem.getColor());
                            journalList.get(editIndex).setTitle(journalItem.getTitle());
                            journalList.get(editIndex).setContent(journalItem.getContent());
                            sortJournal(journalList);
                            adapter.notifyDataSetChanged();
                            Toast.makeText(MainActivity.this,
                                    "Edit Journal Complete", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(MainActivity.this,
                                    "Edit failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
            if (requestConfirm == REQUEST_DELETE_JOURNAL) {
                mDbJournal.child(journalList.get(editIndex).getId()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            journalList.remove(editIndex);
                            adapter.notifyDataSetChanged();
                            Toast.makeText(MainActivity.this,
                                    "Journal Deleted", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(MainActivity.this,
                                    task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }

        }
    }

    public static void sortJournal(List<JournalItem> journalItems) {
        Collections.sort(journalItems, new Comparator<JournalItem>() {
            @Override
            public int compare(JournalItem o1, JournalItem o2) {
                if (o1.getDate().equals(o2.getDate())) {
                    return 0;
                } else if (o1.getDate().before(o2.getDate())) {
                    return 1;
                } else {
                    return -1;
                }
            }
        });
    }
}
