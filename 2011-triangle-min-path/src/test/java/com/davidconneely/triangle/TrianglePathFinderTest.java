package com.davidconneely.triangle;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public final class TrianglePathFinderTest {

  @Test
  public void testFindMinPath_ProvidedExample() {
    // example from the PDF file, with solution provided
    Triangle t = new Triangle();
    t.addRow(new int[] {7});
    t.addRow(new int[] {6, 3});
    t.addRow(new int[] {3, 8, 5});
    t.addRow(new int[] {11, 2, 10, 9});
    int[] path = TrianglePathFinder.findMinPath(t);
    assertArrayEquals(path, new int[] {7, 6, 3, 2});
  }

  @Test
  public void testFindMinPath_AnotherExample() {
    // just an obvious example to check it works
    Triangle t = new Triangle();
    t.addRow(new int[] {0});
    t.addRow(new int[] {0, 1});
    t.addRow(new int[] {2, 0, 3});
    t.addRow(new int[] {4, 5, 0, 6});
    t.addRow(new int[] {7, 8, 0, 9, 10});
    int[] path = TrianglePathFinder.findMinPath(t);
    assertArrayEquals(path, new int[] {0, 0, 0, 0, 0});
  }

  @Test
  public void testFindMinPath_NullTriangle() {
    try {
      TrianglePathFinder.findMinPath(null);
      fail("No NullPointerException thrown");
    } catch (NullPointerException npe) {
      /* expected */
    }
  }

  @Test
  public void testFindMinPath_EmptyTriangle() {
    Triangle t = new Triangle();
    int[] path = TrianglePathFinder.findMinPath(t);
    assertArrayEquals(path, new int[] {});
  }

  @Test
  public void testFindMinPath_SingleRowTriangle() {
    Triangle t = new Triangle();
    t.addRow(new int[] {10});
    int[] path = TrianglePathFinder.findMinPath(t);
    assertArrayEquals(path, new int[] {10});
  }

  @Test
  public void testFindMinPath_NonuniqueMinPaths() {
    Triangle t = new Triangle();
    // could be [ 1, 2, 3 ] or [ 1, 3, 2 ]
    // however, we defined that the path should go left at each decision
    // point where there is an equally-minimal path available left or right
    t.addRow(new int[] {1});
    t.addRow(new int[] {2, 3});
    t.addRow(new int[] {3, 4, 2});
    int[] path = TrianglePathFinder.findMinPath(t);
    assertArrayEquals(path, new int[] {1, 2, 3});
  }

  @Test
  public void testFindMinPath_IntOverflow() {
    Triangle tOK = new Triangle();
    tOK.addRow(new int[] {10});
    tOK.addRow(new int[] {Integer.MAX_VALUE - 10, Integer.MAX_VALUE - 10});
    TrianglePathFinder.findMinPath(tOK);
    try {
      Triangle tOF = new Triangle();
      tOF.addRow(new int[] {11});
      tOF.addRow(new int[] {Integer.MAX_VALUE - 10, Integer.MAX_VALUE - 10});
      TrianglePathFinder.findMinPath(tOF);
      fail("No exception on overflow");
    } catch (ArithmeticException ae) {
      /* expected */
    }
  }

  @Test
  public void testFindMinPath_IntUnderflow() {
    Triangle tOK = new Triangle();
    tOK.addRow(new int[] {-10});
    tOK.addRow(new int[] {Integer.MIN_VALUE + 10, Integer.MIN_VALUE + 10});
    TrianglePathFinder.findMinPath(tOK);
    try {
      Triangle tUF = new Triangle();
      tUF.addRow(new int[] {-11});
      tUF.addRow(new int[] {Integer.MIN_VALUE + 10, Integer.MIN_VALUE + 10});
      TrianglePathFinder.findMinPath(tUF);
      fail("No exception on underflow");
    } catch (ArithmeticException ae) {
      /* expected */
    }
  }

  @Test
  public void testFindMinPath_Timings() {
    // try it 20 times and check it always takes < 500ms
    for (int ntry = 0; ntry < 20; ++ntry) {
      Triangle t = TriangleTestUtils.makeRandomTriangle(500, 0, 99);
      long t0 = System.currentTimeMillis();
      TrianglePathFinder.findMinPath(t);
      long t1 = System.currentTimeMillis();
      assertTrue(t1 - t0 < 500L);
    }
  }

  @Test
  public void testFindMinPath_CompareAltImpl() {
    // try the dumb 'enumerate all paths' algorithm on numerous small
    // triangles to check the two algorithms find the same paths
    for (int ntry = 0; ntry < 22; ++ntry) {
      Triangle t = TriangleTestUtils.makeRandomTriangle(ntry + 1, 0, 99);
      int[] path1 = TrianglePathFinder.findMinPath(t);
      int[] path2 = TriangleTestUtils.findMinPathAltImpl(t);
      assertArrayEquals(path1, path2);
    }
  }
}
