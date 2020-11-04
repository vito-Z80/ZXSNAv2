    device zxspectrum48
    org #6000
start:
    ld a,100
    ld b,%01010101
    call start
    jr $