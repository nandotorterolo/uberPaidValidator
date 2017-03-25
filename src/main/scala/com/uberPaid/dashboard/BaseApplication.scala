package com.uberPaid.dashboard

import com.uberPaid.ValidatorDashboard.ui.AppSession
import org.slf4j.Logger

abstract class BaseApplication {

  val logger: Logger

  val companyName: String
  val name: String
  val version: String

  def nameAndVersion =  name

  protected def init: Boolean

  private var _isInitialized = false
  def isInitialized = _isInitialized
  def initializeIfNot: Boolean = {
    if (!_isInitialized) _isInitialized = init
    _isInitialized
  }

  def login(userName: String, password: String): Either[Throwable, AppSession]

  def isDeveloperMode: Boolean
}