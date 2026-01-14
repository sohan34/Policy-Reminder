package com.example.anandbhavan

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.anandbhavan.adapter.PolicyAdapter
import com.example.anandbhavan.data.PolicyRepository
import com.example.anandbhavan.model.Policy
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch
import java.util.*

class PolicyListActivity : AppCompatActivity() {

    private lateinit var adapter: PolicyAdapter
    private var category: String? = null

    private var activePolicies = listOf<Policy>()
    private var legacyPolicies = listOf<Policy>()
    private var showingActive = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_policy_list)

        category = intent.getStringExtra("CATEGORY")
        title = category ?: "Policies"

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        val tvEmpty = findViewById<TextView>(R.id.tvEmpty)
        val fab = findViewById<FloatingActionButton>(R.id.fabAdd)
        val toggleGroup = findViewById<com.google.android.material.button.MaterialButtonToggleGroup>(R.id.toggleGroup)
        val searchView = findViewById<androidx.appcompat.widget.SearchView>(R.id.searchView)

        // --- SearchView dark mode fix ---
        val searchText = searchView.findViewById<EditText>(
            androidx.appcompat.R.id.search_src_text
        )
        searchText.setTextColor(Color.BLACK)       // typed text
        searchText.setHintTextColor(Color.GRAY)   // hint text

        val searchIcon = searchView.findViewById<ImageView>(
            androidx.appcompat.R.id.search_mag_icon
        )
        searchIcon.setColorFilter(Color.BLACK)

        val closeButton = searchView.findViewById<ImageView>(
            androidx.appcompat.R.id.search_close_btn
        )
        closeButton.setColorFilter(Color.BLACK)
        // --- End SearchView fix ---

        adapter = PolicyAdapter(emptyList()) { policy ->
            val intent = Intent(this, AddEditPolicyActivity::class.java)
            intent.putExtra("CATEGORY", category)
            intent.putExtra("POLICY_ID", policy.id)
            startActivity(intent)
        }

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        fab.setOnClickListener {
            val intent = Intent(this, AddEditPolicyActivity::class.java)
            intent.putExtra("CATEGORY", category)
            startActivity(intent)
        }

        // Toggle listener
        toggleGroup.addOnButtonCheckedListener { _, checkedId, _ ->
            showingActive = checkedId == R.id.btnActive
            displayCurrentList(searchView.query.toString())
        }

        // Search listener
        searchView.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?) = false
            override fun onQueryTextChange(newText: String?): Boolean {
                displayCurrentList(newText.orEmpty())
                return true
            }
        })
    }


    override fun onResume() {
        super.onResume()
        loadPolicies()
    }

    /** Update expired policies to Legacy, then load the lists */
    private fun loadPolicies() {
        val cat = category ?: return
        lifecycleScope.launch {
            val policies = PolicyRepository.getPoliciesByCategory(cat)
            val today = Date()

            // Automatically update expired policies
            policies.forEach { policy ->
                val endDate = policy.endDate
                if (endDate != null && endDate.before(today) && policy.status != "Legacy") {
                    val updatedPolicy = policy.copy(status = "Legacy")
                    PolicyRepository.updatePolicy(updatedPolicy)
                }
            }


            // Reload policies after updates
            val updatedPolicies = PolicyRepository.getPoliciesByCategory(cat)
            activePolicies = updatedPolicies.filter { it.status == "Active" }.sortedBy { it.name }
            legacyPolicies = updatedPolicies.filter { it.status == "Legacy" }.sortedBy { it.name }
            displayCurrentList("")
        }
    }

    private fun displayCurrentList(filter: String) {
        val listToShow = if (showingActive) activePolicies else legacyPolicies
        val filtered = if (filter.isBlank()) listToShow
        else listToShow.filter {
            it.name.contains(filter, true) || it.policyNumber.contains(filter, true)
        }

        adapter.updateList(filtered)
        findViewById<TextView>(R.id.tvEmpty).visibility = if (filtered.isEmpty()) View.VISIBLE else View.GONE
    }
}


