package com.example.expensetracker

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FloatingActionButtonElevation
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.expensetracker.ui.theme.ExpensetrackerTheme
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

@Suppress("DEPRECATION")
class MainActivity : ComponentActivity() {

    private lateinit var deletedTransaction: Transaction
    private lateinit var transactions : List<Transaction>
    private lateinit var oldTransactions : List<Transaction>
    private lateinit var transactionAdapter : TransactionAdapter
    private lateinit var LinearlayoutManager : LinearLayoutManager
    private lateinit var db : Appdatabase

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        transactions = arrayListOf()

        transactionAdapter = TransactionAdapter(transactions)
        LinearlayoutManager = LinearLayoutManager(this)

        db = Room.databaseBuilder(this,
            Appdatabase::class.java,
            "transactions").build()

        val recycleview = findViewById<RecyclerView>(R.id.recyclerview)
        recycleview.apply {
            adapter = transactionAdapter
            layoutManager = LinearlayoutManager
        }

        // swipe to remove
        val itemTouchHelper = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT){
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                deleteTransaction(transactions[viewHolder.adapterPosition])
            }

        }

        val swipeHelper = ItemTouchHelper(itemTouchHelper)
        swipeHelper.attachToRecyclerView(recycleview)


        val addbtn =  findViewById<FloatingActionButton>(R.id.addbtn)
        addbtn.setOnClickListener {
            val intent = Intent(this, AddTransactionActivity::class.java)
            startActivity(intent)
        }


    }

    private fun fetchAll(){
        GlobalScope.launch {
            transactions = db.transactionDao().getAll()

            runOnUiThread {
                updateDashboard()
                transactionAdapter.setData(transactions)
            }
        }
    }
    private  fun updateDashboard(){
        val totalAmount = transactions.map { it.amount }.sum()
        val budgetAmount = transactions.filter { it.amount>0 }.map { it.amount }.sum()
        val exoenseAmount = totalAmount - budgetAmount

        val balance = findViewById<TextView>(R.id.balance)
        balance.text = ("₹ " +
                "%" +
                ".2f").format(totalAmount)
        val budget = findViewById<TextView>(R.id.budget)
        budget.text = "₹ %.2f".format(budgetAmount)
        val expense = findViewById<TextView>(R.id.expense)
        expense.text = "₹ %.2f".format(exoenseAmount)



    }

    private fun undoDelete(){
        GlobalScope.launch {
            db.transactionDao().insertAll(deletedTransaction)

            transactions =  oldTransactions
            runOnUiThread {
                transactionAdapter.setData(transactions)
                updateDashboard()
            }
        }
    }

    private fun showSnackbar(){
        val view = findViewById<View>(R.id.coordinatorlayout)
        val snackbar = Snackbar.make(view, "Transaction Deleted", Snackbar.LENGTH_LONG)
        snackbar.setAction("Undo"){
            undoDelete()
        }

            .setActionTextColor(ContextCompat.getColor(this, R.color.red))
            .setTextColor(ContextCompat.getColor(this, R.color.white))
            .show()

    }

    private fun deleteTransaction(transaction: Transaction){
        deletedTransaction = transaction
        oldTransactions = transactions

        GlobalScope.launch {
            db.transactionDao().delete(transaction)

            transactions = transactions.filter { it.id != transaction.id }
            runOnUiThread {
                updateDashboard()
                transactionAdapter.setData(transactions)
                showSnackbar()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        fetchAll()
    }

}