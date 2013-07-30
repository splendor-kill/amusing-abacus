package com.tentacle.callofwild.util;

import gnu.trove.list.array.TIntArrayList;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.InetAddress;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

public class Utils {
	private static final Logger logger = Logger.getLogger(Utils.class);
	private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
	
	private static final byte UTF8HADER1 = (byte) 0xEF;
	private static final byte UTF8HADER2 = (byte) 0xBB;
	private static final byte UTF8HADER3 = (byte) 0xBF;
	
	private static final Pattern SEPARATOR = Pattern.compile("[;\n\r；]");
	private static final Pattern SUB_PAT = Pattern.compile("(.*)[,:，、][\t ]*(\\d*)[,:，、][\t ]*(\\d*)");
    private static final Pattern illegal_char_pattern = Pattern.compile("[\\\\'`!@#$%^&*+\\-=_|:;.,<>()/?~\"\\[\\]\\n\\r\\p{Z}\\s\\uD800\\uDC00-\uDBFF\uDFFF]", Pattern.UNICODE_CASE);
	
	public static final int num_of_records_per_page = 50;
	public static final Random rand = new Random();

	private static ThreadLocal<SimpleDateFormat> threadLocal = new ThreadLocal<SimpleDateFormat>() {
		protected synchronized SimpleDateFormat initialValue() {
			return new SimpleDateFormat(DATE_FORMAT);
		}
	};

    public static boolean hasNextPage(int totalSize, int page) {
        int index = totalSize % num_of_records_per_page;
        int size = totalSize / num_of_records_per_page;
        if (index > 0)
            size += 1;
        return size > page;
    }
	
	public static DateFormat getDateFormat() {
		return threadLocal.get();
	}

	public static String getDate2Str(Date date) {
		return threadLocal.get().format(date);
	}
    
    public static Date parse(String textDate) {
        try {
            return getDateFormat().parse(textDate);
        } catch (ParseException e) {
            logger.error(e.getMessage(), e);
            return new Date();
        }
    }

    private static Properties sConfigProperties = null;

    public static Properties getConfig() {
        if (sConfigProperties != null)
            return sConfigProperties;
        readConfig();
        return sConfigProperties;
    }
    
