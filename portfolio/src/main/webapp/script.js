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

var form = document.querySelector("form");

/* form.addEventListener("submit", function(event) {
  var data = new FormData(form);
  var output = "";
  for (const entry of data) {
    output = output + entry[0] + "=" + entry[1] + "\r";
  };
  log.innerText = output;
  event.preventDefault();
}, false); */


function setCookie(cname, cvalue) {
  document.cookie = cname + "=" + cvalue; //+ ";path=/";
}

function getCookie(cname) {
  var name = cname + "=";
  var decodedCookie = decodeURIComponent(document.cookie);
  var ca = decodedCookie.split(';');
  for(var i = 0; i <ca.length; i++) {
    var c = ca[i];
    while (c.charAt(0) == ' ') {
      c = c.substring(1);
    }
    if (c.indexOf(name) == 0) {
      return c.substring(name.length, c.length);
    }
  }
  return "";
}

function checkCookie() {
  var house = toStringHouse(getCookie("house"));
  console.log("House: " + house);
  var elements = document.getElementsByClassName("house-navbar");
  elements[0].id = house.toLowerCase();
  elements[0].innerText = house;
}

function toStringHouse(tag) {
  output = "";
  if (tag != "") {
    switch(tag) {
      case 'g':
        output = "Gryffindor"
        break;
      case 'h':
        output = "Hufflepuff"
        break;
      case 'r':
        output = "Ravenclaw"
        break;
      case 's':
        output = "Slytherin"
        break;
    }
  }
  return output;
}

form.addEventListener("submit", function(event) {
  var data = new FormData(form);
  var output = "";
  for (const entry of data) {
    output = output + entry[0] + "=" + entry[1] + "\r";
    setCookie("house", entry[1]);
    const houseContainer = document.getElementById('house-container');
    houseContainer.innerText = "You are a " + toStringHouse(entry[1]) + "!";
  };
  console.log(output)
  checkCookie();
  event.preventDefault();
}, false);

/*function loadImage() {
  alert("Image is loaded");
}*/