package com.wildraion.taskmanager.util

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.text.TextUtils
import java.text.SimpleDateFormat
import java.util.*


class Utils {
    companion object {

        fun inputCheck(title: String, deadline: String): Boolean {
            return !(TextUtils.isEmpty(title) || TextUtils.isEmpty(deadline))
        }

        @SuppressLint("SimpleDateFormat")
        fun datePatternToPretty(inputDate: String): String {
            val date: Date = SimpleDateFormat("dd-MM-yyyy").parse(inputDate) as Date
            return SimpleDateFormat("dd MMMM yyyy").format(date)
        }

        fun timePeriodConverter(hour: Int, minute: Int, is24: Boolean): String{
            val hourStr: String
            val dayPeriod: String

            if(is24){
                hourStr = "%02d".format(hour)
                dayPeriod = ""
            }
            else {
                var hour12: Int
                if(hour > 12){
                    hour12 = hour - 12
                    dayPeriod = " PM"
                }
                else{
                    hour12 = hour
                    dayPeriod = " AM"
                }
                if(hour12 == 0) hour12 = 12
                hourStr = "%02d".format(hour12)
            }

            return hourStr + ":%02d".format(minute) + dayPeriod
        }

        fun getImageId(context: Context, imageName: String): Int {
            return context.resources
                .getIdentifier("drawable/$imageName", null, context.packageName)
        }
    }
}