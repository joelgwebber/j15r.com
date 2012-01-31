#!/usr/bin/python
import os
from os import path

shutterSpeeds = [ '1', '2.5', '6', '15', '40', '100', '250', '640', '1600', '4000', '6400' ]

def photoDirName(focalLength, iso, fStop):
  return '%smm_iso%s_f%s' % (focalLength, iso, fStop)

for focalLength in [35, 50]:
  for iso in [100, 200, 400, 800, 1600, 3200, 6400]:
    for fStop in [1.8, 2.8, 4.5, 7.1, 11, 18]:
      dirName = photoDirName(focalLength, iso, fStop)
      index = 0
      for filename in os.listdir(dirName):
        if filename.startswith('DSC_'):
          inname = path.join(dirName, filename)
          outname = path.join(dirName, '%s.jpg' % shutterSpeeds[index])
          os.rename(inname, outname)

          thumbname = path.join(dirName, '%s_thumb.jpg' % shutterSpeeds[index])
          os.spawnlp(os.P_WAIT, 'convert', 'convert', outname, '-resize', '320x240', thumbname)

          index = index + 1

