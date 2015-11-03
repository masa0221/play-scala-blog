list = ->
  $.get "/board/posts", (response) ->
    $("#posts").empty()
    $.each response.posts, (index, post) ->
      message = $("<div>").addClass("message").text post.message
      create_at = $("<div>").addClass("create_at").text post.create_at
      $("#posts").append $("<li>").append(message).append(create_at)

$ ->
  list()

  $('#send').click ->
    data =
      "user_id" : 1
      "message" : $("#message").val()
      "create_at" : "2015-11-03 13:55:00"
    $.ajax
      url: "/board/add"
      type: "POST"
      dataType: "json"
      contentType: "application/json"
      data:JSON.stringify(data)
      success: (data, status, response) ->
        $("#message").val("")
        list()
      error: ->
        dataType: "json"
