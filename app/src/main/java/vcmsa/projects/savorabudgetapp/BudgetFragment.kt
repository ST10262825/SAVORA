package vcmsa.projects.savorabudgetapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import vcmsa.projects.savorabudgetapp.data.repository.GoalRepository
import vcmsa.projects.savorabudgetapp.databinding.FragmentBudgetBinding
import kotlinx.coroutines.launch

class BudgetFragment : Fragment() {
    private lateinit var binding: FragmentBudgetBinding
    private val goalRepository by lazy { GoalRepository(requireContext()) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBudgetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        loadGoals()

        binding.btnSaveGoal.setOnClickListener {
            val min = binding.etMinGoal.text.toString().toDoubleOrNull()
            val max = binding.etMaxGoal.text.toString().toDoubleOrNull()

            if (min == null || max == null || min > max) {
                Toast.makeText(requireContext(), "Invalid goal values", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                goalRepository.saveGoals(min, max)
                Toast.makeText(requireContext(), "Goals saved", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loadGoals() {
        lifecycleScope.launch {
            val goals = goalRepository.getGoals()
            goals?.let {
                binding.etMinGoal.setText(it.min.toString())
                binding.etMaxGoal.setText(it.max.toString())
            }
        }
    }
}
