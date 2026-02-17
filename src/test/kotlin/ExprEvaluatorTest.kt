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
})

fun tAdd(a: Expr, b: Expr) = BinExpr(
    op = BinExpr.Op.PLUS,
    lhs = a,
    rhs = b
)

fun tSub(a: Expr, b: Expr) = BinExpr(
    op = BinExpr.Op.MINUS,
    lhs = a,
    rhs = b
)

fun tMul(a: Expr, b: Expr) = BinExpr(
    op = BinExpr.Op.MULTIPLY,
    lhs = a,
    rhs = b
)

fun tDiv(a: Expr, b: Expr) = BinExpr(
    op = BinExpr.Op.DIVIDE,
    lhs = a,
    rhs = b
)

fun tInt(value: Long) = MyInt(value)
