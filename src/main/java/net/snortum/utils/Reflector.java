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

/**
 * <p>Hides some of the complexity and error checking of Reflection.</p>
 * 
 * <p>The ultimate goal of using {@link Reflector} is to invoke some method of a class.
 * To facilitate this, Reflector stores intermediate objects in fields.  The caller sets
 * these fields with the various {@code loadXXX(...)} methods of Reflector.  Say you want
 * to invoke a method in a jar file and need to load it dynamically.  You could use Reflector
 * in this manner:</p>
 * <pre>
 *     Reflector r = new Reflector();
 *     r.loadClass(&lt;className&gt;, &lt;pathToJar&gt;);
 *     r.loadConstructor();
 *     r.loadNewInstance();
 *     r.loadMethod(&lt;methodName&gt;);
 *     Object foo = r.invoke();</pre>
 *     
 * <p>Note that the {@code Object} type should be the type the loaded method returns.  The
 * calls to {@code loadConstuctor()} and {@code loadNewInstance()} can be skipped if the
 * loaded method is static.</p>
 * 
 * <p>Since all intermediate objects (class, constructor, etc.) are held internally, you can
 * reuse the Reflector object for another method call on the same instance, for example:</p>
 * <pre>
 *     r.loadMethod(&lt;anotherMethodName&gt;);
 *     Object bar = r.invoke();</pre>    
 * 
 * @author Knute Snortum
 * @version 2017.10.20
 */
// TODO How to do logging
public class Reflector {
	
	/** The class loader for this session */  
	private ClassLoader loader;
	
	/** The class reference for this session */
	private Class<?> clazz;
	
	/** The constructor reference for this session, or {@code null} for static methods */
	private Constructor<?> constructor;
	
	/** The instance reference for this session, or {@code null} for static methods */
	private Object instance;
	
	/** The method reference for this session */
	private Method method;

	/**
	 * Load a class reference for this fully qualified class name.  Use a
	 * {@link URLClassLoader} built from these paths and this parent.  This class
	 * reference is typically used to find methods in a dynamically loaded
	 * jar file.
	 * 
	 * @param className the fully qualified class name
	 * @param parent the parent for the constructed class loader 
	 * @param paths one of more paths with a valid {@link URL} string format for
	 *        the constructed class loader 
	 * @see #loadClassLoader(ClassLoader, String...)
	 * @see #loadClass(String, ClassLoader)
	 */
	public void loadClass(String className, ClassLoader parent,  String... paths) {
		init();
		loadClassLoader(parent, paths);
		loadClass(className, loader);
	}
	
	/**
	 * Load a class reference for this fully qualified class name.  Use a
	 * {@link URLClassLoader} built from these paths.  The parent of the constructed
	 * class loader is {@link Reflector}'s class loader.  This class reference 
	 * is typically used to find methods in a dynamically loaded jar file.
	 * 
	 * @param className the fully qualified class name
	 * @param paths one of more paths with a valid {@link URL} string format for
	 *        the constructed class loader
	 * @see #loadClassLoader(String...)
	 * @see #loadClass(String, ClassLoader)
	 */
	public void loadClass(String className, String... paths) {
		init();
		loadClassLoader(paths);
		loadClass(className, loader);
	}
	
