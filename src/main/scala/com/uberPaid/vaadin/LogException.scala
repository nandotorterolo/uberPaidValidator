package com.uberPaid.vaadin

import org.joda.time.DateTime
import org.slf4j.Logger

trait LogException {

  val logger: Logger
  
  private def getNewExceptionId = DateTime.now().getMillis.toHexString

  def showException(ex: Throwable, caption: String = "Error Interno") : Unit = {
    com.vaadin.ui.Notification.show(caption, logException(ex), com.vaadin.ui.Notification.Type.ERROR_MESSAGE)
  }

  def showTray(msg: String) : Unit = {
    com.vaadin.ui.Notification.show(msg, com.vaadin.ui.Notification.Type.TRAY_NOTIFICATION)
  }

  def logException(ex: Throwable, caption: String = null): String = {
    val reference = "LogException #" + getNewExceptionId + (if (caption==null) "" else " - " + caption)
    logger.warn(reference, ex)
    reference
  }

}