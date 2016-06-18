/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iti.evalue.business;

import com.iti.evalue.daos.TypeDao;
import com.iti.evalue.entities.Type;

/**
 *
 * @author Aya Mahmoud
 */
public class TypeBusiness {
    TypeDao td;
    public TypeBusiness() {
        td = new TypeDao();
    }
    
    public Type getTypebyName(String name) {
        Type type = td.selectByName(name);
        return type;
    }
}
