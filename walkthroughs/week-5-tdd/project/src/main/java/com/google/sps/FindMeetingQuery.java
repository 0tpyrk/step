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
    List<Event> sortedRelevantEvents = new ArrayList<Event>();
    // sort personRelevantEvents
    while (!personRelevantEvents.isEmpty()) {
      Event e = personRelevantEvents.get(0);
      for (int i = 0; i < personRelevantEvents.size(); i++) {
        // if event starts earlier than the earliest found so far
        if (TimeRange.ORDER_BY_START.compare(e.getWhen(), personRelevantEvents.get(i).getWhen()) > 0) {
          e = personRelevantEvents.get(i);
        // if equal starts, compare ending times
        } else if (TimeRange.ORDER_BY_START.compare(e.getWhen(), personRelevantEvents.get(i).getWhen()) == 0) {
          if (TimeRange.ORDER_BY_END.compare(e.getWhen(), personRelevantEvents.get(i).getWhen()) > 0) {
            e = personRelevantEvents.get(i);
          }
        }
      }
      sortedRelevantEvents.add(e);
      personRelevantEvents.remove(e);
    }

    // remove events that are contained within others
    List<Event> toDelete = new ArrayList<Event>();
    for (int i = 0; i < sortedRelevantEvents.size() - 1; i++) {
      if (sortedRelevantEvents.get(i).getWhen().contains(sortedRelevantEvents.get(i+1).getWhen())) {
        toDelete.add(sortedRelevantEvents.get(i+1));
      }
    }
    for (Event e : toDelete) {
      sortedRelevantEvents.remove(e);
    }

    // test if sort works
    System.out.println("Beginning");
    for (Event e : sortedRelevantEvents) {
        System.out.println(e.getWhen().start());
    }

    int start = TimeRange.START_OF_DAY;
    int end;
    int difference;
    int duration = (int) request.getDuration();
    // check between events for enough space for the meeting
    for (Event e : sortedRelevantEvents) {
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
