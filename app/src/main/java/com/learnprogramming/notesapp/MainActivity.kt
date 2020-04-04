package com.learnprogramming.notesapp

import android.app.AlertDialog
import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
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

    //shared preference
    var mSharedPref:SharedPreferences?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mSharedPref = this.getSharedPreferences("My_Data", Context.MODE_PRIVATE)

        //load sorting technique as selected before default setting will be newest
        val mSorting = mSharedPref!!.getString("Sort","newest")
        when(mSorting){
            "newest" -> LoadQueryNewest("%")
            "oldest" -> LoadQueryOldest("%")
            "ascending" -> LoadQueryAscending("%")
            "Descending" -> LoadQueryDecending("%")
        }

        //Load from Db
//        LoadQueryAscending("%")
    }

    override fun onResume() {
        super.onResume()
       // LoadQueryAscending("%")
        val mSorting = mSharedPref!!.getString("Sort","newest")
        when(mSorting){
            "newest" -> LoadQueryNewest("%")
            "oldest" -> LoadQueryOldest("%")
            "ascending" -> LoadQueryAscending("%")
            "Descending" -> LoadQueryDecending("%")
        }
    }

    private fun LoadQueryAscending(title: String) {
        var dbManager = DbManager(this)
        val projections = arrayOf("ID","Title","Description")
        val selectionArgs = arrayOf(title)
        //sort by title
        val cursor = dbManager.Query(projections,"Title like ?",selectionArgs,"Title")
        listNotes.clear()
        //Ascending
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

    private fun LoadQueryDecending(title: String) {
        var dbManager = DbManager(this)
        val projections = arrayOf("ID","Title","Description")
        val selectionArgs = arrayOf(title)
        //sort by title
        val cursor = dbManager.Query(projections,"Title like ?",selectionArgs,"Title")
        listNotes.clear()
        //Decending
        if (cursor.moveToLast()){
            do {
                val ID = cursor.getInt(cursor.getColumnIndex("ID"))
                val Title = cursor.getString(cursor.getColumnIndex("Title"))
                val Description = cursor.getString(cursor.getColumnIndex("Description"))

                listNotes.add(Note(ID,Title,Description))

            }while (cursor.moveToPrevious())
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

    private fun LoadQueryNewest(title: String) {
        var dbManager = DbManager(this)
        val projections = arrayOf("ID","Title","Description")
        val selectionArgs = arrayOf(title)
        //sort by ID
        val cursor = dbManager.Query(projections,"ID like ?",selectionArgs,"ID")
        listNotes.clear()
        //Newest
        if (cursor.moveToLast()){
            do {
                val ID = cursor.getInt(cursor.getColumnIndex("ID"))
                val Title = cursor.getString(cursor.getColumnIndex("Title"))
                val Description = cursor.getString(cursor.getColumnIndex("Description"))

                listNotes.add(Note(ID,Title,Description))

            }while (cursor.moveToPrevious())
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

    private fun LoadQueryOldest(title: String) {
        var dbManager = DbManager(this)
        val projections = arrayOf("ID","Title","Description")
        val selectionArgs = arrayOf(title)
        //sort by ID
        val cursor = dbManager.Query(projections,"ID like ?",selectionArgs,"ID")
        listNotes.clear()
        //Newest
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
                LoadQueryAscending("%"+query+"%")
                return false

            }

            override fun onQueryTextChange(newText: String?): Boolean {
                LoadQueryAscending("%"+newText+"%")
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
                R.id.action_sort -> {
                    //show sorting dialoge
                    ShowSortDialoge()
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun ShowSortDialoge() {
        //list of sorting options
        val sortOptions = arrayOf("Newest","Oldest","Title(Ascending)","Title(Descending)")
        var mBuilder = AlertDialog.Builder(this)
        mBuilder.setTitle("Sort by")
        mBuilder.setIcon(R.drawable.ic_action_sort)
        mBuilder.setSingleChoiceItems(sortOptions,-1){
            dialogInterface, i ->
            if (i==0){
                //Newest
                Toast.makeText(this,"NEWEST",Toast.LENGTH_SHORT).show()
                val editor = mSharedPref!!.edit()
                editor.putString("Sort","newest") //where 'sort' is key && 'newest' is value
                editor.apply() //apply /save the value in our shared preference
                LoadQueryNewest("%")
            }
            if (i==1){
                //Oldest
                Toast.makeText(this,"OLDEST",Toast.LENGTH_SHORT).show()
                val editor = mSharedPref!!.edit()
                editor.putString("Sort","oldest") //where 'sort' is key && 'oldest' is value
                editor.apply() //apply /save the value in our shared preference
                LoadQueryOldest("%")
            }
            if (i==2){
                //Title(Asending)
                Toast.makeText(this,"Title(Ascending)",Toast.LENGTH_SHORT).show()
                val editor = mSharedPref!!.edit()
                editor.putString("Sort","ascending") //where 'sort' is key && 'ascending' is value
                editor.apply() //apply /save the value in our shared preference
                LoadQueryAscending("%")
            }
            if (i==3){
                //Title(Desending)
                Toast.makeText(this,"Title(Descending)",Toast.LENGTH_SHORT).show()
                val editor = mSharedPref!!.edit()
                editor.putString("Sort","descending") //where 'sort' is key && 'decending' is value
                editor.apply() //apply /save the value in our shared preference
                LoadQueryDecending("%")
            }
            dialogInterface.dismiss()
        }

        val mDialog = mBuilder.create()
        mDialog.show()

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
                LoadQueryAscending("%")
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


