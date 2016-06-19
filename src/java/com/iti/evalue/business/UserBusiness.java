/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iti.evalue.business;

import com.iti.evalue.daos.UserDao;
import com.iti.evalue.entities.Users;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 *
 * @author Aya Mahmoud
 */
public class UserBusiness {

    UserDao ud;

    public UserBusiness() {
        ud = new UserDao();
    }

    ///used for registration service
    public String register(Users u) {
        String result = "failed";
        Users u1 = null;
        Users u2;
        if ((u.getName() != null) && (u.getGender() != null) && (u.getPassword() != null) && (u.getToken() != null)) {
            u2 = ud.selectByUser(u.getName());
            if (u.getEmail() != null) {
                u1 = ud.selectByEmail(u.getEmail());
            }
            if (u1 != null && u2 != null) {
                result = "both";
            } else if (u1 != null && u2 == null) {
                result = "email";
            } else if (u1 == null && u2 != null) {
                result = "name";
            } else if (u1 == null && u2 == null) {
                ud.userAdd(u);
                result = "success";
            }
        }
        return result;
    }

    ///used for forgot password
    public boolean checkNameExists(String name) {
        boolean exists = true;
        Users user = ud.selectByUser(name);
        if (user == null) {
            exists = false;
        }
        return exists;
    }

    ///used for login
    public Users login(String name, String password) {
        Users user = new Users();
        user.setName(name);
        user.setPassword(password);
        return ud.checkExists(user);
    }

    //used for view profile
    public Users viewUser(String name) {
        Users user = ud.selectByUser(name);
        return user;
    }

    //used for editing profile
    public String updateUser(Users user) {
        String updated = "fail";
        Users u = ud.selectById(user.getId());
        if (u != null) {
            Users u1 = ud.selectByUser(u.getName());
            Users u2 = ud.selectByEmail(u.getEmail());
            if (u1 != null && u2 != null && !u1.equals(u) && !u2.equals(u)) {
                updated = "both";
            } else if (u1 != null && u2 == null && !u1.equals(u)) {
                updated = "name";
            } else if (u1 == null && u2 != null && !u2.equals(u)) {
                updated = "email";
            } else if (u1 == null && u2 == null) {

                u.setName(user.getName());
                u.setPassword(user.getPassword());
                u.setEmail(user.getEmail());
                u.setGender(user.getGender());
                ud.updateUser(u);
                updated = "success";
            }
        }
        return updated;
    }

    //used for forgot password
    public boolean updatePassword(String name, String password) {
        boolean updated = false;
        Users user = ud.selectByUser(name);
        if (user != null) {
            user.setPassword(password);
            ud.updateUser(user);
            updated = true;
        }
        return updated;
    }

    public List<Users> getChildAccounts(String parent_name) {
        List<Users> children = null;
        Users parent = ud.selectByUser(parent_name);
        if (parent != null) {
            children = parent.getUsersList();
        }
        return children;
    }

    public boolean sendPasswordMail(Users user) {
        String newPassword = new BigInteger(30, new SecureRandom()).toString(32);
        String to = user.getEmail();
        String subject = "evalue app password";
        String body = "Your evalue application password has been reset to \"" + newPassword + "\"";
        boolean sent = Mailer.sendMail(to, subject, body);
        user.setPassword(newPassword);
        ud.updateUser(user);
        return sent;
    }
    // get user id for achievement service..    

//    public int getUserIdByName(String userName) {
//        int uid;
//        Users u = ud.selectByUser(userName);
//        uid = u.getId();
//        return uid;
//    }
///dummy methods to testimage sending
    public void addImage() {
        String dd = "hellooooo newjersey";
        byte[] data = dd.getBytes();
        String ds = Base64.getEncoder().encodeToString(data);
        byte[] result = Base64.getDecoder().decode(ds);
        System.out.println(result + "   " + result.toString());

        //String coded = Base64.getEncoder().encodeToString(image);
        //return coded;
        //Users user = ud.selectByUser("aya");
        //user.setImage(data);
        //ud.updateUser(user);
    }

//    public String readImage() {
//        byte[] image = ud.selectByUser("aya").getImage();
//        String coded = Base64.getEncoder().encodeToString(image);
//        return coded;
//    }
}
