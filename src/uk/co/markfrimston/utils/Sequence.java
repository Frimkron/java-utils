/*
Copyright (c) 2008 Mark Frimston

Permission is hereby granted, free of charge, to any person
obtaining a copy of this software and associated documentation
files (the "Software"), to deal in the Software without
restriction, including without limitation the rights to use,
copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the
Software is furnished to do so, subject to the following
conditions:

The above copyright notice and this permission notice shall be
included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
OTHER DEALINGS IN THE SOFTWARE.
*/

package uk.co.markfrimston.utils;

import java.lang.reflect.Array;
import java.util.*;

public abstract class Sequence<T> implements Collection<T>
{		
	private static class IntArraySequence extends Sequence<Integer>
	{
		private int[] array;
		
		public IntArraySequence(int[] intArray)
		{
			this.array = intArray;
		}
		
		public Integer get(int i)
		{
			return array[i];
		}
		
		public int size()
		{
			return array.length;
		}

		public Sequence<Integer> append(Sequence<Integer> seq)
		{
			int[] out = new int[array.length+seq.size()];
			System.arraycopy(array, 0, out, 0, array.length);
			for(int i=0; i<seq.size(); i++)
			{
				out[array.length+i] = seq.get(i);
			}
			return Sequence.make(out);
		}

		public Sequence<Integer> subSequence(int beginIndex, int endIndex)
		{
			int[] out = new int[endIndex-beginIndex];
			System.arraycopy(array, beginIndex, out, 0, endIndex-beginIndex);
			return Sequence.make(out);
		}

		public Sequence<Integer> subSequence(int beginIndex)
		{
			return subSequence(beginIndex, this.size());
		}

		@Override
		public int hashCode()
		{
			return Arrays.hashCode(array);
		}
	}
	
	public static Sequence<Integer> make(final int[] intArray)
	{
		return new IntArraySequence(intArray);
	}
	
	private static class CharArraySequence extends Sequence<Character>
	{
		private char[] array;
		
		public CharArraySequence(char[] charArray)
		{
			this.array = charArray;
		}
		
		public Character get(int i)
		{
			return array[i];
		}
		
		public int size()
		{
			return array.length;
		}

		public Sequence<Character> append(Sequence<Character> seq)
		{
			char[] out = new char[array.length+seq.size()];
			System.arraycopy(array, 0, out, 0, array.length);
			for(int i=0; i<seq.size(); i++)
			{
				out[array.length+i] = seq.get(i);
			}
			return Sequence.make(out);
		}

		public Sequence<Character> subSequence(int beginIndex, int endIndex)
		{
			char[] out = new char[endIndex-beginIndex];
			System.arraycopy(array, beginIndex, out, 0, endIndex-beginIndex);
			return Sequence.make(out);
		}

		public Sequence<Character> subSequence(int beginIndex)
		{
			return subSequence(beginIndex, this.size());
		}	
		
		public int hashCode()
		{
			return Arrays.hashCode(array);
		}
	}
	
	public static Sequence<Character> make(char[] charArray)
	{
		return new CharArraySequence(charArray);
	}
	
	private static class ShortArraySequence extends Sequence<Short>
	{
		private short[] array;
		
		public ShortArraySequence(short[] shortArray)
		{
			this.array = shortArray;
		}
		
		public Short get(int i)
		{
			return array[i];
		}
		
		public int size()
		{
			return array.length;
		}
		
		public Sequence<Short> append(Sequence<Short> seq)
		{
			short[] out = new short[array.length+seq.size()];
			System.arraycopy(array, 0, out, 0, array.length);
			for(int i=0; i<seq.size(); i++)
			{
				out[array.length+i] = seq.get(i);
			}
			return Sequence.make(out);
		}

		public Sequence<Short> subSequence(int beginIndex, int endIndex)
		{
			short[] out = new short[endIndex-beginIndex];
			System.arraycopy(array, beginIndex, out, 0, endIndex-beginIndex);
			return Sequence.make(out);
		}

		public Sequence<Short> subSequence(int beginIndex)
		{
			return subSequence(beginIndex, this.size());
		}	
		
		public int hashCode()
		{
			return Arrays.hashCode(array);
		}
	}
	
	public static Sequence<Short> make(short[] shortArray)
	{
		return new ShortArraySequence(shortArray);
	}
	
	private static class ByteArraySequence extends Sequence<Byte>
	{
		private byte[] array;
		
		public ByteArraySequence(byte[] byteArray)
		{
			this.array = byteArray;
		}
		
		public Byte get(int i)
		{
			return array[i];
		}
		
