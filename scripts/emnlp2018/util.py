def read_properties(config_file):
    properties = dict()
    with open(config_file, "r") as f:
        for l in f:
            if "=" in l:
                field, val = l.strip().split("=")
                properties[field] = val
    return properties