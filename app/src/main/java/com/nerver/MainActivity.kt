package com.nerver

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import com.nerver.data.ContactRepository
import com.nerver.data.PreferencesManager
import com.nerver.ui.navigation.NavGraph
import com.nerver.ui.navigation.Screen
import com.nerver.ui.theme.NerverTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private lateinit var contactRepository: ContactRepository
    private lateinit var preferencesManager: PreferencesManager
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        contactRepository = ContactRepository(this)
        preferencesManager = PreferencesManager(this)
        
        setContent {
            val contacts by contactRepository.contacts.collectAsState(initial = emptyList())
            val theme by preferencesManager.theme.collectAsState(initial = com.nerver.data.AppTheme.PRIMARY)
            val navController = rememberNavController()
            val context = LocalContext.current
            
            var imageUri by remember { mutableStateOf<Uri?>(null) }
            
            val pickImageLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.GetContent()
            ) { uri: Uri? ->
                imageUri = uri
            }
            
            NerverTheme(appTheme = theme) {
                NavGraph(
                    navController = navController,
                    contacts = contacts,
                    onContactClick = { contact ->
                        imageUri = null
                        navController.navigate(Screen.ContactForm.createRoute(contact.id))
                    },
                    onAddContact = {
                        imageUri = null
                        navController.navigate(Screen.ContactForm.createRoute("new"))
                    },
                    onEditContact = { contact ->
                        imageUri = null
                        navController.navigate(Screen.ContactForm.createRoute(contact.id))
                    },
                    onSaveContact = { contact ->
                        lifecycleScope.launch {
                            if (contacts.any { it.id == contact.id }) {
                                contactRepository.updateContact(contact)
                            } else {
                                contactRepository.addContact(contact)
                            }
                            imageUri = null
                        }
                    },
                    onDeleteContact = { contactId ->
                        lifecycleScope.launch {
                            contactRepository.deleteContact(contactId)
                        }
                    },
                    onSettingsClick = {
                        navController.navigate(Screen.Settings.route)
                    },
                    onBack = {
                        if (navController.previousBackStackEntry != null) {
                            navController.popBackStack()
                        }
                    },
                    getContactById = { id ->
                        contactRepository.getContactById(id)
                    },
                    onPickImage = {
                        pickImageLauncher.launch("image/*")
                    },
                    imageUri = imageUri
                )
            }
        }
    }
}