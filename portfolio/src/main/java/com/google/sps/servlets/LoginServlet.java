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

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    response.setContentType("text/html");
    PrintWriter out = response.getWriter();

    UserService userService = UserServiceFactory.getUserService();
    if (userService.isUserLoggedIn()) {
      // create navbar icon
      String userEmail = userService.getCurrentUser().getEmail();
      // by default identify user by their email
      String user = userEmail;
      String nextURL = request.getParameter("url");
      String urlToRedirectToAfterUserLogsOut = 
          String.format("/%1$s.html" , nextURL);
      String logoutUrl =
          userService.createLogoutURL(urlToRedirectToAfterUserLogsOut);

      // If user has not set a nickname, redirect to nickname page
      String nickname = 
          NicknameServlet.getUserNickname(userService.getCurrentUser().getUserId());
      if (nickname == "") {
        response.sendRedirect(String.format("/nickname?url=%1$s", nextURL));
        return;
      } else {
        // if they have, use it to identify them
        user = nickname;
      }

      out.println(String.format("<button class=\"dropbtn\">%1$s</button>",
          user));
      out.println("<div class=\"dropdown-content\">");
      out.println(String.format("<a href=\"%1$s\">Logout</a>", logoutUrl));
      out.println("</div>");

    } else {
      // create navbar icon
      String urlToRedirectToAfterUserLogsIn = 
          String.format("/%1$s.html" , request.getParameter("url"));
      String loginUrl =
          userService.createLoginURL(urlToRedirectToAfterUserLogsIn);

      response.getWriter().println(String.format("<li><a "
          + "href=\"%1$s\">Login</a></li>", loginUrl));
    }
  }
}
