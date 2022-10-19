package net.devalbert.alcloud

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.SystemClock
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashScreen : AppCompatActivity() {

    private lateinit var animacion2: Animation
    private lateinit var animacion1: Animation

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        animacion1 = AnimationUtils.loadAnimation(this, R.anim.desplazamiento_arriba)
        animacion2 = AnimationUtils.loadAnimation(this, R.anim.desplazamiento_abajo)

        var tvTitulo: TextView = findViewById(R.id.tvTitutlo)
        var tvDescripcion: TextView = findViewById(R.id.tvSubtitulo)
        var iv: ImageView = findViewById(R.id.imageView)

        iv.setImageResource(R.mipmap.logo)

        tvTitulo.setAnimation(animacion2)
        tvDescripcion.setAnimation(animacion1)
        iv.setAnimation(animacion1)

        GlobalScope.launch() {
            SystemClock.sleep(2000)
            launch(Main) {
               ActivityMain()
            }
        }

    }

    private fun ActivityMain() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

}