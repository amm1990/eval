/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iti.evalue.daos;

import com.iti.evalue.SessionFactoryProvider;
import com.iti.evalue.entities.Users;
import java.util.ArrayList;
import org.hibernate.Hibernate;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

/**
 *
 * @author Aya Mahmoud
 */
public class UserDao {
    
    SessionFactory sessionFactory;
    Session session;
    public UserDao() {
        sessionFactory = SessionFactoryProvider.getInstance().sessionFactory;
    }
    
    public void userAdd(Users newUser) {
        session = sessionFactory.getCurrentSession();
        session.beginTransaction();
        session.persist(newUser);
        session.getTransaction().commit();
    }
    
    public void updateUser(Users updatedUser) {
        session = sessionFactory.getCurrentSession();
        session.beginTransaction();
        session.merge(updatedUser);
        session.getTransaction().commit();
    }
    
    public boolean deleteUser(Users deletedUser) {
        boolean deleted = false;
        session = sessionFactory.getCurrentSession();
        session.beginTransaction();
        if(deletedUser!=null) {
            session.delete(deletedUser);
            session.getTransaction().commit();
            deleted = true;
        }
        return deleted;
    }

    public Users selectByEmail(String email) {
        session = sessionFactory.getCurrentSession();
        session.beginTransaction();
        Users user = (Users) session.createQuery("from Users where email = '" + email + "'").uniqueResult();
        session.getTransaction().commit();
        return user;
    }

    public Users selectByUser(String name) {
        session = sessionFactory.getCurrentSession();
        session.beginTransaction();
        Users user = (Users) session.createQuery("from Users where name = '" + name + "'").uniqueResult();
        if(user!=null) {
            Hibernate.initialize(user.getTaskList());
            Hibernate.initialize(user.getUsersTaskList());
            Hibernate.initialize(user.getUsersList());
        }
        session.getTransaction().commit();
        return user;
    }

    public Users checkExists(Users u) {
        session = sessionFactory.getCurrentSession();
        session.beginTransaction();
        Users user = (Users) session.createQuery("from Users where name = '" + u.getName() + "' and password = '" + u.getPassword() + "'").uniqueResult();
        session.getTransaction().commit();
        //session.close();
        return user;
    }

    public ArrayList<Users> selectAllUsers() {
        session = sessionFactory.getCurrentSession();
        session.beginTransaction();
        Query query = session.createQuery("FROM Users");
        ArrayList<Users> allUsers = new ArrayList<>(query.list());
        session.getTransaction().commit();
        return allUsers;
    }
    
    public Users selectById(int id) {
        session = sessionFactory.getCurrentSession();
        session.beginTransaction();
        Users user = (Users) session.createQuery("from Users where id = " + id).uniqueResult();
        session.getTransaction().commit();
        return user;
    }
}
