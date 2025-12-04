MOV AL, [papuvar_abc_Def_1]
ADD AL, 1
MOV [papuvar_abc_Def_1], AL
L1: MOV AL, [papuvar_abc_Def_1]
CMP AL, 20
JLE L6
JMP L3
L6: MOV AL, [papuvar_abc_Def_1]
MOV AH, [papuvar_hey#]
CMP AL, AH
JG L2
JMP L3
L2: MOV AL, 2
MOV AH, 10
MOV BL, [papuvar#X_]
MOV AL, AH
MUL BL
MOV AH, AL
MOV AX, AH
MOD BH
MOV AH, AL
ADD AL, AH
MOV [papuvar_abc_Def_1], AL
MOV AL, [papuvar_abc_Def_1]
ADD AL, 1
MOV [papuvar#X_], AL
JMP L1
L3: L3: NOP
