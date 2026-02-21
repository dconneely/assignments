# `task-scope`

Data analysis problem - finding bitmap targets in a bitmap field.

Note: the code for this task was written in 2011, before Java 8 was released, so there is some code
that could be refactored to better use newer Java language features such as `try`-with-resources,
`record`s, pattern-matching, `List.of()`, etc. I have only updated the build script to Gradle and
applied a standard formatting to the Java code.

The original problem was provided as a PDF file and some text files. It was still being used for
interviews until as late as 2016 and some GitHub repositories with solutions from as recently as
2018 can be found.

## Problem notes

### Overview

The brief is to spend a few hours on this problem, rather than days or weeks, so pattern-recognition
algorithms like _SIFT_, and so on, are not going to be appropriate (since unfortunately I know
nothing about them).

I have therefore assumed that the scope images of the torpedo and starship shapes will not be
rotated, scaled or deformed in any way (perhaps the anti-neutrino detector can only detect neutrinos
that hit it exactly perpendicularly over a surface and the starships and torpedoes have to stay
precisely-oriented in order to remain cloaked ;)), and that the shapes will only be subject to the
noise described in the task instructions PDF file.

The data consisting of '`+`' and '` `' characters can be considered to be a monochrome bitmap image,
and we want to do something that is a little like image convolution, using the starship (or torpedo)
shapes as the kernels, to find the maximal values in the resultant matrix. There may be a clever
shortcut way to do this using the Java 2D API or some other image-processing code, but I have not
tried that.

Instead, I store the image pixels as an array of arrays of `boolean` values.

The same class (`Bitmap`) is used to represent both the ideal target images and the scope image. As
well as the image pixels, this class stores the target's midpoint coordinates (this is redundant
information for the scope image), and allows the target image to be "trimmed" of borders of "off"
pixels (since these only increase the iterations needed and are not helpful in locating targets;
although again it is not used for the scope image only for the target images).

The algorithm is simple and can be located in the `Analyzer` class - it does a bitwise "AND" of the
scope image and target at every possible location of the target in the scope image, and for each
location keeps track of the number of "on" pixels in the result of the "AND" in another array (as a
fraction of the maximal match value - called "confidence" in the code).

One minor complication is that I've assumed the most useful target location would be the midpoint of
the target, rather than its top-left corner, so there are some adjustments made before the
coordinates are returned.

I've assumed that scope images do not wrap around in any way, and have also made the assumption that
any target will be located entirely within the scope image (so no partial targets).

### Issues found #1

Consider the `$` symbols below. If these 11 pixels switched on, then the starship could be seen as
having moved to the right a pixel. Also, if we consider that the spaceship has 54 pixels in total,
then the point to the right is a candidate target with confidence (54-11) / 54 = 79.6% (and the same
for the point one pixel to the left). If one or more of the `$` pixels happens to get flipped "on"
by noise, then the confidence figure could be higher.

```
  ++++++++++$
 ++$       ++$
  ++++++++++$
      ++$
      ++$
      ++$
  ++++++++++$
 ++$       ++$
  ++++++++++$
```

Any connected shape will have this problem to some degree (although it is more acute for the
starship shape), and the problem description doesn't tell us that the targets cannot overlap (maybe
if one is behind the other, for example) so this could actually be correct. I have not added code to
ignore such "shadow targets" as I'd want to ensure I had sound mathematics behind it (it may be that
there isn't any justification for removing them, and we just have to list them). The sorting by
confidence should cause such targets to be listed later, but be aware that there will be false
positives like this in the candidate targets.

### Issues found #2

Testing on a 12,500 x 12,500 pixel scope image caused the default Java heap to be exhausted,
although increasing the JVM's maximum heap size (for example, '`-Xmx2048M`') fixed this for the test
in question, a large enough grid will exhaust any heap size.

Memory usage for the bitmap data could be reduced maybe 32-fold by using a more efficient
representation - each array of boolean values could be replaced by a `java.util.BitSet` instance,
for example. The arrays of `float` values for the confidences could be reduced maybe 2- or 4-fold by
using arrays of `byte` or `short` values instead.

Splitting large grids into overlapping subgrids that are processed serially could also be used to
limit the memory usage.

## Further developments

The algorithm used to create the array for the confidence values could be split to run over multiple
parallel threads by dividing the scope grid into sub-grids (they would be overlapping sub-grids to
allow for targets located on the edge between two sub-grids). Once this was complete, locating the
candidate targets could also be run over sub-grids (generating sublists of candidates). On some
hardware, with very large grids, it is possible that this might be worth doing.

If there was no noise in the bitmap, and the ship/torpedo shapes were guaranteed never to be
overlapping, then there are probably better ways of searching for the target pattern (perhaps by
using hash values created from the target).

If performance was an issue, then using something like a `BitSet` (as mentioned in the discussion
about memory use above) might allow the bitwise "AND" to be done more quickly. Also, several target
locations could be checked at once by doing the "AND" of each row of the scope image with a row of
the target image concatenated to form a row with the same width as the scope image.

Currently, the two targets are hardcoded (although it would be trivial to change them as they are
driven from a Bitmap instance initialized from a resource). Maybe by taking this further and writing
code that explicitly checked each shape would help (particularly for "sparse" target images)? I
don't think this would be worth doing unless there is a real problem, and we've checked that this
would solve it.

If other kinds of targets are needed in the future, then performance will be affected adversely if
that target image is larger than the current targets (basically the algorithm is O(_AB_), where _A_
is the area of the scope image,  and _B_ is the total area of the target image bitmaps).
