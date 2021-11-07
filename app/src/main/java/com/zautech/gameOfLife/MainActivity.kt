package com.zautech.gameOfLife

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit
import kotlin.random.Random

class MainActivity : AppCompatActivity() {
    private lateinit var data: MutableList<MutableList<Boolean>>
    private var disposable: Disposable? = null
    private var state = STATE.STOPPED

    private val gridView: GridView
        get() = findViewById(R.id.grid_view)

    private val reset: Button
        get() = findViewById(R.id.reset)

    private val stopStart: Button
        get() = findViewById(R.id.stop_start)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        data = generateGridData()
        gridView.also {
            it.setGridData(data)
            it.invalidate()
            it.onClickListener = object : GridView.GridViewClicked {
                override fun onTouched(x: Int, y: Int) {
                    data.getOrNull(x)?.getOrNull(y)?.also { value ->
                        data[x][y] = !value
                    }
                    it.setGridData(data)
                    it.invalidate()
                }
            }
        }
        reset.setOnClickListener {
            gridView.also {
                data = generateGridData()
                it.setGridData(data)
                it.invalidate()
            }
        }
        stopStart.setOnClickListener {
            (it as? Button)?.text = if (state == STATE.RUNNING) "Start" else "Stop"
            state = if (state == STATE.RUNNING) STATE.STOPPED else STATE.RUNNING
        }
        findViewById<Button>(R.id.clear).setOnClickListener {
            for (i in 0 until data.size) {
                for (j in 0 until data[i].size) {
                    data[i][j] = false
                    gridView.setGridData(data)
                    gridView.invalidate()
                }
            }
        }
        findViewById<Button>(R.id.shift).setOnClickListener {
            state = STATE.STOPPED
            gridView.also {
                data = recalculate(data)
                it.setGridData(data)
                it.invalidate()
            }
        }
        disposable = Observable.interval(200, TimeUnit.MILLISECONDS).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                gridView.also {
                    if (state == STATE.RUNNING) {
                        data = recalculate(data)
                        it.setGridData(data)
                        it.invalidate()
                    }
                }
            }
    }

    private fun generateGridData(): MutableList<MutableList<Boolean>> {
        val grid = mutableListOf<MutableList<Boolean>>()
        for (i in 0 until GRID_SIZE) {
            val gridLine = mutableListOf<Boolean>()
            for (j in 0 until GRID_SIZE) {
                gridLine.add(Random.nextInt(3) == 0)
            }
            grid.add(gridLine)
        }
        return grid
    }

    private fun recalculate(data: MutableList<MutableList<Boolean>>): MutableList<MutableList<Boolean>> {
        val newData = mutableListOf<MutableList<Boolean>>()
        for (i in 0 until data.size) {
            val gridLine = mutableListOf<Boolean>()
            for (j in 0 until data[i].size) {
                var isAlive = data[i][j]
                val neighbour =
                    (if (data.getOrNull(i - 1)?.getOrNull(j - 1) == true) 1 else 0) +
                            (if (data.getOrNull(i - 1)?.getOrNull(j) == true) 1 else 0) +
                            (if (data.getOrNull(i - 1)?.getOrNull(j + 1) == true) 1 else 0) +
                            (if (data.getOrNull(i)?.getOrNull(j - 1) == true) 1 else 0) +
                            (if (data.getOrNull(i)?.getOrNull(j + 1) == true) 1 else 0) +
                            (if (data.getOrNull(i + 1)?.getOrNull(j - 1) == true) 1 else 0) +
                            (if (data.getOrNull(i + 1)?.getOrNull(j) == true) 1 else 0) +
                            (if (data.getOrNull(i + 1)?.getOrNull(j + 1) == true) 1 else 0)
                if (data[i][j] && neighbour != 2 && neighbour != 3) {
                    isAlive = false
                } else if (!data[i][j] && neighbour == 3) {
                    isAlive = true
                }
                gridLine.add(isAlive)
            }
            newData.add(gridLine)
        }
        return newData
    }

    enum class STATE {
        STOPPED, RUNNING
    }

    companion object {
        const val GRID_SIZE = 64
    }
}