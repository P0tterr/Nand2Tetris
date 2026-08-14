// Fill.asm

(LOOP)
    @KBD
    D=M
    @BLACK
    D;JNE

    @color
    M=0
    @DRAW
    0;JMP

(BLACK)
    @color
    M=-1

(DRAW)
    @SCREEN
    D=A
    @address
    M=D

(FILL_LOOP)
    @address
    D=M
    @KBD
    D=D-A
    @CHECK
    D;JGE

    @color
    D=M
    @address
    A=M
    M=D

    @address
    M=M+1
    @FILL_LOOP
    0;JMP

(CHECK)
    @LOOP
    0;JMP
