package com.davidconneely.scope;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.List;
import java.util.Random;
import org.junit.jupiter.api.Test;

public final class AnalyzerTest {
  @Test
  public void testFindCandidateTargets_Empty() throws IOException {
    // fixed seed
    Random rand = new Random(145);
    // generate an empty noiseless scope.
    Bitmap scope = TestDataGenerator.generateScope(0, 0, rand, 0, 0, 0.0f, 0.0f);
    List<CandidateTarget> candidates = Analyzer.findCandidateTargets(scope, Float.MIN_VALUE, 1);
    assertEquals(0, candidates.size());
  }

  @Test
  public void testFindCandidateTargets_1x1() throws IOException {
    // fixed seed
    Random rand = new Random(145);
    // generate an empty noiseless scope.
    Bitmap scope = TestDataGenerator.generateScope(1, 1, rand, 0, 0, 0.0f, 0.0f);
    List<CandidateTarget> candidates1 = Analyzer.findCandidateTargets(scope, Float.MIN_VALUE, 1);
    assertEquals(0, candidates1.size());
    // fixed seed
    rand = new Random(145);
    // generate a scope with all bits set.
    scope = TestDataGenerator.generateScope(1, 1, rand, 0, 0, 1.0f, 1.0f);
    List<CandidateTarget> candidates2 = Analyzer.findCandidateTargets(scope, Float.MIN_VALUE, 1);
    // we require the target to be entirely within the scope
    assertEquals(0, candidates2.size());
  }

  @Test
  public void testFindCandidateTargets_Small() throws IOException {
    // fixed seed
    Random rand = new Random(145);
    // generate a noiseless scope with one ship on it,
    // make the scope small enough so it can only go it one place.
    Bitmap starship = BitmapLoader.fromResource("Starship.blf").trim();
    Bitmap scope =
        TestDataGenerator.generateScope(
            starship.width(), starship.height(), rand, 1, 0, 0.0f, 0.0f);
    List<CandidateTarget> candidates1 = Analyzer.findCandidateTargets(scope, 1.0f, 2);
    assertEquals(1, candidates1.size());
    assertEquals(1, candidates1.get(0).type());
    assertEquals(candidates1.get(0).midX(), starship.midX());
    assertEquals(candidates1.get(0).midY(), starship.midY());
    assertEquals(1.0f, candidates1.get(0).confidence());
    // fixed seed
    rand = new Random(145);
    // generate a noisy scope with one ship on it,
    // make the scope small enough so it can only go it one place.
    scope =
        TestDataGenerator.generateScope(
            starship.width(), starship.height(), rand, 1, 0, 0.2f, 0.2f);
    List<CandidateTarget> candidates2 = Analyzer.findCandidateTargets(scope, Float.MIN_VALUE, 1);
    assertEquals(1, candidates2.size());
    assertEquals(candidates2.get(0), candidates1.get(0));
  }

  @Test
  public void testFindCandidateTargets_SmallP1() throws IOException {
    // fixed seed
    Random rand = new Random(145);
    // generate a scope with all bits set,
    // make scope small enough so targets can only go in a few places.
    Bitmap starship = BitmapLoader.fromResource("Starship.blf").trim();
    Bitmap torpedo = BitmapLoader.fromResource("SlimeTorpedo.blf").trim();
    // make the width and height be just one more than they need to be to
    // fit both targets.
    int width = Math.max(starship.width(), torpedo.width()) + 1;
    int height = Math.max(starship.height(), torpedo.height()) + 1;
    // this is how many targets could possibly be found if one was found
    // at every location in the scope (which it will be).
    int nLocs =
        (1 + width - starship.width()) * (1 + height - starship.height())
            + (1 + width - torpedo.width()) * (1 + height - torpedo.height());
    Bitmap scope = TestDataGenerator.generateScope(width, height, rand, 0, 0, 1.0f, 1.0f);
    List<CandidateTarget> candidates = Analyzer.findCandidateTargets(scope, 1.0f, nLocs + 1);
    assertEquals(candidates.size(), nLocs);
  }

  @Test
  public void testFindCandidateTargets_BlankScope() throws IOException {
    // fixed seed
    Random rand = new Random(145);
    // generate an empty noiseless scope.
    Bitmap scope = TestDataGenerator.generateScope(100, 100, rand, 0, 0, 0.0f, 0.0f);
    List<CandidateTarget> candidates = Analyzer.findCandidateTargets(scope, Float.MIN_VALUE, 1);
    assertEquals(0, candidates.size());
  }

