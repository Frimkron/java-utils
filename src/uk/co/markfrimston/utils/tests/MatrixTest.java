package uk.co.markfrimston.utils.tests;

import org.junit.*;
import uk.co.markfrimston.utils.*;
import static org.junit.Assert.*;

public class MatrixTest
{
	private double[][] dataA, dataB, dataC;
	private Matrix a, b, c;
	
	@Test
	public void testEquals()
	{
		dataA = new double[][]{
				{0, 1},
				{2, 3}
		};
		a = new Matrix(dataA);
		
		dataB = new double[][]{
				{0, 1},
				{2, 3}
		};
		b = new Matrix(dataB);
		
		assertEquals(a, b);
	}
	
	@Test
	public void testAddition()
	{		
		dataA = new double[][]{
				{0, 1, 2},
				{2, 3, 4}
		};
		a = new Matrix(dataA);
		
		dataB = new double[][]{
				{4, 5, 6},
				{7, 8, 9}
		};
		b = new Matrix(dataB);
		
		dataC = new double[][]{
				{4, 6, 8},
				{9, 11,13}
		};
		c = new Matrix(dataC);
		
		assertEquals(c, a.add(b));
	}
	
	@Test
	public void testScalarMult()
	{
		dataA = new double[][]{
				{0, 2},
				{1, 3}
		};
		a = new Matrix(dataA);
		
		dataB = new double[][]{
				{0,4},
				{2,6}
		};
		b = new Matrix(dataB);
		
		assertEquals(b, a.mult(2.0));
	}
	
	@Test
	public void testMatrixMult()
	{
		dataA = new double[][]{
				{0, 2, 3},
				{1, 4, 5}
		};
		a = new Matrix(dataA);
		
		dataB = new double[][]{
				{1, 2},
				{0, 6},
				{3, 4}
		};
		b = new Matrix(dataB);
		
		/*
		 *		0*1+2*0+3*3		0*2+2*6+3*4
		 *		1*1+4*0+5*3		1*2+4*6+4*5 
		 */
		dataC = new double[][]{
				{9, 24},
				{16,46}
		};
		c = new Matrix(dataC);
		
		assertEquals(c, a.mult(b));
	}
}
