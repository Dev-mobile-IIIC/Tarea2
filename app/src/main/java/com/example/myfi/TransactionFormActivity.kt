package com.example.myfi

import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AdapterView
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import androidx.appcompat.app.AppCompatActivity
import com.example.myfi.databinding.ActivityTransactionFormBinding
import com.example.myfi.domain.model.CategoryType
import com.example.myfi.domain.model.Transaction
import com.example.myfi.viewmodel.CategoryViewModel
import com.example.myfi.viewmodel.RepositoryViewModelFactory
import com.example.myfi.viewmodel.TransactionViewModel
import java.time.LocalDate
import java.time.ZoneId
import java.util.Date

class TransactionFormActivity : AppCompatActivity() {
    companion object { const val EXTRA_TRANSACTION_ID = "transaction_id" }
    private lateinit var binding: ActivityTransactionFormBinding
    private val repository by lazy { (application as MyFiApplication).repository }
    private val categoryViewModel by lazy { ViewModelProvider(this, RepositoryViewModelFactory(repository))[CategoryViewModel::class.java] }
    private val transactionViewModel by lazy { ViewModelProvider(this, RepositoryViewModelFactory(repository))[TransactionViewModel::class.java] }
    private var transactionId: String? = null
    private var type = CategoryType.EXPENSE
    private var selectedCategoryId: String? = null
    private var selectedDate = LocalDate.now()
    private var receiptUri: String? = null
    private var latitude: Double? = null
    private var longitude: Double? = null
    private var locationName: String? = null
    private var locationAddress: String? = null
    private val imagePicker = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri -> uri?.let(::setReceipt) }
    private val cameraCapture = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) pendingCameraUri?.let(::setReceipt)
    }
    private var pendingCameraUri: Uri? = null
    private val locationPicker = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            val data = result.data ?: return@registerForActivityResult
            latitude = data.getDoubleExtra(LocationPickerActivity.RESULT_LATITUDE, 0.0)
            longitude = data.getDoubleExtra(LocationPickerActivity.RESULT_LONGITUDE, 0.0)
            locationName = data.getStringExtra(LocationPickerActivity.RESULT_NAME)
            locationAddress = data.getStringExtra(LocationPickerActivity.RESULT_ADDRESS)
            binding.tvLocationSummary.text = locationName ?: locationAddress ?: getString(R.string.location_selected)
            binding.btnRemoveLocation.visibility = android.view.View.VISIBLE
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTransactionFormBinding.inflate(layoutInflater)
        setContentView(binding.root)
        transactionId = intent.getStringExtra(EXTRA_TRANSACTION_ID)
        transactionId?.let { load(transactionViewModel.getTransactionById(it)) }
        binding.btnExpenseType.setOnClickListener { type = CategoryType.EXPENSE; selectedCategoryId = null; updateCategories() }
        binding.btnIncomeType.setOnClickListener { type = CategoryType.INCOME; selectedCategoryId = null; updateCategories() }
        binding.btnSelectDate.setOnClickListener { showDatePicker() }
        binding.btnAddCategory.setOnClickListener { startActivity(Intent(this, CategoriesActivity::class.java)) }
        binding.btnAddReceipt.setOnClickListener { showReceiptChoices() }
        binding.btnRemoveReceipt.setOnClickListener { receiptUri = null; updateReceipt() }
        binding.btnAddLocation.setOnClickListener { locationPicker.launch(Intent(this, LocationPickerActivity::class.java)) }
        binding.btnRemoveLocation.setOnClickListener { clearLocation() }
        binding.btnSaveTransaction.setOnClickListener { save() }
        binding.btnCancelTransaction.setOnClickListener { finish() }
        updateDate()
        updateCategories()
        updateReceipt()
        binding.tvLocationSummary.text = locationName ?: locationAddress ?: getString(R.string.no_location)
        binding.btnRemoveLocation.visibility = if (latitude == null || longitude == null) android.view.View.GONE else android.view.View.VISIBLE
    }

    private fun load(transaction: Transaction?) {
        if (transaction == null) { finish(); return }
        type = transactionViewModel.getCategoryType(transaction.categoryId) ?: CategoryType.EXPENSE
        selectedCategoryId = transaction.categoryId
        binding.etAmount.setText(transaction.amount.toString())
        binding.etNote.setText(transaction.note.orEmpty())
        selectedDate = transaction.date
        receiptUri = transaction.receiptUri
        latitude = transaction.latitude
        longitude = transaction.longitude
        locationName = transaction.locationName
        locationAddress = transaction.locationAddress
    }

    private fun updateCategories() {
        val categories = categoryViewModel.getCategories(type)
        binding.actvCategory.setAdapter(ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, categories.map { it.name }))
        val selected = categories.firstOrNull { it.id == selectedCategoryId } ?: categories.firstOrNull()
        selectedCategoryId = selected?.id
        binding.actvCategory.setText(selected?.name.orEmpty(), false)
        binding.actvCategory.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ -> selectedCategoryId = categories.getOrNull(position)?.id }
        binding.btnExpenseType.isSelected = type == CategoryType.EXPENSE
        binding.btnIncomeType.isSelected = type == CategoryType.INCOME
    }

    private fun showDatePicker() {
        val calendar = java.util.Calendar.getInstance().apply {
            time = Date.from(selectedDate.atStartOfDay(ZoneId.systemDefault()).toInstant())
        }
        DatePickerDialog(this, { _, year, month, day ->
            selectedDate = LocalDate.of(year, month + 1, day)
            updateDate()
        }, calendar.get(java.util.Calendar.YEAR), calendar.get(java.util.Calendar.MONTH), calendar.get(java.util.Calendar.DAY_OF_MONTH)).show()
    }

    private fun updateDate() { binding.tvSelectedDate.text = selectedDate.toString() }

    private fun showReceiptChoices() {
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle(R.string.choose_image_source)
            .setItems(arrayOf(getString(R.string.take_photo), getString(R.string.choose_image))) { _, which ->
                if (which == 0) takePhoto()
                else imagePicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            }.show()
    }

    private fun takePhoto() {
        val file = java.io.File(cacheDir, "receipt_${System.currentTimeMillis()}.jpg")
        val uri = androidx.core.content.FileProvider.getUriForFile(this, "${packageName}.fileprovider", file)
        pendingCameraUri = uri
        cameraCapture.launch(uri)
    }

    private fun setReceipt(uri: Uri) {
        receiptUri = uri.toString()
        updateReceipt()
    }

    private fun updateReceipt() {
        binding.tvReceiptStatus.setText(if (receiptUri == null) R.string.no_receipt else R.string.receipt_added)
        binding.btnRemoveReceipt.visibility = if (receiptUri == null) android.view.View.GONE else android.view.View.VISIBLE
    }

    private fun clearLocation() {
        latitude = null
        longitude = null
        locationName = null
        locationAddress = null
        binding.tvLocationSummary.setText(R.string.no_location)
        binding.btnRemoveLocation.visibility = android.view.View.GONE
    }

    private fun save() {
        val amount = binding.etAmount.text?.toString()?.trim()?.replace(',', '.')?.toDoubleOrNull()
        if (amount == null || amount <= 0.0) {
            binding.tilAmount.error = getString(R.string.invalid_amount)
            return
        }
        binding.tilAmount.error = null
        val category = categoryViewModel.getCategories(type).firstOrNull { it.id == selectedCategoryId && it.type == type }
        if (category == null) {
            Toast.makeText(this, R.string.no_category_for_type, Toast.LENGTH_SHORT).show()
            return
        }
        val transaction = Transaction(
            id = transactionId ?: com.example.myfi.util.IdGenerator.newId(),
            amount = amount,
            categoryId = category.id,
            date = selectedDate,
            note = binding.etNote.text?.toString(),
            receiptUri = receiptUri,
            latitude = latitude,
            longitude = longitude,
            locationName = locationName,
            locationAddress = locationAddress
        )
        if (transactionId == null) transactionViewModel.addTransaction(transaction) else transactionViewModel.updateTransaction(transaction)
        finish()
    }
}
