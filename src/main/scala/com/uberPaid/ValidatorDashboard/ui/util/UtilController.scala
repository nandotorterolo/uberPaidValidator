package com.uberPaid.ValidatorDashboard.ui.util

import java.text.DecimalFormat

import com.uberPaid.parser.UberLine

/**
  * @see http://alvinalexander.com/scala/how-to-extract-parts-strings-match-regular-expression-regex-scala
  */
object UtilController {


  val formatter = new DecimalFormat("#.###")

  // TODO take care with travaell > 1 hour
  private val patternDuration = """(\d{1,2})(h|m|s)\s(\d{1,2})(h|m|s)""".r
  def duration(line: UberLine) :Double =  {
    val patternDuration(minute, _, second, _) = line.duration
    (minute + "." + second).toDouble
  }

  private val patternPaid = """UYU(\d{1,}\.\d{2})""".r
  def paid(line: UberLine) :Double =  {
    val patternPaid(paid) = line.paid
    paid.toDouble
  }

}
