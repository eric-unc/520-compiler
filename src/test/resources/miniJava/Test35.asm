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
 12         LOADL        3
 13         LOAD         3[LB]
 14         CALLI        L10
 15         LOAD         3[LB]
 16         LOADL        0
 17         CALL         fieldref
 18         CALL         putintnl
 19         RETURN (0)   1
