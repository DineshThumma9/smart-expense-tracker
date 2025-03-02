package com.example.something.screens

import android.util.Log
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.something.db.cloud.PaymentMongoDao
import com.example.something.entity.PaymentMongo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.bson.Document
import java.util.Date

class AnalysisViewModel(
    val modifier: Modifier,
    private val paymentMongoDao: PaymentMongoDao
) : ViewModel() {

    // State for list of payments
    private val _payments = MutableStateFlow<List<PaymentMongo>>(emptyList())
    val payments: StateFlow<List<PaymentMongo>> get() = _payments

    // State for total sum (for totals aggregation functions)
    private val _totalSum = MutableStateFlow(0)
    val totalSum: StateFlow<Int> get() = _totalSum

    // State for total number of payments
    private val _totalCount = MutableStateFlow(0L)
    val totalCount: StateFlow<Long> get() = _totalCount

    // ----------------- Payment List Queries -----------------

    fun getAllPayments() {
        viewModelScope.launch {
            paymentMongoDao.getAllPayments().collect { list ->
                _payments.value = list
            }
        }
    }

    fun getPaymentByName(name: String) {
        viewModelScope.launch {
            paymentMongoDao.getPaymentByName(name).collect { list ->
                _payments.value = list
            }
        }
    }

    fun getPaymentAbove(amount: Int) {
        viewModelScope.launch {
            Log.d("Analysis" , "Calling method")
            paymentMongoDao.getPaymentAbove(amount).collect { list ->
                _payments.value = list
            }
        }
    }

    fun getPaymentReceived() {
        viewModelScope.launch {
            paymentMongoDao.getPaymentReceived().collect { list ->
                _payments.value = list
            }
        }
    }

    fun getPaymentTransferred() {
        viewModelScope.launch {
            paymentMongoDao.getPaymentTransferred().collect { list ->
                _payments.value = list
            }
        }
    }

    fun getPaymentByTags(tags: List<String>) {
        viewModelScope.launch {
            paymentMongoDao.getPaymentByTags(tags).collect { list ->
                _payments.value = list
            }
        }
    }

    fun getCustomPayments(
        name: String? = null,
        dateRange: Pair<Date, Date>? = null,
        amountRange: Pair<Int, Int>? = null,
        tags: List<String>? = null,
        type: String? = null
    ) {
        viewModelScope.launch {
            paymentMongoDao.getCustomPayments(name, dateRange, amountRange, tags, type).collect { list ->
                _payments.value = list
            }
        }
    }

    fun paymentsInDateRange(from: Date, to: Date) {
        viewModelScope.launch {
            paymentMongoDao.paymentsInDateRange(from, to).collect { list ->
                _payments.value = list
            }
        }
    }

    fun paymentsDateAbove(from: Date) {
        viewModelScope.launch {
            paymentMongoDao.paymentsDateAbove(from).collect { list ->
                _payments.value = list
            }
        }
    }

    fun paymentsByAUser(user: String) {
        viewModelScope.launch {
            paymentMongoDao.paymentsByAUser(user).collect { list ->
                _payments.value = list
            }
        }
    }

    fun paymentsByAmount(min: Int, max: Int) {
        viewModelScope.launch {
            Log.d("Analysis" , "Calling method")
            paymentMongoDao.paymentsByAmount(min, max).collect { list ->
                _payments.value = list
            }
        }
    }

    fun paymentsByType(type: String) {
        viewModelScope.launch {
            paymentMongoDao.paymentsByType(type).collect { list ->
                _payments.value = list
            }
        }
    }

    fun paymentsByTag(tags: List<String>) {
        viewModelScope.launch {
            paymentMongoDao.paymentsByTag(tags).collect { list ->
                _payments.value = list
            }
        }
    }

    // ----------------- Totals / Aggregation Queries -----------------

    fun totalSumOfPayments() {
        viewModelScope.launch {
            val sum = paymentMongoDao.totalSumOfPayments()
            _totalSum.value = sum
        }
    }

    fun totalPayments() {
        viewModelScope.launch {
            val count = paymentMongoDao.totalPayments()
            _totalCount.value = count
        }
    }

    fun totalSumOfPaymentsByUser(user: String) {
        viewModelScope.launch {
            val sum = paymentMongoDao.totalSumOfPaymentsByUser(user)
            _totalSum.value = sum
        }
    }

    fun totalSumOfPaymentsInDateRange(from: Date, to: Date) {
        viewModelScope.launch {
            val sum = paymentMongoDao.totalSumOfPaymentsInDateRange(from, to)
            _totalSum.value = sum
        }
    }

    fun totalSumOfPaymentsByTags(tags: List<String>) {
        viewModelScope.launch {
            val sum = paymentMongoDao.totalSumOfPaymentsByTags(tags)
            _totalSum.value = sum
        }
    }

    fun totalSumOfPaymentsByTransactionType(type: String) {
        viewModelScope.launch {
            val sum = paymentMongoDao.totalSumOfPaymentsByTransactionType(type)
            _totalSum.value = sum
        }
    }

    fun totalSumOfPaymentsByCustomQuery(query: Document) {
        viewModelScope.launch {
            val sum = paymentMongoDao.totalSumOfPaymentsByCustomQuery(query)
            _totalSum.value = sum
        }
    }
}
