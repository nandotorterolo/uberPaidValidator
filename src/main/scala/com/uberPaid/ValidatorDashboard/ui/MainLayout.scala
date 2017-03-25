package com.uberPaid.ValidatorDashboard.ui

import com.uberPaid.ValidatorDashboard.ui.views.{DashboardView, ValidatorView}
import com.uberPaid.dashboard.BaseMainLayout
import com.vaadin.ui.MenuBar.Command
import com.vaadin.navigator.Navigator
import com.vaadin.ui._
import com.vaadin.shared.ui.label.ContentMode

class MainLayout(main: Main) extends BaseMainLayout {

  protected val logo: Component = new Label("<span>" + main.app.nameAndVersion + "</span>" + main.app.companyName, ContentMode.HTML)

  protected val navigator: Navigator = new Navigator(main, contentLayout)

  protected val mainMenu: MainMenu = MainMenu(
//    "Dashboard" -> classOf[DashboardView],
    "Validador" -> classOf[ValidatorView]
  )

  protected val usuarioCommand: Command = new Command {
    def menuSelected(selectedItem: MenuBar#MenuItem) = {
//      getUI.addWindow(new Window("test"))
      ()
    }
  }

  init()

  /**
    * Protect visual overflow
    */
  protected def nombreUsuario = "userName"

  protected def logout() = {
    main.logout()
  }

}
