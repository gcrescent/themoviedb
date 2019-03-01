package application.me.baseapplication.helper

import android.content.Context
import android.graphics.Point
import android.view.WindowManager

class DimensionHelper {

    companion object {

        fun getScreenWidth(context: Context): Int {
            val columnWidth: Int
            val wm = context
                    .getSystemService(Context.WINDOW_SERVICE) as WindowManager
            val display = wm.defaultDisplay

            val point = Point()
            display.getSize(point)
            columnWidth = point.x
            return columnWidth
        }
    }
}