package com.pack.tools.novdl;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import com.pack.tools.novdl.listener.UpdateTimerListener;
import com.pack.tools.novdl.service.api.Operation;
import com.pack.tools.novdl.service.api.Service;

public class UpdateTimerService implements Runnable, Service {
	private List<UpdateTimerListener> listeners = new Vector<>();
	private long timeOut = 1000 * 60 * 5;
	private boolean active = true;
	private static UpdateTimerService service;
	private PoolExcecutor executor;
	private List<Operation> operations = new ArrayList<>();
	private boolean reset = false;
	private boolean pause = false;

	public static UpdateTimerService getTimerService() {
		if (service == null)
			service = new UpdateTimerService();
		return service;
	}

	private UpdateTimerService() {
		this.executor = new PoolExcecutor(25);
	}

	@Override
	public void run() {
		while (active) {
			doWork();
		}
	}

	public synchronized void doWork() {
		sendTimeOutEvents();
		try {
			// Thread.sleep(timeOut);
			wait(timeOut);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void forceSendEvent() {
		sendTimeOutEvents();
	}

	private void sendTimeOutEvents() {
		if (!reset) {
			for (UpdateTimerListener listener : listeners) {
				executor.submit(new Runnable() {
					@Override
					public void run() {
						listener.onTimeout();
					}
				});
			}
		}
		reset = false;
	}

	public long getTimeOut() {
		return timeOut;
	}

	public void setTimeOut(long timeOut) {
		this.timeOut = timeOut;
	}

	public void stop() {
		this.active = false;
	}

	public void addUpdateTimerListener(UpdateTimerListener listener) {
		this.listeners.add(listener);
	}

	@Override
	public String getOperationName() {
		return "UpdateTimerService";
	}

	@Override
	public Operation[] getSupportedOperations() {
		return (Operation[]) operations.toArray();
	}

	@Override
	public List<Operation> getOperationsList() {
		return operations;
	}

	@Override
	public String getServiceName() {
		return "UpdateTimerService";
	}

	public synchronized void resetTimer() {
		reset = true;
		notify();
	}

	public synchronized void pauseTimer() {
		this.pause = true;
	}

	public synchronized void resumeTimer() {
		this.pause = false;
	}

    @Override
    public String[] getSupportedOperationNames() {
        return null;
    }

}
