package tool

import org.antlr.v4.runtime.{ANTLRInputStream, CommonTokenStream}
import parser.{SimpleCLexer, SimpleCParser}
import visitors.SimpleCCodeVisitor
import util.CodeFormat.formatCCode

/**
  * Created by sam_coope on 24/10/2016.
  */
object ID {
  def main(args: Array[String]): Unit = {

    val input: ANTLRInputStream = new ANTLRInputStream(System.in)
    val lexer: SimpleCLexer = new SimpleCLexer(input)
    val tokens: CommonTokenStream = new CommonTokenStream(lexer)
    val parser: SimpleCParser = new SimpleCParser(tokens)
    val ctx: SimpleCParser.ProgramContext = parser.program

    if (parser.getNumberOfSyntaxErrors > 0) System.exit(1)

    val tc: Typechecker = new Typechecker
    tc.visit(ctx)
    tc.resolve()
    if (tc.hasErrors) {
      System.err.println("Typechecker in ID has failed")
      import scala.collection.JavaConversions._
      for (err <- tc.getErrors) {
        System.err.println("  " + err)
      }
      System.exit(1)
    }

    val codeVisitor = new SimpleCCodeVisitor
    val thing = codeVisitor.visit(ctx)
    println(formatCCode(thing))

    System.exit(0)
  }
}
