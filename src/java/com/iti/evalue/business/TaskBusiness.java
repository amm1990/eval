/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iti.evalue.business;

import com.iti.evalue.daos.TaskDao;
import com.iti.evalue.daos.TypeDao;
import com.iti.evalue.daos.UserDao;
import com.iti.evalue.daos.UsersTaskDao;
import com.iti.evalue.entities.Task;
import com.iti.evalue.entities.Type;
import com.iti.evalue.entities.Users;
import com.iti.evalue.entities.UsersTask;
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
    TypeDao tyd;
    UsersTaskDao utd;

    public TaskBusiness() {
        td = new TaskDao();
        ud = new UserDao();
        tyd = new TypeDao();
        utd = new UsersTaskDao();
    }

    public List<UsersTask> getUserTasks(String name) {

        ArrayList userTasks = null;
        if (name != null) {
            Users user = ud.selectByUser(name);
            if (user != null) {
                userTasks = new ArrayList();
                List<UsersTask> membertasklist = user.getUsersTaskList();
                for (int i = 0; i < membertasklist.size(); i++) {
                    UsersTask mtl = membertasklist.get(i);
                    if (mtl.getTaskId().getParentid() == null) {
                            if (mtl.getTaskId().getEndDate().compareTo(new Date()) > 0) {
                                userTasks.add(mtl);
                            }
                    }
                }
            }
        }
        return userTasks;
    }

    public List<UsersTask> getUserTeamAndParentTasks(String name) {
        List<UsersTask> unfiltered = getUserTasks(name);
        for(int i=0; i < unfiltered.size(); i++) {
            if(unfiltered.get(i).getTaskId().getTypeId().getName().equals("Individual")) {
                unfiltered.remove(unfiltered.get(i));
            }
        }
        return unfiltered;
    }
    
    public List<Task> getOwnerTasks(String name) {
        ArrayList tasks = null;
        Users user = ud.selectByUser(name);
        if (user != null) {
            tasks = new ArrayList();
            List<Task> ownertasklist = user.getTaskList();
            for (int i = 0; i < ownertasklist.size(); i++) {
                Task task = (Task) ownertasklist.get(i);
                if (task.getParentid() == null) {
                    if (task.getEndDate().compareTo(new Date()) > 0) {
                        tasks.add(task);
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
            if (t.getTypeId().getName().equals("Individual")) {
                assignUserToTask(t.getOwnerId(), t, t.getOwnerId());
            }
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

    //used for task deletion
    public boolean deleteTask(String owner, String name) {
        boolean deleted = false;
        if (owner != null && name != null) {
            Task task = td.selectByName(name);
            Users o = ud.selectByUser(owner);
            if (task != null && o != null) {
                if (task.getOwnerId().equals(o)) {
                    List<Task> list = task.getTaskList();
                    for (int i = 0; i < list.size(); i++) {
                        td.deleteTask(list.get(i));
                    }
                    List<UsersTask> assignments = task.getUsersTaskList();
                    for (int i = 0; i < assignments.size(); i++) {
                        String body = o.getName() + " has deleted " + name + " task";
                        Notifier.send(assignments.get(i).getUserId().getToken(), body);
                    }
                    td.deleteTask(task);
                    deleted = true;
                }
            }
        }
        return deleted;
    }

    public boolean assignUserToTask(Users owner, Task task, Users user) {
        boolean added = false;
        if (task != null && user != null && owner != null) {
            if (owner.equals(task.getOwnerId())) {
                List<UsersTask> list = task.getUsersTaskList();
                System.out.println(list);
                boolean exists = false;
                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i).getUserId().equals(user)) {
                        exists = true;
                    }
                }
                if (!exists) {
                    String app;
                    if (task.getTypeId().getName().equals("Individual")) {
                        app = "approved";
                    } else {
                        app = "disapproved";
                    }
                    UsersTask ut = new UsersTask();
                    ut.setUserId(user);
                    ut.setTaskId(task);
                    ut.setAchievement(0);
                    ut.setApproval(app);
                    ut.setApproval(app);
                    utd.addUserToTask(ut);
                    List<Task> milestones = task.getTaskList();
                    for (int i = 0; i < milestones.size(); i++) {
                        UsersTask utm = new UsersTask();
                        utm.setTaskId(milestones.get(i));
                        utm.setUserId(user);
                        utm.setAchievement(0);
                        utm.setApproval(app);
                        utd.addUserToTask(utm);
                    }
                    if (!task.getTypeId().getName().equals("Individual")) {
                        String body = owner.getName() + " added you to the task " + task.getName();
                        Notifier.send(user.getToken(), body);
                    }
                    added = true;
                }
            }
        }
        return added;
    }

    public boolean removeUserFromTask(Users owner, Task task, Users user) {
        boolean removed = false;
        if (task != null && user != null && owner != null) {
            if (owner.equals(task.getOwnerId())) {
                List<UsersTask> list = task.getUsersTaskList();
                UsersTask ut = null;
                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i).getUserId().equals(user)) {
                        ut = list.get(i);
                    }
                }
                if (ut != null) {

                    List<Task> milestones = task.getTaskList();
                    for (int i = 0; i < milestones.size(); i++) {
                        List<UsersTask> utm = milestones.get(i).getUsersTaskList();
                        for (int j = 0; j < utm.size(); j++) {
                            if (utm.get(j).getUserId().equals(user)) {
                                utd.deleteUserFromTask(utm.get(j));
                            }
                        }
                    }
                    utd.deleteUserFromTask(ut);
                    if (!user.equals(owner)) {
                        String body = owner.getName() + " removed you from " + task.getName() + " task";
                        Notifier.send(user.getToken(), body);
                    }
                    removed = true;
                }
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

    public boolean submitAchievement(float eval, String userName, String milestone) {
        boolean submitted = false;
        Task ms = td.selectByName(milestone);
        Users user = ud.selectByUser(userName);
        if (ms != null && user != null) {
            if (ms.getParentid() != null) {
                if (eval <= ms.getTotal()) {
                    UsersTask ut = utd.selectAssignment(user, ms);
                    if (ut != null) {
                        ut.setAchievement(eval);
                        List<UsersTask> uts = user.getUsersTaskList();
                        for(int i=0; i<uts.size(); i++) {
                                if(uts.get(i).getTaskId().equals(ms.getParentid())) {
                                    System.out.println(uts.get(i).getTaskId());
                                    uts.get(i).setAchievement(eval + uts.get(i).getAchievement());
                                    utd.updateUsersTask(uts.get(i));
                                }
                            }
                        if (ms.getTypeId().getName().equals("Individual")) {
                            ut.setApproval("approved");
                            
                        } else {
                            ut.setApproval("disapproved");
                        }
                        utd.updateUsersTask(ut);
                        if (!ms.getTypeId().getName().equals("Individual")) {
                            String Notificationbody = "user " + user.getName()
                                    + " submitted their achievement for milestone " + ms.getName() + "  of task "
                                    + ms.getParentid().getName() + " and is requesting your approval";
                            Notifier.send(ms.getOwnerId().getToken(), Notificationbody);
                        }
                        submitted = true;
                    }
                }
            }
        }
        return submitted;
    }

    public void approveAchievement(String user, String milestone, String approval) {
        Users u = ud.selectByUser(user);
        Task ms = td.selectByName(milestone);
        if (u != null && ms != null && (approval.equals("approved") || approval.equals("disapproved"))) {
            UsersTask ut = utd.selectAssignment(u, ms);
            ut.setApproval(approval);
            utd.updateUsersTask(ut);
            if (approval.equals("approved")) {
                UsersTask taskAssignment = utd.selectAssignment(u, ms.getParentid());
                taskAssignment.setAchievement(taskAssignment.getAchievement() + ut.getAchievement());
                utd.updateUsersTask(taskAssignment);
            }
            String body = ms.getOwnerId().getName() + " " + approval + " of your achievement for " + ms.getName()
                    + " in " + ms.getParentid().getName() + " task";
            Notifier.send(u.getToken(), body);
        }
    }

    public void approveJoinTask(String user, String task, String approval) {
        Users u = ud.selectByUser(user);
        Task t = td.selectByName(task);
        if (u != null && t != null && (approval.equals("approved") || approval.equals("disapproved"))) {
            Users owner = t.getOwnerId();
            if (approval.equals("disapproved")) {
                UsersTask ut = utd.selectAssignment(u, t);
                removeUserFromTask(owner, t, u);
            } else if (approval.equals("approved")) {
                //check if user exists in list if he doesn't add them
            }
            String body = u.getName() + " " + approval + " of joining " + t.getName() + " task";
            Notifier.send(owner.getToken(), body);
        }
    }

    public List<Task> selectTasksByType(String owner, String type) {
        Users o = ud.selectByUser(owner);
        Type t = tyd.selectByName(type);
        List<Task> tasks = null;
        if (o != null && t != null) {
            tasks = new ArrayList();
            for (Task task : o.getTaskList()) {
                if (task.getTypeId().equals(t) && task.getParentid() == null) {
                    tasks.add(task);
                }
            }
            for (UsersTask usersTask : o.getUsersTaskList()) {
                if (usersTask.getTaskId().getTypeId().equals(t) && usersTask.getTaskId().getParentid() == null) {
                    if (!usersTask.getTaskId().getTypeId().getName().equals("Individual")) {
                        tasks.add(usersTask.getTaskId());
                    }
                }
            }
        }
        return tasks;
    }

    public List<Task> selectTasksAllForUser(String user) {
        Users u = ud.selectByUser(user);
        List<Task> tasks = null;
        if (u != null) {
            tasks = new ArrayList();
            for (Task task : u.getTaskList()) {
                if (task.getParentid() == null) {
                    tasks.add(task);
                }
            }
            for (UsersTask usersTask : u.getUsersTaskList()) {
                if (usersTask.getTaskId().getParentid() == null) {
                    if (!usersTask.getTaskId().getTypeId().getName().equals("Individual")) {
                        tasks.add(usersTask.getTaskId());
                    }
                }
            }
        }
        return tasks;
    }

    public String checkRole(Task task, String user) {
        String role = "user";
        Users u = ud.selectByUser(user);
        if (task.getOwnerId().equals(u)) {
            role = "owner";
        }
        return role;
    }

    public List<Users> getTaskUsers(String taskName) {
        Task task = td.selectByName(taskName);
        List<UsersTask> assignments = task.getUsersTaskList();
        List<Users> members = new ArrayList();
        for(int i=0; i < assignments.size(); i++) {
            members.add(assignments.get(i).getUserId());
        }
        return members;
    }
}
