/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iti.evalue.daos;

import com.iti.evalue.SessionFactoryProvider;
import com.iti.evalue.entities.Task;
import com.iti.evalue.entities.Users;
import com.iti.evalue.entities.UsersTask;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

/**
 *
 * @author Aya Mahmoud
 */
public class UsersTaskDao {
    SessionFactory sessionFactory;
    Session session;
    public UsersTaskDao() {
        sessionFactory = SessionFactoryProvider.getInstance().sessionFactory;
    }
    
    public void addUserToTask(UsersTask ut) {
        session = sessionFactory.getCurrentSession();
        session.beginTransaction();
        session.persist(ut);
        session.getTransaction().commit();
    }
    
    public void deleteUserFromTask(UsersTask assignment) {
        session = sessionFactory.getCurrentSession();
        session.beginTransaction();
        session.delete(assignment);
        session.getTransaction().commit();
    }

    public UsersTask selectAssignment(Users user, Task task) {
        session = sessionFactory.getCurrentSession();
        session.beginTransaction();
        Query query = session.createQuery("SELECT u FROM UsersTask u WHERE u.taskId = :t_id AND u.userId = :u_id");
        query.setEntity("t_id", task);
        query.setEntity("u_id", user);
        UsersTask ut = (UsersTask) query.uniqueResult();
        session.getTransaction().commit();
        return ut;
    }
    
    public void updateUsersTask(UsersTask update) {
        session = sessionFactory.getCurrentSession();
        session.beginTransaction();
        session.merge(update);
        session.getTransaction().commit();
    }
}
