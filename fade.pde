/* Color Shifting Algorithm
By Jeremy Kahn

This function will find a color that exists in between two given colors, based
upon a percentage.*/

/*
from:         Initial color
to:           Shifted-to color (if fadePercent is 100)
fadePercent:  How much to fade.  0 is none, 100 is totally shifted.*/
color fade(color from, color to, float fadePercent)
{
  float fromR = red(from),
  fromG = green(from),
  fromB = blue(from),
  fromA = alpha(from),
  toR = red(to),
  toG = green(to),
  toB = blue(to),
  toA = alpha(to),
  differenceR = map(fadePercent, 0, 100, fromR, toR), 
  differenceG = map(fadePercent, 0, 100, fromG, toG), 
  differenceB = map(fadePercent, 0, 100, fromB, toB),
  differenceA = map(fadePercent, 0, 100, fromA, toA);

  /* These prevent NaNs and Infinite values.*/
  if ((fromR == 0) && (toR == 0))
    differenceR = 0;

  if ((fromG == 0) && (toG == 0))
    differenceG = 0;

  if ((fromB == 0) && (toB == 0))
    differenceB = 0;

  if ((fromA == 0) && (toA == 0))
    differenceA = 0;

  if ((fromR == 255) && (toR == 255))
    differenceR = 255;

  if ((fromG == 255) && (toG == 255))
    differenceG = 255;

  if ((fromB == 255) && (toB == 255))
    differenceB = 255;

  if ((fromA == 255) && (toA == 255))
    differenceA = 255;

  return color(differenceR, differenceG, differenceB, differenceA);
}





