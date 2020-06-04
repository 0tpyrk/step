// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps.servlets;

import java.io.IOException;
import com.google.gson.Gson;
import java.util.List;
import java.util.ArrayList;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.sps.data.Comment;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.Long;
import java.util.logging.Logger;
import java.util.logging.Level;

/** Servlet that returns comments data */
@WebServlet("/data")
public class DataServlet extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    Query query = new Query("Comment").addSort("timestamp", SortDirection.DESCENDING);

    int numComments = getNumComments(request);

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

    // PreparedQuery that contains all the comments inside it
    PreparedQuery results = datastore.prepare(query);

    // Create Logger for warning reporting
    Logger logger = Logger.getLogger(DataServlet.class.getName());
    logger.setLevel(Level.WARNING); 

    List<Comment> comments = new ArrayList<>();
    int count = 0;
    for (Entity entity : results.asIterable()) {
      // >= in order to count starting from 1 instead of 0
      if (count >= numComments) break;
  
      Object input = entity.getKey().getId();
      long id = 0;
      if (input instanceof Long) {
        id = (long) input;
      }
      else {
        logger.warning("Could not convert Entity's ID to long"); 
      }

      String user = entity.getProperty("user").toString();
      String text = entity.getProperty("text").toString();

      input = entity.getProperty("timestamp");
      long timestamp = 0;
      if (input instanceof Long) {
        timestamp = (long) input;
      }
      else {
        logger.warning("Could not convert Entity's timestamp to long"); 
      }

      input = entity.getProperty("likes");
      long likes = 0;
      if (input instanceof Long) {
        likes = (long) input;
      }
      else {
        logger.warning("Could not convert Entity's likes to long"); 
      }

      input = entity.getProperty("dislikes");
      long dislikes = 0;
      if (input instanceof Long) {
        dislikes = (long) input;
      }
      else {
        logger.warning("Could not convert Entity's dislikes to long"); 
      }
      
      Comment comment = new Comment(id, user, text, timestamp, likes, dislikes);
      comments.add(comment);
      
      count++;
    }

    String json = convertToJson(comments);

    response.setContentType("application/json;");
    response.getWriter().println(json);
  }

  /** Returns the number of comments to display entered by the user, or -1 if the choice was invalid. */
  private int getNumComments(HttpServletRequest request) {
    // Get the input from the form.
    String numCommentsString = request.getParameter("num-comments");

    // Convert the input to an int.
    int numComments;
    try {
      numComments = Integer.parseInt(numCommentsString);
    } catch (NumberFormatException e) {
      System.err.println("Could not convert to int: " + numCommentsString);
      return -1;
    }

    return numComments;
  }

  /**
   * Converts a List instance into a JSON string using the Gson library.
   */
  private String convertToJson(List arr) {
    Gson gson = new Gson();
    String json = gson.toJson(arr);
    return json;
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    // Get the input from the form.
    String user = getParameter(request, "username", "");
    String text = getParameter(request, "text-input", "");

    long timestamp = System.currentTimeMillis();

    Entity commentEntity = new Entity("Comment");
    commentEntity.setProperty("user", user);
    commentEntity.setProperty("text", text);
    commentEntity.setProperty("timestamp", timestamp);
    commentEntity.setProperty("likes", 0);
    commentEntity.setProperty("dislikes", 0);

    // Add comment to datastore
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    datastore.put(commentEntity);

	// Refresh the page
    response.sendRedirect("/index.html");
  }

  /**
   * @return the request parameter, or the default value if the parameter
   *         was not specified by the client
   */
  private String getParameter(HttpServletRequest request, String name, String defaultValue) {
    String value = request.getParameter(name);
    if (value == null) {
      return defaultValue;
    }
    return value;
  }
}
