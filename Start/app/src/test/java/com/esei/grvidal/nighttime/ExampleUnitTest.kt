package com.esei.grvidal.nighttime

import com.esei.grvidal.nighttime.data.ChipDayFactory
import com.esei.grvidal.nighttime.data.MyDate
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {






        val myday = MyDate(4,12,2020)
       val  monthArray = ChipDayFactory.datesCreator(myday)

         var number = 0
        var week = 0
        for( testWeeks in monthArray){
            for(testDays in testWeeks)
                println(" $number ->  week -  $week   $testDays ").also{
                    number++;
                }


            if((number%7) == 0) week++
        }

    }
}