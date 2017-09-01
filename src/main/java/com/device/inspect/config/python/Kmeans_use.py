# The script MUST contain a function named azureml_main
# which is the entry point for this module.

# imports up here can be used to 
import pandas as pd
from sqlalchemy import create_engine

def get_DBengine(host='42.159.146.213',root='ilabservice',pwd='#vc6Xb$V*a4OXWzQ',database='inspect'):
        str_='mysql+pymysql://'+root+':'+pwd+'@'+host+':3306/'+database+'?'
        return create_engine(str_)
      
def getRules(device_id,inspect_para):
    host='localhost'
    root='ilabservice'
    pwd='#vc6Xb$V*a4OXWzQ'
    database='inspect'
    engine = get_DBengine(host,root,pwd,database)
    try:
            sql="SELECT training_result from "+database+".ml_results where device_id=\'"+device_id+"\' and inspect_para=\'"+inspect_para+"\'"
            print sql
            df = pd.read_sql(sql, engine)
            if len(df)>0: 
                training_ret=df.training_result[0]
                training_ret=[float(i) for i in training_ret.split(',')]
                center_pre=[]
                delta_pre=[]
                for i in range(0,len(training_ret)/2):
                    center_pre.append(training_ret[i*2])
                    delta_pre.append(training_ret[i*2+1])
                return [center_pre,delta_pre]
    except Exception as e:
            print "Error: ",e
    return None

def use(device_id,inspect_para,val):#this is the function that you will use

    rules=getRules(device_id,inspect_para)
    if rules==None or len(rules)<=0:
        return None
    else:
        [center,delta]=rules
        index=-1
        list_=[]
        for i in range(0,len(center)):
            list_.append(abs(val-center[i]))
            if val>center[i]-delta[i] and val<center[i]+delta[i]:
                index=i
                break
        if index==-1:#if not contain, return the closest
            index=list_.index(min(list_))

    return index#this is the type result
