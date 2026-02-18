ソフトウェアサイエンス特別講義Aの課題 "minis" を Kotlin で作成しました。

### 作ったもの
- 各種 `Ast`, `Expr` とその評価器
- `JsonArrayEvaluator` による配列ベース構文の実現
- `JsonObjectEvaluator` によるオブジェクトベース構文の実現
- 新機能：整数の `xor`, 真理値の `and`, `or`, `not` のサポート

**`src/main/kotlin/`**  
- `Ast.kt`: 抽象構文木の抽象クラス、Program、関数
- `Expr.kt`: 抽象構文木のうち式に関するクラス
- `ExprEvaluator.kt`: 式の評価を行うクラス
- `JsonArrayEvaluator`: 配列ベース構文のパースを行うクラス
- `JsonObjectEvaluator`: オブジェクトベース構文のパースを行うクラス

**`src/test/kotlin/**`**  
各種テストを配置

### 変更点
- 上記、新機能の実装
- 複数の `Expr` を取ることになっている一部の式 (`While` など) においては、
    単一の式を取ることにし、必要に応じて `Seq` を用いるように変更。

### テストの実行方法
`./gradlew test`
