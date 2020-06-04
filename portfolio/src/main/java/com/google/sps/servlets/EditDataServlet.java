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
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
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
    
    Query query = new Query("Comment", key);

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

    // PreparedQuery that contains all the comments inside it
    PreparedQuery results = datastore.prepare(query);

    // Create Logger for warning reporting
    Logger logger = Logger.getLogger(EditDataServlet.class.getName());
    logger.setLevel(Level.WARNING); 

    for (Entity entity : results.asIterable()) {
      if (type.equals("like")) {
        Object likes = entity.getProperty("likes");
        if (likes instanceof Long) {
          entity.setProperty("likes", ((long) likes) + 1);
        }
        else {
          logger.warning("Could not convert Entity's likes to long"); 
        }
      }
      else if (type.equals("dislike")) {
        Object dislikes = entity.getProperty("dislikes");
        if (dislikes instanceof Long) {
          entity.setProperty("dislikes", ((long) dislikes) + 1);
        }
        else {
          logger.warning("Could not convert Entity's dislikes to long"); 
        }
      }
      datastore.put(entity);
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
