package com.nerver.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.nerver.data.AppTheme
import com.nerver.data.PreferencesManager
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    preferencesManager: PreferencesManager,
    contactRepository: com.nerver.data.ContactRepository,
    onBack: () -> Unit,
    onExportImport: () -> Unit
) {
    val theme by preferencesManager.theme.collectAsState(initial = AppTheme.PRIMARY)
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var showTerms by remember { mutableStateOf(false) }
    var showPrivacy by remember { mutableStateOf(false) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Настройки") },
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
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Выбор темы
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Тема приложения",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    AppTheme.values().forEach { appTheme ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = theme == appTheme,
                                onClick = {
                                    scope.launch {
                                        preferencesManager.setTheme(appTheme)
                                    }
                                }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = when (appTheme) {
                                    AppTheme.PRIMARY -> "Основная (темно-синяя)"
                                    AppTheme.DARK -> "Темная"
                                    AppTheme.LIGHT -> "Светлая"
                                }
                            )
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Пользовательское соглашение
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                onClick = { showTerms = true }
            ) {
                Text(
                    text = "Пользовательское Соглашение",
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.titleMedium
                )
            }
            
            // Политика конфиденциальности
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                onClick = { showPrivacy = true }
            ) {
                Text(
                    text = "Политика Конфиденциальности",
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.titleMedium
                )
            }
            
            // Экспорт/Импорт
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                onClick = onExportImport
            ) {
                Text(
                    text = "Экспорт / Импорт данных",
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.titleMedium
                )
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Футер
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "2025 Все права защищены",
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Спасибо, что пользуетесь приложением. Ваша VG.",
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
    
    // Диалоги
    if (showTerms) {
        AlertDialog(
            onDismissRequest = { showTerms = false },
            title = { Text("Пользовательское Соглашение") },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(
                        text = """
                        УНИВЕРСАЛЬНОЕ ПОЛЬЗОВАТЕЛЬСКОЕ СОГЛАШЕНИЕ
                        
                        1. ОБЩИЕ ПОЛОЖЕНИЯ
                        1.1. Настоящее Пользовательское Соглашение (далее — «Соглашение») регулирует отношения между владельцем приложения (далее — «Администрация») и пользователем (далее — «Пользователь») при использовании мобильного приложения.
                        
                        1.2. Используя приложение, Пользователь соглашается с условиями настоящего Соглашения.
                        
                        2. ПРАВА И ОБЯЗАННОСТИ ПОЛЬЗОВАТЕЛЯ
                        2.1. Пользователь имеет право использовать приложение в личных целях.
                        2.2. Пользователь обязуется не использовать приложение для незаконных целей.
                        
                        3. ОТВЕТСТВЕННОСТЬ
                        3.1. Администрация не несет ответственности за решения, принятые Пользователем на основе данных приложения.
                        3.2. Пользователь несет полную ответственность за использование приложения.
                        
                        4. ИЗМЕНЕНИЯ В СОГЛАШЕНИИ
                        4.1. Администрация оставляет за собой право изменять настоящее Соглашение в любое время.
                        
                        Дата последнего обновления: 2025
                        """.trimIndent(),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showTerms = false }) {
                    Text("Закрыть")
                }
            }
        )
    }
    
    if (showPrivacy) {
        AlertDialog(
            onDismissRequest = { showPrivacy = false },
            title = { Text("Политика Конфиденциальности") },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(
                        text = """
                        ПОЛИТИКА КОНФИДЕНЦИАЛЬНОСТИ
                        
                        1. ОБЩИЕ ПОЛОЖЕНИЯ
                        1.1. Настоящая Политика конфиденциальности определяет порядок обработки и защиты персональных данных пользователей приложения.
                        
                        2. СБОР ИНФОРМАЦИИ
                        2.1. Приложение собирает и хранит данные только локально на устройстве Пользователя.
                        2.2. Приложение не передает данные третьим лицам.
                        2.3. Приложение не использует аналитику и трекинг.
                        
                        3. ХРАНЕНИЕ ДАННЫХ
                        3.1. Все данные хранятся исключительно на устройстве Пользователя.
                        3.2. Пользователь может экспортировать или удалить свои данные в любое время.
                        
                        4. ЗАЩИТА ДАННЫХ
                        4.1. Администрация принимает меры для защиты данных Пользователя.
                        4.2. Пользователь несет ответственность за безопасность своего устройства.
                        
                        5. ИЗМЕНЕНИЯ В ПОЛИТИКЕ
                        5.1. Администрация оставляет за собой право изменять настоящую Политику конфиденциальности.
                        
                        Дата последнего обновления: 2025
                        """.trimIndent(),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showPrivacy = false }) {
                    Text("Закрыть")
                }
            }
        )
    }
}

