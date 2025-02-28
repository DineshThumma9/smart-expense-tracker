package com.example.something.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.twotone.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.something.entity.PaymentMongo
import com.example.something.navigation.BottomNavigationBar
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

// Consistent color scheme
private val primaryGradient = Brush.verticalGradient(
    colors = listOf(Color(0xFF6A11CB), Color(0xFF2575FC))
)

private val cardBackground = Color(0xFFF5F5F5)
private val textPrimary = Color(0xFF333333)
private val textSecondary = Color(0xFF666666)
private val accentColor = Color(0xFF2575FC)
private val selectedFilterBackground = Color(0xFFE3F0FF)

// Consistent spacing
private val spacingSmall = 8.dp
private val spacingMedium = 16.dp
private val spacingLarge = 24.dp

// Consistent shapes
private val roundedCornerShape = RoundedCornerShape(12.dp)
private val smallRoundedCornerShape = RoundedCornerShape(8.dp)

sealed class Filter {
    object Date : Filter()
    object Amount : Filter()
    object Type : Filter()
    object Tags : Filter()
}

enum class DateFilter(val days: Int, val displayName: String) {
    WEEK(7, "Last 7 days"),
    MONTH(30, "Last month"),
    THIRTY_DAYS(30, "Last 30 days"),
    NINETY_DAYS(90, "Last 90 days")
}

enum class AmountFilter(val min: Int, val displayName: String) {
    TWO_HUNDRED(200, "₹200 & above"),
    FIVE_HUNDRED(500, "₹500 & above"),
    THOUSAND(1000, "₹1000 & above"),
    TEN_THOUSAND(10000, "₹10000 & above")
}

enum class TypeFilter(val displayName: String) {
    TRANSFERRED("Transferred"),
    RECEIVED("Received")
}

enum class TagsFilter(val displayName: String) {
    FOOD("Food"),
    TRAVEL("Travel"),
    SNACKS("Snacks"),
    OTHERS("Others")
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalysisScreen(
    navController: NavController,
    viewModel: AnalysisViewModel = viewModel()
) {
    val payments by viewModel.payments.collectAsState(emptyList())
    val coroutineScope = rememberCoroutineScope()

    // Track active bottom sheet
    var activeFilter by remember { mutableStateOf<Filter?>(null) }

    // Filter states
    var selectedDate by remember { mutableStateOf<DateFilter?>(null) }
    var selectedAmount by remember { mutableStateOf<AmountFilter?>(null) }
    var selectedType by remember { mutableStateOf<TypeFilter?>(null) }
    var selectedTags by remember { mutableStateOf<Set<TagsFilter>>(emptySet()) }

    // Track if any filter is applied (used for UI highlights)
    val isDateFilterApplied = selectedDate != null
    val isAmountFilterApplied = selectedAmount != null
    val isTypeFilterApplied = selectedType != null
    val isTagsFilterApplied = selectedTags.isNotEmpty()

    // Apply filters when any selection changes
    LaunchedEffect(selectedDate, selectedAmount, selectedType, selectedTags) {
        val dateRange = selectedDate?.let {
            val calendar = Calendar.getInstance()
            calendar.add(Calendar.DAY_OF_YEAR, -it.days)
            val startDate = calendar.time
            val endDate = Date()
            Pair(startDate, endDate)
        }

        val amountRange = selectedAmount?.let {
            Pair(it.min, Int.MAX_VALUE)
        }

        val tags = selectedTags.takeIf { it.isNotEmpty() }?.map { it.name }

        viewModel.getCustomPayments(
            dateRange = dateRange,
            amountRange = amountRange,
            tags = tags,
            type = selectedType?.name?.lowercase()
        )
    }

    // Handle bottom sheet display
    val showBottomSheet = activeFilter != null

    if (showBottomSheet) {
        when (activeFilter) {
            Filter.Date -> {
                DateBottomSheet(
                    selectedDate = selectedDate,
                    onDateSelected = { selectedDate = it },
                    onApply = {
                        activeFilter = null
                    },
                    onDismiss = { activeFilter = null }
                )
            }
            Filter.Amount -> {
                AmountBottomSheet(
                    selectedAmount = selectedAmount,
                    onAmountSelected = { selectedAmount = it },
                    onApply = {
                        activeFilter = null
                    },
                    onDismiss = { activeFilter = null }
                )
            }
            Filter.Type -> {
                TypeBottomSheet(
                    selectedType = selectedType,
                    onTypeSelected = { selectedType = it },
                    onApply = {
                        activeFilter = null
                    },
                    onDismiss = { activeFilter = null }
                )
            }
            Filter.Tags -> {
                TagsBottomSheet(
                    selectedTags = selectedTags,
                    onTagsSelected = { selectedTags = it },
                    onApply = {
                        activeFilter = null
                    },
                    onDismiss = { activeFilter = null }
                )
            }
            else -> {}
        }
    }

    Scaffold(
        //bottomBar = {
         //   BottomNavigationBar(navController = rememberNavController(), modifier = Modifier)
      //  }
    ) { _ ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
        ) {
            // Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(primaryGradient)
                    .padding(spacingLarge)
            ) {
                Text(
                    text = "Payment Analysis",
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }

            // Filter section
            FilterRow(
                selectedDate = selectedDate,
                selectedAmount = selectedAmount,
                selectedType = selectedType,
                selectedTags = selectedTags,
                isDateFilterApplied = isDateFilterApplied,
                isAmountFilterApplied = isAmountFilterApplied,
                isTypeFilterApplied = isTypeFilterApplied,
                isTagsFilterApplied = isTagsFilterApplied,
                onFilterSelected = { filter ->
                    activeFilter = filter
                }
            )

            // Active filters info
            ActiveFiltersInfo(
                selectedDate = selectedDate,
                selectedAmount = selectedAmount,
                selectedType = selectedType,
                selectedTags = selectedTags,
                onClearAll = {
                    selectedDate = null
                    selectedAmount = null
                    selectedType = null
                    selectedTags = emptySet()
                }
            )

            // Payments list
            if (payments.isEmpty()) {
                EmptyState()
            } else {
                PaymentList(payments = payments)
            }
        }
    }
}

