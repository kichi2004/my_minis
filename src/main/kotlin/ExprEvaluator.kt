import BinExpr.Op.*

typealias Env = MutableMap<String, Long?>

class ExprEvaluator {
    private fun evaluateMath(expr: BinExpr, env: Env): Long {
        val lhs = internalEvaluate(expr.lhs, env)?: throw IllegalStateException("Math evaluation resulted in null")
        val rhs = internalEvaluate(expr.rhs, env) ?: throw IllegalStateException("Math evaluation resulted in null")

        return when (expr.op) {
            PLUS -> lhs + rhs
            MINUS -> lhs - rhs
            MULTIPLY -> lhs * rhs
            DIVIDE -> lhs / rhs
            else -> throw IllegalArgumentException("Unknown operator: ${expr.op}")
        }
    }

    private fun evaluateComp(expr: BinExpr, env: Env): Long {
        val lhs = internalEvaluate(expr.lhs, env) ?: throw IllegalStateException("Comp evaluation resulted in null")
        val rhs = internalEvaluate(expr.rhs, env) ?: throw IllegalStateException("Comp evaluation resulted in null")

        return when (expr.op) {
            EQUAL -> if (lhs == rhs) 1 else 0
            NOT_EQUAL -> if (lhs != rhs) 1 else 0
            LESS_THAN -> if (lhs < rhs) 1 else 0
            GREATER_THAN -> if (lhs > rhs) 1 else 0
            LESS_THAN_OR_EQUAL -> if (lhs <= rhs) 1 else 0
            GREATER_THAN_OR_EQUAL -> if (lhs >= rhs) 1 else 0
            else -> throw IllegalArgumentException("Unknown operator: ${expr.op}")
        }
    }

    fun evaluate(expr: Expr) = internalEvaluate(expr, mutableMapOf())

    fun internalEvaluate(expr: Expr, env: Env): Long? =
        when (expr) {
            is MyInt -> expr.value
            is BinExpr -> {
                when (expr.op) {
                    PLUS, MINUS, MULTIPLY, DIVIDE -> evaluateMath(expr, env)
                    EQUAL, NOT_EQUAL, LESS_THAN, GREATER_THAN, LESS_THAN_OR_EQUAL, GREATER_THAN_OR_EQUAL -> evaluateComp(expr, env)
                }
            }
            is Seq -> {
                var result: Long? = null
                expr.bodies.forEach { result = internalEvaluate(it, env) }
                return result
            }
            is Assignment -> {
                val value = internalEvaluate(expr.expression, env)
                env[expr.name] = value
                return value
            }
            is Ident -> env[expr.name] ?: throw IllegalArgumentException("Unbound variable: ${expr.name}")
            is If -> {
                internalEvaluate(
                    if (internalEvaluate(expr.condition, env) != 0L) expr.thenBranch else expr.elseBranch,
                    env
                )
            }
            is While -> {
                while (internalEvaluate(expr.condition, env) != 0L) {
                    internalEvaluate(expr.body, env)
                }
                return null
            }
            else -> throw IllegalArgumentException("Unknown expression type: $expr")
        }
}