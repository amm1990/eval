package com.iti.evalue;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Aya Mahmoud
 */
public class SessionFactoryProvider {
    public static SessionFactoryProvider sessionFactoryProvider;
    public SessionFactory sessionFactory;
    private SessionFactoryProvider() {
        sessionFactory = new Configuration().configure().buildSessionFactory();
        System.out.println("session factory initialized");
    }
    public static SessionFactoryProvider getInstance() {
        if(sessionFactoryProvider==null) {
           sessionFactoryProvider = new SessionFactoryProvider();
        }
        return sessionFactoryProvider;
    }
}