package com.uberPaid.ValidatorDashboard.ui.views

import com.vaadin.ui._
import com.vaadin.navigator.{ViewChangeListener, View}
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent
import com.vaadin.shared.ui.MarginInfo
import com.vaadin.ui.Button.{ClickEvent, ClickListener}
import com.vaadin.shared.ui.label.ContentMode
import com.vaadin.event.LayoutEvents.{LayoutClickEvent, LayoutClickListener}

class DashboardView extends VerticalLayout with View with ViewChangeListener
{
  val notifications = new Window("Notificaciones")

  init()

  def init() {
    setSizeFull()
    addStyleName("dashboard-view")

    val top = new HorizontalLayout()
    top.setWidth("100%")
    top.setSpacing(true)
    top.addStyleName("toolbar")
    addComponent(top)

    val title = new Label("Access")
    title.setSizeUndefined()
    title.addStyleName("h1")
    top.addComponent(title)
    top.setComponentAlignment(title, Alignment.MIDDLE_LEFT)
    top.setExpandRatio(title, 1)

    // Boton Notificar
    val notifyButton = new Button("2")
    notifyButton.setDescription("Notificaciones (2 sin leer)")
    // notify.addStyleName("borderless")
    notifyButton.addStyleName("notifications")
    notifyButton.addStyleName("unread")
    notifyButton.addStyleName("icon-only")
    notifyButton.addStyleName("icon-bell")

    notifyButton.addClickListener(new ClickListener {
      def buttonClick(event: ClickEvent) = {
        event.getButton.removeStyleName("unread")
        event.getButton.setDescription("Notifications")

        if (notifications != null && notifications.getUI != null)
          notifications.close()
        else {
          buildNotifications(event)
          getUI.addWindow(notifications)
          notifications.focus()
          getUI.getContent.asInstanceOf[CssLayout].addLayoutClickListener(new LayoutClickListener() {
            def layoutClick(event: LayoutClickEvent ) {
              notifications.close()
              getUI.getContent.asInstanceOf[CssLayout].removeLayoutClickListener(this)
            }
          })
        }
      }
    })

    top.addComponent(notifyButton)
    top.setComponentAlignment(notifyButton, Alignment.MIDDLE_LEFT)
    // fin boton Notificar


       // inicio de notas

    val notes = new TextArea("Notes")
    notes.setValue("Bienvenidos:\n· Estas notas no son persistidas\n· Escribir lo que quieras \n· ...")
    notes.setSizeFull()
    val panelNotas: CssLayout = createPanel(notes)
    panelNotas.addStyleName("notes")

    // Visualizacion de las row.
    val row = getRowView
    row.addComponent(panelNotas)
  }

  private def getRowView : HorizontalLayout = {
    val row = new HorizontalLayout()
    row.setSizeFull()
    row.setMargin(new MarginInfo(true, true, false, true))
    row.setSpacing(true)
    addComponent(row)
    setExpandRatio(row, 1.5f)
    row
  }

  def enter(event: ViewChangeEvent) = {

  }

  private def createPanel(content: Component ):  CssLayout  = {
    val panel = new CssLayout()
    panel.addStyleName("layout-panel")
    panel.setSizeFull()

    val configure = new Button()
    configure.addStyleName("configure")
    configure.addStyleName("icon-cog")
    configure.addStyleName("icon-only")
    configure.addStyleName("borderless")
    configure.setDescription("Configure")
    configure.addStyleName("small")
    configure.addClickListener(new ClickListener() {
      def buttonClick( event: ClickEvent) {
      }
    })

    panel.addComponent(configure)
    panel.addComponent(content)
    panel
  }

  private def buildNotifications(event:ClickEvent ) {
    val verticalLayout = new VerticalLayout()
    verticalLayout.setMargin(true)
    verticalLayout.setSpacing(true)
    notifications.setContent(verticalLayout)
    notifications.setWidth("300px")
    notifications.addStyleName("notifications")
    notifications.setClosable(false)
    notifications.setResizable(false)
    notifications.setDraggable(false)
    notifications.setPositionX(event.getClientX - event.getRelativeX)
    notifications.setPositionY(event.getClientY - event.getRelativeY)

    verticalLayout.addComponent( new Label("<hr><b>Not 1</b><br><span>hace 25 minutos </span><br>", ContentMode.HTML))
    verticalLayout.addComponent(new Label("<hr><b>Not2</b><br><span>hace 2 days ago</span><br>", ContentMode.HTML))
  }

  def beforeViewChange(event: ViewChangeEvent): Boolean = {
    println("beforeViewChange")
    true
  }

  def afterViewChange(event: ViewChangeEvent): Unit = {
    println("afterViewChange")
  }

  override def attach(): Unit = {
    super.attach()
    if (getUI.getNavigator == null) {
      throw new IllegalStateException("Please configure the Navigator before you attach to the UI")
      }
    getUI.getNavigator.addViewChangeListener(this)
  }

  override def detach(): Unit = {
    getUI.getNavigator.removeViewChangeListener(this)
  }

}