package com.carinov.commons;

import java.util.Vector;

public class PriorityQueue<T extends Comparable<T>>
{
	private Vector<T> queue = null;
	
	public PriorityQueue()
	{
		queue = new Vector<T>();
	}
	
	private int search(T item)
	{
		int index = 0;
		try
		{
			int n = queue.size() - 1;

			for (int low = 0,high = n; high >= low;)
			{
				index = (high + low)/2;
				int diff = queue.get(index).compareTo(item);
				if(diff > 0)
				{
					high = index - 1;
					if(high >= low)
						index = high;
				}
				else
				{
					if(diff < 0)
					{
						low = index + 1;
						if(high < low)
							index = low;
					}
					else
						break;
				}
			}
		}
		catch(Exception ex)
		{
			index = 0;
		}
		return index;
	}

	public synchronized T get(int index)
	{
		T item = null;
		if(!queue.isEmpty())
			item = queue.get(index);
		return item;
			
	}
	
	public synchronized void add(T item)
	{
		int index = search(item);
		if(index > -1)
			queue.add(index, item);
	}
	
	public synchronized T remove()
	{
		if(queue.isEmpty())
			return null;
		else
			return queue.remove(0);
	}
	
	public synchronized T removeAt(int index)
	{
		if(queue.isEmpty())
			return null;
		else
			return queue.remove(index);
	}
	
	public synchronized T peek()
	{
		if(queue.isEmpty())
			return null;
		else
			return queue.firstElement();
	}
	
	public synchronized int size()
	{
		return queue.size();
	}
}
