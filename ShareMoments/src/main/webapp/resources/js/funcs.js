function showWindow() {
    document.getElementById('smallWindow').style.display='block';
    document.getElementById('mask').style.display='block';
    return false;
}

function hideWindow() {
    document.getElementById('smallWindow').style.display='none'; 
    document.getElementById('mask').style.display='none';
    return false;
}
//
//$(".thumbnail").hover(
//  function(){
//  		$(".info", this).css("display", "block");
//	}, function(){
//  		$(".info", this).css("display", "none");
//});