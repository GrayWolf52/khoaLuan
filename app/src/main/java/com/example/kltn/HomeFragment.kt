package com.example.kltn

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.anychart.APIlib
import com.anychart.AnyChart
import com.anychart.AnyChartView
import com.anychart.chart.common.dataentry.DataEntry
import com.anychart.chart.common.dataentry.ValueDataEntry
import com.anychart.charts.Pie
import com.anychart.enums.Align
import com.anychart.enums.LegendLayout
import ir.mahozad.android.DimensionResource
import ir.mahozad.android.PieChart
import ir.mahozad.android.component.Wrapping
import ir.mahozad.android.unit.Dimension
import java.util.*


class HomeFragment : Fragment() {
    private lateinit var pieChart: PieChart
    private lateinit var messageChart: TextView
    private lateinit var newJob: TextView
    private lateinit var doingJob: TextView
    private lateinit var doneJob: TextView
    private lateinit var viewModel: HomeViewModel
    private lateinit var monthSpinner: Spinner
    private lateinit var yearSpinner: Spinner
    private var userId = -1
    private lateinit var pie: Pie
    private var month = 0
    private var year = 2022

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var bundle = arguments
        if (bundle != null) {
            userId = bundle!!.getInt("UserId")
        }
        var view = inflater.inflate(R.layout.fragment_home, container, false)
        //    pie = AnyChart.pie()
        viewModel = ViewModelProviders.of(requireActivity()).get(HomeViewModel::class.java)
        val calender = Calendar.getInstance()
        var month = calender.get(Calendar.MONTH)
        var year = calender.get(Calendar.YEAR)

        Thread {
            viewModel.getInforChart(
                userId,
                month + 1,
                year
            )
        }.start()
        pieChart = view.findViewById(R.id.pieChart)
        messageChart = view.findViewById(R.id.messageChart)
        monthSpinner = view.findViewById(R.id.monthSpinner)
        yearSpinner = view.findViewById(R.id.yearSpinner)
        newJob = view.findViewById(R.id.newJob)
        doingJob = view.findViewById(R.id.doingJob)
        doneJob = view.findViewById(R.id.doneJob)

        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.list_month,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            monthSpinner.adapter = adapter
        }

        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.list_year,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            yearSpinner.adapter = adapter
        }


        monthSpinner.setSelection(month)
        monthSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                Log.d("TAG", "onItemSelected: position = $position")
                Thread {
                    viewModel.getInforChart(
                        userId,
                        position + 1,
                        this@HomeFragment.year
                    )
                }.start()
                this@HomeFragment.month = position + 1
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

        }

        yearSpinner.setSelection(
            resources.getStringArray(R.array.list_year).indexOf(year.toString())
        )
        yearSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                Log.d("TAG", "onItemSelected: position = $position")
                var year = resources.getStringArray(R.array.list_year)
                this@HomeFragment.year = year[position].toInt()
                Thread {
                    viewModel.getInforChart(
                        userId,
                        this@HomeFragment.month,
                        year[position].toInt()
                    )
                }.start()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

        }

        messageChart.visibility = View.GONE

        // setViewPieChart()

        /*  pieChart.description = Description()
          pieChart.setTransparentCircleAlpha(0)
          pieChart.setDrawEntryLabels(true)
  */
        viewModel.inforChart.observe(viewLifecycleOwner) {
            Log.d("TAG", "onCreateView:inforChart = $it ")
            if (it.first == 0 && it.second == 0 && it.third == 0) {
                messageChart.visibility = View.VISIBLE
                pieChart.visibility = View.GONE
            } else {
                pieChart.visibility = View.VISIBLE
                messageChart.visibility = View.GONE
                addDataSet(it.first.toFloat(), it.second.toFloat(), it.third.toFloat())
            }
        }
        return view
    }

    private fun addDataSet(data1: Float, data2: Float, data3: Float) {
        Log.d("TAG", "addDataSet: data1 = $data1 $data2 $data3 ")
        val percentNew = ((data1 * 100f) / (data1 + data2 + data3))
        val percentDoing = ((data2 * 100f) / (data1 + data2 + data3))
        val percentDone = ((data3 * 100f) / (data1 + data2 + data3))
        newJob.text = "Việc mới ${percentNew.toInt().toFloat()} %"
        doingJob.text = "Đang thực hiện ${percentDoing.toInt().toFloat()} %"
        doneJob.text = "Hoàn thành ${percentDone.toInt().toFloat()} %"
        pieChart.apply {
            slices = listOf(
                PieChart.Slice(
                    data1 / (data1 + data2 + data3),
                    ContextCompat.getColor(context, R.color.blue),
                    ContextCompat.getColor(context, R.color.blue),
                    legend = "Việc mới",
                    label = "Việc mới"
                ),
                PieChart.Slice(
                    data2 / (data1 + data2 + data3),
                    ContextCompat.getColor(context, R.color.purple_700),
                    ContextCompat.getColor(context, R.color.purple_700),
                    legend = "Đang thực hiện",
                    label = "Đang thực hiện"
                ),
                PieChart.Slice(
                    data3 / (data1 + data2 + data3),
                    ContextCompat.getColor(context, R.color.green),
                    ContextCompat.getColor(context, R.color.green),
                    legend = "Hoàn thành",
                    label = "Hoàn thành"
                ),
            )
            gradientType = PieChart.GradientType.RADIAL
            legendPosition = PieChart.LegendPosition.BOTTOM
            legendsIcon = PieChart.DefaultIcons.SQUARE
        }

    }

    private fun setViewPieChart() {
        pie.title("Thống kê trạng thái ")

        pie.labels().position("outside")

        pie.legend().title().enabled(true)
        pie.legend().title()
            .text("Chú thích ")
            .padding(0.0, 0.0, 10.0, 0.0)

        pie.legend()
            .position("center-bottom")
            .itemsLayout(LegendLayout.HORIZONTAL)
            .align(Align.CENTER)
    }


    /* override fun onResume() {
         super.onResume()
         Log.d("TAG", "onResume: ")
         Thread {
             viewModel.getInforChart(
                 userId,
                 month + 1,
                 year
             )
         }.start()
     }
 */
    override fun onStart() {
        super.onStart()
        Log.d("TAG", "onStart: ")
        Thread {
            viewModel.getInforChart(
                userId,
                month + 1,
                year
            )
        }.start()
    }

    override fun onStop() {
        super.onStop()
        Log.d("TAG", "onStop: ")
    }
}