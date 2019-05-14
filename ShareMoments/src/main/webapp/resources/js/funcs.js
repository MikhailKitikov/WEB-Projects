function showWindow(wnd_id) {
    document.getElementById(wnd_id).style.display='block';
    document.getElementById(wnd_id + '-mask').style.display='block';
    return false;
}

function hideWindow(wnd_id) {
    document.getElementById(wnd_id).style.display='none'; 
    document.getElementById(wnd_id + '-mask').style.display='none';
    location.reload();
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
