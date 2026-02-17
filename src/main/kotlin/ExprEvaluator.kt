class ExprEvaluator {
    fun evaluate(expr: Expr): Long =
        when (expr) {
            is MyInt -> expr.value
            is BinExpr -> {
                val lhs = evaluate(expr.lhs)
                val rhs = evaluate(expr.rhs)
                when (expr.op) {
                    BinExpr.Op.PLUS -> lhs + rhs
                    BinExpr.Op.MINUS -> lhs - rhs
                    BinExpr.Op.MULTIPLY -> lhs * rhs
                    BinExpr.Op.DIVIDE -> lhs / rhs
                }
            }

            else -> throw IllegalArgumentException("Unknown expression type: $expr")
        }
}