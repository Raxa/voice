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
    
    Copyright 2010 Yohann Martineau 
*/

package net.sourceforge.peers.media;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Properties;

import javax.sound.sampled.UnsupportedAudioFileException;

import raxa.ivr.core.utils.Receiver;
import raxa.ivr.core.utils.Sender;
import raxa.voice.utils.WavReader;



import net.sourceforge.peers.Logger;
import net.sourceforge.peers.rtp.RtpPacket;
import net.sourceforge.peers.rtp.RtpSession;
import net.sourceforge.peers.sdp.Codec;
import net.sourceforge.peers.sip.core.useragent.UserAgent;

public class MediaManager {

    public static final int DEFAULT_CLOCK = 8000; // Hz

    private UserAgent userAgent;
    private CaptureRtpSender captureRtpSender;
    private IncomingRtpReader incomingRtpReader;
    private RtpSession rtpSession;
    private DtmfFactory dtmfFactory;
    private Logger logger;

    private Receiver server;
    private int TTS_HOST_PORT;
    private String TTS_HOST_ADDRESS;    
    private int SAMPLE_RATE;
    private int SAMPLE_SIZE;
    
    private Sender client;
    private String ASR_HOST_ADDRESS;
    private int ASR_HOST_PORT;    
    
    
    private WavReader wavReader;
    