    public static boolean readConfig() {
        if (sConfigProperties == null)
            sConfigProperties = new Properties();
        
        final String fileName = "res/config.properties";
        boolean isOk = true;
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(fileName);
            sConfigProperties.load(fis);
        } catch (Exception e) {
            logger.error(e);
            System.err.println("configure[" + fileName + "] exception.");
            isOk = false;
        } finally {
            try {
                fis.close();
            } catch (IOException e) {
                logger.error(e);
                System.err.println("configure[" + fileName + "] exception.");
                isOk = false;
            }
        }
        return isOk;
    }
    
    
	public static class Holder<T> {
		public Holder(T value) {
			this.value = value;
		}

		public T value;
	}
    
	public static Writer getUtf8Writer(String fileName) throws IOException {
		FileOutputStream os = new FileOutputStream(fileName);
		byte[] b = { UTF8HADER1, UTF8HADER2, UTF8HADER3 };
		os.write(b);
		return new OutputStreamWriter(os);
	}
    
	public static Reader getUtf8Reader(String fileName) throws IOException {
		byte[] inHeader = new byte[3];
		FileInputStream in;
		try {
			in = new FileInputStream(fileName);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}

		in.read(inHeader, 0, 3);

		if (inHeader[0] == UTF8HADER1 && inHeader[1] == UTF8HADER2
				&& inHeader[2] == UTF8HADER3) {
			return new InputStreamReader(in, "UTF-8");
		}
		in.close();
		return new InputStreamReader(new FileInputStream(fileName), "UTF-8");
	}
    
	// in ms
	public static double getMarchTime(int x1, int y1, int x2, int y2, double speed) {
		double dis = Math.hypot(x2 - x1, y2 - y1);
		return dis / speed;
	}

	public static int getDiff(int x, int y) {
		return Math.abs(x - y);
	}

    public static int beginIndex(int index) {
        return (index <= 0) ? 0 : (index * num_of_records_per_page);
    }
	
    public static int endIndex(int index, int size) {
        int val = (index + 1) * num_of_records_per_page;
        return (val > size) ? size : val;
    }

    public static int totalPage(int size) {
        int total = 0;
        if (size <= 0)
            total = 0;
        if (size % num_of_records_per_page == 0) {
            total = size / num_of_records_per_page;
        } else {
            total = size / num_of_records_per_page + 1;
        }
        return total;
    }
	
    public static boolean isLastPage(int currentPage, int totalPage) {
        return currentPage + 1 <= totalPage;
    }
	
	
	/* get a random integer in [mean-swing, mean+swing]  
	 *          _           _           _
	 *        /   \       /   \       /   \   swing
	 *  -----/-----\-----*-----\-----/-------------- mean
	 *      /       \   /       \   /
	 *                -           -   
	 */
	public static int getIntWave(int mean, int swing) {
		assert swing >= 0;
		int inf = mean - swing;
		int span = swing + swing + 1;
		int i = rand.nextInt(span);
		return inf + i;
	}
	
	public static class Pair {
		public int x, y;

		public Pair(int x, int y) {
			this.x = x;
			this.y = y;
		}

		@Override
		public int hashCode() {
			return mix(x, y);
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Pair other = (Pair) obj;
			if (x != other.x)
				return false;
			if (y != other.y)
				return false;
			return true;
		}
	}
	
	public static boolean pointInRect(int x, int y, int rectX1, int rectY1, int rectX2, int rectY2) {
		if (x >= rectX1 && x <= rectX2 && y >= rectY1 && y <= rectY2)
			return true;
		return false;
	}

	// assume that x, y are short actually
	public static int mix(int x, int y) {
		assert (int) ((short) x) == x;
		assert (int) ((short) y) == y;
		return ((x & 0xffff) << 16) | (y & 0xffff);
	}

	public static Pair tear(int a) {
		return new Pair((a >> 16) & 0xffff, a & 0xffff);
	}
	
	/**
	 * get the localhost's ip
	 * 
	 */
	public static String getLocalHostIp() {
		String ip = "127.0.0.1";
		try {
			InetAddress localHostAddress = InetAddress.getLocalHost();
			ip = localHostAddress.getHostAddress();
		} catch (Exception ex) {
			System.out.println("fectch localhost ip error.");
		}
		return ip;
	}
	
	private static List<Integer> findNearestItems(double x, Map<Integer, Integer> dist) {
		List<Integer> ret = new ArrayList<Integer>();

		int minKey = 0;
		double minDelta = 0;
		boolean isValid = false;

		for (Entry<Integer, Integer> entry : dist.entrySet()) {
			double delta = Math.abs(entry.getValue() - x);
			if (!isValid) {
				minKey = entry.getKey();
				minDelta = delta;
				isValid = true;
			} else {
				if (delta < minDelta) {
					minKey = entry.getKey();
					minDelta = delta;
				}
			}
		}

		if (isValid) {
			int minValue = dist.get(minKey);
			for (Entry<Integer, Integer> entry : dist.entrySet()) {
				if (entry.getValue() == minValue)
					ret.add(entry.getKey());
			}
		}

		return ret;
	}
	
	/**
	 * @param mu		mean
	 * @param sigma		standard deviation
	 * @param dist		object id --> value
	 * @param counter	how many objects will be chosen, at most?
	 * @return			a list of object id selected
	 */
	public static List<Integer> normalDist(double mu, double sigma, Map<Integer, Integer> dist, int counter) {
		List<Integer> lucky = new ArrayList<Integer>();

		for (int i = 0; i < counter; i++) {
			double choose = rand.nextGaussian() * sigma + mu;
			List<Integer> nearest = findNearestItems(choose, dist);
			if (nearest.isEmpty())
				break;
			int bird = nearest.get(rand.nextInt(nearest.size()));
			if (!lucky.contains(bird)) {
				lucky.add(bird);
			}
//			dist.remove(bird);
		}

		return lucky;
	}
	
	//随机幸运值
	public static boolean randomChoice(int srand, int luck) {
		if (srand > 0) {
		     int bird = rand.nextInt(srand);
		     if (bird < luck) {
		    	 return true;
		     }
		}
		
		return false;
	}
	/**
	 * @param dist		object id --> odds
	 * @return			the selected lucky bird
	 */
	public static int randomChoice(Map<Integer, Double> dist) {
		int sum = 0;
		for (Double i : dist.values())
			sum += i;
		int bird = rand.nextInt(sum);
		sum = 0;
		for (Entry<Integer, Double> entry : dist.entrySet()) {
			sum += entry.getValue();
			if (bird < sum)
				return entry.getKey();
		}
		return 0;
	}
	
	/**
	 * 必中一个, 返回索引
	 * @param dist
	 * @return
	 */
	public static int randomChoice(TIntArrayList dist) {
		int sum = 0;
		for (int i = 0; i < dist.size(); i++) {
			sum += dist.get(i);
		}
		
		int bird = rand.nextInt(sum);
		sum = 0;
		
		for (int i = 0; i < dist.size(); i++) {
			sum += dist.get(i);
			if (bird < sum) {
				return i;
			}
		}
		return -1;
	}
	
	/**
	 * 
	 * @param dist		object id --> probability
	 * @return			the selected lucky bird
	 */
	public static List<Integer> randomChoice(Map<Integer, Double> dist, int atMost) {		
		List<Integer> ret = new ArrayList<Integer>();
		double bird = rand.nextDouble();
		for (Entry<Integer, Double> entry : dist.entrySet()) {
			if (bird < entry.getValue()) {
				ret.add(entry.getKey());
			}
		}
		int size = ret.size();
		if (size > atMost) {
			int rm = size - atMost;
			for (int i = 0; i < rm; i++) {
				int boult = rand.nextInt(ret.size());
				ret.remove(boult);
			}
		}
		return ret;
	}

	/**
	 * get greatest common divisor of two
	 */
	public static int gcd(int a, int b) {
		assert a > 0;
		assert b > 0;
		while (b != 0) {
			int r = b;
			b = a % b;
			a = r;
		}
		return a;
	}
	
	/**
	 * find a open space for stand
	 * @param openSpace tileId -> position
	 * @return the foothold tileId
	 */
	public static int findSpace(Map<Integer, Integer> openSpace) {
		int idx = rand.nextInt(openSpace.size());
		Integer[] keys = (Integer[]) openSpace.keySet().toArray();
		return keys[idx];
	}
	
	private static void testFindSpace() {
		Map<Integer, Integer> openSpace = new HashMap<Integer, Integer>() {{
			put(1, mix(0, 0));
			put(2, mix(5, 6));
			put(3, mix(9, 9));
			put(4, mix(3, 7));
			put(5, mix(5, 5));
			put(6, mix(6, 5));
			put(7, mix(6, 6));
			put(8, mix(4, 1));
			put(9, mix(8, 5));
		}};
		while (!openSpace.isEmpty()) {
			int id = findSpace(openSpace);
			Pair pos = tear(openSpace.get(id));
			System.out.println("the center[" + pos.x + ", " + pos.y + "]");
			openSpace.remove(id);
		}
	}
	
	
	private static void testGaussian() {
		Random r = new Random();
		double val;
		double sum = 0;
		int sigma3 = 0;
		final int counter = 10000;
		int bell[] = new int[10];
		for (int i = 0; i < counter; i++) {
			val = r.nextGaussian();
			sum += val;
			double t = -2;
			if (val > 3 || val < -3)
				sigma3++;
			for (int x = 0; x < 10; x++, t += 0.5)
				if (val < t) {
					bell[x]++;
					break;
				}
		}
		System.out.println("Average of values: " + (sum / counter));
		System.out.println("sigma3[" + ((double) sigma3) / counter + "]");
		
		// display bell curve, sideways
		/*
		for (int i = 0; i < 10; i++) {
			for (int x = bell[i]; x > 0; x--)
				System.out.print("*");
			System.out.println();
		}
		*/
	}
	
	private static void testNorm() {
		double mu = 1000;
		double sigma = 1000/3;
		Map<Integer, Integer> dist = new HashMap<Integer, Integer>(); // value->id
		dist.put(-2, 0);
		dist.put(-1, 100);
		dist.put(0, 200);
		dist.put(1, 300);
		dist.put(2, 400);
		dist.put(3, 500);
		dist.put(4, 600);
		dist.put(5, 700);
		dist.put(6, 800);
		dist.put(7, 900);
		dist.put(8, 1000);
		dist.put(9, 1100);
		dist.put(10, 1200);
		dist.put(11, 1300);
		dist.put(12, 1400);
		dist.put(13, 1500);
		dist.put(14, 1600);
		dist.put(15, 1700);
		dist.put(16, 1800);
		dist.put(17, 1900);
		dist.put(18, 2000);

		Map<Integer, Integer> counter = new HashMap<Integer, Integer>(); // id->counter
		Map<Integer, Integer> distVar = new HashMap<Integer, Integer>();

		for (int i = 0; i < 100000; i++) {
			distVar.putAll(dist);
			List<Integer> result = normalDist(mu, sigma, distVar, 1);
			for (Integer id : result) {
				if (counter.containsKey(id))
					counter.put(id, counter.get(id) + 1);
				else
					counter.put(id, 1);
			}
			distVar.clear();
		}
		
		for (Integer id : dist.keySet()) {
			int c = 0;
			if (counter.containsKey(id))
				c = counter.get(id);
			System.out.println("[" + id + "] \t: [" + c + "]");
		}

	}
	
	private static void testParabola() {
		final int counter = 1000000;
		Random r = new Random();

		int hist[] = new int[10];
		for (int i = 0; i < counter; i++) {
			double v = r.nextDouble();
			double inv = Math.sqrt(1 - v);
			double t = 0.1;
			for (int x = 0; x < 10; x++, t += 0.1)
				if (inv < t) {
					hist[x]++;
					break;
				}
		}

		for (int i = 0; i < 10; i++) {
			// for (int x = hist[i]; x > 0; x--)
			// System.out.print("*");
			// System.out.println();
			System.out.println("[" + i + "]\t:[" + hist[i] + "]");
		}
	}
	
	private static void testRandomChoice() {
		Map<Integer, Double> dist = new HashMap<Integer, Double>();
		dist.put(1, 10d);
		dist.put(2, 20d);
		dist.put(3, 30d);
		dist.put(4, 40d);

		Map<Integer, Integer> counter = new HashMap<Integer, Integer>(); // id->counter

		for (int i = 0; i < 100000; i++) {
			int id = randomChoice(dist);
			if (counter.containsKey(id))
				counter.put(id, counter.get(id) + 1);
			else
				counter.put(id, 1);
		}

		for (Integer id : dist.keySet()) {
			int c = 0;
			if (counter.containsKey(id))
				c = counter.get(id);
			System.out.println("[" + id + "] \t: [" + c + "]");
		}
	}
	
	private static void testRandomChoice2() {
		Map<Integer, Double> dist = new HashMap<Integer, Double>();
		dist.put(1, 0.10);
		dist.put(2, 0.20);
		dist.put(3, 0.30);
		dist.put(4, 0.40);
		dist.put(5, 0.40);
		dist.put(6, 0.90);

		Map<Integer, Integer> counter = new HashMap<Integer, Integer>(); // id->counter

		for (int i = 0; i < 10000; i++) {
			List<Integer> result = randomChoice(dist, 1);
			for (Integer id : result) {
				if (counter.containsKey(id))
					counter.put(id, counter.get(id) + 1);
				else
					counter.put(id, 1);
			}
		}

		for (Integer id : dist.keySet()) {
			int c = 0;
			if (counter.containsKey(id))
				c = counter.get(id);
			System.out.println("[" + id + "] \t: [" + c + "]");
		}
	}
	
	private static void testShift() {
		int a = -1;
		System.out.println((a >> 16) & 0xffff);
	}

	private static void testEq() {
		Pair p1 = new Pair(1, 1);
		Pair p2 = new Pair(1, 1);
		Pair p3 = new Pair(0, 0);
		Pair p4 = new Pair(1, 0);
		Pair p5 = new Pair(0, 31);
		System.out.println("p1 " + ((p1 == p2) ? "=" : "!") + "= p2");
		System.out.println("p3 " + ((p3 == p2) ? "=" : "!") + "= p2");
		System.out.println("p2 " + ((p2 == p2) ? "=" : "!") + "= p2");

		Map<Pair, Integer> h = new HashMap<Pair, Integer>();
		h.put(p1, 1);
		h.put(p2, 2);
		h.put(p3, 3);
		h.put(p4, 4);
		h.put(p5, 5);
		System.out.println("map size[" + h.size() + "]");

		System.out.println("p1 " + (p1.equals(p2) ? "=" : "!") + "= p2");
		System.out.println("p3 " + (p3.equals(p2) ? "=" : "!") + "= p2");
		System.out.println("p4 " + (p4.equals(p5) ? "=" : "!") + "= p5");
	}
	
	public static void main(String[] args) {
	    testTimeInPeriod();
//	    testGetTimePeriodInMs();
//	    testUnicodeSupplementary();
		/*
		System.out.println(getIntWave(0, 0));
		System.out.println();
		System.out.println(getIntWave(0, 1));
		System.out.println(getIntWave(0, 1));
		System.out.println(getIntWave(0, 1));
		System.out.println();
		System.out.println(getIntWave(-5, 2));
		System.out.println(getIntWave(-5, 2));
		System.out.println(getIntWave(-5, 2));
		System.out.println(getIntWave(-5, 2));
		System.out.println(getIntWave(-5, 2));
		
		int x = 0x1001;
		int y = 0x10001;		
		System.out.println("x is ["+(((int)((short)x) == x)?"":"not")+"] a short actually");
		System.out.println("y is ["+(((int)((short)y) == y)?"":"not")+"] a short actually");
		 */
		
//		testGaussian();
//		testNorm();
//		testParabola();
//		testRandomChoice();
//		testRandomChoice2();
		
//		testShift();
//		testFindSpace();
//		testEq();

//		testGenName();
		
//		testRationalize();
		
//		testRandom();
//		testProrate();
//		testBringBack();
//		
//		System.out.println("c(5,2)=["+Math.exp(Utils.MathTool.logBinom(5, 2))+"]");
//		System.out.println(Utils.initDate());
		
//		testUpperRound();
//		testIsSameDay();
//		testGetDateTime();
//	    testNormalizeWord();
	}

	private static void testBringBack() {		
		class A {
			void f(Long back) {
				back = 1L;
			}
		}		
		Long back = new Long(0L);
		System.out.println("before["+back+"]");
		A a = new A();
		a.f(back);
		System.out.println("after["+back+"]");
	}
	
	public static String genName() {
		String consonant = "bcdfghjklmnpqrstvwxz";
		// String base = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
		String vowel = "aeiuoy";

		StringBuffer sb = new StringBuffer();
		int number = 0;
		int length = getIntWave(5, 2);
		for (int i = 0; i < length;) {
			if (rand.nextInt(3) < 2) {
				number = rand.nextInt(consonant.length());
				sb.append(consonant.charAt(number));
				i++;
			} else {
				number = rand.nextInt(vowel.length());
				sb.append(vowel.charAt(number));
				i++;
			}
		}
		return sb.toString();
	}
	
	private static void testGenName() {
		for (int i = 0; i < 10; i++)
			System.out.println(genName());
	}

	public static String makeIconUrl(String path, String name) {
		String prefix = getConfig().getProperty("icon_url_prefix", "");
		if (path.endsWith("/"))
			return prefix + path + name;
		else
			return prefix + path + "/" + name;
	}
	
	/*
	 * convert (a/b) to rational, and the denominator not < c
	 * retval.x is the numerator, retval.y is the denominator
	 *  ( a    )        ( x  )
	 *  (---, c) ====>  (--- )
	 *  ( b    )        ( y  )
	 *  
	 *  suffer: y is the min int >= c,  x/y = a/b
	 *  
	 */
	public static Pair rationalize(int a, int b, int c) {
		Pair rational = new Pair(0, 0);
		int g = gcd(a, b);
		int x = a / g;
		int y = b / g;
		if (y < c) {
			int s = (int) Math.ceil((double) c / y);
			x *= s;
			y *= s;
		}
		rational.x = x;
		rational.y = y;
		return rational;
	}
	
	private static void testRationalize() {
		int[] mice = { 100, 125, 156, 195, 244, 305, 381, 477, 596, 745, 931,
				1164, 1455, 1819, 2274, 2842, 3553, 4441, 5551, 6939 };
		for (int n : mice) {
			Pair r = rationalize(n, 3600 * 1000, 5000);
			System.out.println("[" + r.x + "]\t/[" + r.y + "]");
		}
	}
	
	public static <T> T prorateRand(Map<T, Double> prorate) {
		double x = rand.nextDouble();

		for (Entry<T, Double> entry : prorate.entrySet()) {
			double v = entry.getValue();
			if (x < v) {
				return entry.getKey();
			} else {
				x -= v;
			}
		}
		return null;
	}
	
	private static void testProrate() {
		Map<Integer, Double> prorate = new HashMap<Integer, Double>() {{
			put(1, 0.6);
			put(2, 0.3);
			put(3, 0.1);
		}};
		
		Map<Integer, Integer> count = new HashMap<Integer, Integer>();
		for (int i = 0; i < 10000; i++) {
			Integer bird = prorateRand(prorate);
			Integer v = count.get(bird);
			count.put(bird, (v == null ? 0 : v + 1));
		}
		
		System.out.println(count);		
	}
	
	private static void testRandom() {
		System.out.println("many Random in one loop, each Random get a int.");
		for (int i = 0; i < 50; i++) {
			Random r = new Random();
			System.out.println(r.nextInt());
		}
		System.out.println("get many ints from a Random.");
		Random r = new Random(System.currentTimeMillis());
		for (int i = 0; i < 50; i++) {
			System.out.println(r.nextInt());
		}
	}
	
	public static void split(String param, final Map<Integer, Double> chance) {
		assert param != null;
		String[] str = SEPARATOR.split(param);
		for (String s : str) {
			s = s.trim();
			if (s.isEmpty())
				return;
			Matcher m = SUB_PAT.matcher(s);
			if (m.find()) {
				if (m.groupCount() < 3)
					return;
				String k = m.group(2).trim();
				String v = m.group(3).trim();
				chance.put(Integer.parseInt(k), (Double) (Double.parseDouble(v) / 100.0));
			}
		}
	}
	
	public static void parseItems(String param, final Map<Integer, Integer> content) {
		assert param != null;
		String[] str = SEPARATOR.split(param);
		for (String s : str) {
			s = s.trim();
			if (s.isEmpty())
				return;
			Matcher m = SUB_PAT.matcher(s);
			if (m.find()) {
				if (m.groupCount() < 3)
					return;
				String k = m.group(2).trim();
				String v = m.group(3).trim();
				content.put(Integer.parseInt(k), Integer.parseInt(v));
			}
		}
	}
	
	//bound value to [min, max]
	public static int limit(int value, int min, int max) {
		assert min <= max;
		if (value < min)
			return min;
		else if (value > max)
			return max;
		return value;
	}
	
	//bound value to [min, max]
	public static double limit(double value, double min, double max) {
		assert min <= max;
		if (value < min)
			return min;
		else if (value > max)
			return max;
		return value;
	}
	
	public static int productTime(){
		
		return 10 * 60 * 1000;
	}
	
	
	static class MathTool {
		private final static double[] logFac = new double[100];
		static {
			double logRes = 0.0;
			for (int i = 1, stop = logFac.length; i < stop; i++)
				logFac[i] = logRes += Math.log(i);
		}

		public static double logBinom(int n, int k) {
			return logFac[n] - logFac[n - k] - logFac[k];
		}
	}
	
    public static Date initDate() {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, 1970);
        return c.getTime();
    }
	
	private static int round(double x) {
		assert x > 0;
		double digitalNum = Math.ceil(Math.log10(x));
		double tail0 = digitalNum - 2;
		if (tail0 <= 0)
			return (int) Math.round(x);
		double divisor = Math.pow(10, tail0);
		int y = (int) Math.round(x / divisor);
		return y * (int) divisor;
	}
	
	// get the int m, satisfy m >= n(1+bias), and round to nearest tens or hundreds, thousands...
	public static int upperRound(int n, double biasPercent) {
		assert biasPercent >= 0;
		if (biasPercent == 0)
			return n;
		double t = n * (1 + biasPercent);
		if (t > 0)
			return round(t);
		return n;
	}
	
	public static boolean bitHold(int n, int bit) {
		return (n >= bit) && ((n & bit) != 0); 
	}
	
	public static int bitAdd(int n, int bit) {
		return n | bit;
	}
	
	public static int bitSub(int n, int bit) {
		return n & ~bit;
	}
	
	private static void testUpperRound() {
		double[] percent = {0.1, 0.5, 1.0};
		for (double r : percent) {
			for (int i = 0; i < 500; i++) {
				int y = upperRound(i, r);
				System.out.println("upper [" + i + "] with [" + r
						+ "] round to [" + y + "]");
			}
		}
//		for (int i=1; i<100; i+=1)
//			System.out.println("["+i+"] round to ["+round(i)+"]");
	}
	
	public static boolean isSameDay(long date1, long date2) {
		Calendar cal1 = Calendar.getInstance();
		Calendar cal2 = Calendar.getInstance();
		cal1.setTimeInMillis(date1);
		cal2.setTimeInMillis(date2);
		return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR)
				&& cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);		
	}
	
	public static boolean isSameDay(Date date1, Date date2) {
		Calendar cal1 = Calendar.getInstance();
		Calendar cal2 = Calendar.getInstance();
		cal1.setTime(date1);
		cal2.setTime(date2);
		return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR)
				&& cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
	}
	
	
	private static void testIsSameDay() {
		Calendar c = Calendar.getInstance();
		c.clear();
		c.set(Calendar.YEAR, 2012);
		c.set(Calendar.MONTH, 9);
		c.set(Calendar.DAY_OF_MONTH, 19);
		System.out.println(c.getTime()+"\t");
		
		long now = System.currentTimeMillis();
		Date nowd = new Date(now);
		System.out.println(nowd+"\t");
		
		System.out.println(isSameDay(c.getTime(), nowd));
		System.out.println(isSameDay(c.getTimeInMillis(), now));
		
	}
	
	private static void testGetDateTime() {
		Calendar c = Calendar.getInstance();
		c.clear();
		c.set(Calendar.YEAR, 2012);
		c.set(Calendar.MONTH, 10);
		c.set(Calendar.DAY_OF_MONTH, 10);
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 30);
		System.out.println(c.getTime().getTime());
	}
	
	public static String normalizeWord(String str) {
        Matcher m = illegal_char_pattern.matcher(str);
        return m.replaceAll("").trim();
	}
	
	public static boolean isIncludeIllegalChars(String str) {
	    Matcher m = illegal_char_pattern.matcher(str);
        return m.find();
	}
	
	private static void testNormalizeWord() {
	    String ss = " *\\$[]+-&%#!~`h e \"/l lo'哦　靠【[战\n者_之王]】 ";
	    ss = ss + new String(Character.toChars(0x1F312)) + "zz" + new String(Character.toChars(0x8f89));
	    System.out.println(ss);
	    String valid = normalizeWord(ss);
	    System.out.println(valid);
	    System.out.println(isIncludeIllegalChars(ss));
	    System.out.println(isIncludeIllegalChars(valid));
	}
	
	public static String makeString(int... codePoint) {
	    StringBuffer sb = new StringBuffer();
	    for (int cp : codePoint)
	        sb.appendCodePoint(cp);
	    return sb.toString();
	}
	
    private static void testUnicodeSupplementary() {
        int codePoint = 0x10ffff; 
        String hexPattern = codePoint <= 0xFFFF
                ? String.format("\\u%04x", codePoint)
                : String.format("\\u%04x\\u%04x",
                         (int)Character.highSurrogate(codePoint),
                         (int)Character.lowSurrogate(codePoint));
        System.out.println(hexPattern);        
        System.out.println(Character.toChars(0x10000));
        String ss = makeString(0x41, 177700, 0x61);
        System.out.println(ss);
        String pattern = "[\\ud800\\udc00-\\udbff\\udfff]+";
        
        System.out.println(ss.replaceAll(pattern, "*"));
    }
	
	// month start from 0, that is JANUARY is 0;
	// day start from 1;
    public static Date getDate(int year, int month, int day, int hour, int minute, int second) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(0);
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, day);
        c.set(Calendar.HOUR_OF_DAY, hour);
        c.set(Calendar.MINUTE, minute);
        c.set(Calendar.SECOND, second);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTime();
    }
    
    // str in HH:mm:ss or HH:mm form
    public static int getTimePeriodInMs(String str) {
        String tmpString = str.trim();
        if (tmpString.isEmpty())
            return 0;
        String[] times = tmpString.split(":");
        try {
            int hour = Integer.parseInt(times[0].trim());
            int min = Integer.parseInt(times[1].trim());
            int sec = (times.length == 3) ? Integer.parseInt(times[2].trim()) : 0;
            return (hour * 3600 + min * 60 + sec) * 1000;
        } catch (Exception e) {
            logger.error("str format unreadable");
        }
        return 0;
    }
    
    public static boolean timeInPeriod(long now, long begin, long end) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(System.currentTimeMillis());
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        long zero = c.getTimeInMillis();
        long offset = now - zero;
        if (begin <= end) {// in same day
            return offset >= begin && offset < end;
        } else {// stride two days
            return !(offset >= end && offset < begin);
        }
    }
    
    private static void testGetTimePeriodInMs() {
        System.out.println(Utils.getTimePeriodInMs("8:00"));
        System.out.println(Utils.getTimePeriodInMs("08:00:30"));
        System.out.println(Utils.getTimePeriodInMs("10:00"));
        System.out.println(Utils.getTimePeriodInMs("10:00:30"));
    }
    
    private static void testTimeInPeriod() {
        int begin = Utils.getTimePeriodInMs("8:00");
        int end = Utils.getTimePeriodInMs("17:50");
        long now = System.currentTimeMillis();
        System.out.println(timeInPeriod(now, begin, end));
        System.out.println(timeInPeriod(now, end, begin));
    }
	
}
