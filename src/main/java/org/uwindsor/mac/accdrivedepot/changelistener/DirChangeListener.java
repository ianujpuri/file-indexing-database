package org.uwindsor.mac.accdrivedepot.changelistener;

import java.io.File;

/**
 * TODO: Use this class to detect changes in output files 
 * and handle changes to build inverted indexes and file indexes.
 * @author Anuj Puri (110120950)
 *
 */
public class DirChangeListener implements Runnable {

	private ChangeListener<File> changeListener = null;
	private volatile boolean running = false;
	
	public DirChangeListener(ChangeListener<File> listener) {
		if(listener == null) {
			throw new NullPointerException( " change listener is null");
		}
		
		this.changeListener = listener;
	}

	@Override
	public void run() {
		this.running = true;
	}

	
	public void start() {		
		if(this.running) {
			throw new IllegalStateException(" Directory change listener is already running...");
		}
		
		new Thread(this).start();
	}

	public void stop() {
		this.running = false;
	}
}
