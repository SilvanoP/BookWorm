package br.com.macaxeira.bookworm.activities

import android.content.Intent
import android.content.ServiceConnection
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import br.com.macaxeira.bookworm.BuildConfig
import br.com.macaxeira.bookworm.R
import br.com.macaxeira.bookworm.network.GoogleBooksClient
import br.com.macaxeira.bookworm.services.ServiceGenerator
import br.com.macaxeira.bookworm.utils.Utils
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.Status
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.*
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity(), GoogleApiClient.OnConnectionFailedListener {

    private val RC_SIGN_IN = 9001

    private var mAuth: FirebaseAuth? = null
    private var mGoogleApiClient: GoogleApiClient? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(BuildConfig.GOOGLE_CLIENT_ID)
                .requestEmail()
                .build()

        mGoogleApiClient = GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build()

        mAuth = FirebaseAuth.getInstance()

        loginButton1.setOnClickListener {
            _: View? -> signIn()
        }

        loginButton2.setOnClickListener {
            _: View? -> signOut()
        }
    }

    override fun onStart() {
        super.onStart()

        updateUi(mAuth?.currentUser)
    }

    private fun signIn() {
        if (Utils.isOnline(this)) {
            val signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient)
            startActivityForResult(signInIntent, RC_SIGN_IN)
        } else {
            Toast.makeText(this, R.string.toast_disconnected, Toast.LENGTH_SHORT).show()
        }
    }

    private fun signOut() {
        mAuth?.signOut()

        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback {
            _: Status -> updateUi(null)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val result = Auth.GoogleSignInApi.getSignInResultFromIntent(data)
            if (result.isSuccess) {
                firebaseAuthWithGoogle(result.signInAccount)
            }
        }
    }

    private fun firebaseAuthWithGoogle(signInAccount: GoogleSignInAccount?) {
        val credential = GoogleAuthProvider.getCredential(signInAccount?.idToken, null)
        mAuth?.signInWithCredential(credential)
                ?.addOnCompleteListener(this, {
                    task: Task<AuthResult> -> if (task.isSuccessful) updateUi(mAuth?.currentUser)
                })
    }

    private fun updateUi(user: FirebaseUser?) {
        loginNameText.text = if (TextUtils.isEmpty(user?.displayName)) "Please sign in" else user?.displayName
        if (user != null) {
            intent = Intent(this, BooksActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onConnectionFailed(result: ConnectionResult) {
        Log.e("LoginActivity", "Error: ${result.errorMessage}")
    }
}
