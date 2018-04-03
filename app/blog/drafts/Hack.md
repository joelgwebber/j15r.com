http://blog.moertel.com/posts/2013-12-14-great-old-timey-game-programming-hack.html

Ah, memories. 6502s, 6809s, and the like are a bit before my time
professionally, though I did muck with the former a bit as a kid (I bet +Ray
Cromwell knew the 6502 really well).

I'll contribute a capsule version of my own small bit-banging hack on the x86
(486, Pentium [Pro] era):

When working on this game (http://www.gamespot.com/wizards-and-
warriors/images/) in the mid 90s, we wrote what had to be one of the last big
software-rendered 3d engines (development of the game straddled the period when
good 3d cards went from non-existent, to available, to common). It was fairly
aggressive for the time -- 16-bit color depth, 640x480, perspective-correct
texture-mapped surfaces, transparency and several blending modes for effects.

Unsurprisingly, a huge part of the time rendering a frame was spent in the
inner texture loop. The outer loops of the rasterizer would set up edge lists
with left-right edges and their associated (u, v, 1/z) values, then the inner
loop would need to tear between the edges as quickly as possible. If you did
this the "obvious" way, it would be slow as hell:

Per-pixel:
- Linearly interpolate (u/z, v/z, 1/z)
- 2 divides ((u/z) / (1/z), (v/z) / (1/z))
- Texture lookup (floor, multiply and clamp)
- Color lookup (textures were 8-bit with an associated 8->16 LUT)
- Blend (read framebuffer, compute color*(alpha) + fb*(1-alpha), among other modes)

There are a few basic things you can do to speed this up. The divides are *really*
expensive, so we cheated by computing "real" U and V values only once every 8-16
pixels, linearly interpolating the rest. The much more clever part of the texture
loop was to shove *both* U and V values into a single 32-bit register, as two
12.4 fixed-point values (IIRC; it may have been 8.8). Do the same with the dU, dV
interpolants

[h/t +Dominic Mitchell]
