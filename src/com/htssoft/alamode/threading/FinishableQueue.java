package com.htssoft.alamode.threading;

import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class FinishableQueue<T> implements Iterable<T> {
	protected LinkedBlockingQueue<T> queue = new LinkedBlockingQueue<T>();
	protected AtomicInteger outstandingCount = new AtomicInteger();
	protected Object waitMonitor = new Object();
	
	public void add(T item){
		outstandingCount.incrementAndGet();
		queue.add(item);
	}
	
	public void addAll(Collection<T> items){
		outstandingCount.addAndGet(items.size());
		queue.addAll(items);
	}
	
	public T take() throws InterruptedException {
		T retval = queue.take();
		return retval;
	}
	
	public void await(){
		while (outstandingCount.get() > 0){
			synchronized (waitMonitor) {
				try {
					waitMonitor.wait();
				} catch (InterruptedException ex) {
					ex.printStackTrace();
					break;
				}
			}
		}
	}
	
	public LinkedBlockingQueue<T> getQueue(){
		return queue;
	}
	
	public Iterator<T> iterator(){
		return queue.iterator();
	}
	
	public void copyContents(Collection<T> store){
		store.addAll(queue);
	}
	
	public void finishedItem(){
		int newCount = outstandingCount.decrementAndGet();
		if (newCount == 0){
			synchronized (waitMonitor) {
				waitMonitor.notifyAll();
			}
		}
	}
}
