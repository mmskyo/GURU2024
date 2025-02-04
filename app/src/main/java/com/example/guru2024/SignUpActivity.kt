package com.example.guru2024

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.guru2024.databinding.ActivitySignUpBinding

class SignUpActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    lateinit var binding: ActivitySignUpBinding

    var selectedCategory: String = "러닝" // 기본값 '러닝'으로 설정

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // view binding
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 스피너 설정
        binding.setCat.onItemSelectedListener = this

        ArrayAdapter.createFromResource(
            this, R.array.categoryArray, android.R.layout.simple_spinner_item
        ).also {
            adapter -> adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.setCat.adapter = adapter
        }

        binding.btnSignUp.setOnClickListener {
            val myHelper = MyDBHelper.getInstance(this)
            val db = myHelper.writableDatabase

            // 가입 정보
            val id = binding.setId.text.toString()
            val pw = binding.setPw.text.toString()
            val name = binding.setName.text.toString()
            val category = binding.setCat.selectedItem.toString() // 스피너 운동 종목

            // 회원가입 데이터 저장
            val insertQuery = "INSERT INTO memberTBL (mId, mPW, mName, mCat) VALUES (?, ?, ?, ?);"
            val stmt = db.compileStatement(insertQuery)
            stmt.bindString(1, id)
            stmt.bindString(2, pw)
            stmt.bindString(3, name)
            stmt.bindString(4, category)

            try {
                stmt.executeInsert() // 실행
                Toast.makeText(this, "회원가입이 완료되었습니다.", Toast.LENGTH_SHORT).show()

                // 로그인으로 이동
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            } catch (e: Exception) {
                Toast.makeText(this, "회원가입에 실패하였습니다. : ${e.message}", Toast.LENGTH_SHORT).show()
            } finally {
                stmt.close()
                db.close()
            }
        }
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        selectedCategory = parent?.getItemAtPosition(position).toString()
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        // 선택하지 않으면 기본값 '러닝' 유지
    }
}