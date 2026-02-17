open class Expr(val type: String)

class BinExpr(val op: Op, val lhs: Expr, val rhs: Expr) : Expr("BinExpr") {
    enum class Op {
        PLUS,
        MINUS,
        MULTIPLY,
        DIVIDE
    }
}

class MyInt(val value: Long) : Expr("Int")

