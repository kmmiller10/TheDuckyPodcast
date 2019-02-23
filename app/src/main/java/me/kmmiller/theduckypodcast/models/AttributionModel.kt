package me.kmmiller.theduckypodcast.models

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

class AttributionModel {
    var name = ""
    var licenseType = ""
    var license = ""

    fun parseFromFile(activity: Activity, fileName: String) {
        // https://stackoverflow.com/questions/33779607/reading-a-txt-file-and-outputing-as-a-textview-in-android

        try {
            val reader = BufferedReader(InputStreamReader(activity.assets.open("licenses/$fileName")))

            // The first 2 lines of the file are the name and licenseType, respectively
            name = reader.readLine() ?: return
            licenseType = reader.readLine() ?: return
            val licenseBuilder = StringBuilder("")

            var line = reader.readLine()
            while(line != null) {
                line = reader.readLine()
                licenseBuilder.append(line).append('\n')
            }
            license = licenseBuilder.toString()

            reader.close()
        } catch(e: IOException) {
            e.printStackTrace()
        }
    }
}