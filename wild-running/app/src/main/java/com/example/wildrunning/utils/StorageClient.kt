package com.example.wildrunning.utils

import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import com.example.wildrunning.activity.LoginActivity
import com.example.wildrunning.activity.MainActivity
import com.example.wildrunning.adapter.RunAdapter
import com.example.wildrunning.data.Run
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import java.io.File

object StorageClient {

    /**
     * Uploads a run picture to the storage
     */
    fun uploadFile(
        runDate: String,
        runStartTime: String,
        file: File,
        callBack: (String, File) -> Unit,
        failureCallback: (Exception) -> Unit
    ) {
        val dirName = (runDate + runStartTime)
            .replace("/", "")
            .replace(":", "")

        val fileName = "$dirName-${MainActivity.photoNumber}"

        val storage =
            FirebaseStorage.getInstance()
                .getReference("images/${LoginActivity.userEmail}/$dirName/$fileName")

        storage.putFile(Uri.fromFile(file))
            .addOnSuccessListener {
                callBack.invoke(dirName, file)
            }
            .addOnFailureListener {
                failureCallback.invoke(it)
            }
    }

    /**
     * Gets an image from the store
     */
    fun getImage(
        run: Run,
        holder: RunAdapter.MyViewHolder,
        failureCallback: (Exception) -> Unit
    ) {
        val path = run.lastImage
        val storage = FirebaseStorage.getInstance().reference.child(path!!)
        val localFile = File.createTempFile("tempImage", "jpg")
        storage.getFile(localFile)
            .addOnSuccessListener {
                val bitmap = BitmapFactory.decodeFile(localFile.absolutePath)
                val percent = 160 / bitmap.width.toFloat()
                MyLayoutManager.setLinearLayoutHeight(
                    holder.lyPicture,
                    (bitmap.height * percent).toInt()
                )
                holder.ivPicture.setImageBitmap(bitmap)

            }
            .addOnFailureListener {
                failureCallback.invoke(it)
            }
    }

    /**
     * Deletes a run pictures from the storage
     */
    fun deletePicture(runId: String) {
        val folderId = runId.subSequence(LoginActivity.userEmail.length, runId.length).toString()
        val storage = Firebase.storage
        val refList = storage.reference.child("images/${LoginActivity.userEmail}/$folderId")
        refList.listAll()
            .addOnSuccessListener {
                it.items.forEach { item ->
                    val storageRef = storage.reference
                    val deleteRef = storageRef.child(item.path)
                    deleteRef.delete()
                }
            }
            .addOnFailureListener {
                Log.e(this.javaClass.name, this::deletePicture.name, it)
            }
    }
}