# 520-compiler
A "miniJava" compiler, as created for COMP 520.

Note that because of class requirements, my code is not as ideal as I want it to be. Please forgive me.

## Features supported
Parses for correct syntax and builds an AST. Working on contextual analysis.

## Usage
The compiler should be used through the command line, through arguments:
* The first argument is the file to be compiled.
* A second optional argument that can be supplied is `--debug`, which only exists for internal debugging purposes.

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
			| **return** Expression**;**
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

I have taken some test files from others, including [Ben Dod](https://github.com/benjdod/someminijavatests), Changon Kim, and Professor Jan Prins, which are unlicensed. I have noted as such where I have used them. The classes in the `AbstractSyntaxTrees` package were made by Professor Prins with some changes as describes below.

### Changes made to classes in the AbstractSyntaxTrees package
* Added support for null (adding `NullLiteral.java`, and updating `Visitor.java` and `ASTDisplay.java` to support it).
* Added `decl` field to `Identifier.java` for contextual analysis, along with alternative constructor to initialize it.
* Added alternative constructor to `FieldDeclList.java`, `MethodDeclList.java`, and `ParameterDeclList.java` to accept one declaration for convenience, which is to be appended to the list.
* Syntactical/code style changes/cleanup.

## Testing
This project uses JUnit for testing. I have my own tests, and some tests added from others. There are also "checkpoint" tests, which are given by Professor Prins, which you can see in `Checkpoint1.java` and similar files. These checkpoint unit tests however rely on samples that are (as of right now) _only_ saved locally to my computer, as they might reveal too much. Without the samples, the checkpoint unit tests will be disabled.

## Credits
* Eric Schneider
* Jan Prins (AST package, checkpoint tests, `Test7.mjava`)
* Ben Dod (`Test2.mjava`, `Test3.mjava`)
* Changon Kim (`Test9.mjava`)
  
