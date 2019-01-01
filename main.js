    // var context = new AudioContext();
  
//debug
var log_br=false;

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
var br_pro=[];

var audioCtx 

// Create nodes
var osc 
var gain 

// window.onload = function () {

// //audio
//  audioCtx = new AudioContext();

// // Create nodes
//  osc = audioCtx.createOscillator();
//  gain = audioCtx.createGain();

// }
init();


function init() {
    loadImg();
    start_audio();
}

function start_audio() {
    // if(br){
    //     for (i = 0; i < img1.width; i++) {
    //         for (j = 0; j < img1.height; j++) {
    //             play1(500 + j * 100, br[i][j])
    //         }
    //     }

        br.forEach(row => {
            for(i=0; i<row.length;i++)
            play1(500+i*100,row[i])
        });
    // }
    
}


function loadImg() {    //loads image 
    var ctx = canvas;
    if (ctx.getContext) {
        ctx = ctx.getContext('2d');
        //drawing of the test image - img1
        img1.onload = function () {
            //draw background image
            ctx.drawImage(img1, 0, 0);
            calc_bri();
        };
        // console.table(ctx.getImageData().data)
    }
}


//calculates brightness array (br[][])
function calc_bri() {

    //b=brightness array
    // var br = [];
    for (i = 0; i < img1.width; i++) {
        var temp = [];
        temp.splice(0, temp.length)
        for (j = 0; j < img1.height; j++) {
            var data = getPixel(i, j);
            var r = data[0];
            var g = data[1];
            var b = data[2];
            temp[j] = ((r + g + b) / 768).toFixed(2);
            // console.table(temp);
        }
        br[i] = temp;
    }
    if(log_br)
    console.table(br);

}

function play1(freq, amp) {
    let audioCtx = new AudioContext();

    // Create nodes
    let osc = audioCtx.createOscillator();
    let gain = audioCtx.createGain();

    // Set parameters
    osc.frequency.value = freq;
    gain.gain.value = amp;

    // Connect graph
    osc.connect(gain);
    gain.connect(audioCtx.destination);

    // Schedule start and stop
    osc.start();
    osc.stop(audioCtx.currentTime + 0.1);
}

function print() { //prints array of brightness
    var ar = "var ar={";
    for (i = 0; i < img1.width; i++) {
        ar += "{"
        for (j = 0; j < img1.height; j++) {
            ar += br[i][j];
            if (j < img1.height - 1)
                ar += ","
        }
        ar += "}"
        if (i < img1.width - 1)
            ar += ",\n"
    }
    ar += "}"

    $("body").append(ar)

    console.log(ar)
}







//play the sound
function play() {

    var freq, amp;
    for (i = 0; i < img1.width; i++) {
        for (j = 0; j < img1.height; j++) {


            freq = map(i, 0, img1.width, 500, 3000);
            amp = map(br[i][j], 0, 255, 0, 2);
            play1(freq,amp);
        }
    }

}



//returns pixel colour values
function getPixel(x, y) {

    var ctx = canvas.getContext('2d');
    // console.table(ctx.getImageData(x, y, 1, 1).data)
    //data[0]=red data[1]=green data[2]=blue data[3]=alpha
    return ctx.getImageData(x, y, 1, 1).data;
}

