package jp.techacademy.yuya.tajima.apiapp

import android.content.Context
import androidx.appcompat.app.AlertDialog

fun showConfirmDeleteFavoriteDialog (context: Context, id: String, deleteFunction: (String) -> Unit ) {
    AlertDialog.Builder(context)
        .setTitle(R.string.delete_favorite_dialog_title)
        .setMessage(R.string.delete_favorite_dialog_message)
        .setPositiveButton(android.R.string.ok) { _, _ ->
            deleteFunction(id)
        }
        .setNegativeButton(android.R.string.cancel) { _, _ ->}
        .create()
        .show()
}