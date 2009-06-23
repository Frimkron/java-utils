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

import java.util.*;
import java.lang.reflect.*;
import uk.co.markfrimston.utils.*;

public class Matrix
{
	private int rows;
	private int cols;
	private double[][] data;
	
	public Matrix(double[][] data)
	{
		this.data = data;
		this.rows = data.length;
		this.cols = data[0].length;
	}
	
	public int getRows()
	{
		return rows;
	}
	
	public int getCols()
	{
		return cols;
	}
	
	public double get(int i,int j)
	{
		return data[i][j];
	}
	
	public Matrix add(Matrix oth)
	{
		if(this.rows != oth.rows || this.cols != oth.cols){
			throw new ArrayIndexOutOfBoundsException("Matrices must be same size");
		}
		
		double[][] result = new double[this.rows][this.cols];
		for(int i=0; i<this.rows; i++)
		{
			for(int j=0; j<this.cols; j++)
			{
				result[i][j] = this.data[i][j]+oth.data[i][j];
			}
		}
		return new Matrix(result);
	}
	
	public Matrix mult(double scalar)
	{
		double[][] result = new double[this.rows][this.cols];
		for(int i=0; i<this.rows; i++)
		{
			for(int j=0; j<this.cols; j++)
			{
				result[i][j] = this.data[i][j] * scalar;
			}
		}
		return new Matrix(result);
	}
	
	public Matrix transpose()
	{
		double[][] result = new double[this.cols][this.rows];
		for(int i=0; i<this.rows; i++)
		{
			for(int j=0; j<this.cols; j++)
			{
				result[i][j] = this.data[j][i];
			}
		}
		return new Matrix(result);
	}
	
	public Matrix mult(Matrix oth)
	{
		if(this.cols != oth.rows){
			throw new ArrayIndexOutOfBoundsException("Cols in first matrix must equal rows in second");
		}
		
		double[][] result = new double[this.rows][oth.cols];
		for(int i=0; i<this.rows; i++)
		{
			for(int j=0; j<oth.cols; j++)
			{
				double val = 0;
				for(int k=0; k<this.cols; k++)
				{
					val += this.data[i][k] * oth.data[k][j];
				}
				result[i][j] = val;
			}
		}		
		return new Matrix(result);
	}
	
	public boolean equals(Object obj)
	{
		if(obj==null) return false;
		if(!obj.getClass().equals(this.getClass())) return false;
		
		Matrix m = (Matrix)obj;
		if((this.data==null) != (m.data==null)) return false;
		
		if(this.data!=null)
		{
			if(data.length != m.data.length) return false;
			if(data.length > 0)
			{
				if(data[0].length != m.data[0].length) return false;
				for(int i=0; i<this.rows; i++)
				{
					if(!Arrays.equals(data[i], m.data[i])) return false; 
				}
			}
		}
		
		return true;
	}
	
	public int hashCode()
	{
		return ("matrix"+Arrays.hashCode(this.data)).hashCode();
	}
	
	public String toString()
	{
		StringBuffer sb = new StringBuffer();
		sb.append("\n");
		for(int i=0; i<this.rows; i++)
		{
			sb.append("[");
			for(int j=0; j<this.cols; j++)
			{
				sb.append(StringUtils.fixedLength(String.valueOf(data[i][j]), 4));
				sb.append(" ");
			}
			sb.append("]\n");
		}
		return sb.toString();
	}
}
