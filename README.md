# 520-compiler
A "miniJava" compiler, as created for COMP 520.

Note that because of class requirements, my code is not as ideal as I want it to be. Please forgive me.

## Features supported
Parses for correct syntax, builds an AST, perform contextual analysis (identification and type checking). Working on code generation.

## Usage
The compiler should be used through the command line, through arguments:
* The first argument is the file to be compiled.
* A second optional argument that can be supplied is a "special mode" that can stop the compiler early, so no errors are generated. The following are supported:
** `--ast-only`: scans, parses, builds an AST, and prints the AST.
** `--contextual-analysis-only`, scans, parses, builds an AST, performs contextual analysis (just identification/type checking).
** `--asm-too`, scans, parses, builds an AST, performs contextual analysis, code generation, and also outputs a dissambled `.asm` file.
** `--jit`, scans, parses, builds an AST, performs contextual analysis, code generation, outputs a dissambled `.asm` file, runs file that was generated (not a true JIT compiler since it's saved to the disk).

## Grammar
* Program ::= Class\* *end*
* Class ::= **class** Id **{** ClassMember\* **}**
* ClassMember ::= FieldDeclaration (FieldTail|Method)
* FieldDeclaration ::= (**public**|**private**)? **static**? (Type|**void**) Id
* FieldTail ::= **;**
* Method ::= **(**ParamList\* **){**Statement\* **}**
* Type ::= **boolean**|((**int**|Id)(**[]**)?)
* ParamList ::= Type Id(, Type Id)*
* ArgList ::= Expression(, Expression)*
* Reference ::= (Id|**this**)(**.**Id)*
* Statement ::= **{** Statement\* **}**
			| **return** (Expression)?**;**
			| **if(**Expression**)** Statement (**else** Statement)?
			| **while(**Expression**)** Statement
			| Type Id **=** Expression**;**
			| Reference(**[**Expression**]**)? **=** Expression**;**
			| Reference**(**ArgList?**);**
* Expression ::= OrExpression
* OrExpression ::= AndExpression (**||** AndExpression)*
* AndExpression ::= EqualityExpression (**&&** EqualityExpression)*
* EqualityExpression ::= RelationalExpression ((**==**|**!=**) RelationalExpression)*
* RelationalExpression ::= AdditiveExpression ((**<=**|**<**|**>**|**>=**) AdditiveExpression)?
* AdditiveExpression ::= MultiplicativeExpression ((**+**|**-**) MultiplicativeExpression)*
* Multiplicative ::= UnaryExpression ((\*|**/**) UnaryExpression)*
* UnaryExpression ::= ((**-**|**!**) UnaryExpression) | PureExpression
* PureExpression ::= **(**Expression**)**
			| Literal
			| **new** (**int[**Expression**]**|Id(**()**|**[**Expression**]**))
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
* Syntactical/code style changes/cleanup.

## Testing
This project uses JUnit for testing. I have my own tests, as well as added from others. There are also "checkpoint" tests, which are given by Professor Prins, which use `Checkpoint1.java`, `Checkpoint2.java`, etc., as controllers, and the files in `pa1_tests`, `pa2_tests`, etc., as the sample test files.

## Credits
* Eric Schneider
* Jan Prins (`AbstractSyntaxTrees` package, `mJAM` package, checkpoint tests, `Test7.mjava`, `Test34.mjava`, `Test35.mjava`)
* Max Beckman-Harned (checkpoint tests)
* Ben Dod (`Test2.mjava`, `Test3.mjava`)
* Changon Kim (`Test9.mjava`)
  
