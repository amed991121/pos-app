package com.savent.erp.domain.usecase


import com.savent.erp.R
import com.savent.erp.utils.Resource
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class SaveReceiptFileUseCase() {

    operator fun invoke(file: File, note: String): Resource<File> {
        return try {
            val fos= FileOutputStream(file)
            fos.write(note.toByteArray())
            fos.close()
            Resource.Success(file)
        }catch (e: IOException){
            e.printStackTrace()
            Resource.Error(R.string.save_note_error)
        }

    }
}