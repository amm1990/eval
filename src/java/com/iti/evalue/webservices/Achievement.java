/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iti.evalue.webservices;

import com.iti.evalue.business.TaskBusiness;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.Consumes;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

/**
 *
 * @author Salma
 */
@Path("/achievement")
public class Achievement {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/addAchievement")
    public JSONObject addAchievement(@QueryParam("username") String userName, 
            @QueryParam("milestone") String milestone, @QueryParam("achievement") String achievement) {

        String inserted = "not_inserted";
        if (userName != null && milestone != null && achievement != null) {
            boolean submitted;
            float eval;
            try {
                eval = Float.parseFloat(achievement);
            } catch (Exception ex) {
                eval = 0;
            }
            if (eval != 0) {
                TaskBusiness tb = new TaskBusiness();
                submitted = tb.submitAchievement(eval, userName, milestone);
                if (submitted) {
                    inserted = "inserted";
                }
            }
        }
        JSONObject jo = new JSONObject();
        try {
            jo.put("insert", inserted);
        } catch (JSONException ex) {
            Logger.getLogger(Login.class.getName()).log(Level.SEVERE, null, ex);
        }
        return jo;
    }
}