MOV AL, [papuvar_a_]
MOV AH, [papuvar_k_]
ADD AL, AH
MOV [papuvar_h_], AL
L1:MOV AL, [papuvar_m_]
JLCMP AL, 7
JL L2
JMP L3
L2:MOV AL, [papuvar_b_]
MOV AH, [papuvar_a_]
SUB AL, AH
MOV [papuvar_j_], AL
MOV AL, [papuvar_m_]
MOV AH, [papuvar_h_]

MOV AH, [papuvar_j_]
ADD AL, AH
MOV [papuvar_m_], AL
JMP L1
L3:NOP
