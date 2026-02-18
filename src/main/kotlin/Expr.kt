abstract class Expr : Ast {
    constructor(type: String) : super(type)
}

class BinExpr(val op: Op, val lhs: Expr, val rhs: Expr) : Expr("BinExpr") {
    enum class Op {
        PLUS,
        MINUS,
        MULTIPLY,
        DIVIDE,
        LESS_THAN,
        GREATER_THAN,
        LESS_THAN_OR_EQUAL,
        GREATER_THAN_OR_EQUAL,
        EQUAL,
        NOT_EQUAL,
    }
}

class MyInt(val value: Long) : Expr("Int")

class Assignment(val name: String, val expression: Expr) : Expr("Assignment")

class Ident(val name: String) : Expr("Ident")

class Seq(vararg val bodies: Expr) : Expr("Seq")

class If(val condition: Expr, val thenBranch: Expr, val elseBranch: Expr) : Expr("If")

class While(val condition: Expr, val body: Expr) : Expr("While")

class Call(val func: String, vararg val args: Expr) : Expr("Call")
