package com.uberPaid.ValidatorDashboard.ui.views

import com.uberPaid.ValidatorDashboard.ui.util.UtilController
import com.uberPaid.compiler.WorkflowCompiler
import com.uberPaid.parser.{AndThen, UberLine, WorkflowAST}
import com.vaadin.navigator.View
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent
import com.vaadin.shared.ui.MarginInfo
import com.vaadin.ui.Table.CellStyleGenerator
import com.vaadin.ui._
import vaadin.scala
import vaadin.scala.Table.ColumnAlignment

class ValidatorView extends VerticalLayout with View
{

  private val table = new scala.Table()
  private val idTimePickUp = "idTimePickUp"
  private val idVehicle = "idVehicle"
  private val idDuration = "idDuration"
  private val idDistance = "idDistance"
  private val idPaid = "idTotal"
  private val idPaidCalc = "idTotalCalculado"
  private val idDiff = "idDiff"

  configureTable()
  configureAll()

  private def configureAll() {
    setSizeFull()
    addStyleName("dashboard-view")
//    addStyleName("login-layout")

    val title = new Label("Validador")
    title.setSizeUndefined()
    title.addStyleName("h1")

    val label = scala.Label(
      """
        | <ol>
        | <li>Ingrese a su cuenta:<a href=" https://partners.uber.com" target="_blank"> https://partners.uber.com</a></li>
        | <li>Copie el texto de sus ingresos, puede consultar el ejemplo.</li>
        | <li>Pegue el contenido en el area de entrada.</li>
        | <li>Ingrese/Modifique los parametros asociados a costos.</li>
        | <li>Valide sus pagos.</li>
        | <p><strong>Validador = (CargoFijo) + (Costo_Tiempo * Tiempo) + (CostoDistancia * Distancia) - (TasaUber)</strong></p>
        | </ol>
      """.stripMargin
    )
    label.contentMode_=(contentMode = scala.Label.ContentMode.Html)

    val top = new VerticalLayout(title)
    top.setComponentAlignment(title, Alignment.MIDDLE_LEFT)
    top.setWidth("100%")
    top.setSpacing(true)
    top.addStyleName("toolbar")

    addComponent(top)



    // Input Validator
    val inputTextArea = new TextArea()
    inputTextArea.setWidthUndefined()
    inputTextArea.setSizeFull()

    val fixedTaxTextField = new scala.TextField {
      caption = "Costo Cargo Fijo"
      value_=("30")
    }

    val timeCostTextField = new scala.TextField {
      caption = "Costo Tiempo x min"
      value_=("3.5")
    }

    val distanceCostTextField = new scala.TextField{
      caption = "Costo Distancia x km"
      value_=("10")
    }
    val tasaUberTextField = new scala.TextField {
      caption = "Tasa Uber %"
      value_=("25")
      //    distanceCostTextField.setDescription("Distancia x km")
    }

    val parserButton = scala.Button("Validar", {
      val input = inputTextArea.getValue.replace("\n", " ")
      val fixedCost = fixedTaxTextField.value.getOrElse("30").toDouble
      val durationCost = timeCostTextField.value.getOrElse("3.5").toDouble
      val distanceCost = distanceCostTextField.value.getOrElse("10").toDouble
      val taxCost = tasaUberTextField.value.getOrElse("25").toDouble
      parseData(input, fixedCost, durationCost, distanceCost, taxCost)
    })

    val cleanButton = scala.Button("Limpiar", inputTextArea.clear())
    val exampleButton = scala.Button("Ejemplo", inputTextArea.setValue(
      """
        | 8:53 pm	uberX	11m 12s	4.28 UYU91.27
        | 9:16 pm	uberX	13m 49s	6.55 UYU92.05
        | 9:40 pm	uberX	6m 29s	2.85 UYU70.21
      """.stripMargin
    ))

    parserButton.styleName_=("default")
    cleanButton.styleName_=("small")
    exampleButton.styleName_=("small")

    val textAreaAndButtons = getHorizontalLayout
    textAreaAndButtons.addComponent(inputTextArea)


    val buttonsLayout = new scala.HorizontalLayout {
      addComponent(component = new scala.HorizontalLayout {
        addComponent(label)
      })

      addComponent(component = new scala.VerticalLayout {
        addComponent(component = new scala.FormLayout {
          addComponent(fixedTaxTextField)
          addComponent(fixedTaxTextField)
          addComponent(timeCostTextField)
          addComponent(distanceCostTextField)
          addComponent(tasaUberTextField)
          addComponent(parserButton)
          addComponent(cleanButton)
          addComponent(exampleButton)
        })
        setSizeFull()
        setDefaultComponentAlignment(Alignment.MIDDLE_RIGHT)
      })
    }

    addComponent(buttonsLayout.p)

    val rowTable = getHorizontalLayout
    rowTable.addComponent(table.p)
    addComponent(rowTable)

    setExpandRatio(textAreaAndButtons,1)
    setExpandRatio(rowTable,2)
  }

