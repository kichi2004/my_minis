import JsonEvaluatorCommon.Companion.buildBinExpr
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.longOrNull

sealed class JsonArrayEvaluator {
    companion object {
        val evaluator = ExprEvaluator()

        fun evaluateJsonArray(jsonString: String): Long? {
            val jsonElement = Json.parseToJsonElement(jsonString)
            return evaluator.evaluate(translateToExpr(jsonElement))
        }

        fun evaluateJsonProgram(jsonString: String): Long? {
            val json = Json.parseToJsonElement(jsonString).jsonArray
            val env = mutableMapOf<String, EnvValue>()
            val bodies = mutableListOf<Expr>()
            val functions = mutableListOf<JsonArray>()

            json.forEach {
                it as JsonArray
                if (it.isNotEmpty() && (it[0] as? JsonPrimitive)?.content == "def") {
                    functions += it
                } else {
                    bodies += translateToExpr(it)
                }
            }

            functions.forEach { f ->
                require(f.size == 4) { "Expected function definition of size 4, got: ${f.size}" }
                val name = f[1].jsonPrimitive.content
                env[name] = Function(Func(name, f[2].jsonArray.map { it.jsonPrimitive.content }, translateToExpr(f[3])))
            }
            return bodies.map { evaluator.evaluate(it, env) }.lastOrNull()
        }

        fun translateToExpr(json: JsonElement): Expr {
            if (json is JsonArray) {
                val op = json[0].jsonPrimitive.content
                if (op in JsonEvaluatorCommon.binExprOps) {
                    require(json.size == 3) { "Expected array of size 3, got: ${json.size} for operator: $op" }
                    return buildBinExpr(op, translateToExpr(json[1]), translateToExpr(json[2]))
                }
                return when (op) {
                    "seq" -> Seq(*json.drop(1).map(::translateToExpr).toTypedArray())
                    "if" -> {
                        require(json.size == 4) { "Expected array of size 4 for 'if' statement, got: ${json.size}" }
                        If(translateToExpr(json[1]), translateToExpr(json[2]), translateToExpr(json[3]))
                    }
                    "while" -> While(translateToExpr(json[1]), translateToExpr(json[2]))
                    "assign" -> Assignment(json[1].jsonPrimitive.content, translateToExpr(json[2]))
                    "id" -> Ident(json[1].jsonPrimitive.content)
                    "call" -> Call(json[1].jsonPrimitive.content, *json.drop(2).map(::translateToExpr).toTypedArray())
                    else -> error("unreachable")
                }
            }
            if (json is JsonPrimitive) {
                return json.longOrNull?.let { tInt(it) } ?: throw IllegalArgumentException("Expected long value, got: ${json.content}")
            }
            throw IllegalArgumentException("Not implemented: $json")
        }
    }
}