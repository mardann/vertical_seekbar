package il.co.procyon.verticalseekbar

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import com.onoapps.hanan.verticalseekbar.VerticalSeekBar
import kotlin.math.roundToInt

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_custom_scroll)

        val verticalSeekbar = findViewById<VerticalSeekBar>(R.id.custom_scroll_bar)
//        verticalSeekbar.setOnLableClickListener { Toast.makeText(this, "Label clicked", Toast.LENGTH_SHORT).show() }
        verticalSeekbar.setOnSeekPercentLisener { percent ->
            findViewById<TextView>(R.id.tv_monitor).setText("percent: ${percent.times(100).roundToInt()}%")
        }
        verticalSeekbar.setOnSeekValueListener { value ->
            findViewById<TextView>(R.id.tv_value).setText("value: ${value}")
        }
    }
}
