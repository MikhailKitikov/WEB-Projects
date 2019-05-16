function showWindow(wnd_id) {
    document.getElementById(wnd_id).style.display='block';
    document.getElementById(wnd_id + '-mask').style.display='block';
    return false;
}

function hideWindow(wnd_id) {
    document.getElementById(wnd_id).style.display='none'; 
    document.getElementById(wnd_id + '-mask').style.display='none';
}

function changeUploadState(state) {
    if (state === 1) {
        document.getElementById('upload-form:fileUpload').style.display='none';
        document.getElementById('panpan').style.display='block';
    }
    else {
        document.getElementById('upload-form:fileUpload').style.display='block';
        document.getElementById('panpan').style.display='none';
    }
}

function changeView() {
    var state = document.getElementById('photo-gallery').style.display;
    if (state === "block") {
        document.getElementById('photo-gallery').style.display = "none";
        document.getElementById('photo-wall').style.display = "block";
    }
    else {
        document.getElementById('photo-gallery').style.display = "block";
        document.getElementById('photo-wall').style.display = "none";
    }
}

//window.onscroll = function() {
//    if (window.scrollY > 40) {
//        document.getElementById('fmb:mb').style.height = "20px";
//        document.getElementById('fmb').style.height = "20px";
//    }
//    else {
//        document.getElementById('fmb:mb').style.height = "40px";
//        document.getElementById('fmb').style.height = "40px";
//    }
//};
