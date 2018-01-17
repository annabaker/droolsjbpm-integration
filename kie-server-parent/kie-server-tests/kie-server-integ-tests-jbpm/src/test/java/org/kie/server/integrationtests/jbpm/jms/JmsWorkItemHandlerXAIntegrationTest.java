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
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runners.MethodSorters;
import org.junit.runners.Parameterized;
import org.kie.api.KieServices;
import org.kie.api.runtime.manager.RuntimeManager;
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
import org.kie.server.integrationtests.category.JMSOnly;
import org.kie.server.integrationtests.jbpm.JbpmKieServerBaseIntegrationTest;
import org.kie.server.integrationtests.shared.KieServerDeployer;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@Category({JMSOnly.class})
public class JmsWorkItemHandlerXAIntegrationTest extends JbpmKieServerBaseIntegrationTest {

    private static final ReleaseId RELEASE_ID = new ReleaseId("org.kie.server.testing", "jms-wih-project", "1.0.0.Final");
    private static final List<Integer> ACTIVE = Arrays.asList(org.kie.api.runtime.process.ProcessInstance.STATE_ACTIVE);
    private static final List<Integer> COMPLETED = Arrays.asList(org.kie.api.runtime.process.ProcessInstance.STATE_COMPLETED);

    private List<KieContainerResource> containers;
    
    // clients
    private KieServicesClient kieServicesClient;
    
    private RuntimeManager runtimeManager;
    
    
    private static KieServicesConfiguration jmsConfiguration;
    private ServiceResponse<KieContainerResourceList> response;

    @Parameterized.Parameters(name = "{index}: {0}")
    public static Collection<Object[]> data() {
        jmsConfiguration = createKieServicesJmsConfiguration();
        //jmsConfiguration.setJmsTransactional(true);
        Collection<Object[]> parameterData = new ArrayList<>(Arrays.asList(new Object[][]{
                {MarshallingFormat.JAXB, jmsConfiguration},
                {MarshallingFormat.JSON, jmsConfiguration},
                {MarshallingFormat.XSTREAM, jmsConfiguration}
        }));

        return parameterData;
    }

    /* build and install kjar to m2 repository */
    @BeforeClass
    public static void buildAndDeployArtifacts() {
        KieServerDeployer.buildAndDeployCommonMavenParent();
        KieServerDeployer.buildAndDeployMavenProject(ClassLoader.class.getResource("/kjars-sources/jms-wih-project").getFile());
    }

//    @AfterClass
//    public static void resetResponseHandler() {
//    	abortAllProcesses();
//    }
    
    @Test
    public void A_testKjarDeploysSuccessfully() throws Exception {
    	
    	kieContainer = KieServices.Factory.get().newKieContainer(RELEASE_ID);  	
    	
    	createContainer(JMS_WIH_CONTAINER_ID, RELEASE_ID);
    	
        response = client.listContainers();
        containers = response.getResult().getContainers();
        
        assertTrue(containers.size() == 1);
    }

    @Test
    public void B_testWIHGetsCalled() throws Exception {
    	
    	List<ProcessInstance> processInstances = queryClient.findProcessInstances(0, 10);
    	assertEquals(0, processInstances.size());
    	
    	processClient.startProcess(JMS_WIH_CONTAINER_ID, PROCESS_ID_JMS_WIH);
    	//List<ProcessInstance> activeProcesses = queryClient.findProcessInstancesByStatus(ACTIVE, 0, 100);
    	//assertEquals(1, activeProcesses.size());

    	processInstances = queryClient.findProcessInstances(0, 10);
    	assertEquals(1, processInstances.size());
    	
    	
        
//        state.clear();
          //state.add(org.kie.api.runtime.process.ProcessInstance.STATE_COMPLETED);
//        while(queryClient.findProcessInstancesByStatus(state, 0, 100).size() == 0) {
//        	this.wait(); 
//        	System.out.println("Waiting...");
//        }
        
        
        
        
    }
    
    @Test
    public void C_testWIHGetsCalled() throws Exception {
    	
    	List<ProcessInstance> activeProcesses = queryClient.findProcessInstancesByStatus(COMPLETED, 0, 100);
    	for (ProcessInstance pi : activeProcesses) {
    		System.out.println(pi.toString());
    	}

    	
    	
        
//        state.clear();
          //state.add(org.kie.api.runtime.process.ProcessInstance.STATE_COMPLETED);
//        while(queryClient.findProcessInstancesByStatus(state, 0, 100).size() == 0) {
//        	this.wait(); 
//        	System.out.println("Waiting...");
//        }
        
        
        
        
    }

} 