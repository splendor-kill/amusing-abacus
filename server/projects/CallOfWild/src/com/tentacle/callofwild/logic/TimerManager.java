package com.tentacle.callofwild.logic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class TimerManager {
	public static final int timespan_1_sec_in_ms = 1000;
	public static final int timespan_1_min_in_ms  = 60 * timespan_1_sec_in_ms;
	public static final int timespan_10_min_in_ms = 10 * timespan_1_min_in_ms;
	public static final int timespan_1_hour_in_ms = 60 * timespan_1_min_in_ms;
	public static final int timespan_1_day_in_ms  = 24 * timespan_1_hour_in_ms;
	public static final int timespan_1_week_in_ms = 7 * timespan_1_day_in_ms;
	public static final int timespan_res_refresh_rate_in_ms = 5 * timespan_1_sec_in_ms;
	public static final int timespan_update_rate_in_ms = 30 * timespan_1_sec_in_ms;
	public static final int timespan_sys_param_renew_in_ms  = 2 * timespan_1_hour_in_ms;
	public static final int timespan_sys_param_yield_in_ms  = 12 * timespan_1_hour_in_ms;
	public static final int timespan_al_invade_in_ms  = 20 * timespan_1_min_in_ms;
	public static final int timespan_infinity = 0x7fffffff;
	public static final int timespan_for_test 	= 2 * timespan_1_sec_in_ms;
		
	public static final int timer_res_refresh 		= 1;
	public static final int timer_24_hour			= 2;
	public static final int timer_upgrade_building  = 3;
	public static final int timer_upgrade_battle_array= 4;
	public static final int timer_upgrade_tech    	= 5;
	public static final int timer_launch_war  		= 6;
	public static final int timer_be_attack			= 7;
	public static final int timer_marching 			= 8;
	public static final int timer_red_alert   		= 9;
	public static final int timer_loyalty_update 	= 10;
	public static final int timer_explore			= 11;
	public static final int timer_consume_food 		= 12;
	public static final int timer_envoy_return		= 13;
	public static final int timer_build				= 14;
	public static final int timer_upgrade_skill   	= 15;
	public static final int timer_training_corps	= 16;
	public static final int timer_sys_param_renew 	= 17;
	public static final int timer_al_tech 			= 18;
	public static final int timer_universal			= 19;
	public static final int timer_yeild   			= 20;
	public static final int timer_update			= 21;
	public static final int timer_al_battle_array 	= 22;
	public static final int timer_sendgamesrvstatus 	= 23;
	
	
	private static TimerManager inst = new TimerManager();
	
	private ArrayList<Stopwatch> timers = new ArrayList<Stopwatch>();
	//通过ID建立索引
	public HashMap<Integer, Stopwatch> timersById = new HashMap<Integer, Stopwatch>();
	
	private TimerManager() {
	}

	public static TimerManager getInstance() {
		return inst;
	}
	
	public Stopwatch newCountdownTimer(int kind, long countdownTime) {
		if (countdownTime <= 0) countdownTime = 1;
		Stopwatch w = new Stopwatch(kind, System.currentTimeMillis(), countdownTime, false);
		add(w);
		w.start();
		return w;
	}
	
	public Stopwatch newClock(int kind, long interval) {
		if (interval <= 0) interval = 1;
		Stopwatch w = new Stopwatch(kind, System.currentTimeMillis(), interval, true);
		add(w);
		w.start();
		return w;
	}	
	
	public void elapse(long frameTime) {
		// frameTime *= Config.timer_scale;
		for (int i = 0; i < timers.size(); i++) {
			Stopwatch sw = timers.get(i);

			if (sw.isStopped()) {
				timers.remove(i);
				timersById.remove(sw.getId());
				i--;
			} else {
				sw.elapse(frameTime);
				if (sw.isStopped()) {
					timers.remove(i);
					i--;
				}
			}
		}
	}
	
	public void add(Stopwatch watch) {
		timers.add(watch);	
		timersById.put(watch.getId(), watch);
	}
	
	public void remove(int id) {
	   Stopwatch stopwatch = timersById.remove(id);
		if (stopwatch != null) {
			//停止
			stopwatch.stop();
			//可以不用此删除 ?????
			timers.remove(stopwatch);
			return;
		}
	}

	//清除所有
	public void clear() {
		timers.clear();
		timersById.clear();
	}
	
	public Stopwatch getTimer(int id) {
		Stopwatch stopwatch = timersById.get(id);
		return stopwatch;
	}
	
	public List<Stopwatch> getTimer(int ownerId, int kind) {
		List<Stopwatch> ret = new ArrayList<Stopwatch>();
		for (Stopwatch t : timers) {
			if (t.getOwnerId() == ownerId && t.getKind() == kind)
				ret.add(t);
		}
		return ret;
	}
	
	public static void main(String[] args) {
//		int nagbig = 0x7fffffff;
//		System.out.println("num["+nagbig+"], bigger num["+(nagbig+1)+"]");
		
		List<Integer> li = new ArrayList<Integer>() {{
			add(1);
			add(2);
			add(3);
			add(2);		
			add(4);
		}};
		
		
//		for (Integer i : li) {
//			if (i == 2)
//				li.remove(i);
//		}
		
//		System.out.println("size of li == ["+li.size()+"]");
//		Iterator<Integer> it = li.iterator();
//		while (it.hasNext()) {
//			if (it.next() == 2)
//				it.remove();			
//		}
//		System.out.println("size of li == ["+li.size()+"], "+li);
		
		List<Integer> li2 = Arrays.asList(1, 1, 2, 3, 5, 8);
		System.out.println("size of li2 == ["+li2.size()+"], "+li2);		
//		li2.add(13);
//		li2.remove(1);
//		System.out.println("size of li2 == ["+li2.size()+"], "+li2);
		
		List<Integer> li3 = Arrays.asList(new Integer[] {1, 1, 2, 3, 5, 8, 13, 21});
		System.out.println("size of li3 == ["+li3.size()+"], "+li3);	
		
		
	}

}
