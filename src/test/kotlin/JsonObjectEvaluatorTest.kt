import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class JsonObjectEvaluatorTest : FunSpec({
    test("evaluateJson(\"1\") == 1") {
        val e = "1"
        JsonObjectEvaluator.evaluateJsonObject(e) shouldBe 1
    }

    test("evaluateJson(\"{\"type\": \"+\", \"operands\": [1, 2]}\") == 3") {
        val e = """{"type": "+", "operands": [1, 2]}"""
        JsonObjectEvaluator.evaluateJsonObject(e) shouldBe 3
    }

    test("evaluateJson(\"{\"type\": \"-\", \"operands\": [1, 2]}\") == -1") {
        val e = """{"type": "-", "operands": [1, 2]}"""
        JsonObjectEvaluator.evaluateJsonObject(e) shouldBe -1
    }

    test("evaluateJson(\"{\"type\": \"*\", \"operands\": [2, 2]}\") == 4") {
        val e = """{"type": "*", "operands": [2, 2]}"""
        JsonObjectEvaluator.evaluateJsonObject(e) shouldBe 4
    }

    test("evaluateJson(\"{\"type\": \"/\", \"operands\": [2, 2]}\") == 1") {
        val e = """{"type": "/", "operands": [2, 2]}"""
        JsonObjectEvaluator.evaluateJsonObject(e) shouldBe 1
    }

    test("1 < 2 == 1") {
        val e = """{"type": "<", "operands": [1, 2]}"""
        JsonObjectEvaluator.evaluateJsonObject(e) shouldBe 1
    }

    test("1 > 2 == 0") {
        val e = """{"type": ">", "operands": [1, 2]}"""
        JsonObjectEvaluator.evaluateJsonObject(e) shouldBe 0
    }

    test("1 <= 2 == 1") {
        val e = """{"type": "<=", "operands": [1, 2]}"""
        JsonObjectEvaluator.evaluateJsonObject(e) shouldBe 1
    }

    test("1 >= 2 == 0") {
        val e = """{"type": ">=", "operands": [1, 2]}"""
        JsonObjectEvaluator.evaluateJsonObject(e) shouldBe 0
    }

    test("1 == 1 == 1") {
        val e = """{"type": "==", "operands": [1, 1]}"""
        JsonObjectEvaluator.evaluateJsonObject(e) shouldBe 1
    }

    test("1 != 1 == 0") {
        val e = """{"type": "!=", "operands": [1, 1]}"""
        JsonObjectEvaluator.evaluateJsonObject(e) shouldBe 0
    }

    test("evaluateJson(\"{\"type\": \"if\", \"condition\": 1, \"then\": 1, \"else\": 2}\") == 1") {
        val e = """{"type": "if", "condition": 1, "then": 1, "else": 2}"""
        JsonObjectEvaluator.evaluateJsonObject(e) shouldBe 1
    }

    test("evaluateJson(\"{\"type\": \"seq\", \"expressions\": [1, 2, 3]}\") == 3") {
        val e = """{"type": "seq", "expressions": [1, 2, 3]}"""
        JsonObjectEvaluator.evaluateJsonObject(e) shouldBe 3
    }

    test("i = 0; while(i < 10) { i = i + 1; } i == 10") {
        val e = """{
            "type": "seq",
            "expressions": [
                {"type": "assign", "name": "x", "value": 1},
                {"type": "while",
                    "condition": {"type": "<", "operands": [{"type": "id", "name": "x"}, 10]},
                    "body": {"type": "seq", "expressions": [
                        {"type": "assign", "name": "x", "value": {"type": "+", "operands": [{"type": "id", "name": "x"}, 1]}}
                    ]}
                },
                {"type": "id", "name": "x"}
            ]
        }"""
        JsonObjectEvaluator.evaluateJsonObject(e) shouldBe 10
    }

    test("def add(a, b) { return a + b; }, add(1, 2) ==> 3") {
        val program = """[
            {"type": "def", "name": "add", "params": ["a", "b"], "body": {"type": "+", "operands": [{"type": "id", "name": "a"}, {"type": "id", "name": "b"}]}},
            {"type": "call", "name": "add", "args": [1, 2]}
        ]"""
        JsonObjectEvaluator.evaluateJsonProgram(program) shouldBe 3
    }
})
