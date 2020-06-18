// Your web app's Firebase configuration
var firebaseConfig = {
  apiKey: "",
  authDomain: "williamlew-step.firebaseapp.com",
  databaseURL: "https://williamlew-step.firebaseio.com",
  projectId: "williamlew-step",
  storageBucket: "williamlew-step.appspot.com",
  messagingSenderId: "374003997173",
  appId: "1:374003997173:web:00c2f90284ce7a6cc0e8bc",
  measurementId: "G-2Z66BLB7YE"
};

// Initialize Firebase
firebase.initializeApp(firebaseConfig);
firebase.analytics();

initLogin = function() {
  firebase.auth().onAuthStateChanged(function(user) {
    if (user) {
      // User is signed in.
      var displayName = user.displayName;
      user.getIdToken().then(function(accessToken) {
        document.getElementById('login').innerHTML = '<button class=\"dropbtn\">' 
            + displayName + '</button><div class=\"dropdown-content\"><a ' 
            + 'href=\"javascript:fireLogout()\">Logout</a></div>';
      });
    } else {
      // User is not signed in.
      document.getElementById('login').innerHTML = "<li><a href=\"/login.html\">Login</li>";         
    }
  }, function(error) {
    console.log(error);
  });
};

window.addEventListener('load', function() {
  initLogin();
});

function fireLogout() {
  firebase.auth().signOut().then(function() {
    // Sign-out successful.
    window.location.href = "/index_copy.html";
  }).catch(function(error) {
    // An error happened.
  });
}
      
