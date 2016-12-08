<%@ page import="Sample.Hello" %>
<%--
  Created by IntelliJ IDEA.
  User: Robert
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
  <title>SHS</title>
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">
  <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.12.4/jquery.min.js"></script>
  <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
  <style>
    .bg-1 {
      background-color: #1b1b1b;
      color: #34bc45;
    }
    .bg-2 {
      background-color: #1b1b1b;
      color: #34bc45;
    }
  </style>
  <script>
      function addDevice() {
          if(typeof(Storage) !== "undefined") {
              sessionStorage.dname = document.getElementById("dev_name").value;
              sessionStorage.dkey = document.getElementById("dev_key").value;
              document.getElementById("device").innerHTML = "Device:" + sessionStorage.dname;
              document.getElementById("key").innerHTML = "Key:" + sessionStorage.dkey;
          } else {
              document.getElementById("error").innerHTML = "Browser does not support web storage...";
          }
      }
  </script>
</head>
<body>
<div class="container-fluid bg-1 text-center">
  <h2 class="message"><%=Hello.getMessage()%></h2>
</div>

<div class="container-fluid bg-2">
  <form>
    <h3>Add new Smart Home Device</h3>
    <div class="col-sm-6">
      <div class="form-group">
        <label for="dev_name">Device Name:</label>
        <input type="text" class="form-control" id="dev_name">
      </div>
    </div>

    <div class="col-sm-6">
    <div class="form-group">
      <label for="dev_key">Device Key:</label>
      <input type="text" class="form-control" id="dev_key">
    </div>
    </div>
    <button onclick="addDevice();" type="button" class="btn btn-success btn-lg">Add</button>
  </form>
  <h3 id="device"></h3>
  <h3 id="key"></h3>
  <h3 id="error"></h3>
</div>

</body>
</html>
