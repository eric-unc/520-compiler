/*** line 10: variable "x "is already defined in method "foo"
 * COMP 520
 * Identification
 */
class fail304 { 	
    
    public void foo(int parm) {
        int x = 0;
        {
            int x = 4;
        }
    }

}
