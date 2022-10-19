package net.devalbert.firebasestorage.api

import net.devalbert.firebasestorage.model.Items

class MainRepository() {
    val service = WebAccess.archivosService
    //val serviceDescarga = WebAccess.descargarArchivos

    suspend fun getArchivos(): List<Items> {
        val webResponse = service.getArchivos().await()
        if (webResponse.isSuccessful) {
            return webResponse.body()!!.items
        }
        return emptyList()
    }

//    suspend fun getArchivoDescargado(alt:String, token:String): Items{
//        val webResponse = serviceDescarga.getArchivoDescargado(alt, token).await()
//        if (webResponse.isSuccessful) {
//            return webResponse.body()!!.
//        }
//        return emptyList()
//    }
}