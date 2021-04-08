/*** line 10: reference "x" is not an array
 * COMP 520
 * Identification or Type checking
 */
class Fail322 {

    public int x;

    void f() {
	x = x[3];
    }
}
