package com.example.anandbhavan

import android.app.DatePickerDialog
import android.graphics.Color
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.anandbhavan.data.PolicyRepository
import com.example.anandbhavan.model.Policy
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class AddEditPolicyActivity : AppCompatActivity() {

    private lateinit var etName: TextInputEditText
    private lateinit var etNumber: TextInputEditText
    private lateinit var etStatus: AutoCompleteTextView
    private lateinit var tvStartDate: TextView
    private lateinit var tvEndDate: TextView
    private lateinit var tvTitle: TextView
    private lateinit var btnSave: Button
    private lateinit var btnDelete: Button
    
    private var category: String? = null
    private var policyId: String? = null
    private var startDate: Date? = null
    private var endDate: Date? = null
    
    private val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_edit_policy)
        
        category = intent.getStringExtra("CATEGORY")
        policyId = intent.getStringExtra("POLICY_ID")
        
        initViews()
        setupStatusDropdown()
        setupDatePickers()
        
        if (policyId != null) {
            loadPolicy(policyId!!)
            tvTitle.text = "Edit Policy"
            btnDelete.visibility = android.view.View.VISIBLE
        }
        
        btnSave.setOnClickListener {
            savePolicy()
        }
        
        btnDelete.setOnClickListener {
            deletePolicy()
        }

        etName.setTextColor(Color.BLACK)
        etName.setHintTextColor(Color.GRAY)

        etNumber.setTextColor(Color.BLACK)
        etNumber.setHintTextColor(Color.GRAY)

        etStatus.setTextColor(Color.BLACK)
        etStatus.setHintTextColor(Color.GRAY)

        tvStartDate.setTextColor(Color.BLACK)
        tvEndDate.setTextColor(Color.BLACK)
        tvTitle.setTextColor(Color.BLACK)

        btnSave.setTextColor(Color.BLACK)
        btnDelete.setTextColor(Color.BLACK)


    }
    
    private fun initViews() {
        tvTitle = findViewById(R.id.tvTitle)
        etName = findViewById(R.id.etName)
        etNumber = findViewById(R.id.etNumber)
        etStatus = findViewById(R.id.etStatus)
        tvStartDate = findViewById(R.id.tvStartDate)
        tvEndDate = findViewById(R.id.tvEndDate)
        btnSave = findViewById(R.id.btnSave)
        btnDelete = findViewById(R.id.btnDelete)
    }
    
    private fun setupStatusDropdown() {
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_dropdown_item_1line,
            listOf("Active", "Legacy")
        )
        etStatus.setAdapter(adapter)
    }
    
    private fun setupDatePickers() {
        tvStartDate.setOnClickListener {
            showDatePicker { date ->
                startDate = date
                tvStartDate.text = dateFormat.format(date)
            }
        }
        
        tvEndDate.setOnClickListener {
             showDatePicker { date ->
                endDate = date
                tvEndDate.text = dateFormat.format(date)
            }
        }
    }
    
    private fun showDatePicker(onDateSelected: (Date) -> Unit) {
        val calendar = Calendar.getInstance()
        DatePickerDialog(
            this,
            { _, year, month, day ->
                calendar.set(year, month, day)
                onDateSelected(calendar.time)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }
    
    private fun savePolicy() {
        val name = etName.text.toString()
        val number = etNumber.text.toString()
        val status = etStatus.text.toString()
        
        if (name.isBlank() || number.isBlank() || startDate == null || endDate == null) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }
        
        btnSave.isEnabled = false
        
        val policy = Policy(
            id = policyId ?: "",
            name = name,
            policyNumber = number,
            category = category ?: "Other",
            startDate = startDate,
            endDate = endDate,
            status = status
        )
        
        lifecycleScope.launch {
            try {
                if (policyId == null) {
                    PolicyRepository.addPolicy(policy)
                } else {
                    PolicyRepository.updatePolicy(policy)
                }
                Toast.makeText(this@AddEditPolicyActivity, "Policy Saved", Toast.LENGTH_SHORT).show()
                finish()
            } catch (e: Exception) {
                Toast.makeText(this@AddEditPolicyActivity, "Error saving policy", Toast.LENGTH_SHORT).show()
                btnSave.isEnabled = true
            }
        }
    }
    
    private fun deletePolicy() {
        if (policyId == null) return
        
        lifecycleScope.launch {
            try {
                PolicyRepository.deletePolicy(policyId!!)
                Toast.makeText(this@AddEditPolicyActivity, "Policy Deleted", Toast.LENGTH_SHORT).show()
                finish()
            } catch (e: Exception) {
                Toast.makeText(this@AddEditPolicyActivity, "Error deleting policy", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    // Simple load logic (ideally should be in ViewModel or Repository with StateFlow)
    private fun loadPolicy(id: String) {
        lifecycleScope.launch {
            // We need a getPolicyById method in Repository or just filter from all
            // For simplicity, we'll fetch all and filter here or add method to Repo. 
            // Let's assume we add getPolicyById to Repo or just iterate.
            // Since I didn't add getById in Repo, I'll add it now or use a quick workaround.
            // Workaround: Since I can't edit Repo in the same turn efficiently without risk, I'll allow Repo update in next step if needed.
            // Actually, I can just use filtering for now as lists are small.
            val policies = PolicyRepository.getAllPolicies()
            val policy = policies.find { it.id == id }
            
            policy?.let {
                etName.setText(it.name)
                etNumber.setText(it.policyNumber)
                etStatus.setText(it.status, false) // false to not filter adapter
                
                startDate = it.startDate
                endDate = it.endDate
                
                startDate?.let { d -> tvStartDate.text = dateFormat.format(d) }
                endDate?.let { d -> tvEndDate.text = dateFormat.format(d) }
            }
        }
    }
}
