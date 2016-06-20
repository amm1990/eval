/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iti.evalue.webservices;

import com.iti.evalue.business.CategoryBusiness;
import com.iti.evalue.business.TaskBusiness;
import com.iti.evalue.business.TypeBusiness;
import com.iti.evalue.business.UserBusiness;
import com.iti.evalue.entities.Category;
import com.iti.evalue.entities.Task;
import com.iti.evalue.entities.Type;
import com.iti.evalue.entities.Users;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

/**
 *
 * @author Start
 */
@Path("/task")
public class TaskServices {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/createtask")
    public JSONObject createTask(@QueryParam("name") String name, @QueryParam("description") String description,
            @QueryParam("category") String categoryName, @QueryParam("type") String typeName,
            @QueryParam("startdate") String startDate, @QueryParam("enddate") String endDate,
            @QueryParam("ownername") String ownerName, @QueryParam("total") String total) {
        CategoryBusiness cb = new CategoryBusiness();
        TypeBusiness tyb = new TypeBusiness();
        TaskBusiness tb = new TaskBusiness();
        UserBusiness ub = new UserBusiness();
        JSONObject jo = new JSONObject();
        int taskId = 0;
        Date sDate;
        Date eDate;
        Task task = null;
        float taskTotal;
        DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
        if (name != null && startDate != null && endDate != null && total != null && ownerName != null
                && categoryName != null && typeName != null) {

            try {
                sDate = df.parse(startDate);
                eDate = df.parse(endDate);
            } catch (ParseException ex) {
                Logger.getLogger(TaskServices.class.getName()).log(Level.SEVERE, null, ex);
                sDate = null;
                eDate = null;
            }
            try {
                taskTotal = Float.parseFloat(total);
            } catch (Exception ex) {
                taskTotal = 0;
            }

            if (taskTotal != 0 && sDate != null && eDate != null) {
                Category category = cb.getCategoryByName(categoryName);
                Type type = tyb.getTypebyName(typeName);
                Users owner = ub.viewUser(ownerName);
                if (category != null && type != null && owner != null) {
                    task = new Task(name, description, category, type, sDate, eDate, taskTotal, owner, null);
                }
            }
        }
        if (task != null) {
            taskId = tb.addTask(task);
        }

        try {
            jo.put("id", taskId);
        } catch (JSONException ex) {
            Logger.getLogger(Registration.class.getName()).log(Level.SEVERE, null, ex);
        }
        return jo;
    }

    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/createmilestone")
    public JSONObject createMilestone(@QueryParam("name") String name, @QueryParam("description") String description,
            @QueryParam("startdate") String startDate, @QueryParam("enddate") String endDate,
            @QueryParam("parent_id") String parentTask, @QueryParam("total") String total) {
        JSONObject json = new JSONObject();
        TaskBusiness tb = new TaskBusiness();
        Task parent;
        int milestoneId = 0;
        Date sDate;
        Date eDate;
        Task milestone = null;
        float milestoneTotal;
        DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
        if (name != null && description != null && startDate != null && endDate != null
                && parentTask != null && total != null) {
            
            parent = tb.getTaskByName(parentTask);
            try {
                sDate = df.parse(startDate);
                eDate = df.parse(endDate);
            } catch (ParseException ex) {
                Logger.getLogger(TaskServices.class.getName()).log(Level.SEVERE, null, ex);
                sDate = null;
                eDate = null;
            }
            try {
                milestoneTotal = Float.parseFloat(total);
            } catch (Exception ex) {
                milestoneTotal = 0;
            }
            if (milestoneTotal != 0 && sDate != null && eDate != null && parent != null) {
                Category category = parent.getCategoryId();
                Type type = parent.getTypeId();
                Users owner = parent.getOwnerId();
                milestone = new Task(name, description, category, type, sDate, eDate, milestoneTotal, owner, parent);
            }
        }
        if (milestone != null) {
            milestoneId = tb.addTask(milestone);
        }

        try {
            json.put("id", milestoneId);
        } catch (JSONException ex) {
            Logger.getLogger(Registration.class.getName()).log(Level.SEVERE, null, ex);
        }
        return json;
    }

    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/delete")
    public JSONObject deleteTask(@QueryParam("owner") String owner, @QueryParam("taskname") String name) {
        TaskBusiness tb = new TaskBusiness();
        boolean deleted = tb.deleteTask(owner, name);
        JSONObject json = new JSONObject();
        try {
            json.put("deleted", deleted);
        } catch (JSONException ex) {
            Logger.getLogger(TaskServices.class.getName()).log(Level.SEVERE, null, ex);
        }
        return json;
    }

    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/adduser")
    public JSONObject addUsers(@QueryParam("owner") String owner, @QueryParam("user") String user, @QueryParam("task") String task) {
        UserBusiness ub = new UserBusiness();
        TaskBusiness tb = new TaskBusiness();
        JSONObject json = new JSONObject();
        String added = "not_added";
        if (user != null && task != null && owner != null) {
            Users o = ub.viewUser(owner);
            Users u = ub.viewUser(user);
            Task t = tb.getTaskByName(task);
            if (tb.assignUserToTask(o, t, u)) {
                added = "added";
            }
        }
        try {
            json.put("result", added);
        } catch (JSONException ex) {
            Logger.getLogger(TaskServices.class.getName()).log(Level.SEVERE, null, ex);
        }
        return json;
    }

    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/removeuser")
    public JSONObject removeUser(@QueryParam("owner") String owner, @QueryParam("user") String user, @QueryParam("task") String task) {
        UserBusiness ub = new UserBusiness();
        TaskBusiness tb = new TaskBusiness();
        JSONObject json = new JSONObject();
        String removed = "not_removed";
        if (user != null && task != null && owner != null) {
            Users o = ub.viewUser(owner);
            Users u = ub.viewUser(user);
            Task t = tb.getTaskByName(task);
            if (tb.removeUserFromTask(o, t, u)) {
                removed = "removed";
            }
        }
        try {
            json.put("removed", removed);
        } catch (JSONException ex) {
            Logger.getLogger(TaskServices.class.getName()).log(Level.SEVERE, null, ex);
        }
        return json;
    }

