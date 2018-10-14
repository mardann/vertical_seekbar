package il.co.procyon.verticalseekbar

import android.animation.ArgbEvaluator
import android.content.Context
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import com.onoapps.hanan.verticalseekbar.VerticalSeekBar
import kotlinx.android.synthetic.main.activity_custom_scroll.*
import kotlin.math.roundToInt

class MainActivity : AppCompatActivity() {

    val colorInterpolatop: ArgbEvaluator = ArgbEvaluator()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_custom_scroll)


        custom_scroll_bar.setOnSeekPercentLisener { percent ->
            tv_monitor.text = "percent: ${percent.times(100).roundToInt()}%"
            custom_scroll_bar.setLabelTextColor(colorInterpolatop.evaluate(percent, Color.GREEN, Color.RED) as Int)
        }
        custom_scroll_bar.setOnSeekValueListener { value ->
            tv_value.text = "value: ${value}"
            custom_scroll_bar.setLabelText("$${value}")
        }
        custom_scroll_bar.setOnLableClickListener {
            Toast.makeText(this, "label click", Toast.LENGTH_LONG).show()
        }


    }
}
