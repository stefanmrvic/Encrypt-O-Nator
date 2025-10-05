package helium314.keyboard.latin.crypto

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.util.Log

class CryptoHelper(private val context: Context) {

    companion object {
        private const val AUTHORITY = "com.example.cryptomiddleware.provider"
        private const val TAG = "CryptoHelper"
    }

    data class Contact(
        @JvmField val name: String,
        @JvmField val hasSession: Boolean
    )

    data class DecryptResult(
        @JvmField val plaintext: String,
        @JvmField val sender: String
    )

    fun getContacts(): List<Contact> {
        val contacts = mutableListOf<Contact>()
        val uri = Uri.parse("content://$AUTHORITY/contacts")

        try {
            val cursor: Cursor? = context.contentResolver.query(uri, null, null, null, null)
            cursor?.use {
                while (it.moveToNext()) {
                    val name = it.getString(it.getColumnIndexOrThrow("name"))
                    val hasSession = it.getInt(it.getColumnIndexOrThrow("hasSession")) == 1
                    contacts.add(Contact(name, hasSession))
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to get contacts", e)
        }

        return contacts
    }

    fun getCurrentContact(): String? {
        val uri = Uri.parse("content://$AUTHORITY/current_contact")

        try {
            val cursor: Cursor? = context.contentResolver.query(uri, null, null, null, null)
            cursor?.use {
                if (it.moveToFirst()) {
                    return it.getString(it.getColumnIndexOrThrow("name"))
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to get current contact", e)
        }

        return null
    }

    fun setCurrentContact(contactName: String): Boolean {
        val uri = Uri.parse("content://$AUTHORITY/set_contact")

        try {
            val result = context.contentResolver.call(uri, "set_contact", contactName, null)
            return result?.getString("success") == "true"
        } catch (e: Exception) {
            Log.e(TAG, "Failed to set contact", e)
            return false
        }
    }

    fun encrypt(message: String, contactName: String): String? {
        val uri = Uri.parse("content://$AUTHORITY/encrypt")

        try {
            val extras = Bundle().apply {
                putString("contact", contactName)
            }
            val result = context.contentResolver.call(uri, "encrypt", message, extras)

            val error = result?.getString("error")
            if (error != null) {
                Log.e(TAG, "Encryption error: $error")
                return null
            }

            return result?.getString("ciphertext")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to encrypt", e)
            return null
        }
    }

    fun decrypt(ciphertext: String): DecryptResult? {
        val uri = Uri.parse("content://$AUTHORITY/decrypt")

        try {
            val result = context.contentResolver.call(uri, "decrypt", ciphertext, null)

            val error = result?.getString("error")
            if (error != null) {
                Log.e(TAG, "Decryption error: $error")
                return null
            }

            val plaintext = result?.getString("plaintext")
            val sender = result?.getString("sender")

            if (plaintext != null && sender != null) {
                return DecryptResult(plaintext, sender)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to decrypt", e)
        }

        return null
    }

    fun isCryptoMiddlewareInstalled(): Boolean {
        return try {
            context.packageManager.getPackageInfo("com.example.cryptomiddleware", 0)
            true
        } catch (e: Exception) {
            false
        }
    }
}
