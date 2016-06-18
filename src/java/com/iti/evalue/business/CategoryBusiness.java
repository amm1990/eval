/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iti.evalue.business;

import com.iti.evalue.daos.CategoryDao;
import com.iti.evalue.entities.Category;

/**
 *
 * @author Start
 */
public class CategoryBusiness {
    CategoryDao cd;
    public CategoryBusiness() {
        cd = new CategoryDao();
    }
    
    public Category getCategoryByName(String name) {
        Category category = cd.selectByName(name);
        return category;
    }
}
