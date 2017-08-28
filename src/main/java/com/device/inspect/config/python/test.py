# -*- coding: utf-8 -*
import web_service_test as ws

def Kmeans_ws(kmeans_url,kmeans_api,paraList):

	#host,root,pwd,database,table,selcol,filter,n_clustering
	#common_input_=[ [host_influx_test ], [ user_influx_test ],[ pwd_influx_test ],[ dbName_influx ],[ table_influx ],[ selcol_influx ],[filter_influx],[n_clustering],]
	common_input_=paraList
	print "common_input ",common_input_
	#testing
	response=ws.req_response(common_input_,kmeans_url,kmeans_api)
	value=response["Results"]["output1"]["value"]
	cd_units=value["Values"]#[[center,delta],...]
	# for i in cd_units:
	#	 print "center: ",i[0],"  delta: ",i[1]#this is the result that you may need
	return cd_units

def TimeSequence_ws():
	#you can modify input
	special_input_=[ [host_influx_test ], [ user_influx_test ],[ pwd_influx_test ],[ dbName_influx ],[ table_influx ],[ selcol_influx ],[filter_influx],]
	print "special_input_ ",special_input_
	#testing
	response=ws.req_response(special_input_,time_sequence_url,time_sequence_api)
	dict_=response["Results"]["output1"]["value"]
	patterns=dict_["Values"]#[centers,patterns]
	print patterns
	center=[float(x) for x in patterns[0]]
	dicts=[eval(x) for x in  patterns[1]]#字符型转字典型

	# for i in range(len(center)):
	# 	print "center: ",center[i]," dict: ",dicts[i]#this is the result that you may need

	#e.g.:[	{0.0: 31.0, 1.0: 8.0, 2.0: 4.0, 3.0: 5.0, 4.0: 12.0, 5.0: 3.0, 6.0: 0.0, -2.0: 2.0, -6.0: 0.0, -5.0: 5.0, -4.0: 14.0, -3.0: 3.0, -1.0: 8.0}, 
	#		{0.0: 51.0, 1.0: 24.0, 2.0: None, 3.0: None, 4.0: None, 5.0: None, 6.0: None, -2.0: None, -6.0: None, -5.0: None, -4.0: None, -3.0: None, -1.0: 24.0}, 
	#		{0.0: 100.0, 1.0: None, 2.0: None, 3.0: None, 4.0: None, 5.0: None, 6.0: None, -2.0: None, -6.0: None, -5.0: None, -4.0: None, -3.0: None, -1.0: None}]
	return center,dicts
