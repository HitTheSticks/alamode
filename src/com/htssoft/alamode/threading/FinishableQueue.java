package com.htssoft.alamode.threading;

import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A blocking queue that reliably knows whether or not there's still
 * work being done.
 * */
public class FinishableQueue<T> implements Iterable<T> {
	protected LinkedBlockingQueue<T> queue = new LinkedBlockingQueue<T>();
	protected AtomicInteger outstandingCount = new AtomicInteger();
	protected Object waitMonitor = new Object();
	
	/**
	 * Add an item to the queue, incrementing the work count.
	 * */
	public void add(T item){
		outstandingCount.incrementAndGet();
		queue.add(item);
	}
	
	/**
	 * Add several items to the queue, increasing the work count.
	 * */
	public void addAll(Collection<T> items){
		outstandingCount.addAndGet(items.size());
		queue.addAll(items);
	}
	
	/**
	 * Block until a work item is available.
	 * 
	 * This does not affect the count of remaining
	 * work items.
	 * */
	public T take() throws InterruptedException {
		T retval = queue.take();
		return retval;
	}
	
	/**
	 * Wait until the count of outstanding work drops to zero.
	 * */
	public void await(){
		synchronized (waitMonitor) {
			while (outstandingCount.get() > 0){
				try {
					waitMonitor.wait();
				} catch (InterruptedException ex) {
					ex.printStackTrace();
					break;
				}
			}
		}
	}
	
	/**
	 * Get the underlying queue.
	 * */
	public LinkedBlockingQueue<T> getQueue(){
		return queue;
	}
	
	/**
	 * Iterate the contents.
	 * */
	public Iterator<T> iterator(){
		return queue.iterator();
	}
	
	/**
	 * (shallow) Copy contents of queue into the given collection.
	 * */
	public void copyContents(Collection<T> store){
		store.addAll(queue);
	}
	
	/**
	 * Inform the queue that a worker thread has finished a work item.
	 * 
	 * This reduces the outstanding work count by one, and potentially
	 * awakens the awaiting client thread.
	 * */
	public void finishedItem(){
		synchronized (waitMonitor) {
			int newCount = outstandingCount.decrementAndGet();
			if (newCount == 0){
				waitMonitor.notifyAll();
			}
		}
	}
}
