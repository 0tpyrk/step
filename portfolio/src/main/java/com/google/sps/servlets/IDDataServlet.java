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
import com.google.appengine.api.datastore.FetchOptions;
import com.google.sps.data.Comment;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.Long;
import java.util.logging.Logger;
import java.util.logging.Level;

/** Servlet that returns id data */
@WebServlet("/id")
public class IDDataServlet extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    Query query = new Query("ID").addSort("id", SortDirection.DESCENDING);

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

    // List that contains all the users inside it
    PreparedQuery results = datastore.prepare(query);


    // Create Logger for warning reporting
    Logger logger = Logger.getLogger(IDDataServlet.class.getName());
    logger.setLevel(Level.WARNING); 

    // find what the last used id number is
    long lastID = 0;
    for (Entity entity : results.asIterable(FetchOptions.Builder.withLimit(1))) {
      Object input = entity.getProperty("id");
      if (input instanceof Long) {
        lastID = (long) input;
      }
      else {
        logger.warning("Could not convert Entity's ID to long"); 
      }
    } 
    
    Entity idEntity = new Entity("ID");
    idEntity.setProperty("id", lastID + 1);
    ArrayList<Long> list = new ArrayList<Long>();

    // adding intial value to the list, otherwise entity draws NullPointerException later
    // when taken out of the datastore
    long initVal = 0;
    list.add(initVal);
    idEntity.setUnindexedProperty("likes", list);

    // Add person to datastore
    datastore.put(idEntity);

    String json = convertToJson(idEntity);

    response.setContentType("application/json;");
    response.getWriter().println(json);
  }

  /**
   * Converts a Entity instance into a JSON string using the Gson library.
   */
  private String convertToJson(Entity e) {
    Gson gson = new Gson();
    String json = gson.toJson(e);
    return json;
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
