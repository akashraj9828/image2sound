//functions that are not used anymore

function process() {
    for (i = 0; i < img1.width; i++) {
        // var temp=[]
        for (j = 0; j < img1.height; j++) {
            // temp[j] = map(br[i][j], 0, 255, 0, 1);
            br[i][j] = map(br[i][j], 0, 255, 0, 1);
        }
        // br_pro[i]=temp;
    }
    // console.table(br_pro)
}

//maps values of one domain to another
/*
 @method map
 * @param  {Number} value  the incoming value to be converted
 * @param  {Number} start1 lower bound of the value's current range
 * @param  {Number} stop1  upper bound of the value's current range
 * @param  {Number} start2 lower bound of the value's target range
 * @param  {Number} stop2  upper bound of the value's target range
 * @return {Number}        remapped number
*/
function map(n, start1, stop1, start2, stop2) {
    return ((n - start1) / (stop1 - start1)) * (stop2 - start2) + start2;
};


for (i = 0; i < img1.width; i++) {
    for (j = 0; j < img1.height; j++) {
        play1(500 + j * 100, br[i][j])
    }
}
//    for(i=1;i<50;i++){
//        play1(i*200,0.5)
//    }