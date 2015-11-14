list = ->
  $.get "/board/posts", (response) ->
    $("#posts").empty()
    $.each response.posts, (index, post) ->
      message = $("<div>").
        addClass("message").
        text post.message
      create_at = $("<div>").
        addClass("create_at").
        text post.create_at
      del_link = $("<a>").
        attr("id", "delete_" + post.id).
        attr("href", "javascript:void(0);").
        addClass("delete").
        text "削除"
      $("#posts").append $("<li>").
        attr("id", "post_" + post.id).
        append(message).
        append(create_at).
        append(del_link)

$ ->
  list()

$ ->
  $("#send").click ->
    data =
      "user_id" : 1
      "message" : $("#message").val()
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

$ ->
  $("#posts").on "click",".delete", ->
    post_id = $(this).parents("li").attr("id")
    id = post_id.replace("post_", "")

    $.ajax
      url: "/board/delete/" + id
      type: "POST"
      success: (data, status, response) ->
        $('#' + post_id).remove()
      error: ->
        dataType: "json"
