package com.ragna;

import com.mongodb.BasicDBObject;
import com.mongodb.util.JSON;

import java.util.List;
import java.util.Map;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.commons.io.IOUtils;

/**
 * Created by ramsescarbajal on 02/10/16.
 */
public class SaveMongo implements Processor {

    @SuppressWarnings("rawtypes")
    @Override
    public void process( Exchange exchange ) throws Exception {
        Message message = exchange.getIn();
        String plugin = message.getHeader( "x-plugin", String.class );
        String config = IOUtils.toString( getClass().getResourceAsStream( "/testFiles/saveResponse.json" ) );
        Object o = JSON.parse((String)config);
        BasicDBObject jsonList = (BasicDBObject)o ;

        message.setBody( jsonList );


    }
}
