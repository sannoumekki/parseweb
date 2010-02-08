/*
 * parsewebproject.actions
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

package parsewebproject.actions;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.swt.widgets.Shell;
import pw.common.CommonConstants;

public class ProgrammerInput extends InputDialog {
	
	public ProgrammerInput(Shell shell, String selectedText) {
		super(shell, "Input", "GiveInput", selectedText, null);
		CommonConstants.bStartAction = false;
	}
	
	
	public void buttonPressed(int buttonId) 
	{		
		if(buttonId == IDialogConstants.OK_ID)
		{
			String userQuery = getText().getText();
			
			if(userQuery == null || userQuery.equals(""))
				return;
			
			int indexOfArrow = userQuery.indexOf("->");
			if(indexOfArrow == -1)
			{
				CommonConstants.DestinationObject = userQuery.trim();
				CommonConstants.sourceObject = "";
			}
			else
			{
				CommonConstants.sourceObject = userQuery.substring(0, indexOfArrow).trim();
				CommonConstants.DestinationObject = userQuery.substring(indexOfArrow + 2, userQuery.length()).trim();
			}	
					
			CommonConstants.bStartAction = true;
		}
		super.buttonPressed(buttonId);
	}
}
