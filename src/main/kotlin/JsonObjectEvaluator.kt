import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.longOrNull

sealed class JsonObjectEvaluator {
    companion object {
        val evaluator = ExprEvaluator()

        fun evaluateJsonObject(jsonString: String): Long? {
            val jsonElement = Json.parseToJsonElement(jsonString)
            return evaluator.evaluate(translateToExpr(jsonElement))
        }

        fun evaluateJsonProgram(jsonString: String): Long? {
            val json = Json.parseToJsonElement(jsonString).jsonArray
            val env = mutableMapOf<String, EnvValue>()
            val bodies = mutableListOf<Expr>()
            val functions = mutableListOf<JsonObject>()

            json.forEach {
                it as JsonObject
                if ((it["type"] as? JsonPrimitive)?.content == "def") {
                    functions += it
                } else {
                    bodies += translateToExpr(it)
                }
            }

            functions.forEach { f ->
                require(f.size == 4) { "Expected function definition of size 4, got: ${f.size}" }
                val name = f["name"]!!.jsonPrimitive.content
                env[name] = Function(
                    Func(
                        name,
                        f["params"]!!.jsonArray.map { it.jsonPrimitive.content },
                        translateToExpr(f["body"]!!)
                    )
                )
            }
            return bodies.map { evaluator.evaluate(it, env) }.lastOrNull()
        }

        fun translateToExpr(json: JsonElement): Expr {
            if (json is JsonObject) {
                val op = json["type"]?.jsonPrimitive?.content
                    ?: throw IllegalArgumentException("Expected type field in object")
                val operands = json["operands"]?.jsonArray
                if (op in JsonEvaluatorCommon.binExprOps) {
                    requireNotNull(operands)
                    require(operands.size == 2) { "Expected operands array of size 2, got: ${operands.size}" }
                    return JsonEvaluatorCommon.buildBinExpr(
                        op,
                        translateToExpr(operands[0]),
                        translateToExpr(operands[1])
                    )
                }

                return when (op) {
                    "not" -> Not(translateToExpr(json["operand"]!!))
                    "seq" -> {
                        val expressions = json["expressions"]?.jsonArray
                        requireNotNull(expressions)
                        Seq(*expressions.map(::translateToExpr).toTypedArray())
                    }
                    "if" -> If(translateToExpr(json["condition"]!!), translateToExpr(json["then"]!!), translateToExpr(json["else"]!!))
                    "while" -> While(translateToExpr(json["condition"]!!), translateToExpr(json["body"]!!))
                    "assign" -> Assignment(json["name"]!!.jsonPrimitive.content, translateToExpr(json["value"]!!))
                    "id" -> Ident(json["name"]!!.jsonPrimitive.content)
                    "call" -> Call(
                        json["name"]!!.jsonPrimitive.content,
                        *json["args"]!!.jsonArray.map(::translateToExpr).toTypedArray()
                    )
                    else -> error("unreachable")
                }
            }
            if (json is JsonPrimitive) {
                return json.longOrNull?.let { tInt(it) }
                    ?: throw IllegalArgumentException("Expected long value, got: ${json.content}")
            }
            throw IllegalArgumentException("Not implemented: $json")
        }
    }
}
