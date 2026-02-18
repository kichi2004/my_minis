sealed class JsonEvaluatorCommon {
    companion object {
        val binExprOps: Set<String> = setOf("+", "-", "*", "/", "<", ">", "<=", ">=", "==", "!=")

        fun buildBinExpr(op: String, lhs: Expr, rhs: Expr): BinExpr =
            when (op) {
                "+" -> tAdd(lhs, rhs)
                "-" -> tSub(lhs, rhs)
                "*" -> tMul(lhs, rhs)
                "/" -> tDiv(lhs, rhs)
                "<" -> tLt(lhs, rhs)
                ">" -> tGt(lhs, rhs)
                "<=" -> tLe(lhs, rhs)
                ">=" -> tGe(lhs, rhs)
                "==" -> tEq(lhs, rhs)
                "!=" -> tNe(lhs, rhs)
                else -> throw IllegalArgumentException("unreachable")
            }
    }
}
