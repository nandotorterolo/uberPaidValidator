package com.uberPaid.dashboard

import com.vaadin.event.ShortcutAction
import com.vaadin.server.Sizeable
import com.vaadin.ui.Button.{ClickEvent, ClickListener}
import com.vaadin.ui._

class LoginView(title: String, login: (String, String) => Unit) extends VerticalLayout {

  private val labelsLayout = new HorizontalLayout()
  private val welcomeLabel = new Label("Bienvenido")
  private val titleLabel = new Label(title)
  private val loggingFieldsLayout = new HorizontalLayout()
  private val usernameField = new TextField("Usuario")
  private val passwordField = new PasswordField("Contraseña")
  private val signInButton = new Button("Ingresar")

  welcomeLabel.addStyleName("h4")
  welcomeLabel.setSizeUndefined()

  titleLabel.addStyleName("h2")
  titleLabel.addStyleName("light")
  titleLabel.setSizeUndefined()

  labelsLayout.addStyleName("labels")
  labelsLayout.setWidth(100, Sizeable.Unit.PERCENTAGE)
  labelsLayout.setMargin(true)

  labelsLayout.addComponent(welcomeLabel)
  labelsLayout.setComponentAlignment(welcomeLabel, Alignment.MIDDLE_LEFT)
  labelsLayout.addComponent(titleLabel)
  labelsLayout.setComponentAlignment(titleLabel, Alignment.MIDDLE_RIGHT)

  loggingFieldsLayout.setSpacing(true)
  loggingFieldsLayout.setMargin(true)
  loggingFieldsLayout.addComponent(usernameField)
  loggingFieldsLayout.addComponent(passwordField)

  usernameField.setNullRepresentation("")
  passwordField.setNullRepresentation("")

  loggingFieldsLayout.addComponent(signInButton)
  loggingFieldsLayout.setComponentAlignment(signInButton, Alignment.BOTTOM_LEFT)

  signInButton.addStyleName("default")
  signInButton.addClickListener(new ClickListener() {
    override def buttonClick(event: ClickEvent): Unit = {
      login(usernameField.getValue, passwordField.getValue)
    }
  })
  signInButton.setClickShortcut(ShortcutAction.KeyCode.ENTER)

  private val loginPanelLayout = new CssLayout()
  loginPanelLayout.addStyleName("login-panel")
  loginPanelLayout.addComponent(labelsLayout)
  loginPanelLayout.addComponent(loggingFieldsLayout)

  setSizeFull()
  addStyleName("login-layout")
  addComponent(loginPanelLayout)
  setComponentAlignment(loginPanelLayout, Alignment.MIDDLE_CENTER)

  override def focus() = usernameField.focus()

  def clearPasswordField(): Unit = passwordField.setValue(null)

  def setUsername(value: String): Unit = usernameField.setValue(value)

  def setPassword(value: String): Unit = passwordField.setValue(value)

}
