package com.king.nutrynow

import androidx.appcompat.app.AppCompatActivity

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.View
import android.widget.Toast

import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_sign_in.*


class SignInActivity : AppCompatActivity() {

    private var mAuth: FirebaseAuth? = null
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private var currentUser: FirebaseUser? = null
    private var uid: String = ""
    internal var name: String? = null
    private var db = FirebaseFirestore.getInstance()
    private lateinit var sharedPreferences: SharedPreferences

    override fun onStart() {
        super.onStart()

        // Checking if user is signed in (non-null) already or not
        currentUser = mAuth!!.currentUser
        if (currentUser != null) {
            name = currentUser!!.displayName
            uid = currentUser!!.uid
            sharedPreferences.edit().putString("name", name).apply()
            sharedPreferences.edit().putString("uid", uid).apply()
            startActivity(Intent(this@SignInActivity, MainActivity::class.java))
            finish()
        }

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)


        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(applicationContext)!!
        SignInProgressBar.visibility = View.GONE
        signInButton.setSize(SignInButton.SIZE_STANDARD)
        signInButton.setOnClickListener { signIn() }

        // Initializing Firebase Auth Instance
        mAuth = FirebaseAuth.getInstance()


        // Configuring sign-in to request the user'tag ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()

        // GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

    }

    private fun signIn() {

        SignInProgressBar.visibility = View.VISIBLE
        mGoogleSignInClient.signOut()
        mGoogleSignInClient.revokeAccess()
        val signInIntent = mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)

    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...)
        // present in signIn() method
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticating with Firebase
                val account = task.getResult<ApiException>(ApiException::class.java)
                if (account != null) firebaseAuthWithGoogle(account)
            } catch (e: ApiException) {
                // Google Sign In failed
                SignInProgressBar.visibility = View.GONE
                Log.w(TAG, "Google sign in failed", e)
                Toast.makeText(this@SignInActivity, "Sign In Failed", Toast.LENGTH_SHORT).show()
            }

        }
    }

    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.id!!)

        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        mAuth!!.signInWithCredential(credential)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        //if Auth Success,  Sign in success
                        Log.d(TAG, "signInWithCredential:success")

                        val user = mAuth!!.currentUser
                        if (user != null) {
                            name = user.displayName
                            uid = user.uid
                            sharedPreferences.edit().putString("name", name).apply()
                            sharedPreferences.edit().putString("uid", uid).apply()

                            //Checking if database exists for this user
                            val documentReference: DocumentReference = db.collection("users").document(uid)
                            documentReference.get()
                                    .addOnSuccessListener { documentSnapshot ->
                                        if (documentSnapshot.exists()) {
                                            //Database exists
                                            //retrieving already stored data
                                            val goal: String? = documentSnapshot.getString("goal")
                                            val exLvl: String? = documentSnapshot.getString("exLvl")
                                            val gender: String? = documentSnapshot.getString("gender")
                                            val height: String? = documentSnapshot.getString("height")
                                            val weight: String? = documentSnapshot.getString("weight")

                                            sharedPreferences.edit().putString("height", height).apply()
                                            sharedPreferences.edit().putString("weight", weight).apply()
                                            sharedPreferences.edit().putString("gender", gender).apply()
                                            sharedPreferences.edit().putString("exLvl", exLvl).apply()
                                            sharedPreferences.edit().putString("goal", goal).apply()

                                            NutritionData(applicationContext)

                                            SignInProgressBar.visibility = View.GONE

                                            startActivity(Intent(this@SignInActivity, MainActivity::class.java))
                                            finish()
                                        } else {
                                            //Doesn't exists
                                            SignInProgressBar.visibility = View.GONE
                                            startActivity(Intent(this@SignInActivity, OnboardActivity1::class.java))
                                            finish()
                                        }
                                    }
                                    .addOnFailureListener { e ->
                                        SignInProgressBar.visibility = View.GONE
                                        Toast.makeText(this@SignInActivity,
                                                e.toString(), Toast.LENGTH_SHORT).show()
                                    }
                        }
                    } else {
                        // if Auth Failed,  sign in fails
                        SignInProgressBar.visibility = View.GONE
                        Log.w(TAG, "signInWithCredential:failure", task.exception)
                        Toast.makeText(this@SignInActivity, "Authentication Failed.", Toast.LENGTH_SHORT).show()
                    }
                }
    }

    companion object {
        private const val TAG = "tag"
        private const val  RC_SIGN_IN = 101
    }

}
