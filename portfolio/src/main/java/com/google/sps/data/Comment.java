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

package com.google.sps.data;

/** An item on a todo list. */
public final class Comment {

  private long id;
  private String user;
  private String text;
  private long timestamp;
  private long likes;
  private long dislikes;
  private String house;

  public void setID(long id) {
    this.id = id;
  }
  
  public void setUser(String user) {
    this.user = user;
  }

  public void setText(String text) {
    this.text = text;
  }

  public void setTimestamp(long timestamp) {
    this.timestamp = timestamp;
  }

  public void setLikes(long likes) {
    this.likes = likes;
  }

  public void setDislikes(long dislikes) {
    this.dislikes = dislikes;
  }
  
  public void setHouse(String house) {
    this.house = house;
  }
}
