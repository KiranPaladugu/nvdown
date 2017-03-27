/* ********************************************************************************
 * All rights reserved.
 ******************************************************************************* */
package com.pack.tools.novdl;

import java.util.LinkedList;
import java.util.Queue;

public class BlockingQueue<T> {

	private Queue<T> queue = new LinkedList<T>();
	private int capacity;
	private String name = "Queue";

	/**
	 * 
	 */
	public BlockingQueue() {
		this(10);
	}

	public BlockingQueue(int capacity, String name) {
		this.capacity = capacity;
		this.setName(name);
	}

	public BlockingQueue(int capacity) {
		this.capacity = capacity;
	}

	public synchronized void insert(T element) throws InterruptedException {
		while (queue.size() == capacity) {
			log(name + " is full : wait until queue is available");
			wait();
		}

		queue.add(element);
		log(name + " is available to read : notify others to read");
		notify();
		// notifyAll();
	}

	public synchronized T remove() throws InterruptedException {
		while (queue.isEmpty()) {
			log(name + " is empty : wait until queue is available to read");
			wait();
		}

		T item = queue.remove();
		log("Making " + name + " is avilable to fill : notify others to fill");
		notify();
		// notifyAll();
		return item;
	}

	public synchronized void insert(T element, long timeout) throws InterruptedException {
		while (queue.size() == capacity) {
			log(name + " is full : wait until queue is available");
			wait(timeout);
		}

		queue.add(element);
		log(name + " is availabe to read : notify others to read");
		notify();
		// notifyAll();
	}

	public synchronized T remove(long timeout) throws InterruptedException {
		while (queue.isEmpty()) {
			log(name + " is empty : wait until queue is available");
			wait(timeout);
		}

		T item = queue.remove();
		log("Making " + name + " is avilable to fill : notify others to fill");
		notify();
		// notifyAll();
		return item;
	}

	public synchronized T peek() {
		return queue.peek();
	}

	public String getThreadName() {
		return Thread.currentThread().getName();
	}

	public void log(String log) {
		// System.out.println("[DEBUG] => "+getThreadName()+" - "+log);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}