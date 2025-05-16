
/*
# boot_animation
Runs an animation for a set amount of time

Returns True when time reaches `finish_time`
*/
bool boot_animation(unsigned long finish_time){
    bool done = false;

        if(millis() < finish_time){
            beam(1, 20, CRGB::Cyan, 0);
            beam(1, 20, CRGB::Red, 1);
        } else {
            done = true;
        }

    FastLED.show();
    return done;
}