  private def configureTable() = {
    table.addContainerProperty(idTimePickUp, classOf[String])
    table.addContainerProperty(idVehicle, classOf[String])
    table.addContainerProperty(idDuration, classOf[String])
    table.addContainerProperty(idDistance, classOf[String])
    table.addContainerProperty(idPaid, classOf[String])
    table.addContainerProperty(idPaidCalc, classOf[String])
    table.addContainerProperty(idDiff, classOf[Double])

    table.setColumnHeader(idTimePickUp, "Tiempo hasta la recogida")
    table.setColumnHeader(idVehicle, "Vehículo")
    table.setColumnHeader(idDuration, "Duración")
    table.setColumnHeader(idDistance, "Distancia (km)")
    table.setColumnHeader(idPaid, "Total Uber")
    table.setColumnHeader(idPaidCalc,"Total Validador")
    table.setColumnHeader(idDiff, "Diferencia")

    table.setColumnAlignment(idTimePickUp, ColumnAlignment.Right)
//    table.setColumnAlignment(idVehicle, ColumnAlignment.Right)
    table.setColumnAlignment(idDuration,ColumnAlignment.Right)
    table.setColumnAlignment(idDistance,ColumnAlignment.Right)
    table.setColumnAlignment(idPaid, ColumnAlignment.Right)
    table.setColumnAlignment(idPaidCalc,ColumnAlignment.Right)
    table.setColumnAlignment(idDiff, ColumnAlignment.Right)

    table.p.setCellStyleGenerator( new CellStyleGenerator {
      override def getStyle(source: Table, itemId: Any, propertyId: Any) = {
        if (propertyId != null) {
          if (propertyId.asInstanceOf[String] == idDiff) {
            val item = source.getItem(itemId).getItemProperty(idDiff).getValue.asInstanceOf[Double]
             if (item > 0) "gain" else "loss"
          }else if (propertyId.asInstanceOf[String] == idPaidCalc) {
            val item = source.getItem(itemId).getItemProperty(idPaidCalc).getValue.asInstanceOf[String]  // TODO get instance from property
            "bold"
          }  else null
        } else {
          null
        }
      }
    })
  }

  private def getHorizontalLayout : HorizontalLayout = {
    val row = new HorizontalLayout()
    row.setSizeFull()
    row.setMargin(new MarginInfo(true, true, false, true))
    row.setSpacing(true)
    addComponent(row)
//    setExpandRatio(row, 1.5f)
    row
  }

  /**
    * Validar previamente estos datos
    * @param input
    * @param fixedCost
    * @param durationCost
    * @param distanceCost
    */
  private def parseData(input:String, fixedCost:Double, durationCost:Double, distanceCost:Double, taxCost:Double):Unit = {

    def astToList(wf: WorkflowAST):List[UberLine] = {
      wf match {
        case u:UberLine => List(u)
        case AndThen(step1:UberLine, step2:UberLine) => List(step1, step2)
        case AndThen(step1:UberLine, step2:AndThen) => List(step1) ++ astToList(step2)
        case _ => Nil
      }
    }

    WorkflowCompiler(input) match {
      case Right(ast) =>
        table.clear()
        table.refreshRowCache()
        astToList(ast).zipWithIndex.foreach { line =>

          // idTimePickUp, idVehicle, idDuration , idDistance idPaid, idPaidCalc, idDiff
          table.addItem(line._2); // Create item by explicit ID
          val item1 = table.getItem(line._2)

          val property1 = item1.getProperty(idTimePickUp)
          property1.value_=(line._1.timeToPick)

          val property2 = item1.getProperty(idVehicle)
          property2.value_=(line._1.vehicle)

          val property3 = item1.getProperty(idDuration)
          property3.value_=(line._1.duration)

          val property4 = item1.getProperty(idDistance)
          property4.value_=(line._1.distance)

          val property5 = item1.getProperty(idPaid)
          val paid = UtilController.paid(line._1)
          property5.value_=(line._1.paid) // todo shoud be load as string or double?  (considerer order)

          val property6 = item1.getProperty(idPaidCalc)
          // TODO : fix do double cast with a try
          val paidCalc = fixedCost + (UtilController.duration(line._1) * durationCost) + ( line._1.distance.toDouble * distanceCost)
          val paidCalcMinusPercentage = paidCalc - (paidCalc * taxCost / 100)
          property6.value_=(f"UYU ${paidCalcMinusPercentage}%1.2f")

          val property7 = item1.getProperty(idDiff)
          val diff = paid - paidCalcMinusPercentage
//          property7.value_=(f"$diff%1.2f")
          property7.value_=(BigDecimal(diff).setScale(2, BigDecimal.RoundingMode.HALF_UP).toDouble)
      }

      case Left(error) => Notification.show("Un error a ocurrido a parsear el texto")
    }
  }

  def enter(event: ViewChangeEvent) = {

  }

//  private def createPanel(content: Component ):  CssLayout  = {
//    val panel = new CssLayout()
//    panel.addStyleName("layout-panel")
//    panel.setSizeFull()
//
//    val configure = new Button()
//    configure.addStyleName("configure")
//    configure.addStyleName("icon-cog")
//    configure.addStyleName("icon-only")
//    configure.addStyleName("borderless")
//    configure.setDescription("Configure")
//    configure.addStyleName("small")
//    configure.addClickListener(new ClickListener() {
//      def buttonClick( event: ClickEvent) {
//      }
//    })
//
//    panel.addComponent(configure)
//    panel.addComponent(content)
//    panel
//  }
}