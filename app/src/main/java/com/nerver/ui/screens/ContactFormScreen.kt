package com.nerver.ui.screens

import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.nerver.data.Contact

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactFormScreen(
    contact: Contact? = null,
    onSave: (Contact) -> Unit,
    onCancel: () -> Unit,
    onDelete: ((String) -> Unit)? = null,
    onPickImage: (() -> Unit)? = null,
    imageUri: Uri? = null
) {
    val isEditing = contact != null
    
    var fio by remember { mutableStateOf(TextFieldValue(contact?.fio ?: "")) }
    var description by remember { mutableStateOf(TextFieldValue(contact?.description ?: "")) }
    var answers by remember {
        mutableStateOf(contact?.answers ?: List(12) { false })
    }
    
    val questionLabels = listOf(
        // Block 1: Resources (1-3)
        "1. Контакт предоставляет ресурсы (время, деньги, связи)?",
        "2. Контакт имеет высокую ценность для ваших целей?",
        "3. Контакт способствует вашему развитию?",
        // Block 2: Reciprocity (4-6)
        "4. Контакт возвращает вложенные ресурсы?",
        "5. Контакт соблюдает взаимность в отношениях?",
        "6. Контакт выполняет обещания?",
        // Block 3: ConditionalSupport (7-9)
        "7. Контакт поддерживает вас в трудных ситуациях?",
        "8. Контакт готов помочь при необходимости?",
        "9. Контакт подтвердил взаимность поддержкой?",
        // Block 4: RedFlags (10-12)
        "10. Контакт использует манипуляции?",
        "11. Контакт ведет себя как паразит?",
        "12. Контакт нарушает ваши границы?"
    )
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isEditing) "Редактировать контакт" else "Новый контакт") },
                navigationIcon = {
                    IconButton(onClick = onCancel) {
                        Text("←")
                    }
                },
                actions = {
                    if (contact != null && onDelete != null) {
                        TextButton(
                            onClick = {
                                onDelete(contact.id)
                                onCancel()
                            }
                        ) {
                            Text("Удалить", color = MaterialTheme.colorScheme.error)
                        }
                    }
                    TextButton(
                        onClick = {
                            val normalizedFio = if (fio.text.length > 50) fio.text.take(50) else fio.text
                            val normalizedDesc = if (description.text.length > 50) {
                                description.text.take(50).padEnd(50, ' ')
                            } else {
                                description.text.padEnd(50, ' ')
                            }
                            
                            val updatedContact = Contact(
                                id = contact?.id ?: System.currentTimeMillis().toString(),
                                photoPath = imageUri?.toString() ?: contact?.photoPath,
                                fio = normalizedFio,
                                description = normalizedDesc,
                                answers = answers
                            )
                            onSave(updatedContact)
                        }
                    ) {
                        Text("Сохранить")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Фото
            Card(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape),
                onClick = { onPickImage?.invoke() }
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    val photoPath = imageUri?.toString() ?: contact?.photoPath
                    if (photoPath != null) {
                        AsyncImage(
                            model = photoPath,
                            contentDescription = "Фото контакта",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Text("+ Фото")
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // ФИО
            OutlinedTextField(
                value = fio,
                onValueChange = { 
                    if (it.text.length <= 50) {
                        fio = it
                    }
                },
                label = { Text("ФИО (до 50 символов)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                supportingText = {
                    Text("${fio.text.length}/50")
                }
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Описание
            OutlinedTextField(
                value = description,
                onValueChange = { 
                    if (it.text.length <= 50) {
                        description = it
                    }
                },
                label = { Text("Описание (до 50 символов)") },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 3,
                supportingText = {
                    Text("${description.text.length}/50")
                }
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Divider()
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Блок 1: Resources
            Text(
                text = "Блок 1: Ресурсы",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            (0..2).forEach { index ->
                QuestionItem(
                    question = questionLabels[index],
                    answer = answers[index],
                    onAnswerChange = { answers = answers.toMutableList().apply { set(index, it) } }
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            Divider()
            Spacer(modifier = Modifier.height(16.dp))
            
            // Блок 2: Reciprocity
            Text(
                text = "Блок 2: Взаимность",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            (3..5).forEach { index ->
                QuestionItem(
                    question = questionLabels[index],
                    answer = answers[index],
                    onAnswerChange = { answers = answers.toMutableList().apply { set(index, it) } }
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            Divider()
            Spacer(modifier = Modifier.height(16.dp))
            
            // Блок 3: ConditionalSupport
            Text(
                text = "Блок 3: Условная поддержка",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            (6..8).forEach { index ->
                QuestionItem(
                    question = questionLabels[index],
                    answer = answers[index],
                    onAnswerChange = { answers = answers.toMutableList().apply { set(index, it) } }
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            Divider()
            Spacer(modifier = Modifier.height(16.dp))
            
            // Блок 4: RedFlags
            Text(
                text = "Блок 4: Красные флаги",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.error
            )
            Spacer(modifier = Modifier.height(8.dp))
            (9..11).forEach { index ->
                QuestionItem(
                    question = questionLabels[index],
                    answer = answers[index],
                    onAnswerChange = { answers = answers.toMutableList().apply { set(index, it) } }
                )
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun QuestionItem(
    question: String,
    answer: Boolean,
    onAnswerChange: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = question,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.width(8.dp))
            Row {
                TextButton(
                    onClick = { onAnswerChange(false) },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = if (!answer) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                    )
                ) {
                    Text("Нет")
                }
                TextButton(
                    onClick = { onAnswerChange(true) },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = if (answer) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                    )
                ) {
                    Text("Да")
                }
            }
        }
    }
}

