# Analysis Scripts

Within this directory are the analysis scripts of [Team 8858 "Beast From the East"](https://www.thebluealliance.com/team/8858).
- Under `tba_analysis/` is a library of functions that can simplify the process of scripting data-collection from [Blue Alliance's API](https://www.thebluealliance.com/apidocs).
- Other folders below this directory are examples of some of the applications we've developed to collect data and analyze performance.

## Prerequisites

- Python 3.x
- Virtual Environment (recommended)
- The Blue Alliance API Key

## Installation

1. Clone the repository:
```bash
git clone <repository-url>
# navigate to this directory under the cloned repository
cd 8858_FRC_code/analysis_scripts
```

2. Set up the `tba_analysis` libraries which contain functions used to parse [The Blue Alliance's API](https://www.thebluealliance.com/apidocs):
```bash
# uses setup.py to set up the libraries described in it
pip install -e .
# remember this^ dot!
```

3. Set up the API Key
    - API Key can be generated from The Blue Alliance's [account page](https://www.thebluealliance.com/account) under "Read API Keys"
    - automated method (recommended)
        - Running any of the apps will prompt you on providing the API Key if it's detected that this key has not been provided previously
        - API Key will be stored inside of `./tba_analysis/api_key.py` (relative to this directory)
    - manual method (not recommended)
        - you can make the `api_key.py` file yourself under the `tba_analysis` folder
        - This file should only contain an assignment of your API Key within quotes to a variable named "`API_KEY`"

After completing these steps, you'll be ready to either run one of the existing applications or start developing your own!

## Usage
Recommended method to import the libraries:
```python
from tba_analysis.analysis_func import *
```
- This import will automatically import the `requests`, `os`, `sys` and `Path` modules.
- This will also handle setup of the API Key (`API_KEY`) and provide a header (`headers`) required to make data requests to the API.
- The variable `BASE_URL` will point to the base address of the API's database.
- `debug_mode` can be set to `True` to increase the verbosity of debug messages within the functions.
