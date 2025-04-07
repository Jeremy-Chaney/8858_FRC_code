import argparse
import requests
import sys
import os

try:
    from api_key import *
except ImportError:
    print(f"An error occured while importing api_key.py, generating a blank copy to put your TBA API Key in")
    print(f"To generate an API Key, go to https://www.thebluealliance.com/account then add a key under \"Read API Keys\"")
    outfile = open("api_key.py", 'w')
    outfile.write("# Your API key\nAPI_KEY = \"your_api_key_here\"\n")
    outfile.close()
    exit()

class CustomHelpFormatter(
    argparse.RawDescriptionHelpFormatter,
    argparse.ArgumentDefaultsHelpFormatter,
    argparse.MetavarTypeHelpFormatter):
    pass

help_text = """
Wow, you must be really desperate to come here...
"""

# Base URL for TBA API
BASE_URL = "https://www.thebluealliance.com/api/v3"

# Headers for the request
headers = {
    "X-TBA-Auth-Key": API_KEY
}

debug_mode = False

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

def clear():
    """
    function to clear the screen
    """
    if sys.platform == 'win32':
        _ = os.system('cls')
    else:
        _ = os.system('clear')

team_pen_diff_list = []
team_pen_list = []
team_rp_list = []

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

def get_event_name(BASE_URL, event_key, headers):
    event_endpoint = f"/event/{event_key}"
    response = requests.get(BASE_URL + event_endpoint, headers=headers)
    if response.status_code == 200:
        event_data = response.json()
        return event_data['name']

def get_event_year(BASE_URL, event_key, headers):
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
        print(f"Event Name: {get_event_name(BASE_URL, event_key, headers)}")
        print(f"Date: {event_data['start_date']} to {event_data['end_date']}")
        print(f"Location: {event_data['city']}, {event_data['state_prov']}, {event_data['country']}\n")
    else:
        print(f"Failed to fetch event details: {response.status_code} - {response.reason}")
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

def get_event_teams(event_endpoint):
    """
    fetces a list of teams at an event
    initializes lists used for analysis
    """
    global team_rp_list
    team_rp_list = []
    teams_endpoint = f"{event_endpoint}/teams"
    response = requests.get(teams_endpoint, headers=headers)
    if response.status_code == 200:
        team_list = response.json()
        if debug_mode:
            print(teams_endpoint)
            print("\nTeams at the event:")
        for team in team_list:
            if debug_mode:
                print(f"TEAM {team['team_number']}: {team['nickname']}")
            team_rp_list.append([f"frc{team['team_number']}", [], [], 0])
    else:
        print(f"Failed to fetch data: {response.status_code} - {response.reason}")
        print(f"The event {event_endpoint} could not be found, exiting...")
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

def get_rp_per_match(event_endpoint, team_num):
    matches_endpoint = f"{event_endpoint}/matches"
    response = requests.get(matches_endpoint, headers=headers)
    team_key = f"frc{team_num}"
    total_ranked_list = []
    if response.status_code == 200:
        get_event_teams(event_endpoint)
        matches = response.json()
        for match in matches:
            if match['comp_level'] == "qm":
                match_num = match['match_number']

                # Check if 'score_breakdown' exists and is not None
                if match.get('score_breakdown') is not None:
                    red_rp = match['score_breakdown'].get('red', {}).get('rp', 0)
                    blue_rp = match['score_breakdown'].get('blue', {}).get('rp', 0)
                else:
                    red_rp = 0
                    blue_rp = 0

                for team_id in match['alliances']['blue']['team_keys']:
                    for i in range(len(team_rp_list)):
                        if team_rp_list[i][0] == team_id:
                            team_rp_list[i][1].append(blue_rp) # append this match's ranking point
                            team_rp_list[i][2].append(team_rp_list[i][3] + blue_rp) # add the raking points to the running total
                            team_rp_list[i][3] = team_rp_list[i][3] + blue_rp

                for team_id in match['alliances']['red']['team_keys']:
                    for i in range(len(team_rp_list)):
                        if team_rp_list[i][0] == team_id:
                            team_rp_list[i][1].append(red_rp) # append this match's ranking point
                            team_rp_list[i][2].append(team_rp_list[i][3] + red_rp) # add the raking points to the running total
                            team_rp_list[i][3] = team_rp_list[i][3] + red_rp

    team_rp_list.sort(key=lambda x: x[3], reverse=True)
    for team in team_rp_list:
        print(f"\nTEAM: {team[0]}")
        print(f"\tRP by match: {team[1]}")
        print(f"\tRP total by match: {team[2]}")
        print(f"\tRP total: {team[3]}")
    return team_rp_list

def main():

    global debug_mode

    parser = argparse.ArgumentParser(
        formatter_class=CustomHelpFormatter,
        description=help_text
    )

    parser.add_argument("-t", "-team_num", dest = "team_num", type = str, default=["prompt in script"], nargs = 1, help="Team Number to analyze")
    parser.add_argument("-y", "-year", dest = "year", type = str, default=["prompt in script"], nargs = 1, help="Year to fetch events from")
    parser.add_argument("-e", "-evnum", dest = "evnum", type = str, default=["prompt in script"], nargs = 1, help="Event # to fetch for")
    parser.add_argument("-d", "-debug_mode", dest = "debug_mode", action = 'store_true', help="enable printing of non-essential debug messages")

    # assign command line argument variables to their respective variables in the script
    args = parser.parse_args()
    team_num    = args.team_num[0]
    year        = args.year[0]
    evnum       = args.evnum[0]
    debug_mode  = args.debug_mode

    if team_num == "prompt in script":
        team_num = input(f"Please Enter a Team Number: ")
    team_num = int(team_num)

    if year == "prompt in script":
        year = input(f"Please Enter a Year: ")
    year = int(year)

    # Endpoint for team information
    team_endpoint = f"{BASE_URL}/team/frc{team_num}"
    team_events = f"{team_endpoint}/events/{year}"

    if debug_mode:
        print(team_endpoint)
        print(team_events)

    # Check that a team exists
    validate_team(team_endpoint)

    # get ID codes for each event a team participated this year
    event_ids, evnum = get_event_id_list(team_events, evnum)

    evnum = int(evnum)

    # sanity check that the event number is in the index of possible events
    if evnum > (len(event_ids)):
        print(f"ERROR : Event number {evnum} selected but only {len(event_ids)} were detected at {team_events}")
        exit()

    event_endpoint = f"{BASE_URL}/event/{event_ids[evnum - 1]}"
    if debug_mode:
        print(event_endpoint)
    response = requests.get(event_endpoint, headers=headers)

    if response.status_code == 200:
        event_data = response.json()
        print(f"\nEvent Name: {event_data['name']}")
        print(f"Location: {event_data['city']}, {event_data['state_prov']}, {event_data['country']}")

        # total_ranked_list, total_ranking, diff_ranked_list, diff_ranking, match_breakdown = get_pen_pts_ranks_by_team(event_endpoint=event_endpoint, team_num=team_num)
        get_rp_per_match(event_endpoint=event_endpoint, team_num=team_endpoint)
        # print(f"INFO : Team {team_num} ranked {total_ranking}/{len(total_ranked_list)} in overall penalty points given and {diff_ranking}/{len(diff_ranked_list)} in penalty point differential")
        if debug_mode:
            get_event_matches(event_endpoint=event_endpoint, team_num=team_num)
    else:
        print(f"Failed to fetch data: {response.status_code} - {response.reason}")
        print(f"ERROR : Couldn't find the event {event_ids[evnum - 1]}, exiting...")
        exit()

if __name__ == "__main__":
    clear()
    main()