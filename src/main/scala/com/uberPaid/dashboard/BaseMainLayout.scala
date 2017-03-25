package com.uberPaid.dashboard

import com.uberPaid.vaadin.CreateWindow
import com.vaadin.navigator.{Navigator, View}
import com.vaadin.server.{Sizeable, ThemeResource}
import com.vaadin.ui.Button.{ClickEvent, ClickListener}
import com.vaadin.ui.MenuBar.Command
import com.vaadin.ui._
import org.slf4j.LoggerFactory

abstract class BaseMainLayout extends HorizontalLayout {

  lazy val logger = LoggerFactory.getLogger(getClass.getName)

  protected val contentLayout = new CssLayout()
  private val sidebarLayout = new VerticalLayout()
  private val brandingLayout = new CssLayout()

  protected val logo: Component
  private val menuLayout = new CssLayout()
  private val userMenuLayout = new VerticalLayout()
  private val profilePic = new Image(null, new ThemeResource("img/profile-pic.png"))
  private val userNameLabel = new Label()
  private val settingsMenuBar = new MenuBar()
  protected val settingsMenu = settingsMenuBar.addItem("", null)
  private val exitButton = new NativeButton()

  protected def logout(): Unit
  protected val navigator: Navigator
  protected val mainMenu: MainMenu
  protected def nombreUsuario: String

  protected val usuarioCommand: Command

  protected val NotImplementedYet = new Command() {
    override def menuSelected(selectedItem: MenuBar#MenuItem): Unit = Notification.show("Not implemented yet")
  }

  def init() {
    initBrandingLayout()
    initMenuLayout()
    initUserMenuLayout()
    initSettingsMenu()
    initExitButton()
    initSidebar()
    initMainViewLayout()

//    //FIXME: Si en el navegador se ingresan rutas incorrectas aparecen errores raros, la aplicacion se cae y no arranca mas
//    val url = Page.getCurrent.getUriFragment
//    if (url != null && url.startsWith("!"))  mainMenu.select(url.substring(1)) else {
//      if (url == null || url.equals("") || url.equals("/")) mainMenu.select(0) else mainMenu.select(url)
//    }

    userNameLabel.setValue(nombreUsuario.replace(" ", "\n"))
  }


  class MenuItem(val nombre: String, val classOf: Class[_ <: View]) {
    private val button = new NativeButton(nombre.substring(0, 1).toUpperCase + nombre.substring(1).replace('-', ' '))
    button.addStyleName("icon-" + nombre)
    button.addClickListener(new ClickListener() {
      override def buttonClick(event: ClickEvent): Unit = {
        mainMenu.selected = Option(MenuItem.this)
      }
    })
    menuLayout.addComponent(button)
    navigator.addView(nombre, classOf)

    private var _selected = false
    def selected = _selected
    def selected_=(value: Boolean): Unit = {
      _selected = value
       if (_selected) button.addStyleName("selected")
      else button.removeStyleName("selected")
    }
  }

  class MainMenu(list: List[MenuItem]) {

    private var _selected: Option[MenuItem] = None
    def selected: Option[MenuItem] = _selected
    def selected_=(optItem: Option[MenuItem]) = {
      _selected match {
        case Some(x) => x.selected = false
        case _ =>
      }
      _selected = optItem
      _selected match {
        case Some(x) =>
          x.selected = true
          navigator.navigateTo(x.nombre)
        case _ =>
      }
    }

    def find(nombre: String): Option[MenuItem] = list.find((n) => n.nombre == nombre)

    def select(nombre: String): Unit = selected = find(nombre)

    def select(index: Int): Unit = if(index>=0 && index<list.size) selected = Option(list(index))
  }

  object MainMenu {
    def apply(items: (String, Class[_ <: View])*): MainMenu = new MainMenu(items.map(i => new MenuItem(i._1, i._2)).toList)
  }

  private def initBrandingLayout(){
    logo.setSizeUndefined()
    brandingLayout.addStyleName("branding")
    brandingLayout.addComponent(logo)
  }

  private def initMenuLayout(){


    menuLayout.addStyleName("menu")
  }

  private def initSettingsMenu(){
    settingsMenu.setStyleName("icon-cog")
    settingsMenu.addItem("Usuario", usuarioCommand)
  }

  private def initUserMenuLayout(){
    profilePic.setWidth("34px")

    userNameLabel.setSizeUndefined()

    userMenuLayout.setSizeUndefined()
    userMenuLayout.addStyleName("user")
    userMenuLayout.addComponent(profilePic)
    userMenuLayout.addComponent(userNameLabel)
    userMenuLayout.addComponent(settingsMenuBar)
    userMenuLayout.addComponent(exitButton)
  }

  private def initSidebar(){
    sidebarLayout.addStyleName("sidebar")
    sidebarLayout.setWidth(null)
    sidebarLayout.setHeight(100, Sizeable.Unit.PERCENTAGE)
    sidebarLayout.addComponent(brandingLayout)
    sidebarLayout.addComponent(menuLayout)
    sidebarLayout.setExpandRatio(menuLayout, 1)
    sidebarLayout.addComponent(userMenuLayout)
  }

  private def initMainViewLayout(){
    contentLayout.addStyleName("view-content")
    contentLayout.setSizeFull()

    addStyleName("mainview")
    setSizeFull()
    addComponent(sidebarLayout)
    addComponent(contentLayout)
    setExpandRatio(contentLayout, 1)
  }

  private def initExitButton(){
    exitButton.addStyleName("icon-cancel")
    exitButton.setDescription("Salir")
    exitButton.addClickListener(new ClickListener() {
      override def buttonClick(event: ClickEvent): Unit = {
        closeAllWindows()
        logout()
      }
    })
  }

  private def closeAllWindows() = {
    if (getUI != null) {
      val iter = getUI.getWindows.iterator
      while (iter.hasNext) {
        iter.next match {
          case window: CreateWindow => window.closeWindow()
          case _ =>
        }
      }
    }
  }
}