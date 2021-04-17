  0         LOADL        0
  1         CALL         newarr  
  2         CALL         L12
  3         HALT   (0)   
  4  L11:   LOAD         0[OB]
  5         LOAD         -1[LB]
  6         CALL         add     
  7         STORE        0[OB]
  8         RETURN (0)   1
  9  L12:   LOADL        -1
 10         LOADL        -1
 11         LOADL        2
 12         CALL         newobj  
 13         LOADL        3
 14         CALLI        L11
 15         CALLI        L10
 16         RETURN (0)   1
