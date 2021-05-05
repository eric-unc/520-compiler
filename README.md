# 520-compiler
A "miniJava" compiler, as created for COMP 520, aimed at the mJAM VM.

Note that partly owing to class requirements, time requirements, and mJAM restrictions, my code is not as perfect as I want it to be. Please forgive me.

## Features supported
The compiler parses for correct syntax, builds an AST, perform contextual analysis (identification and type checking), on code generation. miniJava supports variables, integer and boolean operations, basic control logic, arrays, and some level of OOP. Working on simple overloading.

### Extensions (PA5)
* Modulus (%) support (see `ModTest.java`).
* Static initialization block support (see `StaticInitBlockTest.java`).
* Constructor support, with support for parameters (see `ConstructorTest.java`). No support for overloading.
* System.exit support (see `SystemExitTest.java`). Doesn't do anything with the parameter, just halts.
* Basic for loop support, with optional components (see `ForTest.java`).
* Field initialization, _including non-static initialization_ (see `FieldInitializationTest.java`).

## Usage
The compiler should be used through the command line, through arguments:
* The first argument is the file to be compiled.
* A second optional argument that can be supplied is a "special mode" that can stop the compiler early, so no errors are generated. The following are supported:
    * `--ast-only`: scans, parses, builds an AST, and prints the AST.
    * `--contextual-analysis-only`, scans, parses, builds an AST, performs contextual analysis (just identification/type checking).
    * `--asm-too`, scans, parses, builds an AST, performs contextual analysis, code generation, and also outputs a dissambled `.asm` file.
    * `--run`, scans, parses, builds an AST, performs contextual analysis, code generation, outputs a dissambled `.asm` file, runs file that was generated.

## Grammar
* Program ::= Class\* *end*
* Class ::= **class** Id **{** ClassMember\* **}**
* ClassMember ::= FieldDeclaration (FieldTail|Method) | **static** StaticBlock | Constructor
* FieldDeclaration ::= (**public**|**private**)? **static**? (Type|**void**) Id
* FieldTail ::= (**=** Expression)?**;**
* Method ::= **(**ParamList\* **){**Statement\* **}**
* StaticBlock ::= **{** Statement\* **}**
* Constructor ::= (**public**|**private**)? Id **(**ParamList\* **){**Statement\* **}**
* Type ::= **boolean**|((**int**|Id)(**[]**)?)
* ParamList ::= Type Id(, Type Id)*
* ArgList ::= Expression(, Expression)*
* Reference ::= (Id|**this**)(**.**Id)*
* Statement ::= PureStatement**;**
			| **{** Statement\* **}**
			| **if(**Expression**)** Statement (**else** Statement)?
			| **while(**Expression**)** Statement
			| **for(**PureStatement?**;** Expression?**;** PureStatement?**)** Statement
* PureStatement ::= **return** (Expression)?**
			| Type Id **=** Expression
			| Reference(**[**Expression**]**)? **=** Expression
			| Reference**(**ArgList?**)**
* Expression ::= OrExpression
* OrExpression ::= AndExpression (**||** AndExpression)*
* AndExpression ::= EqualityExpression (**&&** EqualityExpression)*
* EqualityExpression ::= RelationalExpression ((**==**|**!=**) RelationalExpression)*
* RelationalExpression ::= AdditiveExpression ((**<=**|**<**|**>**|**>=**) AdditiveExpression)?
* AdditiveExpression ::= MultiplicativeExpression ((**+**|**-**) MultiplicativeExpression)*
* Multiplicative ::= UnaryExpression ((\*|**/**|**%**) UnaryExpression)*
* UnaryExpression ::= ((**-**|**!**) UnaryExpression) | PureExpression
* PureExpression ::= **(**Expression**)**
			| Literal
			| **new** (**int[**Expression**]**|Id(**(**ArgList?**)**|**[**Expression**]**))
			| Reference(**[**Expression**]**|**(**ArgList?**)**)?
* Id ::= \[\w**$_**][\w\d]+
* Literal ::= \d(\d)+ | **true** | **false** | **null**

The grammar above uses EBNF with some POSIX conventions sprinkled in to make my life easier. Whitespace is generally insignificant but there must be spaces between words and so on.

## License
As you can see in the [license file](LICENSE), this project is under the "Unlicense" which effectively releases it into the public domain. Basically, do what you want with my code. However, I don't recommend using the code for reference for COMP 520; Professor Prins explicitly doesn't allow this, and the particular subset to implement will change year-to-year. And of course, you will find it more rewarding to create something by yourself from scratch.

I have taken some test files from others, including [Ben Dod](https://github.com/benjdod/someminijavatests), Changon Kim, and Professor Jan Prins, which are unlicensed. I have noted as such where I have used them. The classes in the `AbstractSyntaxTrees` package were made by Professor Prins with some changes as describes below, and the classes in the `mJAM` package were made by him too (with unlisted modifications by me).

### Changes made to classes in the AbstractSyntaxTrees package
* Added support for null (adding `NullLiteral.java`, and updating `Visitor.java` and `ASTDisplay.java` to support it).
* Added `decl` field to `Identifier.java` and `Reference.java` for contextual analysis, along with an alternative constructor to initialize `Identifier.java`. Added `inClass` field to `MethodDecl.java` and `FieldDecl.java` for contextual analysis.
* Added alternative constructor to `FieldDeclList.java`, `MethodDeclList.java`, and `ParameterDeclList.java` to accept one declaration for convenience, which is to be appended to the list.
* Added abstract `equals` and `toPrettyString` methods in `TypeDenoter.java`, accompanied with implementation in `BaseType.java`, `ClassType.java`, and `ArrayType.java`. Added `equals` to `Identifier.java` and `Declaration.java`.
* Added `isInitialized` field to `VarDecl.java`, defaulted to `false`, which is used for contextual analysis (a [local] variable cannot be used if it has not been initialized).
* Added `main` field (of type `MethodDecl`) to `Package.java` to help with code generation.
* Added `RuntimeDescriptor.java`, with corresponding `MethodDescriptor.java`, `ClassDescriptor.java`, `VarDescriptor.java`. Added `runtimeDescriptor` field to `Declaration.java`.
* Added `StaticBlockDecl.java`, accompanied with a `staticBlockDecl` field in `ClassDecl.java`.
* Added `ConstructorDecl.java`, accompanied with a `constructorDecl` field in `ClassDecl.java`. Modified `NewObjectExpr.java` to include an `argList` field.
* Added `ForStmt.java`, accompanied with an update to `Visitor.java` (and `ASTDisplay.java`)
* Added `initExpression` field to `FieldDecl.java` and `toInitialize` field to `ClassDecl.java`.
* Syntactical/code style changes/cleanup.

## Testing
This project uses JUnit for testing. I have my own tests, as well as added from others. There are also "checkpoint" tests, which are given by Professor Prins, which use `Checkpoint1.java`, `Checkpoint2.java`, etc., as controllers, and the files in `pa1_tests`, `pa2_tests`, etc., as the sample test files.

## Credits
* Eric Schneider
* Jan Prins (`AbstractSyntaxTrees` package, `mJAM` package, checkpoint tests, `Test7.mjava`, `Test34.mjava`, `Test35.mjava`, `Test62.mjava`)
* Max Beckman-Harned (checkpoint tests)
* Ben Dod (`Test2.mjava`, `Test3.mjava`)
* Changon Kim (`Test9.mjava`)
  
