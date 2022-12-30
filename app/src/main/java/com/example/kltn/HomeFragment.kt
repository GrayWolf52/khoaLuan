package com.example.kltn

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
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
import java.util.*


class HomeFragment : Fragment() {
    private lateinit var pieChart: AnyChartView
    private lateinit var messageChart: TextView
    private lateinit var viewModel: HomeViewModel
    private lateinit var monthSpinner: Spinner
    private var userId = -1
    private lateinit var pie: Pie

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var bundle = arguments
        if (bundle != null) {
            userId = bundle!!.getInt("UserId")
        }
        var view = inflater.inflate(R.layout.fragment_home, container, false)
        pie = AnyChart.pie()
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
                        year
                    )
                }.start()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

        }

        messageChart.visibility = View.GONE

        setViewPieChart()
        pieChart.setChart(pie)

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
                requireActivity().runOnUiThread {
                    APIlib.getInstance().setActiveAnyChartView(pieChart)
                    addDataSet(it.first.toFloat(), it.second.toFloat(), it.third.toFloat())
                }
            }
        }
        return view
    }

    private fun addDataSet(data1: Float, data2: Float, data3: Float) {
        Log.d("TAG", "addDataSet: ")
        val data: MutableList<DataEntry> = ArrayList()
        data.add(ValueDataEntry("Việc mới", data1))
        data.add(ValueDataEntry("Đang thực hiện", data2))
        data.add(ValueDataEntry("Hoàn thành", data3))
        Log.d("TAG", "addDataSet:0 ")
        pie.data(data)
        pieChart.visibility = View.GONE
        pieChart.visibility = View.VISIBLE
        pieChart.invalidate()

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

}