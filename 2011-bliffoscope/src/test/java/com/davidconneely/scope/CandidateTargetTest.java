package com.davidconneely.scope;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Comparator;
import org.junit.jupiter.api.Test;

public final class CandidateTargetTest {

  @Test
  public void testHashCode() {
    CandidateTarget ct1 = new CandidateTarget(0, 10, 10, 0.1f);
    CandidateTarget ct2 = new CandidateTarget(0, 10, 10, 0.2f);
    assertEquals(ct1.hashCode(), ct2.hashCode());
    CandidateTarget ct3 = new CandidateTarget(1, 10, 10, 0.3f);
    // following is beyond contract of hashCode.
    assertNotEquals(ct2.hashCode(), ct3.hashCode());
  }

  @Test
  public void testToString() {
    CandidateTarget ct1 = new CandidateTarget(0, 10, 10, 0.1f);
    CandidateTarget ct2 = new CandidateTarget(0, 10, 10, 0.1f);
    assertEquals(ct1.toString(), ct2.toString());
    CandidateTarget ct3 = new CandidateTarget(1, 20, 20, 0.2f);
    assertNotEquals(ct2.toString(), ct3.toString());
  }

  @Test
  public void testEqualsObject() {
    CandidateTarget ct1 = new CandidateTarget(0, 10, 10, 0.1f);
    CandidateTarget ct2 = new CandidateTarget(0, 10, 10, 0.1f);
    assertTrue(ct1.equals(ct2) && ct2.equals(ct1));
    CandidateTarget ct3 = new CandidateTarget(0, 10, 10, 0.2f);
    assertTrue(ct2.equals(ct3) && ct3.equals(ct2));
    CandidateTarget ct4 = new CandidateTarget(1, 20, 20, 0.2f);
    assertTrue(!ct3.equals(ct4) && !ct4.equals(ct3));
  }

  @Test
  public void testConfidenceOrder() {
    Comparator<CandidateTarget> confidenceOrder = CandidateTarget.confidenceOrder();
    CandidateTarget ct1 = new CandidateTarget(0, 10, 10, 0.1f);
    CandidateTarget ct2 = new CandidateTarget(0, 10, 10, 0.1f);
    // equal everything is zero.
    assertEquals(0, confidenceOrder.compare(ct1, ct2));
    assertEquals(0, confidenceOrder.compare(ct2, ct1));
    CandidateTarget ct3 = new CandidateTarget(0, 10, 10, 0.2f);
    // confidence sorted decreasing order.
    assertTrue(confidenceOrder.compare(ct2, ct3) > 0);
    assertTrue(confidenceOrder.compare(ct3, ct2) < 0);
    CandidateTarget ct4 = new CandidateTarget(1, 20, 20, 0.2f);
    // confidence sorted decreasing order first.
    assertTrue(confidenceOrder.compare(ct2, ct4) > 0);
    assertTrue(confidenceOrder.compare(ct4, ct2) < 0);
    // same confidence, so other things sorted increasing order.
    assertTrue(confidenceOrder.compare(ct3, ct4) < 0);
    assertTrue(confidenceOrder.compare(ct4, ct3) > 0);
  }
}
