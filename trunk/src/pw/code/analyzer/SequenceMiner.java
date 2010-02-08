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

import java.util.HashMap;
import java.util.HashSet;

/**
 * Miner class that mines several final sequences for gathering final set
 * Heuristics of this class are available in SequencePath
 * @author suresh_thummalapenta
 *
 */
public class SequenceMiner {
	
	public static void storeSequence(String path, String sequence, int numOfMethodCalls, 
			int numOfUndefinedVars, HashSet keySet, boolean confidence, HashMap sequenceMap,
			String currentFileName, String currentMethodName)
	{
		SequencePath spObj = new SequencePath(path, keySet, confidence);
		SequenceStore ssStore = (SequenceStore) sequenceMap.get(spObj);
		if(ssStore == null)
		{
			ssStore = new SequenceStore();
			ssStore.sequence = sequence;
			ssStore.incrNumOfTimes();
			ssStore.javaFileName = currentFileName;
			ssStore.methodName = currentMethodName; 
			ssStore.numOfMethodCalls = numOfMethodCalls;
			ssStore.prevClassName = currentFileName;
			ssStore.numUndefinedVars = numOfUndefinedVars;
			ssStore.confidenceLevel = confidence;
			ssStore.actualPath = path;
			ssStore.keySet = keySet;
			sequenceMap.put(spObj, ssStore);
		}
		else
		{
			//Heuristics are applied while matching current sequence with prior sequences.
			//Increment this only when new sequence is found in a new file. If not the same sequence can be given
			//high priority due to method inlining
			if(!currentFileName.equals(ssStore.prevClassName))
			{	
				ssStore.incrNumOfTimes();
				ssStore.prevClassName = currentFileName;
			}
			
		
			if(ssStore.numOfMethodCalls > numOfMethodCalls)
			{
				//Current sequence is a sub set of old sequence. Give preference to smaller sequence
				
				//Before updating make sure that current sequence has no UNKNOWN
				//because a longer sequence with no UNKNOWNS is better than a shorter with UNKNOWNS
				if(sequence.indexOf("#UNKNOWN#") == -1)
				{
					ssStore.sequence = sequence;
					ssStore.javaFileName = currentFileName;
					ssStore.methodName = currentMethodName;
					ssStore.numOfMethodCalls = numOfMethodCalls;
					ssStore.prevClassName = currentFileName;
					ssStore.numUndefinedVars = numOfUndefinedVars;
					ssStore.actualPath = path;
					ssStore.keySet = keySet;
					
					//Update key also
					sequenceMap.remove(spObj);
					SequencePath spObj1 = new SequencePath(path, keySet, confidence);
					sequenceMap.put(spObj1, ssStore);
				}
			}
		}
	}

}
