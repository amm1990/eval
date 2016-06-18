/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iti.evalue.daos;

import com.iti.evalue.SessionFactoryProvider;
import com.iti.evalue.entities.Category;
import java.util.ArrayList;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

/**
 *
 * @author Aya Mahmoud
 */
public class CategoryDao {
    SessionFactory sessionFactory;
    Session session;
    
    public CategoryDao() {
        sessionFactory = SessionFactoryProvider.getInstance().sessionFactory;
    }
    
    // Insert New Category
      public void categoryAdd(Category newCategory){
       session = sessionFactory.getCurrentSession();
       session.beginTransaction();
       session.persist(newCategory);
       session.getTransaction().commit();
//       session.close();
      }
     
      // Select All Categories
        public ArrayList<Category> selectAllCategories() {
        session = sessionFactory.getCurrentSession();
        session.beginTransaction();
        Query query = session.createQuery("FROM Category");
        ArrayList<Category> allCategories = new ArrayList<>(query.list());
        session.getTransaction().commit();
        return allCategories;
        }
        
       //Update Category Info
     public void updateCategory(Category updatedCategory) {
        session = sessionFactory.getCurrentSession();
        session.beginTransaction();
        session.saveOrUpdate(updatedCategory);
        session.getTransaction().commit();
//        session.close();
    }
      
     //Delete Category Info
     public boolean deleteCategory(Category deletedCategory) {
        boolean deleted = false;
        session = sessionFactory.getCurrentSession();
        session.beginTransaction();
        if(deletedCategory!=null) {
            session.delete(deletedCategory);
            session.getTransaction().commit();
//            session.close();
            deleted = true;
        }
        return deleted;
    }
     
     //Select from category by id
     public Category selectById(int categoryId) {
        session = sessionFactory.getCurrentSession();
        session.beginTransaction();
        Category category = (Category) session.createQuery("from Category where id = '" + categoryId + "'").uniqueResult();
        session.getTransaction().commit();
//        session.close();
        return category;
    }
     
          //Select from category by name
     public Category selectByName(String categoryName) {
        session = sessionFactory.getCurrentSession();
        session.beginTransaction();
        Category category = (Category) session.createQuery("from Category where name = '" + categoryName + "'").uniqueResult();
        session.getTransaction().commit();
//        session.close();
        return category;
    }
}