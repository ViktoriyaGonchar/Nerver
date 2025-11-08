package com.nerver.ui.screens

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import com.nerver.data.ContactRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileWriter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExportImportScreen(
    contactRepository: ContactRepository,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    var showImportDialog by remember { mutableStateOf(false) }
    var showExportDialog by remember { mutableStateOf(false) }
    var exportText by remember { mutableStateOf("") }
    var importText by remember { mutableStateOf("") }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Экспорт / Импорт") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Text("←")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Экспорт
            Button(
                onClick = {
                    val json = contactRepository.exportToJson()
                    exportText = json
                    saveToFile(context, json, "contacts_export.json")
                    showExportDialog = true
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Экспортировать в JSON")
            }
            
            // Импорт
            Button(
                onClick = { showImportDialog = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Импортировать из JSON")
            }
        }
    }
    
    if (showImportDialog) {
        AlertDialog(
            onDismissRequest = { showImportDialog = false },
            title = { Text("Импорт данных") },
            text = {
                Column {
                    Text("Вставьте JSON данные:")
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = importText,
                        onValueChange = { importText = it },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 10
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        CoroutineScope(Dispatchers.Main).launch {
                            val success = contactRepository.importFromJson(importText)
                            if (success) {
                                Toast.makeText(context, "Данные импортированы", Toast.LENGTH_SHORT).show()
                                showImportDialog = false
                                importText = ""
                            } else {
                                Toast.makeText(context, "Ошибка импорта", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                ) {
                    Text("Импортировать")
                }
            },
            dismissButton = {
                TextButton(onClick = { showImportDialog = false }) {
                    Text("Отмена")
                }
            }
        )
    }
    
    if (showExportDialog) {
        AlertDialog(
            onDismissRequest = { showExportDialog = false },
            title = { Text("Экспорт данных") },
            text = {
                Column {
                    Text("JSON данные (скопированы в буфер обмена):")
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = exportText,
                        onValueChange = {},
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 10,
                        readOnly = true
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        clipboardManager.setText(AnnotatedString(exportText))
                        Toast.makeText(context, "Скопировано в буфер обмена", Toast.LENGTH_SHORT).show()
                        showExportDialog = false
                    }
                ) {
                    Text("Скопировать")
                }
            },
            dismissButton = {
                TextButton(onClick = { showExportDialog = false }) {
                    Text("Закрыть")
                }
            }
        )
    }
}

private fun saveToFile(context: Context, content: String, fileName: String) {
    try {
        val file = File(context.getExternalFilesDir(null), fileName)
        FileWriter(file).use { writer ->
            writer.write(content)
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

