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

import java.util.HashSet;

import pw.common.CommonConstants;

/**
 * Class that stores the sequence path information. Used in mining process
 * @author suresh_thummalapenta
 *
 */
public class SequencePath  {

	
	String actualPath; //Variable holding the actual path sequence
	HashSet keySet;	   //HashSet holding the set of keys used for comparison later
	boolean confidence;	//Low confidence sequences are not used as a part of heuristics
	
	public SequencePath(String actualPath, HashSet keySet, boolean confidence)
	{
		this.actualPath = actualPath;
		this.keySet = keySet;
		this.confidence = confidence;
	}
	
	public boolean equals(Object arg)
	{
		if(!(arg instanceof SequencePath))
			return false;
		
		SequencePath spObj = (SequencePath) arg;
		
		/** Initially perfect matching is performed **/
		//If paths are exactly equal
		if(actualPath.equals(spObj.actualPath))
			return true;
	
		
		//For low confidence sequences, no additional checks are made
		if(!this.confidence || !spObj.confidence)
			return false;
		
		//If no heuristics are configured or sequences are of low confidence levels
		if(CommonConstants.methodSequenceClusterPrecision == 0)
			return false;
		
		//If one is a subset of other.
		if(this.keySet.containsAll(spObj.keySet) || spObj.keySet.containsAll(this.keySet))
			return true;

		
		/**Matching with heuristics **/
		//When CommonConstants.methodSequenceClusterPrecision = 1 -> "1 4 5 7" and "1 4 7 9" are treated as same
		HashSet interSectSet = (HashSet) this.keySet.clone();
		
		
		//Gather the intersection of both the sets
		interSectSet.retainAll(spObj.keySet);
		
		int intersectionCnt = interSectSet.size();
		int firstSetSize = this.keySet.size(); 
		int secSetSize = spObj.keySet.size();
		
		if(intersectionCnt > 0 && ((firstSetSize - intersectionCnt <= CommonConstants.methodSequenceClusterPrecision) 
				|| (secSetSize - intersectionCnt <= CommonConstants.methodSequenceClusterPrecision)))
			return true;
		else
		{
			//A special case where Intersection Count is zero, because both the sequences are of length one only
			//if(this.keySet.size() == 1 && spObj.keySet.size() == 1)
			//{
			//	ASTCrawler astc = ASTCrawler.getCurrentInstance();
			//	if(astc != null)
			//	{
			//		String seq1 = ((SequenceStore)astc.sequenceMap.get(this)).sequence;
			//		String seq2 = ((SequenceStore)astc.sequenceMap.get(spObj)).sequence;
			//		
			//		
			//		
			//	}
			//}
		}
		
		
		
		return false;
	}
	
	//HashCode value made constant to make sure that all elements are stored in the same list. Performance doesn't matter because
	//number of elements in this list as always near to 10 only.
	public int hashCode()
	{
		return 10;
	}
}
