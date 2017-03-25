package com.uberPaid.lexer

import com.uberPaid.compiler.{Location, WorkflowLexerError}

import scala.util.parsing.combinator.RegexParsers

object WorkflowLexer extends RegexParsers {
  override def skipWhitespace = true
  override val whiteSpace = "[ \t\r\f]+".r

  def apply(code: String): Either[WorkflowLexerError, List[WorkflowToken]] = {
    parse(tokensWithoutIdentations, code) match {
      case NoSuccess(msg, next) => Left(WorkflowLexerError(Location(next.pos.line, next.pos.column), msg))
      case Success(result, next) => Right(result)
    }
  }

  // take care with the order of the tokens, cause "UYU99.39" could be cached by vehicle "UYU".
  def tokensWithoutIdentations: Parser[List[WorkflowToken]] = {
    phrase(rep1(paid | vehicle | timeToPick | duration | distance))
  }


  def tokens: Parser[List[WorkflowToken]] = {
    phrase(rep1(exit | readInput | callService | switch | otherwise | colon | arrow
      | equals | comma | literal | identifier | indentation )) ^^ { rawTokens =>
      processIndentations(rawTokens)
    }
  }

  private def processIndentations(tokens: List[WorkflowToken],
                                  indents: List[Int] = List(0)): List[WorkflowToken] = {
    tokens.headOption match {

      // if there is an increase in indentation level, we push this new level into the stack
      // and produce an INDENT
      case Some(INDENTATION(spaces)) if spaces > indents.head =>
        INDENT() :: processIndentations(tokens.tail, spaces :: indents)

      // if there is a decrease, we pop from the stack until we have matched the new level and
      // we produce a DEDENT for each pop
      case Some(INDENTATION(spaces)) if spaces < indents.head =>
        val (dropped, kept) = indents.partition(_ > spaces)
        (dropped map (_ => DEDENT())) ::: processIndentations(tokens.tail, kept)

      // if the indentation level stays unchanged, no tokens are produced
      case Some(INDENTATION(spaces)) if spaces == indents.head =>
        processIndentations(tokens.tail, indents)

      // other tokens are ignored
      case Some(token) =>
        token :: processIndentations(tokens.tail, indents)

      // the final step is to produce a DEDENT for each indentation level still remaining, thus
      // "closing" the remaining open INDENTS
      case None =>
        indents.filter(_ > 0).map(_ => DEDENT())

    }
  }

  def identifier: Parser[IDENTIFIER] = positioned {
    "[a-zA-Z_][a-zA-Z0-9_]*".r ^^ { str =>
//      println(s"==================>> $str")
      IDENTIFIER(str) }
  }

  def literal: Parser[LITERAL] = positioned {
    """"[^"]*"""".r ^^ { str =>
      val content = str.substring(1, str.length - 1)
      LITERAL(content)
    }
  }

  def indentation: Parser[INDENTATION] = positioned {
    "\n[ ]*".r ^^ { whitespace =>
      val nSpaces = whitespace.length - 1
      INDENTATION(nSpaces)
    }
  }

  def exit          = positioned { "exit"          ^^ (_ => EXIT()) }
  def readInput     = positioned { "read input"    ^^ (_ => READINPUT()) }
  def callService   = positioned { "call service"  ^^ (_ => CALLSERVICE()) }
  def switch        = positioned { "switch"        ^^ (_ => SWITCH()) }
  def otherwise     = positioned { "otherwise"     ^^ (_ => OTHERWISE()) }
  def colon         = positioned { ":"             ^^ (_ => COLON()) }
  def arrow         = positioned { "->"            ^^ (_ => ARROW()) }
  def equals        = positioned { "=="            ^^ (_ => EQUALS()) }
  def comma         = positioned { ","             ^^ (_ => COMMA()) }


  def timeToPick: Parser[TIME_TO_PICK] = positioned {
    """\d{1,2}:\d{1,2}\s(am|pm)""".r ^^ { str =>
//      println(s"=======TIME_TO_PICK===========>> $str")
      TIME_TO_PICK(str) }
  }

  def vehicle: Parser[VEHICLE] = positioned {
    "[a-zA-Z_][a-zA-Z]*".r ^^ { str =>
//      println(s"=======VEHICLE===========>> $str")
      VEHICLE(str) }
  }


  def duration: Parser[DURATION] = positioned {
   """\d{1,2}(h|m|s)\s\d{1,2}(h|m|s)""".r ^^ { str =>
//     println(s"=======DURATION===========>> $str")
     DURATION(str) }
  }

  def distance: Parser[DISTANCE] = positioned {
    """\d{1,}\.\d{2}""".r ^^ { str =>
//      println(s"=======DISTANCE===========>> $str")
      DISTANCE(str) }
  }

  def paid: Parser[PAID] = positioned {
    """UYU\d{1,}\.\d{2}""".r ^^ { str =>
//      println(s"=======PAID===========>> $str")
      PAID(str)
    }
  }


}
