package com.ragna.conekta;


import com.ragna.FindMongo;
import com.ragna.SaveMongo;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.RouteDefinition;
import org.apache.camel.test.spring.CamelSpringTestSupport;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.junit.Test;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by ramsescarbajal on 02/10/16.
 */
public class UnitTest extends CamelSpringTestSupport {


    private Set<String> adviced = new TreeSet<String>();
    final static Logger logger = Logger.getLogger(UnitTest.class);
    /* (non-Javadoc)
	 * @see org.apache.camel.test.spring.CamelSpringTestSupport#createApplicationContext()
	 */
    @Override
    protected AbstractApplicationContext createApplicationContext() {
        return new ClassPathXmlApplicationContext( "META-INF/spring/camel-context.xml", "META-INF/spring/test-references.xml" );
    }

    /* (non-Javadoc)
	 * @see org.apache.camel.test.junit4.CamelTestSupport#isCreateCamelContextPerClass()
	 */
    @Override
    public boolean isCreateCamelContextPerClass() {
        return true;
    }

    @SuppressWarnings("deprecation")
    private void adviceEndpoints(String routeId) throws Exception {
        if (adviced.contains(routeId))
            return;
        RouteDefinition route = context().getRouteDefinition(routeId);
        route.adviceWith(context(), new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                interceptSendToEndpoint("direct:saveData").skipSendToOriginalEndpoint().process(new SaveMongo());
                interceptSendToEndpoint("direct:findData").skipSendToOriginalEndpoint().process(new FindMongo());
                interceptSendToEndpoint("mongodb:*").skipSendToOriginalEndpoint().process(new SaveMongo());
            }
        });
        adviced.add(routeId);
    }

    @Test
    public void testTokenCreation() throws Exception {
        adviceEndpoints("createToken");
        Object json_request = IOUtils.toString( getClass().getResourceAsStream( "/testFiles/tokenTequest.json" ) );
        logger.info("antes del data");
        Map headers = new HashMap<String, String>();
        headers.put("x-timestamp","x-timestamp");
        headers.put("x-bodid","x-bodid");
        Object data = template.requestBodyAndHeaders("direct:createToken", json_request, headers);
        assertNotNull(data);
        assertTrue(((String)data).contains("token"));

    }


    @Test
    public void testPurchase() throws Exception {
        adviceEndpoints("createToken");
        Object json_request = IOUtils.toString( getClass().getResourceAsStream( "/testFiles/purchaseRequest.json" ) );
        logger.info("antes del data");
        Map headers = new HashMap<String, String>();
        headers.put("x-timestamp","x-timestamp");
        headers.put("x-bodid","x-bodid");
        Object data = template.requestBodyAndHeaders("direct:purchase", json_request, headers);
        assertNotNull(data);
        assertTrue(((String)data).contains("payment processed"));

    }
}
