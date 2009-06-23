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

public class ThreeDUtils
{
	public static Matrix scale(Matrix model, double x, double y, double z)
	{
		double[][] transform = new double[][]{
				{x, 0, 0, 0},
				{0, y, 0, 0},
				{0, 0, z, 0},
				{0, 0, 0, 1}
		};	
		Matrix m = new Matrix(transform);
		return m.mult(model);
	}
	
	public static Matrix project(Matrix model, double focalLength)
	{
		double f = focalLength;
		
		double[][] transform = new double[][]{
				{1, 0, 0, 0},
				{0, 1, 0, 0},
				{0, 0, 1, 0},
				{0, 0, f, 0}
		};	
		Matrix m = new Matrix(transform);
		return m.mult(model);
	}
	
	public static Matrix translate(Matrix model, double x, double y, double z)
	{
		double[][] transform = new double[][]{
				{1, 0, 0, x},
				{0, 1, 0, y},
				{0, 0, 1, z},
				{0, 0, 0, 1}
		};	
		Matrix m = new Matrix(transform);
		return m.mult(model);
	}
	
	public static Matrix rotate(Matrix model, double x, double y, double z)
	{
		double[][] transform;
		transform = new double[][]{
				{1, 0, 				0, 				0},
				{0, Math.cos(x), 	-Math.sin(x), 	0},
				{0, Math.sin(x), 	Math.cos(x), 	0},
				{0, 0, 				0, 				1}
		};	
		Matrix mx = new Matrix(transform);
		transform = new double[][]{
				{Math.cos(y), 	0, Math.sin(y), 	0},
				{0, 			1, 0, 				0},
				{-Math.sin(y), 	0, Math.cos(y), 	0},
				{0, 			0, 0, 				1}
		};
		Matrix my = new Matrix(transform);
		transform = new double[][]{
				{Math.cos(z), 	-Math.sin(z), 	0, 0},
				{Math.sin(z), 	Math.cos(z), 	0, 0},
				{0, 			0, 				1, 0},
				{0, 			0, 				0, 1}
		};
		Matrix mz = new Matrix(transform);
		
		return mz.mult(my).mult(mx).mult(model);
	}
}
