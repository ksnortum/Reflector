/*
 * Licensed under the Creative Commons Attribution-ShareAlike 4.0 International 
 * 
 * https://creativecommons.org/licenses/by-sa/4.0/
 */
package net.snortum.utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Date;

/**
 * This class is a "sand box" or "playground" for working with Java's reflection classes.
 * It was developed as a way to guide the development of the {@link Reflector} class and
 * is left here as a tutorial.
 *   
 * @author Knute Snortum
 * @version 2017.10.20
 */
public class ReflectionPlay {
	
	private final static long ONE_DAY_IN_MILLIS = 1000 * 60 * 60 * 24;
	
	// The URL of the absolute path to your jar file
	private final static String PATH_TO_JAR = "file:///C:/Programs/joda-time-2.9.9/joda-time-2.9.9.jar";
	
	// The fully qualified class name in the jar
	private final static String CLASS_NAME = "org.joda.time.DateTime";

	public static void main(String[] args) {
		new ReflectionPlay().run();
	}

	private void run() {
		try {
			URL url = new URL(PATH_TO_JAR);
			ClassLoader parent = getClass().getClassLoader();
			URLClassLoader loader = new URLClassLoader(new URL[] {url}, parent);
			Class<?> clazz = Class.forName(CLASS_NAME, true, loader);
			
			Constructor<?> constructor = clazz.getConstructor();
			Object instance = constructor.newInstance();
			Method method = clazz.getMethod("getMonthOfYear");
			int month = (int) method.invoke(instance);
			
			System.out.println("Month: " + month);
		} catch (MalformedURLException | ClassNotFoundException | NoSuchMethodException | SecurityException
				| InstantiationException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("unused")
	private void run2() {
		try {
			URL url = new URL(PATH_TO_JAR);
			ClassLoader parent = getClass().getClassLoader();
			URLClassLoader loader = new URLClassLoader(new URL[] {url}, parent);
			Class<?> clazz = Class.forName(CLASS_NAME, true, loader);
			
			Constructor<?> constructor = clazz.getConstructor(long.class);
			Object instance = constructor.newInstance(new Date().getTime());
			Method method = clazz.getMethod("getMonthOfYear");
			int month = (int) method.invoke(instance);
			
			System.out.println("Month: " + month);
		} catch (MalformedURLException | ClassNotFoundException | NoSuchMethodException | SecurityException
				| InstantiationException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unused")
	private void run3() {
		try {
			URL url = new URL(PATH_TO_JAR);
			ClassLoader parent = getClass().getClassLoader();
			URLClassLoader loader = new URLClassLoader(new URL[] {url}, parent);
			Class<?> clazz = Class.forName(CLASS_NAME, true, loader);
			
			Constructor<?> constructor = clazz.getConstructor();
			Object instance = constructor.newInstance();
			Method minusMethod = clazz.getMethod("minus", long.class);
			Object minusDate = minusMethod.invoke(instance, ONE_DAY_IN_MILLIS); 
			
			Constructor<?> minusConstructor = clazz.getConstructor(Object.class);
			Object minusInstance = minusConstructor.newInstance(minusDate);
			Method monthMethod = clazz.getMethod("getDayOfWeek");
			int dayOfWeek = (int) monthMethod.invoke(minusInstance);
					
			System.out.println("Day of the Week: " + dayOfWeek);
		} catch (MalformedURLException | ClassNotFoundException | NoSuchMethodException | SecurityException
				| InstantiationException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("unused")
	private void run4() {
		try {
			URL url = new URL(PATH_TO_JAR);
			ClassLoader parent = getClass().getClassLoader();
			URLClassLoader loader = new URLClassLoader(new URL[] {url}, parent);
			Class<?> clazz = Class.forName(CLASS_NAME, true, loader);
			
			Method nowMethod = clazz.getMethod("now");
			Object nowDate = nowMethod.invoke(null); 
			
			Constructor<?> minusConstructor = clazz.getConstructor(Object.class);
			Object minusInstance = minusConstructor.newInstance(nowDate);
			Method monthMethod = clazz.getMethod("getDayOfWeek");
			int dayOfWeek = (int) monthMethod.invoke(minusInstance);
					
			System.out.println("Day of the Week: " + dayOfWeek);
		} catch (MalformedURLException | ClassNotFoundException | NoSuchMethodException | SecurityException
				| InstantiationException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			e.printStackTrace();
		}
	}
}
