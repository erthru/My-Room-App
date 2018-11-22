package com.erthru.myroom.ui.user

import android.app.Activity
import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.util.Log
import com.bumptech.glide.Glide
import com.erthru.myroom.R
import com.erthru.myroom.api.ApiConfig
import com.erthru.myroom.api.ApiConfig.Companion.IMG_URL
import com.erthru.myroom.api.model.UpdateUserResponse
import com.erthru.myroom.api.model.UpdateUserWithoutIMGResponse
import com.erthru.myroom.ui.login.LoginActivity
import com.erthru.myroom.utils.FCMInstanceID
import com.erthru.myroom.utils.GoogleLogin
import com.erthru.myroom.utils.SharedPrefUser
import com.erthru.myroom.utils.UIHandler
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.vincent.filepicker.Constant
import com.vincent.filepicker.activity.ImagePickActivity
import com.vincent.filepicker.filter.entity.ImageFile
import kotlinx.android.synthetic.main.activity_user.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import pub.devrel.easypermissions.EasyPermissions
import retrofit2.Call
import retrofit2.Response
import java.io.File

class UserActivity : AppCompatActivity() {

    var imgToUpload:MultipartBody.Part? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user)

        btnBackUser.setOnClickListener { this.finish() }

        requestPermission()

        Glide.with(this).load(IMG_URL+SharedPrefUser(this).getPhoto()).into(photoUser)
        txNameUser.setText(SharedPrefUser(this).getName())

        photoUser.setOnClickListener {
            val i = Intent(this, ImagePickActivity::class.java)
            i.putExtra(Constant.MAX_NUMBER,1)
            startActivityForResult(i, Constant.REQUEST_CODE_PICK_IMAGE)
        }

        btnSaveUser.setOnClickListener {

            if(imgToUpload == null && txNameUser.text.toString().equals(SharedPrefUser(this).getName())){
                this.finish()
            }else if(txNameUser.text.toString().isNullOrBlank()){
                UIHandler(this).showErrorToast("Required field is empty")
            }else{

                if(imgToUpload == null){
                    saveUserWithoutIMG()
                }else{
                    saveUser()
                }

            }

        }


        btnLogoutUser.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Confirmation")
                .setMessage("Logout From "+SharedPrefUser(this).getName())
                .setPositiveButton("LOGOUT") { dialogInterface, i ->
                    logout()
                }
                .setNegativeButton("CANCEL") { dialogInterface, i ->
                    dialogInterface.dismiss()
                }
                .show()
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == Constant.REQUEST_CODE_PICK_IMAGE && resultCode == Activity.RESULT_OK && data != null){

            val pickedImagePath = data?.getParcelableArrayListExtra<ImageFile>(Constant.RESULT_PICK_IMAGE)[0]?.path
            val requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), File(pickedImagePath))
            imgToUpload = MultipartBody.Part.createFormData("photo", File(pickedImagePath)?.name,requestBody)

            Glide.with(this).load(pickedImagePath).into(photoUser)

        }


    }

    private fun requestPermission(){
        if(!EasyPermissions.hasPermissions(this,android.Manifest.permission.READ_EXTERNAL_STORAGE) || !EasyPermissions.hasPermissions(this,android.Manifest.permission.WRITE_EXTERNAL_STORAGE)){

            EasyPermissions.requestPermissions(this,"App need permission to access gallery",991,android.Manifest.permission.READ_EXTERNAL_STORAGE)
            EasyPermissions.requestPermissions(this,"App need permission to access gallery",992,android.Manifest.permission.WRITE_EXTERNAL_STORAGE)

        }
    }

    private fun saveUser(){

        val loading = ProgressDialog(this@UserActivity)
        loading.setMessage("Updating...")
        loading.show()

        val email = RequestBody.create(MediaType.parse("multipart/form-data"), SharedPrefUser(this).getEmail()!!)
        val name = RequestBody.create(MediaType.parse("multipart/form-data"), txNameUser.text.toString())

        ApiConfig().retrofit().updateUser(email,name,imgToUpload)
            .enqueue(object : retrofit2.Callback<UpdateUserResponse>{

                override fun onFailure(call: Call<UpdateUserResponse>, t: Throwable) {
                    Log.d("ONFAILURE",t.toString())
                    loading.dismiss()
                    UIHandler(this@UserActivity).showErrorToast("Failed to update data.")                }

                override fun onResponse(call: Call<UpdateUserResponse>, response: Response<UpdateUserResponse>) {

                    if(response?.isSuccessful){

                        if(response?.body()?.error == false){

                            UIHandler(this@UserActivity).showSuccessToast("Data successfully updated.")
                            loading.dismiss()
                            finish()
                            SharedPrefUser(this@UserActivity).clearUser()

                        }

                    }

                }

            })

    }

    private fun saveUserWithoutIMG(){

        val loading = ProgressDialog(this@UserActivity)
        loading.setMessage("Updating...")
        loading.show()

        ApiConfig().retrofit().updateUserWithoutIMG(SharedPrefUser(this).getEmail(),txNameUser.text.toString())
            .enqueue(object : retrofit2.Callback<UpdateUserWithoutIMGResponse>{
                override fun onFailure(call: Call<UpdateUserWithoutIMGResponse>, t: Throwable) {
                    Log.d("ONFAILURE",t.toString())
                    loading.dismiss()
                    UIHandler(this@UserActivity).showErrorToast("Failed to update data.")
                }

                override fun onResponse(call: Call<UpdateUserWithoutIMGResponse>, response: Response<UpdateUserWithoutIMGResponse>) {

                    if(response?.isSuccessful){

                        if(response?.body()?.error == false){

                            UIHandler(this@UserActivity).showSuccessToast("Data successfully updated.")
                            loading.dismiss()
                            finish()
                            SharedPrefUser(this@UserActivity).clearUser()

                        }

                    }

                }

            })

    }

    private fun logout(){

        class Task : AsyncTask<String,String,Void?>(){

            val loading = ProgressDialog(this@UserActivity)

            override fun onPreExecute() {
                loading.setMessage("Processing data...")
                loading.show()
            }

            override fun doInBackground(vararg p0: String?): Void? {

                FCMInstanceID(this@UserActivity).removeToken(GoogleSignIn.getLastSignedInAccount(this@UserActivity)?.email)

                return null
            }

            override fun onPostExecute(result: Void?) {
                super.onPostExecute(result)

                loading.dismiss()
                GoogleLogin(this@UserActivity).account().signOut()
                SharedPrefUser(this@UserActivity).clearUser()
                finish()
                startActivity(Intent(this@UserActivity,LoginActivity::class.java))

            }

        }

        Task().execute()

    }

}
