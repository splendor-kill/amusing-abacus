package com.tentacle.callofwild.util;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.tentacle.callofwild.util.Utils;

public class TestUtils {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testRationalize() {		
		Utils.Pair p = Utils.rationalize(100, 3600, 5);
		assertTrue(p.x == 1 && p.y == 36);
		p = Utils.rationalize(500, 3600, 5);
		assertTrue(p.x == 5 && p.y == 36);		
		p = Utils.rationalize(2400, 3600, 5);
		assertTrue(p.x == 4 && p.y == 6);		
		p = Utils.rationalize(3600, 3600, 5);
		assertTrue(p.x == 5 && p.y == 5);
		p = Utils.rationalize(4000, 3600, 5);
		assertTrue(p.x == 10 && p.y == 9);		
		p = Utils.rationalize(2, 7, 5);
		assertTrue(p.x == 2 && p.y == 7);
		p = Utils.rationalize(2, 4, 5);
		assertTrue(p.x == 3 && p.y == 6);
		p = Utils.rationalize(1, 4, 5);
		assertTrue(p.x == 2 && p.y == 8);
		p = Utils.rationalize(4, 4, 5);
		assertTrue(p.x == 5 && p.y == 5);
		p = Utils.rationalize(1, 5, 5);
		assertTrue(p.x == 1 && p.y == 5);
		p = Utils.rationalize(10, 5, 5);
		assertTrue(p.x == 10 && p.y == 5);
		p = Utils.rationalize(2, 6, 5);
		assertTrue(p.x == 2 && p.y == 6);
		p = Utils.rationalize(9, 15, 7);
		assertTrue(p.x == 6 && p.y == 10);
		
	}
	
	@Test
	public void testLimit() {
		assertTrue(Utils.limit(-10, 0, 100) == 0);
		assertTrue(Utils.limit(10, 0, 100) == 10);
		assertTrue(Utils.limit(0, 0, 100) == 0);
		assertTrue(Utils.limit(100, 0, 100) == 100);
		assertTrue(Utils.limit(110, 0, 100) == 100);
		assertTrue(Utils.limit(-10, -100, 100) == -10);
		assertTrue(Utils.limit(-110, -100, 100) == -100);
		assertTrue(Utils.limit(100-1, 100, 100) == 100);
		assertTrue(Utils.limit(100, 100, 100) == 100);
		assertTrue(Utils.limit(100+1, 100, 100) == 100);
	}
	
	@Test
	public void testCombination() {
		assertTrue(Math.round(Math.exp(Utils.MathTool.logBinom(52, 7))) == 133784560);
		assertTrue(Math.round(Math.exp(Utils.MathTool.logBinom(5, 2))) == 10);
		assertTrue(Math.round(Math.exp(Utils.MathTool.logBinom(5, 1))) == 5);
	}
	
	@Test
	public void testUpperRound() {
		assertTrue(Utils.upperRound(100, 0.0) == 100);
		assertTrue(Utils.upperRound(100, 0.1) == 110);
		assertTrue(Utils.upperRound(101, 0.1) == 110);
		assertTrue(Utils.upperRound(105, 0.1) == 120);
		assertTrue(Utils.upperRound(109, 0.1) == 120);
		assertTrue(Utils.upperRound(111, 0.1) == 120);
		assertTrue(Utils.upperRound(100, 0.5) == 150);
		assertTrue(Utils.upperRound(100, 1.0) == 200);
		assertTrue(Utils.upperRound(1234, 0.0) == 1234);
		assertTrue(Utils.upperRound(7856, 0.0) == 7856);
		assertTrue(Utils.upperRound(1234, 0.1) == 1400);
		assertTrue(Utils.upperRound(7856, 0.1) == 8600);
		assertTrue(Utils.upperRound(1234, 0.5) == 1900);
		assertTrue(Utils.upperRound(7856, 0.5) == 12000);
		assertTrue(Utils.upperRound(1234, 1.0) == 2500);
		assertTrue(Utils.upperRound(7856, 1.0) == 16000);
	}

	@Test
	public void testBitOp() {
		assertTrue(Utils.bitHold(3, 1));
		assertTrue(Utils.bitHold(3, 2));
		assertTrue(Utils.bitHold(3, 3));
		assertFalse(Utils.bitHold(3, 4));
		assertFalse(Utils.bitHold(3, 7));
		assertFalse(Utils.bitHold(3, 8));
		
		assertFalse(Utils.bitHold(4, 1));
		assertFalse(Utils.bitHold(4, 2));
		assertFalse(Utils.bitHold(4, 3));
		assertTrue(Utils.bitHold(4, 4));
		assertFalse(Utils.bitHold(4, 5));

		assertTrue(Utils.bitHold(7, 3));
		assertTrue(Utils.bitHold(7, 5));
		assertTrue(Utils.bitHold(15, 7));
		assertFalse(Utils.bitHold(7, 15));
		
		assertTrue(Utils.bitAdd(0, 1) == 1);
		assertTrue(Utils.bitAdd(0, 8) == 8);
		assertTrue(Utils.bitAdd(3, 1) == 3);
		assertTrue(Utils.bitAdd(3, 2) == 3);
		assertTrue(Utils.bitAdd(3, 4) == 7);
		assertTrue(Utils.bitAdd(3, 8) == 11);
		
		assertTrue(Utils.bitSub(3, 1) == 2);
		assertTrue(Utils.bitSub(3, 2) == 1);
		assertTrue(Utils.bitSub(8, 1) == 8);
		assertTrue(Utils.bitSub(8, 2) == 8);
		assertTrue(Utils.bitSub(8, 4) == 8);
		assertTrue(Utils.bitSub(8, 8) == 0);
		
	}
	
}
