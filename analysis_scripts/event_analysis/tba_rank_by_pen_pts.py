import argparse

from tba_analysis.analysis_func import *

class CustomHelpFormatter(
    argparse.RawDescriptionHelpFormatter,
    argparse.ArgumentDefaultsHelpFormatter,
    argparse.MetavarTypeHelpFormatter):
    pass

help_text = """
Wow, you must be really desperate to come here...
"""

team_pen_diff_list = []
team_pen_list = []

def get_event_teams(event_endpoint):
    """
    fetces a list of teams at an event
    initializes lists used for analysis
    """
    global team_pen_list, team_pen_diff_list
    team_pen_list = []
    team_pen_diff_list = []
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
            team_pen_diff_list.append([f"frc{team['team_number']}", 0, 0])
            team_pen_list.append([f"frc{team['team_number']}", 0, 0, 0])
    else:
        print(f"Failed to fetch data: {response.status_code} - {response.reason}")
        print(f"The event {event_endpoint} could not be found, exiting...")
        exit()

def get_pen_pts_ranks_by_team(event_endpoint, team_num):
    """
    Finds the rankings of teams for a given event by the differential
    of penalty points their alliance gave up minus the penalty points their alliance
    was awarded at this event.
    """
    matches_endpoint = f"{event_endpoint}/matches"
    response = requests.get(matches_endpoint, headers=headers)
    team_key = f"frc{team_num}"
    total_ranked_list = []
    diff_ranked_list = []
    match_breakdown = []
    if response.status_code == 200:
        get_event_teams(event_endpoint)
        matches = response.json()
        if debug_mode:
            print(matches_endpoint)
            print("\nMatches at the Event:")

        print(f"\nINFO : Breakdown of matches for team #{team_num}:")
        for match in matches:
            if match['comp_level'] == "qm":
                match_num = match['match_number']

                # Check if 'score_breakdown' exists and is not None
                if match.get('score_breakdown') is not None:
                    blue_penalty_pts = match['score_breakdown'].get('red', {}).get('foulPoints', 0)
                    red_penalty_pts = match['score_breakdown'].get('blue', {}).get('foulPoints', 0)
                else:
                    blue_penalty_pts = 0
                    red_penalty_pts = 0

                if debug_mode:
                    print(f"\nMATCH NUMBER: {match_num}")
                    print(f"BLUE PEN PTS: {blue_penalty_pts}")
                    print(f"RED PEN PTS:  {red_penalty_pts}")

                for team_id in match['alliances']['blue']['team_keys']:
                    if team_id == team_key:
                        diff = blue_penalty_pts - red_penalty_pts
                        match_breakdown.append({
                            'match_num': match_num,
                            'against_team': blue_penalty_pts,
                            'against_opponent': red_penalty_pts,
                            'difference': diff
                        })
                        print(f"Match #{match_num}:\t{blue_penalty_pts} pts against {team_num}, \t{red_penalty_pts} pts against opponent\t-> {diff} points")
                    for i in range(len(team_pen_diff_list)):
                        if team_pen_diff_list[i][0] == team_id:
                            team_pen_list[i][1] = team_pen_list[i][1] + blue_penalty_pts
                            team_pen_list[i][2] = team_pen_list[i][2] + blue_penalty_pts - red_penalty_pts
                            team_pen_list[i][3] = team_pen_list[i][3] + 1

                for team_id in match['alliances']['red']['team_keys']:
                    if team_id == team_key:
                        diff = red_penalty_pts - blue_penalty_pts
                        match_breakdown.append({
                            'match_num': match_num,
                            'against_team': red_penalty_pts,
                            'against_opponent': blue_penalty_pts,
                            'difference': diff
                        })
                        print(f"Match #{match_num}:\t{red_penalty_pts} pts against {team_num}, \t{blue_penalty_pts} pts against opponent\t-> {diff} points")
                    for i in range(len(team_pen_diff_list)):
                        if team_pen_diff_list[i][0] == team_id:
                            team_pen_list[i][1] = team_pen_list[i][1] + red_penalty_pts
                            team_pen_list[i][2] = team_pen_list[i][2] + red_penalty_pts - blue_penalty_pts
                            team_pen_list[i][3] = team_pen_list[i][3] + 1

        team_pen_list.sort(key=lambda x: x[1])
        i = 1
        print(f"\n\n{color.GREEN}TOTAL PENALTY POINTS RANKING (lower is better){color.RESET}")
        for team, pen_score, pen_diff, num_matches in team_pen_list:
            total_ranked_list.append([team, pen_score])
            if team == team_key:
                print(f"{color.GREEN}{i}.\tTEAM ID: {team}   \tPENALTY SCORE: {pen_score} \t# MATCHES: {num_matches}{color.RESET}")
                total_ranking = i
            else:
                print(f"{i}.\tTEAM ID: {team}   \tPENALTY SCORE: {pen_score} \t# MATCHES: {num_matches}")
            i = i + 1

        team_pen_list.sort(key=lambda x: x[2])
        i = 1
        print(f"\n\n{color.GREEN}PENALTY POINTS DIFFERENTIAL RANKING (lower is better){color.RESET}")
        for team, pen_score, pen_diff, num_matches in team_pen_list:
            diff_ranked_list.append([team, pen_diff])
            if team == team_key:
                print(f"{color.GREEN}{i}.\tTEAM ID: {team}   \tPENALTY DIFFERENTIAL: {pen_diff} \t# MATCHES: {num_matches}{color.RESET}")
                diff_ranking = i
            else:
                print(f"{i}.\tTEAM ID: {team}   \tPENALTY DIFFERENTIAL: {pen_diff} \t# MATCHES: {num_matches}")
            i = i + 1

    else:
        print(f"Failed to fetch matches: {response.status_code} - {response.reason}")
        print(f"The event's matches list {matches_endpoint} could not be found, exiting...")
        exit()

    return total_ranked_list, total_ranking, diff_ranked_list, diff_ranking, match_breakdown

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

        total_ranked_list, total_ranking, diff_ranked_list, diff_ranking, match_breakdown = get_pen_pts_ranks_by_team(event_endpoint=event_endpoint, team_num=team_num)
        print(f"INFO : Team {team_num} ranked {total_ranking}/{len(total_ranked_list)} in overall penalty points given and {diff_ranking}/{len(diff_ranked_list)} in penalty point differential")
        if debug_mode:
            get_event_matches(event_endpoint=event_endpoint, team_num=team_num)
    else:
        print(f"Failed to fetch data: {response.status_code} - {response.reason}")
        print(f"ERROR : Couldn't find the event {event_ids[evnum - 1]}, exiting...")
        exit()

if __name__ == "__main__":
    clear()
    main()