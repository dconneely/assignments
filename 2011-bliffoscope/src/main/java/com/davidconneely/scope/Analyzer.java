package com.davidconneely.scope;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public final class Analyzer {
	/**
	 * Given some scope data, and two thresholds - the minimum confidence
	 * level, and the maximum number of candidates - return a list of
	 * candidate targets, sorted in descending order of confidence.
	 */
	public static List<CandidateTarget> findCandidateTargets(Bitmap scope,
			float limitConfidence, int limitCount) throws IOException {
		// calculate confidence we have a torpedo with its top-left corner at
		// each location.
		Bitmap torpedo = BitmapLoader.fromResource("SlimeTorpedo.blf").trim();
		float[][] torpedoConfidence = calculateConfidence(torpedo, scope);
		// calculate confidence we have a starship with its top-left corner at
		// each location.
		Bitmap starship = BitmapLoader.fromResource("Starship.blf").trim();
		float[][] starshipConfidence = calculateConfidence(starship, scope);
		// now find the locations with confidence above the minimum specified;
		// and convert the top-left coords to midpoint coords.
		Bitmap[] targets = new Bitmap[] { torpedo, starship };
		float[][][] confidences = new float[][][] {
				torpedoConfidence, starshipConfidence };
		List<CandidateTarget> candidates = confidencesToCandidates(targets,
				confidences, limitConfidence);
		// sort these candidates in confidence order.
		Collections.sort(candidates, CandidateTarget.confidenceOrder());
		// now take a subset of the first limitCount candidates.
		List<CandidateTarget> ret = new ArrayList<CandidateTarget>(
				Math.min(candidates.size(), limitCount));
		for (CandidateTarget candidate: candidates) {
			if (ret.size() >= limitCount) {
				break;
			}
			ret.add(candidate);
		}
		return ret;
	}

	/**
	 * Calculate confidence as the fraction of pixels within the target's shape
	 * that are "on" in the scope image, assuming the target is positioned at
	 * each possible location in the scope image. We only check positions where
	 * the target would be located entirely within the scope image (since if
	 * the target is only partially visible, we're not going to be sure anyway).
	 */
	private static float[][] calculateConfidence(Bitmap target, Bitmap scope) {
		float[][] confidence = new float[scope.height()][scope.width()];
		int targetCardinality = target.cardinality();
		int targetWidth = target.width();
		int targetHeight = target.height();
		int scopeWidth = scope.width();
		int scopeHeight = scope.height();
		for (int i = 0; i < 1+scopeHeight-targetHeight; ++i) {
			for (int j = 0; j < 1+scopeWidth-targetWidth; ++j) {
				int targetWeight = 0;
				for (int m = 0; m < targetHeight; ++m) {
					for (int n = 0; n < targetWidth; ++n) {
						if (target.at(n, m) && scope.at(j+n, i+m)) {
							++targetWeight;
						}
					}
				}
				confidence[i][j] = (float) targetWeight
						/ (float) targetCardinality;
			}
		}
		return confidence;
	}
	
	/**
	 * Convert an array of confidence arrays into a list of candidate targets
	 * by ignoring any elements in the confidence array that have less than
	 * the given threshold.
	 * <p>
	 * The target objects are needed only for their midpoint information - to
	 * convert the array indices within the corresponding confidence array
	 * (which refer to the top-left of the target's bitmap) into target
	 * locations which refer to the midpoint of the target's bitmap.
	 */
	private static List<CandidateTarget> confidencesToCandidates(
			Bitmap[] targets, float[][][] confidences, float limitConfidence) {
		int ntargets = confidences.length;
		int height = (ntargets != 0) ? confidences[0].length : 0;
		int width = (height != 0) ? confidences[0][0].length : 0;

		List<CandidateTarget> ret = new LinkedList<CandidateTarget>();
		for (int i = 0; i < height; ++i) {
			for (int j = 0; j < width; ++j) {
				for (int k = 0; k < ntargets; ++k) {
					if (confidences[k][i][j] >= limitConfidence) {
						ret.add(new CandidateTarget(k, j+targets[k].midX(),
								i+targets[k].midY(), confidences[k][i][j]));
					}
				}
			}
		}
		return ret;
	}
}