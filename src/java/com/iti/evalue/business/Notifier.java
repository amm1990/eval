/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iti.evalue.business;

import com.notnoop.apns.APNS;
import com.notnoop.apns.ApnsService;

/**
 *
 * @author Aya Mahmoud
 */
public class Notifier {
    public void approveAchievement() {
        
    }

    void send(String token, String body) {
        ApnsService service = APNS.newService().withCert(
                "C:\\Users\\Aya Mahmoud\\Documents\\NetBeansProjects\\evalue\\web\\WEB-INF\\resources\\"
                        + "EvalueCertificate.p12", "12345").withSandboxDestination().build();
        String payload = APNS.newPayload().alertBody(body).build();
        service.push(token, payload);
    }
}
