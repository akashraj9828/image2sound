var canvas = document.createElement('canvas');
canvas.setAttribute("id", "can");
canvas.height = 105;
canvas.width = 105;
$("#container").append(canvas);


//Loading of the home test image - img1
var img1 = new Image();
img1.src = 'test3.png';

//brighness array
var br = [];

init();

function init() {
    loadImg();
}



function loadImg() {
    var ctx = canvas;
    if (ctx.getContext) {
        ctx = ctx.getContext('2d');
        //drawing of the test image - img1
        img1.onload = function () {
            //draw background image
            ctx.drawImage(img1, 0, 0);
            calc_bri()
        };
        // console.table(ctx.getImageData().data)
    }
}

//returns pixel colour values
function getPixel(x, y) {

    var ctx = canvas.getContext('2d');

    // console.table(ctx.getImageData(x, y, 1, 1).data)

    //data[0]=red data[1]=green data[2]=blue data[3]=alpha
    return ctx.getImageData(x, y, 1, 1).data;
}


//calculates brightness array (br[][])
function calc_bri() {

    //b=brightness array
    // var br = [];
    for (i = 0; i < img1.width; i++) {
        var temp = [];
        temp.splice(0, temp.length)
        for (j = 0; j < img1.height; j++) {
            var data =getPixel(i,j);
            var r = data[0];
            var g = data[1];
            var b = data[2];
            temp[j] = Math.floor((r + g + b) / 3);
            // console.table(temp);
        }
        br[i] = temp;
    }
    console.table(br);

}

//play the sound
function play(){
    
}