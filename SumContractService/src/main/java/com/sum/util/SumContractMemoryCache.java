package com.sum.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections4.MapIterator;
import org.apache.commons.collections4.map.LRUMap;

public class SumContractMemoryCache<K, T> {

	private long timeToLive;
	private LRUMap<K, T> myMemoryMap;

	protected class memoryChacheObject {
		public long lastAccessed = System.currentTimeMillis();
		public List<Object> value;

		protected memoryChacheObject(List<Object> numbers) {
			this.value = numbers;
		}
	}

	public SumContractMemoryCache(long timeToLive, final long timerInterval, int maxItems) {
		this.timeToLive = timeToLive * 1000;

		myMemoryMap = new LRUMap(maxItems);

		if (timeToLive > 0 && timerInterval > 0) {

			Thread t = new Thread(new Runnable() {
				public void run() {
					while (true) {
						try {
							Thread.sleep(timerInterval * 1000);
						} catch (InterruptedException ex) {
						}
						cleanup();
					}
				}
			});

			t.setDaemon(true);
			t.start();
		}
	}

	public void put(K key, List<Object> numbers) {
		synchronized (myMemoryMap) {
			myMemoryMap.put(key, (T) new memoryChacheObject(numbers));
		}
	}

	@SuppressWarnings("unchecked")
	public T get(K key) {
		synchronized (myMemoryMap) {
			memoryChacheObject c = (memoryChacheObject) myMemoryMap.get(key);

			if (c == null)
				return null;
			else {
				c.lastAccessed = System.currentTimeMillis();
				return (T) c.value;
			}
		}
	}

	public void remove(K key) {
		synchronized (myMemoryMap) {
			myMemoryMap.remove(key);
		}
	}

	public int size() {
		synchronized (myMemoryMap) {
			return myMemoryMap.size();
		}
	}

	@SuppressWarnings("unchecked")
	public void cleanup() {
		long now = System.currentTimeMillis();
		ArrayList<K> deleteKey = null;

		synchronized (myMemoryMap) {
			MapIterator<K, T> itr = myMemoryMap.mapIterator();

			deleteKey = new ArrayList<K>((myMemoryMap.size() / 2) + 1);
			K key = null;
			memoryChacheObject c = null;

			while (itr.hasNext()) {
				key = (K) itr.next();
				c = (memoryChacheObject) itr.getValue();

				if (c != null && (now > (timeToLive + c.lastAccessed))) {
					deleteKey.add(key);
				}
			}
		}

		for (K key : deleteKey) {
			synchronized (myMemoryMap) {
				myMemoryMap.remove(key);
			}

			Thread.yield();
		}
	}

}
