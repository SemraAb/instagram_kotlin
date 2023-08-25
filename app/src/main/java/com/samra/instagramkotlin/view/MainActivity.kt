package com.samra.instagramkotlin.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.samra.instagramkotlin.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        auth = Firebase.auth
        var currentUser = auth.currentUser
        if(currentUser!=null){
            var intent = Intent(this , FeedActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
    fun singInClicked(view: View){
        var email = binding.emailText.text.toString()
        var password = binding.passwordText.text.toString()
        if(email.equals("") || password.equals("")){
            Toast.makeText(this@MainActivity , "Email or Password is empty!" , Toast.LENGTH_LONG).show()
        }else{
            auth.signInWithEmailAndPassword(email, password).addOnSuccessListener {
                var intent = Intent(this@MainActivity , FeedActivity::class.java)
                startActivity(intent)
                finish()
            }.addOnFailureListener {
                Toast.makeText(this@MainActivity , it.localizedMessage , Toast.LENGTH_LONG).show()
            }
        }
    }

    fun singUpClicked(view:View){
        var email = binding.emailText.text.toString()
        var password = binding.passwordText.text.toString()
        if(email.equals("")|| password.equals("")) {
            Toast.makeText(this@MainActivity , "Email or Password is empty!" , Toast.LENGTH_LONG).show()
        }else {
            auth.createUserWithEmailAndPassword(email, password).addOnSuccessListener {
                var intent = Intent(this@MainActivity , FeedActivity::class.java)
                startActivity(intent)
                finish()
            }.addOnFailureListener {
                Toast.makeText(this@MainActivity , it.localizedMessage , Toast.LENGTH_LONG).show()
            }
        }
    }
}