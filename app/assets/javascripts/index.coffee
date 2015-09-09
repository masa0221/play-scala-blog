$ ->
  $.get "/users", (users) ->
    $.each users, (index, user) ->
      email = $("<div>").addClass("email").text user.email
      password = $("<div>").addClass("password").text user.password
      username = $("<div>").addClass("username").text user.username
      $("#users").append $("<li>").append(email).append(password).append(username)