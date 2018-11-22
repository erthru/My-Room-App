package com.erthru.myroom.utils

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import com.valdesekamdem.library.mdtoast.MDToast

class UIHandler(val context: Context){

    fun showSuccessToast(message:String?){
        MDToast.makeText(context,message,MDToast.LENGTH_SHORT,MDToast.TYPE_SUCCESS).show()
    }

    fun showErrorToast(message:String?){
        MDToast.makeText(context,message,MDToast.LENGTH_SHORT,MDToast.TYPE_ERROR).show()
    }

    fun showWarningToast(message:String?){
        MDToast.makeText(context,message,MDToast.LENGTH_SHORT,MDToast.TYPE_WARNING).show()
    }

    fun showDialog(title:String?, message:String?){

        AlertDialog.Builder(context)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("CLOSE") { dialogInterface, i ->
                dialogInterface.dismiss()
            }
            .show()

    }

}