package com.example.homly.activities

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.homly.MyUtils
import com.example.homly.R
import com.example.homly.databinding.ActivityLoginOptionsBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.FirebaseDatabase

class LoginOptionsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginOptionsBinding

    private val TAG = "LOGIN_OPTIONS_TAG"

    // ProgressDialogue to show while google sign in
    private lateinit var progressDialog: ProgressDialog

    // Firebase Auth for auth related tasks
    private lateinit var firebaseAuth: FirebaseAuth

    private lateinit var mGoogleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityLoginOptionsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Progress Dialogue to show while sign-up
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please wait")
        progressDialog.setCanceledOnTouchOutside(false)

        // Firebase Auth for auth related tasks
        firebaseAuth = FirebaseAuth.getInstance()

        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder((GoogleSignInOptions.DEFAULT_SIGN_IN))
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

        //handle skipBtn click, go-back
        binding.skipBtn.setOnClickListener {
            finish()
        }

        // handle loginGoogleBtn click, begin google signIn
        binding.loginGoogleBtn.setOnClickListener {
            beginLoginBtn()
        }

        // handle loginEmailBtn click, start LoginEmailActivity
        binding.loginEmailBtn.setOnClickListener {
            startActivity(Intent(this, LoginEmailActivity::class.java))
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
    private fun beginLoginBtn(){
        Log.d(TAG, "beginLoginBtn")

        //Intent to launch google options dialog
        val googleSignInIntent = mGoogleSignInClient.signInIntent
        googleSignInARL.launch(googleSignInIntent)
    }

    private val googleSignInARL = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {result ->

        if (result.resultCode == Activity.RESULT_OK){
            val data = result.data
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                Log.d(TAG, "googleSignInARL: AccountID: ${account.id}")

                firebaseAuthWithGoogleAccount(account.idToken)
            } catch (e: Exception){
                Log.e(TAG, "googleSignInARL: ", e)
            }
        }else{
            // Cancelled from google signIn options/confirmation dialog
            Log.d(TAG, "googleSignInARL: Cancelled...!")

            MyUtils.toast(this, "Cancelled...!")
        }
    }

    private fun firebaseAuthWithGoogleAccount(idToken: String?){
        Log.d(TAG, "firebaseAuthWithGoogleAccount: idToken: $idToken")

        // Auth Credential to setup credential to signIn using (google) credential
        val credential = GoogleAuthProvider.getCredential(idToken,null)

        // SignIn in to firebase auth using Google Credentials
        firebaseAuth.signInWithCredential(credential)
            .addOnSuccessListener { authResult ->
                //SignIn success, let's check if the user is new(New account register) or existing (Existing Login)
                if (authResult.additionalUserInfo!!.isNewUser){
                    Log.d(TAG, "firebaseAuthWithGoogleAccount: Account Created...!")
                    //New user, Account created, let's save user info to firebase realtime database
                    updateUserInfoDb()
                } else {
                    //New user, Account created, No need to save info to firebase realtime database, Start MainActivity
                    startActivity(Intent(this, MainActivity::class.java))
                    finishAffinity()
                }
            }
            .addOnFailureListener { e->
                //SignIn failed, show exception
                Log.e(TAG, "firebaseAuthWithGoogleAccount: ",e)
                MyUtils.toast(this, "Failed signing in due to ${e.message}")
            }
    }

    private fun updateUserInfoDb(){
        Log.d(TAG, "updateUserInfoDb: ")

        //set message and show progress dialog
        progressDialog.setMessage("Saving user info...!")
        progressDialog.show()

        val timestamp = MyUtils.timestamp()
        val registeredUserUid = "${firebaseAuth.uid}"
        val registeredUserEmail = "${firebaseAuth.currentUser!!.email}"
        val name = "${firebaseAuth.currentUser!!.displayName}"

        val hashMap = HashMap<String, Any>()
        hashMap["uid"] = registeredUserUid
        hashMap["email"] = registeredUserEmail
        hashMap["name"] = name
        hashMap["timestamp"] = timestamp
        hashMap["phoneCode"] = ""
        hashMap["phoneNumber"] = ""
        hashMap["profileImageUrl"] = ""
        hashMap["dob"] = ""
        hashMap["userType"] = MyUtils.USER_TYPE_GOOGLE
        hashMap["token"] = ""

        //set data to firebase db
        val ref = FirebaseDatabase.getInstance().getReference("Users")
        ref.child(registeredUserUid)
            .setValue(hashMap)
            .addOnSuccessListener {
                //Firebase db save success
                Log.d(TAG, "updateUserInfoDb: User info saved...!")
                progressDialog.dismiss()
                startActivity(Intent(this, MainActivity::class.java))
                finishAffinity()
            }
            .addOnFailureListener { e->
                //Firebase db save failed
                Log.e(TAG, "updateUserInfoDb: ", e)
                progressDialog.dismiss()
                MyUtils.toast(this, "Failed to save data due to ${e.message}")
            }
    }

}