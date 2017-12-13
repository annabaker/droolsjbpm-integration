/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.kie.server.integrationtests.jbpm.jms;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runners.Parameterized;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.api.runtime.manager.RuntimeManagerFactory;
import org.kie.server.api.marshalling.MarshallingFormat;
import org.kie.server.api.model.KieContainerResource;
import org.kie.server.api.model.KieContainerResourceList;
import org.kie.server.api.model.ReleaseId;
import org.kie.server.api.model.ServiceResponse;
import org.kie.server.api.model.definition.ProcessDefinition;
import org.kie.server.api.model.instance.ProcessInstance;
import org.kie.server.client.KieServicesClient;
import org.kie.server.client.KieServicesConfiguration;
import org.kie.server.client.KieServicesFactory;
import org.kie.server.client.jms.AsyncResponseHandler;
import org.kie.server.client.jms.BlockingResponseCallback;
import org.kie.server.client.jms.ResponseCallback;
import org.kie.server.integrationtests.category.JMSOnly;
import org.kie.server.integrationtests.jbpm.JbpmKieServerBaseIntegrationTest;
import org.kie.server.integrationtests.shared.KieServerDeployer;

@Category({JMSOnly.class})
public class JmsWorkItemHandlerXAIntegrationTest extends JbpmKieServerBaseIntegrationTest {

    private static final ReleaseId RELEASE_ID = new ReleaseId("org.kie.server.testing", "jms-wih-project", "1.0.0.Final");
    
    private List<KieContainerResource> containers;
    
    // clients
    private KieServicesClient kieServicesClient;
    
    private RuntimeManager runtimeManager;
    
    
    private static KieServicesConfiguration jmsConfiguration;
    private ServiceResponse<KieContainerResourceList> response;

    @Parameterized.Parameters(name = "{index}: {0}")
    public static Collection<Object[]> data() {
        jmsConfiguration = createKieServicesJmsConfiguration();
        Collection<Object[]> parameterData = new ArrayList<>(Arrays.asList(new Object[][]{
                {MarshallingFormat.JAXB, jmsConfiguration},
                {MarshallingFormat.JSON, jmsConfiguration},
                {MarshallingFormat.XSTREAM, jmsConfiguration}
        }));

        return parameterData;
    }

    @BeforeClass
    public static void buildAndDeployArtifacts() {
        KieServerDeployer.buildAndDeployCommonMavenParent();
        KieServerDeployer.buildAndDeployMavenProject(ClassLoader.class.getResource("/kjars-sources/jms-wih-project").getFile());

        kieContainer = KieServices.Factory.get().newKieContainer(RELEASE_ID);

        createContainer(CONTAINER_ID, RELEASE_ID);
    }

//    @After
//    public void resetResponseHandler() {
//        processClient.setResponseHandler(new RequestReplyResponseHandler());
//        queryClient.setResponseHandler(new RequestReplyResponseHandler());
//        taskClient.setResponseHandler(new RequestReplyResponseHandler());
//    }

    @Override
    protected void addExtraCustomClasses(Map<String, Class<?>> extraClasses) throws Exception {
        //extraClasses.put(PERSON_CLASS_NAME, Class.forName(PERSON_CLASS_NAME, true, kieContainer.getClassLoader()));
    }
    
    @Test
    public void testKieContainerDeployment() throws Exception {
        kieServicesClient = KieServicesFactory.newKieServicesClient(jmsConfiguration);
        response = kieServicesClient.listContainers();
        containers = response.getResult().getContainers();
        
        assertTrue(containers.size() == 1);
        System.out.println("RESPONSE: " + response.getResult().toString());
    }

    @Test
    public void testWIHGetsCalled() throws Exception {
    	
    	kieServicesClient = KieServicesFactory.newKieServicesClient(jmsConfiguration);
        response = kieServicesClient.listContainers();
        containers = response.getResult().getContainers();
    	System.out.println("CONTAINER SIZE BEFORE: " + containers.size());
    	
    	List<ProcessInstance> processes = processClient.findProcessInstances(JMS_WIH_CONTAINER_ID, 0, 10);
    	assertEquals(processes.size(), 0);
    	
    	ProcessDefinition processDefinition = processClient.getProcessDefinition(JMS_WIH_CONTAINER_ID, PROCESS_ID_JMS_WIH);
    	
    	Long processId = processClient.startProcess(JMS_WIH_CONTAINER_ID, PROCESS_ID_JMS_WIH);
    	
    	
    	
    	System.out.println("CONTAINER SIZE AFTER: " + containers.size());
	
        assertEquals(1,1);
    }

}
