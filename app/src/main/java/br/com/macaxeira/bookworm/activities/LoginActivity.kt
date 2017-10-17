package br.com.macaxeira.bookworm.activities

import android.accounts.Account
import android.content.Intent
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import br.com.macaxeira.bookworm.BuildConfig
import br.com.macaxeira.bookworm.R
import br.com.macaxeira.bookworm.models.Bookshelf
import br.com.macaxeira.bookworm.models.BookshelfBaseResponse
import br.com.macaxeira.bookworm.network.GoogleBooksClient
import br.com.macaxeira.bookworm.services.ServiceGenerator
import br.com.macaxeira.bookworm.utils.Constants
import br.com.macaxeira.bookworm.utils.Utils
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.Scope
import com.google.android.gms.common.api.Status
import com.google.android.gms.tasks.Task
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.firebase.auth.*
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_login.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class LoginActivity : AppCompatActivity(), GoogleApiClient.OnConnectionFailedListener {

    private val RC_SIGN_IN = 9001

    private var mAuth: FirebaseAuth? = null
    var mAccessToken: String = ""
    var mAccount: Account? = null
    private var mBookshelves: List<Bookshelf>? = null
    private var mGoogleApiClient: GoogleApiClient? = null
    private var mUserId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(BuildConfig.GOOGLE_CLIENT_ID)
                .requestScopes(Scope("https://www.googleapis.com/auth/books"))
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
    }

    override fun onStart() {
        super.onStart()

        if (mAuth != null && mAuth?.currentUser != null) {
            /*if (TextUtils.isEmpty(mAccessToken))
                generateAccessToken()
            getUserIdAndBookshelves()*/
        }
    }

    private fun updateUi() {
        val user = mAuth?.currentUser
        if (user != null) {
            intent = Intent(this, BooksActivity::class.java)
            intent.putExtra(Constants.INTENT_ACCESS_TOKEN_EXTRA, mAccessToken)
            intent.putExtra(Constants.INTENT_USER_ID_EXTRA, mUserId)
            startActivity(intent)
        } else {
            loginNameText.text = resources.getString(R.string.sign_in)
        }
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
            _: Status -> updateUi()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val result = Auth.GoogleSignInApi.getSignInResultFromIntent(data)
            if (result.isSuccess) {
                firebaseAuthWithGoogle(result.signInAccount)
            }
        } else {
            Toast.makeText(this, R.string.toast_auth_error, Toast.LENGTH_SHORT).show()
        }
    }

    private fun firebaseAuthWithGoogle(signInAccount: GoogleSignInAccount?) {
        val mSignInToken = signInAccount?.idToken
        val credential = GoogleAuthProvider.getCredential(mSignInToken, null)
        mAuth?.signInWithCredential(credential)
                ?.addOnCompleteListener(this, {
                    task: Task<AuthResult> -> if (task.isSuccessful) {
                    mAccount = signInAccount?.account
                    generateAccessToken()
                } else {
                    Toast.makeText(this@LoginActivity, R.string.toast_auth_error, Toast.LENGTH_SHORT).show()
                }
                })
    }

    private fun getUserIdAndBookshelves() {
        val client = ServiceGenerator.createService(GoogleBooksClient::class.java, mAccessToken)
        val call = client.getBookshelves(BuildConfig.GOOGLE_API_KEY)

        call.enqueue(object : Callback<BookshelfBaseResponse> {
            override fun onResponse(call: Call<BookshelfBaseResponse>?, response: Response<BookshelfBaseResponse>?) {
                val shelves = response?.body()
                if (shelves != null) {
                    mBookshelves = shelves.bookshelves
                    val bookshelf = shelves.bookshelves[0]
                    var url = bookshelf.link
                    var urlSegments = url.split("/")
                    if (urlSegments.size >= 7) {
                        mUserId = urlSegments[6]
                    }
                    updateUi()
                } else {
                    Log.e(this@LoginActivity.javaClass.simpleName, "Error ${response?.code()} while retrieving bookshelves")
                    Log.e(this@LoginActivity.javaClass.simpleName, "Json: ${Gson().toJson(response)}")
                }
            }

            override fun onFailure(call: Call<BookshelfBaseResponse>?, t: Throwable?) {
                t?.printStackTrace()
            }
        })

    }

    fun generateAccessToken() {
        AccessTokenAsyncTask(this).execute("")
    }

    private class AccessTokenAsyncTask(val loginActivity: LoginActivity) : AsyncTask<String, String, String>() {
        override fun doInBackground(vararg arg: String): String {
            val account = loginActivity.mAccount
            account ?: return ""

            val accCredential = GoogleAccountCredential.usingOAuth2(loginActivity, Collections.singleton("https://www.googleapis.com/auth/books"))
            accCredential.selectedAccount = account
            return accCredential.token
        }

        override fun onPostExecute(result: String) {
            loginActivity.mAccessToken = if(!TextUtils.isEmpty(result)) result else "not valid"
            loginActivity.getUserIdAndBookshelves()
        }
    }

    override fun onConnectionFailed(result: ConnectionResult) {
        Log.e("LoginActivity", "Error: ${result.errorMessage}")
    }
}
