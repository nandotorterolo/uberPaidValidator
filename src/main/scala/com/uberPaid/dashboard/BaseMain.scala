package com.uberPaid.dashboard

import com.uberPaid.vaadin.LogException
import com.vaadin.server.VaadinRequest
import com.vaadin.ui._

trait BaseMain extends LogException {
  
  protected def createMainView(): BaseMainLayout

  protected def createLoginView(): LoginView

  val app: BaseApplication

  protected val rootLayout = new CssLayout()
  private var loginView: LoginView = null
  private var mainView: BaseMainLayout = null

  def login(username: String, password: String): Unit = {
    app.login(username, password) match {
      case Right(ssn) =>
        if (mainView == null) mainView = createMainView()
        setView(mainView)
      case Left(ex) =>
        com.vaadin.ui.Notification.show(ex.getMessage, logException(ex), com.vaadin.ui.Notification.Type.TRAY_NOTIFICATION)
//        showException(ex)
    }
  }

  protected def addStyleName(styleName: String): Unit
  protected def removeStyleName(styleName: String): Unit

  protected def closeCurrentSession(): Unit

  protected def isLoggedIn: Boolean

  def logout() = {
    closeCurrentSession()
    setView(loginView)
    loginView.clearPasswordField()
  }

  private def setView(view: AbstractLayout): Unit = {
    if (view == loginView || mainView == null) {
//      addStyleName("login")
      addStyleName("loginview")
      if (mainView != null) rootLayout.removeComponent(mainView)
      rootLayout.addComponent(loginView)
      loginView.focus()
    } else {
//      removeStyleName("login")
      removeStyleName("loginview")
      rootLayout.removeComponent(loginView)
      rootLayout.addComponent(mainView)
    }
  }

  private def initRootLayout(){
//    val background = new Label()
//    background.setSizeUndefined()
//    background.addStyleName("login-bg")
    rootLayout.addStyleName("root")
    rootLayout.setSizeFull()
//    rootLayout.addComponent(background)
  }

  def init(request: VaadinRequest) = {
    if (loginView == null) loginView = createLoginView()
    if (isLoggedIn) setView(mainView) else setView(loginView)
  }

  initRootLayout()

}