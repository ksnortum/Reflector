/*
 * Licensed under the Creative Commons Attribution-ShareAlike 4.0 International 
 * 
 * https://creativecommons.org/licenses/by-sa/4.0/
 */
package net.snortum.utils;

/**
 * A "sand box" or "playground" in which to test the {@link Reflector} class.  Change the 
 * program to execute one of the three {@code runX()} methods.  Unmodified, this program
 * uses itself invoke a method reflectively. 
 * 
 * @author Knute Snortum
 * @version 2017.10.20
 */
public class ReflectorPlay {
	
	private final static long ONE_DAY_IN_MILLIS = 1000 * 60 * 60 * 24;
	
	/** 
	 * The URL of the absolute path to your jar file.  Must in a valid URL string format.
	 * For this demonstration, the latest Joda Time jar file was downloaded and placed in
	 * the folder {@code C:\Programs\joda-time-2.9.9\}.
	 */
	private final static String PATH_TO_JAR = "file:///C:/Programs/joda-time-2.9.9/joda-time-2.9.9.jar";
	
	/**
	 *  A fully qualified class name in the jar file specified by {@code PATH_TO_JAR}.
	 */
	private final static String CLASS_NAME = "org.joda.time.DateTime";

	/**
	 * Change the main method to run one of the three supplied methods, or create your own.
	 * 
	 * @param args arguments from the command line
	 */
	public static void main(String[] args) {
		new ReflectorPlay().run3();
	}
	
	// Demonstrations using a dynamically loaded jar file, Joda Time 

	/**
	 * <p>Demonstration of invoking methods from a jar file dynamically.  After creating a {@link Reflector}
	 * instance, load the class reference using the fully qualified class name and a custom
	 * class loader using the path to the jar file.  Then load a constructor, instance, and the method
	 * {@code getMonthOfYear} from Joda Time.  The method is invoked on the loaded instance and the
	 * result returned.  The return type of {@code invoke()} is determined by the type of the variable
	 * {@code month}.</p>
	 * 
	 * <p>Then, reusing the class and class loader, the constructor and instance, the Joda Time method
	 * {@code getDayOfYear} is called.</p>
	 * 
	 * <p>Finally, the static Joda Time method {@code now()} is called.  The loaded constructor and
	 * instance are ignored</p>
	 */
	@SuppressWarnings("unused")
	private void run() {
		Reflector r = new Reflector();
		r.loadClass(CLASS_NAME, PATH_TO_JAR);
		r.loadConstructor();
		r.loadNewInstance();
		r.loadMethod("getMonthOfYear");
		int month = r.invoke();
		System.out.println("Month: " + month);
		
		r.loadMethod("getDayOfYear");
		System.out.println("Day: " + r.invoke());
		
		r.loadMethod("now");
		System.out.println("Now: " + r.invoke());
	}
	
	/**
	 * <p>As in {@code run()} above, the class, constructor, and instance are loaded for Joda Time.
	 * This time the method {@code minus(long)} is loaded with one parameter type.  The method
	 * is invoked with the number of milliseconds in a day.  This is subtracted from the Joda Time 
	 * instance, which is the current date and time.  Then a new constructor is loaded that takes
	 * an Object and the a new instance is loaded that uses the object that is a Joda Time
	 * current day minus one day.  The {@code getDayOfWeek} method is loaded and invoked.  The
	 * result is a number yesterday's day of the week.</p>
	 */
	@SuppressWarnings("unused")
	private void run2() {
		Reflector r = new Reflector();
		r.loadClass(CLASS_NAME, PATH_TO_JAR);
		r.loadConstructor();
		r.loadNewInstance();
		r.loadMethod("minus", long.class);
		Object minusDate = r.invoke(ONE_DAY_IN_MILLIS);
		
		r.loadConstructor(Object.class);
		r.loadNewInstance(minusDate);
		r.loadMethod("getDayOfWeek");
		int dayOfWeek = r.invoke();
		System.out.println("Day of week: " + dayOfWeek);
	}
	
	// Demonstration of using simple reflection.
	
	/**
	 * Invoke the method {@ #add(int, int)} in this class reflectively.
	 */
	private void run3() {
		Reflector r = new Reflector();
		r.loadClass("net.snortum.utils.ReflectorPlay");
		r.loadMethod("add", int.class, int.class);
		System.out.println("Sum: " + r.invoke(2, 3));
	}
	
	/**
	 * This method gets invoked reflectively.
	 * 
	 * @param a an int
	 * @param b another int
	 * @return the sum
	 */
	public static int add(int a, int b) {
		return a + b;
	}
}
