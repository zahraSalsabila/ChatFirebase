package com.asysyifazahrasalsabila.chatfirebase

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    var name: String? = null
    val db = FirebaseDatabase.getInstance().reference.root
    var arrayAdapter: ArrayAdapter<String>? = null
    val listOfRoom = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        arrayAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, listOfRoom)
        listRoom.adapter = arrayAdapter

        requestUserName()

        btnAddRoom.setOnClickListener {
            val map = HashMap<String, Any>()
            map[edtNameRoom.text.toString()] = ""
            db.updateChildren(map)

            edtNameRoom.setText("")
        }

        db.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {
                val set = HashSet<String>()
                val dataChat = p0.children.iterator()
                while (dataChat.hasNext()){
                    set.add((dataChat.next() as DataSnapshot).key.toString())
                }
                listOfRoom.clear()
                listOfRoom.addAll(set)

                arrayAdapter!!.notifyDataSetChanged()
            }
        })

        listRoom.onItemClickListener = AdapterView.OnItemClickListener {
                parent, view, position, id ->
            val i = Intent(applicationContext, ChatRoomActivity::class.java)
            i.putExtra("room_name", (view as TextView).text.toString())
            i.putExtra("user_name", name)
            startActivity(i)
        }
    }

    private fun requestUserName() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Enter Name")
        val input = EditText(this)
        builder.setView(input)

        builder.setPositiveButton("OK"){
            dialog, which ->
            name = input.text.toString()
        }
        builder.setNegativeButton("Cancel"){
            dialog, which ->
            dialog.cancel()
            requestUserName()
        }

        builder.create().show()
    }
}
