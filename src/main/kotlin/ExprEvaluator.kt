import BinExpr.Op.*
import kotlin.collections.forEachIndexed

sealed class EnvValue
data class Function(val func: Func) : EnvValue()
data class Value(val value: Long?) : EnvValue()

typealias Env = MutableMap<String, EnvValue>

class ExprEvaluator {
    private fun evaluateMath(expr: BinExpr, env: Env): Long {
        val lhs = evaluate(expr.lhs, env) ?: error("Math evaluation resulted in null")
        val rhs = evaluate(expr.rhs, env) ?: error("Math evaluation resulted in null")

        return when (expr.op) {
            PLUS -> lhs + rhs
            MINUS -> lhs - rhs
            MULTIPLY -> lhs * rhs
            DIVIDE -> lhs / rhs
            XOR -> lhs xor rhs
            else -> error("Unknown operator: ${expr.op}")
        }
    }

    private fun evaluateComp(expr: BinExpr, env: Env): Long {
        val lhs = evaluate(expr.lhs, env) ?: error("Comp evaluation resulted in null")
        val rhs = evaluate(expr.rhs, env) ?: error("Comp evaluation resulted in null")

        return when (expr.op) {
            EQUAL -> if (lhs == rhs) 1 else 0
            NOT_EQUAL -> if (lhs != rhs) 1 else 0
            LESS_THAN -> if (lhs < rhs) 1 else 0
            GREATER_THAN -> if (lhs > rhs) 1 else 0
            LESS_THAN_OR_EQUAL -> if (lhs <= rhs) 1 else 0
            GREATER_THAN_OR_EQUAL -> if (lhs >= rhs) 1 else 0
            AND -> if (lhs != 0L && rhs != 0L) 1 else 0
            OR -> if (lhs != 0L || rhs != 0L) 1 else 0
            else -> error("Unknown operator: ${expr.op}")
        }
    }

    fun evaluateProgram(program: Program): Long? {
        val env = mutableMapOf<String, EnvValue>()
        program.functions.forEach { env[it.name] = Function(it) }
        return evaluate(program.bodies, env)
    }

    fun evaluate(expr: Expr, env: Env = mutableMapOf()): Long? =
        when (expr) {
            is MyInt -> expr.value
            is BinExpr -> {
                when (expr.op) {
                    PLUS, MINUS, MULTIPLY, DIVIDE, XOR -> evaluateMath(expr, env)
                    EQUAL, NOT_EQUAL, LESS_THAN, GREATER_THAN,
                    LESS_THAN_OR_EQUAL, GREATER_THAN_OR_EQUAL, AND, OR -> evaluateComp(
                        expr,
                        env
                    )
                }
            }
            is Not -> return if (requireNotNull(evaluate(expr.expr, env)) == 0L) 1 else 0
            is Seq -> {
                var result: Long? = null
                expr.bodies.forEach { result = evaluate(it, env) }
                return result
            }
            is Assignment -> {
                val value = evaluate(expr.expression, env)
                env[expr.name] = Value(value)
                return value
            }
            is Ident -> {
                require(env.containsKey(expr.name)) { "Unbound variable: ${expr.name}" }
                return (env[expr.name] as Value).value ?: error("Variable value is null: ${expr.name}")
            }
            is If -> {
                evaluate(
                    if (evaluate(expr.condition, env) != 0L) expr.thenBranch else expr.elseBranch,
                    env
                )
            }
            is While -> {
                while (evaluate(expr.condition, env) != 0L) {
                    evaluate(expr.body, env)
                }
                return null
            }
            is Call -> {
                val e = env[expr.func] ?: throw IllegalArgumentException("Unbound function: ${expr.func}")
                require(e is Function) { "Not a function: ${expr.func}" }
                val newEnv = env.toMutableMap()
                expr.args.forEachIndexed { i, expr -> newEnv[e.func.paramNames[i]] = Value(evaluate(expr, env)) }
                return evaluate(e.func.body, newEnv)
            }
            else -> error("Unknown expression type: $expr")
        }
}
