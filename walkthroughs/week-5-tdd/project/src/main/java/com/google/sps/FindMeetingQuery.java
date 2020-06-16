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

package com.google.sps;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

public final class FindMeetingQuery {
  public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {
    // events that have 1 or more attendees of query event attending them
    List<Event> personRelevantEvents = Arrays.asList();
    personRelevantEvents = new ArrayList<>(personRelevantEvents);
    // fill personRelevantEvents
    for (Event e : events) {
      for (String p : e.getAttendees()) {
        if (request.getAttendees().contains(p)) {
          personRelevantEvents.add(e);
          break;
        }
      }
    }
    
    List<TimeRange> finalTimes = Arrays.asList();
    finalTimes = new ArrayList<>(finalTimes);

    personRelevantEvents.sort(Event.ORDER_BY_START);
    
    // remove events that are contained within others
    List<Event> toDelete = new ArrayList<Event>();
    for (int i = 0; i < personRelevantEvents.size() - 1; i++) {
      if (personRelevantEvents.get(i).getWhen().contains(personRelevantEvents.get(i+1).getWhen())) {
        toDelete.add(personRelevantEvents.get(i+1));
      }
    }
    for (Event e : toDelete) {
      personRelevantEvents.remove(e);
    }
    
    // beginning of the period we are going to check for meeting availability
    int start = TimeRange.START_OF_DAY;
    // end of the period we are going to check for meeting availability
    int end;
    int difference;
    // the amount of time the meeting we're trying to schedule needs
    int duration = (int) request.getDuration();
    // check between events for enough space for the meeting
    for (Event e : personRelevantEvents) {
      end = e.getWhen().start();
      System.out.println("Checking: " + start + " - " + end);
      difference = end - start;
      if (difference >= duration) {
        finalTimes.add(TimeRange.fromStartDuration(start, difference));
      }
      start = e.getWhen().end();
    }
    // check between end of last event and end of the day
    end = TimeRange.END_OF_DAY;
    difference = end - start;
    if (difference >= duration) finalTimes.add(TimeRange.fromStartEnd(start, end, true));
    System.out.println(finalTimes);
    return finalTimes;
  }
}
