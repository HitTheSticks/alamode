package com.htssoft.alamode.threading;

import java.util.Collection;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * A threaded pipeline. You provide the kernel.
 * */
public abstract class ThreadedPipeline<WORK_T, OUT_T> {
	protected LinkedBlockingQueue<WORK_T> input;
	protected LinkedBlockingQueue<OUT_T> output = new LinkedBlockingQueue<OUT_T>();
	
	protected Thread[] threads;
	
	public ThreadedPipeline(int nThreads){
		this(nThreads, new LinkedBlockingQueue<WORK_T>());
	}
	
	public ThreadedPipeline(int nThreads, LinkedBlockingQueue<WORK_T> input){
		this.input = input;
		threads = new Thread[nThreads];
		for (int i = 0; i < threads.length; i++){
			threads[i] = new Thread(new Processor());
			threads[i].setDaemon(true);
			threads[i].start();
		}
	}
	
	public void finalize(){
		for (Thread t : threads){
			if (t != null){
				t.interrupt();
			}
		}
	}
	/**
	 * Get the output queue.
	 * */
	public LinkedBlockingQueue<OUT_T> getOutputQueue(){
		return output;
	}
	
	/**
	 * Submit a single work item.
	 * */
	public void submitWorkItem(WORK_T item){
		input.add(item);
	}
	
	/**
	 * Submit multiple work items.
	 * */
	public void submitWorkItems(Collection<WORK_T> items){
		input.addAll(items);
	}
	
	/**
	 * Wait until the input queue is empty.
	 * */
	public void awaitFinished(){
		if (input.isEmpty()){
			return;
		}
		
		synchronized (input){
			while (!input.isEmpty()){
				try {
					input.wait();
				} catch (InterruptedException ex) {
					ex.printStackTrace();
				}
			}
		}
	}
		
	abstract protected OUT_T processItem(WORK_T item);
	
	
	
	protected class Processor implements Runnable {
		public void run(){
			mainloop:
				while (!Thread.interrupted()){
					WORK_T workItem;
					try {
						workItem = input.take();
					} catch (InterruptedException ex) {
						break mainloop;
					}
					OUT_T outItem = processItem(workItem);
					if (outItem != null){
						output.add(outItem);
					}
					if (input.isEmpty()){
						synchronized (input){
							input.notifyAll();
						}
					}
				}
		}
	}
}
