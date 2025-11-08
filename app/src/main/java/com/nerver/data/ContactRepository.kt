package com.nerver.data

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.File
import java.io.FileReader
import java.io.FileWriter

class ContactRepository(private val context: Context) {
    private val gson = Gson()
    private val contactsFile = File(context.filesDir, "contacts.json")
    
    private val _contacts = MutableStateFlow<List<Contact>>(emptyList())
    val contacts: Flow<List<Contact>> = _contacts.asStateFlow()
    
    init {
        loadContacts()
    }
    
    private fun loadContacts() {
        try {
            if (contactsFile.exists()) {
                FileReader(contactsFile).use { reader ->
                    val type = object : TypeToken<List<Contact>>() {}.type
                    val loaded = gson.fromJson<List<Contact>>(reader, type) ?: emptyList()
                    _contacts.value = loaded
                }
            }
        } catch (e: Exception) {
            _contacts.value = emptyList()
        }
    }
    
    private fun saveContacts() {
        try {
            FileWriter(contactsFile).use { writer ->
                gson.toJson(_contacts.value, writer)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    suspend fun addContact(contact: Contact) {
        val current = _contacts.value.toMutableList()
        current.add(contact)
        _contacts.value = current
        saveContacts()
    }
    
    suspend fun updateContact(contact: Contact) {
        val current = _contacts.value.toMutableList()
        val index = current.indexOfFirst { it.id == contact.id }
        if (index != -1) {
            current[index] = contact
            _contacts.value = current
            saveContacts()
        }
    }
    
    suspend fun deleteContact(contactId: String) {
        val current = _contacts.value.toMutableList()
        current.removeAll { it.id == contactId }
        _contacts.value = current
        saveContacts()
    }
    
    fun getContactById(id: String): Contact? {
        return _contacts.value.find { it.id == id }
    }
    
    fun exportToJson(): String {
        return gson.toJson(_contacts.value)
    }
    
    suspend fun importFromJson(json: String): Boolean {
        return try {
            val type = object : TypeToken<List<Contact>>() {}.type
            val imported = gson.fromJson<List<Contact>>(json, type) ?: emptyList()
            _contacts.value = imported
            saveContacts()
            true
        } catch (e: Exception) {
            false
        }
    }
}

