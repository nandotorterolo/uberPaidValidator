package com.uberPaid.compiler

import com.uberPaid.lexer.WorkflowLexer
import com.uberPaid.parser.{WorkflowAST, WorkflowParser}


object WorkflowCompiler {
  def apply(code: String): Either[WorkflowCompilationError, WorkflowAST] = {
    for {
      tokens <- WorkflowLexer(code).right
      ast <- WorkflowParser(tokens).right
    } yield ast
  }
}
