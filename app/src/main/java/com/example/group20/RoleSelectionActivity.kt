package com.example.group20

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import kotlin.jvm.java

class RoleSelectionActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_role_selection)

        // 找到3个按钮
        val btnParent = findViewById<Button>(R.id.btnParent)
        val btnProvider = findViewById<Button>(R.id.btnProvider)
        val btnChild = findViewById<Button>(R.id.btnChild)

        // 点击事件
        btnParent.setOnClickListener { openSignUp("Parent") }
        btnProvider.setOnClickListener { openSignUp("Provider") }
        btnChild.setOnClickListener { openSignUp("Child") }
    }

    private fun openSignUp(role: String) {
        val intent = Intent(this, SignUpActivity::class.java)
        intent.putExtra("ROLE", role)
        startActivity(intent)
    }
}