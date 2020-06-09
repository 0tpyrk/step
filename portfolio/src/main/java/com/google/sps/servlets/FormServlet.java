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

@WebServlet("/form")
public class FormServlet extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    response.setContentType("text/html;");
    PrintWriter out = response.getWriter();

    UserService userService = UserServiceFactory.getUserService();

    if (userService.isUserLoggedIn()) {
      // If user has not set a nickname, we also can't let them comment
      String nickname =
          NicknameServlet.getUserNickname(userService.getCurrentUser().getUserId());
      if (nickname != "") {
        out.println("<form id=\"comment-submission\" action=\"/data\" "
            + "method=\"POST\">");
        out.println("<br>");
        out.println("<textarea id=\"text-input\" name=\"text-input\" "
            + "placeholder=\"Write comment here.\" required></textarea>");
        out.println("<br>");
        out.println("<input type=\"submit\">");
        out.println("<br>");
        out.println("</form>");
      } else {
        out.println("<p>To leave a comment, you must first set a nickname.</p>");  
      }
    } else {
      String loginUrl = userService.createLoginURL("/");
      out.println(String.format("<p>To leave a comment, login <a href=\"%1$s"
          + "\">here</a>.</p>", loginUrl));
    }
  }
}
