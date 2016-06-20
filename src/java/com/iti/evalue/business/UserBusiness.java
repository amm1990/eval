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
import java.util.List;
import org.apache.tomcat.util.codec.binary.Base64;

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
            if ((u.getEmail().equals(user.getEmail()) && u.getName().equals(user.getName()))
                    || ((u.getEmail().equals(user.getEmail()) && (!u.getName().equals(user.getName()) && ud.selectByUser(user.getName()) == null)))
                    || ((!u.getEmail().equals(user.getEmail()) && ud.selectByEmail(user.getEmail()) == null) && u.getName().equals(user.getName()))
                    || ((!u.getEmail().equals(user.getEmail()) && ud.selectByEmail(user.getEmail()) == null)
                    && (!u.getName().equals(user.getName()) && ud.selectByUser(user.getName()) == null))) {
                u.setName(user.getName());
                u.setPassword(user.getPassword());
                u.setEmail(user.getEmail());
                u.setGender(user.getGender());
                ud.updateUser(u);
                updated = "success";
            } else if (((!u.getEmail().equals(user.getEmail()) && ud.selectByEmail(user.getEmail()) == null)
                    && (!u.getName().equals(user.getName()) && ud.selectByUser(user.getName()) != null))
                    || ((u.getEmail().equals(user.getEmail()))
                    && (!u.getName().equals(user.getName()) && ud.selectByUser(user.getName()) == null))) {

                updated = "name";
            } else if (((!u.getEmail().equals(user.getEmail()) && ud.selectByEmail(user.getEmail()) != null) && (u.getName().equals(user.getName())))
                    || ((!u.getEmail().equals(user.getEmail()) && ud.selectByEmail(user.getEmail()) != null) && (!u.getName().equals(user.getName())
                    && ud.selectByUser(user.getName()) == null))) {
                updated = "email";
            } else if ((!u.getEmail().equals(user.getEmail()) && ud.selectByEmail(user.getEmail()) != null)
                    && (!u.getName().equals(user.getName()) && ud.selectByUser(user.getName()) != null)) {
                updated = "both";
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

    public void addImage(String image) {
        byte[] imageBytes = Base64.decodeBase64(image);
        Users user = ud.selectByUser("Mas");
        user.setImage(imageBytes);
        ud.updateUser(user);
    }

    public List<Users> selectAllSubscribers() {
        return ud.selectAllUsers();
    }
}
