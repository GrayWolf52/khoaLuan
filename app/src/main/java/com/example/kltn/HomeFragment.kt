package com.example.kltn

import android.content.Context
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
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import java.util.*
import kotlin.collections.ArrayList


class HomeFragment : Fragment() {
    private lateinit var pieChart: PieChart
    private lateinit var messageChart: TextView
    private lateinit var viewModel: HomeViewModel
    private lateinit var monthSpinner: Spinner
    private var userId = -1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var bundle = arguments
        if (bundle != null) {
            userId = bundle!!.getInt("UserId")
        }
        var view = inflater.inflate(R.layout.fragment_home, container, false)
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
        pieChart.description = Description()
        pieChart.setTransparentCircleAlpha(0)
        pieChart.setDrawEntryLabels(true)

        viewModel.inforChart.observe(viewLifecycleOwner) {
            Log.d("TAG", "onCreateView:inforChart = $it ")
            if (it.first == 0 && it.second == 0 && it.third == 0) {
                messageChart.visibility = View.VISIBLE
                pieChart.visibility = View.GONE
            } else {
                pieChart.visibility = View.VISIBLE
                messageChart.visibility = View.GONE
                addDataSet(pieChart, it.first.toFloat(), it.second.toFloat(), it.third.toFloat())
            }
        }
        return view
    }

    private fun addDataSet(pieChart: PieChart, data1: Float, data2: Float, data3: Float) {
        val yEntrys: ArrayList<PieEntry> = ArrayList()
        val xEntrys: ArrayList<String> = ArrayList()
        val yData = floatArrayOf(data1, data2, data3)
        val xData = arrayOf("Việc mới", "Đang thực hiện", "Hoàn thành")
        for (i in yData.indices) {
            yEntrys.add(PieEntry(yData[i], i))
        }
        for (i in xData.indices) {
            xEntrys.add(xData[i])
        }
        val pieDataSet = PieDataSet(yEntrys, "Thống kê trạng thái")
        pieDataSet.sliceSpace = 2f
        pieDataSet.valueTextSize = 20f
        val colors: ArrayList<Int> = ArrayList()
        colors.add(R.color.blue)
        colors.add(R.color.purple_700)
        colors.add(R.color.green)
        pieDataSet.colors = colors
        val legend = pieChart.legend
        legend.form = Legend.LegendForm.CIRCLE
        val pieData = PieData(pieDataSet)
        pieChart.data = pieData
        pieChart.invalidate()
    }

}