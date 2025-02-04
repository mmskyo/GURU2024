package com.example.guru2024

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView

class RecRVAdapter(private var recList: ArrayList<RecList>, private val context: Context) :
    RecyclerView.Adapter<RecRVAdapter.ViewHolder>() {

    override fun getItemCount(): Int {
        return recList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_rv, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val record = recList[position]

        holder.time.text = record.time
        holder.cat.text = record.cat
        holder.loc.text = record.loc
        holder.content.text = record.content

        // 삭제 버튼 클릭 시 해당 항목 삭제
        holder.btnDelete.setOnClickListener {
            deleteRecord(record.time, record.loc, record.cat, record.content, position)
        }
    }

    // 데이터베이스에서 항목 삭제
    private fun deleteRecord(time: String, loc: String, cat: String, content: String, position: Int) {
        val myHelper = MyDBHelper.getInstance(context)
        val db: SQLiteDatabase = myHelper.writableDatabase

        val deleteQuery = "DELETE FROM recTBL WHERE rTime = ? AND rLoc = ? AND rCat = ? AND rContent = ?"
        val stmt = db.compileStatement(deleteQuery)
        stmt.bindString(1, time)
        stmt.bindString(2, loc)
        stmt.bindString(3, cat)
        stmt.bindString(4, content)

        try {
            stmt.executeUpdateDelete()
            recList.removeAt(position)  // 리스트에서 삭제
            notifyItemRemoved(position)  // RecyclerView 갱신
            (context as? RecFragment)?.loadRecordsFromDB(context)  // RecFragment 갱신
            Toast.makeText(context, "삭제되었습니다.", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(context, "삭제에 실패하였습니다.", Toast.LENGTH_SHORT).show()
        } finally {
            stmt.close()
            db.close()
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val time: TextView = itemView.findViewById(R.id.textTimeSet)
        val cat: TextView = itemView.findViewById(R.id.textCatSet)
        val loc: TextView = itemView.findViewById(R.id.textLocSet)
        val content: TextView = itemView.findViewById(R.id.textContent)
        val btnDelete: Button = itemView.findViewById(R.id.btnDelete)
    }
}