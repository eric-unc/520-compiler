// PA2 pass references in expresssions 
class Refs {

    void p() {
    	x1 = a;
    	x2 = a.b;
        x3 = this;
        x4 = a[n];
        x5 = f(n);
        x6 = 6;
        x7 = false;
        x8 = new Foo();
        x9 = new Foo[n];
        x10 = new int [n];
    }
}
