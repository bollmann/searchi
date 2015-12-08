//config item settings
var frameObj1 = [
	{dir:"left",dist:150,spd:.5,delay:.2,ease:Back.easeOut}/*item_1_1*/,
	{dir:"right",dist:15,spd:.5,delay:.5,ease:Back.easeOut}/*item_1_2*/,
	{dir:"right",dist:15,spd:.5,delay:.6,ease:Back.easeOut}/*item_1_3*/,
];
var frameObj2 = [
	{dir:"none",dist:15,spd:.9,delay:.2,ease:Back.easeOut,scaleX:.7,scaleY:.7}/*item_2_1*/,
	{dir:"right",dist:15,spd:.5,delay:.3,ease:Back.easeOut}/*item_2_2*/
];
var frameObj3 = [
	{dir:"none",dist:15,spd:.9,delay:.2,ease:Back.easeOut,scaleX:.7,scaleY:.7}/*item_3_1*/,
	{dir:"right",dist:15,spd:.5,delay:.3,ease:Back.easeOut}/*item_3_2*/,
	{dir:"right",dist:15,spd:.5,delay:.4,ease:Back.easeOut}/*item_3_3*/
];
var frameObj4 = [
	{dir:"left",dist:150,spd:.5,delay:.2,ease:Back.easeOut}/*item_4_1*/,
	{dir:"right",dist:15,spd:.5,delay:.3,ease:Back.easeOut}/*item_4_2*/
];
var frameObj5 = [
	{dir:"left",dist:15,spd:.5,delay:.2,ease:Back.easeOut}/*item_5_1*/,
	{dir:"none",spd:.5,delay:.3,ease:Back.easeOut}/*item_5_2*/,
	{dir:"none",spd:.8,delay:.3,ease:Back.easeOut,scaleX:0,scaleY:0}/*item_5_3*/,
	{dir:"left",dist:15,spd:.5,delay:.4,ease:Back.easeOut}/*item_5_4*/,
	{dir:"left",dist:15,spd:.5,delay:.5,ease:Back.easeOut}/*item_5_5*/,
	{dir:"bottom",dist:15,spd:.5,delay:.6,ease:Back.easeOut}/*item_5_6*/,
	{dir:"bottom",dist:15,spd:.5,delay:.7,ease:Back.easeOut}/*item_5_7*/,
	{dir:"bottom",dist:15,spd:.5,delay:.8,ease:Back.easeOut}/*item_5_8*/,

	{dir:"left",dist:15,spd:.5,delay:.9,ease:Back.easeOut}/*logoEnd*/,
	{dir:"right",dist:15,spd:.5,delay:.1,ease:Back.easeOut}/*cta*/

];
var lastFrameItems = new Array("cta","logoEnd");
var delayFrames = [0,1,1,1,1,1];
var MAX_FRAMES = 5;
var MAX_ITEMS = 15;
var frameNow = 1;
var bannerWidth=300;
var bannerHeight=250;

var animArray = new Array ();
var tt = TweenLite.to;
var tf = TweenLite.from;
var td = TweenLite.delayedCall;
var ts = TweenLite.set;

var allEase = Back.easeOut;
var posArray = new Array("blank_space");


