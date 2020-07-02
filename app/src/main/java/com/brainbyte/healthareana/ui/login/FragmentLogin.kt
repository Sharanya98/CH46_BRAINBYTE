package com.brainbyte.healthareana.ui.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import com.brainbyte.healthareana.HealthArenaApplication
import com.brainbyte.healthareana.R
import com.brainbyte.healthareana.databinding.FragmentLoginBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import timber.log.Timber
import javax.inject.Inject

class FragmentLogin : Fragment() {
    @Inject
    lateinit var viewModel: LoginVM

    private lateinit var loginComponent: LoginComponent

    private lateinit var googleSignInClient: GoogleSignInClient

    private lateinit var auth: FirebaseAuth

    private val RC_SIGN_IN = 9001

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding = FragmentLoginBinding.inflate(layoutInflater, container, false)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestId()
            .requestIdToken(resources.getString(R.string.web_server_id))
            .requestEmail()
            .requestProfile()
            .build()

        googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)

        auth = FirebaseAuth.getInstance()

        viewModel.status.observe(viewLifecycleOwner) {
            when (it) {

                LoginState.NOT_LOGGED_IN -> {
                    Toast.makeText(
                        requireContext(),
                        "Please Sign Up Using Google",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                LoginState.LOGGED_IN -> {
                    Toast.makeText(
                        requireContext(),
                        "Logged In Successfully",
                        Toast.LENGTH_SHORT
                    ).show()

                    findNavController().navigate(FragmentLoginDirections.actionFragmentLoginToFragmentHome())
                }

                LoginState.LOGIN_ERROR -> {
                    Toast.makeText(
                        requireContext(),
                        "Please Try Again",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }


        binding.buttonSignIn.apply {
            (this.getChildAt(0) as TextView).text = resources.getText(R.string.sign_in_button)
            setOnClickListener {
                signIn()
            }
        }

        return binding.root
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)

        // Creates an instance of Registration component by grabbing the factory from the app graph
        loginComponent = (requireActivity().application as HealthArenaApplication).appComponent.loginComponent()
            .create()

        loginComponent.inject(this@FragmentLogin)

    }

    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        Timber.d("Sign in Called!!")
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(RC_SIGN_IN == requestCode) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)

            try {
                val account = task.getResult(ApiException::class.java)
                Timber.d("account details : ${account?.displayName}")
                account?.let {
                    viewModel.loginInUser(auth, it)
                }

            } catch (e: ApiException) {
            }
        }

    }
}