  @Test
  public void testFindCandidateTargets_SingleStarship() throws IOException {
    // fixed seed
    Random rand = new Random(145);
    // generate a noiseless scope with one ship on it.
    Bitmap scope = TestDataGenerator.generateScope(100, 100, rand, 1, 0, 0.0f, 0.0f);
    List<CandidateTarget> candidates1 = Analyzer.findCandidateTargets(scope, 1.0f, 2);
    assertEquals(1, candidates1.size());
    assertEquals(1, candidates1.get(0).type());
    assertEquals(1.0f, candidates1.get(0).confidence());
    // fixed seed
    rand = new Random(145);
    // generate a noisy scope with one ship on it.
    scope = TestDataGenerator.generateScope(100, 100, rand, 1, 0, 0.2f, 0.2f);
    List<CandidateTarget> candidates2 = Analyzer.findCandidateTargets(scope, 0.5f, 1);
    // note there may be random seeds where some or all of these are not
    // true (if the pseudo-random noise happened to blank out the target,
    // for example). however, they are true for random seed 145.
    assertEquals(1, candidates2.size());
    assertEquals(candidates2.get(0), candidates1.get(0));
    assertEquals(1, candidates2.get(0).type());
    assertTrue(candidates2.get(0).confidence() > 0.5f);
  }

  @Test
  public void testFindCandidateTargets_SingleTorpedo() throws IOException {
    // fixed seed
    Random rand = new Random(145);
    // generate a noiseless scope with one ship on it.
    Bitmap scope = TestDataGenerator.generateScope(100, 100, rand, 0, 1, 0.0f, 0.0f);
    List<CandidateTarget> candidates1 = Analyzer.findCandidateTargets(scope, 1.0f, 2);
    assertEquals(1, candidates1.size());
    assertEquals(0, candidates1.get(0).type());
    assertEquals(1.0f, candidates1.get(0).confidence());
    // fixed seed
    rand = new Random(145);
    // generate a noisy scope with one ship on it.
    scope = TestDataGenerator.generateScope(100, 100, rand, 0, 1, 0.2f, 0.2f);
    List<CandidateTarget> candidates2 = Analyzer.findCandidateTargets(scope, 0.5f, 1);
    // note there may be random seeds where some or all of these are not
    // true (if the pseudo-random noise happened to blank out the target,
    // for example). however, they are true for random seed 145.
    assertEquals(1, candidates2.size());
    assertEquals(candidates2.get(0), candidates1.get(0));
    assertEquals(0, candidates2.get(0).type());
    assertTrue(candidates2.get(0).confidence() > 0.5f);
  }

  @Test
  public void testFindCandidateTargets_Multiple() throws IOException {
    // fixed seed
    Random rand = new Random(145);
    // generate a noiseless scope with 3 ships and 3 torpedoes on it.
    Bitmap scope = TestDataGenerator.generateScope(100, 100, rand, 3, 3, 0.0f, 0.0f);
    List<CandidateTarget> candidates1 = Analyzer.findCandidateTargets(scope, 1.0f, 7);
    assertEquals(6, candidates1.size());
    for (CandidateTarget candidate1 : candidates1) {
      assertEquals(1.0f, candidate1.confidence());
    }
    // fixed seed
    rand = new Random(145);
    // generate a noisy scope with 3 ships and 3 torpedoes on it.
    scope = TestDataGenerator.generateScope(100, 100, rand, 3, 3, 0.1f, 0.1f);
    List<CandidateTarget> candidates2 = Analyzer.findCandidateTargets(scope, 0.5f, 6);
    // note there may be random seeds where some or all of these are not
    // true (if the pseudo-random noise happened to blank out the target,
    // for example). however, they are true for random seed 145.
    assertEquals(6, candidates2.size());
    for (CandidateTarget candidate2 : candidates2) {
      assertTrue(candidates1.contains(candidate2));
    }
  }

  @Test
  public void testFindCandidateTargets_Time() throws IOException {
    // fixed seed
    Random rand = new Random(145);
    // generate a large noisy scope with many ships and torpedoes on it.
    Bitmap scope = TestDataGenerator.generateScope(1000, 1000, rand, 300, 500, 0.15f, 0.15f);
    long t0 = System.currentTimeMillis();
    List<CandidateTarget> candidates = Analyzer.findCandidateTargets(scope, 0.5f, 800);
    long t1 = System.currentTimeMillis();
    // note this may not be true on all machines (although it's about 1000ms
    // on the two machines I've tried). this is here to check that nothing
    // has gone disastrously wrong with the algorithm during re-factoring.
    assertTrue(t1 - t0 < 2000);
    // note there may be random seeds where some or all of these are not
    // true (if the pseudo-random noise happened to blank out the target,
    // for example). however, they are true for random seed 145.
    assertEquals(800, candidates.size());
  }
}
