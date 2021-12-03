import json

import pandas as pd

with open('melvin.json', 'r') as f:
    data = json.loads(f.read())

df_nested_list = pd.json_normalize(data)
print(df_nested_list)

