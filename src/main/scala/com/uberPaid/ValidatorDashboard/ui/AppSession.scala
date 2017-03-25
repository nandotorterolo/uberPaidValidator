package com.uberPaid.ValidatorDashboard.ui

import java.text.SimpleDateFormat

import org.joda.time.DateTime

trait AppSession {

  def id:Int
  def userName: String
  def start: DateTime

  override def toString: String = "ssn#" + id + ": " + userName + " " + AppSession.dateFormat.format(start.toDate)
}

object AppSession {
  val dateFormat = new SimpleDateFormat("ddd.dd MMM hh:mm")
}


