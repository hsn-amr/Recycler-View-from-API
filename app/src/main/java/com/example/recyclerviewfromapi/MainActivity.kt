package com.example.recyclerviewfromapi

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Adapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.*
import org.json.JSONArray
import java.lang.Exception
import java.net.URL

class MainActivity : AppCompatActivity() {

    lateinit var adapter: RV
    lateinit var rvMain: RecyclerView

    var names = ArrayList<String>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        rvMain = findViewById(R.id.rvMain)
        adapter = RV(names)
        rvMain.adapter = adapter
        rvMain.layoutManager = LinearLayoutManager(this)
        requestApi()
    }

    private fun requestApi(){
        var data = ""
        CoroutineScope(Dispatchers.IO).launch{
            data = async {
                fetchData()
            }.await()
            if(data.isNotEmpty()){
                val array = JSONArray(data)
                val len = array.length()
                withContext(Dispatchers.Main){
                    for (i in 0 until len-1){
                        names.add(array.getJSONObject(i).getString("name"))
                    }
                    rvMain.adapter!!.notifyDataSetChanged()
                }
            }
        }
    }

    private fun fetchData(): String {
        var response = ""
        try {
            response = URL("https://dojo-recipes.herokuapp.com/people/").readText(Charsets.UTF_8)
        }catch (e: Exception){
            Log.e("TAG", "$e")
        }
        return response
    }
}