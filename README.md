# void-Plague
A better disease model

### What is Void-Plague?
I think this program is best described with a story. Once there was a kid in statistics class. He learned that the Hong Kong flu (Please die H3N2, nobody loves you) spreads every other day and takes 3 days to recover from. He learned this not because it was true, but because it makes the numbers work out on the SIR disease model. This model allows someone to look at 3 populations and it shows kids the exciting uses of calculus. (or you can use timesteps). This model sucks in computers if you use timesteps because of the imprecision of Doubles and timesteps. Yes even doubles are not precise enough and you will soon find negative suceptible people with the right numbers.

My program was an attempt to revamp this for a school project. Using APFloat, you can get (theoretical) infinite precision and everything works great. Up until you look at your numbers and they are all weird. So I scrapped that and am now working on a city simulation. The major advantage of a city is that, because I am using actual people, I can use doubles again and they seemingly work properly. It's wonderful.

### Why can't I use the APFloat part of this program

APFloats just aren't that great. I mean they are but they don't seem to work the way my mathmatician brain wants them to. Besides, the city is much more accurate. If you want to use the APFloat part of the program, feel free to recompile this program, or run this program from a text file with the starting number 0 or 1.

### What are your plans for this software

Get the city working better. Sometimes it feels like the distribution is not random enough and I want to fix that. Also I want importable and exportable cities. The latter of these works but the former not so much. Such is life.


### What License is this code under?

My code is under the GPLv3. All of the code from APFloat is under the LGPLv2.1. You can use anything from APFloat in a propritary project but my stuff must be used in free software. Stallman Free

Here's a GNU from cowsay to illistrate

```
 _________________________________
< Make free software with my code >
 ---------------------------------
    \               ,-----._
  .  \         .  ,'        `-.__,------._
 //   \      __\\'                        `-.
((    _____-'___))                           |
 `:='/     (alf_/                            |
 `.=|      |='                               |
    |)   O |                                  \
    |      |                               /\  \
    |     /                          .    /  \  \
    |    .-..__            ___   .--' \  |\   \  |
   |o o  |     ``--.___.  /   `-'      \  \\   \ |
    `--''        '  .' / /             |  | |   | \
                 |  | / /              |  | |   mmm
                 |  ||  |              | /| |
                 ( .' \ \              || | |
                 | |   \ \            // / /
                 | |    \ \          || |_|
                /  |    |_/         /_|
               /__/
```
Cowsay is under the MIT license, bad example
