package org.raxa.audioplayer;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import com.google.gson.Gson;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;


public class WitSpeech {
	 private final String WIT_SPEECH_URL = "https://api.wit.ai/speech";
	    private final String VERSION = "20140501";
	    private final String AUTHORIZATION_HEADER = "Authorization";
	    private final String ACCEPT_HEADER = "Accept";
	    private final String CONTENT_TYPE_HEADER = "Content-Type";
	    private final String TRANSFER_ENCODING_HEADER = "Transfer-Encoding";
	    private final String ACCEPT_VERSION = "application/vnd.wit." + VERSION;
	    private final String BEARER_FORMAT = "Bearer %s";
	    private String _accessToken;
	    private String _contentType;
		private Logger logger = Logger.getLogger(this.getClass());
	    public WitSpeech(String accessToken, String contentType) {
	        _accessToken = accessToken;
	        _contentType = contentType;
	    }
	    
	    
public String getResponse(InputStream... speech){
	  String response = null;
      try {
          logger.info("Wit requesting SPEECH ...." + _contentType);
          URL url = new URL(WIT_SPEECH_URL);
          logger.info("Wit posting speech to " + WIT_SPEECH_URL);
          HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
          urlConnection.setDoOutput(true);
          urlConnection.setRequestProperty(AUTHORIZATION_HEADER, String.format(BEARER_FORMAT, _accessToken));
          urlConnection.setRequestProperty(ACCEPT_HEADER, ACCEPT_VERSION);
          urlConnection.setRequestProperty(CONTENT_TYPE_HEADER, _contentType);
          urlConnection.setRequestProperty(TRANSFER_ENCODING_HEADER, "chunked");
          urlConnection.setChunkedStreamingMode(0);

          try {
              OutputStream out = urlConnection.getOutputStream();
              int n;
              byte[] buffer = new byte[1024];
              while((n = speech[0].read(buffer)) > -1) {
                  out.write(buffer, 0, n);   // Don't allow any extra bytes to creep in, final write
              }
              out.close();
              logger.info("Wit done sending data");
              int statusCode = urlConnection.getResponseCode();
              InputStream in;
              if (statusCode != 200) {
                  in = urlConnection.getErrorStream();
              }
              else {
                  in = new BufferedInputStream(urlConnection.getInputStream());
              }
              response = IOUtils.toString(in);
              in.close();
          } finally {
              urlConnection.disconnect();
          }
      } catch (Exception e) {
    	  logger.info("Wit An error occurred during the request, did you set your token correctly? Is the content-type correct ?", e);
      }
      return response;	
}

public WitResponse processWitResponse(String result) {
    WitResponse response = null;
    Error errorDuringRecognition = null;
    logger.info("Wit : Response " + result);
    try {
        Gson gson = new Gson();
        response = gson.fromJson(result, WitResponse.class);
        logger.info("Gson : Response " + gson.toJson(response));
    } catch (Exception e) {
    	logger.info("Wit : Error " + e.getMessage());
        errorDuringRecognition = new Error(e.getMessage());
    }
   return response;
}

}
