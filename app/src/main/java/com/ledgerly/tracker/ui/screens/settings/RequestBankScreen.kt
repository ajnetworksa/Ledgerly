package com.ledgerly.tracker.ui.screens.settings

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ledgerly.tracker.ui.theme.Spacing
import java.net.URLEncoder

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RequestBankScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    var bankName by remember { mutableStateOf("") }
    var country by remember { mutableStateOf("") }

    var sender1 by remember { mutableStateOf("") }
    var sms1 by remember { mutableStateOf("") }

    var sender2 by remember { mutableStateOf("") }
    var sms2 by remember { mutableStateOf("") }

    var sender3 by remember { mutableStateOf("") }
    var sms3 by remember { mutableStateOf("") }

    val transactionTypes = remember {
        mutableStateMapOf(
            "Debit/Credit transactions" to false,
            "UPI transactions" to false,
            "ATM withdrawals" to false,
            "Card transactions" to false,
            "NEFT/IMPS transfers" to false,
            "Subscription/Mandate notifications" to false,
            "International transactions" to false
        )
    }

    var additionalInfo by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Request Bank Support") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Surface(
                color = MaterialTheme.colorScheme.errorContainer,
                shape = MaterialTheme.shapes.medium
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Warning,
                        contentDescription = "Warning",
                        tint = MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = "Security Warning: To protect your data, please manually replace real account numbers, balances, and OTPs with 'X' in your SMS examples before submitting. This app operates 100% locally and your request will be generated as a GitHub issue format that you can review before posting.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }

            Text("Bank Information", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            OutlinedTextField(
                value = bankName,
                onValueChange = { bankName = it },
                label = { Text("Bank Name") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = country,
                onValueChange = { country = it },
                label = { Text("Country") },
                modifier = Modifier.fillMaxWidth()
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            Text("SMS Examples", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

            // Example 1
            Text("Example 1", style = MaterialTheme.typography.titleSmall)
            OutlinedTextField(
                value = sender1,
                onValueChange = { sender1 = it },
                label = { Text("Sender (e.g. AD-MYBANK)") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = sms1,
                onValueChange = { sms1 = it },
                label = { Text("Paste SMS text here") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            // Example 2
            Spacer(modifier = Modifier.height(8.dp))
            Text("Example 2", style = MaterialTheme.typography.titleSmall)
            OutlinedTextField(
                value = sender2,
                onValueChange = { sender2 = it },
                label = { Text("Sender") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = sms2,
                onValueChange = { sms2 = it },
                label = { Text("Paste another SMS example here") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            // Example 3
            Spacer(modifier = Modifier.height(8.dp))
            Text("Example 3 (Optional)", style = MaterialTheme.typography.titleSmall)
            OutlinedTextField(
                value = sender3,
                onValueChange = { sender3 = it },
                label = { Text("Sender") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = sms3,
                onValueChange = { sms3 = it },
                label = { Text("Different type of transaction if available") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            Text("Transaction Types Needed", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

            transactionTypes.keys.toList().forEach { type ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Checkbox(
                        checked = transactionTypes[type] ?: false,
                        onCheckedChange = { checked ->
                            transactionTypes[type] = checked
                        }
                    )
                    Text(text = type, style = MaterialTheme.typography.bodyMedium)
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            Text("Additional Information", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            OutlinedTextField(
                value = additionalInfo,
                onValueChange = { additionalInfo = it },
                label = { Text("Any other details") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    val body = buildString {
                        appendLine("### Bank Information")
                        appendLine("- **Bank Name:** $bankName")
                        appendLine("- **Country:** $country")
                        appendLine()
                        appendLine("### SMS Examples")
                        if (sms1.isNotBlank()) {
                            appendLine("**Example 1 (Sender: $sender1)**")
                            appendLine("```\n$sms1\n```")
                            appendLine()
                        }
                        if (sms2.isNotBlank()) {
                            appendLine("**Example 2 (Sender: $sender2)**")
                            appendLine("```\n$sms2\n```")
                            appendLine()
                        }
                        if (sms3.isNotBlank()) {
                            appendLine("**Example 3 (Sender: $sender3)**")
                            appendLine("```\n$sms3\n```")
                            appendLine()
                        }
                        appendLine("### Transaction Types Needed")
                        transactionTypes.forEach { (type, isChecked) ->
                            if (isChecked) appendLine("- [x] $type")
                            else appendLine("- [ ] $type")
                        }
                        appendLine()
                        if (additionalInfo.isNotBlank()) {
                            appendLine("### Additional Information")
                            appendLine(additionalInfo)
                        }
                    }

                    val encodedBody = URLEncoder.encode(body, "UTF-8")
                    val encodedTitle = URLEncoder.encode("Bank Support Request: $bankName", "UTF-8")
                    val url = "https://github.com/Mukto/ledgerly-tracker/issues/new?title=$encodedTitle&body=$encodedBody"
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    context.startActivity(intent)
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = bankName.isNotBlank() && country.isNotBlank() && sms1.isNotBlank()
            ) {
                Text("Generate Request on GitHub")
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
