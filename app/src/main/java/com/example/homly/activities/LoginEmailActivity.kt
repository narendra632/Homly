package com.example.homly.activities

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.homly.MyUtils
import com.example.homly.R
import com.example.homly.databinding.ActivityLoginEmailBinding
import com.google.firebase.auth.FirebaseAuth

class LoginEmailActivity : AppCompatActivity() {

    //View Binding
    private lateinit var binding: ActivityLoginEmailBinding
    //Tag to show logs in logcat
    private val TAG = "LOGIN_EMAIL_TAG"
    //ProgressDialogue to show while sign-in
    private lateinit var progressDialog: ProgressDialog
    //Firebase Auth for auth related tasks
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        //activity_login_email.xml = ActivityLoginEmailBinding
        binding = ActivityLoginEmailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //init/setup ProgressDialog to show while sign-in
        progressDialog = ProgressDialog(this, )
        progressDialog.setTitle("Please wait")
        progressDialog.setCanceledOnTouchOutside(false)

        //get instance of firebase auth for Auth related tasks
        firebaseAuth = FirebaseAuth.getInstance()

        //handle toolbarBackBtn click, go-back
        binding.toolbarBackBtn.setOnClickListener {
            finish()
        }

        //handle loginBtn click, start login
        binding.loginBtn.setOnClickListener {
            validateData()
        }

        //setContentView(R.layout.activity_login_email)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private var email = ""
    private var password = ""

    private fun validateData(){
        //input data
        email = binding.emailEt.text.toString().trim()
        password = binding.passwordEt.text.toString()

        Log.d(TAG, "validateData: Email: $email")
        Log.d(TAG, "validateData: Password: $password")

        //validate data
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            //email pattern is invalid, show error
            binding.emailEt.error = "Invalid Email"
            binding.emailEt.requestFocus()
        } else if (password.isEmpty()){
            //password is not entered, show error
            binding.passwordEt.error = "Enter Password"
            binding.passwordEt.requestFocus()
        } else {
            loginUser()
        }
    }

    private fun loginUser(){
        //show progress
        progressDialog.setMessage("Logging In...")
        progressDialog.show()

        //start user login
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                //User login success
                Log.d(TAG, "loginUser: Logged In...")
                progressDialog.dismiss()

                startActivity(Intent(this, MainActivity::class.java))
                finishAffinity()
            }
            .addOnFailureListener {e->
                //User login failed
                Log.d(TAG, "loginUser: ",e)
                progressDialog.dismiss()
                MyUtils.toast(this, "Failed due to ${e.message}")
            }
    }

}