package net.devalbert.firebasestorage.api

import kotlinx.coroutines.Deferred
import net.devalbert.firebasestorage.model.Respuesta
import retrofit2.Response
import retrofit2.http.*

interface ArchivosService {

    // No necesita nada para trabajar
    @GET("alcloud-e5e3b.appspot.com/o")
    fun getArchivos(): Deferred<Response<Respuesta>>
}

/*interface DescargarArchivos{
    @GET("fir-storage-906bf.appspot.com/o" )
    fun getArchivoDescargado(
        @Query("alt") alt:String,
        @Query("token") token:String): Deferred<Response<Respuesta>>
}*/
