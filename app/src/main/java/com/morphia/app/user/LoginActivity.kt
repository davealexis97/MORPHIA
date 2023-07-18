package com.morphia.app.user

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
import android.view.View
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.GoogleAuthProvider
import com.morphia.app.MainActivity
import com.morphia.app.R
import com.morphia.app.base.BaseActivity
import com.morphia.app.base.Status
import com.morphia.app.databinding.ActivityLoginBinding
import com.morphia.app.extension.ViewExtension.asColor
import com.morphia.app.extension.ViewExtension.showToast


class LoginActivity : BaseActivity(), View.OnClickListener {

    lateinit var binding: ActivityLoginBinding
    private var googleSignInClient: GoogleSignInClient? = null
    private val viewModel: LoginViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()
        setClicks()
        initGoogleSignInClient()
        subscribeObserver()
    }

    private fun subscribeObserver() {
        viewModel.googleSignInHandle.observe(this) {
            when (it.status) {
                Status.SUCCESS -> {
                    hideProgress()
                    startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                }

                Status.ERROR -> {
                    hideProgress()
                    if (it.exception != null) {
                        viewModel.setError(it.exception.message)
                    }
                }

                Status.LOADING -> {
                    showProgress()
                }
            }
        }

        viewModel.authSignInHandle.observe(this) {
            when (it.status) {
                Status.SUCCESS -> {
                    hideProgress()
                    startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                }

                Status.ERROR -> {
                    hideProgress()
                    if (it.exception != null) {
                        viewModel.setError(it.exception.message)
                    }

                }

                Status.LOADING -> {
                    showProgress()
                }
            }
        }

        viewModel.getErrorObserver().observe(this) {
            if (!it.isNullOrEmpty() || it.isNotBlank()) {
                showToast(it)
            }
        }


    }

    private fun initGoogleSignInClient() {
        val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build()
        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions)
    }

    private fun setClicks() {
        binding.ivGoogleSignIn.setOnClickListener(this)
        binding.btnSignIn.setOnClickListener(this)
    }

    private fun initView() {
        setSpanString()
    }

    private fun setSpanString() {
        val string = getString(R.string.dont_have_account)
        if (string.contains("?")) {
            val split = string.split("?")

            val ss = SpannableString(string)
            val clickableSpan: ClickableSpan = object : ClickableSpan() {
                override fun onClick(widget: View) {
                    startActivity(Intent(this@LoginActivity, RegisterActivity::class.java))
                }

                override fun updateDrawState(ds: TextPaint) {
                    super.updateDrawState(ds)
                    ds.isUnderlineText = true
                    ds.color = R.color.text_color.asColor()
                }
            }
            ss.setSpan(
                clickableSpan, split[0].length + 2, string.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            binding.createAccount.text = ss
            binding.createAccount.movementMethod = LinkMovementMethod.getInstance()
        }


    }

    private val googleSignInLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            val singInData = result.data
            if (singInData != null) {
                val task: Task<GoogleSignInAccount> =
                    GoogleSignIn.getSignedInAccountFromIntent(singInData)
                try {
                    val googleSignInAccount: GoogleSignInAccount =
                        task.getResult(ApiException::class.java)
                    getGoogleAuthCredential(googleSignInAccount)
                } catch (e: ApiException) {
                    e.printStackTrace()
                    viewModel.setError(e.message)
                }
            }
        } else {
            viewModel.setError(getString(R.string.something_went_wrong))
        }
    }

    private fun getGoogleAuthCredential(googleSignInAccount: GoogleSignInAccount) {
        val googleTokenId = googleSignInAccount.idToken
        val googleAuthCredential = GoogleAuthProvider.getCredential(googleTokenId, null)
        signInWithAuthCredential(googleAuthCredential)
    }

    private fun signInWithAuthCredential(authCredential: AuthCredential) {
        viewModel.signInWithGoogle(authCredential)
    }


    override fun onClick(v: View?) {
        when (v) {
            binding.ivGoogleSignIn -> {
                signIn()
            }

            binding.btnSignIn -> {
                loginWithCredential()
            }
        }
    }

    private fun loginWithCredential() {
        val email = binding.teEmail.text.toString().trim()
        val password = binding.tePassword.text.toString().trim()
        if (email.isEmpty()) {
            binding.tlEmail.error = getString(R.string.enter_your_email)
            return
        }

        if (password.isEmpty()) {
            binding.tlEmail.error = getString(R.string.enter_password)
            return
        }

        viewModel.signInwithCredential(email, password)
    }

    private fun signIn() {
        if (googleSignInClient != null) {
            val signInIntent: Intent = googleSignInClient!!.signInIntent
            googleSignInLauncher.launch(signInIntent)
        }

    }
}