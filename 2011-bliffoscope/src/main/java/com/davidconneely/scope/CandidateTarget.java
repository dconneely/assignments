package com.davidconneely.scope;

import java.text.MessageFormat;
import java.util.Comparator;

/**
 * Represent the type and location of a candidate target in the scope data, also contains an
 * indication of our confidence in the target.
 */
public final class CandidateTarget {
  private int type;
  private int midX;
  private int midY;
  private float confidence;

  /**
   * Create a candidate target initialized with the given parameters.
   *
   * @param type <code>0</code> for a slime torpedo; <code>1</code> for a Rejecto starship.
   * @param midX zero-based column of the midpoint of the object.
   * @param midY zero-based row of the midpoint of the object.
   * @param confidence the fraction of matching pixels between the scope and the object's ideal
   *     shape (between <code>0.0f</code> - if none of the "on" pixels matched - and <code>1.0f
   *     </code> - if all of the "on" pixels matched).
   */
  CandidateTarget(int type, int midX, int midY, float confidence) {
    this.type = type;
    this.midX = midX;
    this.midY = midY;
    this.confidence = confidence;
  }

  int type() {
    return type;
  }

  int midX() {
    return midX;
  }

  int midY() {
    return midY;
  }

  float confidence() {
    return confidence;
  }

  @Override
  public String toString() {
    return MessageFormat.format(
        "Possible"
            + " {0,choice,0#slime torpedo|1#Rejecto starship}"
            + " at location ({1,number,integer}; {2,number,integer})"
            + " with confidence {3,number,0.0%}.",
        type, midX, midY, confidence);
  }

  /**
   * Two candidate targets are equal if they are of the same type and location (confidence is not
   * considered).
   */
  @Override
  public boolean equals(Object o) {
    // Implementation is a straightforward element-wise equality check,
    // but skipping the confidence.
    if (o instanceof CandidateTarget) {
      CandidateTarget c = (CandidateTarget) o;
      return (type == c.type && midX == c.midX && midY == c.midY);
    } else {
      return false;
    }
  }

  /**
   * Hash value generated from the type and location (but is unaffected by confidence). This is to
   * make it consistent with the definition of <code>equals</code>.
   */
  @Override
  public int hashCode() {
    // Implementation is a straightforward element-wise hash,
    // but skipping the confidence.
    return 17 * (17 * type + midX) + midY;
  }

  /**
   * Return a comparator that orders by descending confidence first, then by increasing midX, midY
   * and type.
   */
  static Comparator<CandidateTarget> confidenceOrder() {
    return ConfidenceOrder.instance();
  }

  /**
   * Comparator class returned by the <code>confidenceOrder</code> method. This class does not need
   * to be visible outside of the parent class, since it is just the implementation class returned
   * by the <code>CandidateTarget.confidenceOrder()</code> method.
   */
  private static final class ConfidenceOrder implements Comparator<CandidateTarget> {
    private static final ConfidenceOrder instance = new ConfidenceOrder();

    static ConfidenceOrder instance() {
      return instance;
    }

    @Override
    public int compare(CandidateTarget a, CandidateTarget b) {
      // necessary to handle special cases like 'NaN'.
      int comparison = Float.compare(b.confidence(), a.confidence());
      if (comparison == 0) {
        // not strictly necessary to use <code>Integer.signum</code>,
        // but "clean up" the return value (so that no-one can make the
        // mistake of relying on the comparison return value being
        // calculated in a particular way).
        comparison = Integer.signum(a.midX() - b.midX());
        if (comparison == 0) {
          comparison = Integer.signum(a.midY() - b.midY());
          if (comparison == 0) {
            comparison = Integer.signum(a.type() - b.type());
          }
        }
      }
      return comparison;
    }
  }
}
