
package bills.project.billmanagementapp.UI

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import bills.project.billmanagementapp.ViewModel.LoginUserViewModel
import bills.project.billmanagementapp.ViewModel.UserViewModel
import bills.project.billmanagementapp.databinding.ActivityLoginBinding
import bills.project.billmanagementapp.model.LoginRequest
import bills.project.billmanagementapp.model.RegisterRequest

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

        binding.btnLoggingIn.setOnClickListener {
            clearLogInErrors()
            validateLogIn()
            val intent = Intent(this, MainPage::class.java)
            startActivity(intent)
        }
        loginUserViewModel.errLiveData.observe(this, Observer { err->
            Toast.makeText(this, err, Toast.LENGTH_SHORT).show()
            binding.pbLogin.visibility = View.GONE
        })
        loginUserViewModel.regLiveData.observe(this, Observer { regResponse->
            binding.pbLogin.visibility = View.GONE
            Toast.makeText(this, regResponse.message, Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, MainPage::class.java))
        })

    }

    fun validateLogIn(){
        val emailAddress = binding.etEmail.text.toString()
        val password = binding.etPassword.text.toString()

        var error = false

        if (emailAddress.isEmpty()){
            binding.tilEmail.error = "Please input user name"
            error = true
        }
        if (password.isEmpty()){
            binding.tilPassword.error = "Please input password"
            error = true
        }
        if(!error){
            val loginRequest = LoginRequest(
                email = emailAddress,
                password = password
            )
            binding.pbLogin.visibility = View.VISIBLE
            loginUserViewModel.loginUser(loginRequest)
        }
    }

    fun clearLogInErrors(){
        binding.tilEmail.error = null
        binding.tilPassword.error = null
    }
}