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
import java.io.*;
import uk.co.markfrimston.utils.*;
import java.util.regex.*;

public class WireframeModel
{	
	private static final double[][] CUBE_POINTS = {
		{-1, -1, -1},
		{ 1, -1, -1},
		{ 1,  1, -1},
		{-1,  1, -1},
		{-1, -1,  1},
		{ 1, -1,  1},
		{ 1,  1,  1},
		{-1,  1,  1}
	};
	private static final int[][] CUBE_LINES = {
		{0,1},{1,2},{2,3},{3,0},
		{0,4},{1,5},{2,6},{3,7},
		{4,5},{5,6},{6,7},{7,4}
	};
	public static final WireframeModel CUBE = new WireframeModel(CUBE_POINTS,CUBE_LINES);
	
	private static final double[][] TUBE_POINTS = {
		{-0.75,    0,-1.5},
		{-0.53,-0.53,-1.5},
		{    0,-0.75,-1.5},
		{ 0.53,-0.53,-1.5},
		{ 0.75,    0,-1.5},
		{ 0.53, 0.53,-1.5},
		{    0, 0.75,-1.5},
		{-0.53, 0.53,-1.5},
		{-0.75,    0, 1.5},
		{-0.53,-0.53, 1.5},
		{    0,-0.75, 1.5},
		{ 0.53,-0.53, 1.5},
		{ 0.75,    0, 1.5},
		{ 0.53, 0.53, 1.5},
		{    0, 0.75, 1.5},
		{-0.53, 0.53, 1.5},
	};
	private static final int[][] TUBE_LINES = {
		{0,1},{1,2},{2,3},{3,4},{4,5},{5,6},{6,7},{7,0},
		{0,8},{1,9},{2,10},{3,11},{4,12},{5,13},{6,14},{7,15},
		{8,9},{9,10},{10,11},{11,12},{12,13},{13,14},{14,15},{15,8}
	};
	public static final WireframeModel TUBE = new WireframeModel(TUBE_POINTS,TUBE_LINES);
	
	private static final double[][] CONE_POINTS = {
		{   0,    0,   1},
		{  -1,    0,  -1},
		{-0.7, -0.7,  -1},
		{   0,   -1,  -1},
		{ 0.7, -0.7,  -1},
		{   1,    0,  -1},
		{ 0.7,  0.7,  -1},
		{   0,    1,  -1},
		{-0.7,  0.7,  -1},
	};
	private static final int[][] CONE_LINES = {
		{1,2},{2,3},{3,4},{4,5},{5,6},{6,7},{7,8},{8,1},
		{0,1},{0,2},{0,3},{0,4},{0,5},{0,6},{0,7},{0,8}
	};
	public static final WireframeModel CONE = new WireframeModel(CONE_POINTS, CONE_LINES); 
	
	private double[][] points;
	private int[][] lines;
	private Matrix matrix;
	private boolean dirty = true;
	
	public WireframeModel()
	{
		
	}
	
	public WireframeModel(double[][] points, int[][] lines)
	{
		this.points = points;
		this.lines = lines;
	}

	private void update()
	{
		double[][] data = new double[4][points.length];
		for(int i=0; i<points.length; i++)
		{
			for(int j=0; j<3; j++)
			{
				data[j][i] = points[i][j];
			}
			data[3][i] = 1;
		}
		this.matrix = new Matrix(data);
		this.dirty = false;
	}
	
	public double[][] getPoints()
	{
		return points;
	}

	public void setPoints(double[][] points)
	{
		this.points = points;
		dirty = true;
	}

	public int[][] getLines()
	{
		return lines;
	}

	public void setLines(int[][] lines)
	{
		this.lines = lines;
		dirty = true;
	}
	
	public int getNumLines()
	{
		return lines.length;
	}
	
	public int[] getLine(int number)
	{
		return lines[number];
	}
	
	public int getNumPoints()
	{
		return points.length;
	}
	
	public Matrix getMatrix()
	{
		if(dirty) update();
		return matrix;
	}
	