    @GET
    @Path("/usertasks")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public JSONArray getUserTasks(@QueryParam("name") String name) {
        JSONArray json = new JSONArray();
        TaskBusiness tb = new TaskBusiness();
        List<Task> tasks = tb.getUserTasks(name);
        for (int i = 0; i < tasks.size(); i++) {
            JSONObject jo = new JSONObject();
            Task task = (Task) tasks.get(i);
            try {
                jo.put("name", task.getName());
                jo.put("description", task.getDescription());
                jo.put("category", task.getCategoryId().getName());
                jo.put("startdate", task.getStartDate());
                jo.put("enddate", task.getEndDate());
                jo.put("type", task.getTypeId().getName());
//                jo.put("evaluation", task.getEvaluation());
//                jo.put("progress", task.getProgress());
                json.put(jo);
            } catch (JSONException ex) {
                Logger.getLogger(TaskServices.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return json;
    }

    @GET
    @Path("/ownertasks")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public JSONArray getOwnerTasks(@QueryParam("name") String name) {
        JSONArray json = new JSONArray();
        TaskBusiness tb = new TaskBusiness();
        List<Task> tasks = tb.getOwnerTasks(name);
        for (int i = 0; i < tasks.size(); i++) {
            JSONObject jo = new JSONObject();
            Task task = (Task) tasks.get(i);
            try {
                jo.put("name", task.getName());
                jo.put("description", task.getDescription());
                jo.put("category", task.getCategoryId().getName());
                jo.put("startdate", task.getStartDate());
                jo.put("enddate", task.getEndDate());
                jo.put("type", task.getTypeId().getName());
//                jo.put("evaluation", task.getEvaluation());
//                jo.put("progress", task.getProgress());
                json.put(jo);
            } catch (JSONException ex) {
                Logger.getLogger(TaskServices.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return json;
    }

    @GET
    @Path("/milestones")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public JSONArray getMilestones(@QueryParam("task_name") String name) {
        JSONArray json = new JSONArray();
        TaskBusiness tb = new TaskBusiness();
        List<Task> milestones = tb.getTaskMilestones(name);
        System.out.println("milestoneis is null");
        if (milestones != null) {
            System.out.println("milestoneis not null");
            for (int i = 0; i < milestones.size(); i++) {

                JSONObject jo = new JSONObject();
                Task milestone = milestones.get(i);
                try {
                    jo.put("name", milestone.getName());
                    jo.put("description", milestone.getDescription());
                    jo.put("startdate", milestone.getStartDate());
                    jo.put("enddate", milestone.getEndDate());
//                    jo.put("evaluation", milestone.getEvaluation());
//                    jo.put("progress", milestone.getProgress());
                    json.put(jo);
                } catch (JSONException ex) {
                    Logger.getLogger(TaskServices.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return json;
    }
    
    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/approveachievement")
    public JSONObject approveAchievement(@QueryParam("user") String user, @QueryParam("task") String task, 
            @QueryParam("achievement") String approval) {
        JSONObject json = new JSONObject();
        if(user != null && task != null && approval != null) {
            TaskBusiness tb = new TaskBusiness();
            tb.approveAchievement(user, task, approval);
        }
        try {
            json.put("approval", approval);
        } catch (JSONException ex) {
            Logger.getLogger(TaskServices.class.getName()).log(Level.SEVERE, null, ex);
        }
        return json;
    }
}
