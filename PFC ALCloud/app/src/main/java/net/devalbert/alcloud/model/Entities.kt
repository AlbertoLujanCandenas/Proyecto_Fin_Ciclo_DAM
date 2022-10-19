package net.devalbert.firebasestorage.model

import java.io.File
import java.io.Serializable

data class Respuesta (
    var archivo: List<Archivo>,
    var items: List<Items>
    )

data class Archivo(
    var kind : String,
    var items: Items
):Serializable
data class FileMetadata(
    var firebaseStorageDownloadTokens: String
):Serializable
data class Items (
    var id:String,
    var selfLink:String,
    var mediaLink:String,
    var name:String,
    var size:String,
    var contentType: String,
    var metadata: FileMetadata,
    var bucket: String
        ): Serializable