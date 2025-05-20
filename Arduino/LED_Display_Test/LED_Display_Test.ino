#include <FastLED.h>

// LED strip configuration
#define NUM_LEDS    300
#define NUM_ROWS    5

#define LED_PIN0 9
#define LED_PIN1 10
#define LED_PIN2 11
#define LED_PIN3 12
#define LED_PIN4 13

CRGB leds0[NUM_LEDS];
CRGB leds1[NUM_LEDS];
CRGB leds2[NUM_LEDS];
CRGB leds3[NUM_LEDS];
CRGB leds4[NUM_LEDS];
CRGB* leds[NUM_ROWS] = {
    leds0,
    leds1,
    leds2,
    leds3,
    leds4
};

bool rainbow_row_en[NUM_ROWS] = {
    true,   // Strip 0
    true,  // Strip 1
    true,  // Strip 2
    true,  // Strip 3
    true   // Strip 4
};

unsigned long last_LEDupdate[NUM_ROWS];

int boot_time_s = 5;

/**
  * This is a command that can be run to clear the Serial Monitor feed
  */
void clearscreen(){
    Serial.println("\033[2J");
};

void printTime(){
    Serial.print("INFO : Time = ");
    Serial.print(millis() / 1000.0);
    Serial.println(" seconds");
}

void setup() {
    Serial.begin(115200);
    while(!Serial);

    clearscreen();

    for (int i = 0; i < NUM_ROWS; i++){
        // FastLED.addLeds<WS2812, led_gpio[i], GRB>(leds[i], NUM_LEDS);
    }
    FastLED.addLeds<WS2812, LED_PIN0, GRB>(leds[0], NUM_LEDS);
    FastLED.addLeds<WS2812, LED_PIN1, GRB>(leds[1], NUM_LEDS);
    FastLED.addLeds<WS2812, LED_PIN2, GRB>(leds[2], NUM_LEDS);
    FastLED.addLeds<WS2812, LED_PIN3, GRB>(leds[3], NUM_LEDS);
    FastLED.addLeds<WS2812, LED_PIN4, GRB>(leds[4], NUM_LEDS);

    FastLED.setBrightness(20);

    unsigned long finish_time = millis() + (1000 * boot_time_s);
    bool boot_done = false;
    while(!boot_done){
        boot_done = boot_animation(finish_time);
    }

    Serial.println("INFO : Entering main loop");
    printTime();
    beam_sync();
}

// Input Handling
String str_in = "default";
String inputString = "";
String str_in_last = "default";

String disp_str = "TEAM 8858 BEAST FROM THE EAST - WYLIE EAST HS - WYLIE, TEXAS";
int disp_speed = 7; // seconds

// Brightness Variables
uint8_t min_brightness = 0x00;
uint8_t max_brightness = 0xFF;
uint8_t bstep = 0x10;
uint8_t brightness = 0x80;
bool stringComplete = false;

