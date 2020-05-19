package org.yip.sandbox.pch;

import static org.junit.Assert.*;

import java.util.Arrays;

import org.junit.Test;

/**
 * FeatureRepeatablePermutationTest
 */
public class FeatureRepeatablePermutationTest{

	@Test
	public void testCount(){

		FeatureRepeatablePermutation<String> f = FeatureRepeatablePermutation.of(Arrays.asList("A", "B", "C"));

		int count = f.compute(3).size();

		assertEquals(count, 27);

	}

}
