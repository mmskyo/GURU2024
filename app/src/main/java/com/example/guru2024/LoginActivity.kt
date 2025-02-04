package com.example.guru2024

import android.content.Intent
import android.database.Cursor
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.guru2024.databinding.ActivityLoginBinding
import java.util.regex.Pattern

class LoginActivity : AppCompatActivity() {

    lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // view binding
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnLogin.setOnClickListener {
            // db
            val myHelper = MyDBHelper.getInstance(this)
            val db = myHelper.readableDatabase

            // id, pw
            val id = binding.loginId.text.toString().trim()
            val pw = binding.loginPw.text.toString().trim()

            // 입력값 검증
            if (!isValidId(id) || !isValidPassword(pw)){
                Toast.makeText(this, "아이디 또는 비밀번호 형식을 확인해주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 데이터베이스 조회
            val cursor: Cursor = db.rawQuery(
                "SELECT * FROM memberTBL WHERE mId=? AND mPW=?", arrayOf(id, pw)
            )

            if (cursor.moveToFirst()) {
                Toast.makeText(this, "환영합니다!", Toast.LENGTH_SHORT).show()

                // 화면 전환
                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra("ID", id) // 사용자 ID를 전달
                startActivity(intent)
                finish()
            } else { Toast.makeText(this, "아이디 또는 비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show() }

            cursor.close()
            db.close()
        }

        // 회원가입 화면으로 이동
        binding.btnSignUp.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }
    }

    // 아이디 검증 (영문자로만 최대 10자)
    private fun isValidId(id: String): Boolean {
        val idPattern = "^[a-zA-Z]{1,10}$"
        return Pattern.matches(idPattern, id)
    }

    // 비밀번호 검증 (영문자 + 숫자, 8~10자)
    private fun isValidPassword(pw: String): Boolean {
        val pwPattern = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,10}$"
        return Pattern.matches(pwPattern, pw)
    }
}