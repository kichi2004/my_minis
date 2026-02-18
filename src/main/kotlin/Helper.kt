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

fun tLt(a: Expr, b: Expr) = BinExpr(
    op = BinExpr.Op.LESS_THAN,
    lhs = a,
    rhs = b
)

fun tGt(a: Expr, b: Expr) = BinExpr(
    op = BinExpr.Op.GREATER_THAN,
    lhs = a,
    rhs = b
)

fun tLe(a: Expr, b: Expr) = BinExpr(
    op = BinExpr.Op.LESS_THAN_OR_EQUAL,
    lhs = a,
    rhs = b
)

fun tGe(a: Expr, b: Expr) = BinExpr(
    op = BinExpr.Op.GREATER_THAN_OR_EQUAL,
    lhs = a,
    rhs = b
)

fun tEq(a: Expr, b: Expr) = BinExpr(
    op = BinExpr.Op.EQUAL,
    lhs = a,
    rhs = b
)

fun tNe(a: Expr, b: Expr) = BinExpr(
    op = BinExpr.Op.NOT_EQUAL,
    lhs = a,
    rhs = b
)

fun tAnd(a: Expr, b: Expr) = BinExpr(
    op = BinExpr.Op.AND,
    lhs = a,
    rhs = b
)

fun tOr(a: Expr, b: Expr) = BinExpr(
    op = BinExpr.Op.OR,
    lhs = a,
    rhs = b
)

fun tXor(a: Expr, b: Expr) = BinExpr(
    op = BinExpr.Op.XOR,
    lhs = a,
    rhs = b
)
