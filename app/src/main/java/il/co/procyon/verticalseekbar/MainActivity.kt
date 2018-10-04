package il.co.procyon.verticalseekbar

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import com.onoapps.hanan.verticalseekbar.VerticalSeekBar
import kotlinx.android.synthetic.main.activity_custom_scroll.*
import kotlin.math.roundToInt

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_custom_scroll)

        custom_scroll_bar.setOnSeekPercentLisener { percent ->
            tv_monitor.text = "percent: ${percent.times(100).roundToInt()}%"
        }
        custom_scroll_bar.setOnSeekValueListener { value ->
            tv_value.text = "value: ${value}"
        }


    }
}
