package com.davidconneely.triangle;

import java.io.IOException;

public final class MinTrianglePath {
  /**
   * Read a text-format triangle from standard input and output a minimal path to standard output in
   * a specific way.
   */
  public static void main(String[] args) {
    TriangleReader tr = new TriangleReader(System.in);
    int[] path;
    try {
      Triangle t = tr.readTriangle();
      path = TrianglePathFinder.findMinPath(t);
      int total = 0;
      StringBuilder sb = new StringBuilder("Minimal path is: ");
      for (int i = 0; i < path.length; ++i) {
        if (i > 0) {
          sb.append(" + ");
        }
        sb.append(path[i]);
        total += path[i];
      }
      sb.append(" = ");
      sb.append(total);
      System.out.println(sb.toString());
    } catch (TriangleShapeException tse) {
      System.err.println("There is a line that is too short or too long" + " in the input data:");
      System.err.println(tse.getMessage());
      System.exit(65);
    } catch (TriangleValueException tve) {
      System.err.println("There is an invalid value in the input data:");
      System.err.println(tve.getMessage());
      System.exit(65);
    } catch (ArithmeticException ae) {
      System.err.println(
          "The sum of numbers along part of some path"
              + " through\nthe triangle is > 2^31 or is < 1-2^31:");
      System.err.println(ae.getMessage());
      System.exit(70);
    } catch (IOException ioe) {
      System.err.println("There was an I/O error:");
      System.err.println(ioe.getMessage());
      System.exit(74);
    }
  }
}
