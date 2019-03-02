package me.kmmiller.theduckypodcast.models

import android.app.Activity
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

class AttributionModel(val fileName: String) {
    var name = ""
    var licenseType = ""
    var license = ""

    fun parseNameAndType(activity: Activity) {
        // https://stackoverflow.com/questions/33779607/reading-a-txt-file-and-outputing-as-a-textview-in-android
        try {
            val reader = BufferedReader(InputStreamReader(activity.assets.open("licenses/$fileName")))

            // The first 2 lines of the file are the name and licenseType, respectively
            name = reader.readLine() ?: return
            licenseType = reader.readLine() ?: return

            reader.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun parseFromFile(activity: Activity) {
        // https://stackoverflow.com/questions/33779607/reading-a-txt-file-and-outputing-as-a-textview-in-android
        try {
            val reader = BufferedReader(InputStreamReader(activity.assets.open("licenses/$fileName")))

            // The first 2 lines of the file are the name and licenseType, respectively
            name = reader.readLine() ?: return
            licenseType = reader.readLine() ?: return
            val licenseBuilder = StringBuilder("")

            // Read the first line and append to builder
            var currentLine = reader.readLine()
            licenseBuilder.append(currentLine)

            while(currentLine != null) {
                // Add each line of the rest of the file
                currentLine = reader.readLine()
                if(currentLine != null) {
                    val nextLine = reader.readLine()
                    if(nextLine == null) {
                        // End of file, exit loop
                        licenseBuilder.append(currentLine)
                        currentLine = null
                    } else if(nextLine.isBlank() || nextLine.first() == '\n') {
                        // If next line is a blank line, then append to new line characters
                        licenseBuilder.append(currentLine).append("\n\n")
                        currentLine = nextLine
                    } else if(currentLine.isBlank() || currentLine.first() == '\n'){
                        licenseBuilder.append("\n\n")
                        currentLine = nextLine
                    } else {
                        // Otherwise just append to the end of the last line (avoids weird indentation problems
                        licenseBuilder.append(currentLine).append(' ')
                        currentLine = nextLine
                    }

                    if(currentLine != null ) licenseBuilder.append(currentLine)
                }
            }
            license = licenseBuilder.toString()

            reader.close()
        } catch(e: IOException) {
            e.printStackTrace()
        }
    }
}