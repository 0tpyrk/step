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

/**
 * Fetches comments and displays the content, timestamp, likes, and dislikes
 */
async function getComments() {
  const response = await fetch('/data' + '?' + 'num-comments=' +
      document.getElementById('num-comments').value);
  const comments = await response.json();
  const commentsSectionElement = document.getElementById('comments-list');
  commentsSectionElement.innerHTML = '';
  comments.forEach(function(comm) {
    const liElement = document.createElement('li');

    const headerElement = document.createElement('div');
    headerElement.innerHTML = comm.user.bold() + ', ' + getTimeSince(comm.timestamp);
    liElement.appendChild(headerElement);

    const likesElement = createLikesButtons(comm);
    headerElement.appendChild(likesElement);

    const bodyElement = document.createElement('p');
    bodyElement.innerHTML = comm.text;
    liElement.appendChild(bodyElement);

    commentsSectionElement.appendChild(liElement);
  })
}

/**
 * Creates the like and dislike buttons inside their own likesElement
 */
function createLikesButtons(comm) {
  var likesElement = document.createElement('div');
  likesElement.id = 'likes-section';

  // like button
  likesElement.appendChild(document.createTextNode(comm.likes));
  var likeButton = document.createElement("button");
  likeButton.innerHTML = ":)";
  likeButton.id = "like";
  var like = function () {
    editComment(comm.id, "like");
  }
  likeButton.onclick = like;
  likesElement.appendChild(likeButton);

  likesElement.appendChild(document.createTextNode(" "));

  // dislike button
  likesElement.appendChild(document.createTextNode(comm.dislikes));
  var dislikeButton = document.createElement("button");
  dislikeButton.innerHTML = ":(";
  dislikeButton.id = "dislike";
  var dislike = function () {
    editComment(comm.id, "dislike");
  }
  dislikeButton.onclick = dislike;
  likesElement.appendChild(dislikeButton);
  
  return likesElement;
}

async function deleteComments() {
  const response = await fetch('/delete-data', {method: 'POST'});
  getComments();
}

function editComment(key, type) {
  getID().then((response) => {
      fetch('/edit-data' + '?' + 'key=' + key + "&type=" + type + '&id=' + 
      response, {method: 'POST'}).then(() => {
      getComments()})
      });
}

function getTimeSince(time) {
  var currDate = Date.now();
  var commDate = new Date(time);

  // difference in milliseconds
  var msDiff = currDate - commDate;

  // difference in seconds
  var sDiff = msDiff / 1000;

  if (sDiff < 60) {
    if (sDiff < 2) return Math.trunc(sDiff) + " second ago";
    else return Math.trunc(sDiff) + " seconds ago";
  }

  // difference in minutes
  var mDiff = sDiff / 60;
  if (mDiff < 60) {
    if (mDiff < 2) return Math.trunc(mDiff) + " minute ago"
    else return Math.trunc(mDiff) + " minutes ago";
  }

  // difference in hours
  var hDiff = mDiff / 60;
  if (hDiff < 24) {
    if (hDiff < 2) return Math.trunc(hDiff) + " hour ago";
    else return Math.trunc(hDiff) + " hours ago";
  }

  // difference in days
  var dDiff = hDiff / 24;
  if (dDiff < 30) {
    if (dDiff < 2) return Math.trunc(dDiff) + " day ago";
    else return Math.trunc(dDiff) + " days ago";
  }

  return "";
}

async function getID() {
  var id = getCookie('id');
  if (id == '') {
    const response = await fetch('/id');
    const newID = await response.json();
    id = newID.propertyMap.id;
    setCookie('id', id);
  }
  return id;
}

async function getLogin() {
  const response = await fetch('/login');
  const html = await response.text();
  const navbarSlot = document.getElementById('login');
  navbarSlot.innerHTML = html;
}

async function getCommentsForm() {
  const response = await fetch('/form');
  const html = await response.text();
  const comments = document.getElementById('comment-submission');
  comments.innerHTML = html;
}
