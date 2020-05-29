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