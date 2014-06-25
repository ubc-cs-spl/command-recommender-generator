/**
 * 
 */
package ca.ubc.cs.commandrecommender.model.cf;

import java.util.Iterator;
import java.util.Map;


/**
 * Iterates over the array of values in a map.
 * 
 * Could also be implemented as a CompositeVisitor
 */
//TODO: check over
public class MappedArrayIterator<Key,Value> implements Iterable<Value> {

	private final Map<Key, Value[]> map;
	
	public MappedArrayIterator(Map<Key, Value[]> map) {
		this.map = map;
	}

	@Override
	public Iterator<Value> iterator() {
		return new Iterator<Value>(){

			Iterator<Key> keyIterator = map.keySet().iterator();
			Iterator<Value> valueIterator = new Iterator<Value>(){
				public boolean hasNext() {return false;}
				public Value next() {return null;}
				public void remove() {}
			};
			
			@Override
			public boolean hasNext() {
				
				if(valueIterator.hasNext()){
					return true;
				
				}else if(keyIterator.hasNext()){
					Key p = keyIterator.next();
					valueIterator = new ArrayIterator<Value>(map.get(p));
					return hasNext();
				}else{
					return false;	
				}
			}

			@Override
			public Value next() {
				return valueIterator.next();
			}

			@Override
			public void remove() {}
		};
	}
}