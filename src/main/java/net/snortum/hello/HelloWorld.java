package net.snortum.hello;

/**
 * Provides methods to be invoked reflectively
 * 
 * @author Knute Snortum
 * @version 2018-11-20
 */
public class HelloWorld {

	/** 
	 * Prints "Hello, World!" It is invoked reflectively in {@link net.snortum.utils.ReflectorPlay} 
	 */
	public void printIt() {
		System.out.println("Hello, World!");
	}

	/**
	 * Print a customized greeting. It is invoked reflectively in {@link net.snortum.utils.ReflectorPlay}
	 * 
	 * @param name The name to be printed in the greeting
	 */
	public void printIt(String name) {
		System.out.printf("Hello, %s%n", name);
	}
	
}
