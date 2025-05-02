package vcmsa.projects.savorabudgetapp

import android.Manifest
import android.app.Activity
import android.app.DatePickerDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch
import vcmsa.projects.savorabudgetapp.data.Category
import vcmsa.projects.savorabudgetapp.data.Expense
import vcmsa.projects.savorabudgetapp.data.repository.ExpenseRepository
import vcmsa.projects.savorabudgetapp.databinding.FragmentAddBinding
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class AddExpenseFragment : Fragment() {
    private lateinit var binding: FragmentAddBinding
    private val expenseRepository: ExpenseRepository by lazy {
        ExpenseRepository(requireContext())
    }

    // Photo handling variables
    private var selectedImageUri: Uri? = null
    private var currentPhotoPath: String? = null

    companion object {
        private const val REQUEST_IMAGE_CAPTURE = 1
        private const val REQUEST_IMAGE_PICK = 2
        private const val REQUEST_CAMERA_PERMISSION = 101
        private const val REQUEST_STORAGE_PERMISSION = 102
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupCategorySpinner()
        setupDatePicker()
        setupSaveButton()
        setupPhotoButton()
    }
    private fun setupCategorySpinner() {
        val categories = resources.getStringArray(R.array.categories_array).toMutableList()
        categories.add("+ Add New Category") // Add this as the last option

        binding.spCategory.adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            categories
        ).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }

        // Handle spinner selection
        binding.spCategory.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selected = parent?.getItemAtPosition(position).toString()
                if (selected == "+ Add New Category") {
                    showAddCategoryDialog()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun showAddCategoryDialog() {
        val dialogView = LayoutInflater.from(requireContext())
            .inflate(R.layout.dialog_new_category, null)

        val editText = dialogView.findViewById<EditText>(R.id.etNewCategory)

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("New Category")
            .setView(dialogView)
            .setPositiveButton("Add") { _, _ ->
                val input = editText.text.toString()
                if (input.isNotBlank()) {
                    addCustomCategory(input)
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun addCustomCategory(categoryName: String) {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                // Insert into database
                expenseRepository.insertCategory(Category(Categoryname = categoryName))

                // Update spinner
                val adapter = binding.spCategory.adapter as ArrayAdapter<String>
                adapter.remove("+ Add New Category")
                adapter.add(categoryName)
                adapter.add("+ Add New Category")
                binding.spCategory.setSelection(adapter.getPosition(categoryName))

                Toast.makeText(context, "Category added", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }

        }
        if (categoryName.length > 20) {
            Toast.makeText(context, "Category name too long", Toast.LENGTH_SHORT).show()
            return
        }
    }

    private fun setupDatePicker() {
        binding.etDate.apply {
            setOnClickListener { showDatePicker() }
            keyListener = null
            isFocusable = false
            setText(getCurrentDate()) // Set current date by default
        }
    }

    private fun getCurrentDate(): String {
        return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Calendar.getInstance().time)
    }

    private fun setupSaveButton() {
        binding.btnSave.setOnClickListener {
            if (validateInput()) {
                saveExpense()
            }
        }
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        DatePickerDialog(
            requireContext(),
            { _, year, month, day ->
                val selectedDate = formatDate(year, month, day)
                binding.etDate.setText(selectedDate)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun formatDate(year: Int, month: Int, day: Int): String {
        val calendar = Calendar.getInstance().apply {
            set(year, month, day)
        }
        return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.time)
    }

    private fun validateInput(): Boolean {
        return when {
            binding.etAmount.text.isNullOrBlank() -> {
                binding.etAmount.error = "Amount is required"
                false
            }
            binding.etDate.text.isNullOrBlank() -> {
                binding.etDate.error = "Date is required"
                false
            }
            binding.etAmount.text.toString().toDoubleOrNull() == null -> {
                binding.etAmount.error = "Invalid amount"
                false
            }
            else -> true
        }
    }


    // Photo handling methods ============================================
    private fun setupPhotoButton() {
        binding.btnAddPhoto.setOnClickListener { showImageSourceDialog() }
        binding.ivExpensePhoto.setOnClickListener {
            selectedImageUri?.let { showFullScreenImage(it) }
        }
    }

    private fun showImageSourceDialog() {
        val options = arrayOf("Take Photo", "Choose from Gallery", "Remove Photo", "Cancel")
        MaterialAlertDialogBuilder(requireContext())
            .setItems(options) { _, which ->
                when (which) {
                    0 -> takePhoto()
                    1 -> pickFromGallery()
                    2 -> removePhoto()
                }
            }
            .show()
    }

    private fun takePhoto() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                arrayOf(Manifest.permission.CAMERA),
                REQUEST_CAMERA_PERMISSION
            )
            return
        }

        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(requireActivity().packageManager)?.also {
                try {
                    val photoFile = createImageFile()
                    val photoURI = FileProvider.getUriForFile(
                        requireContext(),
                        "${requireContext().packageName}.fileprovider",
                        photoFile
                    ).also {
                        selectedImageUri = it
                    }
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
                } catch (e: IOException) {
                    Toast.makeText(context, "Couldn't create photo file", Toast.LENGTH_SHORT).show()
                }
            } ?: run {
                Toast.makeText(context, "No camera app found", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun pickFromGallery() {
        if (!hasStoragePermission()) {
            requestStoragePermission()
            return
        }
        launchGalleryIntent()
    }

    private fun hasStoragePermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_MEDIA_IMAGES
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun requestStoragePermission() {
        val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arrayOf(Manifest.permission.READ_MEDIA_IMAGES)
        } else {
            arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        }
        requestPermissions(permissions, REQUEST_STORAGE_PERMISSION)
    }

    private fun launchGalleryIntent() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI).apply {
            type = "image/*"
            putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/jpeg", "image/png"))
        }

        try {
            startActivityForResult(intent, REQUEST_IMAGE_PICK)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(context, "No gallery app found", Toast.LENGTH_SHORT).show()
        }
    }

    private fun removePhoto() {
        selectedImageUri = null
        binding.ivExpensePhoto.setImageResource(R.drawable.ic_add_photo_placeholder)
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_",
            ".jpg",
            storageDir
        ).apply {
            currentPhotoPath = absolutePath
        }
    }

    private fun showFullScreenImage(uri: Uri) {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "image/*")
            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        }
        startActivity(intent)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            REQUEST_CAMERA_PERMISSION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    takePhoto()
                } else {
                    Toast.makeText(context, "Camera permission required", Toast.LENGTH_SHORT).show()
                }
            }
            REQUEST_STORAGE_PERMISSION -> {
                if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                    launchGalleryIntent()
                } else {
                    Toast.makeText(context, "Storage permission required", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_IMAGE_CAPTURE -> {
                    selectedImageUri?.let { uri ->
                        binding.ivExpensePhoto.setImageURI(uri)
                    }
                }
                REQUEST_IMAGE_PICK -> {
                    data?.data?.let { uri ->
                        selectedImageUri = uri
                        binding.ivExpensePhoto.setImageURI(uri)
                    }
                }
            }
        }
    }

    private fun saveExpense() {
        val categoryName = binding.spCategory.selectedItem.toString()
        val amount = binding.etAmount.text.toString().toDouble()
        val date = binding.etDate.text.toString()
        val description = binding.etDescription.text.toString()

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val expense = Expense(
                    amount = amount,
                    Categoryname = categoryName,
                    date = date,
                    description = description,
                    photoUri = selectedImageUri?.toString()
                )

                expenseRepository.ensureCategoryExists(categoryName)
                expenseRepository.insertExpense(expense)

                showToast("Expense added successfully")
                findNavController().popBackStack()
            } catch (e: Exception) {
                showToast("Error: ${e.localizedMessage ?: "Unknown error"}")
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}