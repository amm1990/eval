/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iti.evalue.business;

import com.iti.evalue.daos.TaskDao;
import com.iti.evalue.daos.UserDao;
import com.iti.evalue.daos.UsersTaskDao;
import com.iti.evalue.entities.Task;
import com.iti.evalue.entities.Users;
import com.iti.evalue.entities.UsersTask;
import com.notnoop.apns.APNS;
import com.notnoop.apns.ApnsService;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * @author Aya Mahmoud
 */
public class TaskBusiness {

    TaskDao td;
    UserDao ud;
    UsersTaskDao utd;

    public TaskBusiness() {
        td = new TaskDao();
        ud = new UserDao();
        utd = new UsersTaskDao();
    }

    public List getUserTasks(String name) {

        ArrayList tasks = new ArrayList();
        if (name != null) {
            Users user = ud.selectByUser(name);
            if (user != null) {
                List membertasklist = user.getTaskList();
                for (int i = 0; i < membertasklist.size(); i++) {
                    Task task = (Task) membertasklist.get(i);
                    if (task.getParentid() == null) {
                        if (task.getEndDate().compareTo(new Date()) > 0) {
                            tasks.add(task);
                        }
                    }
                }
            }
        }
        return tasks;
    }

    public List getOwnerTasks(String name) {

        ArrayList tasks = new ArrayList();
        if (name != null) {
            Users user = ud.selectByUser(name);
            if (user != null) {
                List ownertasklist = user.getTaskList();
                for (int i = 0; i < ownertasklist.size(); i++) {
                    Task task = (Task) ownertasklist.get(i);
                    if (task.getParentid() == null) {
                        if (task.getEndDate().compareTo(new Date()) > 0) {
                            tasks.add(task);
                        }
                    }
                }
            }
        }
        return tasks;
    }

    public List getMilestones(String name) {
        List milestones = null;
        if (name != null) {
            Task task = getTaskByName(name);
            milestones = task.getTaskList();
        }
        return milestones;
    }

    //used to insert new task and milestone
    public int addTask(Task task) {
        Task existing = getTaskByName(task.getName());
        int taskId = 0;
        if (existing == null) {
            if (task.getDescription() == null) {
                task.setDescription("");
            }
            td.taskAdd(task);
            Task t = getTaskByName(task.getName());
            taskId = t.getId();
        }
        return taskId;
    }

    //added for milestones creation service
    public Task getTaskByName(String name) {
        return td.selectByName(name);
    }

    public String addMilestone(Task milestone) {
        td.taskAdd(milestone);
        String result = "saved";
        return result;
    }

    // get task  by owner_id & task-name &     user-name
    public Task getTaskByNameAndOwnerName(String taskName, String userName) {

        UserBusiness ub = new UserBusiness();
        int userId = ub.getUserIdByName(userName);
        Task task;
        TaskDao t = new TaskDao();
        int taskId = t.selectByOwnerIdAndTaskName(userId, taskName);
        task = t.selectById(taskId);
        return task;
    }

    //used for task deletion
    public boolean deleteTask(String name) {
        boolean deleted = false;
        Task task = td.selectByName(name);
        if (task != null) {
            for (int i = 0; i < task.getTaskList().size(); i++) {
                td.deleteTask(task.getTaskList().get(i));
            }
            deleted = td.deleteTask(task);
        }
        return deleted;
    }

    public boolean assignUserToTask(Task task, Users user) {
        boolean added = false;
        if (task != null && user != null) {
            List<UsersTask> list = task.getUsersTaskList();
            boolean exists = false;
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).getUserId().equals(user)) {
                    exists = true;
                }
                if (!exists) {
                    UsersTask ut = new UsersTask();
                    ut.setUserId(user);
                    ut.setTaskId(task);
                    utd.addUserToTask(ut);
                    added = true;
                }
            }
        }
        return added;
    }

    public boolean removeUserFromTask(Task task, Users user) {
        boolean removed = false;
        if (task != null && user != null) {
            List<UsersTask> list = task.getUsersTaskList();
            UsersTask ut = null;
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).getUserId().equals(user)) {
                    ut = list.get(i);
                }
            }
            if (ut != null) {
                utd.deleteUserFromTask(ut);
                removed = true;
            }
        }
        return removed;
    }

    public List<Task> getTaskMilestones(String name) {
        List<Task> milestones = null;
        Task parent = td.selectByName(name);
        if (parent != null) {
            milestones = parent.getTaskList();
        }
        return milestones;
    }

    public boolean submitAchievement(float eval, String userName, String taskName) {
        boolean submitted = false;
        Task task = td.selectByName(taskName);
        Users user = ud.selectByUser(userName);
        if (task != null && user != null) {
            UsersTask ut = utd.selectAssignment(user, task);
            ut.setAchievement(eval);
            Notifier notifier = new Notifier();
            String Notificationbody = "user " + user.getName() + " submitted their achievement for "
                    + task.getName() + " task and is requesting your approval";
            notifier.send(task.getOwnerId().getToken(), Notificationbody);
            submitted = true;
        }
        return submitted;
    }
}