package com.uberPaid.vaadin

import com.vaadin.ui.{UI, Component, Window}
import com.vaadin.event.ShortcutAction.KeyCode

trait CreateWindow extends Component {

  def createWindow(title: String = "") = {
    val window = new Window()
    window.setContent(this)
    window.setClosable(true)    // Poner closable true, agrega CloseShortcut(KeyCode.ESCAPE) por defecto
    window.setCloseShortcut(KeyCode.ESCAPE)
    window.setCaption(title)
    window.setModal(true)
    window.setResizable(false)
    window
  }

  def closeWindow() = {
   if(isWindowed) UI.getCurrent.removeWindow(this.getParent.asInstanceOf[Window])
  }

  def createAndShow(title: String = "") = {
    val window = createWindow(title)
    UI.getCurrent.addWindow(window)
    window.focus()
  }

  def isWindowed = {
    val window = this.getParent
    window != null && window.isInstanceOf[Window]
  }

}
