package com.uberPaid.parser

import com.uberPaid.compiler.{Location, WorkflowParserError}
import com.uberPaid.lexer._

import scala.util.parsing.combinator.Parsers
import scala.util.parsing.input.{NoPosition, Position, Reader}

object WorkflowParser extends Parsers {
  override type Elem = WorkflowToken

  class WorkflowTokenReader(tokens: Seq[WorkflowToken]) extends Reader[WorkflowToken] {
    override def first: WorkflowToken = tokens.head
    override def atEnd: Boolean = tokens.isEmpty
    override def pos: Position = tokens.headOption.map(_.pos).getOrElse(NoPosition)
    override def rest: Reader[WorkflowToken] = new WorkflowTokenReader(tokens.tail)
  }

  def apply(tokens: Seq[WorkflowToken]): Either[WorkflowParserError, WorkflowAST] = {
    val reader = new WorkflowTokenReader(tokens)
    program(reader) match {
      case NoSuccess(msg, next) => Left(WorkflowParserError(Location(next.pos.line, next.pos.column), msg))
      case Success(result, next) => Right(result)
    }
  }

  def program: Parser[WorkflowAST] = positioned {
//    phrase(block)
    phrase(blockUber)
  }

  def blockUber: Parser[WorkflowAST] = positioned {
    rep1(statementUber) ^^ (stmtList => stmtList reduceRight AndThen)
  }

  def statementUber: Parser[WorkflowAST] = positioned {
    val line = timeToPick ~ vehicle ~ duration ~ distance ~ paid ^^ {
      case TIME_TO_PICK(t) ~ VEHICLE(v) ~ DURATION(dur) ~ DISTANCE(dis) ~ PAID(p) => UberLine(t, v, dur, dis, p)
    }
    line
  }

  def block: Parser[WorkflowAST] = positioned {
    rep1(statement) ^^ { case stmtList => stmtList reduceRight AndThen }
  }

  def statement: Parser[WorkflowAST] = positioned {
    val exit = EXIT() ^^ (_ => Exit)
    val readInput = READINPUT() ~ rep(identifier ~ COMMA()) ~ identifier ^^ {
      case read ~ inputs ~ IDENTIFIER(lastInput) => ReadInput(inputs.map(_._1.str) ++ List(lastInput))
    }
    val callService = CALLSERVICE() ~ literal ^^ {
      case call ~ LITERAL(serviceName) => CallService(serviceName)
    }
    val switch = SWITCH() ~ COLON() ~ INDENT() ~ rep1(ifThen) ~ opt(otherwiseThen) ~ DEDENT() ^^ {
      case _ ~ _ ~ _ ~ ifs ~ otherwise ~ _ => Choice(ifs ++ otherwise)
    }
    exit | readInput | callService | switch
  }

  def ifThen: Parser[IfThen] = positioned {
    (condition ~ ARROW() ~ INDENT() ~ block ~ DEDENT()) ^^ {
      case cond ~ _ ~ _ ~ block ~ _ => IfThen(cond, block)
    }
  }

  def otherwiseThen: Parser[OtherwiseThen] = positioned {
    (OTHERWISE() ~ ARROW() ~ INDENT() ~ block ~ DEDENT()) ^^ {
      case _ ~ _ ~ _ ~ block ~ _ => OtherwiseThen(block)
    }
  }

  def condition: Parser[Equals] = positioned {
    (identifier ~ EQUALS() ~ literal) ^^ { case IDENTIFIER(id) ~ eq ~ LITERAL(lit) => Equals(id, lit) }
  }

  private def identifier: Parser[IDENTIFIER] = positioned {
    accept("identifier", { case id @ IDENTIFIER(name) => id })
  }

  private def literal: Parser[LITERAL] = positioned {
    accept("string literal", { case lit @ LITERAL(name) => lit })
  }

  private def timeToPick: Parser[TIME_TO_PICK] = positioned {
    accept("timeToPick literal", { case t @ TIME_TO_PICK(name) => t })
  }

  private def vehicle: Parser[VEHICLE] = positioned {
    accept("vehicle literal", { case v @ VEHICLE(name) => v })
  }

  private def duration: Parser[DURATION] = positioned {
    accept("duration", { case d @ DURATION(name) => d })
  }

  private def distance: Parser[DISTANCE] = positioned {
    accept("distance", { case d @ DISTANCE(name) => d })
  }

  private def paid: Parser[PAID] = positioned {
    accept("paid", { case p @ PAID(name) => p })
  }

}
