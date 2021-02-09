# 520-compiler
A "miniJava" compiler, as created for COMP 520.

Note that because of class requirements, my code is not as ideal as I want it to be. Please forgive me on this.

## Features supported
Currently can lex. Working on parsing.

## Usage
The compiler should be used through the command line, through arguments:
* The first argument is the file to be compiled.
* A second optional argument that can be supplied is `--debug`, which only exists for internal debugging purposes.

## Grammar
* Program ::= Class\* *end*
* Class ::= **class** Id **{** ClassItem\* **}**
* ClassItem ::= Modifiers (Type Field|(**void**|Type) Method)
* Modifiers ::= (**public**|**private**)? **static**?
* Type ::= (**int**|**boolean**|Id)(**[]**)?
* Field ::= Id**;**
* Method ::= Id**(**ParamList\* **){**Statement\* **}**
* ParamList ::= Type Id(, Type Id)*
* ArgList ::= Id(, Id)*
* Reference ::= (Id|**this**)(**.**Id)*
* Statement ::= **{** Statement\* **}**
			| **return** Expression**;**
			| **if(**Expression**)** Statement (**else** Statement)?
			| **while(**Expression**)** Statement
			| Type Id **=** Expression**;**
			| Reference(**[**Expression**]**)? **=** Expression**;**
			| Reference**(**ArgList?**);**
* Expression ::= (Reference(**[**Expression**]**|**(**ArgList?**)**)?
			| Unop Expression
			| **(**Expression**)**
			| Literal
			| **new** (Id**()**|Type**[**Expression**]**))
			(Biop Expression)?
* Id ::= \[\w**$_**][\w\d]+
* Unop ::= **!** | **-**
* Biop ::= **>** | **<** | **==** | **<=** | **>=** | **!=** | **&&** | **||** | **+** | **-** | \* | **/**
* Literal ::= \d(\d)+ | **true** | **false**

The grammar above uses EBNF with some POSIX conventions sprinkled in to make my life easier. Whitespace is generally insignificant but there must be spaces between words.

## License
As you can see in the [license file](LICENSE), this project is under the "Unlicense" which effectively releases it into the public domain. Basically, do what you want with my code. However, I don't recommend using the code for reference for COMP 520; Professor Prins explicitly doesn't allow this, and the particular subset to implement will change year-to-year. And of course, you will find it more rewarding to create something by yourself from scratch.

## Credits
* Eric Schneider
  