		public int size()
		{
			return array.length;
		}
		
		public Sequence<Byte> append(Sequence<Byte> seq)
		{
			byte[] out = new byte[array.length+seq.size()];
			System.arraycopy(array, 0, out, 0, array.length);
			for(int i=0; i<seq.size(); i++)
			{
				out[array.length+i] = seq.get(i);
			}
			return Sequence.make(out);
		}

		public Sequence<Byte> subSequence(int beginIndex, int endIndex)
		{
			byte[] out = new byte[endIndex-beginIndex];
			System.arraycopy(array, beginIndex, out, 0, endIndex-beginIndex);
			return Sequence.make(out);
		}

		public Sequence<Byte> subSequence(int beginIndex)
		{
			return subSequence(beginIndex, this.size());
		}	
		
		public int hashCode()
		{
			return Arrays.hashCode(array);
		}
	}
	
	public static Sequence<Byte> make(byte[] byteArray)
	{
		return new ByteArraySequence(byteArray);
	}
	
	private static class LongArraySequence extends Sequence<Long>
	{
		private long[] array;
		
		public LongArraySequence(long[] longArray)
		{
			this.array = longArray;
		}
		
		public Long get(int i)
		{
			return array[i];
		}
		
		public int size()
		{
			return array.length;
		}
		
		public Sequence<Long> append(Sequence<Long> seq)
		{
			long[] out = new long[array.length+seq.size()];
			System.arraycopy(array, 0, out, 0, array.length);
			for(int i=0; i<seq.size(); i++)
			{
				out[array.length+i] = seq.get(i);
			}
			return Sequence.make(out);
		}

		public Sequence<Long> subSequence(int beginIndex, int endIndex)
		{
			long[] out = new long[endIndex-beginIndex];
			System.arraycopy(array, beginIndex, out, 0, endIndex-beginIndex);
			return Sequence.make(out);
		}

		public Sequence<Long> subSequence(int beginIndex)
		{
			return subSequence(beginIndex, this.size());
		}	
		
		public int hashCode()
		{
			return Arrays.hashCode(array);
		}
	}
	
	public static Sequence<Long> make(long[] longArray)
	{
		return new LongArraySequence(longArray);
	}
	
	private static class FloatArraySequence extends Sequence<Float>
	{
		private float[] array;
		
		public FloatArraySequence(float[] floatArray)
		{
			this.array = floatArray;
		}
		
		public Float get(int i)
		{
			return array[i];
		}
		
		public int size()
		{
			return array.length;
		}
		
		public Sequence<Float> append(Sequence<Float> seq)
		{
			float[] out = new float[array.length+seq.size()];
			System.arraycopy(array, 0, out, 0, array.length);
			for(int i=0; i<seq.size(); i++)
			{
				out[array.length+i] = seq.get(i);
			}
			return Sequence.make(out);
		}

		public Sequence<Float> subSequence(int beginIndex, int endIndex)
		{
			float[] out = new float[endIndex-beginIndex];
			System.arraycopy(array, beginIndex, out, 0, endIndex-beginIndex);
			return Sequence.make(out);
		}

		public Sequence<Float> subSequence(int beginIndex)
		{
			return subSequence(beginIndex, this.size());
		}	
		
		public int hashCode()
		{
			return Arrays.hashCode(array);
		}
	}
	
	public static Sequence<Float> make(float[] floatArray)
	{
		return new FloatArraySequence(floatArray);
	}
	
	private static class DoubleArraySequence extends Sequence<Double>
	{
		private double[] array;
		
		public DoubleArraySequence(double[] doubleArray)
		{
			this.array = doubleArray;
		}
		
		public Double get(int i)
		{
			return array[i];
		}
		
		public int size()
		{
			return array.length;
		}
		
		public Sequence<Double> append(Sequence<Double> seq)
		{
			double[] out = new double[array.length+seq.size()];
			System.arraycopy(array, 0, out, 0, array.length);
			for(int i=0; i<seq.size(); i++)
			{
				out[array.length+i] = seq.get(i);
			}
			return Sequence.make(out);
		}

		public Sequence<Double> subSequence(int beginIndex, int endIndex)
		{
			double[] out = new double[endIndex-beginIndex];
			System.arraycopy(array, beginIndex, out, 0, endIndex-beginIndex);
			return Sequence.make(out);
		}

		public Sequence<Double> subSequence(int beginIndex)
		{
			return subSequence(beginIndex, this.size());
		}	
		
		public int hashCode()
		{
			return Arrays.hashCode(array);
		}
	}
	
	public static Sequence<Double> make(double[] doubleArray)
	{
		return new DoubleArraySequence(doubleArray);
	}
	