@Composable
private fun ActiveFiltersInfo(
    selectedDate: DateFilter?,
    selectedAmount: AmountFilter?,
    selectedType: TypeFilter?,
    selectedTags: Set<TagsFilter>,
    onClearAll: () -> Unit
) {
    val activeFilterCount = listOfNotNull(
        selectedDate?.let { 1 },
        selectedAmount?.let { 1 },
        selectedType?.let { 1 },
        selectedTags.takeIf { it.isNotEmpty() }?.let { 1 }
    ).sum()

    if (activeFilterCount > 0) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = spacingMedium),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "$activeFilterCount filter${if (activeFilterCount > 1) "s" else ""} applied",
                style = MaterialTheme.typography.bodyMedium,
                color = textSecondary
            )

            TextButton(
                onClick = onClearAll,
                colors = ButtonDefaults.textButtonColors(contentColor = accentColor)
            ) {
                Text("Clear All")
            }
        }
    }
}

@Composable
private fun EmptyState() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(spacingLarge),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Filled.Payments,
                contentDescription = null,
                modifier = Modifier.size(72.dp),
                tint = Color.LightGray
            )
            Spacer(modifier = Modifier.height(spacingMedium))
            Text(
                text = "No payments found",
                style = MaterialTheme.typography.bodyLarge,
                color = textSecondary
            )
            Spacer(modifier = Modifier.height(spacingSmall))
            Text(
                text = "Try adjusting your filters",
                style = MaterialTheme.typography.bodyMedium,
                color = textSecondary
            )
        }
    }
}

