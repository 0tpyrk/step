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
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.sps.data.Comment;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.Long;
import java.util.logging.Logger;
import java.util.logging.Level;

/** Servlet that edits comment data */
@WebServlet("/edit-data")
public class EditDataServlet extends HttpServlet {

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    Key key = KeyFactory.createKey("Comment", Long.parseLong(request.getParameter("key")));
    String type = request.getParameter("type");
    long userID = Long.parseLong(request.getParameter("id"));
    
    Query keyQuery = new Query("Comment", key);
    FilterPredicate filter = new FilterPredicate("id", Query.FilterOperator.EQUAL, userID);
    Query idQuery = new Query("ID");
    idQuery.setFilter(filter);

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

    // PreparedQuery that contains all the comments inside it
    PreparedQuery commResults = datastore.prepare(keyQuery);
    // PreparedQuery that has the user who liked the comment inside it
    PreparedQuery userResults = datastore.prepare(idQuery);

    // Create Logger for warning reporting
    Logger logger = Logger.getLogger(EditDataServlet.class.getName());
    logger.setLevel(Level.WARNING); 

    for (Entity comment : commResults.asIterable()) {
      // Get comment's id
      Object input = comment.getKey().getId();
      long commID = 0;
      if (input instanceof Long) {
        commID = (long) input;
      }
      else {
        logger.warning("Could not convert comment's ID to long"); 
      }

      for (Entity user : userResults.asIterable()) {
        ArrayList<Long> userLikes = null;
        Object objUserLikes = user.getProperty("likes");
        if (objUserLikes instanceof ArrayList) {
            userLikes = (ArrayList<Long>) objUserLikes;
        }
        else {
            logger.warning("Could not convert User's likes list to ArrayList<Long>"); 
        }

        // check to make sure user's list of liked comments doesn't have this comment inside of it
        if (!userLikes.contains(commID)) {
          
          if (type.equals("like")) {
            Object likes = comment.getProperty("likes");
            if (likes instanceof Long) {
              comment.setProperty("likes", ((long) likes) + 1);
            }
            else {
              logger.warning("Could not convert comment's likes to long"); 
            }
          }
          else if (type.equals("dislike")) {
            Object dislikes = comment.getProperty("dislikes");
            if (dislikes instanceof Long) {
              comment.setProperty("dislikes", ((long) dislikes) + 1);
            }
            else {
              logger.warning("Could not convert comment's dislikes to long"); 
            }
          }
          userLikes.add(commID);
          user.setProperty("likes", userLikes);
          datastore.put(user);
          datastore.put(comment);
        }
      }
    }
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
