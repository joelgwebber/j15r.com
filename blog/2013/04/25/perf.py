import math

FWIDTH = 1000
FHEIGHT = 160
HWIDTH = FWIDTH/2
HHEIGHT = FHEIGHT/2
BORDER_SIZE = 16
BOXHEIGHT = 24
LINEHEIGHT = 32

DATA = [
    #["C", 2.62, 0.24, 0],
    #["NaCl", 3.39, 0.25, 1],
    #["asm.js", 5.12, 0.59, 0],
    #["Java 1.7", 6.02, 0.67, 1],
    #["AS3", 10.4, 0.91, 0],
    #["Box2dWeb (Safari)", 20.4, 2.01,  0],
    #["Emscripten (Chrome)", 23.6, 4.87, 1],
    #["Box2dWeb (Chrome)", 29.1, 6.91,  -1],
    #["Emscripten (Firefox)", 31.6, 2.48, 0],
    #["Box2dWeb (Firefox)", 36.2, 4.53, 1],
    #["Dart", 40.7, 9.21, -2]

    ["C", 3.83, 0.62, 0],
    ["Mandreel (Chrome)", 48.33, 4.82, 0],
    ["Box2dWeb (Safari)", 68.85, 5.20, 0],
    ["Box2dWeb (Chrome)", 74.30, 13.4, 1],
    ["Box2dWeb (Firefox)", 110.9, 7.26, 0]
]

BASELINE = DATA[0][1]
RANGE = 35.0
STEP = 5

def computeRange():
    max = 0
    for i in range(len(DATA)):
        val = DATA[i][1] + DATA[i][2]
        if val > max:
            max = val
    return max

def scale(x):
    return x / RANGE * FWIDTH

def interval(name, mean, stddev, textLine):
    nostroke()
    fill(0, 0, 1, 0.5)
    strokewidth(2)

    x = scale(mean / BASELINE)
    y = scale(stddev / BASELINE)
    rect(x - y, HHEIGHT - BOXHEIGHT/2 + LINEHEIGHT*textLine, y*2, BOXHEIGHT)

    font("Helvetica Bold", 14)
    align(CENTER)
    w = textwidth(name)

    fill(0, 0, 1, 1)
    text(name, x - w/2, HHEIGHT + LINEHEIGHT*textLine + 4)

def renderIntervals():
    for i in range(len(DATA)):
        item = DATA[i]
        interval(item[0], item[1], item[2], item[3])

def renderTicks():
    stroke(0, 0, 0, 0.1)
    fill(0, 0, 0, 0.5)
    font("Helvetica", 12)
    align(CENTER)

    for x in range(0, int(RANGE), STEP):
        line(scale(x), 0, scale(x), FHEIGHT - 16)
        w = textwidth(str(x))
        text(str(x), scale(x) - w/2 + 1, FHEIGHT)

size(FWIDTH + BORDER_SIZE*2, FHEIGHT + BORDER_SIZE*2)
translate(BORDER_SIZE, BORDER_SIZE)
#RANGE = computeRange()
renderTicks()
renderIntervals()
