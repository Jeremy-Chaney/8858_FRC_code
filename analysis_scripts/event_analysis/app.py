from flask import Flask, render_template, request, jsonify
import plotly.graph_objects as go
import plotly.utils
import json
import requests
from tba_rank_by_pen_pts import get_pen_pts_ranks_by_team, BASE_URL, headers
from get_rp_per_match import get_rp_per_match, get_event_name, get_event_year

app = Flask(__name__)

@app.route('/')
def index():
    return render_template('index.html')

@app.route('/get_team_events', methods=['POST'])
def get_team_events():
    team_num = request.form.get('team_num')
    year = request.form.get('year')

    # Get team events for the year
    team_events_endpoint = f"{BASE_URL}/team/frc{team_num}/events/{year}"
    response = requests.get(team_events_endpoint, headers=headers)

    if response.status_code == 200:
        events = response.json()
        event_list = []
        for event in events:
            event_list.append({
                'key': event['key'],
                'name': event['name'],
                'start_date': event['start_date']
            })
        # Sort events by start date
        event_list.sort(key=lambda x: x['start_date'])
        return jsonify({'success': True, 'events': event_list})
    else:
        return jsonify({'success': False, 'error': f"Error {response.status_code}: {response.reason}"})

@app.route('/analyze', methods=['POST'])
def analyze():
    event_key = request.form.get('event_key')
    team_num = request.form.get('team_num')
    year = request.form.get('year')

    # Get penalty points data
    total_ranked_list, total_ranking, diff_ranked_list, diff_ranking, match_breakdown = get_pen_pts_ranks_by_team(f"{BASE_URL}/event/{event_key}", team_num)
    team_rp_list = get_rp_per_match(f"{BASE_URL}/event/{event_key}", team_num)

    # Sort match breakdown by match number
    match_breakdown.sort(key=lambda x: x['match_num'])

    # Create bar chart for differentials
    teams = [item[0].replace('frc', '') for item in diff_ranked_list]
    differences = [item[1] for item in diff_ranked_list]

    pen_fig = go.Figure(data=[
        go.Bar(
            x=teams,
            y=differences,
            marker_color=['red' if diff > 0 else 'green' for diff in differences]
        )
    ])

    pen_fig.update_layout(
        title=f"Penalty Points Differential by Team - {get_event_name(BASE_URL, event_key, headers)} ({get_event_year(BASE_URL, event_key, headers)})",
        xaxis_title="Team Number",
        yaxis_title="Penalty Points Differential",
        template="plotly_dark"
    )

    # Ranking Points Graph
    rp_fig = go.Figure()
    for team in team_rp_list:

        # generate the X-axis
        matches = []
        for i in range(len(team[2])):
            matches.append(i + 1)

        if team[0] == f"frc{team_num}": # graph for the team being analyzed
            rp_fig.add_trace(
                go.Scatter(
                    x=matches,
                    y=team[2],
                    mode='lines',
                    name=team[0].replace('frc', ''),
                    line=dict(color='cyan', width=6, dash='solid')
                )
            )
        else: # not the team being analyzed
            rp_fig.add_trace(
                go.Scatter(
                    x=matches,
                    y=team[2],
                    mode='lines',
                    name=team[0].replace('frc', ''),
                    visible='legendonly',
                    line=dict(dash='dash')
                )
            )

    rp_fig.update_layout(
        title=f"Ranking Points by Match - {get_event_name(BASE_URL, event_key, headers)} ({get_event_year(BASE_URL, event_key, headers)})",
        xaxis_title="Match #",
        yaxis_title="Ranking Points",
        template='plotly_dark',
        legend=dict(
            bgcolor='rgba(0,0,0,0.7)',   # semi-transparent dark bg
            font=dict(color='white'),   # white text
            bordercolor='white',
            borderwidth=1
        )
    )

    # Prepare the response data
    response_data = {
        'pen_graph': json.dumps(pen_fig, cls=plotly.utils.PlotlyJSONEncoder),
        'rp_graph': json.dumps(rp_fig, cls=plotly.utils.PlotlyJSONEncoder),
        'total_rankings': [{
            'team': item[0].replace('frc', ''),
            'score': item[1]
        } for item in total_ranked_list],
        'diff_rankings': [{
            'team': item[0].replace('frc', ''),
            'diff': item[1]
        } for item in diff_ranked_list],
        'team_ranking': {
            'total': total_ranking,
            'differential': diff_ranking
        },
        'selected_team': team_num,
        'match_breakdown': match_breakdown
    }

    return jsonify(response_data)

if __name__ == '__main__':
    app.run(debug=True)