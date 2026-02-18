abstract class Ast(open val type: String)

class Program(val functions: List<Func>, val bodies: Expr) : Ast("Program")

class Func(val name: String, val paramNames: List<String>, val body: Expr) : Ast("Func")
