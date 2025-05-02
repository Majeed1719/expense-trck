package com.example.expensetracker

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.ImageButton
import android.widget.RelativeLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.addTextChangedListener
import androidx.room.Room
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

@Suppress("DEPRECATION")
class DetailedActivity : AppCompatActivity() {

    private lateinit var labelInput: TextInputEditText
    private lateinit var amountInput: TextInputEditText
    private lateinit var descriptionInput: TextInputEditText
    private lateinit var labelLayout: TextInputLayout
    private lateinit var amountLayout: TextInputLayout
    private lateinit var updateBtn: Button
    private lateinit var rootView: ConstraintLayout
    private lateinit var transaction: Transaction




    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_detailed)

        labelInput = findViewById(R.id.lableInput2)
        amountInput = findViewById(R.id.amountInput2)
        labelLayout = findViewById(R.id.labelLayout2)
        amountLayout = findViewById(R.id.amountLayout2)
        descriptionInput = findViewById(R.id.descriptionInput2)
        updateBtn = findViewById(R.id.updatebtn)
        rootView = findViewById(R.id.rootView)

        transaction = intent.getSerializableExtra("transaction") as Transaction

        labelInput.setText(transaction.label)
        amountInput.setText(transaction.amount.toString())
        descriptionInput.setText(transaction.description)

        rootView.setOnClickListener {
            this.window.decorView.clearFocus()

            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(it.windowToken, 0)
        }


        labelInput.addTextChangedListener {
            updateBtn.visibility =  View.VISIBLE
            if (it!!.count() > 0 )
                labelLayout.error = null
        }

        amountInput.addTextChangedListener {
            updateBtn.visibility =  View.VISIBLE
            if (it!!.count() > 0 )
                amountLayout.error = null
        }

        descriptionInput.addTextChangedListener {
            updateBtn.visibility =  View.VISIBLE
        }

        // Add text change listeners to remove errors

        updateBtn.setOnClickListener {
            val label = labelInput.text.toString()
            val description = descriptionInput.text.toString()
            val amount = amountInput.text.toString().toDoubleOrNull()


            if (label.isEmpty())
                labelLayout.error = "Please enter a valid label"

            else if (amount == null)
                amountLayout.error = "Please enter a valid amount"

            else{
                val transaction = Transaction(transaction.id, label, amount, description)
                update(transaction)
            }


        }

        val closebtn = findViewById<ImageButton>(R.id.closebtn2)
        closebtn.setOnClickListener {
            finish()
        }


    }

    private fun update(transaction: Transaction){
        val db = Room.databaseBuilder(this,
            Appdatabase::class.java,
            "transactions").build()

        GlobalScope.launch {
            db.transactionDao().update(transaction)
            finish()
        }

    }

}
