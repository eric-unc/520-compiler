  0         LOADL        0
  1         CALL         newarr  
  2         CALL         L11
  3         HALT   (0)   
  4  L10:   LOAD         0[OB]
  5         LOAD         -1[LB]
  6         CALL         add     
  7         STORE        0[OB]
  8         RETURN (0)   1
  9  L11:   LOADL        -1
 10         LOADL        1
 11         CALL         newobj  
 12         STORE        0[LB]
 13         LOADL        3
 14         LOAD         3[LB]
 15         CALLI        L10
 16         LOAD         3[LB]
 17         LOADL        0
 18         CALL         fieldref
 19         CALL         putint  
 20         RETURN (0)   1
