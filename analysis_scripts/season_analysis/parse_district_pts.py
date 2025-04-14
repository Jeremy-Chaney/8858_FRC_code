
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

def main():

    global debug_mode

    parser = argparse.ArgumentParser(
        formatter_class=CustomHelpFormatter,
        description=help_text
    )

    parser.add_argument("-t", "-team_num", dest = "team_num", type = str, default=["prompt in script"], nargs = 1, help="Team Number to analyze")
    parser.add_argument("-y", "-year", dest = "year", type = str, default=["prompt in script"], nargs = 1, help="Year to fetch events from")
    parser.add_argument("-d", "-debug_mode", dest = "debug_mode", action = 'store_true', help="enable printing of non-essential debug messages")

    # assign command line argument variables to their respective variables in the script
    args = parser.parse_args()
    team_num    = args.team_num[0]
    year        = args.year[0]
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

    # Check that a team exists
    validate_team(team_endpoint)

    # get district points
    # district_pts, district_pts_by_week = count_team_dp(team_num, team_events) # analyze one team
    full_dist_pts_by_week = get_district_pts_by_week(get_team_district(team_num, year), year) # analyze the full district
    # print(f"Total District Points : {district_pts}")

    if debug_mode:
        print(team_endpoint)
        print(team_events)

if __name__ == "__main__":
    clear()
    main()