package com.grappim.momentkit

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.grappim.momentkit.formatters.DateTimeFormatters
import com.grappim.momentkit.ui.theme.MomentKitTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MomentKitTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MomentKitScreen(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun MomentKitScreen(modifier: Modifier = Modifier, viewModel: MomentKitViewModel = viewModel()) {
    val currentDateTime by viewModel.currentDateTime.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = stringResource(RString.title),
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 16.dp)
        )

        // Time Section
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = stringResource(RString.section_current_time),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold
                )
                HorizontalDivider()

                TimeRow(
                    label = stringResource(RString.label_time_24h),
                    value = DateTimeFormatters.format24Hour(currentDateTime)
                )

                TimeRow(
                    label = stringResource(RString.label_time_12h),
                    value = DateTimeFormatters.format12Hour(currentDateTime)
                )
            }
        }

        // Date Section
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = stringResource(RString.section_current_date),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold
                )
                HorizontalDivider()

                TimeRow(
                    label = stringResource(RString.label_date_ddmmyyyy),
                    value = DateTimeFormatters.formatDDMMYYYY(currentDateTime)
                )

                TimeRow(
                    label = stringResource(RString.label_date_mmddyyyy),
                    value = DateTimeFormatters.formatMMDDYYYY(currentDateTime)
                )

                TimeRow(
                    label = stringResource(RString.label_date_yyyymmdd),
                    value = DateTimeFormatters.formatYYYYMMDD(currentDateTime)
                )
            }
        }

        // Additional Info Section
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = stringResource(RString.section_additional_info),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold
                )
                HorizontalDivider()

                TimeRow(
                    label = stringResource(RString.label_week_of_year),
                    value = DateTimeFormatters.getWeekOfYear(currentDateTime).toString()
                )

                TimeRow(
                    label = stringResource(RString.label_day_of_year),
                    value = DateTimeFormatters.getDayOfYear(currentDateTime).toString()
                )

                TimeRow(
                    label = stringResource(RString.label_day_of_week),
                    value = DateTimeFormatters.getDayOfWeek(currentDateTime)
                )

                TimeRow(
                    label = stringResource(RString.label_month),
                    value = DateTimeFormatters.getMonth(currentDateTime)
                )

                TimeRow(
                    label = stringResource(RString.label_month_of_year),
                    value = DateTimeFormatters.getMonthOfYear(currentDateTime).toString()
                )
            }
        }
    }
}

@Composable
fun TimeRow(label: String, value: String, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun MomentKitScreenPreview() {
    MomentKitTheme {
        MomentKitScreen()
    }
}
