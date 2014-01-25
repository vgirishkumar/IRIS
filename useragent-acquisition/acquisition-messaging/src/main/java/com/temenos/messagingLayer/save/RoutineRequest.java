package com.temenos.messagingLayer.save;

import com.temenos.messagingLayer.pojo.Ofsml13SecurityContext;
import com.temenos.messagingLayer.pojo.Ofsml13ServiceRequest;
import com.temenos.messagingLayer.pojo.Ofsml13StandardRoutine;
import com.temenos.messagingLayer.requestUtils.Marshall;
import com.temenos.messagingLayer.securityContext.SecurityContext;
import com.temenos.messagingLayer.talkToServer.TalkToServer;

/**
 * Forms the ofsml routine request
 * 
 * @author anitha
 * 
 */
public class RoutineRequest {

	// Routine based request for PostMeMyDocuments
	public String formRoutineRequest(String routineName, String routineParams, TalkToServer server) {
		Ofsml13ServiceRequest serReq = new Ofsml13ServiceRequest();
		SecurityContext Sec = new SecurityContext();
		Ofsml13StandardRoutine stdRoutine = new Ofsml13StandardRoutine();
		Ofsml13SecurityContext secContext = Sec.generateSecurityContext(server.getSecurityContext().getUserName(),
				server.getSecurityContext().getPassword(), null);
		serReq.setSecurityContext(secContext);
		stdRoutine.setName(routineName);
		serReq.setOfsStandardRoutine(stdRoutine);
		Marshall mar = new Marshall();
		// create a Marshaller and marshal to System.out
		String genRequestXml = mar.doMarshalling(serReq);
		String[] genRequests = genRequestXml.split("/>");
		genRequestXml = genRequests[0] + ">" + routineParams + "</ofsStandardRoutine>" + genRequests[1];
		return genRequestXml;
	}
}
