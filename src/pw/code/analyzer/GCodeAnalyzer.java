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

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

import java.util.logging.Logger;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;

import pw.common.CommonConstants;


/**
 * Main class of the package responsible for collecting the files and invoking the
 * ASTCrawler for futher analysis
 * @author suresh_thummalapenta
 *
 */
public class GCodeAnalyzer {

	
	public static Logger logger = Logger.getLogger("GCodeAnalyzer");
	ASTCrawler astC = null;		//This is described as a member variable as often the previous instance of this class is used for next runs 
	
	//Constructur for processing in the specified order of files
	public GCodeAnalyzer()
	{
		
	}
	
	public TreeSet analyze(String directoryPath, String sourceObject, String destinationObject, List fileNameList, boolean bReUseOldInstance)
	{
		return invokeASTCrawler(directoryPath, fileNameList, sourceObject, destinationObject, bReUseOldInstance);
	}
	
	public TreeSet analyze(String directoryPath, String sourceObject, String destinationObject, boolean bReUseOldInstance)
	{
		List filesList = collect(directoryPath);
		return invokeASTCrawler(directoryPath, filesList, sourceObject, destinationObject, bReUseOldInstance);
	}
	
	/**
	 * Function for reading the files from the given path
	 */
	public List collect(String dirPath)
	{
        File inputFile = new File(dirPath);
        if (!inputFile.exists()) {
            throw new RuntimeException("File " + inputFile.getName() + " doesn't exist");
        }
        List<String> dataSources = new ArrayList<String>();
        if (!inputFile.isDirectory()) {
            //TODO: To give some error message
        } else {
        	String[] candidates = inputFile.list();
        	for (int i = 0; i < candidates.length; i++) {
        		if(candidates[i].endsWith(".java"))
        		{	
        			//File tmp = new File(inputFile + FILE_SEP + candidates[i]);
        			dataSources.add(candidates[i]);
        		}	
        	}    
        }
        return dataSources;

	}
	
	/**
	 * Function for invoking ASTCrawler on each file in the list
	 */
	public TreeSet invokeASTCrawler(String directoryPath, List filesList, String sourceObject, String destinationObject, boolean bReUseOldInstance)
	{
		//ASTParser parser = ASTParser.newParser(DEFAULT_LANGUAGE_SPECIFICATION);
		ASTParser parser= ASTParser.newParser(AST.JLS3);
		parser.setResolveBindings(true);
		parser.setStatementsRecovery(true);
		
		//ASTCrawler class
		if(!bReUseOldInstance || astC == null)
			astC = new ASTCrawler(directoryPath);
		astC.sourceObj = sourceObject;
		astC.destObj = destinationObject;
		//astC.setDirectoryName(directoryPath);
		
			
		for(Iterator iter = filesList.iterator(); iter.hasNext();)
		{
			try
			{
				String inputFileName = (String) iter.next();
				File inputFile = new File(directoryPath + CommonConstants.FILE_SEP + inputFileName);
				
				logger.info("Analyzing " + inputFile.getName());
				char[] source = new char[(int) inputFile.length()];
		        FileReader fileReader = new FileReader(inputFile);
		        fileReader.read(source);
		        fileReader.close();
				
				parser.setSource(source);
				
				
				
				CompilationUnit root;
				root= (CompilationUnit) parser.createAST(null);
				
				
				if ((root.getFlags() & CompilationUnit.MALFORMED) != 0) {
					logger.info("Error occurred while parsing the file: " + inputFile.getName());
		            continue;
		        }
				
				try
				{
					//This also clears the class specific data of the previous run
					astC.preProcessClass(root, inputFile.getName());	
					root.accept(astC);
					astC.closeDebugFile();
				}
				catch(Exception ex)
				{
					ex.printStackTrace();
				}
			}
			catch(Exception ex)
			{
				logger.info("Error occurred while reading the file " + ex);
				logger.info(ex.getStackTrace().toString());
			}
		}
		
		return astC.postProcess();
	}
	
	/**
	 * Main function that starts the execution.
	 * @param args
	 */
	public static void main(String args[])
	{
		try
		{
			new GCodeAnalyzer().analyze(args[0], args[1], args[2], false);
		}
		catch(Exception ex)
		{
			logger.info(ex.getStackTrace().toString());
		}
	}
	
	
}
