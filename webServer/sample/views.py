from django.shortcuts import render
from django.http import HttpResponse, JsonResponse
from sample.models import SampleModel
from django.views.decorators.csrf import csrf_exempt
import base64
import json
import urllib.parse
import datetime
import sqlite3
import os

MEDIA_ROOT = os.path.normpath(os.path.join(os.path.dirname(os.path.abspath(__file__)),'..' , 'image'))
BASE_ROOT =  os.path.normpath(os.path.join(MEDIA_ROOT, ".."))
conn = sqlite3.connect(os.path.join(BASE_ROOT, "db.sqlite3"), isolation_level=None)

def mapviews(request):
	samplemodels = SampleModel.objects.all()
	context = {'samplemodels': samplemodels}
	return render(request, 'sample/index.html', context)

def deleteMarker(request):
	lat = request.GET['lat']
	lng = request.GET['lng']
	address = str(lat) + ", " + str(lng)
	try:
		samplemodel = SampleModel.objects.get(address = address)
		samplemodel.delete()
	except:
		samplemodels = SampleModel.objects.all()
		context = {'samplemodels': samplemodels}
		return render(request, 'sample/index.html', context)
	
	samplemodels = SampleModel.objects.all()
	context = {'samplemodels': samplemodels}
	return render(request, 'sample/index.html', context)

@csrf_exempt
def post_list3(request):
	datas = json.loads(request.body)
	#print(datas)
	flag = True
	lng = lat = ""
	imgBinary = ""
	try:
		lng = datas['longitude']
		lat = datas['latitude']
		imgBinary = datas['file']
	except:
		return JsonResponse({'result' : 'failed'}, json_dumps_params = {'ensure_ascii': True})
	filename = ""
	if(lng == "0.0" and lat == "0.0"):
		filename = datetime.datetime.now().strftime("%Y%m%d_%H%M%S") + ".jpg"
		flag = False
	else:
		filename = urllib.parse.quote_plus(lng + "_" + lat) + ".jpg"
	
	fp = open(os.path.join(MEDIA_ROOT, filename), "wb")
	fp.write(base64.b64decode(imgBinary))
	if flag:
		cur = conn.cursor()
		sql = """INSERT INTO sample_samplemodel (id, address, geolocation, memo, status) VALUES (0, {0}, {0}, {1}, {2});""".format("{0}, {1}".format(lng, lat), "", filename)
		cur.execute(sql)
	return JsonResponse({'result' : 'success'}, json_dumps_params = {'ensure_ascii': True})