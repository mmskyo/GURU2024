package com.example.guru2024

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.guru2024.databinding.FragmentRecBinding

class RecFragment : Fragment() {
    lateinit var binding: FragmentRecBinding
    lateinit var recAdapter: RecRVAdapter
    var recList = arrayListOf<RecList>()

    private val ADD_RECORD_REQUEST = 1  // 요청 코드

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentRecBinding.inflate(inflater, container, false)

        // RecyclerView 설정
        recAdapter = RecRVAdapter(recList, requireContext())
        binding.rvList.layoutManager = LinearLayoutManager(requireContext())
        binding.rvList.adapter = recAdapter

        loadRecordsFromDB(requireContext()) // 데이터 불러오기

        return binding.root
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ADD_RECORD_REQUEST && resultCode == Activity.RESULT_OK) {
            loadRecordsFromDB(requireContext()) // 데이터 갱신
        }
    }

    fun loadRecordsFromDB(context: Context) {
        recList.clear()
        val myHelper = MyDBHelper.getInstance(context)
        val db: SQLiteDatabase = myHelper.readableDatabase
        val cursor = db.rawQuery("SELECT rTime, rLoc, rCat, rContent FROM recTBL", null)

        while (cursor.moveToNext()) {
            val time = cursor.getString(0)
            val loc = cursor.getString(1)
            val cat = cursor.getString(2)
            val content = cursor.getString(3)

            recList.add(RecList(time, cat, loc, content))
        }
        cursor.close()
        db.close()

        recAdapter.notifyDataSetChanged() // RecyclerView 갱신
    }
}
