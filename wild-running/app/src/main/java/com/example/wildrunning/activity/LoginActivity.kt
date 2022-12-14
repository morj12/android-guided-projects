package com.example.wildrunning.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import com.example.wildrunning.utils.EmailValidator
import com.example.wildrunning.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class LoginActivity : AppCompatActivity() {

    companion object {
        lateinit var userEmail: String
        lateinit var providerSession: String
        private const val RC_SIGN_GOOGLE_IN = 100
    }

    private lateinit var currentEmail: String
    private lateinit var currentPassword: String
    private lateinit var emailTextField: EditText
    private lateinit var passwordTextField: EditText
    private lateinit var termsLayout: LinearLayout
    private var registerEnabled = false

    private lateinit var mAuth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        userEmail = ""

        termsLayout = findViewById(R.id.llTerms)
        termsLayout.visibility = View.INVISIBLE

        emailTextField = findViewById(R.id.etEmail)
        passwordTextField = findViewById(R.id.etPassword)
        mAuth = FirebaseAuth.getInstance()

        checkLoginData()
        emailTextField.doOnTextChanged { text, start, before, count -> checkLoginData() }
        passwordTextField.doOnTextChanged { text, start, before, count -> checkLoginData() }
    }

    override fun onStart() {
        super.onStart()

        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) goHome(
            user.email.toString(),
            if (user.providerId == "firebase") "email" else user.providerId
        )
    }

    override fun onBackPressed() {
        val startMain = Intent(Intent.ACTION_MAIN)
        startMain.addCategory(Intent.CATEGORY_HOME)
        startMain.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(startMain)
    }

    private fun checkLoginData() {
        val btnLogin = findViewById<TextView>(R.id.btnLogin)
        currentEmail = emailTextField.text.toString()
        currentPassword = passwordTextField.text.toString()

        if (!EmailValidator.isEmail(currentEmail) || TextUtils.isEmpty(currentPassword)) {
            btnLogin.isEnabled = false
            btnLogin.setBackgroundColor(ContextCompat.getColor(this, R.color.gray))
        } else {
            btnLogin.isEnabled = true
            btnLogin.setBackgroundColor(ContextCompat.getColor(this, R.color.green))
        }

    }

    fun onLoginClicked(view: View) {
        currentEmail = emailTextField.text.toString()
        currentPassword = passwordTextField.text.toString()

        mAuth.signInWithEmailAndPassword(currentEmail, currentPassword)
            .addOnCompleteListener(this) {
                when {
                    // Logged in
                    it.isSuccessful -> {
                        goHome(currentEmail, "email")

                    }
                    // Allow register option
                    it.exception is FirebaseAuthInvalidUserException -> {
                        // Enable if it was disabled
                        if (!registerEnabled) {
                            registerEnabled = true
                            Toast.makeText(this, getString(R.string.register), Toast.LENGTH_SHORT)
                                .show()
                            termsLayout.visibility = View.INVISIBLE
                            findViewById<TextView>(R.id.btnLogin).text =
                                getString(R.string.registerPetition)
                        } else {
                            // Register
                            register()
                            registerEnabled = false
                        }
                    }
                    // Any other exception
                    else -> {
                        Toast.makeText(this, it.exception?.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
    }

    private fun goHome(email: String, provider: String) {
        userEmail = email
        providerSession = provider

        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    private fun register() {
        currentEmail = emailTextField.text.toString()
        currentPassword = passwordTextField.text.toString()

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(currentEmail, currentPassword)
            .addOnCompleteListener {
                // Successful register
                if (it.isSuccessful) {
                    saveUser()
                    goHome(currentEmail, "email")
                    // Any exception
                } else {
                    Toast.makeText(this, it.exception?.message, Toast.LENGTH_SHORT).show()
                }
            }
    }

    fun onTermsClicked(view: View) {
        val intent = Intent(this, TermsActivity::class.java)
        startActivity(intent)
    }

    fun onForgotPasswordClicked(view: View) {
        val forgottenEmail = emailTextField.text.toString()
        if (!TextUtils.isEmpty(forgottenEmail)) {
            mAuth.sendPasswordResetEmail(forgottenEmail)
                .addOnCompleteListener {
                    // Email sent
                    if (it.isSuccessful) {
                        Toast.makeText(this, "Message sent", Toast.LENGTH_SHORT).show()
                        // Any exception
                    } else {
                        Toast.makeText(this, it.exception?.message, Toast.LENGTH_SHORT).show()
                    }
                }
            // Empty email
        } else {
            Toast.makeText(this, "Empty email", Toast.LENGTH_SHORT).show()
        }
    }

    fun onLoginGoogleClicked(view: View) {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        val googleSignInClient = GoogleSignIn.getClient(this, gso)
        googleSignInClient.signOut()


        startActivityForResult(googleSignInClient.signInIntent, RC_SIGN_GOOGLE_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_GOOGLE_IN) {
            try {
                val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                val account = task.getResult(ApiException::class.java)

                if (account != null) {
                    currentEmail = account.email!!
                    val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                    mAuth.signInWithCredential(credential).addOnCompleteListener {
                        // Logged in
                        if (it.isSuccessful) {
                            saveUser()
                            goHome(currentEmail, "Google")
                        }
                    }
                }
            } catch (e: ApiException) {
                Toast.makeText(this, "Google authentication error", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveUser() {
        val registerDate = SimpleDateFormat("dd/MM/yyyy").format(Date())
        val dbRegister = FirebaseFirestore.getInstance()

        val document = dbRegister.collection("users").document(currentEmail)
        document.get().addOnSuccessListener {
            if (!it.exists()) {
                document.set(hashMapOf("user" to currentEmail, "registerDate" to registerDate))
            }
        }
    }
}



