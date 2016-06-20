/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iti.evalue.webservices;

import com.iti.evalue.business.UserBusiness;
import com.iti.evalue.entities.Users;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import org.apache.tomcat.util.codec.binary.Base64;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import javax.ws.rs.Consumes;
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

    @GET
    //@POST
    @Path("/testsave")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public JSONObject imageCall(@QueryParam("image") String image) {
        FileInputStream fis = null;
        File file = new File("D:\\extras\\pictures\\habit.jpg");
        try {
            fis = new FileInputStream(file);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Registration.class.getName()).log(Level.SEVERE, null, ex);
        }
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] buf = new byte[1024];
        try {
            for (int readNum; (readNum = fis.read(buf)) != -1;) {
                bos.write(buf, 0, readNum);
            }
        } catch (IOException ex) {
            Logger.getLogger(Registration.class.getName()).log(Level.SEVERE, null, ex);
        }
        byte[] bytes = bos.toByteArray();
        String imag64 = Base64.encodeBase64String(bytes);
        JSONObject json = new JSONObject();
        // if (image != null) {
        UserBusiness ub = new UserBusiness();
        //    ub.addImage(image);
        ub.addImage(imag64);
        // }
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

        //test reading
        //ImageIO is a class containing static methods for locating ImageReaders
        //and ImageWriters, and performing simple encoding and decoding. 
        try {
            byte[] reverse = Base64.decodeBase64(imageStringBase64);

            ByteArrayInputStream bis = new ByteArrayInputStream(reverse);
            Iterator<?> readers = ImageIO.getImageReadersByFormatName("jpg");
            ImageReader reader = (ImageReader) readers.next();
            Object source = bis;
            ImageInputStream iis = ImageIO.createImageInputStream(source);
            reader.setInput(iis, true);
            ImageReadParam param = reader.getDefaultReadParam();
            Image image = reader.read(0, param);
            BufferedImage bufferedImage = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_RGB);
            Graphics2D g2 = bufferedImage.createGraphics();
            g2.drawImage(image, null, null);

            File imageFile = new File("D:\\newrose2.jpg");
            ImageIO.write(bufferedImage, "jpg", imageFile);
            //got an image file
        } catch (IOException ex) {
            Logger.getLogger(Registration.class.getName()).log(Level.SEVERE, null, ex);
        }

        //bufferedImage is the RenderedImage to be written
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
                jo.put("email", user.getEmail());
                jo.put("parent", user.getParentId().getName());
                json.put(jo);
            } catch (JSONException ex) {
                Logger.getLogger(Registration.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return json;
    }
}
