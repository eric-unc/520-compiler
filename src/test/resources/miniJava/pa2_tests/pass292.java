// PA2 pass assorted VarDeclStmt, AssignStmt, IxAssignStmt, and CallStmt
class Foo {

    void stmts() {
	int [] v = 1;
	x = 2;
	x.y = 3;
	m(4);
	Foo bar = 5;
	Foo [] x = new Foo[6];
	x[i] = 7;
	x.m(y,8);
	x.m[i] = 9;
    }
}

	
	
