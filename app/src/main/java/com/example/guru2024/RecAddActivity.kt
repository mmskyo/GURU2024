package com.example.guru2024

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.guru2024.databinding.ActivityRecAddBinding

class RecAddActivity : AppCompatActivity() {

    lateinit var binding: ActivityRecAddBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRecAddBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val receivedTime = intent.getStringExtra("TIME")
        val receivedCategory = intent.getStringExtra("CATEGORY")
        val receivedLocation = intent.getStringExtra("LOCATION")

        binding.textTimeSet.setText(receivedTime ?: "")
        binding.textCatSet.setText(receivedCategory ?: "")
        binding.textLocSet.setText(receivedLocation ?: "")

        // 뒤로 가기 버튼
        binding.btnBack.setOnClickListener{
            this@RecAddActivity.finish()
            Log.d("recAddActivity", "click")
        }

        // 저장 버튼
        binding.btnSave.setOnClickListener {
            val myHelper = MyDBHelper.getInstance(this)
            val db = myHelper.writableDatabase

            val setTime = binding.textTimeSet.text.toString()
            val setCat = binding.textCatSet.text.toString()
            val setLoc = binding.textLocSet.text.toString()
            val setCon = binding.textContent.text.toString()
            val setId = getSharedPreferences("UserPrefs", 0).getString("mId", "")
            Log.d("test12345", setId.toString())
            Log.d("test123456", intent.getStringExtra("mId").toString())

            val insertQuery = "INSERT INTO recTBL (mId, rTime, rLoc, rCat, rContent) VALUES (?, ?, ?, ?, ?)"
            val stmt = db.compileStatement(insertQuery)
            stmt.bindString(1, setId)
            stmt.bindString(2, setTime)
            stmt.bindString(3, setLoc)
            stmt.bindString(4, setCat)
            stmt.bindString(5, setCon)

            try {
                stmt.executeInsert()
                setResult(RESULT_OK)  // RecFragment에 변경을 알림
                finish()  // 액티비티 종료
            } catch (e: Exception) {
                Toast.makeText(this, "등록이 되지 않습니다.", Toast.LENGTH_SHORT).show()
            } finally {
                stmt.close()
                db.close()
            }
        }
    }
}