// $(function () {
//     // import {loadUserInfo} from './reuse_html.js';
//     // import * as JWT from './jwt-token';
//
//     console.log("怎么会这样呢");
//
//     function doLogin(loginData) {
//         $.ajax({
//             url: "http://localhost:8086/auth",
//             type: "POST",
//             data: JSON.stringify(loginData),
//             contentType: "application/json; charset=utf-8",
//             dataType: "json",
//             success: function (data/*, textStatus, jqXHR*/) {
//                 console.log(data);
//                 localStorage.setItem("jwtToken", data.token);
//
//                 window.location.href = "taskList.html";
//             },
//             error: function (jqXHR, textStatus, errorThrown) {
//                 if (jqXHR.status === 401 || jqXHR.status === 403) {
//                     alert(jqXHR.responseText);
//                 } else {
//                     throw new Error("an unexpected error occurred: " + errorThrown);
//                 }
//             }
//         });
//     }
//
//     $("#login-form").submit(function (event) {
//         event.preventDefault();
//
//         console.log("开始log in");
//
//         const $form = $(this);
//         const formData = {
//             username: $form.find('input[name="username"]').val(),
//             password: $form.find('input[name="password"]').val()
//         };
//
//         doLogin(formData);
//     });
//
//     $("#logout-button").click(function (e) {
//         console.log("点了一下logout");
//     });
// });
