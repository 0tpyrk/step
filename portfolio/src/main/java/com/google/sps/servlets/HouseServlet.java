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

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.Query.FilterOperator;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.logging.Logger;
import java.util.logging.Level;

@WebServlet("/house")
public class HouseServlet extends HttpServlet {

  /** 
   *  Returns the house of the given user
   */
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    response.setContentType("text/html");
    PrintWriter out = response.getWriter();

    UserService userService = UserServiceFactory.getUserService();
    if (userService.isUserLoggedIn()) {
      String userID = userService.getCurrentUser().getUserId();
    
      FilterPredicate filter = new FilterPredicate("id", 
          Query.FilterOperator.EQUAL, userID);
      Query userQuery = new Query("User");
      userQuery.setFilter(filter);

      DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

      // PreparedQuery that has the user who liked the comment inside it
      PreparedQuery userResults = datastore.prepare(userQuery);

      // Create Logger for warning reporting
      Logger logger = Logger.getLogger(HouseServlet.class.getName());
      logger.setLevel(Level.WARNING); 

      for (Entity user : userResults.asIterable()) {
        Object input = user.getProperty("house");
        String house = "";
        if (input instanceof String) {
          house = input.toString();
        } else {
          logger.warning("Could not convert User's house to String"); 
        }

        out.println(house);
      }
    }
  }

  /** 
   *  Sets the house for a given user
   */
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    UserService userService = UserServiceFactory.getUserService();
    if (!userService.isUserLoggedIn()) {
      return;
    } else {
      String userID = userService.getCurrentUser().getUserId();
      String newHouse = request.getParameter("house");
    
      FilterPredicate filter = new FilterPredicate("id", 
          Query.FilterOperator.EQUAL, userID);
      Query userQuery = new Query("User");
      userQuery.setFilter(filter);

      DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

      // PreparedQuery that has the user who liked the comment inside it
      PreparedQuery userResults = datastore.prepare(userQuery);

      // Create Logger for warning reporting
      Logger logger = Logger.getLogger(HouseServlet.class.getName());
      logger.setLevel(Level.WARNING); 

      for (Entity user : userResults.asIterable()) {
        Object input = user.getProperty("house");
        String house = "";
        if (input instanceof String) {
          house = input.toString();
        } else {
          logger.warning("Could not convert User's house to String"); 
        }

        user.setProperty("house", newHouse);
        datastore.put(user);
      }
    }
  }
}
