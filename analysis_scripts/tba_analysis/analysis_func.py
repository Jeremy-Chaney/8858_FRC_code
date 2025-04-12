
"""
# Analysis Functions Library
"""

import requests
import os
import sys
from pathlib import Path

# Handle the API Key for The Blue Alliance
try:
    from tba_analysis.api_key import *

    if API_KEY == "your_api_key_here":
        print(f"ERROR : API_KEY does not look to be updated in {Path(__file__).resolve().parent}/api_key.py")
        print(f"\tGenerate an API Key from https://www.thebluealliance.com/account and paste it into this file")
        exit()

    # Headers for the request
    headers = {
        "X-TBA-Auth-Key": API_KEY
    }
except ImportError:
    print(f"An error occured while importing api_key.py, generating a blank copy to put your TBA API Key in")
    print(f"To generate an API Key, go to https://www.thebluealliance.com/account then add a key under \"Read API Keys\"")
    outfile = open(f"{Path(__file__).resolve().parent}/api_key.py", 'w')
    api_key_in = input("Enter your API Key here (leave blank to manually enter the key later):")
    if api_key_in == "":
        api_key_in = "your_api_key_here"
        print(f"INFO : API Key left blank to manually enter later to {Path(__file__).resolve().parent}/api_key.py")

    outfile.write(
f"""
\"\"\"Your Key for [The Blue Alliance's API](https://www.thebluealliance.com/apidocs)\"\"\"

API_KEY = \"{api_key_in}\"
"""
    )
    outfile.close()
    exit()

# Base URL for TBA API
BASE_URL = "https://www.thebluealliance.com/api/v3"

debug_mode = False

def clear():
    """
    function to clear the screen
    """
    if sys.platform == 'win32':
        _ = os.system('cls')
    else:
        _ = os.system('clear')

class color:
    """
    Class used to make it easier to print colors
    """
    GREY        = "\033[38;5;246m"
    GREEN       = "\033[38;5;10m"
    YELLOW      = "\033[38;5;220m"
    RED         = "\033[38;5;196m"
    WHITE       = "\033[38;5;255m"
    RESET       = "\033[0;0m"
    GREEN_BG    = "\033[48;5;10m\033[38;5;232m"  # bold black text on green background
    YELLOW_BG   = "\033[48;5;220m\033[38;5;232m" # bold black text on yellow background
    GREY_BG     = "\033[48;5;246m\033[38;5;232m" # bold black text on grey background
    RED_BG      = "\033[48;5;196m\033[38;5;232m"   # bold white text on red background

def validate_team(team_endpoint):
    """
    Sanity check to make sure the inputted team exists
    """
    response = requests.get(team_endpoint, headers=headers)

    if response.status_code == 200:
        team_data = response.json()
        print(f"Team Name: {team_data['nickname']}")
        print(f"Location: {team_data['city']}, {team_data['state_prov']}, {team_data['country']}")
    else:
        print(f"Failed to fetch data: {response.status_code} - {response.reason}")
        print("Unable to validate team, exiting...")
        exit()

def get_event_id_list(team_events_endpoint, evnum):
    """
    get list of events a team participated in for a given year
    """
    event_ids = []

    response = requests.get(team_events_endpoint, headers=headers)

    if response.status_code == 200:
        team_event_list = response.json()
        i = 1
        for event in team_event_list:
            event_ids.append(event['key'])
            if evnum == "prompt in script":
                print(f"\nEVENT #{i}:")
                get_event_details(event['key'])
            i = i + 1
    else:
        print(f"Failed to fetch data: {response.status_code} - {response.reason}")
        print("Unable to detect event list for this team/year combination, exiting...")
        exit()

    if evnum == "prompt in script":
        evnum = input(f"\nSELECT WHICH EVENT OF THE ABOVE {i - 1} CHOICES YOU'D LIKE TO ANALYZE: ")

    return event_ids, evnum

def get_event_name(event_key):
    event_endpoint = f"/event/{event_key}"
    response = requests.get(BASE_URL + event_endpoint, headers=headers)
    if response.status_code == 200:
        event_data = response.json()
        return event_data['name']

def get_event_year(event_key):
    event_endpoint = f"/event/{event_key}"
    response = requests.get(BASE_URL + event_endpoint, headers=headers)
    if response.status_code == 200:
        event_data = response.json()
        return event_data['year']

def get_event_details(event_key):
    """
    fetch event details
    """
    event_endpoint = f"/event/{event_key}"
    response = requests.get(BASE_URL + event_endpoint, headers=headers)
    if response.status_code == 200:
        event_data = response.json()
        print(f"Event Name: {get_event_name(event_key)}")
        print(f"Date: {event_data['start_date']} to {event_data['end_date']}")
        print(f"Location: {event_data['city']}, {event_data['state_prov']}, {event_data['country']}\n")
    else:
        print(f"Failed to fetch event details: {response.status_code} - {response.reason}")
        exit()

# Function to fetch matches at the event
def get_event_matches(event_endpoint, team_num):
    """
    print a complete list of matches
    """
    matches_endpoint = f"{event_endpoint}/matches"
    response = requests.get(matches_endpoint, headers=headers)
    team_key = f"frc{team_num}"
    if response.status_code == 200:
        matches = response.json()
        if debug_mode:
            print(matches_endpoint)
            print("\nMatches at the Event:")
        for match in matches:
            if team_key in match['alliances']['blue']['team_keys'] or team_key in match['alliances']['red']['team_keys']:
                print(f"{color.GREEN}\nMatch {match['match_number']}: {match['alliances']}{color.RESET}")
            else:
                print(f"\nMatch {match['match_number']}: {match['alliances']}")
    else:
        print(f"Failed to fetch matches: {response.status_code} - {response.reason}")
        print(f"The event's matches list {matches_endpoint} could not be found, exiting...")
        exit()
