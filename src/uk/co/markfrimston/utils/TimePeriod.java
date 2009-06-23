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

public class TimePeriod
{
	public static enum Unit
	{
		MILLISECOND("ms", 1, null),
		SECOND("s", 1000, MILLISECOND),
		MINUTE("min", 60, SECOND),
		HOUR("h", 60, MINUTE),
		DAY(" days", 24, HOUR),
		WEEK(" weeks", 7, DAY),
		YEAR(" years", 52.178571428571428571428571428571, WEEK);
		
		private long millis;
		private Unit subUnit;
		private double numSubs;
		private String postfix;
		
		private Unit(String postfix, double numSubs, Unit subUnit)
		{
			this.postfix = postfix;
			this.numSubs = numSubs;
			this.subUnit = subUnit;
			if(subUnit!=null)
			{
				this.millis = (long)(subUnit.millis * numSubs);
			}
			else
			{
				this.millis = 1;
			}
		}
	}
	
	private static final Unit TOP_UNIT = Unit.YEAR;
	
	private long period;
	
	public TimePeriod(long millis)
	{
		period = millis;
	}
	
	public TimePeriod(Unit unit, double amount)
	{
		period = (long)(unit.millis * amount);
	}
	
	public static double millisTo(long millis, Unit unit)
	{
		return (double)millis / unit.millis;
	}
	
	public double getIn(Unit unit)
	{
		return millisTo(period,unit);  
	}
	
	public static long millisToWhole(long millis, Unit unit)
	{
		return millis / unit.millis;
	}
	
	public long getWholeIn(Unit unit)
	{
		return millisToWhole(period,unit);
	}
	
	/*
	public static Pair<Long,Double> millisToWholeAndFraction(long millis, Unit unit)
	{
		Pair<Long,Double> r = new Pair<Long,Double>();
		r.a = millis / unit.millis;
		r.b = (double)(millis % unit.millis)/unit.millis;
		return r;
	}
	
	public Pair<Long,Double> getWholeAndFractionIn(Unit unit)
	{
		return millisToWholeAndFraction(period, unit);
	}
	*/
	
	public static String millisToPrettyString(long millis)
	{
		StringBuffer sb = new StringBuffer();
		Unit unit = TOP_UNIT;
		long amount = millis;
		do
		{
			double numOf = millisTo(amount, unit);
			long wholeOf = (long)Math.floor(numOf);
			if(wholeOf > 0)
			{
				sb.append(wholeOf+unit.postfix+" ");
			}					
			amount = (long)((numOf-wholeOf) * unit.millis);
			unit = unit.subUnit;
		}
		while(unit!=null);
		
		return sb.toString();
	}
	
	public String toString()
	{
		return millisToPrettyString(period);
	}
	
	public static void main(String[] args)
	{
		TimePeriod p = new TimePeriod(TimePeriod.Unit.YEAR, 5.5);
		System.out.println(p.toString());
	}
}
