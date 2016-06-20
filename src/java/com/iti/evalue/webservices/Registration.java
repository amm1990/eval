/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iti.evalue.webservices;

import com.iti.evalue.business.UserBusiness;
import com.iti.evalue.entities.Users;
import org.apache.tomcat.util.codec.binary.Base64;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

/**
 *
 * @author Aya Mahmoud
 */
@Path("/register")
public class Registration {

    @GET
    @Path("/newuser")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public JSONObject createUser(@QueryParam("parent_name") String parentName, @QueryParam("name") String name,
            @QueryParam("password") String password, @QueryParam("email") String email,
            @QueryParam("gender") String gender, @QueryParam("token") String token) {
        UserBusiness ub = new UserBusiness();
        JSONObject registration = new JSONObject();
        Users parent = null;
        String registered = "failed";
        if (name != null && password != null && gender != null && token != null) {
            if (parentName != null) {
                parent = ub.viewUser(parentName);
            }
            Users user = new Users(parent, name, password, email, gender, token);
            registered = ub.register(user);
        }

        try {
            registration.put("status", registered);
        } catch (JSONException ex) {
            Logger.getLogger(Registration.class.getName()).log(Level.SEVERE, null, ex);
        }
        return registration;
    }

    //@GET
    @POST
    @Path("/saveimage")
    @Produces(MediaType.APPLICATION_JSON)
    //@Consumes(MediaType.MEDIA_TYPE_WILDCARD)
    public JSONObject imageCall(@FormParam("image") String image) {
        JSONObject json = new JSONObject();
         if (image != null) {
             System.out.println("value of image is not null");
        UserBusiness ub = new UserBusiness();
            ub.addImage(image);
         }
        try {
            json.put("inserted", "inserted");
        } catch (JSONException ex) {
            Logger.getLogger(Registration.class.getName()).log(Level.SEVERE, null, ex);
        }
        return json;
    }

    @GET
    //@POST
    @Path("/testread")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public JSONObject sendImage(@QueryParam("user") String user) {
        JSONObject json = new JSONObject();
        UserBusiness ub = new UserBusiness();
        Users u = ub.viewUser(user);
        byte[] imageBytes = u.getImage();
        String imageStringBase64 = Base64.encodeBase64String(imageBytes);
        try {
            json.put("image", imageStringBase64);
        } catch (JSONException ex) {
            Logger.getLogger(Registration.class.getName()).log(Level.SEVERE, null, ex);
        }
        return json;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/allusers")
    public JSONArray allSubscribers() {
        JSONArray json = new JSONArray();
        UserBusiness ub = new UserBusiness();
        List<Users> subscribers = ub.selectAllSubscribers();
        for (int i = 0; i < subscribers.size(); i++) {

            JSONObject jo = new JSONObject();
            Users user = subscribers.get(i);
            try {
                jo.put("name", user.getName());
                jo.put("gender", user.getGender());
                if (user.getEmail() != null) {
                    jo.put("email", user.getEmail());
                }
                if (user.getParentId() != null) {
                    jo.put("parent", user.getParentId().getName());
                }
                json.put(jo);
            } catch (JSONException ex) {
                Logger.getLogger(Registration.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return json;
    }
}