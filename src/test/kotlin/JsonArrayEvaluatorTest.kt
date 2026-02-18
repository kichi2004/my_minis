import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class JsonArrayEvaluatorTest : FunSpec({
    test("1 + 1 ==> 2") {
        val e = """["+", 1, 1]"""
        JsonArrayEvaluator.evaluateJsonArray(e) shouldBe 2
    }

    test("1 + 2 + 3 ==> 7") {
        val e = """["+", 1, ["+", 2, 3]]"""
        JsonArrayEvaluator.evaluateJsonArray(e) shouldBe 6
    }

    test("1 - 1 ==> 0") {
        val e = """["-", 1, 1]"""
        JsonArrayEvaluator.evaluateJsonArray(e) shouldBe 0
    }

    test("1 - 2 ==> -1") {
        val e = """["-", 1, 2]"""
        JsonArrayEvaluator.evaluateJsonArray(e) shouldBe -1
    }

    test("1 * 1 ==> 1") {
        val e = """["*", 1, 1]"""
        JsonArrayEvaluator.evaluateJsonArray(e) shouldBe 1
    }

    test("1 * 0 ==> 0") {
        val e = """["*", 1, 0]"""
        JsonArrayEvaluator.evaluateJsonArray(e) shouldBe 0
    }

    test("2 * 2 ==> 4") {
        val e = """["*", 2, 2]"""
        JsonArrayEvaluator.evaluateJsonArray(e) shouldBe 4
    }

    test("0 / 1 ==> 0") {
        val e = """["/", 0, 1]"""
        JsonArrayEvaluator.evaluateJsonArray(e) shouldBe 0
    }

    test("2 / 1 ==> 2") {
        val e = """["/", 2, 1]"""
        JsonArrayEvaluator.evaluateJsonArray(e) shouldBe 2
    }

    test("6 / 2 ==> 3") {
        val e = """["/", 6, 2]"""
        JsonArrayEvaluator.evaluateJsonArray(e) shouldBe 3
    }

    test("(1 + (2 * 3) - 1) / 2 == 3") {
        val e = """["/",
            ["-",
                ["+", 1, ["*", 2, 3]],
                1],
            2]"""
        JsonArrayEvaluator.evaluateJsonArray(e) shouldBe 3
    }

    test("1 < 2 ==> 1") {
        val e = """["<", 1, 2]"""
        JsonArrayEvaluator.evaluateJsonArray(e) shouldBe 1
    }

    test("1 > 2 ==> 0") {
        val e = """[">", 1, 2]"""
        JsonArrayEvaluator.evaluateJsonArray(e) shouldBe 0
    }

    test("1 <= 2 ==> 1") {
        val e = """["<=", 1, 2]"""
        JsonArrayEvaluator.evaluateJsonArray(e) shouldBe 1
    }

    test("1 >= 2 ==> 0") {
        val e = """[">=", 1, 2]"""
        JsonArrayEvaluator.evaluateJsonArray(e) shouldBe 0
    }

    test("1 == 1 ==> 1") {
        val e = """["==", 1, 1]"""
        JsonArrayEvaluator.evaluateJsonArray(e) shouldBe 1
    }

    test("1 != 1 ==> 0") {
        val e = """["!=", 1, 1]"""
        JsonArrayEvaluator.evaluateJsonArray(e) shouldBe 0
    }

    test("1 AND 1 ==> 1") {
        val e = """["&", 1, 1]"""
        JsonArrayEvaluator.evaluateJsonArray(e) shouldBe 1
    }

    test("1 AND 0 ==> 0") {
        val e = """["&", 1, 0]"""
        JsonArrayEvaluator.evaluateJsonArray(e) shouldBe 0
    }

    test("1 OR 0 ==> 1") {
        val e = """["|", 1, 0]"""
        JsonArrayEvaluator.evaluateJsonArray(e) shouldBe 1
    }

    test("0 OR 0 ==> 0") {
        val e = """["|", 0, 0]"""
        JsonArrayEvaluator.evaluateJsonArray(e) shouldBe 0
    }

    test("5 XOR 3 ==> 6") {
        val e = """["^", 5, 3]"""
        JsonArrayEvaluator.evaluateJsonArray(e) shouldBe 6
    }

    test("12 XOR 10 ==> 6") {
        val e = """["^", 12, 10]"""
        JsonArrayEvaluator.evaluateJsonArray(e) shouldBe 6
    }

    test("NOT 1 ==> 0") {
        val e = """["not", 1]"""
        JsonArrayEvaluator.evaluateJsonArray(e) shouldBe 0
    }

    test("NOT 0 ==> 1") {
        val e = """["not", 0]"""
        JsonArrayEvaluator.evaluateJsonArray(e) shouldBe 1
    }

    test("if(1) then 1 else 2 ==> 1") {
        val e = """["if", 1, 1, 2]"""
        JsonArrayEvaluator.evaluateJsonArray(e) shouldBe 1
    }

    test("if(not 0) then 5 else 3 ==> 5") {
        val e = """["if", ["not", 0], 5, 3]"""
        JsonArrayEvaluator.evaluateJsonArray(e) shouldBe 5
    }

    test("if(1 & 1) then 10 else 20 ==> 10") {
        val e = """["if", ["&", 1, 1], 10, 20]"""
        JsonArrayEvaluator.evaluateJsonArray(e) shouldBe 10
    }

    test("if(0 | 1) then 7 else 8 ==> 7") {
        val e = """["if", ["|", 0, 1], 7, 8]"""
        JsonArrayEvaluator.evaluateJsonArray(e) shouldBe 7
    }

    test("{1; 2; 3} ==> 3") {
        val e = """["seq", 1, 2, 3]"""
        JsonArrayEvaluator.evaluateJsonArray(e) shouldBe 3
    }

    test("i = 0; while(i < 10) { i = i + 1; } i ==> 10") {
        val e = """[
            "seq",
            ["assign", "i", 0],
            ["while",
                ["<", ["id", "i"], 10],
                ["assign", "i", ["+", ["id", "i"], 1]]],
            ["id", "i"]
        ]"""
        JsonArrayEvaluator.evaluateJsonArray(e) shouldBe 10
    }

    test("i = 0; while(i < 5 & i >= 0) { i = i + 1; } i ==> 5") {
        val e = """[
            "seq",
            ["assign", "i", 0],
            ["while",
                ["&", ["<", ["id", "i"], 5], [">=", ["id", "i"], 0]],
                ["assign", "i", ["+", ["id", "i"], 1]]],
            ["id", "i"]
        ]"""
        JsonArrayEvaluator.evaluateJsonArray(e) shouldBe 5
    }

    test("i = 10; while(not (i == 0)) { i = i - 1; } i ==> 0") {
        val e = """[
            "seq",
            ["assign", "i", 10],
            ["while",
                ["not", ["==", ["id", "i"], 0]],
                ["assign", "i", ["-", ["id", "i"], 1]]],
            ["id", "i"]
        ]"""
        JsonArrayEvaluator.evaluateJsonArray(e) shouldBe 0
    }

    test("def add(a, b) { return a + b; }, add(1, 2) ==> 3") {
        val program = """[
            ["def", "add", ["a", "b"], ["+", ["id", "a"], ["id", "b"]]],
            ["call", "add", 1, 2]
        ]"""
        JsonArrayEvaluator.evaluateJsonProgram(program) shouldBe 3
    }

    test("def factorial(n) { if(n == 0) { return 1; } else { return n * factorial(n - 1); } }, factorial(5) ==> 120") {
        val program = """[
            ["def", "factorial", ["n"],
                ["if", ["==", ["id", "n"], 0],
                    1,
                    ["*", ["id", "n"], ["call", "factorial", ["-", ["id", "n"], 1]]]
                ]
            ],
            ["call", "factorial", 5]
        ]"""
        JsonArrayEvaluator.evaluateJsonProgram(program) shouldBe 120
    }
})
