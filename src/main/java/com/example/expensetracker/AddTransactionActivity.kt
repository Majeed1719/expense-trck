package com.example.expensetracker

import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import androidx.activity.ComponentActivity
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.room.Room
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class AddTransactionActivity : AppCompatActivity() {  // Use AppCompatActivity

    private lateinit var labelInput: TextInputEditText
    private lateinit var amountInput: TextInputEditText
    private lateinit var descriptionInput: TextInputEditText
    private lateinit var labelLayout: TextInputLayout
    private lateinit var amountLayout: TextInputLayout
    private lateinit var addTransactionBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_transaction)  // Must be called first

        // Initialize views AFTER setContentView()
        labelInput = findViewById(R.id.lableInput1)
        amountInput = findViewById(R.id.amountInput)
        labelLayout = findViewById(R.id.labelLayout)
        amountLayout = findViewById(R.id.amountLayout)
        descriptionInput = findViewById(R.id.descriptionInput)
        addTransactionBtn = findViewById(R.id.addtransactionbtn1)

        labelInput.addTextChangedListener {
            if (it!!.count() > 0 )
                labelLayout.error = null
        }

        amountInput.addTextChangedListener {
            if (it!!.count() > 0 )
                amountLayout.error = null
        }

        // Add text change listeners to remove errors

        addTransactionBtn.setOnClickListener {
            val label = labelInput.text.toString()
            val description = descriptionInput.text.toString()
            val amount = amountInput.text.toString().toDoubleOrNull()


            if (label.isEmpty())
                labelLayout.error = "Please enter a valid label"

            else if (amount == null)
                amountLayout.error = "Please enter a valid amount"

            else{
                val transaction = Transaction(0, label, amount, description)
                insert(transaction)
            }


        }

        val closebtn = findViewById<ImageButton>(R.id.closebtn)
        closebtn.setOnClickListener {
            finish()
        }


    }

    private fun insert(transaction: Transaction){
        val db = Room.databaseBuilder(this,
            Appdatabase::class.java,
            "transactions").build()

        GlobalScope.launch {
            db.transactionDao().insertAll(transaction)
            finish()
        }

    }

}