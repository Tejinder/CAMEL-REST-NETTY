package vn.ngthphat.camel.processor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.component.netty4.http.NettyHttpMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.handler.codec.http.multipart.Attribute;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;
import io.netty.handler.codec.http.multipart.MixedAttribute;
import io.netty.handler.codec.http.multipart.MixedFileUpload;
import vn.ngthphat.camel.dom.ResponseStatus;
import vn.ngthphat.camel.dom.ResponseWrapper;

/**
 * Created by phatnguyen on 12/13/16.
 */
public class UploadProcessor implements Processor {

    private static final Logger LOGGER = LoggerFactory.getLogger(UploadProcessor.class);

    @Override
    public void process(Exchange exchange) throws Exception {
        // get netty message
        NettyHttpMessage nettyHttpMessage = exchange.getIn(NettyHttpMessage.class);
        // use HttpPostRequestDecoder to extract form data
        HttpPostRequestDecoder postRequest = new HttpPostRequestDecoder(nettyHttpMessage.getHttpRequest());
        getHttpDataAttributes(postRequest);
        // return status to client
        exchange.getIn().setBody(new ResponseWrapper(ResponseStatus.Success));
    }
    public void getHttpDataAttributes(HttpPostRequestDecoder request) {
        ResponseWrapper result = new ResponseWrapper();
        try {
            for (InterfaceHttpData part : request.getBodyHttpDatas()) {
                if (part instanceof MixedAttribute) {
                    Attribute attribute = (MixedAttribute) part;
                    LOGGER.info(String.format("Found part with 1st key: %s and value: %s ", attribute.getName(), attribute.getValue()));
                    copyFile(attribute.getFile().getAbsoluteFile());
                } else if (part instanceof MixedFileUpload) {
                    MixedFileUpload attribute = (MixedFileUpload) part;
                    LOGGER.info(String.format("Found part with 2nd key: %s and value: %s ", attribute.getName(), attribute.getFilename()));
                    copyFile(attribute.getFile().getAbsoluteFile());
                }
            }
        } catch (IOException e) {
            String errorMsg = String.format("Cannot parse request");
            result.setMessage(errorMsg);
            LOGGER.error(errorMsg,e);
        } finally {
            request.destroy();
        }
    }
    
    
    public void copyFile(File sourceFile) 
    {
    	try
    	{
	    	File dest = new File("E:\\APACHE-CAMEL");
	    	
	    	//File sourceFile = new File("C:\\Users\\Demo\\Downloads\\employee\\"+img);
	    	File destinationFile = new File("E:\\\\APACHE-CAMEL\\" + sourceFile.getName());
	
	    	FileInputStream fileInputStream = new FileInputStream(sourceFile);
	    	FileOutputStream fileOutputStream = new FileOutputStream(
	    	                destinationFile);
	
	    	int bufferSize;
	    	byte[] bufffer = new byte[512];
	    	while ((bufferSize = fileInputStream.read(bufffer)) > 0) {
	    	    fileOutputStream.write(bufffer, 0, bufferSize);
	    	}
	    	fileInputStream.close();
	    	fileOutputStream.close();
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}
    }
}
