package infinuma.android.shows.util

import android.net.Uri
import androidx.core.net.toUri
import java.io.File
import java.io.FileOutputStream
import java.net.URL
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

suspend fun saveImage(url: String, imageDir: File, id: String): Uri? {
    return withContext(Dispatchers.IO) {
        try {
            val imageFile = File(imageDir, "showImage${id}.jpg")
            val localUri = imageFile.toUri()

            if(!imageFile.exists()) {
                val inputStream = URL(url).openStream()
                val outputStream = FileOutputStream(File(localUri.path ?: ""))
                inputStream.copyTo(outputStream)
                inputStream.close()
                outputStream.close()
            }

            localUri
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
