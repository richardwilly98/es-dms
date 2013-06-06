package com.github.richardwilly98.esdms;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.github.richardwilly98.esdms.api.SearchResult;
import com.google.common.base.Objects;

public class SearchResultImpl<T> implements SearchResult<T> {

	
	private long milliseconds;	
	private Set<T> resultItems;	
	
	public void sort(Comparator<T> c){
		List<T> list = new ArrayList<T>(this.resultItems);
		Collections.sort(list, c);
		this.resultItems.clear();
		this.resultItems.addAll(list);
	}
	
	public void setElapsedTime(long milliseconds)
	{
		this.milliseconds = milliseconds;
	}
	
	public long getElapsedTime(){
		return this.milliseconds;
	}
	
	public int size(){
		if (this.resultItems == null) this.resultItems = new LinkedHashSet<T>();
		return resultItems.size();
	}
	
	public boolean isEmpty(){
		if (this.resultItems == null) this.resultItems = new LinkedHashSet<T>();
		return this.resultItems.isEmpty();
	}
	
	public boolean add(T element){
		if (this.resultItems == null) this.resultItems = new LinkedHashSet<T>();
		return this.resultItems.add(element);
	}
	
	public boolean remove(T element){
		if (this.resultItems == null) this.resultItems = new LinkedHashSet<T>();
		return this.resultItems.remove(element);
	}
	
	public Iterator<T> iterator(){
		if (this.resultItems == null) this.resultItems = new LinkedHashSet<T>();
		return this.resultItems.iterator();
	}
	
	public boolean contains(T element){
		if (this.resultItems == null) this.resultItems = new LinkedHashSet<T>();
		return this.resultItems.contains(element);
	}
	
	public boolean containsAll(Collection<T> c){
		if (this.resultItems == null) this.resultItems = new LinkedHashSet<T>();
		return this.resultItems.containsAll(c);
	}
	
	public boolean addAll(Collection<T> c){
		if (this.resultItems == null) this.resultItems = new LinkedHashSet<T>();
		return this.resultItems.addAll(c);
	}
		
	public boolean removeAll(Collection<T> c){
		if (this.resultItems == null) this.resultItems = new LinkedHashSet<T>();
		return this.resultItems.removeAll(c);
	}
	
	public boolean retainAll(Collection<T> c){
		if (this.resultItems == null) this.resultItems = new LinkedHashSet<T>();
		return this.resultItems.retainAll(c);
	}
	
	public void clear(){
		if (this.resultItems == null) this.resultItems = new LinkedHashSet<T>();
		this.resultItems.clear();
	}
	
	public Object[] toArray(){
		if (this.resultItems == null) this.resultItems = new LinkedHashSet<T>();
		return this.resultItems.toArray();
	}
	
	public T[] toArray(T[] a){
		if (this.resultItems == null) this.resultItems = new LinkedHashSet<T>();
		return this.resultItems.toArray(a);
	}
	
	public String toString(){
		return Objects.toStringHelper(this.resultItems).add("milliseconds", this.milliseconds).toString();
	}
	
	
}