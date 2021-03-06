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
import android.widget.Button;
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

import butterknife.OnClick;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, NewTransactionDialog.TransactionHandler {

    private RecyclerView recyclerViewTransactions;
    private TransactionsAdapter transactionsAdapter;
    private String group;
    private DrawerLayout drawerLayout;
    private TextView tvUserDisplayName;
    private TextView tvUserID;
    private TextView tvNumberOfMembers;
    private TextView tvTotalExpense;
    private TextView tvTotalPerMember;
    private Button btnDeleteAll;
    private int groupNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setUpGroup();

        Toolbar toolbar = setUpToolbar();

        setUpFab();

        setUpDrawer(toolbar);

        setUpRecyclerView();

        setUpInfoBar();

        setUpDeleteAll();

        initTransactions();
    }

    private void setUpGroup() {
        group = getIntent().getStringExtra(getString(R.string.groupName));
        setTitle(group);
    }

    private Toolbar setUpToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        return toolbar;
    }

    private void setUpFab() {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new NewTransactionDialog().show(getFragmentManager(), getString(R.string.newTransactionDialog));
            }
        });
    }

    private void setUpDeleteAll() {
        btnDeleteAll = findViewById(R.id.btnDeleteAll);
        btnDeleteAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteAllItems();
            }
        });
    }

    private void setUpInfoBar() {
        tvNumberOfMembers = findViewById(R.id.tvNumberOfMembers);
        tvTotalExpense = findViewById(R.id.tvTotalExpense);
        tvTotalPerMember = findViewById(R.id.tvTotalPerMember);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(getString(R.string.groups)).child(group).
                child(getString(R.string.groupMembers));
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                groupNumber = dataSnapshot.getValue(Integer.class);
                tvNumberOfMembers.setText(getString(R.string.numMembers)+ groupNumber);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void setUpRecyclerView() {
        transactionsAdapter = new TransactionsAdapter(getApplicationContext(),
                FirebaseAuth.getInstance().getCurrentUser().getUid(), group);
        recyclerViewTransactions = (RecyclerView) findViewById(R.id.recyclerViewTransactions);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerViewTransactions.setLayoutManager(layoutManager);
        recyclerViewTransactions.setAdapter(transactionsAdapter);
    }

    private void setUpDrawer(Toolbar toolbar) {
        drawerLayout = findViewById(R.id.drawer_layout);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        setUpDrawerHeader();
    }

    private void setUpDrawerHeader() {
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View headerView = navigationView.getHeaderView(0);
        tvUserDisplayName = headerView.findViewById(R.id.tvUserDisplayName);
        tvUserID = headerView.findViewById(R.id.tvUserID);
        tvUserDisplayName.setText(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
        tvUserID.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail());
    }


    private void initTransactions() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(getString(R.string.groups)).child(group).
                child(getString(R.string.transactions));

        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                addTransaction(dataSnapshot);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                removeTransaction(dataSnapshot);
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void removeTransaction(DataSnapshot dataSnapshot) {
        transactionsAdapter.removeTransactionByKey(dataSnapshot.getKey());
        double totalExpenses = transactionsAdapter.totalExpenses();
        double splitExpense = Math.ceil(totalExpenses/groupNumber*1000)/1000;
        tvTotalExpense.setText(getString(R.string.totalExpense) + String.format(getString(R.string.decimalSymbol),totalExpenses));
        tvTotalPerMember.setText(getString(R.string.amountOwed) +
                String.format(getString(R.string.decimalSymbol), splitExpense));
    }

    private void addTransaction(DataSnapshot dataSnapshot) {
        Transaction newTransaction = dataSnapshot.getValue(Transaction.class);
        transactionsAdapter.addTransaction(newTransaction, dataSnapshot.getKey());
        double totalExpenses = transactionsAdapter.totalExpenses();
        double splitExpense = Math.ceil(totalExpenses/groupNumber*100)/100;
        tvTotalExpense.setText(getString(R.string.totalExpense) + String.format(getString(R.string.decimalSymbol),totalExpenses));
        tvTotalPerMember.setText(getString(R.string.amountOwed) +
                String.format(getString(R.string.decimalSymbol), splitExpense));
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

    public void deleteAllItems() {
        transactionsAdapter.deleteAll();
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
            showSnackBarMessage(getString(R.string.aboutMsg));
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
