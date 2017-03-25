package com.uberPaid.parser

import com.uberPaid.ValidatorDashboard.ui.util.UtilController
import org.scalatest.{FlatSpec, Matchers}


class UtilControllerSpec  extends FlatSpec with Matchers {

  val duration = "11m 12s"

  "Pattern Duration" should "successfully parse a valid duration" in {
    UtilController.duration(UberLine("","",duration,"","")) shouldBe 11.12
  }

  val paid = "UYU91.27"

  "Pattern Paid" should "successfully parse a valid paid" in {
    UtilController.paid(UberLine("","","","",paid)) shouldBe 91.27
  }

}
