  0         LOADL        0
  1         CALL         newarr  
  2         CALL         L12
  3         HALT   (0)   
  4  L11:   LOAD         0[OB]
  5         LOAD         0[LB]
  6         CALL         add     
  7         STORE        0[OB]
  8         RETURN (0)   1
  9  L12:   LOADL        3
 10         CALLI        L11
 11         CALLI        L10
 12         RETURN (0)   1
