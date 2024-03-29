package com.brainbyte.healthareana.ui.calendar

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.brainbyte.healthareana.databinding.FragmentCalendarBinding
import com.brainbyte.healthareana.util.USER_BD
import com.brainbyte.healthareana.util.USER_SP_KEY
import timber.log.Timber

class FragmentCalendar : Fragment() {

    private lateinit var binding: FragmentCalendarBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentCalendarBinding.inflate(layoutInflater, container, false)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            binding.birthPicker.setOnDateChangedListener { _, year, monthOfYear, dayOfMonth ->
                context?.getSharedPreferences(USER_SP_KEY, Context.MODE_PRIVATE)?.edit()?.putString(
                    USER_BD, "$dayOfMonth/$monthOfYear/$year")?.apply()
                Timber.d("$dayOfMonth/$monthOfYear/$year")
            }
        }


        binding.fab.setOnClickListener {
            findNavController().navigate(FragmentCalendarDirections.actionFragmentCalendarToFragmentHome())
        }

        return binding.root
    }
}