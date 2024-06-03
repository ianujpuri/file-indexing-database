package org.uwindsor.mac.acc.drivedepot.executor;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.uwindsor.mac.acc.drivedepot.constants.Constants;
import org.uwindsor.mac.acc.drivedepot.writer.IWriter;

/**
 * Common Executor service for handling the thread pool and other concurrent tasks
 * @author 110120950 (AP)
 *
 */
public class ParserExecutorService {

	private static final ParserExecutorService INSTANCE = new ParserExecutorService(); 
	
	private static ExecutorService writingServiceThreads;
	private static ExecutorService extractorServiceThreads;
	
	private ParserExecutorService() {
		
	}
	
	public static final ParserExecutorService getInstance() {
		init();
		return INSTANCE;
	}
	
	private static void init() {
		int writingThreadCount = Integer.parseInt(Constants.THREAD_COUNT_WRITING);
		writingServiceThreads = Executors.newFixedThreadPool(writingThreadCount);
		
		int extractorsThreadCount = Integer.parseInt(Constants.THREAD_COUNT_EXTRACTOR);
		extractorServiceThreads = Executors.newFixedThreadPool(extractorsThreadCount);
	}
	
	public void submitWritingTask(Runnable r) {
		if(r == null || !(r instanceof IWriter)) {
			throw new IllegalArgumentException(" Runnable can't be submitted to writing service.");
		}
		
		writingServiceThreads.submit(r);
	}
	
	public void submitWritingTask(Callable<IWriter> c) {
		if(c == null) {
			throw new IllegalArgumentException(" Callable can't be submitted to writing service.");
		}
		
		writingServiceThreads.submit(c);
	}
	
	public void submitExtractor(Runnable r) {
		
	}
	
	public void submitExtractor(Callable<Object> c){
		if(c == null) {
			throw new IllegalArgumentException(" Callable can't be submitted to extractor service.");
		}
		extractorServiceThreads.submit(c);
	}
}
