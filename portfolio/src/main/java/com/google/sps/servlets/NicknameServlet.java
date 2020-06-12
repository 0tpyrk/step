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
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.ArrayList;
import java.util.logging.Logger;
import java.util.logging.Level;

@WebServlet("/nickname")
public class NicknameServlet extends HttpServlet {

  /** 
   *  Displays the form for entering a new nickname
   */
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    response.setContentType("text/html");
    PrintWriter out = response.getWriter();

    UserService userService = UserServiceFactory.getUserService();
    if (userService.isUserLoggedIn()) {
      String nickname = getUserNickname(userService.getCurrentUser().getUserId());
      String nextURL = request.getParameter("url");
      out.println("<div id=\"nick\">");
      out.println("<h4>Set your nickname here:</h4>");
      out.println(String.format("<form method=\"POST\" "
         + "action=\"/nickname?url=%1$s\">", nextURL));
      out.println("<input name=\"nickname\" value=\"" + nickname + "\" />");
      out.println("<br/>");
      out.println("<br/>");
      out.println("<button>Submit</button>");
      out.println("</form>");
      out.println("</div>");
    } else {
      String loginUrl = userService.createLoginURL("/nickname");
      out.println("<p>Login <a href=\"" + loginUrl + "\">here</a>.</p>");
    }
  }

  /** 
   *  Handles responses to nickname form and creates new users 
   *  for the datastore
   */
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    UserService userService = UserServiceFactory.getUserService();
    if (!userService.isUserLoggedIn()) {
      response.sendRedirect("/nickname");
      return;
    }

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
  
    Entity user = userResults.asSingleEntity();

    if (user != null) {
      user.setProperty("nickname", request.getParameter("nickname"));
      datastore.put(user);
    } else {
      String newNickname = request.getParameter("nickname");
      String id = userService.getCurrentUser().getUserId();

      Entity entity = new Entity("User", id);

      List<Long> list = new ArrayList<Long>();
      // adding intial value to the list, otherwise entity draws NullPointerException
      // later when taken out of the datastore
      long initVal = 0;
      list.add(initVal);

      entity.setProperty("id", id);
      entity.setProperty("nickname", newNickname);
      entity.setUnindexedProperty("likes", list);
      entity.setUnindexedProperty("dislikes", list);
      entity.setProperty("house", "");
      // The put() function automatically inserts new data or 
      // updates existing data based on ID
      datastore.put(entity);
    }
    String nextURL = request.getParameter("url");
    response.sendRedirect(String.format("/%1$s.html" , nextURL));
  }

  /**
   * Returns the nickname of the user with id, or empty String if the user has 
   * not set a nickname.
   */
  public static String getUserNickname(String id) {
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    Query query =
        new Query("User")
            .setFilter(new Query.FilterPredicate("id", 
            Query.FilterOperator.EQUAL, id));
    PreparedQuery results = datastore.prepare(query);
    Entity entity = results.asSingleEntity();
    if (entity == null) {
      return "";
    }
    String nickname = entity.getProperty("nickname").toString();
    return nickname;
  }
}
