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
 * Updates navbar with house based off datastore
 */
async function getHouse() {
  const response = await fetch('/house');
  const houseID = await response.text();
  var house = toStringHouse(houseID[0]);
  var elements = document.getElementsByClassName("house-navbar");
  elements[0].id = house.toLowerCase();
  elements[0].innerText = house;
}

function setHouse(house) {
  fetch('/house' + '?' + 'house=' + house, {method: 'POST'});
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

  setHouse(output);

  // print message
  const houseContainer = document.getElementById('house-container');
  houseContainer.innerText = "You are a " + toStringHouse(output) + "!";
  
  // update navbar
  getHouse();

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
    headerElement.innerHTML = comm.user.bold();
    // add space between name and house flag
    if (comm.house != '') headerElement.appendChild(document.createTextNode(' '));
    headerElement.appendChild(createHouseElement(comm.house));
    headerElement.appendChild(document.createTextNode(', ' + 
        getTimeSince(comm.timestamp)));
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

/**
 * Creates a formatted flag for a commenter's house
 */
function createHouseElement(houseInput) {
  var house = toStringHouse(houseInput);
  var houseElement = document.createElement('a');
  if (house != '') {
    houseElement.id = house.toLowerCase();
    houseElement.innerHTML = '&nbsp;&nbsp;' + house[0] + '&nbsp;&nbsp;';
  }
  
  return houseElement;
}

/**
 * Deletes all comments
 */
async function deleteComments() {
  const response = await fetch('/delete-data', {method: 'POST'});
  getComments();
}

/**
 * Edits the data of a comment (currently likes or dislikes)
 */
function editComment(key, type) {
  fetch('/edit-data' + '?' + 'key=' + key + "&type=" + type,
      {method: 'POST'}).then(() => {
      getComments()});
}

/**
 * Returns formatted string containing the amount of time since parameter time
 */
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

/**
 * Renders login button/logout dropdown
 */
async function getLogin(url) {
  const response = await fetch('/login' + '?' + 'url=' + url);
  const html = await response.text();
  const navbarSlot = document.getElementById('login');
  navbarSlot.innerHTML = html;
}

/**
 * Renders comments form
 */
async function getCommentsForm() {
  const response = await fetch('/comment-form');
  const html = await response.text();
  const comments = document.getElementById('comment-submission');
  comments.innerHTML = html;
}

/**
 * Location that contains a set of coordinates and information about it
 */
function Location(loc, content) {
  this.loc = loc;
  this.content = content;
}

/**
 * Creates a map of locations
 */
function createMap() {
  // lay out all the locations
  var locArray = [];
  let theboys = locArray.push(
      new Location({lat: 37.750931, lng: -121.954878},
      '<div id="infowindow">'+
      '<h3>San Ramon, CA</h1>'+
      '<a href="images/fullsize/theboys.jpg"><img src="images/resized/theboys_r.jpg"/></a>'+
      '</div>'));
  let hazel = locArray.push(
      new Location({lat: 45.448673, lng:  -122.669502},
      '<div id="infowindow">'+
      '<h3>Platt Hall, Lewis & Clark College - Portland, OR</h1>'+
      '<a href="images/fullsize/hazel.jpg"><img src="images/resized/hazel_r.jpg"/></a>'+
      '</div>'));
  let zach = locArray.push(
      new Location({lat: 45.448670, lng: -122.669518},
      '<div id="infowindow">'+
      '<h3>Platt Hall, Lewis & Clark College - Portland, OR</h1>'+
      '<a href="images/fullsize/zach.jpg"><img src="images/resized/zach_r.jpg"/></a>'+
      '</div>'));
  let nin = locArray.push(
      new Location({lat: 45.448678, lng: -122.669518},
      '<div id="infowindow">'+
      '<h3>Platt Hall, Lewis & Clark College - Portland, OR</h1>'+
      '<a href="images/fullsize/nin.jpg"><img src="images/resized/nin_r.jpg"/></a>'+
      '</div>'));
  let mist = locArray.push(
      new Location({lat: 37.763465, lng: -121.959294},
      '<div id="infowindow">'+
      '<h3>City Center Bishop Ranch - San Ramon, CA</h1>'+
      '<a href="images/fullsize/mist.jpg"><img src="images/resized/mist_r.jpg"/></a>'+
      '</div>'));
  let st = locArray.push(
      new Location({lat: 32.7077161, lng: -117.1604850},
      '<div id="infowindow">'+
      '<h3>The Netflix Experience - San Diego, CA</h1>'+
      '<a href="images/fullsize/st.jpg"><img src="images/resized/st_r.jpg"/></a>'+
      '</div>'));
  let sunset = locArray.push(
      new Location({lat: 37.734854, lng: -121.921460},
      '<div id="infowindow">'+
      '<h3>Old Ranch Park - San Ramon, CA</h1>'+
      '<a href="images/fullsize/sunset.jpg"><img src="images/resized/sunset_r.jpg"/></a>'+
      '</div>'));
  let timeslikethese = locArray.push(
      new Location({lat: 37.747452, lng: -121.437607},
      '<div id="infowindow">'+
      '<h3>Dr. Powers Park - Tracy, CA</h1>'+
      '<a href="images/fullsize/timeslikethese.jpg"><img src="images/resized/timeslikethese_r.jpg"/></a>'+
      '</div>'));
  let snow = locArray.push(
      new Location({lat: 39.339379, lng: -120.247639},
      '<div id="infowindow">'+
      '<h3>Truckee, CA</h1>'+
      '<a href="images/fullsize/snow.jpg"><img src="images/resized/snow_r.jpg"/></a>'+
      '</div>'));
  let sf = locArray.push(
      new Location({lat: 37.803685, lng: -122.430087},
      '<div id="infowindow">'+
      '<h3>Great Meadow Park at Fort Mason - San Francisco, CA</h1>'+
      '<a href="images/fullsize/sf.jpg"><img src="images/resized/sf_r.jpg"/></a>'+
      '</div>'));
  
  // make the map itself, centered so that all the markers are visible
  const map = new google.maps.Map(
      document.getElementById('map'),
      {center: {lat: 40.149494, lng: -120.765521}, zoom: 5});

  // make a marker for each location
  locArray.forEach(location => {
    var marker = new google.maps.Marker({position: location.loc, map: map});

    var infowindow = new google.maps.InfoWindow({
      content: location.content
    });

    marker.addListener('click', function() {
      infowindow.open(map, marker);
    });
  });
}

