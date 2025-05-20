
/*
# boot_animation
Runs an animation for a set amount of time

Returns True when time reaches `finish_time`
*/
bool boot_animation(unsigned long finish_time){
    bool done = false;

        if(millis() < finish_time){
            beam(10, 20, CRGB::Purple, 0);
            beam(10, 20, CRGB::Blue, 1);
            beam(10, 20, CRGB::Green, 2);
            beam(10, 20, CRGB::Yellow, 3);
            beam(10, 20, CRGB::Orange, 4);
        } else {
            done = true;
        }

    FastLED.show();
    return done;
}