void loop() {

    if(stringComplete){

        str_in_last = str_in; // save the last input just in case you need to restore

        // Get the input
        str_in = inputString;
        inputString = "";
        stringComplete = false;
        clearscreen();

        // Randomizes the positions of all beams
        if(str_in == "rand"){
            Serial.println("INFO : Randomizing Beams");
            beam_randomize();
            rainbow_beam_randomize();
            str_in = str_in_last;
        }

        // Synchronizes the positions of all beams
        if(str_in == "sync") {
            Serial.println("INFO : Synchronizing Beams");
            beam_sync();
            rainbow_beam_sync();
            str_in = str_in_last;
        }

        // Raises Brightness
        if(str_in == "bup"){
            str_in = str_in_last;
            if(brightness < max_brightness - bstep){
                brightness += bstep;
                Serial.print("INFO : Raising Brightness to ");
                Serial.print(brightness);
                Serial.print("/");
                Serial.println(max_brightness);
            } else {
                brightness = max_brightness;
                Serial.print("INFO : Already Max Brightness ");
                Serial.print(brightness);
                Serial.print("/");
                Serial.println(max_brightness);
            }
            FastLED.setBrightness(brightness);
        }

        // Lowers Brightness
        if(str_in == "bdn"){
            str_in = str_in_last;
            if(brightness > min_brightness + bstep){
                brightness -= bstep;
                Serial.print("INFO : Lowering Brightness to ");
                Serial.print(brightness);
                Serial.print("/");
                Serial.println(max_brightness);
            } else {
                brightness = min_brightness;
                Serial.print("INFO : Already Min Brightness ");
                Serial.print(brightness);
                Serial.print("/");
                Serial.println(max_brightness);
            }
            FastLED.setBrightness(brightness);
        }
    }

    EVERY_N_MILLIS(20){
        if(str_in == "rbeam"){
            rainbowBeam(2, 40, 0);
            rainbowBeam(2, 40, 1);
            rainbowBeam(2, 40, 2);
            rainbowBeam(2, 40, 3);
            rainbowBeam(2, 40, 4);
        }

        if(str_in == "red"){
            beam(5, 10, CRGB::Red, 0);
            beam(5, 10, CRGB::Red, 1);
            beam(5, 10, CRGB::Red, 2);
            beam(5, 10, CRGB::Red, 3);
            beam(5, 10, CRGB::Red, 4);
        }

        if(str_in == "blue"){
            beam(5, 10, CRGB::Blue, 0);
            beam(5, 10, CRGB::Blue, 1);
            beam(5, 10, CRGB::Blue, 2);
            beam(5, 10, CRGB::Blue, 3);
            beam(5, 10, CRGB::Blue, 4);
        }

        if(str_in == "purple"){
            beam(5, 10, CRGB::Purple, 0);
            beam(5, 10, CRGB::Purple, 1);
            beam(5, 10, CRGB::Purple, 2);
            beam(5, 10, CRGB::Purple, 3);
            beam(5, 10, CRGB::Purple, 4);
        }

        if(str_in == "green"){
            beam(5, 10, CRGB::Green, 0);
            beam(5, 10, CRGB::Green, 1);
            beam(5, 10, CRGB::Green, 2);
            beam(5, 10, CRGB::Green, 3);
            beam(5, 10, CRGB::Green, 4);
        }

        if(str_in == "rwall"){
            rainbow(5000 / NUM_LEDS, NUM_LEDS / 1, rainbow_row_en);
        }

        // Send a Dot across the strip
        if(str_in == "d1"){ // one-hot, switch to shifting in 'off' LEDs after function reports that a dot has been sent
            if(dot(5, true, 0, CRGB::Green)){
                str_in = "d1_done";
                Serial.println("Sending dot...");
            }
        }
        if(str_in == "d1_done"){ // continue to shift in the dot
            dot(5, false, 0, CRGB::Green);
        }

        // Send a Dot across the strip
        if(str_in == "d2"){ // one-hot, switch to shifting in 'off' LEDs after function reports that a dot has been sent
            if(dot(5, true, 1, CRGB::Orange)){
                str_in = "d2_done";
                Serial.println("Sending dot...");
            }
        }
        if(str_in == "d2_done"){ // continue to shift in the dot
            dot(5, false, 1, CRGB::Orange);
        }

        if(str_in == "default"){
            beam(5, 40, CRGB::Purple, 0);
            beam(5, 40, CRGB::Blue, 1);
            beam(5, 40, CRGB::Green, 2);
            beam(5, 40, CRGB::Yellow, 3);
            beam(5, 40, CRGB::Orange, 4);
        }

        if(str_in == "disp"){
            if(string_to_led_map(disp_str, disp_speed, CRGB::Blue, false)){
                str_in = "disp_done";
                Serial.print("INFO : Done printing message \"");
                Serial.print(disp_str);
                Serial.println("\"");
            }
        }
        if(str_in == "disp_done"){ // wait here while
            string_to_led_map(disp_str, disp_speed, CRGB::Blue, true);
        }
        FastLED.show();
    }

    while(Serial.available()){
        char inChar = (char)Serial.read();
        if (inChar == '\n') {
            stringComplete = true;
        } else {
            inputString += inChar;
        }
    }
}
