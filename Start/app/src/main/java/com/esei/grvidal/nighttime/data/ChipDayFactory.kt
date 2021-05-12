package com.esei.grvidal.nighttime.data

import java.lang.StringBuilder
import java.time.LocalDate


fun LocalDate.toMyDate(): MyDate {
    return MyDate(
        this.dayOfMonth,
        this.monthValue,
        this.year
    )
}

data class MyDate(val day: Int, val month: Int, val year: Int) {

    val previousMonth: MyDate
        get() {
            return LocalDate.of(year, month, day).minusMonths(1).toMyDate()
        }

    val nextMonth: MyDate
        get() {
            return LocalDate.of(year, month, day).plusMonths(1).toMyDate()
        }

    fun toStringFormatted(): String {
        return StringBuilder().append(day)
            .append("/")
            .append(month)
            .append("/")
            .append(year)
            .toString()
    }
}

/**
 * Creates the calendar layout ( days of the week) from a day of a month
 *
 */
class ChipDayFactory {


    companion object ChipDayFactory {

        /**
         *  val today when someone gets it, it updates to the actual date
         */
        private val today: MyDate
            get() {
                val calendar = LocalDate.now()
                return MyDate(
                    calendar.dayOfMonth,
                    calendar.monthValue,
                    calendar.year
                )
            }

        /**
         * @param selectedDate Selected date.
         *
         * @return An array of arrays composed with 7 days in the selected month surrounded by the previous days
         * or the next days if needed
         */
        fun datesCreator(selectedDate: MyDate = today): List<List<MyDate>> {

            //Array with the dates to return
            val monthArray = mutableListOf<MyDate>()


            //localDate is used to calculate the days before day 1 to complete the Layout
            val localDate = LocalDate.of(selectedDate.year, selectedDate.month, 1)

            //Days within the previous month
            val daysOff = localDate.dayOfWeek.value - 1


            // if day 1 of the month is different from Monday
            if (daysOff > 0) {

                //previous month is calculated
                val localDateLastMonth = localDate.minusMonths(1)

                //previous month number
                val previousMonthMonth = localDateLastMonth.month.value

                //previous year number
                val previousMonthYear = localDateLastMonth.year

                //Last day of the previous month
                val previousMonthDay = localDateLastMonth.lengthOfMonth() - daysOff + 1

                //Loop to create the previous days
                for (day in previousMonthDay..localDateLastMonth.lengthOfMonth()) {
                    monthArray.add(
                        MyDate(
                            day,
                            previousMonthMonth,
                            previousMonthYear
                        )
                    )
                }
            }

            //Max day of the selected month
            val maxDaysActualMonth = localDate.lengthOfMonth()

            //Loop to create the days of the month
            for (day in 1..maxDaysActualMonth) {
                monthArray.add(
                    MyDate(
                        day,
                        localDate.monthValue,
                        localDate.year
                    )
                )
            }

            val lastDay = LocalDate.of(selectedDate.year, selectedDate.month, monthArray.last().day)
            // if the last day is not Sunday
            if (lastDay.dayOfWeek.value != 7) {

                //Next month is calculated
                val localDateNextMonth = localDate.plusMonths(1)

                //Next month number
                val nextMonthMonth = localDateNextMonth.month.value

                //Next year number
                val nextMonthYear = localDateNextMonth.year

                //Loop to create the previous days
                for (day in 1..(7 - lastDay.dayOfWeek.value)) {
                    monthArray.add(
                        MyDate(
                            day,
                            nextMonthMonth,
                            nextMonthYear
                        )
                    )
                }
            }





            return monthArray.chunked(7)
            //return  selectedDate.get(Calendar.DAY_OF_WEEK).toString() + "  -> " +  selectedDate.toString()
        }

    }
}



