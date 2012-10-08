/*
    This file is part of Peers, a java SIP softphone.

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
    
    Copyright 2012 Raxa 
*/

package raxa.ivr.sip.server;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.SocketException;


import net.sourceforge.peers.sip.RFC3261;
import net.sourceforge.peers.sip.Utils;
import net.sourceforge.peers.sip.core.useragent.SipListener;
import net.sourceforge.peers.sip.core.useragent.UserAgent;
import net.sourceforge.peers.sip.syntaxencoding.SipHeaderFieldName;
import net.sourceforge.peers.sip.syntaxencoding.SipHeaderFieldValue;
import net.sourceforge.peers.sip.syntaxencoding.SipHeaders;
import net.sourceforge.peers.sip.syntaxencoding.SipUriSyntaxException;
import net.sourceforge.peers.sip.transactionuser.Dialog;
import net.sourceforge.peers.sip.transactionuser.DialogManager;
import net.sourceforge.peers.sip.transport.SipRequest;
import net.sourceforge.peers.sip.transport.SipResponse;

public class SipServer implements SipListener
{
    private UserAgent userAgent;
	public static void main( String[] args ) throws IOException
    {        
        System.out.println("Starting Raxa SIP Server ...");
		new SipServer();
    }

    public SipServer() {
        try {
            userAgent = new UserAgent(this, ".", null);
            // start a new call
            userAgent.getUac().register(); 	// register this as a SIP phone to 
            								// your SIP provider            
            //userAgent.getUac().invite("sip:14124822427@127.0.0.1", null);
            //userAgent.getUac().register();
            
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (SipUriSyntaxException e) {
            e.printStackTrace();
        }        
    }

    @Override public void registering(SipRequest sipRequest) { }
    @Override public void registerSuccessful(SipResponse sipResponse) { }
    @Override public void registerFailed(SipResponse sipResponse) { }
    @Override 
    public void incomingCall(SipRequest sipRequest, SipResponse provResponse) { 
    	SipHeaders sipHeaders = sipRequest.getSipHeaders();
        SipHeaderFieldName sipHeaderFieldName =
            new SipHeaderFieldName(RFC3261.HDR_FROM);
        SipHeaderFieldValue fromHDR = sipHeaders.get(sipHeaderFieldName);
        final String callerID = fromHDR.getValue();
        System.out.println("Caller:" + callerID);
        
        String callId = Utils.getMessageCallId(sipRequest);
        DialogManager dialogManager = userAgent.getDialogManager();
        Dialog dialog = dialogManager.getDialog(callId);
        userAgent.getUas().acceptCall(sipRequest, dialog);    
        
    }
    @Override public void remoteHangup(SipRequest sipRequest) { }
    @Override public void ringing(SipResponse sipResponse) { }
    @Override public void calleePickup(SipResponse sipResponse) { }
    @Override public void error(SipResponse sipResponse) { }
}
