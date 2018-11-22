package com.erthru.myroom.ui.newuser

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.erthru.myroom.R
import com.erthru.myroom.api.ApiConfig
import com.erthru.myroom.api.model.NewUserResponse
import com.erthru.myroom.ui.login.LoginActivity
import com.erthru.myroom.ui.main.MainActivity
import com.erthru.myroom.utils.GetFileFromBitmap
import com.erthru.myroom.utils.GoogleLogin
import com.erthru.myroom.utils.UIHandler
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.vincent.filepicker.Constant.*
import com.vincent.filepicker.activity.ImagePickActivity
import com.vincent.filepicker.filter.entity.ImageFile
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_new_user.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.apache.commons.io.FileUtils
import pub.devrel.easypermissions.EasyPermissions
import retrofit2.Call
import retrofit2.Response
import java.io.File
import java.lang.Exception
import java.net.URL

class NewUserActivity : AppCompatActivity() {

    var imageToUpload:MultipartBody.Part? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_user)

        loadDefault()
        imageFileDefault()
        requestPermission()

        btnInfoNU.setOnClickListener {
            UIHandler(this).showDialog("Information","Tap save button to complete the registration.")
        }

        photoNU.setOnClickListener {

            val i = Intent(this,ImagePickActivity::class.java)
            i.putExtra(MAX_NUMBER,1)
            startActivityForResult(i, REQUEST_CODE_PICK_IMAGE)

        }

        btnSaveNU.setOnClickListener {
            btnSaveNU.visibility = View.GONE
            addUser()
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == REQUEST_CODE_PICK_IMAGE && resultCode == Activity.RESULT_OK && data != null){

            val pickedImagePath = data.getParcelableArrayListExtra<ImageFile>(RESULT_PICK_IMAGE)[0]?.path
            val requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), File(pickedImagePath))
            imageToUpload = MultipartBody.Part.createFormData("photo",File(pickedImagePath)?.name,requestBody)
            Glide.with(this).load(pickedImagePath).into(photoNU)

        }

    }

    private fun requestPermission(){
        if(!EasyPermissions.hasPermissions(this,android.Manifest.permission.READ_EXTERNAL_STORAGE) || !EasyPermissions.hasPermissions(this,android.Manifest.permission.WRITE_EXTERNAL_STORAGE)){

            EasyPermissions.requestPermissions(this,"App need permission to access gallery",991,android.Manifest.permission.READ_EXTERNAL_STORAGE)
            EasyPermissions.requestPermissions(this,"App need permission to access gallery",992,android.Manifest.permission.WRITE_EXTERNAL_STORAGE)

        }
    }

    private fun loadDefault(){

        Log.d("IMG_URL",""+GoogleSignIn.getLastSignedInAccount(this)?.photoUrl?.toString())
        Glide.with(this).load(GoogleSignIn.getLastSignedInAccount(this)?.photoUrl).into(photoNU)
        txNameNU.setText(GoogleSignIn.getLastSignedInAccount(this)?.displayName)

    }

    private fun imageFileDefault(){

        Glide.with(this).asBitmap().load(GoogleSignIn.getLastSignedInAccount(this)?.photoUrl).into(object : SimpleTarget<Bitmap>(){

            override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {

                val imgFile = GetFileFromBitmap().getFileFromBitmap(resource!!,applicationContext)

                val requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), imgFile!!)
                imageToUpload = MultipartBody.Part.createFormData("photo",imgFile.name,requestBody)

            }

        })

    }

    private fun addUser(){

        Log.d("DATA_TO_UPLOAD"," "+txNameNU.text.toString())

        val email = RequestBody.create(MediaType.parse("multipart/form-data"), GoogleSignIn.getLastSignedInAccount(this)?.email!!)
        val name = RequestBody.create(MediaType.parse("multipart/form-data"), txNameNU.text.toString())

        ApiConfig().retrofit().newUser(email,name,imageToUpload)
            .enqueue(object : retrofit2.Callback<NewUserResponse>{

                override fun onFailure(call: Call<NewUserResponse>, t: Throwable) {
                    btnSaveNU.visibility = View.VISIBLE
                    UIHandler(this@NewUserActivity).showErrorToast("Registration failed")
                    Log.d("ONFAILURE",t.toString())
                }

                override fun onResponse(call: Call<NewUserResponse>, response: Response<NewUserResponse>) {
                    if(response?.isSuccessful){

                        if(response?.body()?.error!!){
                            btnSaveNU.visibility = View.VISIBLE
                            UIHandler(this@NewUserActivity).showErrorToast("Registration failed")
                        }else{
                            finish()
                            startActivity(Intent(this@NewUserActivity,MainActivity::class.java))
                        }

                    }
                }


            })

    }

}
