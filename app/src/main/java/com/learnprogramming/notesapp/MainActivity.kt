package com.learnprogramming.notesapp

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.ClipboardManager
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.row.view.*

class MainActivity : AppCompatActivity() {

    var listNotes = ArrayList<Note>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Load from Db
        LoadQuery("%")
    }

    override fun onResume() {
        super.onResume()
        LoadQuery("%")
    }

    private fun LoadQuery(title: String) {
        var dbManager = DbManager(this)
        val projections = arrayOf("ID","Title","Description")
        val selectionArgs = arrayOf(title)
        val cursor = dbManager.Query(projections,"Title like ?",selectionArgs,"Title")
        listNotes.clear()
        if (cursor.moveToFirst()){
            do {
                val ID = cursor.getInt(cursor.getColumnIndex("ID"))
                val Title = cursor.getString(cursor.getColumnIndex("Title"))
                val Description = cursor.getString(cursor.getColumnIndex("Description"))

                listNotes.add(Note(ID,Title,Description))

            }while (cursor.moveToNext())
        }

        //adapter
        var myNotesAdapter = MyNotesAdapter(this,listNotes)
        //set adapter
        notesLv.adapter =myNotesAdapter

        //get total no of task from listview
        val total = notesLv.count
        //actionbar
        val mActionBar = supportActionBar
        if (mActionBar != null){
            //set to actionbar as subtitle of actionbar
            mActionBar.subtitle = "You Have $total note's in list"

        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu,menu)

        //search view
        val sv: SearchView = menu!!.findItem(R.id.app_bar_search).actionView as SearchView

        val sm = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        sv.setSearchableInfo(sm.getSearchableInfo(componentName))
        sv.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                LoadQuery("%"+query+"%")
                return false

            }

            override fun onQueryTextChange(newText: String?): Boolean {
                LoadQuery("%"+newText+"%")
                return false
            }
        })
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item != null){
            when(item.itemId){
                R.id.addNote -> {
                    startActivity(Intent(this,AddNoteActivity::class.java))
                }
                R.id.action_settings -> {
                    Toast.makeText(this,"Settings", Toast.LENGTH_SHORT).show()
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    inner class MyNotesAdapter : BaseAdapter {
        var ListNotesAdapter = ArrayList<Note>()
        var context:Context? = null

        constructor(context: Context,listNotesArray:ArrayList<Note>) : super(){
            this.ListNotesAdapter = listNotesArray
            this.context = context
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            //inflate layout row
            var myView = layoutInflater.inflate(R.layout.row,null)
            var myNote = ListNotesAdapter[position]
            myView.titleTv.text = myNote.nodeName
            myView.descTv.text = myNote.nodeDesc
            //delete button click
            myView.deleteBtn.setOnClickListener{
                var dbManager = DbManager(this.context!!)
                val selectionArgs = arrayOf(myNote.nodeId.toString())
                dbManager.delete("ID=?",selectionArgs)
                LoadQuery("%")
            }
            //edit //update button click
            myView.editBtn.setOnClickListener{
                GoTOUpdateFun(myNote)
            }

            //copy btn click
            myView.copyBtn.setOnClickListener{
                //get title
                val title = myView.titleTv.text.toString()
                //get description
                val desc = myView.descTv.text.toString()
                //concatinate
                val s = title +"\n"+desc
                val cb = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                cb.text = s //add to clip board
                Toast.makeText(this@MainActivity,"Copied...",Toast.LENGTH_SHORT).show()
            }

            //share btn click
            myView.shareBtn.setOnClickListener{
                //get title
                val title = myView.titleTv.text.toString()
                //get description
                val desc = myView.descTv.text.toString()
                //concatinate
                val s = title +"\n"+desc
                val shareIntent = Intent()
                shareIntent.action = Intent.ACTION_SEND
                shareIntent.type = "text/plain"
                shareIntent.putExtra(Intent.EXTRA_TEXT,s)
                startActivity(Intent.createChooser(shareIntent,s))
            }
            return myView
        }

        override fun getItem(position: Int): Any {
            return ListNotesAdapter[position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getCount(): Int {
           return ListNotesAdapter.count()
        }
    }

    private fun GoTOUpdateFun(myNote: Note) {
        var intent = Intent(this,AddNoteActivity::class.java)
        intent.putExtra("ID",myNote.nodeId) //put id
        intent.putExtra("name",myNote.nodeName) //put name
        intent.putExtra("des",myNote.nodeDesc)//put desc
        startActivity(intent)// Start Activity

    }
}


