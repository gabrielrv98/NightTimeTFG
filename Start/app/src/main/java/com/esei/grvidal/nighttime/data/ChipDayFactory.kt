package com.esei.grvidal.nighttime.data

import java.time.LocalDate


fun LocalDate.toMyDate(): MyDay{
    return MyDay(
        this.dayOfMonth,
        this.monthValue,
        this.year
    )
}

data class MyDay(val day: Int, val month: Int, val year: Int){
    val previousMonth: MyDay
    get() {
        return LocalDate.of(year, month, day).minusMonths(1).toMyDate()
    }

    val nextMonth: MyDay
        get() {
            return LocalDate.of(year,month,day).plusMonths(1).toMyDate()
        }
}

class ChipDayFactory {


    companion object ChipDayFactory {

        /**
         *  val today when somone gets it, it updates to the actual date
         */
        private val today: MyDay
            get() {
                val calendar = LocalDate.now()
                return MyDay(
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
        fun datesCreator(selectedDate: MyDay = today): List<List<MyDay>> {

            //Array with the dates to return
            val monthArray = mutableListOf<MyDay>()


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
                val previousMonthDay = localDateLastMonth.lengthOfMonth() - daysOff +1

                //Loop to create the previous days
                for (day in previousMonthDay .. localDateLastMonth.lengthOfMonth()) {
                    monthArray.add(
                        MyDay(
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
            for(day in 1 .. maxDaysActualMonth){
                monthArray.add(
                    MyDay(
                        day,
                        localDate.monthValue,
                        localDate.year
                    )
                )
            }

            val lastDay = LocalDate.of(selectedDate.year, selectedDate.month, monthArray.last().day)
            // if the last day is not Sunday
            if( lastDay.dayOfWeek.value != 7){

                //Next month is calculated
                val localDateNextMonth = localDate.plusMonths(1)

                //Next month number
                val nextMonthMonth = localDateNextMonth.month.value

                //Next year number
                val nextMonthYear = localDateNextMonth.year

                //Loop to create the previous days
                for (day in 1 .. ( 7 - lastDay.dayOfWeek.value) ){
                    monthArray.add(
                        MyDay(
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