@Composable
private fun FilterRow(
    selectedDate: DateFilter?,
    selectedAmount: AmountFilter?,
    selectedType: TypeFilter?,
    selectedTags: Set<TagsFilter>,
    isDateFilterApplied: Boolean,
    isAmountFilterApplied: Boolean,
    isTypeFilterApplied: Boolean,
    isTagsFilterApplied: Boolean,
    onFilterSelected: (Filter) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(spacingMedium)
    ) {
        Text(
            text = "Filter Payments",
            style = MaterialTheme.typography.titleMedium,
            color = textPrimary,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = spacingSmall)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = spacingSmall),
            horizontalArrangement = Arrangement.spacedBy(spacingSmall)
        ) {
            FilterChip(
                label = "Date",
                selection = selectedDate?.displayName,
                isApplied = isDateFilterApplied,
                onClick = { onFilterSelected(Filter.Date) },
                modifier = Modifier.weight(1f)
            )
            FilterChip(
                label = "Amount",
                selection = selectedAmount?.displayName,
                isApplied = isAmountFilterApplied,
                onClick = { onFilterSelected(Filter.Amount) },
                modifier = Modifier.weight(1f)
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = spacingSmall),
            horizontalArrangement = Arrangement.spacedBy(spacingSmall)
        ) {
            FilterChip(
                label = "Type",
                selection = selectedType?.displayName,
                isApplied = isTypeFilterApplied,
                onClick = { onFilterSelected(Filter.Type) },
                modifier = Modifier.weight(1f)
            )
            FilterChip(
                label = "Tags",
                selection = if (selectedTags.size == 1) {
                    selectedTags.first().displayName
                } else if (selectedTags.size > 1) {
                    "${selectedTags.size} selected"
                } else null,
                isApplied = isTagsFilterApplied,
                onClick = { onFilterSelected(Filter.Tags) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun FilterChip(
    label: String,
    selection: String?,
    isApplied: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val displayText = selection?.let { "$label: $it" } ?: label

    Surface(
        shape = smallRoundedCornerShape,
        color = if (isApplied) selectedFilterBackground else Color.LightGray.copy(alpha = 0.3f),
        modifier = modifier
            .clip(smallRoundedCornerShape)
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = spacingMedium, vertical = spacingSmall),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = displayText,
                style = MaterialTheme.typography.bodyMedium,
                color = if (isApplied) accentColor else textSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
            Icon(
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = null,
                tint = if (isApplied) accentColor else textSecondary,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun PaymentList(payments: List<PaymentMongo>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(spacingMedium),
        verticalArrangement = Arrangement.spacedBy(spacingMedium)
    ) {
        items(payments.sortedByDescending { it.date }) { payment ->
            PaymentItem(payment = payment)
        }
    }
}

@Composable
private fun PaymentItem(payment: PaymentMongo) {
    val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    val formattedDate = payment.date?.let { dateFormat.format(it) } ?: "Unknown date"

    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = roundedCornerShape,
        colors = CardDefaults.cardColors(containerColor = cardBackground)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(spacingMedium),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Payment icon
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(accentColor.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Payment,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(spacingMedium))

            // Payment details
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "₹${payment.amount}",
                    style = MaterialTheme.typography.titleMedium,
                    color = textPrimary,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = formattedDate,
                    style = MaterialTheme.typography.bodyMedium,
                    color = textSecondary
                )

                if (payment.tags.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(spacingSmall))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        payment.tags.take(2).forEach { tag ->
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = accentColor.copy(alpha = 0.1f),
                                modifier = Modifier.wrapContentWidth()
                            ) {
                                Text(
                                    text = tag,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = accentColor,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        if (payment.tags.size > 2) {
                            Text(
                                text = "+${payment.tags.size - 2} more",
                                style = MaterialTheme.typography.bodySmall,
                                color = textSecondary
                            )
                        }
                    }
                }
            }
        }
    }
}

