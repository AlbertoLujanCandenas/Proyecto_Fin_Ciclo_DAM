package net.devalbert.firebasestorage.api

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object WebAccess {

    val archivosService: ArchivosService by lazy {

        //Parsea el JSON en objetos
        val retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .baseUrl("https://storage.googleapis.com/storage/v1/b/")
            .build()

        return@lazy retrofit.create(ArchivosService::class.java)
    }

/*    val descargarArchivos: DescargarArchivos by lazy {

        //Parsea el JSON en objetos
        val retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .baseUrl("https://firebasestorage.googleapis.com/v0/b/")
            .build()

        return@lazy retrofit.create(DescargarArchivos::class.java)
    }*/
}