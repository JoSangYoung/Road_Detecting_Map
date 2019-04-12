from django.shortcuts import render
from django.http import HttpResponse

from .models import Memo

# Create your views here.
def memo(request):
	memos = Memo.objects.all()
	context = {'memos': memos}
	return render(request, 'memo/memo.html', context)