function init(){
	
	for (var a = 1; a < MAX_FRAMES+1; a++) {
		var frameArray = new Array();
		var curFrameObj = this["frameObj" + a];
		for (var b = 1; b < MAX_ITEMS; b++) {			
			if (document.getElementById("item_" + a + "_" + b)) {
				var curObj = new Object ();
				curObj.obj =  document.getElementById("item_" + a + "_" + b);				
				curObj.xPos = getComputedStyle(curObj.obj).getPropertyValue('left');
				curObj.yPos = getComputedStyle(curObj.obj).getPropertyValue('top');
				curObj.scale = 1;

				curObj.obj.style.opacity = 0;				

				var posX = curObj.xPos;
				var posY = curObj.yPos;
				
				if (curFrameObj) {
					var dist = curFrameObj[b-1].dist
					switch (curFrameObj[b-1].dir) {
						case "left" :
							posX = parseInt(posX,10) - dist;
							break;
						case "right" :
							posX = parseInt(posX,10) + dist;
							break; 
						case "top" :
							posY = parseInt(posY,10) - dist;
							break;
						case "bottom" :
							posY = parseInt(posY,10) + dist;
							break;
						default:
							break;
					}
					var scaleX =  (curFrameObj[b-1].scaleX!=undefined) ? (curFrameObj[b-1].scaleX) : 1;	
					var scaleY =  (curFrameObj[b-1].scaleY!=undefined) ? (curFrameObj[b-1].scaleY) : 1;					
				}
				ts(curObj.obj, {left:posX,top:posY,scaleX:scaleX,scaleY:scaleY});
				frameArray.push(curObj);
			}
		}
		if(a==MAX_FRAMES){
			for (var c=0; c<lastFrameItems.length; c++) {
				curObj = new Object ();
				curObj.obj =  document.getElementById(lastFrameItems[c]);			
				curObj.xPos = getComputedStyle(curObj.obj).getPropertyValue('left');
				curObj.yPos = getComputedStyle(curObj.obj).getPropertyValue('top');
				curObj.scale = 1;
				curObj.obj.style.opacity = 0;	
				var arrPos = curFrameObj.length - (lastFrameItems.length-c);
				
				var posX = curObj.xPos;
				var posY = curObj.yPos;
				if (curFrameObj) {
					var dist = curFrameObj[arrPos].dist;
					switch (curFrameObj[arrPos].dir) {
						case "left" :
							posX = parseInt(posX,10) - dist;
							break;
						case "right" :
							posX = parseInt(posX,10) + dist;
							break; 
						case "top" :
							posY = parseInt(posY,10) - dist;
							break;
						case "bottom" :
							posY = parseInt(posY,10) + dist;
							break;
						default:
							break;
					}
					var scaleX =  (curFrameObj[arrPos].scaleX!=undefined) ? (curFrameObj[arrPos].scaleX) : 1;	
					var scaleY =  (curFrameObj[arrPos].scaleY!=undefined) ? (curFrameObj[arrPos].scaleY) : 1;					
				}
				ts(curObj.obj, {left:posX,top:posY,scaleX:scaleX,scaleY:scaleY});
				frameArray.push(curObj);
			}
		}
		posArray.push(frameArray);
	}

	hotSpot.addEventListener('mouseover',hotSpotRollOver);
	hotSpot.addEventListener('mouseout',hotSpotRollOut);

	playFrameAnimation()
}

function playFrameAnimation(){
	animArray.length=0;
	var frame = document.getElementById("frame" + frameNow);
	frame.style.display="block";
	var frameItemMax = posArray[frameNow].length;
	for (var t = 0; t < frameItemMax; t++) {
		var tObj = posArray[frameNow][t].obj;
		var xPos = posArray[frameNow][t].xPos;
		var yPos = posArray[frameNow][t].yPos;
		var scale = posArray[frameNow][t].scale;
		var curFrameObj = this["frameObj" + frameNow];

		if (curFrameObj) {
			var dly = curFrameObj[t].delay;
			var eases = curFrameObj[t].ease;
			var spd = curFrameObj[t].spd;
		}

		if (frameNow == MAX_FRAMES) {
			//last frame
			
			theAnim = tt(tObj,.5,{alpha:1,left:xPos,top:yPos,scaleX:scale,scaleY:scale,delay:dly,ease:eases});
		} else {			
			if(t==frameItemMax-1){
				//last item will hold the onComplete
				theAnim = tt(tObj,.5,{alpha:1,left:xPos,top:yPos,scaleX:scale,scaleY:scale,delay:dly,ease:eases,onComplete:animHoldTimer,onReverseComplete:reverseComplete});
			}else{
				theAnim = tt(tObj,.5,{alpha:1,left:xPos,top:yPos,scaleX:scale,scaleY:scale,delay:dly,ease:eases});
			}			
		}
		animArray.push(theAnim);
	}
}
function animHoldTimer(){
	td(delayFrames[frameNow],releaseHold);
}
function reverseComplete(){
	frameNow+=1;
	playFrameAnimation();
}
function releaseHold(){
	var count = 0;
	var animTotal = animArray.length;
	setInterval(holdDone,100);
	function holdDone(){
		if(count<animTotal){
			animArray[count].reverse();
			count++;
		}
	}
}

function activateRollOver(){
	ctaRollOverAnim();
	//legal_btn.addEventListener('mouseover',legalRollOver);
}
function hotSpotRollOver(){
	var ctaRollOver = document.getElementById("ctaRollOver");
	ctaRollOver.style.display="block";
	tt(ctaRollOver,.5,{opacity:1});
}
function hotSpotRollOut(){
	tt(ctaRollOver,.5,{opacity:0});
}
function ctaRollOverAnim(){
	tt(ctaRollOver,.5,{left:ctaW, ease:allEase, overwrite:false});
}
function legalRollOver(){
	//legal_btn.removeEventListener('mouseover',legalRollOver);
	var leg = document.getElementById("legal");
	leg.style.display="block";
	//tt(legal,.5,{top:121, ease:allEase, onComplete:overDone, overwrite:false});
}
function overDone(){
	//legal.addEventListener('mouseout',legalRollOut);
}
function legalRollOut(){
	//tt(legal,.5,{top:250, ease:allEase, onComplete:outDone, overwrite:false});
}
function outDone(){
	//legal.removeEventListener('mouseout',legalRollOut);
	//legal_btn.addEventListener('mouseover',legalRollOver);
}

function clickThru(){
	Enabler.exit('clickTag');
	window.open(window.clickTag);
}
init();