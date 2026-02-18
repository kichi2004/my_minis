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

    test("1 AND 1 == 1") {
        val e = """{"type": "&", "operands": [1, 1]}"""
        JsonObjectEvaluator.evaluateJsonObject(e) shouldBe 1
    }

    test("1 AND 0 == 0") {
        val e = """{"type": "&", "operands": [1, 0]}"""
        JsonObjectEvaluator.evaluateJsonObject(e) shouldBe 0
    }

    test("1 OR 0 == 1") {
        val e = """{"type": "|", "operands": [1, 0]}"""
        JsonObjectEvaluator.evaluateJsonObject(e) shouldBe 1
    }

    test("0 OR 0 == 0") {
        val e = """{"type": "|", "operands": [0, 0]}"""
        JsonObjectEvaluator.evaluateJsonObject(e) shouldBe 0
    }

    test("5 XOR 3 == 6") {
        val e = """{"type": "^", "operands": [5, 3]}"""
        JsonObjectEvaluator.evaluateJsonObject(e) shouldBe 6
    }

    test("12 XOR 10 == 6") {
        val e = """{"type": "^", "operands": [12, 10]}"""
        JsonObjectEvaluator.evaluateJsonObject(e) shouldBe 6
    }

    test("NOT 1 == 0") {
        val e = """{"type": "not", "operand": 1}"""
        JsonObjectEvaluator.evaluateJsonObject(e) shouldBe 0
    }

    test("NOT 0 == 1") {
        val e = """{"type": "not", "operand": 0}"""
        JsonObjectEvaluator.evaluateJsonObject(e) shouldBe 1
    }

    test("evaluateJson(\"{\"type\": \"if\", \"condition\": 1, \"then\": 1, \"else\": 2}\") == 1") {
        val e = """{"type": "if", "condition": 1, "then": 1, "else": 2}"""
        JsonObjectEvaluator.evaluateJsonObject(e) shouldBe 1
    }

    test("if(not 0) then 5 else 3 == 5") {
        val e = """{"type": "if", "condition": {"type": "not", "operand": 0}, "then": 5, "else": 3}"""
        JsonObjectEvaluator.evaluateJsonObject(e) shouldBe 5
    }

    test("if(1 & 1) then 10 else 20 == 10") {
        val e = """{"type": "if", "condition": {"type": "&", "operands": [1, 1]}, "then": 10, "else": 20}"""
        JsonObjectEvaluator.evaluateJsonObject(e) shouldBe 10
    }

    test("if(0 | 1) then 7 else 8 == 7") {
        val e = """{"type": "if", "condition": {"type": "|", "operands": [0, 1]}, "then": 7, "else": 8}"""
        JsonObjectEvaluator.evaluateJsonObject(e) shouldBe 7
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

    test("i = 0; while(i < 5 & i >= 0) { i = i + 1; } i == 5") {
        val e = """{
            "type": "seq",
            "expressions": [
                {"type": "assign", "name": "i", "value": 0},
                {"type": "while",
                    "condition": {"type": "&", "operands": [{"type": "<", "operands": [{"type": "id", "name": "i"}, 5]}, {"type": ">=", "operands": [{"type": "id", "name": "i"}, 0]}]},
                    "body": {"type": "seq", "expressions": [
                        {"type": "assign", "name": "i", "value": {"type": "+", "operands": [{"type": "id", "name": "i"}, 1]}}
                    ]}
                },
                {"type": "id", "name": "i"}
            ]
        }"""
        JsonObjectEvaluator.evaluateJsonObject(e) shouldBe 5
    }

    test("i = 10; while(not (i == 0)) { i = i - 1; } i == 0") {
        val e = """{
            "type": "seq",
            "expressions": [
                {"type": "assign", "name": "i", "value": 10},
                {"type": "while",
                    "condition": {"type": "not", "operand": {"type": "==", "operands": [{"type": "id", "name": "i"}, 0]}},
                    "body": {"type": "seq", "expressions": [
                        {"type": "assign", "name": "i", "value": {"type": "-", "operands": [{"type": "id", "name": "i"}, 1]}}
                    ]}
                },
                {"type": "id", "name": "i"}
            ]
        }"""
        JsonObjectEvaluator.evaluateJsonObject(e) shouldBe 0
    }

    test("def add(a, b) { return a + b; }, add(1, 2) ==> 3") {
        val program = """[
            {"type": "def", "name": "add", "params": ["a", "b"], "body": {"type": "+", "operands": [{"type": "id", "name": "a"}, {"type": "id", "name": "b"}]}},
            {"type": "call", "name": "add", "args": [1, 2]}
        ]"""
        JsonObjectEvaluator.evaluateJsonProgram(program) shouldBe 3
    }
})
