package com.pack.tools.novdl;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class PoolExcecutor {
	private ThreadPoolExecutor executor;
	private int threadLimit;
	public boolean runFlag = true;

	public PoolExcecutor(int limit) {
		this.threadLimit = limit;
		this.init();
	}

	public void init() {
		executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(threadLimit);

	}

	public void submit(Runnable runnable) {
		executor.execute(runnable);
	}

	public void stop() {
		this.executor.shutdown();
		this.runFlag = false;
	}

	private void run() {
		while (runFlag) {
			executor.getActiveCount();
		}
	}

	public ThreadPoolExecutor getThreadPoolExcutor() {
		return executor;
	}
}
