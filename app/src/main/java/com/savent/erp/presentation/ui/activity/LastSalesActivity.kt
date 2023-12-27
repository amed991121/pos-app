package com.savent.erp.presentation.ui.activity

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.mazenrashed.printooth.Printooth
import com.mazenrashed.printooth.data.printable.Printable
import com.mazenrashed.printooth.ui.ScanningActivity
import com.mazenrashed.printooth.utilities.Printing
import com.mazenrashed.printooth.utilities.PrintingCallback
import com.savent.erp.AppConstants
import com.savent.erp.R
import com.savent.erp.databinding.ActivityLastSalesBinding
import com.savent.erp.presentation.ui.CustomSnackBar
import com.savent.erp.presentation.ui.dialog.SendReceiptDialog
import com.savent.erp.presentation.ui.adapters.SalesAdapter
import com.savent.erp.presentation.ui.model.SharedReceipt
import com.savent.erp.presentation.viewmodel.SalesViewModel
import com.savent.erp.utils.CheckPermissions
import com.savent.erp.utils.DecimalFormat
import com.savent.erp.utils.GetUriFromFile
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File

class LastSalesActivity : AppCompatActivity(), SalesAdapter.OnClickListener,
    SendReceiptDialog.OnClickListener {
    private lateinit var binding: ActivityLastSalesBinding
    private val salesViewModel: SalesViewModel by viewModel()
    private lateinit var salesAdapter: SalesAdapter
    private var sendDialog: SendReceiptDialog? = null
    private var sharedReceiptMethod: SharedReceiptMethod? = null
    private var sharedReceipt: SharedReceipt? = null
    private var printing: Printing? = null
    private var receiptToPrint: ArrayList<Printable>? = null
    private var saleIdToShare: Int? = null

    enum class SharedReceiptMethod {
        Whatsapp, Sms, Email, BluetoothPrinter
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init()
        subscribeToObservables()
        setupRecyclerView()
    }

    private fun init() {
        binding = ActivityLastSalesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.lifecycleOwner = this
        binding.viewModel = salesViewModel
        initEvents()
    }

    private fun setupRecyclerView() {
        salesAdapter = SalesAdapter(this)
        salesAdapter.setOnClickListener(this)
        val recyclerView = binding.salesRecycler
        recyclerView.layoutManager = LinearLayoutManager(this)
        salesAdapter.setHasStableIds(true)
        recyclerView.adapter = salesAdapter
        recyclerView.itemAnimator = DefaultItemAnimator()
    }

    private fun initEvents() {
        binding.searchEdit.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                s?.let { salesViewModel.loadSales(it.toString()) }
            }

        })
        binding.syncSales.setOnClickListener {
            salesViewModel.syncDataFromLocalStore()
        }

        binding.refreshLayout.setOnRefreshListener {
            salesViewModel.syncDataFromLocalStore()
        }

    }

    private fun subscribeToObservables() {
        salesViewModel.sales.observe(this) {
            salesAdapter.setData(it)
        }
        salesViewModel.cashBalance.observe(this) {

            binding.revenue.text = getString(R.string.price)
                .format(DecimalFormat.format(it.revenues))

            binding.debts.text = getString(R.string.price)
                .format(DecimalFormat.format(it.debts))
        }

        salesViewModel.salesBalance.observe(this) {
            binding.sendingPending.text = it.pendingToSend.toString()
            binding.totalSales.text = it.total.toString()
            if (it.pendingToSend > 0) binding.syncSales.visibility = View.VISIBLE
            else binding.syncSales.visibility = View.GONE

        }

        salesViewModel.loading.observe(this) {
            binding.refreshLayout.isRefreshing = it
        }

        salesViewModel.printable.observe(this) {
            receiptToPrint = it
            printReceiptByBluetooth()
        }

        lifecycleScope.launchWhenCreated {
            salesViewModel.uiEvent.flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
                .collectLatest { uiEvent ->
                    when (uiEvent) {
                        is SalesViewModel.UiEvent.ShowMessage -> {
                            CustomSnackBar.make(
                                binding.root,
                                getString(uiEvent.resId ?: R.string.unknown_error),
                                CustomSnackBar.LENGTH_LONG
                            ).show()
                        }
                        is SalesViewModel.UiEvent.ShareReceipt -> {
                            sharedReceipt = uiEvent.receipt
                            salesViewModel.saveReceiptFile(
                                File(
                                    filesDir,
                                    getString(R.string.file_receipt_name) +
                                            "-${System.currentTimeMillis()}.txt"
                                ), uiEvent.receipt.note
                            )
                        }
                        is SalesViewModel.UiEvent.SavedReceipt -> {
                            when (sharedReceiptMethod) {
                                SharedReceiptMethod.Whatsapp -> {
                                    sendReceiptByWhatsApp(
                                        sharedReceipt?.contact?.phoneNumber,
                                        GetUriFromFile(this@LastSalesActivity, uiEvent.file)
                                    )
                                }
                                SharedReceiptMethod.Sms -> {
                                    sendReceiptBySms(
                                        sharedReceipt?.contact?.phoneNumber,
                                        sharedReceipt?.note ?: ""
                                    )
                                }
                                SharedReceiptMethod.Email -> {
                                    sendReceiptByEmail(
                                        sharedReceipt?.contact?.email,
                                        GetUriFromFile(this@LastSalesActivity, uiEvent.file)
                                    )
                                }
                                else -> {
                                }
                            }
                        }
                    }
                }
        }
    }

    fun back(view: View) {
        finish()
    }

    override fun onSendReceipt(id: Int) {
        saleIdToShare = id
        showDialog()
    }

    private fun sendReceiptByEmail(email: String?, noteUri: Uri) {
        try {
            Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:")
                putExtra(Intent.EXTRA_EMAIL, Array(1) { email })
                putExtra(Intent.EXTRA_SUBJECT, getString(R.string.receipt_subject))
                packageManager.queryIntentActivities(this, PackageManager.MATCH_ALL).forEach {
                    grantUriPermission(
                        it.activityInfo.packageName,
                        noteUri,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION
                    )
                }
                putExtra(Intent.EXTRA_STREAM, noteUri)
                flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                startActivity(Intent.createChooser(this, getString(R.string.share_receipt_header)))
            }
        } catch (ex: ActivityNotFoundException) {
            CustomSnackBar.make(
                binding.root,
                getString(R.string.mail_app_not_found),
                CustomSnackBar.LENGTH_LONG
            ).show()
        }

    }

    private fun sendReceiptBySms(phoneNumber: Long?, note: String) {
        try {
            Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse("smsto:+${phoneNumber}")
                putExtra("sms_body", note)
                startActivity(Intent.createChooser(this, getString(R.string.share_receipt_header)))
            }
        } catch (e: ActivityNotFoundException) {
            e.printStackTrace()
            CustomSnackBar.make(
                binding.root,
                getString(R.string.sms_app_not_found),
                CustomSnackBar.LENGTH_LONG
            ).show()
        }

    }

    private fun sendReceiptByWhatsApp(phoneNumber: Long?, noteUri: Uri) {
        try {
            Intent(Intent.ACTION_SEND).apply {
                type = "application/pdf"
                packageManager.queryIntentActivities(this, PackageManager.MATCH_ALL).forEach {
                    grantUriPermission(
                        it.activityInfo.packageName,
                        noteUri,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION
                    )
                }
                putExtra("jid", "$phoneNumber@s.whatsapp.net")
                putExtra(Intent.EXTRA_STREAM, noteUri)
                flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                setPackage("com.whatsapp")
                startActivity(this)
            }


        } catch (e: ActivityNotFoundException) {
            CustomSnackBar.make(
                binding.root,
                getString(R.string.whatsapp_not_found),
                CustomSnackBar.LENGTH_LONG
            ).show()
        }

    }

    private fun printReceiptByBluetooth() {
        if (sharedReceiptMethod == SharedReceiptMethod.BluetoothPrinter && receiptToPrint != null) {
            printing?.print(receiptToPrint!!)
        }
    }

    override fun sendByWhatsapp() {
        sharedReceiptMethod = SharedReceiptMethod.Whatsapp
        sendDialog?.dismiss()
        salesViewModel.loadReceiptToSend(saleIdToShare ?: 0)
    }

    override fun sendBySms() {
        sharedReceiptMethod = SharedReceiptMethod.Sms
        sendDialog?.dismiss()
        salesViewModel.loadReceiptToSend(saleIdToShare ?: 0)
    }

    override fun sendByEmail() {
        sharedReceiptMethod = SharedReceiptMethod.Email
        sendDialog?.dismiss()
        salesViewModel.loadReceiptToSend(saleIdToShare ?: 0)
    }

    override fun sendPrintable() {
        if (isNeededRequestAndroid12BLEPermission()) return
        sharedReceiptMethod = SharedReceiptMethod.BluetoothPrinter
        sendDialog?.dismiss()
        salesViewModel.loadReceiptToPrint(saleIdToShare ?: 0)
        resultLauncher.launch(
            Intent(
                this,
                ScanningActivity::class.java
            )
        )
    }

    fun showDialog() {
        sendDialog = SendReceiptDialog(this)
        sendDialog?.setOnClickListener(this)
        sendDialog?.show()
    }

    private fun initPrinter() {
        printing = Printooth.printer()
        printing?.printingCallback = object : PrintingCallback {
            override fun connectingWithPrinter() {
                Toast.makeText(
                    this@LastSalesActivity,
                    "Connecting with printer",
                    Toast.LENGTH_SHORT
                ).show()
            }

            override fun printingOrderSentSuccessfully() {
                Toast.makeText(this@LastSalesActivity, "Order sent to printer", Toast.LENGTH_SHORT)
                    .show()
            }

            override fun connectionFailed(error: String) {
                Toast.makeText(
                    this@LastSalesActivity,
                    "Failed to connect printer",
                    Toast.LENGTH_SHORT
                ).show()
            }

            override fun onError(error: String) {
                Toast.makeText(this@LastSalesActivity, error, Toast.LENGTH_SHORT).show()
            }

            override fun onMessage(message: String) {
                Toast.makeText(this@LastSalesActivity, "Message: $message", Toast.LENGTH_SHORT)
                    .show()
            }

            override fun disconnected() {
                Toast.makeText(this@LastSalesActivity, "Disconnected Printer", Toast.LENGTH_SHORT)
                    .show()
            }

        }
    }

    private var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == ScanningActivity.SCANNING_FOR_PRINTER || result.resultCode == Activity.RESULT_OK) {
                initPrinter()
                printReceiptByBluetooth()
            }
        }


    private fun isNeededRequestAndroid12BLEPermission(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!CheckPermissions.check(this, AppConstants.ANDROID_12_BLE_PERMISSIONS)) {
                requestPermissions(
                    AppConstants.ANDROID_12_BLE_PERMISSIONS,
                    AppConstants.REQUEST_12_BLE_CODE
                )
                return true
            }

        }
        return false

    }


}