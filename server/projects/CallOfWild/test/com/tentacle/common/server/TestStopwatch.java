package com.tentacle.common.server;

import static org.junit.Assert.*;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.tentacle.common.contract.IEventHandler;
import com.tentacle.common.contract.Incident;
import com.tentacle.common.util.Stopwatch;

public class TestStopwatch {
	Mockery context = new Mockery();
	private final int timespan_one_unit = 60 * 60 * 1000;
	private final int rewind_of_timer = 24 * timespan_one_unit;

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testElapse1() {
		final IEventHandler mcHandler = context.mock(IEventHandler.class);
		
		int interval = 60;
		Stopwatch watch = new Stopwatch(0, 0, interval, false);
		final int id = watch.getId();
		context.checking(new Expectations() {{
			oneOf(mcHandler).onTimer(id);
		}});
		
		watch.start();
		int sup = 100;
		while (true) {
			if (sup-- == 0) {
				fail("cannot come here.");
				break;
			}
			watch.elapse(8);
			if (watch.isStopped())
				break;
		}
		assertTrue(watch.getElapsedTime() == 64);
		assertTrue(watch.getRemainTime() == -4);
	}
	
	@Test
	public void testElapse2() {
		final IEventHandler mcHandler = context.mock(IEventHandler.class);
		
		int interval = 60;
		Stopwatch watch = new Stopwatch(0, 0, interval, true);
		watch.addEvent(mcHandler);
		final int id = watch.getId();
		context.checking(new Expectations() {{
			exactly(13).of(mcHandler).onTimer(id);
		}});
		
		watch.start();
		int sup = 100;
		while (true) {
			if (sup-- == 0)
				break;
			watch.elapse(8);
			if (watch.isStopped()) {
				fail("cannot come here.");
				break;
			}
		}
		assertTrue(watch.getElapsedTime() == 800);
		assertTrue(watch.getRemainTime() == 40);
	}
	
	@Test
	public void testElapse3() {
		final IEventHandler mcHandler = context.mock(IEventHandler.class);
		
		int interval = 60;
		Stopwatch watch = new Stopwatch(0, 0, interval, true);
		watch.addEvent(mcHandler);
		final int id = watch.getId();
		context.checking(new Expectations() {{
			never(mcHandler).onTimer(id);
		}});
		
		watch.start();
		int sup = 100;
		while (true) {
			if (sup-- == 0)
				break;
			watch.elapse(0);
			if (watch.isStopped()) {
				fail("cannot come here.");
				break;
			}
		}
		assertTrue(watch.getElapsedTime() == 0);
		assertTrue(watch.getRemainTime() == 60);
	}
	
	@Test
	public void testElapse4() {
		final IEventHandler mcHandler = context.mock(IEventHandler.class);
		
		int interval = 60;
		Stopwatch watch = new Stopwatch(0, 0, interval, false);
		watch.addEvent(mcHandler);
		final int id = watch.getId();
		context.checking(new Expectations() {{
			oneOf(mcHandler).onTimer(id);
		}});
		
		watch.start();
		int sup = 100;
		while (true) {
			if (sup-- == 0) {
				fail("cannot come here.");
				break;
			}
			watch.elapse(61);
			if (watch.isStopped())
				break;
		}
		assertTrue(watch.getElapsedTime() == 61);
		assertTrue(watch.getRemainTime() == -1);
	}
	
	@Test
	public void testElapse5() {
		final IEventHandler mcHandler = context.mock(IEventHandler.class);
		
		int interval = 60;
		Stopwatch watch = new Stopwatch(0, 0, interval, true);
		watch.addEvent(mcHandler);
		final int id = watch.getId();
		context.checking(new Expectations() {{
			exactly(16).of(mcHandler).onTimer(id);
		}});
		
		watch.start();
		int sup = 100;
		while (true) {
			if (sup-- == 0)
				break;
			watch.elapse(1);
			if (watch.isStopped()) {
				fail("cannot come here.");
				break;
			}
		}
		assertTrue(watch.getElapsedTime() == 100);
		assertTrue(watch.getRemainTime() == 20);
	}
	
