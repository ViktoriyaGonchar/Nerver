package com.nerver.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.nerver.data.Contact
import com.nerver.ui.screens.ContactFormScreen
import com.nerver.ui.screens.ExportImportScreen
import com.nerver.ui.screens.KanbanScreen
import com.nerver.ui.screens.SettingsScreen

sealed class Screen(val route: String) {
    object Kanban : Screen("kanban")
    object Settings : Screen("settings")
    object ExportImport : Screen("export_import")
    object ContactForm : Screen("contact_form/{contactId}") {
        fun createRoute(contactId: String? = null) = if (contactId != null) "contact_form/$contactId" else "contact_form/new"
    }
}

@Composable
fun NavGraph(
    navController: NavHostController,
    contacts: List<Contact>,
    onContactClick: (Contact) -> Unit,
    onAddContact: () -> Unit,
    onEditContact: (Contact) -> Unit,
    onSaveContact: (Contact) -> Unit,
    onDeleteContact: (String) -> Unit,
    onSettingsClick: () -> Unit,
    onBack: () -> Unit,
    getContactById: (String) -> Contact?,
    onPickImage: (() -> Unit)? = null,
    imageUri: android.net.Uri? = null
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Kanban.route
    ) {
        composable(Screen.Kanban.route) {
            KanbanScreen(
                contacts = contacts,
                onContactClick = { contact ->
                    onContactClick(contact)
                },
                onAddContact = onAddContact,
                onSettingsClick = onSettingsClick
            )
        }
        
        composable(Screen.Settings.route) {
            val context = androidx.compose.ui.platform.LocalContext.current
            SettingsScreen(
                preferencesManager = com.nerver.data.PreferencesManager(context),
                contactRepository = com.nerver.data.ContactRepository(context),
                onBack = onBack,
                onExportImport = {
                    navController.navigate(Screen.ExportImport.route)
                }
            )
        }
        
        composable(Screen.ExportImport.route) {
            val context = androidx.compose.ui.platform.LocalContext.current
            ExportImportScreen(
                contactRepository = com.nerver.data.ContactRepository(context),
                onBack = onBack
            )
        }
        
        composable(
            route = Screen.ContactForm.route,
            arguments = listOf(
                navArgument("contactId") {
                    type = androidx.navigation.NavType.StringType
                    defaultValue = "new"
                }
            )
        ) { backStackEntry ->
            val contactId = backStackEntry.arguments?.getString("contactId") ?: "new"
            val contact = if (contactId != "new") {
                getContactById(contactId)
            } else {
                null
            }
            
            ContactFormScreen(
                contact = contact,
                onSave = { savedContact ->
                    onSaveContact(savedContact)
                    onBack()
                },
                onCancel = onBack,
                onDelete = if (contact != null) {
                    { contactId ->
                        onDeleteContact(contactId)
                        onBack()
                    }
                } else null,
                onPickImage = onPickImage,
                imageUri = imageUri
            )
        }
    }
}

