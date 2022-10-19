package net.azarquiel.recyclerviewitemss.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnLongClickListener
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import net.devalbert.alcloud.R
import net.devalbert.firebasestorage.model.Items


/**
 * Created by pacopulido on 9/10/18.
 */
class CustomAdapter(
    val context: Context,
    val layout: Int,
    val username: String
                    ) : RecyclerView.Adapter<CustomAdapter.ViewHolder>() {

    private var dataList: List<Items> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val viewlayout = layoutInflater.inflate(layout, parent, false)
        return ViewHolder(viewlayout, context)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = dataList[position]
        holder.bind(item,username)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    internal fun setItemss(itemss: List<Items>) {
        this.dataList = itemss
        notifyDataSetChanged()
    }


    class ViewHolder(viewlayout: View, val context: Context) : RecyclerView.ViewHolder(viewlayout) {
        fun bind(dataItem: Items, username: String){
            // itemview es el item de diseño
            // al que hay que poner los datos del objeto dataItem

            val tvRowNombreFoto = itemView.findViewById(R.id.tvRowNombreFoto) as TextView
            val ivRowFoto = itemView.findViewById(R.id.ivRowFoto) as ImageView
            val tvContentType = itemView.findViewById(R.id.tvContentType) as TextView
            val cvFichero = itemView.findViewById(R.id.cvFichero) as CardView

            tvRowNombreFoto.text = dataItem.name
            if(dataItem.contentType.contains(".document")){
                tvContentType.text = "DOCUMENTO DE TEXTO"
            }else if(dataItem.contentType.contains(".sheet")) {
                tvContentType.text = "HOJA DE CALCULO"
            }else if(dataItem.contentType.contains("image")){
                tvContentType.text = "IMG"
            }else if(dataItem.contentType.contains("pdf")){
                tvContentType.text = "PDF"
            }
            // foto de internet a traves de Picasso
            Picasso.get().load("${dataItem.mediaLink}").into(ivRowFoto)

            cvFichero.setOnLongClickListener {
                if (true) {
                    val builder = AlertDialog.Builder(context)
                    builder.setTitle("Eliminar.")
                    builder.setMessage("¿Desea eliminar este archivo?")
                    builder.setPositiveButton("Eliminar") { dialog, which ->
                        val storageReference = FirebaseStorage.getInstance()
                        var fileRef = storageReference.getReferenceFromUrl("gs://${dataItem.bucket}/${dataItem.name}")
                        fileRef.delete()
                        Toast.makeText(context, "Eliminado correctamente", Toast.LENGTH_SHORT).show()
                    }
                    builder.setNegativeButton("Cancelar") { dialog, which ->
                    }
                    builder.show()

                }
                true
            }

            //Muestra solo los datos que tengan como nombre Alberto -> PARA PARTE DEL LOGIN
            if(dataItem.name.contains(username)){
                tvRowNombreFoto.text = dataItem.name
                tvContentType.text = dataItem.contentType
                // foto de internet a traves de Picasso
                Picasso.get().load("${dataItem.mediaLink}").into(ivRowFoto)
            }else{
                cvFichero.visibility = View.INVISIBLE
            }
            itemView.tag = dataItem
        }

    }
}