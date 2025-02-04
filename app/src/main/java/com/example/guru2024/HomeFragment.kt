package com.example.guru2024

import android.content.Context
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.example.guru2024.databinding.FragmentHomeBinding
import java.util.Timer
import kotlin.concurrent.timer

class HomeFragment : Fragment(), AdapterView.OnItemSelectedListener {
    lateinit var binding: FragmentHomeBinding

    private var time = 0
    private var timerTask: Timer? = null

    private lateinit var myHelper: MyDBHelper
    private lateinit var sqlDB: SQLiteDatabase
    private lateinit var strTime: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {

        binding = FragmentHomeBinding.inflate(inflater)

        myHelper = MyDBHelper.getInstance(requireContext())

        // 스피너
        binding.categorySpinner.onItemSelectedListener = this
        ArrayAdapter.createFromResource(
            requireContext(), R.array.categoryArray, android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.categorySpinner.adapter = adapter
        }

        // 종료 버튼 비활성화
        binding.btnEnd.isEnabled = false

        // 위치 찾기 버튼 리스너
        binding.btnLocation.setOnClickListener {
            // 지도 되지 않을 경우 xml -> editText
        }

        // 시작 버튼 리스너
        binding.btnStart.setOnClickListener {
            start()
        }

        // 종료 버튼 리스너
        binding.btnEnd.setOnClickListener {

            strTime = String.format("%02d:%02d", time / 100, time % 100)
            val selectedCategory = binding.categorySpinner.selectedItem.toString()
            // 위치
            // val location = ""
            end()

            sqlDB = myHelper.writableDatabase
            sqlDB.execSQL( //${locationTextView.text}
                "INSERT INTO timeTBL VALUES ('${getUserId()}', '$strTime', 'location0', '${binding.categorySpinner.selectedItem}');"
            )
            sqlDB.close()

            // RecAddActivity로 데이터 전달 후 전환
            val intent = Intent(context, RecAddActivity::class.java).apply {
                putExtra("ID", getUserId()) // 아이디 전달
                putExtra("TIME", strTime)
                putExtra("CATEGORY", selectedCategory)
            }
            startActivity(intent)
        }
        return binding.root
    }

    private fun start() {
        // 타이머 시작 시 종료 버튼 제외한 모든 뷰 비활성화
        setClickEnabled(false)

        // 타이머 시작
        binding.btnStart.isEnabled = false
        timerTask = timer(period = 10) {
            time++
            val sec = time / 100
            val milli = time % 100

            // 프래그먼트가 액티비티에 연결된 상태에서만 실행
            if (isAdded && activity != null) {
                activity?.runOnUiThread {
                    binding.tvSec.text = "$sec"
                    binding.tvMilli.text = "$milli"
                }
            } else {
                // 프래그먼트가 분리된 경우 타이머 종료
                timerTask?.cancel()
            }
        }
    }

    private fun end() {
        // 타이머 종료 시 모든 뷰 다시 활성화
        setClickEnabled(true)

        // 타이머 종료
        timerTask?.cancel()

        // 초기화
        time = 0
        binding.btnStart.isEnabled = true
        binding.tvSec.text = "0"
        binding.tvMilli.text = "00"
    }

    // 뷰 활성화/비활성화 메서드
    private fun setClickEnabled(enabled: Boolean) {
        binding.btnStart.isEnabled = enabled
        binding.btnEnd.isEnabled = !enabled // 종료 버튼은 반대로 활성화
        // 비활성화할 다른 버튼들 추가
        binding.btnLocation.isEnabled = enabled
        // spinner.isEnabled = enabled
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val selectedItem = parent?.getItemAtPosition(position).toString()
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
    }

    // 아이디
    private fun getUserId(): String? {
        val sharedPref = requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        return sharedPref.getString("userId", null) // 저장된 userId 가져오기
    }
}