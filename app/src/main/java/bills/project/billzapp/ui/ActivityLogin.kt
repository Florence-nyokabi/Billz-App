
package bills.project.billzapp.ui

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import bills.project.billzapp.databinding.ActivityLoginBinding
import bills.project.billzapp.ViewModel.LoginUserViewModel
import bills.project.billzapp.model.LoginRequest
import bills.project.billzapp.model.LoginResponse
import bills.project.billzapp.utils.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class ActivityLogin : AppCompatActivity() {

    lateinit var binding: ActivityLoginBinding
    val loginUserViewModel: LoginUserViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {

        binding = ActivityLoginBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
    }

    override fun onResume() {
        super.onResume()
        setContentView(binding.root)

        binding.btnlogin.setOnClickListener {
            clearLogInErrors()
            validateLogIn()
        }
        binding.tvSignUp.setOnClickListener {
            startActivity(Intent(this@ActivityLogin, ActivitySignUp::class.java))
        }

        loginUserViewModel.errLiveData.observe(this, Observer { err->
            Toast.makeText(this, err, Toast.LENGTH_SHORT).show()
            binding.pbProgressBar.visibility = View.GONE
        })
        loginUserViewModel.regLiveData.observe(this, Observer { logResponse->
            persistLogin(logResponse)
            binding.pbProgressBar.visibility = View.GONE
            Toast.makeText(this, logResponse.message, Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, MainPage::class.java))
            finish()
        })
    }

    fun validateLogIn(){
        val emailAddress = binding.etEmail.text.toString()
        val password = binding.etpassword.text.toString()

        var error = false

        if (emailAddress.isBlank()){
            binding.tilemail.error = "Please Enter Your Phone Number"
            error = true
        }
        if (password.isBlank()){
            binding.tilPassword.error = "Please Enter Your Password "
            error = true
        }
        if(!error){
            val loginRequest = LoginRequest(
                email = emailAddress,
                password = password
            )
            binding.pbProgressBar.visibility = View.VISIBLE
//            loginUserViewModel.loginUser(loginRequest)
            GlobalScope.launch(Dispatchers.IO) {
                loginUserViewModel.loginUser(loginRequest)
            }
        }
    }

    fun clearLogInErrors(){
        binding.tilemail.error = null
        binding.tilPassword.error = null
    }

    fun persistLogin(loginResponse: LoginResponse){
        val sharedPrefs = getSharedPreferences(Constants.PREFS, Context.MODE_PRIVATE)
        val editor = sharedPrefs.edit()
        editor.putString(Constants.USER_ID, loginResponse.userId)
        editor.putString(Constants.ACCESS_TOKEN, loginResponse.accessToken)
        editor.apply()
    }

}
