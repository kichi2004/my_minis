import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class ExprEvaluatorTest : FunSpec({
    val evaluator = ExprEvaluator()
    context("足し算") {
        test("1 + 1 == 2") {
            val e = tAdd(tInt(1), tInt(1))
            evaluator.evaluate(e) shouldBe 2
        }
    }
    context("引き算") {
        test("1 - 1 == 0") {
            val e = tSub(tInt(1), tInt(1))
            evaluator.evaluate(e) shouldBe 0
        }
        test("1 - 2 == -1") {
            val e = tSub(tInt(1), tInt(2))
            evaluator.evaluate(e) shouldBe -1
        }
    }
    context("掛け算") {
        test("1 * 1 == 1") {
            val e = tMul(tInt(1), tInt(1))
            evaluator.evaluate(e) shouldBe 1
        }
        test("1 * 0 == 0") {
            val e = tMul(tInt(1), tInt(0))
            evaluator.evaluate(e) shouldBe 0
        }
        test("2 * 2 == 4") {
            val e = tMul(tInt(2), tInt(2))
            evaluator.evaluate(e) shouldBe 4
        }
    }
    context("割り算") {
        test("1 / 1 == 1") {
            val e = tDiv(tInt(1), tInt(1))
            evaluator.evaluate(e) shouldBe 1
        }
        test("2 / 1 == 2") {
            val e = tDiv(tInt(2), tInt(1))
            evaluator.evaluate(e) shouldBe 2
        }
        test("6 / 2 == 3") {
            val e = tDiv(tInt(6), tInt(2))
            evaluator.evaluate(e) shouldBe 3
        }
    }
    context("複合式") {
        test("(1 + (2 * 3) - 1) / 2 == 3") {
            val e = tDiv(
                tSub(
                    tAdd(
                        tInt(1),
                        tMul(tInt(2), tInt(3))
                    ),
                    tInt(1)
                ),
                tInt(2)
            )
            evaluator.evaluate(e) shouldBe 3
        }
    }
    context("比較") {
        test ("1 < 2 == 1") {
            val e = tLt(tInt(1), tInt(2))
            evaluator.evaluate(e) shouldBe 1
        }
        test ("2 > 1 == 1") {
            val e = tGt(tInt(2), tInt(1))
            evaluator.evaluate(e) shouldBe 1
        }
        test ("1 <= 1 == 1") {
            val e = tLe(tInt(1), tInt(1))
            evaluator.evaluate(e) shouldBe 1
        }
        test ("1 >= 1 == 1") {
            val e = tGe(tInt(1), tInt(1))
            evaluator.evaluate(e) shouldBe 1
        }
        test ("1 == 1 == 1") {
            val e = tEq(tInt(1), tInt(1))
            evaluator.evaluate(e) shouldBe 1
        }
        test ("1 != 0 == 1") {
            val e = tNe(tInt(1), tInt(0))
            evaluator.evaluate(e) shouldBe 1
        }
    }
    context("論理演算") {
        test ("1 AND 1 == 1") {
            val e = tAnd(tInt(1), tInt(1))
            evaluator.evaluate(e) shouldBe 1
        }
        test ("1 AND 0 == 0") {
            val e = tAnd(tInt(1), tInt(0))
            evaluator.evaluate(e) shouldBe 0
        }
        test ("1 OR 0 == 1") {
            val e = tOr(tInt(1), tInt(0))
            evaluator.evaluate(e) shouldBe 1
        }
        test ("0 OR 0 == 0") {
            val e = tOr(tInt(0), tInt(0))
            evaluator.evaluate(e) shouldBe 0
        }
        test ("5 XOR 3 == 6") {
            val e = tXor(tInt(5), tInt(3))
            evaluator.evaluate(e) shouldBe 6
        }
        test ("12 XOR 10 == 6") {
            val e = tXor(tInt(12), tInt(10))
            evaluator.evaluate(e) shouldBe 6
        }
        test ("NOT 1 == 0") {
            val e = Not(tInt(1))
            evaluator.evaluate(e) shouldBe 0
        }
        test ("NOT 0 == 1") {
            val e = Not(tInt(0))
            evaluator.evaluate(e) shouldBe 1
        }
    }
    context("代入と識別子") {
        test("{ a = 100; a} == 100") {
            val e = Seq(
                Assignment("a", tInt(100)),
                Ident("a")
            )
            evaluator.evaluate(e) shouldBe 100
        }
        test("{a = 100; b = a + 1; b} == 101") {
            val e = Seq(
                Assignment("a", tInt(100)),
                Assignment("b", tAdd(Ident("a"), tInt(1))),
                Ident("b")
            )
            evaluator.evaluate(e) shouldBe 101
        }
    }
    context("条件分岐") {
        test("if (1 > 2) 2 else 1 == 1") {
            val e = If(tGt(tInt(1), tInt(2)), tInt(2), tInt(1))
            evaluator.evaluate(e) shouldBe 1
        }
        test("if (NOT 0) 5 else 3 == 5") {
            val e = If(Not(tInt(0)), tInt(5), tInt(3))
            evaluator.evaluate(e) shouldBe 5
        }
        test("if (1 AND 1) 10 else 20 == 10") {
            val e = If(tAnd(tInt(1), tInt(1)), tInt(10), tInt(20))
            evaluator.evaluate(e) shouldBe 10
        }
        test("if (0 OR 1) 7 else 8 == 7") {
            val e = If(tOr(tInt(0), tInt(1)), tInt(7), tInt(8))
            evaluator.evaluate(e) shouldBe 7
        }
    }
    context("While ループ") {
        test("i = 0; while (i < 10) { i = i + 1; } == 10") {
            val e = Seq(
                Assignment("i", tInt(0)),
                While(tLt(Ident("i"), tInt(10)), Seq(Assignment("i", tAdd(Ident("i"), tInt(1))))),
                Ident("i")
            )
            evaluator.evaluate(e) shouldBe 10
        }
        test("i = 0; while (i < 5 AND i >= 0) { i = i + 1; } == 5") {
            val e = Seq(
                Assignment("i", tInt(0)),
                While(tAnd(tLt(Ident("i"), tInt(5)), tGe(Ident("i"), tInt(0))), Seq(Assignment("i", tAdd(Ident("i"), tInt(1))))),
                Ident("i")
            )
            evaluator.evaluate(e) shouldBe 5
        }
        test("i = 10; while (NOT (i == 0)) { i = i - 1; } == 0") {
            val e = Seq(
                Assignment("i", tInt(10)),
                While(Not(tEq(Ident("i"), tInt(0))), Seq(Assignment("i", tSub(Ident("i"), tInt(1))))),
                Ident("i")
            )
            evaluator.evaluate(e) shouldBe 0
        }
    }
    context("関数呼び出し") {
        test("function add(a, b) { return a + b; }, add(1, 2) == 3") {
            val program = Program(
                listOf(
                    Func("add", listOf("a", "b"), tAdd(Ident("a"), Ident("b"))),
                ),
                Call("add", tInt(1), tInt(2))
            )
            evaluator.evaluateProgram(program) shouldBe 3
        }
    }
})