	/**
	 * Load a class reference for this fully qualified class name and class
	 * loader.
	 * 
	 * @param className the fully qualified class name
	 * @param loader the class loader parent to build the class reference from 
	 */
	public void loadClass(String className, ClassLoader loader) {
		init();
		try {
			clazz = Class.forName(className, true, loader);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Load a class reference for this fully qualified class name.
	 * 
	 * @param className the fully qualified class name
	 */
	public void loadClass(String className) {
		init();
		try {
			clazz = Class.forName(className);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	private void init() {
		this.clazz = null;
		this.constructor = null;
		this.method = null;
		this.instance = null;
	}
	
	/**
	 * Sets the {@link #loader} field to a {@link URLClassLoader} reference based on the paths 
	 * and parent class loader passed.  The paths array cannot be null or empty.  If the 
	 * parent is null then it is set to this class's class loader.
	 * 
	 * @param parent the parent class loader, or null
	 * @param paths one or more path(s) to build the class loader with
	 */
	public void loadClassLoader(ClassLoader parent, String... paths) {
		if (paths == null || paths.length == 0) {
			throw new IllegalArgumentException("Must pass at least one path");
		}
		
		if (parent == null) {
			parent = Reflector.class.getClassLoader();
		}
		
		URL[] urls = new URL[paths.length];
		
		for (int i = 0; i < paths.length; i++) {
    		try {
    			URL url = new URL(paths[i]);
    			urls[i] = url;
    		} catch (MalformedURLException e) {
    			e.printStackTrace();
    			break;
    		}
		}

		if (urls.length > 0) {
			loader = new URLClassLoader(urls, parent);
		}
	}
	
	/**
	 * Sets the {@link #loader} field to a {@link URLClassLoader} reference based on the paths 
	 * passed.  The paths array cannot be null or empty.  The parent class loader  
	 * is set to this class's class loader.
	 * 
	 * @param paths one or more path(s) to build the class loader with
	 * @see #loadClassLoader(ClassLoader, String...)
	 */
	public void loadClassLoader(String... paths) {
		loadClassLoader(null, paths);
	}
	
	/**
	 * Set the {@link #constructor} field to a reference to the loaded class's constructor
	 * based on a signature of the loaded class's constructor.  One of the {@code loadClass()}
	 * methods must be called first.  Calling this method is not necessary if the method you 
	 * wish to invoke is static.
	 * 
	 * @param paramTypes zero or more parameter types for this constructor
	 * @throws IllegalStateException if a {@code loadClass()} method has not been called first
	 */
	public void loadConstructor(Class<?>... paramTypes) {
		if (clazz == null) {
			throw new IllegalStateException("Class has not been successfully loaded");
		}
		
		try {
			constructor = clazz.getDeclaredConstructor(paramTypes);
		} catch (NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Set the {@link #instance} field to a new instance reference based on the loaded
	 * constructor.  {@link #loadConstructor(Class...)} must be called first.  Calling this method 
	 * is not necessary if the method you wish to invoke is static. 
	 *   
	 * @param params zero or more parameters for this constructor
	 * @throws IllegalStateException if {@code loadConstructor(Class...)} has not been called
	 */
	public void loadNewInstance(Object... params) {
		if (constructor == null) {
			throw new IllegalStateException("Constructor has not been successfully loaded "
					+ "or method is static");
		}
		
		try {
			instance = constructor.newInstance(params);
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Set the {@link #method} field based on the loaded class, method name, and parameter
	 * types.  One of the {@code loadClass()} methods must be called first.
	 * 
	 * @param methodName the method name as a String
	 * @param paramTypes zero or more parameter types for this method
	 * @throws IllegalStateException if a {@code loadClass()} method has not been called first
	 */
	public void loadMethod(String methodName, Class<?>... paramTypes) {
		if (clazz == null) {
			throw new IllegalStateException("Class has not been successfully loaded");
		}
		
		try {
			method = clazz.getMethod(methodName, paramTypes);
		} catch (NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Invokes the loaded method and returns the result. The result will be cast to the
	 * type of variable the caller is setting.  The {@link #loadMethod(String, Class...)}
	 * method must be called first.  Result is {@code null} if the method is static or an error
	 * was encountered. 
	 * 
	 * @param <T> the type of the result of invoking the method
	 * @param params zero or more parameters for the invoked method
	 * @return the result of invoking the method, or {@code null} if the context is void or
	 *         an error is encountered
	 * @throws IllegalStateException if {@link #loadMethod(String, Class...)} was not called
	 *         first
	 */
	@SuppressWarnings("unchecked") /* We assume the caller knows what type is returned */
	public <T> T invoke(Object... params) {
		if (method == null) {
			throw new IllegalStateException("Method has not been successfully loaded");
		}

		Object result;
		try {
			result = method.invoke(instance, params);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			if (instance == null) {
				System.err.println("Instance was null but this is not a static method");
			}
			e.printStackTrace();
			return null;
		}
		
		return (T) result;
	}
	
	/** @return the {@link #loader} field (the class loader reference) */
	public ClassLoader getLoader() {
		return loader;
	}
	
	/** @return the {@link #clazz} field (the class reference) */
	public Class<?> getClazz() {
		return clazz;
	}
	
	/** @return the {@link #constructor} reference or {@code null} if method is static */
	public Constructor<?> getConstructor() {
		return constructor;
	}
	
	/** @return the {@link #instance} reference or {@code null} if method is static */
	public Object getNewInstance() {
		return instance;
	}

}