	private static class BooleanArraySequence extends Sequence<Boolean>
	{
		private boolean[] array;
		
		public BooleanArraySequence(boolean[] booleanArray)
		{
			this.array = booleanArray;
		}
		
		public Boolean get(int i)
		{
			return array[i];
		}
		
		public int size()
		{
			return array.length;
		}
		
		public Sequence<Boolean> append(Sequence<Boolean> seq)
		{
			boolean[] out = new boolean[array.length+seq.size()];
			System.arraycopy(array, 0, out, 0, array.length);
			for(int i=0; i<seq.size(); i++)
			{
				out[array.length+i] = seq.get(i);
			}
			return Sequence.make(out);
		}

		public Sequence<Boolean> subSequence(int beginIndex, int endIndex)
		{
			boolean[] out = new boolean[endIndex-beginIndex];
			System.arraycopy(array, beginIndex, out, 0, endIndex-beginIndex);
			return Sequence.make(out);
		}

		public Sequence<Boolean> subSequence(int beginIndex)
		{
			return subSequence(beginIndex, this.size());
		}	
		
		public int hashCode()
		{
			return Arrays.hashCode(array);
		}
	}
	
	public static Sequence<Boolean> make(boolean[] booleanArray)
	{
		return new BooleanArraySequence(booleanArray);
	}
	
	private static class ObjectArraySequence extends Sequence<Object>
	{
		private Object[] array;
		
		public ObjectArraySequence(Object[] objectArray)
		{
			this.array = objectArray;
		}
		
		public Object get(int i)
		{
			return array[i];
		}
		
		public int size()
		{
			return array.length;
		}
		
		public Sequence<Object> append(Sequence<Object> seq)
		{
			Object[] out = new Object[array.length+seq.size()];
			System.arraycopy(array, 0, out, 0, array.length);
			for(int i=0; i<seq.size(); i++)
			{
				out[array.length+i] = seq.get(i);
			}
			return Sequence.make(out);
		}

		public Sequence<Object> subSequence(int beginIndex, int endIndex)
		{
			Object[] out = new Object[endIndex-beginIndex];
			System.arraycopy(array, beginIndex, out, 0, endIndex-beginIndex);
			return Sequence.make(out);
		}

		public Sequence<Object> subSequence(int beginIndex)
		{
			return subSequence(beginIndex, this.size());
		}	
		
		public int hashCode()
		{
			return Arrays.hashCode(array);
		}
	}
	
	public static Sequence<Object> make(Object[] objectArray)
	{
		return new ObjectArraySequence(objectArray);
	}
	
	private static class ListSequence<T> extends Sequence<T>
	{
		private List<T> list;
		
		public ListSequence(List<T> list)
		{
			this.list = list;
		}
		
		public T get(int i)
		{
			return list.get(i);
		}
		
		public int size()
		{
			return list.size();
		}
		
		public Sequence<T> append(Sequence<T> seq)
		{
			List<T> newList = new ArrayList<T>();
			newList.addAll(list);
			for(int i=0; i<seq.size(); i++)
			{
				newList.add(seq.get(i));
			}
			return Sequence.make(newList);
		}

		public Sequence<T> subSequence(int beginIndex, int endIndex)
		{
			return Sequence.make(list.subList(beginIndex, endIndex));
		}

		public Sequence<T> subSequence(int beginIndex)
		{
			return subSequence(beginIndex, size());
		}	
		
		public int hashCode()
		{
			return list.hashCode();
		}
	}
	
	public static <T> Sequence<T> make(List<T> list)
	{
		return new ListSequence<T>(list);
	}
	
	private static class StringSequence extends Sequence<Character>
	{
		private CharSequence chars;
		
		public StringSequence(CharSequence chars)
		{
			this.chars = chars;
		}
		
		public Character get(int i)
		{
			return chars.charAt(i);
		}
		
		public int size()
		{
			return chars.length();
		}
		
		public Sequence<Character> append(Sequence<Character> seq)
		{
			StringBuffer sb = new StringBuffer();
			sb.append(chars);
			for(int i=0; i<seq.size(); i++)
			{
				sb.append(seq.get(i));
			}
			return Sequence.make(sb.toString());
		}

		public Sequence<Character> subSequence(int beginIndex, int endIndex)
		{
			return Sequence.make(chars.subSequence(beginIndex, endIndex));
		}

		public Sequence<Character> subSequence(int beginIndex)
		{
			return subSequence(beginIndex, size());
		}	
		
		public int hashCode()
		{
			return chars.hashCode();
		}
	}
	
