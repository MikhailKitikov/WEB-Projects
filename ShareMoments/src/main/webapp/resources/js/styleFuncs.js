function showImage(ID) {
    document.getElementById(ID).style.transform='scale(3)';
    return false;
}

function hideImage(ID) {
    document.getElementById(ID).style.transform='';
    return false;
}