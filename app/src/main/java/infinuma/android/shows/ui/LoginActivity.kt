package infinuma.android.shows.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import infinuma.android.shows.R

class LoginActivity : AppCompatActivity() {

    /*
    * 1. Put the app in background and move it back to foreground
      -> onPause() -> onStop() -> onRestart() -> onStart() -> onResume()

    * 2. Kill the app
     -> onPause() -> onStop() -> onDestroy()

    * 3. Lock the phones screen and unlock it
      -> onPause() -> onStop() -> onRestart() -> onStart() -> onResume()
    */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        Log.d("Academy", "On create has been called")
    }

    override fun onStart() {
        super.onStart()
        Log.d("Academy", "On start has been called")
    }
    override fun onResume() {
        super.onResume()
        Log.d("Academy", "On resume has been called")
    }
    override fun onPause() {
        Log.d("Academy", "On pause has been called")
        super.onPause()
    }

    override fun onStop() {
        Log.d("Academy", "On stop has been called")
        super.onStop()
    }

    override fun onDestroy() {
        Log.d("Academy", "On destroy has been called")
        super.onDestroy()
    }


    override fun onRestart() {
        Log.d("Academy", "On restart has been called")
        super.onRestart()
    }
}