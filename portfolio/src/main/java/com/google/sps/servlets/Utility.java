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
import javax.servlet.http.HttpServletRequest;


public class Utility {

  /**
   * Converts an Object instance into a JSON string using the Gson library.
   */
  public static String convertToJson(Object o) {
    Gson gson = new Gson();
    String json = gson.toJson(o);
    return json;
  }

  /**
   * @return the request parameter, or the default value if the parameter
   *         was not specified by the client
   */
  public static String getParameter(HttpServletRequest request, String name, String defaultValue) {
    String value = request.getParameter(name);
    if (value == null) {
      return defaultValue;
    }
    return value;
  }
}
