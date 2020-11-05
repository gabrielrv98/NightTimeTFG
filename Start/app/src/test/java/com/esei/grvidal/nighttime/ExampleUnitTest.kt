package com.esei.grvidal.nighttime

import com.esei.grvidal.nighttime.ui.ChipDayFactory
import com.esei.grvidal.nighttime.ui.MyDay
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {






        val myday = MyDay(4,7,2020)
       val  monthArray = ChipDayFactory.datesCreator(myday)

         var number = 0
        var week = 0
        for( test in monthArray){

            println(number++.toString() + "-> " + "  week - "+ week.toString()+ "  "+ test.day.toString() + ":" + test.month.toString() + ":" + test.year.toString())

            if((number%7) == 0) week++
        }

    }
}