// Improved bottom sheet implementations with Apply buttons
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DateBottomSheet(
    selectedDate: DateFilter?,
    onDateSelected: (DateFilter?) -> Unit,
    onApply: () -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    var tempSelectedDate by remember { mutableStateOf(selectedDate) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color.White
    ) {
        Column(modifier = Modifier.padding(spacingMedium)) {
            BottomSheetHeader(
                title = "Select Date Range",
                onClose = {
                    scope.launch {
                        sheetState.hide()
                        onDismiss()
                    }
                }
            )

            Spacer(modifier = Modifier.height(spacingMedium))

            DateFilter.values().forEach { option ->
                BottomSheetOption(
                    text = option.displayName,
                    isSelected = tempSelectedDate == option,
                    onSelect = { tempSelectedDate = option }
                )
            }

            Spacer(modifier = Modifier.height(spacingLarge))

            BottomSheetActions(
                onClear = { tempSelectedDate = null },
                onApply = {
                    onDateSelected(tempSelectedDate)
                    scope.launch {
                        sheetState.hide()
                        onApply()
                    }
                }
            )

            Spacer(modifier = Modifier.height(spacingMedium))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AmountBottomSheet(
    selectedAmount: AmountFilter?,
    onAmountSelected: (AmountFilter?) -> Unit,
    onApply: () -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    var tempSelectedAmount by remember { mutableStateOf(selectedAmount) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color.White
    ) {
        Column(modifier = Modifier.padding(spacingMedium)) {
            BottomSheetHeader(
                title = "Select Minimum Amount",
                onClose = {
                    scope.launch {
                        sheetState.hide()
                        onDismiss()
                    }
                }
            )

            Spacer(modifier = Modifier.height(spacingMedium))

            AmountFilter.values().forEach { option ->
                BottomSheetOption(
                    text = option.displayName,
                    isSelected = tempSelectedAmount == option,
                    onSelect = { tempSelectedAmount = option }
                )
            }

            Spacer(modifier = Modifier.height(spacingLarge))

            BottomSheetActions(
                onClear = { tempSelectedAmount = null },
                onApply = {
                    onAmountSelected(tempSelectedAmount)
                    scope.launch {
                        sheetState.hide()
                        onApply()
                    }
                }
            )

            Spacer(modifier = Modifier.height(spacingMedium))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TypeBottomSheet(
    selectedType: TypeFilter?,
    onTypeSelected: (TypeFilter?) -> Unit,
    onApply: () -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    var tempSelectedType by remember { mutableStateOf(selectedType) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color.White
    ) {
        Column(modifier = Modifier.padding(spacingMedium)) {
            BottomSheetHeader(
                title = "Select Payment Type",
                onClose = {
                    scope.launch {
                        sheetState.hide()
                        onDismiss()
                    }
                }
            )

            Spacer(modifier = Modifier.height(spacingMedium))

            TypeFilter.values().forEach { option ->
                BottomSheetOption(
                    text = option.displayName,
                    isSelected = tempSelectedType == option,
                    onSelect = { tempSelectedType = option }
                )
            }

            Spacer(modifier = Modifier.height(spacingLarge))

            BottomSheetActions(
                onClear = { tempSelectedType = null },
                onApply = {
                    onTypeSelected(tempSelectedType)
                    scope.launch {
                        sheetState.hide()
                        onApply()
                    }
                }
            )

            Spacer(modifier = Modifier.height(spacingMedium))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TagsBottomSheet(
    selectedTags: Set<TagsFilter>,
    onTagsSelected: (Set<TagsFilter>) -> Unit,
    onApply: () -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    var tempSelectedTags by remember { mutableStateOf(selectedTags) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color.White
    ) {
        Column(modifier = Modifier.padding(spacingMedium)) {
            BottomSheetHeader(
                title = "Select Tags",
                onClose = {
                    scope.launch {
                        sheetState.hide()
                        onDismiss()
                    }
                }
            )

            Spacer(modifier = Modifier.height(spacingMedium))

            TagsFilter.values().forEach { tag ->
                BottomSheetCheckOption(
                    text = tag.displayName,
                    isSelected = tempSelectedTags.contains(tag),
                    onToggle = {
                        tempSelectedTags = if (tempSelectedTags.contains(tag)) {
                            tempSelectedTags - tag
                        } else {
                            tempSelectedTags + tag
                        }
                    }
                )
            }

            Spacer(modifier = Modifier.height(spacingLarge))

            BottomSheetActions(
                onClear = { tempSelectedTags = emptySet() },
                onApply = {
                    onTagsSelected(tempSelectedTags)
                    scope.launch {
                        sheetState.hide()
                        onApply()
                    }
                }
            )

            Spacer(modifier = Modifier.height(spacingMedium))
        }
    }
}

@Composable
private fun BottomSheetOption(
    text: String,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onSelect)
            .padding(vertical = spacingSmall),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = isSelected,
            onClick = onSelect,
            colors = RadioButtonDefaults.colors(
                selectedColor = accentColor,
                unselectedColor = textSecondary
            )
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = if (isSelected) textPrimary else textSecondary,
            modifier = Modifier.padding(start = spacingSmall)
        )
    }
}

@Composable
private fun BottomSheetCheckOption(
    text: String,
    isSelected: Boolean,
    onToggle: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onToggle)
            .padding(vertical = spacingSmall),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = isSelected,
            onCheckedChange = { onToggle() },
            colors = CheckboxDefaults.colors(
                checkedColor = accentColor,
                uncheckedColor = textSecondary
            )
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = if (isSelected) textPrimary else textSecondary,
            modifier = Modifier.padding(start = spacingSmall)
        )
    }
}

@Composable
private fun BottomSheetHeader(title: String, onClose: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            color = textPrimary,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1f)
        )
        IconButton(onClick = onClose) {
            Icon(
                imageVector = Icons.TwoTone.Close,
                contentDescription = "Close",
                tint = textSecondary
            )
        }
    }
}

@Composable
private fun BottomSheetActions(
    onClear: () -> Unit,
    onApply: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = spacingMedium),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        OutlinedButton(
            onClick = onClear,
            colors = ButtonDefaults.outlinedButtonColors(contentColor = textSecondary)
        ) {
            Text("Clear")
        }

        Button(
            onClick = onApply,
            colors = ButtonDefaults.buttonColors(containerColor = accentColor)
        ) {
            Text("Apply")
        }
    }
}