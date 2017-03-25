package com.uberPaid.parser

import com.uberPaid.compiler.WorkflowCompiler
import org.scalatest.{FlatSpec, Matchers}

class WorkflowCompilerSpec extends FlatSpec with Matchers {

  val validCode =
    """
      |read input name, country
      |switch:
      |  country == "PT" ->
      |    call service "A"
      |    exit
      |  otherwise ->
      |    call service "B"
      |    switch:
      |      name == "unknown" ->
      |        exit
      |      otherwise ->
      |        call service "C"
      |        exit
    """.stripMargin.trim

  val successfulAST = AndThen(
    ReadInput(List("name", "country")),
    Choice(List(
      IfThen( Equals("country", "PT"), AndThen(CallService("A"), Exit) ),
      OtherwiseThen(
        AndThen(
          CallService("B"),
          Choice(List(
            IfThen( Equals("name", "unknown"), Exit ),
            OtherwiseThen( AndThen(CallService("C"), Exit) )
          ))
        )
      )
    ))
  )

  // one line
  val oneLineCode = """6:56 pm uberX 21m 26s 12.20 UYU99.39""".trim

  val oneLineAST=
    UberLine("6:56 pm",	"uberX", "21m 26s", "12.20", "UYU99.39")

  "Workflow compiler" should "successfully parse a valid workflow" in {
    WorkflowCompiler(oneLineCode) shouldBe Right(oneLineAST)
  }

  // multiline
  val multiLineCode =
   """
     | 8:53 pm	uberX	11m 12s	4.28 UYU91.27 9:16 pm	uberX	13m 49s	6.55 UYU92.05 9:40 pm	uberX	6m 29s	2.85 UYU70.21
   """.stripMargin.trim

  val multiLineAST =
    AndThen(
      UberLine("8:53 pm",	"uberX", "11m 12s", "4.28", "UYU91.27"), AndThen (
        UberLine("9:16 pm",	"uberX", "13m 49s", "6.55", "UYU92.05"),
          UberLine("9:40 pm",	"uberX", "6m 29s", "2.85", "UYU70.21")
    )
  )

  "Workflow compiler" should "successfully parse a valid workflow multiline" in {
      WorkflowCompiler(multiLineCode) shouldBe Right(multiLineAST)
  }

  // multiple lines with return
  val multiLineReturnLinesCode =
    """
      |8:53 pm	uberX	11m 12s	4.28 UYU91.27
      |     9:16 pm	uberX	13m 49s	6.55 UYU92.05
      |   9:40 pm	uberX	6m 29s	2.85 UYU70.21
    """.stripMargin.replace("\n"," ").trim

  // multiple lines with return
  val multiLineReturnLines2Code =
    """
      |8:53 pm	uberX	11m 12s	4.28 UYU91.27
      |9:16 pm	uberX	13m 49s	6.55 UYU92.05
      |9:40 pm	uberX	6m 29s	2.85 UYU70.21
    """.stripMargin.replace("\n"," ").trim

  // paste from web
  val pasteFromWebCode =
    """
      |8:53 pm	uberX	11m 12s	4.28
      |UYU91.27
      |9:16 pm	uberX	13m 49s	6.55
      |UYU92.05
      |9:40 pm	uberX	6m 29s	2.85
      |UYU70.21
    """.stripMargin.replace("\n"," ").trim


  val multiLineReturnLinesAST =
    AndThen(
      UberLine("8:53 pm",	"uberX", "11m 12s", "4.28", "UYU91.27"), AndThen (
        UberLine("9:16 pm",	"uberX", "13m 49s", "6.55", "UYU92.05"),
        UberLine("9:40 pm",	"uberX", "6m 29s", "2.85", "UYU70.21")
      )
    )

  "Workflow compiler" should "successfully parse a valid workflow multiline with returns" in {
    WorkflowCompiler(multiLineReturnLinesCode) shouldBe Right(multiLineReturnLinesAST)
  }

  "Workflow compiler" should "successfully parse a valid workflow multiline with returns v2" in {
    WorkflowCompiler(multiLineReturnLines2Code) shouldBe Right(multiLineReturnLinesAST)
  }

  "Workflow compiler" should "successfully parse a valid workflow paste from web" in {
    WorkflowCompiler(pasteFromWebCode) shouldBe Right(multiLineReturnLinesAST)
  }

}
