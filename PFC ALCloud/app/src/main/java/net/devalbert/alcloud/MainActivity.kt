package net.devalbert.alcloud

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import net.azarquiel.caravanretrofit.viewmodel.MainViewModel
import net.azarquiel.recyclerviewitemss.adapter.CustomAdapter
import net.devalbert.firebasestorage.model.Items

enum class ProviderType{
    BASIC,
    GOOGLE
}

class MainActivity : AppCompatActivity() {
    private lateinit var prefs: SharedPreferences.Editor
    private lateinit var email: String
    private lateinit var username: String
    private lateinit var storageRef: StorageReference
    private lateinit var storageReference: FirebaseStorage
    private lateinit var archivos: List<Items>
    private lateinit var viewModel: MainViewModel
    private lateinit var rvArchivos: RecyclerView
    private lateinit var adapter: CustomAdapter
    private val fileResult = 1
    private lateinit var uploadImageView: FloatingActionButton
    private lateinit var ivMostrarArchivos: ImageView
    private lateinit var progreso: ProgressBar
    private lateinit var subtitulo: TextView
    private lateinit var llFotos: LinearLayout
    private lateinit var ivLogout: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        ivLogout = findViewById(R.id.ivLogout)
        ivLogout.setOnClickListener {
            dialogoCerrarSesion()
        }

        uploadImageView = findViewById(R.id.uploadImageView)
        uploadImageView.setOnClickListener {
            fileManager()
        }
        ivMostrarArchivos = findViewById(R.id.ivMostrarFotos)
        ivMostrarArchivos.setOnClickListener {
            initRV()
            Toast.makeText(this, "Actualizando lista de archivos", Toast.LENGTH_SHORT).show()
        }

        progreso = findViewById(R.id.progreso)
        progreso.visibility = View.INVISIBLE

        subtitulo = findViewById(R.id.textView2)
        subtitulo.text="Tus archivos"
        rvArchivos = findViewById(R.id.rvArchivos)
        llFotos = findViewById(R.id.llFotos)
        llFotos.visibility = View.VISIBLE

        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)

        //realizando un bundle tambien podemos obtener los extras de la anterior pantalla
        val bundle = intent.extras
        email = bundle?.getString("email")!!
        val provider = bundle?.getString("provider")
        username = bundle?.getString("username")!!

        //Guardar la informacion de inicio de sesion en un SP
        prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
        prefs.putString("email", email)
        prefs.putString("provider", provider)
        prefs.putString("username", username)
        prefs.apply()

        initRV()
    }


    private fun initRV() {
        if(username == ""){
            username = email.replaceAfter("@","")
            prefs.putString("username", username)
            prefs.apply()
        }
        adapter = CustomAdapter(this, R.layout.row_archivos, username)
        rvArchivos.adapter = adapter
        rvArchivos.layoutManager = LinearLayoutManager(this)
        getArchivos()
    }

    private fun getArchivos(){
        viewModel.getArchivos().observe(this) { it ->
            it?.let {
                archivos = it
                showArchivos()
            }
        }
    }

    private fun showArchivos() {
            adapter.setItemss(archivos)
    }

    //Navegar al adminsitrador de archivos para obtener el fichero (URI)
    private fun fileManager() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        }else{
            Toast.makeText(this, "Se necesita una version de Android mayor para realizar esta acción", Toast.LENGTH_SHORT).show()
        }
        intent.type = "*/*"
        startActivityForResult(intent, fileResult)
    }

    //Obtener la URI del archivo seleccionado para subirlo
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == fileResult) {
            if (resultCode == RESULT_OK && data != null) {
                val clipData = data.clipData

                if (clipData != null) {
                    for (i in 0 until clipData.itemCount) {
                        val uri = clipData.getItemAt(i).uri
                        uri?.let { fileUpload(it) }
                    }
                } else {
                    val uri = data.data
                    uri?.let { fileUpload(it) }
                }
            }
        }
    }

    //Subir el archivo a FireBase Storage
    private fun fileUpload(mUri: Uri) {
        //Crear carpeta para cada uno de los usuarios
        val folder: StorageReference = FirebaseStorage.getInstance().reference.child(username)
        val path = mUri.lastPathSegment.toString()
        val fileName: StorageReference = folder.child(path.substring(path.lastIndexOf('/') + 1))
        progreso.visibility = View.VISIBLE
        llFotos.visibility = View.INVISIBLE
        subtitulo.text = "Subiendo archivos"
        fileName.putFile(mUri).addOnSuccessListener {
            progreso.visibility = View.INVISIBLE
            subtitulo.text = "Tus archivos"
            initRV()
            llFotos.visibility = View.VISIBLE
        }.addOnFailureListener {
            progreso.visibility = View.INVISIBLE
            subtitulo.text = "Tus archivos"
            Toast.makeText(this, "Error en la subida de archivos", Toast.LENGTH_SHORT).show()
        }
    }
    //Mostrar el fichero en otra pantalla para ampliar la vista
    fun onClickFichero(v: View){
        var ficheroSeleccionado = v.tag as Items
        subtitulo.text = "Mostrando archivos"

        storageReference = FirebaseStorage.getInstance()
        storageRef = storageReference.getReferenceFromUrl("gs://${ficheroSeleccionado.bucket}/${ficheroSeleccionado.name}")

        val intent = Intent(this, FicheroPulsadoActivity::class.java)
        intent.putExtra("ficheroPulsado",ficheroSeleccionado)
        startActivity(intent)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            dialogoSalir()
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    private fun dialogoSalir() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("SALIR.")
        builder.setMessage("Quiere salir de ${title}")
        builder.setPositiveButton("Salir") { dialog, which ->
            finish()
        }
        builder.setNegativeButton("Volver") { dialog, which ->
        }
        builder.show()
    }
    private fun dialogoCerrarSesion() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("SALIR.")
        builder.setMessage("Quiere cerrar la sesion en ${title}")
        builder.setPositiveButton("Salir") { dialog, which ->
            prefs.remove("email")
            prefs.remove("provider")
            prefs.remove("username")
            prefs.apply()
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
        builder.setNegativeButton("Volver") { dialog, which ->
        }
        builder.show()
    }
}