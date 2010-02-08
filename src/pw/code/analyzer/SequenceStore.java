/*
 * pw.code.analyzer
 * 
 * Copyright (c) 2007 - 2010 Suresh Thummalapenta
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. The name of the author may not be used to endorse or promote products
 *    derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package pw.code.analyzer;

import java.util.Comparator;
import java.util.HashSet;

/**
 * Class for preserving end sequence used for ranking
 */
public class SequenceStore implements Comparator 
{
	public int numOfTimes = 0;		//Number of times the sequence found in patterns
	public String sequence;		//Actual sequence of method calls that needs to be printed
	public int numOfMethodCalls = 0; //Number of method calls inside the sequence used for ranking criteria
	
	public int rank = 0;
	
	public String javaFileName;	//Java file name in which the sequence is found 
	public String methodName;		//Name of the method inside the java file, where the sequence is found
	
	String prevClassName = "";	//Previous class name in which this sequence is detected. This is used 
							//to prevent the incrementing of number of occurrences
	
	public int numUndefinedVars = 0; //Variable that stores number of variables whose sequences are not traced in the current method sequence
	
	public boolean confidenceLevel = true; //This is set to false in case the sequence found is not a fully confident one
	
	public String actualPath;
	public HashSet keySet;
	
	public void incrNumOfTimes()
	{
		numOfTimes = numOfTimes + 1;
	}

	public int compare(Object arg0, Object arg1) {
		// TODO Auto-generated method stub
		
		if(arg0 instanceof SequenceStore && arg1 instanceof SequenceStore)
		{
			SequenceStore ss1 = (SequenceStore) arg0;
			SequenceStore ss2 = (SequenceStore) arg1;
			
			//Sorting gives the ranking criteria used. The current sorting is in descending order. Using the following 
			//ranking criteria 
			
			//Confidence level gives the level of confidence of this sequence
			if(ss1.confidenceLevel == true && ss2.confidenceLevel == false)
				return -1;
			else if(ss1.confidenceLevel == false && ss2.confidenceLevel == true)
				return 1;
			
			
			//1. Number of times the pattern observed				
			if(ss1.numOfTimes > ss2.numOfTimes)
				return -1;
			else if(ss1.numOfTimes < ss2.numOfTimes)
				return 1;
			
			//2. Priority is given to shortest Jungloids
			if(ss1.numOfMethodCalls < ss2.numOfMethodCalls)
				return -1;
			else if(ss1.numOfMethodCalls > ss2.numOfMethodCalls)
				return 1;
			
			
			//3. More undefined variables in the pattern -> Less priority
			if(ss1.numUndefinedVars < ss2.numUndefinedVars)
				return -1;
			else if(ss1.numUndefinedVars > ss2.numUndefinedVars)
				return 1;
			
			
			//3. Higher rank to the method call having less package boundaries
			//TODO: To implement this
			
		}	
		
		return -1;
		
	}
}
