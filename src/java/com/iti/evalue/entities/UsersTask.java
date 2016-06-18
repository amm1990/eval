/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iti.evalue.entities;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Aya Mahmoud
 */
@Entity
@Table(name = "users_task")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "UsersTask.findAll", query = "SELECT u FROM UsersTask u"),
    @NamedQuery(name = "UsersTask.findById", query = "SELECT u FROM UsersTask u WHERE u.id = :id"),
    @NamedQuery(name = "UsersTask.findByAchievement", query = "SELECT u FROM UsersTask u WHERE u.achievement = :achievement"),
    @NamedQuery(name = "UsersTask.findByApproval", query = "SELECT u FROM UsersTask u WHERE u.approval = :approval")})
public class UsersTask implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @Column(name = "achievement")
    private float achievement;
    @Basic(optional = false)
    @Column(name = "approval")
    private String approval;
    @JoinColumn(name = "task_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Task taskId;
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Users userId;

    public UsersTask() {
    }

    public UsersTask(Integer id) {
        this.id = id;
    }

    public UsersTask(Integer id, float achievement, String approval) {
        this.id = id;
        this.achievement = achievement;
        this.approval = approval;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public float getAchievement() {
        return achievement;
    }

    public void setAchievement(float achievement) {
        this.achievement = achievement;
    }

    public String getApproval() {
        return approval;
    }

    public void setApproval(String approval) {
        this.approval = approval;
    }

    public Task getTaskId() {
        return taskId;
    }

    public void setTaskId(Task taskId) {
        this.taskId = taskId;
    }

    public Users getUserId() {
        return userId;
    }

    public void setUserId(Users userId) {
        this.userId = userId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof UsersTask)) {
            return false;
        }
        UsersTask other = (UsersTask) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.iti.evalue.entities.UsersTask[ id=" + id + " ]";
    }
    
}
