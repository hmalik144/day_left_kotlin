package com.appttude.h_mal.days_left_kotlin

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.fragment.app.FragmentManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.appttude.h_mal.days_left_kotlin.FirebaseClass.Companion.SHIFT_FIREBASE
import com.appttude.h_mal.days_left_kotlin.FirebaseClass.Companion.USER_FIREBASE
import com.appttude.h_mal.days_left_kotlin.FirebaseClass.Companion.auth
import com.appttude.h_mal.days_left_kotlin.FirebaseClass.Companion.mDatabase
import com.appttude.h_mal.days_left_kotlin.Login.FullscreenActivity
import com.appttude.h_mal.days_left_kotlin.Objects.AbnObject
import com.appttude.h_mal.days_left_kotlin.Objects.ShiftObject
import com.google.android.material.navigation.NavigationView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_drawer_main.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.nav_header_main.view.*

class MainActivity : AppCompatActivity() {
    companion object{
        var shiftList = ArrayList<ShiftObject>()
    }

    lateinit var fragmentManager: FragmentManager
    lateinit var progBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_drawer_main)
        //initialising views as val

        val toolBar: androidx.appcompat.widget.Toolbar = toolbar
        progBar = progressBar2

        //setup backstack change listener
        fragmentManager = supportFragmentManager
        fragmentManager.addOnBackStackChangedListener(backStackChangedListener)

        //set toolbar
        setSupportActionBar(toolBar)

        //setup fab
        fab.setOnClickListener{
            val intent = Intent(this, AddShiftActivity::class.java)
            startActivity(intent)
        }

        //setup drawer layout
        val drawer: DrawerLayout = drawer_layout
        val toggle = ActionBarDrawerToggle(this,drawer,toolBar,R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer.addDrawerListener(toggle)
        toggle.syncState()

        //setup naviation view
        val navigationView: NavigationView = nav_view
        navigationView.setNavigationItemSelectedListener { menuItem ->

            menuItem.itemId.let { id ->
                if (id == R.id.nav_camera){
                    val intent = Intent(this,ChangeUserDetailsActivity::class.java)
                    startActivity(intent)
                }
            }

            drawer.closeDrawer(GravityCompat.START)

            true
        }

        //Setup drawer
        SetupDrawer(navigationView)

        //initialise data for fragments
        initiateFragment()
    }

    private val onNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_home -> {
                fragmentManager.beginTransaction().replace(R.id.container,HomeFragment()).commit()
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_list -> {
                fragmentManager.beginTransaction().replace(R.id.container,FragmentList()).commit()
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_tools -> {
                fragmentManager.beginTransaction().replace(R.id.container,FragmentTools()).commit()
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    private val backStackChangedListener = FragmentManager.OnBackStackChangedListener {
        fragmentManager.fragments.get(0).javaClass.simpleName.let {fragmentName ->
            lateinit var name:String

            when (fragmentName){
                "HomeFragment" -> {
                    name = "Home"
                }
                "ListFragment" -> {
                    name = "List"
                }
                "ToolsFragment" -> {
                    name = "Tools"
                }
                else -> {
                    name = getString(R.string.app_name)
                }
            }

            setTitle(name)
        }
    }

    override fun setTitle(title: CharSequence?) {
        toolbar.setTitle(title)
    }

    fun SetupDrawer(navigationView: NavigationView){
        val header: View = navigationView.getHeaderView(0)

        checkNotNull(auth).currentUser.let { user ->
            checkNotNull(user?.email).let {email ->
                header.driver_email.setText(email)
            }
            checkNotNull(user?.displayName).let {name ->
                header.driver_name.setText(name)
            }

            Picasso.get().load(user?.photoUrl).placeholder(R.mipmap.ic_launcher_round)
                .into(header.profileImage)
        }

        logout.setOnClickListener{
            auth.signOut()
            val intent = Intent(this, FullscreenActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    fun initiateFragment(){
        val uid = auth.uid as String
        progBar.visibility = View.VISIBLE

        mDatabase.child(USER_FIREBASE).child(uid).child(SHIFT_FIREBASE).addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                progBar.visibility = View.GONE
            }

            override fun onDataChange(p0: DataSnapshot) {
                progBar.visibility = View.GONE

                if(shiftList.isNotEmpty()){
                    shiftList.clear()
                }

                for(postSnapshot in p0.children){
                    shiftList.add(postSnapshot.getValue(ShiftObject::class.java)!!)
                }

                Log.i("firebase", "shiftlist count = " + shiftList.size)

                if (shiftList.size > 0){
                    //apply navigation listener on bottom bar navigation view
                    navigation.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)
                    //add first fragment
                    fragmentManager.beginTransaction().replace(R.id.container,HomeFragment()).commit()
                    navigation.setSelectedItemId(R.id.navigation_home)
                }else{
                    Toast.makeText(getBaseContext() , "Cannot load data", Toast.LENGTH_SHORT).show()
                }

            }
        })
    }

}
