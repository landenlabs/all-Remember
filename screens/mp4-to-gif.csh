#!/bin/csh -f

set frames=$1       # 1 lowest, typical 1 or 5 or 10
set inMP4=$2
set outGIF=$3

ffmpeg -i $inMP4 -r $frames -vf scale=540:960 foo.gif -hide_banner
convert -delay 50x100 foo.gif $outGIF
rm foo.gif
    
    