    public MediaManager(UserAgent userAgent, Logger logger) {
        this.userAgent = userAgent;
        this.logger = logger;
        dtmfFactory = new DtmfFactory();
        
        // Read properties files to configure audio servers settings
        Properties prop = new Properties();
        String configFile = "./conf/sip.config";
        try {
        	prop.load(new FileInputStream(configFile));
        	this.ASR_HOST_ADDRESS = prop.getProperty("ASR_HOST_ADDRESS");
        	this.ASR_HOST_PORT = Integer.valueOf(prop.getProperty("ASR_HOST_PORT"));
        	this.TTS_HOST_ADDRESS = prop.getProperty("TTS_HOST_ADDRESS");
        	this.TTS_HOST_PORT = Integer.valueOf(prop.getProperty("TTS_HOST_PORT"));
        	this.SAMPLE_RATE = Integer.valueOf(prop.getProperty("SAMPLE_RATE"));
        	this.SAMPLE_SIZE = Integer.valueOf(prop.getProperty("SAMPLE_SIZE"));
        	
        } catch (IOException ioe) {
        	logger.error(ioe.getMessage());
        	System.err.println("Error probably means that " + configFile + " is not present of one of \n " +
        			"\"ASR_HOST_ADDRESS\", \"ASR_HOST_PORT\"," +
        			"\"TTS_HOST_ADDRESS\" or \"TTS_HOST_PORT\" is specified incorrectly");
        }
        
        this.server = new Receiver(TTS_HOST_ADDRESS, TTS_HOST_PORT, SAMPLE_RATE, SAMPLE_SIZE);
        try {
			this.wavReader = new WavReader("./resource/music.wav");
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		server.start();
		System.out.println("[MediaManager] receiver started ... ");      
        try {
			this.client = new Sender(InetAddress.getByName(ASR_HOST_ADDRESS), ASR_HOST_PORT, "1", false);
			client.start();
			System.out.println("TCP client connected ...");
		} catch (IOException e) {
			System.out.println("TCP client could not started ...");
			e.printStackTrace();
		}
        
    }

    public void successResponseReceived(String localAddress,
            String remoteAddress, int remotePort, Codec codec) {
        switch (userAgent.getMediaMode()) {
        case captureAndPlayback:
            //TODO this could be optimized, create captureRtpSender at stack init
            //     and just retrieve it here
            SoundManager soundManager = userAgent.getSoundManager();
            soundManager.openAndStartLines();
            
            InetAddress inetAddress;
            try {
                inetAddress = InetAddress.getByName(localAddress);
            } catch (UnknownHostException e) {
                logger.error("unknown host: " + localAddress, e);
                return;
            }
            
            rtpSession = new RtpSession(inetAddress, userAgent.getRtpPort(),
                    userAgent.isMediaDebug(), logger, userAgent.getPeersHome());
            
            try {
                inetAddress = InetAddress.getByName(remoteAddress);
                rtpSession.setRemoteAddress(inetAddress);
            } catch (UnknownHostException e) {
                logger.error("unknown host: " + remoteAddress, e);
            }
            rtpSession.setRemotePort(remotePort);
            
            
            try {
                captureRtpSender = new CaptureRtpSender(rtpSession,
                        soundManager, userAgent.isMediaDebug(), codec, logger,
                        userAgent.getPeersHome(), server, wavReader);
            } catch (IOException e) {
                logger.error("input/output error", e);
                return;
            }

            try {
                captureRtpSender.start();
            } catch (IOException e) {
                logger.error("input/output error", e);
            }
            
            try {
                //TODO retrieve port from SDP offer
//                    incomingRtpReader = new IncomingRtpReader(localAddress,
//                            Utils.getInstance().getRtpPort(),
//                            remoteAddress, remotePort);
                incomingRtpReader = new IncomingRtpReader(
                        captureRtpSender.getRtpSession(), soundManager, codec,
                        logger, client);
            } catch (IOException e) {
                logger.error("input/output error", e);
                return;
            }

            incomingRtpReader.start();
            break;

        case echo:
            Echo echo;
            try {
                echo = new Echo(localAddress, userAgent.getRtpPort(),
                            remoteAddress, remotePort, logger);
            } catch (UnknownHostException e) {
                logger.error("unknown host amongst "
                        + localAddress + " or " + remoteAddress);
                return;
            }
            userAgent.setEcho(echo);
            Thread echoThread = new Thread(echo);
            echoThread.start();
            break;
        case none:
        default:
            break;
        }
    }

    public void handleAck(String destAddress, int destPort, Codec codec) {
        switch (userAgent.getMediaMode()) {
        case captureAndPlayback:
            //TODO this could be optimized, create captureRtpSender at stack init
            //     and just retrieve it here
            if (rtpSession != null) {
                rtpSession.stop();
                while (!rtpSession.isSocketClosed()) {
                    try {
                        Thread.sleep(15);
                    } catch (InterruptedException e) {
                        logger.debug("sleep interrupted");
                    }
                }
            }
            if (captureRtpSender != null) {
                captureRtpSender.stop();
            }
            SoundManager soundManager = userAgent.getSoundManager();
            soundManager.closeLines();
            soundManager.openAndStartLines();
            
            
            rtpSession = new RtpSession(userAgent.getConfig()
                    .getLocalInetAddress(), userAgent.getRtpPort(),
                    userAgent.isMediaDebug(), logger, userAgent.getPeersHome());
            
            try {
                InetAddress inetAddress = InetAddress.getByName(destAddress);
                rtpSession.setRemoteAddress(inetAddress);
            } catch (UnknownHostException e) {
                logger.error("unknown host: " + destAddress, e);
            }
            rtpSession.setRemotePort(destPort);
            
            try {
                captureRtpSender = new CaptureRtpSender(rtpSession,
                        soundManager, userAgent.isMediaDebug(), codec, logger,
                        userAgent.getPeersHome(), server, wavReader);
            } catch (IOException e) {
                logger.error("input/output error", e);
                return;
            }
            try {
                captureRtpSender.start();
            } catch (IOException e) {
                logger.error("input/output error", e);
            }
            try {
                //TODO retrieve port from SDP offer
//                        incomingRtpReader = new IncomingRtpReader(localAddress,
//                                Utils.getInstance().getRtpPort(),
//                                remoteAddress, remotePort);
                //FIXME RTP sessions can be different !
                incomingRtpReader = new IncomingRtpReader(rtpSession,
                        soundManager, codec, logger, client);
            } catch (IOException e) {
                logger.error("input/output error", e);
                return;
            }

            incomingRtpReader.start();
            break;
        case echo:
            Echo echo;
            try {
                echo = new Echo(userAgent.getConfig().getLocalInetAddress()
                            .getHostAddress(),
                        userAgent.getRtpPort(), destAddress, destPort, logger);
            } catch (UnknownHostException e) {
                logger.error("unknown host amongst "
                        + userAgent.getConfig().getLocalInetAddress()
                            .getHostAddress() + " or " + destAddress);
                return;
            }
            userAgent.setEcho(echo);
            Thread echoThread = new Thread(echo);
            echoThread.start();
            break;
        case none:
        default:
            break;
        }
    }

    public void sendDtmf(char digit) {
        if (captureRtpSender != null) {
            List<RtpPacket> rtpPackets = dtmfFactory.createDtmfPackets(digit);
            RtpSender rtpSender = captureRtpSender.getRtpSender();
            rtpSender.pushPackets(rtpPackets);
        }
    }

    public void stopSession() {
        if (rtpSession != null) {
            rtpSession.stop();
            while (!rtpSession.isSocketClosed()) {
                try {
                    Thread.sleep(15);
                } catch (InterruptedException e) {
                    logger.debug("sleep interrupted");
                }
            }
            rtpSession = null;
        }
        if (incomingRtpReader != null) {
            incomingRtpReader = null;
        }
        if (captureRtpSender != null) {
            captureRtpSender.stop();
            captureRtpSender = null;
        }
    }

}
