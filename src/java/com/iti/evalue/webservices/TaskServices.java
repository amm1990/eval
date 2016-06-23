
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
import com.iti.evalue.entities.UsersTask;
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
        String date_validations = "valid";
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
                if (eDate.before(parent.getEndDate())) {
                    //for (int i = 0; i < parent.getTaskList().size(); i++) {
                        
                        Category category = parent.getCategoryId();
                        Type type = parent.getTypeId();
                        Users owner = parent.getOwnerId();
                        milestone = new Task(name, description, category, type, sDate, eDate, milestoneTotal, owner, parent);
                    //}
                } else {
                    date_validations = "enddate";
                }
            }
        }
        if (milestone != null) {
            milestoneId = tb.addTask(milestone);
        }

        try {
            json.put("id", milestoneId);
            json.put("valid_dates", date_validations);
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
    public JSONObject addUsers(@QueryParam("owner") String owner, @QueryParam("user") String user,
            @QueryParam("task") String task) {
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
    public JSONObject removeUser(@QueryParam("owner") String owner, @QueryParam("user") String user,
            @QueryParam("task") String task) {
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
        List<UsersTask> uts = tb.getUserTeamAndParentTasks(name);
        for (int i = 0; i < uts.size(); i++) {
            JSONObject jo = new JSONObject();
            Task task = uts.get(i).getTaskId();

            try {
                jo.put("name", task.getName());
                jo.put("description", task.getDescription());
                jo.put("category", task.getCategoryId().getName());
                jo.put("startdate", task.getStartDate());
                jo.put("enddate", task.getEndDate());
                jo.put("type", task.getTypeId().getName());
                jo.put("total", task.getTotal());
                jo.put("approval", uts.get(i).getApproval());
                jo.put("achievement", uts.get(i).getAchievement());
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
        if (name != null) {
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
                    jo.put("total", task.getTotal());
                    json.put(jo);
                } catch (JSONException ex) {
                    Logger.getLogger(TaskServices.class.getName()).log(Level.SEVERE, null, ex);
                }
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
        if (milestones != null) {
            
            for (int i = 0; i < milestones.size(); i++) {
                JSONObject jo = new JSONObject();
                Task milestone = milestones.get(i);
                String inputStart = milestone.getStartDate().toString();
                String outputStart = inputStart.substring(0, 10);
                String inputEnd = milestone.getEndDate().toString();
                String outputEnd = inputEnd.substring(0, 10);
                System.out.println(outputStart + " " + outputEnd);
                try {
                    jo.put("name", milestone.getName());
                    jo.put("description", milestone.getDescription());
                    jo.put("startdate", outputStart);
                    jo.put("enddate", milestone.getEndDate());
                    jo.put("total", outputEnd);
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
    public JSONObject approveAchievement(@QueryParam("user") String user, @QueryParam("milestone") String task,
            @QueryParam("approval") String approval) {
        JSONObject json = new JSONObject();
        if (user != null && task != null && approval != null) {
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

    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/approvejoiningtask")
    public JSONObject joinTask(@QueryParam("user") String user, @QueryParam("task") String task,
            @QueryParam("approval") String approval) {
        JSONObject json = new JSONObject();
        if (user != null && task != null && approval != null) {
            TaskBusiness tb = new TaskBusiness();
            tb.approveJoinTask(user, task, approval);
            try {
                json.put("approval", approval);
            } catch (JSONException ex) {
                Logger.getLogger(TaskServices.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return json;
    }

    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/taskbytype")
    public JSONArray selectTaskByType(@QueryParam("user") String user, @QueryParam("type") String type) {
        JSONArray json = new JSONArray();
        if (user != null && type != null) {
            TaskBusiness tb = new TaskBusiness();
            List<Task> tasks = tb.selectTasksByType(user, type);
            if (tasks != null) {
                for (int i = 0; i < tasks.size(); i++) {
                    JSONObject jo = new JSONObject();
                    Task task = tasks.get(i);
                    try {
                        jo.put("name", task.getName());
                        jo.put("description", task.getDescription());
                        jo.put("category", task.getCategoryId().getName());
                        jo.put("type", task.getTypeId().getName());
                        jo.put("start_date", task.getStartDate());
                        jo.put("end_date", task.getEndDate());
                        jo.put("total", task.getTotal());
                        json.put(jo);
                    } catch (JSONException ex) {
                        Logger.getLogger(TaskServices.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
        return json;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/allusertasks")
    public JSONArray getAllUserTasks(@QueryParam("user") String user) {
        JSONArray json = new JSONArray();
        if (user != null) {
            TaskBusiness tb = new TaskBusiness();
            List<Task> tasks = tb.selectTasksAllForUser(user);
            if (tasks != null) {
                for (int i = 0; i < tasks.size(); i++) {
                    JSONObject jo = new JSONObject();
                    Task task = tasks.get(i);
                    String role = tb.checkRole(task, user);
                    try {
                        jo.put("name", task.getName());
                        jo.put("description", task.getDescription());
                        jo.put("category", task.getCategoryId().getName());
                        jo.put("type", task.getTypeId().getName());
                        jo.put("startdate", task.getStartDate());
                        jo.put("enddate", task.getEndDate());
                        jo.put("total", task.getTotal());
                        jo.put("role", role);
                        json.put(jo);
                    } catch (JSONException ex) {
                        Logger.getLogger(TaskServices.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
        return json;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/general_statistics")
    public JSONArray displayAllTasksStatistics(@QueryParam("user") String user) {
        JSONArray json = new JSONArray();
        TaskBusiness tb = new TaskBusiness();
        List<UsersTask> assignments = tb.getUserTasks(user);
        for (int i = 0; i < assignments.size(); i++) {
            JSONObject jo = new JSONObject();
            try {
                jo.put("task", assignments.get(i).getTaskId().getName());
                jo.put("total", assignments.get(i).getTaskId().getTotal());
                jo.put("achievement", assignments.get(i).getAchievement());
                json.put(jo);
            } catch (JSONException ex) {
                Logger.getLogger(TaskServices.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return json;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/taskusers")
    public JSONArray getTaskUsers(@QueryParam("taskname") String task) {
        JSONArray json = new JSONArray();
        TaskBusiness tb = new TaskBusiness();
        List<Users> users = tb.getTaskUsers(task);
        for (int i = 0; i < users.size(); i++) {
            Users u = users.get(i);
            JSONObject jo = new JSONObject();
            try {
                jo.put("name", u.getName());
                jo.put("email", u.getEmail());
                jo.put("gender", u.getGender());
                json.put(jo);
            } catch (JSONException ex) {
                Logger.getLogger(TaskServices.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return json;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/taskbyname")
    public JSONObject getTaskByName(@QueryParam("task") String name) {

        JSONObject jo = new JSONObject();
        TaskBusiness tb = new TaskBusiness();
        Task task = tb.getTaskByName(name);
        try {
            jo.put("name", task.getName());
            jo.put("description", task.getDescription());
            jo.put("startdate", task.getStartDate());
            jo.put("enddate", task.getEndDate());
            jo.put("category", task.getCategoryId().getName());
            jo.put("type", task.getTypeId().getName());
            jo.put("total", task.getTotal());
            jo.put("owner", task.getOwnerId().getName());

        } catch (JSONException ex) {
            Logger.getLogger(TaskServices.class.getName()).log(Level.SEVERE, null, ex);
        }
        return jo;
    }
}
