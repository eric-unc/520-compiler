// PA2 return stmt options
class Foo {

    void check() {
	if (true == false)
	    return;
	else
	    return new Foo();
    }
}
