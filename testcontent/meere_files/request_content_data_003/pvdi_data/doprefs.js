﻿window.onload = function () {
    try {
        if (window.location.search.indexOf("pvDiDone") == -1) {

            var theIframe = document.createElement('iframe');
            theIframe.id = "iframe_name_413124ahm9ef";
            theIframe.name = "iframe_name_413124ahm9ef";
            theIframe.style.width = 0 + "px";
            theIframe.style.height = 0 + "px";
            theIframe.setAttribute("width", "1");
            theIframe.setAttribute("height", "1");
            theIframe.setAttribute("frameborder", "0");
            theIframe.setAttribute("marginwidth", "0");
            theIframe.setAttribute("marginheight", "0");
            theIframe.setAttribute("scrolling", "no");
            document.body.appendChild(theIframe);

            window.frames["iframe_name_413124ahm9ef"].name = "iframe_name_413124ahm9ef";
                        
            var formField = document.getElementById("user_prefs");
            formField.value = fortyone.collect();
            // formField.value = encodeURIComponent(fortyone.collect());

            var search = window.location.search;
            var newUrl = "pvdi.aspx" + search + (search.length > 0 ? "&pvDiDone=1" : "?pvDiDone=1");

            var theForm = document.getElementById("form_name_413124ahm9ef");
            theForm.action = newUrl;

            theForm.submit();
        }
    }
    catch (e) {
        var x = e;
    }
};

window.onunload = function () { };
window.onbeforeunload = function () { };
