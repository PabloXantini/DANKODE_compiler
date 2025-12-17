# DANKODE: Programming Language

## Meeting DANKODE

DANKODE is an imperative programming language, powered entirely by Java:

- Supports assignments with complex expressions.
- **while** is the unique special sentence implemented, however other keyword as **if/else**, **do/while**, **for** are available for its future implementation.

## DANKODE Syntax

### Variables
Variables always start with **papuvar** separated with `_` or `#`, also it can end with digit substring from 0 to 9 between this symbols you can put your custom names for them. Next, the regular expression for variable definition is shown.

```regex
	^papuvar[_#]([A-Za-z]+[-#])+[0-9]*
```

### Sentences

1. Most simple sentences must end with `;`.

2. For **assignments** you must use the symbol `=` for load a value above a variable. Thus you can use complex expression for represent a value that be computed in compilation time.

3. The summarized rules for syntax is described to continuation:
- **Declaration**: 
		(Declaration) => (DataType)(AssignmentList)`;`
		(AssignmentList) => (Variable) | (Variable)`=`(Expression)(NextAssign)
		(NextAssign) => `,`(AssignmentList) | ()
- **Assignments**: (Variable)`=`(Expression)`;`
- **While**: `while``(`(Expression)`)``{`(Body)`}`

### Operators and Expressions
The operators supported by the language are:
* **Addition**: `+`
* **Subtraction**: `-`
* **Multiplication**: `*`
* **Residue**: `%`
* **Division**: `/`

Also, expressions or subexpressions can be contained in parenthesis `()`.


## DANKODE Compiler

DANKODE is supported by the compiler implemented in this repository, made on JAVA, this one is able of perform lexical, syntactical and semantical analysis with error response. The compiler forms an Abstract Syntax Tree (AST) that it conversed into Intermediate Representation (IR) and can be optimized in this case applying the rule of Data Store Elimination.

### Compiler Estructure
Inside this repository you can find the compiler implementation in [dankcompiler](https://PabloXantini/DANKODE_compiler/tree/main/src/dankcompiler) folder.