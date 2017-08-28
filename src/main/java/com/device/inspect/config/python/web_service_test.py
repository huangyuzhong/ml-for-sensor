import urllib2
# If you are using Python 3+, import urllib instead of urllib2

import json 

def req_response(input_,url_,api_key_):
    data =  {

            "Inputs": {

                    "input1":
                    {
                        "ColumnNames": ["data"],
                        "Values": [ ]
                    },        },
                "GlobalParameters": {
    }
        }

    data["Inputs"]["input1"]["Values"]=input_#[ ["42.159.115.153" ], [ "root" ],[ "Gea11JxIJQ8$WuR3" ],[ "inspect" ],[ "inspect_data" ],[ "result" ],["where device_inspect_id=303"],]

    body = str.encode(json.dumps(data))
    url=url_
    api_key = api_key_ # Replace this with the API key for the web service
    headers = {'Content-Type':'application/json', 'Authorization':('Bearer '+ api_key)}

    req = urllib2.Request(url, body, headers) 

    try:
        response = urllib2.urlopen(req)

        # If you are using Python 3+, replace urllib2 with urllib.request in the above code:
        # req = urllib.request.Request(url, body, headers) 
        # response = urllib.request.urlopen(req)

        result = response.read()
        
        return json.loads(str.decode(result))#construct data to <type'dict'>

    except urllib2.HTTPError, error:
        print("The request failed with status code: " + str(error.code))

        # Print the headers - they include the requert ID and the timestamp, which are useful for debugging the failure
        print(error.info())

        print(json.loads(error.read()))                 