	public boolean equals(Object oth)
	{
		if(oth==null) return false;
		if(!oth.getClass().equals(this.getClass())) return false;
		
		WireframeModel mod = (WireframeModel)oth;
		if((this.points==null)!=(mod.points==null)) return false;
		if(this.points!=null && this.points.length!=mod.points.length) return false;
		if(this.points.length > 0)
		{
			for(int i=0; i<this.points.length; i++)
			{
				if(!Arrays.equals(this.points[i], mod.points[i])) return false;
			}
		}
		if((this.lines==null)!=(mod.lines==null)) return false;
		if(this.lines!=null && this.lines.length!=mod.lines.length) return false;
		if(this.lines.length > 0)
		{
			for(int i=0; i<this.lines.length; i++)
			{
				if(!Arrays.equals(this.lines[i], mod.lines[i])) return false;
			}
		}
		
		return true;
	}
	
	public int hashCode()
	{
		return ("model"+String.valueOf(Arrays.hashCode(points))
				+String.valueOf(Arrays.hashCode(lines))).hashCode();
	}
	
	public void loadObj(InputStream input) 
		throws IOException, ModelFormatException
	{
		loadObj(input, 1.0);
	}
	
	public void loadObj(InputStream input, double scaling)
		throws IOException, ModelFormatException
	{
		Pattern faceVertexPattern = Pattern.compile("(\\d+)(?:/(\\d*)(?:/(\\d*))?)?");
		
		BufferedReader reader = null;
		try
		{
			List<Trio<Double,Double,Double>> points 
				= new ArrayList<Trio<Double,Double,Double>>();
			List<Pair<Integer,Integer>> lines
				= new ArrayList<Pair<Integer,Integer>>();
			
			reader = new BufferedReader(new InputStreamReader(input));
			String line = null;
			while((line=reader.readLine())!=null)
			{
				String[] tokens = line.split("\\s");
				if(tokens.length>0)
				{
					if(tokens[0].equals("v"))
					{
						//vertex with x,y,z 
						if(tokens.length != 4){
							throw new ModelFormatException("Expected 3 values for vertex");
						}
						double x,y,z;
						try
						{
							x = Double.parseDouble(tokens[1]) * scaling;
							y = Double.parseDouble(tokens[2]) * scaling;
							z = Double.parseDouble(tokens[3]) * scaling;
						}
						catch(NumberFormatException e)
						{
							throw new ModelFormatException("Bad coordinate value for vertex",e);
						}
						points.add(new Trio<Double,Double,Double>(x,y,z));
					}
					else if(tokens[0].equals("f"))
					{
						//face with list of vertices
						int firstVertex = -1;
						int prevVertex = -1;
						for(int i=1; i<tokens.length; i++)
						{
							String vertex = tokens[i];
							Matcher m = faceVertexPattern.matcher(vertex);
							if(!m.matches()){
								throw new ModelFormatException("Bad vertex format for face");
							}
							int vertexIndex = -1;
							try
							{
								vertexIndex = Integer.parseInt(m.group(1));
							}
							catch(NumberFormatException e)
							{
								throw new ModelFormatException("Bad vertex index for face",e);
							}
							if(firstVertex == -1)
							{
								firstVertex = vertexIndex;
							}
							else
							{
								lines.add(new Pair<Integer,Integer>(prevVertex-1,vertexIndex-1));
								if(i==tokens.length-1)
								{
									lines.add(new Pair<Integer,Integer>(vertexIndex-1,firstVertex-1));
								}
							}
							prevVertex= vertexIndex;
						}
					}
				}
			}		
			
			this.points = new double[points.size()][4];
			for(int i=0; i<points.size(); i++)
			{
				Trio<Double,Double,Double> p = points.get(i);
				this.points[i][0] = p.a;
				this.points[i][1] = p.b;
				this.points[i][2] = p.c;
			}
			
			this.lines = new int[lines.size()][2];
			for(int i=0; i<lines.size(); i++)
			{
				Pair<Integer,Integer> l = lines.get(i);
				this.lines[i][0] = l.a;
				this.lines[i][1] = l.b;
			}
			
			dirty = true;
		}
		finally
		{
			try{
				reader.close();
			}catch(Exception e){}
		}		
	}
}
