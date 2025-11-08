package com.nerver.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.nerver.data.Contact
import com.nerver.ui.theme.CriticalRed
import com.nerver.ui.theme.OnHoldYellow
import com.nerver.ui.theme.SafeGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KanbanScreen(
    contacts: List<Contact>,
    onContactClick: (Contact) -> Unit,
    onAddContact: () -> Unit,
    onSettingsClick: () -> Unit
) {
    val criticalContacts = contacts.filter { it.category == Contact.Category.CRITICAL }
    val onHoldContacts = contacts.filter { it.category == Contact.Category.ON_HOLD }
    val safeContacts = contacts.filter { it.category == Contact.Category.SAFE }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Канбан Контактов") },
                actions = {
                    IconButton(onClick = onSettingsClick) {
                        Text("⚙️")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddContact) {
                Text("+")
            }
        }
    ) { padding ->
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Колонка: Критично / Исключить
            KanbanColumn(
                title = "🔴 Критично / Исключить",
                contacts = criticalContacts,
                color = CriticalRed,
                modifier = Modifier.weight(1f),
                onContactClick = onContactClick
            )
            
            // Колонка: На удержании
            KanbanColumn(
                title = "🟡 На удержании",
                contacts = onHoldContacts,
                color = OnHoldYellow,
                modifier = Modifier.weight(1f),
                onContactClick = onContactClick
            )
            
            // Колонка: Можно общаться
            KanbanColumn(
                title = "🟢 Можно общаться",
                contacts = safeContacts,
                color = SafeGreen,
                modifier = Modifier.weight(1f),
                onContactClick = onContactClick
            )
        }
    }
}

@Composable
fun KanbanColumn(
    title: String,
    contacts: List<Contact>,
    color: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier,
    onContactClick: (Contact) -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxHeight()
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(8.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            modifier = Modifier.padding(bottom = 8.dp),
            color = color
        )
        
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(contacts) { contact ->
                ContactCard(
                    contact = contact,
                    onClick = { onContactClick(contact) }
                )
            }
        }
    }
}

@Composable
fun ContactCard(
    contact: Contact,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(8.dp)
        ) {
            // Фото (если есть)
            contact.photoPath?.let { path ->
                AsyncImage(
                    model = path,
                    contentDescription = contact.fio,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.height(4.dp))
            }
            
            // ФИО
            Text(
                text = contact.normalizeFio(),
                style = MaterialTheme.typography.titleSmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            // Описание
            Text(
                text = contact.normalizeDescription(),
                style = MaterialTheme.typography.bodySmall,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

