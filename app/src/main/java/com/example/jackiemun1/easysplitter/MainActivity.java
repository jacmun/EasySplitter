package com.example.jackiemun1.easysplitter;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jackiemun1.easysplitter.adapter.TransactionsAdapter;
import com.example.jackiemun1.easysplitter.data.Transaction;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, NewTransactionDialog.TransactionHandler {

    private TransactionsAdapter transactionsAdapter;
    private String group;
    private DrawerLayout drawerLayout;
    private TextView tvUserDisplayName;
    private TextView tvUserID;
    private TextView tvNumberOfMembers;
    private TextView tvTotalExpense;
    private TextView tvTotalPerMember;
    private int groupNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvUserDisplayName = findViewById(R.id.tvUserDisplayName);
        tvUserID = findViewById(R.id.tvUserID);
        drawerLayout = findViewById(R.id.drawer_layout);

        group = getIntent().getStringExtra("GROUP_NAME");
        setTitle(group);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new NewTransactionDialog().show(getFragmentManager(), "NewTransactionDialog");
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        transactionsAdapter = new TransactionsAdapter(getApplicationContext(),
                FirebaseAuth.getInstance().getCurrentUser().getUid(), group);
        RecyclerView recyclerViewTransactions = (RecyclerView) findViewById(R.id.recyclerViewTransactions);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerViewTransactions.setLayoutManager(layoutManager);
        recyclerViewTransactions.setAdapter(transactionsAdapter);

        tvNumberOfMembers = findViewById(R.id.tvNumberOfMembers);
        tvTotalExpense = findViewById(R.id.tvTotalExpense);
        tvTotalPerMember = findViewById(R.id.tvTotalPerMember);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("groups").child(group).
                child("group members");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                groupNumber = dataSnapshot.getValue(Integer.class);
                tvNumberOfMembers.setText("Number of members in group: "+ groupNumber);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        initTransactions();
    }

    private void initTransactions() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("groups").child(group).
                child("transactions");

        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Transaction newTransaction = dataSnapshot.getValue(Transaction.class);
                transactionsAdapter.addTransaction(newTransaction, dataSnapshot.getKey());
                double totalExpenses = transactionsAdapter.totalExpenses();
                double splitExpense = Math.ceil(totalExpenses/groupNumber*100)/100;
                tvTotalExpense.setText("Total expense: $" + String.format("%.2f",totalExpenses));
                tvTotalPerMember.setText("Amount each person needs to pay: $" +
                        String.format("%.2f", splitExpense));
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                transactionsAdapter.removeTransactionByKey(dataSnapshot.getKey());
                double totalExpenses = transactionsAdapter.totalExpenses();
                double splitExpense = Math.ceil(totalExpenses/groupNumber*1000)/1000;
                tvTotalExpense.setText("Total expense: $" + String.format("%.2f",totalExpenses));
                tvTotalPerMember.setText("Amount each person needs to pay: $" +
                        String.format("%.2f", splitExpense));
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }



    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_logout) {
            FirebaseAuth.getInstance().signOut();
            finish();
        }
        if (id == R.id.nav_about) {
            showSnackBarMessage("HariniPoo & JackiePoo");
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onNewTransactionCreated(Transaction newTransaction) {
        String key = FirebaseDatabase.getInstance().getReference().child("groups").
                child(group).child("transactions").push().getKey();
        FirebaseDatabase.getInstance().getReference().child("groups").
                child(group).child("transactions").child(key).setValue(newTransaction).
                addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(MainActivity.this, "Transaction created", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showSnackBarMessage(String message) {
        Snackbar.make(drawerLayout,
                message,
                Snackbar.LENGTH_LONG
        ).setAction("Hide", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //...
            }
        }).show();
    }
}
