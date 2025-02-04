package com.example.guru2024

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.guru2024.databinding.FragmentInfoBinding


class InfoFragment : Fragment(), AdapterView.OnItemSelectedListener {

    lateinit var binding: FragmentInfoBinding

    private lateinit var myHelper: MyDBHelper
    private lateinit var sqlDB: SQLiteDatabase

    private var userId: String = "" // 현재 로그인한 사용자의 ID
    private var selectedCategory: String = "" // 선택된 운동 카테고리

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding= FragmentInfoBinding.inflate(inflater)

        myHelper = MyDBHelper.getInstance(requireContext()) // DB 헬퍼 초기화

        // 스피너 설정
        binding.editCategory.onItemSelectedListener = this
        ArrayAdapter.createFromResource(
            requireContext(), R.array.categoryArray, android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.editCategory.adapter = adapter
        }

        // 현재 로그인한 유저 정보 가져오기
        userId = getCurrentUserId()
        loadUserInfo(userId)

        // 저장 버튼 리스너
        binding.btnEdit.setOnClickListener {
            saveUserInfo(userId)
        }

        // 로그아웃
        binding.logOut.setOnClickListener{
            logout(userId)
            val intent = Intent(context, IntroActivity::class.java)
            Toast.makeText(requireContext(), "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show()
            activity?.finishAffinity()
            startActivity(intent)
        }
        return binding.root
    }

    /**
     *  현재 로그인한 사용자 ID를 가져오는 함수
     *  SharedPreferences, 글로벌 변수, 또는 로그인 시 저장한 데이터에서 불러오기
     */

    private fun getCurrentUserId(): String {
        // SharedPreferences에서 저장된 userId를 가져오기
        val sharedPreferences = requireActivity().getSharedPreferences("UserPrefs", 0)
        return sharedPreferences.getString("mId", "") ?: ""
    }

    private fun logout(userId: String) {
        val sharedPreferences = requireActivity().getSharedPreferences("UserPrefs", 0)
        val editor = sharedPreferences.edit()

        editor.remove("mId")
        editor.remove("mPW")
        editor.apply()
    }

    /**
     *  데이터베이스에서 사용자 정보 조회 후 EditText 및 Spinner에 설정
     */
    private fun loadUserInfo(userId: String) {
        sqlDB = myHelper.readableDatabase
        val cursor = sqlDB.rawQuery("SELECT mName, mPW, mCat FROM memberTBL WHERE mId=?", arrayOf(userId))

        if (cursor.moveToFirst()) {
            val name = cursor.getString(0)  // mName
            val pw = cursor.getString(1)    // mPW
            selectedCategory = cursor.getString(2) // mCat

            // EditText에 값 설정
            binding.editName.setText(name)
            binding.editPw.setText(pw)

            // 스피너에서 저장된 카테고리를 기본 선택값으로 설정
            val categoryArray = resources.getStringArray(R.array.categoryArray)
            val categoryIndex = categoryArray.indexOf(selectedCategory)
            if (categoryIndex >= 0) {
                binding.editCategory.setSelection(categoryIndex)
            }
        }
        cursor.close()
        sqlDB.close()
    }

    /**
     *  사용자가 수정한 정보를 DB에 업데이트
     */
    private fun saveUserInfo(userId: String) {
        val newName = binding.editName.text.toString()
        val newPW = binding.editPw.text.toString()
        val newCategory = selectedCategory

        sqlDB = myHelper.writableDatabase

        val values = ContentValues().apply {
            put("mName", newName)
            put("mPW", newPW)
            put("mCat", newCategory)
        }

        val result = sqlDB.update("memberTBL", values, "mId=?", arrayOf(userId))

        if (result > 0) {
            Toast.makeText(requireContext(), "정보가 저장되었습니다.", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(requireContext(), "정보 저장 실패", Toast.LENGTH_SHORT).show()
        }

        sqlDB.close()
    }

    // 스피너에서 항목 선택 시 호출
    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        selectedCategory = parent?.getItemAtPosition(position).toString()
    }

    // 스피너에서 아무것도 선택되지 않았을 때 호출
    override fun onNothingSelected(parent: AdapterView<*>?) {
        // 기본값 유지
    }
}