	public static Sequence<Character> make(CharSequence chars)
	{
		return new StringSequence(chars);
	}
	
	private static class SequenceIterator<T> implements Iterator<T>
	{
		private int pos = 0;
		private Sequence<T> seq;
		
		public SequenceIterator(Sequence<T> seq)
		{
			this.seq = seq;
		}
		
		public boolean hasNext()
		{
			return pos < seq.size();
		}

		public T next()
		{
			if(!hasNext()){
				throw new NoSuchElementException();
			}
			T item = seq.get(pos);
			pos++;
			return item;
		}

		public void remove()
		{
			throw new UnsupportedOperationException();
		}	
	}
	
	public static boolean isSequenceable(Object o)
	{
		if(o instanceof Object[] || o instanceof byte[] || o instanceof char[] 
		    || o instanceof short[] || o instanceof int[] || o instanceof long[]
		    || o instanceof float[] || o instanceof double[] || o instanceof List<?>
		    || o instanceof CharSequence)
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
	public static Sequence<?> make(Object o)
	{
		if(o instanceof Object[])
		{
			return Sequence.make((Object[])o);
		}
		else if(o instanceof byte[])
		{
			return Sequence.make((byte[])o);
		}
		else if(o instanceof short[])
		{
			return Sequence.make((short[])o);
		}
		else if(o instanceof char[])
		{
			return Sequence.make((char[])o);
		}
		else if(o instanceof int[])
		{
			return Sequence.make((int[])o);
		}
		else if(o instanceof long[])
		{
			return Sequence.make((long[])o);
		}
		else if(o instanceof float[])
		{
			return Sequence.make((float[])o);
		}
		else if(o instanceof double[])
		{
			return Sequence.make((double[])o);
		}
		else if(o instanceof List<?>)
		{
			return Sequence.make((List<?>)o);
		}
		else if(o instanceof CharSequence)
		{
			return Sequence.make((CharSequence)o);
		}
		else
		{
			return null;
		}
	}
	
	public abstract T get(int i);
	public abstract int size();
	public abstract Sequence<T> subSequence(int beginIndex);
	public abstract Sequence<T> subSequence(int beginIndex, int endIndex);
	public abstract Sequence<T> append(Sequence<T> seq);
	public abstract int hashCode();
	
	public boolean equals(Object obj)
	{
		if(obj==null){
			return false;
		}
		if(!(obj instanceof Sequence))
		{
			return false;
		}
		Sequence<?> seq = (Sequence<?>)obj;
		if(size() != seq.size()){
			return false;
		}
		for(int i=0; i<size(); i++)
		{
			if(!get(i).equals(seq.get(i))){
				return false;
			}
		}
		return true;
	}
	
	public String toString()
	{
		return "Seq["+StringUtils.implode(this)+"]";		
	}
	
	public Iterator<T> iterator()
	{
		return new SequenceIterator<T>(this);
	}
	
	public boolean add(T o)
	{
		throw new UnsupportedOperationException();
	}
	
	public boolean addAll(Collection<? extends T> c)
	{
		throw new UnsupportedOperationException();
	}
	
	public void clear()
	{
		throw new UnsupportedOperationException();
	}
	
	public boolean isEmpty()
	{
		return size()==0;
	}
	
	public boolean remove(Object o)
	{
		throw new UnsupportedOperationException();
	}
	
	public boolean removeAll(Collection<?> c)
	{
		throw new UnsupportedOperationException();
	}
	
	public boolean retainAll(Collection<?> c)
	{
		throw new UnsupportedOperationException();
	}
	
	public Object[] toArray()
	{
		Object[] newArray = new Object[size()];
		for(int i=0; i<size(); i++)
		{
			newArray[i] = get(i);
		}
		return newArray;
	}
	
	public <X> X[] toArray(X[] array)
	{
		X[] newArray = (X[])Array.newInstance(array.getClass().getComponentType(), size());
		for(int i=0; i<size(); i++)
		{
			newArray[i] = (X)get(i);
		}
		return newArray;
	}
	
	public boolean contains(Object o)
	{
		for(Object i : this)
		{
			if(o.equals(i)){
				return true;
			}
		}
		return false;
	}
	
	public boolean containsAll(Collection<?> c)
	{
		for(Object i : c)
		{
			if(!contains(i)){
				return false;
			}
		}
		return true;
	}
	
	public static void main(String[] args)
	{
		Sequence<Integer> seq = Sequence.make(new int[]{1,2,3,4,5});
		System.out.println(seq.subSequence(0, 2).append(seq.subSequence(3,5)));
	}
}
