package com.davidconneely.scope;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Random;

/**
 * Test fixture used to generate test scope image data.
 */
final class TestDataGenerator {

	/**
	 * Not meant for general use, only to generate test data.
	 */
	public static void main(String[] args) throws IOException {
		final int WIDTH = 500;
		final int HEIGHT = 500;
		final int NUM_STARSHIPS = 5;
		final int NUM_TORPEDOES = 5;
		final float PROB_FLIP_ON = 0.2f;
		final float PROB_FLIP_OFF = 0.2f;
		Bitmap bitmap = generateScope(WIDTH, HEIGHT, new Random(),
				NUM_STARSHIPS, NUM_TORPEDOES, PROB_FLIP_ON, PROB_FLIP_OFF);
		Writer wr = new BufferedWriter(new FileWriter("TestData.blf"));
		BitmapLoader.toWriter(bitmap, wr);
		wr.close();
	}

	/**
	 * Create a <code>Bitmap</code> of the given width and height. It will have
	 * had the specified number of starships and torpedoes placed within it, and
	 * then noise applied to the resultant pixels (probabilities for an "off"
	 * pixel outside of a target flipping "on" and for an "on" pixel inside a
	 * target's shape flipping "off" can be given separately).
	 * <p>
	 * For testing, we can use a seeded <code>Random</code> random number
	 * generator to produce predictable scopes. Note also that the target
	 * locations are picked before the noise is applied, so the pseudo-random
	 * locations of the targets will not change if a <code>Random</code>
	 * instance with the same seeding is used, even if the
	 * <code>probFlipOn</code> and <code>probFlipOff</code> probabilities are
	 * varied.
	 */
	static Bitmap generateScope(int width, int height, Random rand,
			int numStarships, int numTorpedoes, float probFlipOn,
			float probFlipOff) throws IOException {
		Bitmap starship = BitmapLoader.fromResource("Starship.blf").trim();
		Bitmap torpedo = BitmapLoader.fromResource("SlimeTorpedo.blf").trim();
		boolean[][] data = new boolean[height][width];
		// add starships.
		if (width >= starship.width() && height >= starship.height()) {
			for (int i = 0; i < numStarships; ++i) {
				int leftx = rand.nextInt(1 + width - starship.width());
				int topy = rand.nextInt(1 + height - starship.height());
				placeTarget(starship, data, leftx, topy);
			}
		}
		// add torpedoes.
		if (width >= torpedo.width() && height >= torpedo.height()) {
			for (int i = 0; i < numTorpedoes; ++i) {
				int leftx = rand.nextInt(1 + width - torpedo.width());
				int topy = rand.nextInt(1 + height - torpedo.height());
				placeTarget(torpedo, data, leftx, topy);
			}
		}
		// add noise.
		for (int i = 0; i < height; ++i) {
			for (int j = 0; j < width; ++j) {
				if (data[i][j]) {
					if (rand.nextFloat() < probFlipOff) {
						data[i][j] = false;
					}
				} else {
					if (rand.nextFloat() < probFlipOn) {
						data[i][j] = true;
					}
				}
			}
		}
		return Bitmap.fromArray(data);
	}

	private static void placeTarget(Bitmap shape, boolean[][] data, int leftX,
			int topY) {
		for (int i = 0; i < shape.height(); ++i) {
			for (int j = 0; j < shape.width(); ++j) {
				if (topY + i < data.length && leftX + j < data[topY + i].length) {
					data[topY + i][leftX + j] |= shape.at(j, i);
				}
			}
		}
	}
}