	@Test
	public void testElapse6() {
		final IEventHandler mcHandler = context.mock(IEventHandler.class);
		
		int interval = 7 * timespan_one_unit;
		int timespan = 1 * timespan_one_unit;
		Stopwatch watch = new Stopwatch(0, 0, interval, true);
		watch.addEvent(mcHandler);
		final int id = watch.getId();
		context.checking(new Expectations() {{
			exactly(14).of(mcHandler).onTimer(id);
		}});
		
		watch.start();
		int sup = 100;
		while (true) {
			if (sup-- == 0)
				break;
			watch.elapse(timespan);
			if (watch.isStopped()) {
				fail("cannot come here.");
				break;
			}
		}
//		assertTrue(watch.getElapsedTime() == 100 * unit);
//		assertTrue(watch.getRemainTime() == 5 * unit);
	}
	
	/*
	 * {rewind: 24} *
	 * {interval: 23, 24, 25} *
	 * {time span: 1, 7, 8} *
	 * {periodic: false, true}
	 *  
	 */
	
	@Test
	public void testElapse7() { //<24, 24, 1, false> 
		final IEventHandler mcHandler = context.mock(IEventHandler.class);
		
		int interval = 24 * timespan_one_unit;
		int timespan = 1 * timespan_one_unit;
		Stopwatch watch = new Stopwatch(0, 0, interval, false);
		watch.addEvent(mcHandler);
		final int id = watch.getId();
		context.checking(new Expectations() {{
			oneOf(mcHandler).onTimer(id);
		}});
		
		watch.start();
		int sup = 100;
		while (true) {
			if (sup-- == 0) {
				fail("cannot come here.");
				break;
			}
			watch.elapse(timespan);
			if (watch.isStopped())				
				break;
		}
		assertTrue(watch.getElapsedTime() == 1 * timespan_one_unit); //should be 24
		assertTrue(watch.getRemainTime() == 0 * timespan_one_unit);
	}
	
	@Test
	public void testElapse8() { //<24, 25, 1, false> 
		final IEventHandler mcHandler = context.mock(IEventHandler.class);
		
		int interval = 25 * timespan_one_unit;
		int timespan = 1 * timespan_one_unit;
		Stopwatch watch = new Stopwatch(0, 0, interval, false);
		watch.addEvent(mcHandler);
		final int id = watch.getId();
		context.checking(new Expectations() {{
			oneOf(mcHandler).onTimer(id);
		}});
		
		watch.start();
		int sup = 100;
		while (true) {
			if (sup-- == 0) {
				fail("cannot come here.");
				break;
			}
			watch.elapse(timespan);
			if (watch.isStopped())				
				break;
		}
		assertTrue(watch.getElapsedTime() == 2 * timespan_one_unit); //should be 25
		assertTrue(watch.getRemainTime() == 0 * timespan_one_unit);
	}
	
	@Test
	public void testElapse9() { //<24, 25, 7, false> 
		final IEventHandler mcHandler = context.mock(IEventHandler.class);
		
		int interval = 25 * timespan_one_unit;
		int timespan = 7 * timespan_one_unit;
		Stopwatch watch = new Stopwatch(0, 0, interval, false);
		watch.addEvent(mcHandler);
		final int id = watch.getId();
		context.checking(new Expectations() {{
			oneOf(mcHandler).onTimer(id);
		}});
		
		watch.start();
		int sup = 100;
		while (true) {
			if (sup-- == 0) {
				fail("cannot come here.");
				break;
			}
			watch.elapse(timespan);
			if (watch.isStopped())				
				break;
		}
		assertTrue(watch.getElapsedTime() == 7 * timespan_one_unit); //should be 28 
		assertTrue(watch.getRemainTime() == -3 * timespan_one_unit);
	}
	
	/*
	 * {rewind: 24u} *
	 * {var: -25u,-24u,-23u, -1u,0,1u, 23u,24u,25u, -1000, 1000} *
	 * {interval: 23u, 24u, 25u, 1000} *
	 * {timespan: 1, 5, 7, 8} *
	 * {periodic: false, true}*
	 * 
	 */
	@Test
	public void testAdjust1() {
		int interval = 1000;
		int timespan = 10;
		
		Stopwatch watch = new Stopwatch(0, 0, interval, false);
		watch.adjust(1000);
		assertTrue(watch.getElapsedTime() == 1000);
		assertTrue(watch.getRemainTime() == 0);
		watch.adjust(-100);
		assertTrue(watch.getElapsedTime() == 900);
		assertTrue(watch.getRemainTime() == 100);
		watch.adjust(0);
		assertTrue(watch.getElapsedTime() == 900);
		assertTrue(watch.getRemainTime() == 100);		
	}

	
	

}
