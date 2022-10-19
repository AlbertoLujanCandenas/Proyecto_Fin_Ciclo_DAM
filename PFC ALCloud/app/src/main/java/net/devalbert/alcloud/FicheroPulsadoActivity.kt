package net.devalbert.alcloud

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.Environment.DIRECTORY_DOCUMENTS
import android.os.Environment.DIRECTORY_DOWNLOADS
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import net.devalbert.firebasestorage.model.Items

class FicheroPulsadoActivity : AppCompatActivity() {
    private lateinit var ficheroPulsado: Items
    private lateinit var ivFoto: ImageView
    private lateinit var fabDescargar: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fichero_pulsado)

        ivFoto = findViewById(R.id.ivFoto)
        fabDescargar = findViewById(R.id.fabDescargar)

        ficheroPulsado = intent.getSerializableExtra("ficheroPulsado") as Items
        Picasso.get().load("${ficheroPulsado.mediaLink}").into(ivFoto)

        fabDescargar.setOnClickListener{
            descargarArchivo()
        }
    }

    fun descargarArchivo(){
        val storageReference = FirebaseStorage.getInstance()
        var storageRef = storageReference.getReferenceFromUrl("gs://${ficheroPulsado.bucket}/${ficheroPulsado.name}")

        storageRef.downloadUrl.addOnSuccessListener{
            var nombreFichero = ficheroPulsado.name.removeSuffix("User/")
            var extensionFichero = ficheroPulsado.contentType
            if(extensionFichero.contains("image/jpeg")){
                extensionFichero = ficheroPulsado.contentType.removePrefix("image/")
            }else if(extensionFichero.contains("application/pdf")){
                extensionFichero = ficheroPulsado.contentType.removePrefix("application/")
            }else if(extensionFichero.contains(".sheet")){
                extensionFichero = "sheet"
            }else if(extensionFichero.contains(".document")){
                extensionFichero = "docx"
            }
            else if(extensionFichero.contains("video/mp4")){
                extensionFichero = ficheroPulsado.contentType.removePrefix("video/")
            }
            downloadFiles(this,nombreFichero, extensionFichero, DIRECTORY_DOWNLOADS,it)
            Toast.makeText(this, "Descargando...", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener{
            Toast.makeText(this, "No se puede descargar", Toast.LENGTH_SHORT).show()
        }
    }

    private fun downloadFiles(context: Context, fileName:String, fileExtension: String, destinationDirectory:String, uri:Uri) {
        // Crear una tarea de descarga, downloadUrl es el enlace de descarga
        val request = DownloadManager.Request(uri)
        // Especificar la ruta de descarga y el nombre del archivo con su extension
        request.setDestinationInExternalFilesDir(context, destinationDirectory, "${fileName}.${fileExtension}")
        // Obtener el administrador de descargas
        val downloadManager = this.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        // Agregar la tarea de descarga a la cola del sistema para descargarlo
        downloadManager.enqueue(request)
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
    }
}