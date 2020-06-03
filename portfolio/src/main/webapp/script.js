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

/**
 * Adds a random greeting to the page.
 */
function addRandomGreeting() {
  const greetings =
      ['Hello world!', '¡Hola Mundo!', '你好，世界！', 'Bonjour le monde!'];

  // Pick a random greeting.
  const greeting = greetings[Math.floor(Math.random() * greetings.length)];

  // Add it to the page.
  const greetingContainer = document.getElementById('greeting-container');
  greetingContainer.innerText = greeting;
}

/**
 * Adds a random movie recommendation to the page.
 */
function addRandomMovie() {
  const movies =
      ['Searching (2018)', 'The Avengers (2012)',
      'Spider-Man: Homecoming (2017)', 'Your Name (2016)',
      'Knives Out (2019)', 'La La Land (2016)', 'Ant-Man (2015)',
      'Kingsman: The Secret Service (2014)', 'Iron Man (2008)',
      'Coco (2017)', 'Ratatouille (2007)', 'Get Out (2017)',
      'Jojo Rabbit (2019)', 'The Big Sick (2017)'];

  // Pick a random movie.
  const movie = movies[Math.floor(Math.random() * movies.length)];

  // Add it to the page.
  const movieContainer = document.getElementById('movie-container');
  movieContainer.innerText = movie;
}

function setCookie(cname, cvalue) {
  document.cookie = cname + "=" + cvalue + "; ";
}

/**
 * Gets value of the cookie cname
 */
function getCookie(cname) {
  var name = cname + "=";
  var decodedCookie = decodeURIComponent(document.cookie);
  var cArr = decodedCookie.split(';');
  for(var i = 0; i < cArr.length; i++) {
    var c = cArr[i];
    while (c.charAt(0) == ' ') {
      c = c.substring(1);
    }
    if (c.indexOf(name) == 0) {
      return c.substring(name.length, c.length);
    }
  }
  return "";
}

/**
 * Updates navbar with house based off cookie
 */
function checkCookie() {
  var house = toStringHouse(getCookie("house"));
  var elements = document.getElementsByClassName("house-navbar");
  elements[0].id = house.toLowerCase();
  elements[0].innerText = house;
}

/**
 * Turns letter tag into corresponding house name
 */
function toStringHouse(tag) {
  output = "";
  if (tag != "") {
    switch(tag) {
      case 'g':
        output = "Gryffindor";
        break;
      case 'h':
        output = "Hufflepuff";
        break;
      case 'r':
        output = "Ravenclaw";
        break;
      case 's':
        output = "Slytherin";
        break;
    }
  }
  return output;
}

var form = document.querySelector("form");

/**
 * Triggered when user submits house quiz
 */
form.addEventListener("submit", function(event) {
  var data = new FormData(form);
  var output = "";
  for (const entry of data) {
    output = entry[1];
  };

  setCookie("house", output);

  // print message
  const houseContainer = document.getElementById('house-container');
  houseContainer.innerText = "You are a " + toStringHouse(output) + "!";
  
  // update navbar
  checkCookie();
  
  event.preventDefault();
}, false);

async function getComments() {
  const response = await fetch('/data' + '?' + 'num-comments=' +
      document.getElementById('num-comments').value);
  const comments = await response.json();
  const commentsSectionElement = document.getElementById('comments-section');
  commentsSectionElement.innerHTML = '';
  comments.forEach(function(comm) {
    commentsSectionElement.appendChild(
        createListElement(comm.text + ", " + comm.timestamp));
  })
}

async function deleteComments() {
  const response = await fetch('/delete-data', {method: 'POST'});
  getComments();
}

/** Creates an <li> element containing text. */
function createListElement(text) {
  const liElement = document.createElement('li');
  liElement.innerText = text;
  return liElement;
}
